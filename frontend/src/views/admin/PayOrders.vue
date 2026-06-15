<template>
  <div class="payorders-page">
    <div class="page-header animate-in">
      <div class="header-left">
        <h2 class="page-title">
          <span class="title-accent">&gt;_</span> 支付订单
        </h2>
        <span class="title-meta">共 {{ total }} 条记录</span>
      </div>
      <div class="header-actions">
        <select v-model="statusFilter" class="filter-select" @change="loadData">
          <option value="">全部状态</option>
          <option value="pending">待支付</option>
          <option value="paid">已支付</option>
          <option value="failed">失败</option>
          <option value="refunded">已退款</option>
        </select>
      </div>
    </div>

    <div class="table-wrapper animate-in" style="animation-delay: 0.15s">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="mono-text" style="font-size: 11px">{{ row.orderNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="uid" label="用户ID" width="80" />
        <el-table-column label="金额" width="100">
          <template #default="{ row }">
            <span class="mono-text" style="color: var(--neon-amber)">{{ row.amount || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="支付方式" width="100">
          <template #default="{ row }">
            <span class="mono-text">{{ row.payType === 'wxpay' ? '微信' : row.payType === 'qqpay' ? 'QQ' : row.payType || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">
            <span class="mono-text" style="font-size: 11px">{{ row.createTime || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="支付时间" width="170">
          <template #default="{ row }">
            <span class="mono-text" style="font-size: 11px">{{ row.payTime || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <div class="action-btns">
              <button class="tbl-btn tbl-btn-danger" @click="handleRefund(row)"
                :disabled="row.status !== 'paid'">退款</button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-bar animate-in" style="animation-delay: 0.3s" v-if="total > 0">
      <el-pagination layout="total, prev, pager, next" :total="total" :page-size="pageSize"
        v-model:current-page="currentPage" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPayOrderList, refundPayOrder } from '@/api/admin'

const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const loading = ref(false)
const statusFilter = ref('')

onMounted(() => loadData())

async function loadData() {
  if (loading.value) return
  loading.value = true
  try {
    const params = { page: currentPage.value, size: pageSize.value }
    if (statusFilter.value) params.status = statusFilter.value
    const res = await getPayOrderList(params)
    if (res.code === 200) {
      list.value = res.data?.records || []
      total.value = res.data?.total || 0
    }
  } finally {
    loading.value = false
  }
}

function statusType(status) {
  const map = { pending: 'warning', paid: 'success', failed: 'danger', refunded: 'info' }
  return map[status] || 'info'
}

function statusLabel(status) {
  const map = { pending: '待支付', paid: '已支付', failed: '失败', refunded: '已退款' }
  return map[status] || status
}

async function handleRefund(row) {
  await ElMessageBox.confirm(`确定对订单 ${row.orderNo} 执行退款？退款将扣减用户余额。`, '确认退款', { type: 'warning' })
  try {
    const res = await refundPayOrder(row.orderNo)
    if (res.code === 200) {
      ElMessage.success('退款成功')
      loadData()
    } else {
      ElMessage.error(res.msg || '退款失败')
    }
  } catch (e) { ElMessage.error('退款失败') }
}
</script>

<style scoped lang="scss">
.payorders-page { max-width: 1400px; }

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
  &.tbl-btn-danger { color: var(--neon-magenta); border-color: rgba(255, 45, 120, 0.2);
    &:hover { background: rgba(255, 45, 120, 0.08); }
  }
}

.pagination-bar { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
