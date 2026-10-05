<template>
  <div class="page-workorders">
    <el-card shadow="never" class="panel-card">
      <div class="toolbar">
        <el-radio-group v-model="statusFilter" @change="handleFilter">
          <el-radio-button :value="-1">全部</el-radio-button>
          <el-radio-button :value="0">待处理</el-radio-button>
          <el-radio-button :value="1">已回复</el-radio-button>
          <el-radio-button :value="2">已关闭</el-radio-button>
        </el-radio-group>
      </div>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="uid" label="用户UID" width="90" />
        <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="220" show-overflow-tooltip />
        <el-table-column prop="reply" label="回复" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="statusTag(scope.row.status)" size="small">{{ statusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="addtime" label="提交时间" width="170" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="openReply(scope.row)">回复</el-button>
            <el-button
              v-if="scope.row.status !== 2"
              size="small"
              type="danger"
              link
              @click="handleClose(scope.row)"
            >关闭</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog v-model="replyVisible" title="回复工单" width="520px">
      <div class="wo-content" v-if="replyRow">
        <div class="wo-title">{{ replyRow.title }}</div>
        <div class="wo-body">{{ replyRow.content }}</div>
      </div>
      <el-input
        v-model="replyText"
        type="textarea"
        :rows="5"
        placeholder="请输入回复内容"
        maxlength="500"
        show-word-limit
      />
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" :loading="replyLoading" @click="submitReply">提交回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getWorkOrderList, replyWorkOrder, closeWorkOrder } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const statusFilter = ref(-1)
const query = reactive({ page: 1, size: 20 })

const replyVisible = ref(false)
const replyRow = ref(null)
const replyText = ref('')
const replyLoading = ref(false)

function statusText(s) {
  if (s === 1) return '已回复'
  if (s === 2) return '已关闭'
  return '待处理'
}

function statusTag(s) {
  if (s === 1) return 'success'
  if (s === 2) return 'info'
  return 'warning'
}

async function loadData() {
  loading.value = true
  try {
    const params = { page: query.page, size: query.size }
    if (statusFilter.value >= 0) params.status = statusFilter.value
    const res = await getWorkOrderList(params)
    if (res.code === 200) {
      list.value = res.data.records || []
      total.value = Number(res.data.total || 0)
    }
  } finally {
    loading.value = false
  }
}

function handleFilter() {
  query.page = 1
  loadData()
}

function openReply(row) {
  replyRow.value = row
  replyText.value = row.reply || ''
  replyVisible.value = true
}

async function submitReply() {
  if (!replyText.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  replyLoading.value = true
  try {
    const res = await replyWorkOrder(replyRow.value.id, replyText.value.trim())
    if (res.code === 200) {
      ElMessage.success('回复成功')
      replyVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '回复失败')
    }
  } finally {
    replyLoading.value = false
  }
}

async function handleClose(row) {
  const confirmed = await ElMessageBox.confirm('确定关闭该工单？', '提示', { type: 'warning' }).catch(() => false)
  if (!confirmed) return
  const res = await closeWorkOrder(row.id)
  if (res.code === 200) {
    ElMessage.success('工单已关闭')
    loadData()
  } else {
    ElMessage.error(res.msg || '操作失败')
  }
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.panel-card {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
}

.toolbar {
  margin-bottom: 16px;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.wo-content {
  margin-bottom: 12px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: var(--radius-md);

  .wo-title {
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 6px;
  }

  .wo-body {
    font-size: 13px;
    color: var(--text-secondary);
    white-space: pre-wrap;
  }
}
</style>
