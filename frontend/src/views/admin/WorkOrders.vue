<template>
  <div class="workorders-page">
    <div class="page-header animate-in">
      <div class="header-left">
        <h2 class="page-title">
          <span class="title-accent">&gt;_</span> 工单管理
        </h2>
        <span class="title-meta">共 {{ total }} 条工单</span>
      </div>
      <div class="header-actions">
        <select v-model="statusFilter" class="filter-select" @change="loadData">
          <option value="">全部状态</option>
          <option :value="0">待处理</option>
          <option :value="1">已处理</option>
          <option :value="2">已关闭</option>
        </select>
      </div>
    </div>

    <div class="table-wrapper animate-in" style="animation-delay: 0.15s">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="uid" label="用户ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="reply" label="回复" min-width="160" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'warning' : row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 0 ? '待处理' : row.status === 1 ? '已处理' : '已关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">
            <span class="mono-text" style="font-size: 11px">{{ row.addtime || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-btns">
              <button class="tbl-btn tbl-btn-ghost" @click="showReplyDialog(row)"
                :disabled="row.status === 2">回复</button>
              <button class="tbl-btn tbl-btn-warn" @click="handleClose(row)"
                :disabled="row.status === 2">关闭</button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-bar animate-in" style="animation-delay: 0.3s" v-if="total > 0">
      <el-pagination layout="total, prev, pager, next" :total="total" :page-size="pageSize"
        v-model:current-page="currentPage" @current-change="loadData" />
    </div>

    <!-- Reply dialog -->
    <el-dialog v-model="replyDialogVisible" title="回复工单" width="500px">
      <p style="margin-bottom: 12px; color: var(--text-secondary); font-size: 13px">
        工单标题: <strong style="color: var(--neon-cyan)">{{ replyTarget?.title }}</strong>
      </p>
      <p style="margin-bottom: 16px; color: var(--text-dim); font-size: 12px">
        {{ replyTarget?.content }}
      </p>
      <el-input v-model="replyContent" type="textarea" :rows="4" placeholder="输入回复内容..." />
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="replying" @click="handleReply">回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminWorkOrderList, replyWorkOrder, closeWorkOrder } from '@/api/admin'

const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const loading = ref(false)
const statusFilter = ref('')

const replyDialogVisible = ref(false)
const replyTarget = ref(null)
const replyContent = ref('')
const replying = ref(false)

onMounted(() => loadData())

async function loadData() {
  if (loading.value) return
  loading.value = true
  try {
    const params = { page: currentPage.value, size: pageSize.value }
    if (statusFilter.value !== '') params.status = statusFilter.value
    const res = await getAdminWorkOrderList(params)
    if (res.code === 200) {
      list.value = res.data?.records || []
      total.value = res.data?.total || 0
    }
  } finally {
    loading.value = false
  }
}

function showReplyDialog(row) {
  replyTarget.value = row
  replyContent.value = row.reply || ''
  replyDialogVisible.value = true
}

async function handleReply() {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  if (!replyTarget.value?.id) {
    ElMessage.warning('工单信息异常')
    return
  }
  replying.value = true
  try {
    const res = await replyWorkOrder(replyTarget.value.id, { reply: replyContent.value })
    if (res.code === 200) {
      ElMessage.success('回复成功')
      replyDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '回复失败')
    }
  } catch (e) {
    ElMessage.error('回复失败')
  } finally {
    replying.value = false
  }
}

async function handleClose(row) {
  await ElMessageBox.confirm('确定关闭该工单？', '确认', { type: 'warning' })
  try {
    const res = await closeWorkOrder(row.id)
    if (res.code === 200) {
      ElMessage.success('工单已关闭')
      loadData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) { ElMessage.error('操作失败') }
}
</script>

<style scoped lang="scss">
.workorders-page { max-width: 1400px; }

.page-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;
  .header-left { display: flex; align-items: baseline; gap: 12px; }
  .header-actions { display: flex; gap: 10px; align-items: center; }
  .page-title { font-family: var(--font-display); font-size: 20px; font-weight: 700; color: var(--text-primary);
    .title-accent { font-family: var(--font-mono); color: var(--neon-cyan); font-size: 16px; margin-right: 4px; }
  }
  .title-meta { font-family: var(--font-mono); font-size: 12px; color: var(--text-dim); }
}

.filter-select {
  font-family: var(--font-mono); font-size: 13px; padding: 8px 12px;
  background: var(--bg-surface); border: 1px solid var(--border-subtle);
  border-radius: var(--radius-sm); color: var(--text-primary); outline: none;
  cursor: pointer; appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%23666' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E");
  background-repeat: no-repeat; background-position: right 10px center; padding-right: 28px;
  option { background: var(--bg-elevated); color: var(--text-primary); }
}

.table-wrapper { background: var(--bg-card); border: 1px solid var(--border-subtle); border-radius: var(--radius-lg); overflow: hidden; }
.mono-text { font-family: var(--font-mono); font-size: 13px; }

.action-btns { display: flex; gap: 4px; }

.tbl-btn {
  font-family: var(--font-mono); font-size: 11px; font-weight: 500; padding: 4px 10px;
  border-radius: var(--radius-sm); border: 1px solid var(--border-subtle);
  background: transparent; cursor: pointer; transition: all 0.2s ease; color: var(--text-secondary);
  &:hover { border-color: var(--border-visible); background: rgba(255, 255, 255, 0.04); }
  &:disabled { opacity: 0.4; cursor: not-allowed; }
  &.tbl-btn-ghost { color: var(--neon-cyan); border-color: rgba(0, 240, 255, 0.2);
    &:hover { background: rgba(0, 240, 255, 0.08); }
  }
  &.tbl-btn-warn { color: var(--neon-amber); border-color: rgba(255, 170, 0, 0.2);
    &:hover { background: rgba(255, 170, 0, 0.08); }
  }
}

.pagination-bar { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
