<template>
  <div class="page-tixian">
    <el-card shadow="never" class="panel-card">
      <div class="toolbar">
        <el-radio-group v-model="statusFilter" @change="handleFilter">
          <el-radio-button :value="-1">全部</el-radio-button>
          <el-radio-button :value="0">待审核</el-radio-button>
          <el-radio-button :value="1">已通过</el-radio-button>
          <el-radio-button :value="2">已拒绝</el-radio-button>
        </el-radio-group>
      </div>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="uid" label="用户UID" width="90" />
        <el-table-column prop="account" label="收款账号" min-width="160" show-overflow-tooltip />
        <el-table-column prop="name" label="收款人" width="120" show-overflow-tooltip />
        <el-table-column prop="money" label="金额(元)" width="100" align="right">
          <template #default="scope">{{ formatMoney(scope.row.money) }}</template>
        </el-table-column>
        <el-table-column prop="realmoney" label="实际到账" width="100" align="right">
          <template #default="scope">{{ scope.row.realmoney == null ? '-' : formatMoney(scope.row.realmoney) }}</template>
        </el-table-column>
        <el-table-column label="方式" width="90">
          <template #default="scope">
            <el-tag size="small" effect="plain">{{ typeText(scope.row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="statusTag(scope.row.status)" size="small">{{ statusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="addtime" label="申请时间" width="170" show-overflow-tooltip />
        <el-table-column prop="endtime" label="处理时间" width="170" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <template v-if="scope.row.status === 0">
              <el-button size="small" type="success" link @click="openApprove(scope.row)">通过</el-button>
              <el-button size="small" type="danger" link @click="handleReject(scope.row)">拒绝</el-button>
            </template>
            <span v-else class="done-tip">已处理</span>
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

    <el-dialog v-model="approveVisible" title="通过提现申请" width="440px">
      <el-form label-width="100px" v-if="approveRow">
        <el-form-item label="申请人">
          <span>UID {{ approveRow.uid }} / {{ approveRow.name }}</span>
        </el-form-item>
        <el-form-item label="收款方式">
          <span>{{ typeText(approveRow.type) }} - {{ approveRow.account }}</span>
        </el-form-item>
        <el-form-item label="申请金额">
          <span>{{ formatMoney(approveRow.money) }} 元</span>
        </el-form-item>
        <el-form-item label="实际转账">
          <el-input-number v-model="approveMoney" :precision="2" :min="0" :step="1" />
          <div class="form-tip">默认按申请金额填入，可按实际到账修改</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="primary" :loading="approveLoading" @click="submitApprove">确认通过</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTixianList, approveTixian, rejectTixian } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const statusFilter = ref(0)
const query = reactive({ page: 1, size: 20 })

const approveVisible = ref(false)
const approveRow = ref(null)
const approveMoney = ref(0)
const approveLoading = ref(false)

function formatMoney(v) {
  return Number(v || 0).toFixed(2)
}

function typeText(t) {
  const s = String(t)
  if (s === '0') return '支付宝'
  if (s === '1') return '微信'
  if (s === '2') return 'QQ钱包'
  return s
}

function statusText(s) {
  if (s === 1) return '已通过'
  if (s === 2) return '已拒绝'
  return '待审核'
}

function statusTag(s) {
  if (s === 1) return 'success'
  if (s === 2) return 'danger'
  return 'warning'
}

async function loadData() {
  loading.value = true
  try {
    const params = { page: query.page, size: query.size }
    if (statusFilter.value >= 0) params.status = statusFilter.value
    const res = await getTixianList(params)
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

function openApprove(row) {
  approveRow.value = row
  approveMoney.value = Number(row.money || 0)
  approveVisible.value = true
}

async function submitApprove() {
  approveLoading.value = true
  try {
    const res = await approveTixian(approveRow.value.id, approveMoney.value)
    if (res.code === 200) {
      ElMessage.success('已通过审核')
      approveVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } finally {
    approveLoading.value = false
  }
}

async function handleReject(row) {
  const confirmed = await ElMessageBox.confirm(
    '拒绝后将退回 ' + formatMoney(row.money) + ' 元到用户余额，确定拒绝？',
    '拒绝提现',
    { type: 'warning' }
  ).catch(() => false)
  if (!confirmed) return
  const res = await rejectTixian(row.id)
  if (res.code === 200) {
    ElMessage.success('已拒绝，余额已退回')
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

.form-tip {
  font-size: 12px;
  color: var(--text-dim);
  margin-top: 4px;
}

.done-tip {
  font-size: 12px;
  color: var(--text-dim);
}
</style>
