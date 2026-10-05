<template>
  <div class="uc-scene">
    <div class="grid-bg"></div>

    <header class="uc-header">
      <div class="brand">
        <span class="bracket">&lt;</span><span class="brand-text">EasyCloud</span><span class="bracket">/&gt;</span>
      </div>
      <div class="header-right">
        <span class="hello">{{ userUsername || '用户' }}</span>
        <el-button text class="link-btn" @click="handleLogout">退出登录</el-button>
        <el-button text class="link-btn" @click="$router.push('/')">返回首页</el-button>
      </div>
    </header>

    <main class="uc-main">
      <!-- 概览 -->
      <section class="stat-row">
        <div class="stat-card">
          <div class="stat-label">账户余额</div>
          <div class="stat-value">{{ formatMoney(info.rmb) }}<span class="unit">元</span></div>
        </div>
        <div class="stat-card">
          <div class="stat-label">积分合计</div>
          <div class="stat-value">{{ formatMoney(info.points) }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-label">我的邀请码</div>
          <div class="stat-value mono">{{ info.inviteCode || '-' }}</div>
        </div>
        <div class="stat-card action-card">
          <div class="stat-label">今日签到</div>
          <el-button
            type="primary"
            :disabled="info.todayChecked"
            :loading="checkinLoading"
            @click="handleCheckin"
          >{{ info.todayChecked ? '已签到' : '立即签到' }}</el-button>
        </div>
      </section>

      <el-tabs v-model="activeTab" class="uc-tabs">
        <!-- 充值 -->
        <el-tab-pane label="余额充值" name="recharge">
          <div class="recharge-box">
            <el-form inline>
              <el-form-item label="充值金额">
                <el-input-number v-model="rechargeAmount" :precision="2" :min="1" :step="10" />
              </el-form-item>
              <el-form-item label="支付方式">
                <el-radio-group v-model="payType">
                  <el-radio-button value="wxpay">微信</el-radio-button>
                  <el-radio-button value="qqpay">QQ</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="payLoading" @click="handlePay">创建订单</el-button>
              </el-form-item>
            </el-form>
            <el-alert v-if="payResult" :title="payResult" type="info" :closable="false" class="pay-tip" />
          </div>
        </el-tab-pane>

        <!-- 提现 -->
        <el-tab-pane label="余额提现" name="tixian">
          <div class="tixian-form">
            <el-form inline>
              <el-form-item label="提现金额">
                <el-input-number v-model="tixianMoney" :precision="2" :min="1" :step="10" />
              </el-form-item>
              <el-form-item label="收款方式">
                <el-select v-model="tixianType" style="width: 120px">
                  <el-option label="支付宝" value="0" />
                  <el-option label="微信" value="1" />
                  <el-option label="QQ钱包" value="2" />
                </el-select>
              </el-form-item>
              <el-form-item label="收款账号">
                <el-input v-model="tixianAccount" placeholder="账号" style="width: 180px" />
              </el-form-item>
              <el-form-item label="收款人">
                <el-input v-model="tixianName" placeholder="真实姓名" style="width: 140px" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="tixianLoading" @click="handleTixian">申请提现</el-button>
              </el-form-item>
            </el-form>
          </div>
          <el-table :data="tixianList" v-loading="tixianListLoading" size="small" stripe>
            <el-table-column prop="id" label="编号" width="70" />
            <el-table-column label="金额" width="100" align="right">
              <template #default="scope">{{ formatMoney(scope.row.money) }}</template>
            </el-table-column>
            <el-table-column label="方式" width="90">
              <template #default="scope">{{ typeText(scope.row.type) }}</template>
            </el-table-column>
            <el-table-column prop="account" label="账号" min-width="140" show-overflow-tooltip />
            <el-table-column label="状态" width="90">
              <template #default="scope">
                <el-tag :type="tixianTag(scope.row.status)" size="small">{{ tixianText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="addtime" label="申请时间" width="170" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>

        <!-- 工单 -->
        <el-tab-pane label="工单支持" name="workorder">
          <div class="wo-create">
            <el-input v-model="woTitle" placeholder="问题标题" maxlength="50" class="wo-title-input" />
            <el-input v-model="woContent" type="textarea" :rows="3" placeholder="详细描述您遇到的问题" maxlength="500" />
            <el-button type="primary" :loading="woLoading" @click="handleCreateWo">提交工单</el-button>
          </div>
          <el-table :data="woList" v-loading="woListLoading" size="small" stripe class="wo-table">
            <el-table-column prop="id" label="编号" width="70" />
            <el-table-column prop="title" label="标题" min-width="150" show-overflow-tooltip />
            <el-table-column prop="content" label="内容" min-width="180" show-overflow-tooltip />
            <el-table-column prop="reply" label="客服回复" min-width="180" show-overflow-tooltip />
            <el-table-column label="状态" width="90">
              <template #default="scope">
                <el-tag :type="woTag(scope.row.status)" size="small">{{ woText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="addtime" label="提交时间" width="170" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>

        <!-- 积分 -->
        <el-tab-pane label="我的积分" name="points">
          <el-table :data="pointList" v-loading="pointListLoading" size="small" stripe>
            <el-table-column prop="id" label="编号" width="70" />
            <el-table-column label="变动" width="110" align="right">
              <template #default="scope">{{ formatMoney(scope.row.point) }}</template>
            </el-table-column>
            <el-table-column prop="bz" label="备注" min-width="180" show-overflow-tooltip />
            <el-table-column prop="addtime" label="时间" width="170" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>

        <!-- 邀请 -->
        <el-tab-pane label="邀请记录" name="invite">
          <el-table :data="inviteList" v-loading="inviteListLoading" size="small" stripe>
            <el-table-column prop="id" label="编号" width="70" />
            <el-table-column prop="uid" label="被邀请用户" width="110" />
            <el-table-column label="返利金额" width="110" align="right">
              <template #default="scope">{{ formatMoney(scope.row.money) }}</template>
            </el-table-column>
            <el-table-column prop="bz" label="备注" min-width="180" show-overflow-tooltip />
            <el-table-column prop="creationTime" label="时间" width="170" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>

        <!-- 签到记录 -->
        <el-tab-pane label="签到记录" name="checkin">
          <el-table :data="checkinList" v-loading="checkinListLoading" size="small" stripe>
            <el-table-column prop="id" label="编号" width="70" />
            <el-table-column prop="date" label="日期" width="130" />
            <el-table-column label="奖励" width="110" align="right">
              <template #default="scope">{{ formatMoney(scope.row.reward) }}</template>
            </el-table-column>
            <el-table-column prop="addtime" label="时间" width="170" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getUserInfo, doCheckin, getCheckinList, getPointList, getInviteList,
  applyTixian, getTixianList, createWorkOrder, getWorkOrderList, createPayOrder
} from '@/api/user'
import { useUserStore } from '@/stores/user'
import router from '@/router'

const userStore = useUserStore()
const userUsername = ref(userStore.userUsername)

const activeTab = ref('recharge')
const info = ref({})
const checkinLoading = ref(false)

const rechargeAmount = ref(10)
const payType = ref('wxpay')
const payLoading = ref(false)
const payResult = ref('')

const tixianMoney = ref(10)
const tixianType = ref('0')
const tixianAccount = ref('')
const tixianName = ref('')
const tixianLoading = ref(false)
const tixianList = ref([])
const tixianListLoading = ref(false)

const woTitle = ref('')
const woContent = ref('')
const woLoading = ref(false)
const woList = ref([])
const woListLoading = ref(false)

const pointList = ref([])
const pointListLoading = ref(false)
const inviteList = ref([])
const inviteListLoading = ref(false)
const checkinList = ref([])
const checkinListLoading = ref(false)

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

function tixianText(s) {
  if (s === 1) return '已通过'
  if (s === 2) return '已拒绝'
  return '待审核'
}

function tixianTag(s) {
  if (s === 1) return 'success'
  if (s === 2) return 'danger'
  return 'warning'
}

function woText(s) {
  if (s === 1) return '已回复'
  if (s === 2) return '已关闭'
  return '待处理'
}

function woTag(s) {
  if (s === 1) return 'success'
  if (s === 2) return 'info'
  return 'warning'
}

async function loadInfo() {
  const res = await getUserInfo()
  if (res.code === 200) {
    info.value = res.data || {}
  }
}

async function handleCheckin() {
  checkinLoading.value = true
  try {
    const res = await doCheckin()
    if (res.code === 200) {
      ElMessage.success('签到成功，奖励 ' + formatMoney(res.data?.reward) + ' 元')
      loadInfo()
      loadCheckinList()
    } else {
      ElMessage.warning(res.msg || '今日已签到')
      loadInfo()
    }
  } finally {
    checkinLoading.value = false
  }
}

async function handlePay() {
  payLoading.value = true
  payResult.value = ''
  try {
    const res = await createPayOrder({ amount: rechargeAmount.value, payType: payType.value })
    if (res.code === 200) {
      const url = res.data?.payUrl
      if (url) {
        payResult.value = '订单已创建（' + res.data.orderNo + '），正在打开支付页面...'
        window.open(url, '_blank')
      } else {
        payResult.value = '订单已创建（' + res.data.orderNo + '），支付渠道暂未开放，请联系管理员'
      }
    } else {
      ElMessage.error(res.msg || '下单失败')
    }
  } finally {
    payLoading.value = false
  }
}

async function loadTixianList() {
  tixianListLoading.value = true
  try {
    const res = await getTixianList({ page: 1, size: 20 })
    if (res.code === 200) tixianList.value = res.data.records || []
  } finally {
    tixianListLoading.value = false
  }
}

async function handleTixian() {
  if (!tixianAccount.value || !tixianName.value) {
    ElMessage.warning('请填写收款账号与收款人')
    return
  }
  tixianLoading.value = true
  try {
    const res = await applyTixian({
      money: tixianMoney.value,
      type: tixianType.value,
      account: tixianAccount.value,
      name: tixianName.value
    })
    if (res.code === 200) {
      ElMessage.success('提现申请已提交')
      loadTixianList()
      loadInfo()
    } else {
      ElMessage.error(res.msg || '申请失败')
    }
  } finally {
    tixianLoading.value = false
  }
}

async function loadWoList() {
  woListLoading.value = true
  try {
    const res = await getWorkOrderList({ page: 1, size: 20 })
    if (res.code === 200) woList.value = res.data.records || []
  } finally {
    woListLoading.value = false
  }
}

async function handleCreateWo() {
  if (!woTitle.value.trim() || !woContent.value.trim()) {
    ElMessage.warning('请填写标题与内容')
    return
  }
  woLoading.value = true
  try {
    const res = await createWorkOrder({ title: woTitle.value.trim(), content: woContent.value.trim() })
    if (res.code === 200) {
      ElMessage.success('工单已提交')
      woTitle.value = ''
      woContent.value = ''
      loadWoList()
    } else {
      ElMessage.error(res.msg || '提交失败')
    }
  } finally {
    woLoading.value = false
  }
}

async function loadPointList() {
  pointListLoading.value = true
  try {
    const res = await getPointList({ page: 1, size: 20 })
    if (res.code === 200) pointList.value = res.data.records || []
  } finally {
    pointListLoading.value = false
  }
}

async function loadInviteList() {
  inviteListLoading.value = true
  try {
    const res = await getInviteList({ page: 1, size: 20 })
    if (res.code === 200) inviteList.value = res.data.records || []
  } finally {
    inviteListLoading.value = false
  }
}

async function loadCheckinList() {
  checkinListLoading.value = true
  try {
    const res = await getCheckinList({ page: 1, size: 20 })
    if (res.code === 200) checkinList.value = res.data.records || []
  } finally {
    checkinListLoading.value = false
  }
}

function handleLogout() {
  userStore.userLogout()
}

onMounted(() => {
  loadInfo()
  loadTixianList()
  loadWoList()
  loadPointList()
  loadInviteList()
  loadCheckinList()
})
</script>

<style scoped lang="scss">
.uc-scene {
  min-height: 100vh;
  background: var(--bg-void);
  position: relative;
}

.grid-bg {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(0, 240, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 240, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
  mask-image: radial-gradient(ellipse 60% 60% at 50% 30%, black 20%, transparent 70%);
  pointer-events: none;
}

.uc-header {
  position: sticky;
  top: 0;
  z-index: 20;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 28px;
  background: var(--bg-surface);
  border-bottom: 1px solid var(--border-dim);
  backdrop-filter: blur(12px);

  .brand {
    font-family: var(--font-mono);
    font-size: 18px;
    font-weight: 600;

    .bracket { color: var(--neon-cyan); }
    .brand-text { color: var(--text-primary); }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 12px;

    .hello {
      font-size: 13px;
      color: var(--text-secondary);
    }

    .link-btn {
      color: var(--text-dim);

      &:hover { color: var(--neon-cyan); }
    }
  }
}

.uc-main {
  position: relative;
  max-width: 1080px;
  margin: 0 auto;
  padding: 28px 20px 60px;
}

.stat-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  padding: 20px;

  .stat-label {
    font-family: var(--font-mono);
    font-size: 11px;
    color: var(--text-dim);
    letter-spacing: 0.08em;
    text-transform: uppercase;
    margin-bottom: 10px;
  }

  .stat-value {
    font-family: var(--font-display);
    font-size: 24px;
    font-weight: 700;
    color: var(--text-primary);

    .unit {
      font-size: 12px;
      color: var(--text-dim);
      margin-left: 4px;
    }

    &.mono {
      font-family: var(--font-mono);
      font-size: 18px;
      color: var(--neon-cyan);
    }
  }

  &.action-card {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
}

.uc-tabs {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  padding: 8px 20px 20px;
}

.recharge-box,
.tixian-form,
.wo-create {
  padding: 8px 0 16px;
}

.wo-create {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;

  .wo-title-input {
    max-width: 420px;
  }

  .el-button {
    align-self: flex-start;
  }
}

.pay-tip {
  margin-top: 8px;
}
</style>
