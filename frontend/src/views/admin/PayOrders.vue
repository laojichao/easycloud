<template>
  <div class="page-payorders">
    <el-card shadow="never" class="panel-card">
      <div class="toolbar">
        <el-radio-group v-model="statusFilter" @change="handleFilter">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="pending">待支付</el-radio-button>
          <el-radio-button value="paid">已支付</el-radio-button>
          <el-radio-button value="failed">失败</el-radio-button>
          <el-radio-button value="refunded">已退款</el-radio-button>
        </el-radio-group>
      </div>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" min-width="190" show-overflow-tooltip />
        <el-table-column prop="uid" label="用户UID" width="90" />
        <el-table-column label="金额(元)" width="110" align="right">
          <template #default="scope">{{ formatMoney(scope.row.amount) }}</template>
        </el-table-column>
        <el-table-column label="支付方式" width="100">
          <template #default="scope">
            <el-tag size="small" effect="plain">{{ payTypeText(scope.row.payType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="statusTag(scope.row.status)" size="small">{{ statusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="tradeNo" label="渠道流水号" min-width="160" show-overflow-tooltip />
        <el-table-column prop="payTime" label="支付时间" width="170" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="openDetail(scope.row)">详情</el-button>
            <el-button
              v-if="scope.row.status === 'paid'"
              size="small"
              type="danger"
              link
              @click="handleRefund(scope.row)"
            >退款</el-button>
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

    <el-dialog v-model="detailVisible" title="订单详情" width="480px">
      <el-descriptions :column="1" border size="small" v-if="detailRow">
        <el-descriptions-item label="订单号">{{ detailRow.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户UID">{{ detailRow.uid }}</el-descriptions-item>
        <el-descriptions-item label="金额">{{ formatMoney(detailRow.amount) }} 元</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ payTypeText(detailRow.payType) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusText(detailRow.status) }}</el-descriptions-item>
        <el-descriptions-item label="渠道流水号">{{ detailRow.tradeNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ detailRow.payTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailRow.createTime || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPayOrderList, refundPayOrder } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const statusFilter = ref('')
const query = reactive({ page: 1, size: 20 })

const detailVisible = ref(false)
const detailRow = ref(null)

function formatMoney(v) {
  return Number(v || 0).toFixed(2)
}

function payTypeText(t) {
  const s = String(t || '')
  if (s === 'wxpay') return '微信'
  if (s === 'qqpay') return 'QQ'
  if (s === 'alipay') return '支付宝'
  return s || '-'
}

function statusText(s) {
  if (s === 'paid') return '已支付'
  if (s === 'failed') return '失败'
  if (s === 'refunded') return '已退款'
  return '待支付'
}

function statusTag(s) {
  if (s === 'paid') return 'success'
  if (s === 'failed') return 'danger'
  if (s === 'refunded') return 'info'
  return 'warning'
}

async function loadData() {
  loading.value = true
  try {
    const params = { page: query.page, size: query.size }
    if (statusFilter.value) params.status = statusFilter.value
    const res = await getPayOrderList(params)
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

function openDetail(row) {
  detailRow.value = row
  detailVisible.value = true
}

async function handleRefund(row) {
  const confirmed = await ElMessageBox.confirm(
    '退款将扣除用户 ' + formatMoney(row.amount) + ' 元余额并回滚邀请返利，确定退款？',
    '订单退款',
    { type: 'warning' }
  ).catch(() => false)
  if (!confirmed) return
  const res = await refundPayOrder(row.orderNo)
  if (res.code === 200) {
    ElMessage.success('退款成功')
    loadData()
  } else {
    ElMessage.error(res.msg || '退款失败')
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
</style>
