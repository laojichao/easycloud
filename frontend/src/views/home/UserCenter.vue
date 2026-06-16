<template>
  <div class="user-center">
    <div class="grid-bg"></div>
    <div class="glow-orb orb-1"></div>
    <div class="glow-orb orb-2"></div>

    <!-- Top nav -->
    <nav class="topnav">
      <div class="nav-inner">
        <div class="logo-mark">
          <span class="logo-bracket">&lt;</span>
          <span class="logo-text">EC</span>
          <span class="logo-bracket">/&gt;</span>
        </div>
        <div class="nav-links">
          <router-link to="/" class="nav-link">首页</router-link>
          <span class="nav-link active">用户中心</span>
          <span class="nav-link username-display">{{ userStore.userUsername }}</span>
          <el-button text class="nav-link logout-btn" @click="handleLogout">退出</el-button>
        </div>
      </div>
    </nav>

    <div class="center-content">
      <!-- User info card -->
      <div class="info-card animate-in">
        <div class="info-avatar">
          <el-icon :size="36"><User /></el-icon>
        </div>
        <div class="info-details">
          <h2>{{ userStore.userUsername }}</h2>
          <div class="info-meta">
            <span class="meta-item">
              <el-icon><Coin /></el-icon>
              积分: {{ userInfo.points || 0 }}
            </span>
            <span class="meta-item">
              <el-icon><Connection /></el-icon>
              邀请码: {{ userInfo.inviteCode || '--' }}
            </span>
          </div>
        </div>
        <el-button type="primary" class="checkin-btn" :loading="checkinLoading" @click="handleCheckin">
          <el-icon><Calendar /></el-icon>
          {{ todayChecked ? '今日已签到' : '签到' }}
        </el-button>
      </div>

      <!-- Tab sections -->
      <div class="sections-grid animate-in" style="animation-delay: 0.2s">
        <!-- Checkin records -->
        <div class="section-block">
          <h3 class="section-title"><span class="title-accent">//</span> 签到记录</h3>
          <el-table :data="checkinList" stripe class="cyber-table" size="small" v-loading="checkinListLoading">
            <el-table-column prop="date" label="日期" width="180" />
            <el-table-column prop="reward" label="获得积分" width="120" />
          </el-table>
          <el-empty v-if="!checkinListLoading && checkinList.length === 0" description="暂无签到记录" />
        </div>

        <!-- Work orders -->
        <div class="section-block">
          <div class="section-header">
            <h3 class="section-title"><span class="title-accent">//</span> 工单列表</h3>
            <el-button type="primary" size="small" @click="showWorkOrderDialog = true">
              <el-icon><Plus /></el-icon> 创建工单
            </el-button>
          </div>
          <el-table :data="workOrderList" stripe class="cyber-table" size="small" v-loading="workOrderLoading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 0 ? 'warning' : row.status === 1 ? 'success' : 'info'" size="small">
                  {{ row.status === 0 ? '待处理' : row.status === 1 ? '已处理' : '已关闭' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="addtime" label="创建时间" width="180" />
          </el-table>
          <el-empty v-if="!workOrderLoading && workOrderList.length === 0" description="暂无工单" />
        </div>

        <!-- Invite records -->
        <div class="section-block">
          <h3 class="section-title"><span class="title-accent">//</span> 邀请记录</h3>
          <el-table :data="inviteList" stripe class="cyber-table" size="small" v-loading="inviteLoading">
            <el-table-column prop="qq" label="被邀请人QQ" />
            <el-table-column prop="money" label="获得奖励" width="120" />
            <el-table-column prop="creationTime" label="时间" width="180" />
          </el-table>
          <el-empty v-if="!inviteLoading && inviteList.length === 0" description="暂无邀请记录" />
        </div>

        <!-- Point records -->
        <div class="section-block">
          <h3 class="section-title"><span class="title-accent">//</span> 积分记录</h3>
          <el-table :data="pointList" stripe class="cyber-table" size="small" v-loading="pointLoading">
            <el-table-column prop="action" label="类型" width="100">
              <template #default="{ row }">
                <el-tag size="small">{{ row.action || '-' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="point" label="积分" width="100" />
            <el-table-column prop="addtime" label="时间" width="180" />
          </el-table>
          <el-empty v-if="!pointLoading && pointList.length === 0" description="暂无积分记录" />
        </div>

        <!-- Tixian section -->
        <div class="section-block">
          <div class="section-header">
            <h3 class="section-title"><span class="title-accent">//</span> 提现记录</h3>
            <el-button type="primary" size="small" @click="showTixianDialog = true">
              <el-icon><Wallet /></el-icon> 申请提现
            </el-button>
          </div>
          <el-table :data="tixianList" stripe class="cyber-table" size="small" v-loading="tixianLoading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="money" label="金额" width="100" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 0 ? 'warning' : row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 0 ? '审核中' : row.status === 1 ? '已通过' : '已拒绝' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="addtime" label="时间" width="180" />
          </el-table>
          <el-empty v-if="!tixianLoading && tixianList.length === 0" description="暂无提现记录" />
        </div>
      </div>
    </div>

    <!-- Create work order dialog -->
    <el-dialog v-model="showWorkOrderDialog" title="创建工单" width="480px" class="cyber-dialog">
      <el-form :model="workOrderForm" label-width="0" size="large">
        <el-form-item>
          <el-input v-model="workOrderForm.title" placeholder="工单标题" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="workOrderForm.content" type="textarea" :rows="4" placeholder="详细描述..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showWorkOrderDialog = false">取消</el-button>
        <el-button type="primary" :loading="createWorkOrderLoading" @click="handleCreateWorkOrder">提交</el-button>
      </template>
    </el-dialog>

    <!-- Tixian dialog -->
    <el-dialog v-model="showTixianDialog" title="申请提现" width="480px" class="cyber-dialog">
      <el-form :model="tixianForm" label-width="0" size="large">
        <el-form-item>
          <el-input v-model="tixianForm.amount" placeholder="提现金额" type="number" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="tixianForm.account" placeholder="收款账号（支付宝/微信）" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="tixianForm.name" placeholder="收款人姓名" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showTixianDialog = false">取消</el-button>
        <el-button type="primary" :loading="applyTixianLoading" @click="handleApplyTixian">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 用户中心页面
 *
 * 功能模块（各 Tab 功能）：
 * 1. 用户信息卡：展示用户名、积分余额、邀请码，提供每日签到按钮
 * 2. 签到记录：展示最近 10 条签到记录（日期、获得积分、备注）
 * 3. 工单列表：展示用户提交的工单（ID、标题、状态、创建时间），支持创建新工单
 * 4. 邀请记录：展示邀请好友注册的记录（被邀请人、获得积分、时间）
 * 5. 积分记录：展示积分收支明细（类型收入/支出、积分、备注、时间）
 * 6. 提现记录：展示提现申请记录（ID、金额、状态审核中/已通过/已拒绝、时间），支持申请提现
 *
 * 业务逻辑：
 * - 签到：每日一次，成功后获得积分奖励，按钮变为"今日已签到"
 * - 工单：用户提交问题反馈，支持标题和详细描述
 * - 邀请：通过邀请码邀请好友注册，双方获得积分奖励
 * - 积分：签到、邀请等行为获得积分，可查看收支明细
 * - 提现：使用积分兑换现金，填写金额和收款账号（支付宝/微信），需审核
 *
 * 页面加载时并行请求所有数据（Promise.all），提升加载速度
 *
 * 对应后端端点：/api/user/**
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Coin, Connection, Calendar, Plus, Wallet } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import {
  userCheckin,
  userCheckinList,
  createWorkOrder,
  userWorkOrderList,
  userInviteList,
  userPointList,
  applyTixian,
  userTixianList,
  getUserInfo
} from '@/api/admin'

const userStore = useUserStore()

// ==================== 用户信息 ====================

/**
 * 用户基本信息
 * @property {number} points - 积分余额
 * @property {string} inviteCode - 用户邀请码
 */
const userInfo = reactive({
  points: 0,
  inviteCode: ''
})

// ==================== 签到模块 ====================

/** 今日是否已签到 */
const todayChecked = ref(false)

/** 签到按钮加载状态 */
const checkinLoading = ref(false)

/** 签到记录列表加载状态 */
const checkinListLoading = ref(false)

/** 签到记录列表 */
const checkinList = ref([])

// ==================== 工单模块 ====================

/** 工单列表加载状态 */
const workOrderLoading = ref(false)

/** 工单列表数据 */
const workOrderList = ref([])

/** 创建工单对话框是否可见 */
const showWorkOrderDialog = ref(false)

/** 创建工单按钮加载状态 */
const createWorkOrderLoading = ref(false)

/**
 * 工单创建表单
 * @property {string} title - 工单标题
 * @property {string} content - 工单详细内容
 */
const workOrderForm = reactive({ title: '', content: '' })

// ==================== 邀请模块 ====================

/** 邀请记录列表加载状态 */
const inviteLoading = ref(false)

/** 邀请记录列表 */
const inviteList = ref([])

// ==================== 积分模块 ====================

/** 积分记录列表加载状态 */
const pointLoading = ref(false)

/** 积分记录列表 */
const pointList = ref([])

// ==================== 提现模块 ====================

/** 提现记录列表加载状态 */
const tixianLoading = ref(false)

/** 提现记录列表 */
const tixianList = ref([])

/** 提现申请对话框是否可见 */
const showTixianDialog = ref(false)

/** 提现申请按钮加载状态 */
const applyTixianLoading = ref(false)

/**
 * 提现申请表单
 * @property {string} amount - 提现金额
 * @property {string} account - 收款账号（支付宝/微信）
 * @property {string} remark - 备注（可选）
 */
const tixianForm = reactive({ amount: '', account: '', name: '', type: 'alipay' })

// ==================== 数据加载函数 ====================

/**
 * 加载用户基本信息（积分余额、邀请码）
 */
async function loadUserInfo() {
  try {
    const res = await getUserInfo()
    if (res.code === 200 && res.data) {
      userInfo.points = res.data.points || 0
      userInfo.inviteCode = res.data.inviteCode || ''
    }
  } catch (e) {
    console.error('加载用户信息失败', e)
  }
}

/**
 * 加载签到记录列表
 * 同时判断今日是否已签到（通过比较记录日期与今日日期）
 */
async function loadCheckinList() {
  checkinListLoading.value = true
  try {
    const res = await userCheckinList({ page: 1, size: 10 })
    if (res.code === 200) {
      checkinList.value = res.data?.records || res.data || []
      // 判断今日是否已签到：比较记录中的日期是否包含今日日期
      const today = new Date().toISOString().slice(0, 10)
      todayChecked.value = checkinList.value.some(item => item.date?.startsWith(today))
    }
  } catch (e) {
    console.error('加载签到记录失败', e)
  } finally {
    checkinListLoading.value = false
  }
}

/**
 * 加载工单列表
 */
async function loadWorkOrderList() {
  workOrderLoading.value = true
  try {
    const res = await userWorkOrderList({ page: 1, size: 20 })
    if (res.code === 200) {
      workOrderList.value = res.data?.records || res.data || []
    }
  } catch (e) {
    console.error('加载工单失败', e)
  } finally {
    workOrderLoading.value = false
  }
}

/**
 * 加载邀请记录列表
 */
async function loadInviteList() {
  inviteLoading.value = true
  try {
    const res = await userInviteList({ page: 1, size: 20 })
    if (res.code === 200) {
      inviteList.value = res.data?.records || res.data || []
    }
  } catch (e) {
    console.error('加载邀请记录失败', e)
  } finally {
    inviteLoading.value = false
  }
}

/**
 * 加载积分收支记录列表
 */
async function loadPointList() {
  pointLoading.value = true
  try {
    const res = await userPointList({ page: 1, size: 20 })
    if (res.code === 200) {
      pointList.value = res.data?.records || res.data || []
    }
  } catch (e) {
    console.error('加载积分记录失败', e)
  } finally {
    pointLoading.value = false
  }
}

/**
 * 加载提现记录列表
 */
async function loadTixianList() {
  tixianLoading.value = true
  try {
    const res = await userTixianList({ page: 1, size: 20 })
    if (res.code === 200) {
      tixianList.value = res.data?.records || res.data || []
    }
  } catch (e) {
    console.error('加载提现记录失败', e)
  } finally {
    tixianLoading.value = false
  }
}

// ==================== 操作处理函数 ====================

/**
 * 处理每日签到
 * 如果今日已签到则提示并返回，否则调用签到 API
 * 签到成功后更新积分余额和签到状态，刷新签到记录列表
 */
async function handleCheckin() {
  if (todayChecked.value) {
    ElMessage.info('今日已签到')
    return
  }
  checkinLoading.value = true
  try {
    const res = await userCheckin()
    if (res.code === 200) {
      ElMessage.success('签到成功')
      todayChecked.value = true
      // 签到成功后累加积分
      if (res.data?.reward) {
        userInfo.points = (userInfo.points || 0) + res.data.reward
      }
      await loadCheckinList()
    } else {
      ElMessage.error(res.msg || '签到失败')
    }
  } catch (e) {
    ElMessage.error(e.message || '签到失败')
  } finally {
    checkinLoading.value = false
  }
}

/**
 * 创建工单
 * 验证标题非空后提交，成功后关闭对话框、清空表单、刷新工单列表
 */
async function handleCreateWorkOrder() {
  if (!workOrderForm.title.trim()) {
    ElMessage.warning('请输入工单标题')
    return
  }
  createWorkOrderLoading.value = true
  try {
    const res = await createWorkOrder({
      title: workOrderForm.title,
      content: workOrderForm.content
    })
    if (res.code === 200) {
      ElMessage.success('工单创建成功')
      showWorkOrderDialog.value = false
      // 清空表单
      workOrderForm.title = ''
      workOrderForm.content = ''
      await loadWorkOrderList()
    } else {
      ElMessage.error(res.msg || '创建失败')
    }
  } catch (e) {
    ElMessage.error(e.message || '创建失败')
  } finally {
    createWorkOrderLoading.value = false
  }
}

/**
 * 申请提现
 * 验证金额和收款账号非空后提交，成功后关闭对话框、清空表单、刷新提现记录
 */
async function handleApplyTixian() {
  if (!tixianForm.amount || Number(tixianForm.amount) <= 0) {
    ElMessage.warning('请输入有效金额')
    return
  }
  if (!tixianForm.account.trim()) {
    ElMessage.warning('请输入收款账号')
    return
  }
  applyTixianLoading.value = true
  try {
    const res = await applyTixian({
      money: Number(tixianForm.amount),
      account: tixianForm.account,
      name: tixianForm.name,
      type: tixianForm.type
    })
    if (res.code === 200) {
      ElMessage.success('提现申请已提交')
      showTixianDialog.value = false
      // 清空表单
      tixianForm.amount = ''
      tixianForm.account = ''
      tixianForm.name = ''
      await loadTixianList()
    } else {
      ElMessage.error(res.msg || '申请失败')
    }
  } catch (e) {
    ElMessage.error(e.message || '申请失败')
  } finally {
    applyTixianLoading.value = false
  }
}

/**
 * 用户登出
 * 清除 user_token 并跳转到用户登录页
 */
function handleLogout() {
  userStore.userLogout()
}

/**
 * 页面挂载时并行加载所有数据模块
 * 使用 Promise.all 并发请求，避免串行等待，提升页面加载速度
 */
onMounted(async () => {
  await Promise.all([
    loadUserInfo(),
    loadCheckinList(),
    loadWorkOrderList(),
    loadInviteList(),
    loadPointList(),
    loadTixianList()
  ])
})
</script>

<style scoped lang="scss">
.user-center {
  min-height: 100vh;
  background: var(--bg-void);
  position: relative;
  overflow: hidden;
}

.grid-bg {
  position: fixed;
  inset: 0;
  background-image:
    linear-gradient(rgba(0, 240, 255, 0.025) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 240, 255, 0.025) 1px, transparent 1px);
  background-size: 80px 80px;
  mask-image: radial-gradient(ellipse 70% 50% at 50% 30%, black 10%, transparent 60%);
  pointer-events: none;
}

.glow-orb {
  position: fixed;
  border-radius: 50%;
  filter: blur(120px);
  pointer-events: none;
}

.orb-1 {
  width: 400px;
  height: 400px;
  background: var(--neon-purple);
  top: -120px;
  left: -100px;
  opacity: 0.08;
}

.orb-2 {
  width: 300px;
  height: 300px;
  background: var(--neon-cyan);
  bottom: -80px;
  right: -80px;
  opacity: 0.06;
}

/* Nav */
.topnav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(5, 5, 8, 0.8);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid var(--border-dim);
}

.nav-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-mark {
  font-family: var(--font-mono);
  font-size: 20px;
  font-weight: 600;

  .logo-bracket { color: var(--neon-cyan); }
  .logo-text { color: var(--text-primary); }
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-link {
  font-family: var(--font-body);
  font-size: 14px;
  color: var(--text-secondary);
  padding: 8px 16px;
  border-radius: var(--radius-sm);
  transition: all 0.2s ease;

  &:hover, &.active {
    color: var(--text-primary);
    background: rgba(255, 255, 255, 0.04);
  }
}

.username-display {
  font-family: var(--font-mono);
  color: var(--neon-cyan);
  font-size: 13px;
}

.logout-btn {
  font-size: 13px !important;
  color: var(--text-dim) !important;

  &:hover {
    color: var(--neon-magenta) !important;
  }
}

/* Content */
.center-content {
  max-width: 1000px;
  margin: 0 auto;
  padding: 96px 32px 48px;
}

/* Info card */
.info-card {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  padding: 28px 32px;
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 32px;
}

.info-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: rgba(0, 240, 255, 0.1);
  border: 2px solid rgba(0, 240, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--neon-cyan);
  flex-shrink: 0;
}

.info-details {
  flex: 1;

  h2 {
    font-family: var(--font-display);
    font-size: 22px;
    font-weight: 700;
    color: var(--text-primary);
    margin-bottom: 8px;
  }

  .info-meta {
    display: flex;
    gap: 20px;
  }

  .meta-item {
    display: flex;
    align-items: center;
    gap: 6px;
    font-family: var(--font-mono);
    font-size: 13px;
    color: var(--text-secondary);
  }
}

.checkin-btn {
  flex-shrink: 0;
  height: 42px;
  font-family: var(--font-display);
  font-size: 14px;
  font-weight: 600;
  border-radius: var(--radius-md) !important;
}

/* Sections */
.sections-grid {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.section-block {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  padding: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;

  .title-accent {
    font-family: var(--font-mono);
    color: var(--neon-cyan);
    margin-right: 8px;
    opacity: 0.7;
  }
}

.section-header .section-title {
  margin-bottom: 0;
}

/* Cyber table overrides */
.cyber-table {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: rgba(0, 240, 255, 0.04);
  --el-table-row-hover-bg-color: rgba(0, 240, 255, 0.06);
  --el-table-border-color: var(--border-dim);
  --el-table-text-color: var(--text-secondary);
  --el-table-header-text-color: var(--text-primary);
  font-family: var(--font-body);
  font-size: 13px;
}

/* Dialog overrides */
.cyber-dialog {
  background: var(--bg-card) !important;
  border: 1px solid var(--border-subtle) !important;
  border-radius: var(--radius-lg) !important;
}

:deep(.el-dialog) {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
}

:deep(.el-dialog__header) {
  font-family: var(--font-display);
}

:deep(.el-dialog__title) {
  color: var(--text-primary);
}

:deep(.el-empty__description p) {
  color: var(--text-dim);
}
</style>
