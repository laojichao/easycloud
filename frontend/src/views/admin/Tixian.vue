<template>
  <div class="tixian-page">
    <div class="page-header animate-in">
      <div class="header-left">
        <h2 class="page-title">
          <span class="title-accent">&gt;_</span> 提现审核
        </h2>
        <span class="title-meta">共 {{ total }} 条记录</span>
      </div>
      <div class="header-actions">
        <select v-model="statusFilter" class="filter-select" @change="loadData">
          <option value="">全部状态</option>
          <option :value="0">待处理</option>
          <option :value="1">已处理</option>
          <option :value="2">已拒绝</option>
        </select>
      </div>
    </div>

    <div class="table-wrapper animate-in" style="animation-delay: 0.15s">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="uid" label="用户ID" width="80" />
        <el-table-column label="金额" width="100">
          <template #default="{ row }">
            <span class="mono-text" style="color: var(--neon-amber)">{{ row.amount || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="实际到账" width="100">
          <template #default="{ row }">
            <span class="mono-text" :style="{ color: row.realmoney ? 'var(--neon-green)' : 'var(--text-dim)' }">
              {{ row.realmoney || '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="account" label="收款账号" min-width="150" show-overflow-tooltip />
        <el-table-column prop="name" label="收款人" width="100" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'warning' : row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 0 ? '待处理' : row.status === 1 ? '已通过' : '已拒绝' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="170">
          <template #default="{ row }">
            <span class="mono-text" style="font-size: 11px">{{ row.addtime || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-btns" v-if="row.status === 0">
              <button class="tbl-btn tbl-btn-ok" @click="showApproveDialog(row)">批准</button>
              <button class="tbl-btn tbl-btn-danger" @click="handleReject(row)">拒绝</button>
            </div>
            <span v-else class="mono-text" style="font-size: 11px; color: var(--text-dim)">已处理</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-bar animate-in" style="animation-delay: 0.3s" v-if="total > 0">
      <el-pagination layout="total, prev, pager, next" :total="total" :page-size="pageSize"
        v-model:current-page="currentPage" @current-change="loadData" />
    </div>

    <!-- Approve dialog -->
    <el-dialog v-model="approveDialogVisible" title="批准提现" width="400px">
      <p style="margin-bottom: 16px; color: var(--text-secondary); font-size: 13px">
        用户申请提现: <strong style="color: var(--neon-amber)">{{ approveTarget?.amount }}</strong> 元
      </p>
      <el-form label-width="100px">
        <el-form-item label="实际到账金额">
          <el-input v-model="realMoney" type="number" placeholder="实际转账金额" />
          <div class="form-tip">填写实际转账到用户账户的金额</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="approving" @click="handleApprove">确认批准</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminTixianList, approveTixian, rejectTixian } from '@/api/admin'

const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const loading = ref(false)
const statusFilter = ref('')

const approveDialogVisible = ref(false)
const approveTarget = ref(null)
const realMoney = ref('')
const approving = ref(false)

onMounted(() => loadData())

async function loadData() {
  if (loading.value) return
  loading.value = true
  try {
    const params = { page: currentPage.value, size: pageSize.value }
    if (statusFilter.value !== '') params.status = statusFilter.value
    const res = await getAdminTixianList(params)
    if (res.code === 200) {
      list.value = res.data?.records || []
      total.value = res.data?.total || 0
    }
  } finally {
    loading.value = false
  }
}

function showApproveDialog(row) {
  approveTarget.value = row
  realMoney.value = row.amount || ''
  approveDialogVisible.value = true
}

async function handleApprove() {
  if (!realMoney.value || Number(realMoney.value) <= 0) {
    ElMessage.warning('请输入有效的到账金额')
    return
  }
  if (!approveTarget.value?.id) {
    ElMessage.warning('提现信息异常')
    return
  }
  approving.value = true
  try {
    const res = await approveTixian(approveTarget.value.id, { realmoney: Number(realMoney.value) })
    if (res.code === 200) {
      ElMessage.success('已批准')
      approveDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    approving.value = false
  }
}

async function handleReject(row) {
  await ElMessageBox.confirm('确定拒绝该提现申请？用户余额将自动退回。', '确认拒绝', { type: 'warning' })
  try {
    const res = await rejectTixian(row.id)
    if (res.code === 200) {
      ElMessage.success('已拒绝，余额已退回')
      loadData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) { ElMessage.error('操作失败') }
}
</script>

<style scoped lang="scss">
.tixian-page { max-width: 1400px; }

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
.form-tip { font-size: 11px; color: var(--text-dim); margin-top: 4px; }

.action-btns { display: flex; gap: 4px; }

.tbl-btn {
  font-family: var(--font-mono); font-size: 11px; font-weight: 500; padding: 4px 10px;
  border-radius: var(--radius-sm); border: 1px solid var(--border-subtle);
  background: transparent; cursor: pointer; transition: all 0.2s ease; color: var(--text-secondary);
  &:hover { border-color: var(--border-visible); background: rgba(255, 255, 255, 0.04); }
  &.tbl-btn-ok { color: var(--neon-green); border-color: rgba(0, 255, 136, 0.2);
    &:hover { background: rgba(0, 255, 136, 0.08); }
  }
  &.tbl-btn-danger { color: var(--neon-magenta); border-color: rgba(255, 45, 120, 0.2);
    &:hover { background: rgba(255, 45, 120, 0.08); }
  }
}

.pagination-bar { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
