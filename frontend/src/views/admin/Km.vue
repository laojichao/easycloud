<template>
  <div class="km-page">
    <div class="page-header animate-in">
      <div class="header-left">
        <h2 class="page-title">
          <span class="title-accent">&gt;_</span> 卡密管理
        </h2>
        <span class="title-meta">共 {{ total }} 条记录</span>
      </div>
      <el-button type="primary" @click="showGenerateDialog">
        <el-icon><Plus /></el-icon>
        生成卡密
      </el-button>
    </div>

    <!-- Filters -->
    <div class="filter-bar animate-in" style="animation-delay: 0.1s">
      <div class="filter-group">
        <input v-model="filter.appid" class="filter-input" placeholder="应用 ID" />
        <select v-model="filter.state" class="filter-select">
          <option value="">全部状态</option>
          <option value="y">启用</option>
          <option value="n">禁用</option>
        </select>
        <select v-model="filter.type" class="filter-select">
          <option value="">全部类型</option>
          <option value="code">时长卡</option>
          <option value="single">次数卡</option>
        </select>
        <select v-model="filter.useStatus" class="filter-select">
          <option value="">全部状态</option>
          <option value="unused">未使用</option>
          <option value="used">已使用</option>
          <option value="expired">已过期</option>
        </select>
        <button class="filter-btn" @click="loadData">
          <el-icon><Search /></el-icon>
          搜索
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="table-wrapper animate-in" style="animation-delay: 0.2s">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="kami" label="卡密" min-width="180">
          <template #default="{ row }">
            <span class="kami-text">{{ row.kami }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="appid" label="应用" width="70" />
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <span class="type-tag" :class="row.type === 'code' ? 'type-code' : 'type-single'">
              {{ row.type === 'code' ? '时长' : '次数' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="kmTime" label="时长" width="70">
          <template #default="{ row }">
            <span class="mono-text">{{ row.kmTime || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="数量" width="70">
          <template #default="{ row }">
            <span class="mono-text">{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="user" label="使用者" width="120" show-overflow-tooltip />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <span class="status-badge" :class="row.state === 'y' ? 'status-on' : 'status-off'">
              {{ row.state === 'y' ? '启用' : '禁用' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <div class="action-btns">
              <button
                class="tbl-btn"
                :class="row.state === 'y' ? 'tbl-btn-warn' : 'tbl-btn-ok'"
                @click="handleToggle(row)"
              >
                {{ row.state === 'y' ? '禁用' : '启用' }}
              </button>
              <button class="tbl-btn tbl-btn-ghost" @click="handleUnbind(row)">解绑</button>
              <button class="tbl-btn tbl-btn-danger" @click="handleDelete(row)">删除</button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-bar animate-in" style="animation-delay: 0.3s" v-if="total > 0">
      <el-pagination
        layout="total, prev, pager, next"
        :total="total"
        :page-size="pageSize"
        v-model:current-page="currentPage"
        @current-change="loadData"
      />
    </div>

    <!-- Generate dialog -->
    <el-dialog v-model="genDialogVisible" title="生成卡密" width="520px">
      <el-form :model="genForm" label-width="100px">
        <el-form-item label="应用 ID">
          <el-input v-model.number="genForm.appid" placeholder="目标应用 ID" />
        </el-form-item>
        <el-form-item label="卡密类型">
          <el-select v-model="genForm.type" style="width: 100%">
            <el-option value="code" label="时长卡" />
            <el-option value="single" label="次数卡" />
          </el-select>
        </el-form-item>
        <el-form-item label="时长类型" v-if="genForm.type === 'code'">
          <el-select v-model="genForm.km_time" style="width: 100%">
            <el-option value="hour" label="小时" />
            <el-option value="day" label="天" />
            <el-option value="week" label="周" />
            <el-option value="month" label="月" />
            <el-option value="season" label="季" />
            <el-option value="year" label="年" />
            <el-option value="longuse" label="永久" />
          </el-select>
        </el-form-item>
        <el-form-item label="次数" v-if="genForm.type === 'single'">
          <el-input-number v-model="genForm.amount" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="生成数量">
          <el-input-number v-model="genForm.count" :min="1" :max="10000" style="width: 100%" />
        </el-form-item>
        <el-form-item label="卡密长度">
          <el-input-number v-model="genForm.length" :min="8" :max="64" style="width: 100%" />
        </el-form-item>
        <el-form-item label="前缀">
          <el-input v-model="genForm.prefix" placeholder="可选前缀" />
        </el-form-item>
        <el-form-item label="字符结构">
          <el-select v-model="genForm.structure" style="width: 100%">
            <el-option :value="1" label="混合大小写+数字" />
            <el-option :value="2" label="大写字母+数字" />
            <el-option :value="3" label="小写字母+数字" />
            <el-option :value="4" label="小写字母" />
            <el-option :value="5" label="大写字母" />
            <el-option :value="6" label="纯数字" />
            <el-option :value="7" label="大小写字母" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="genDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="generating" @click="handleGenerate">生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 卡密管理页面
 *
 * 功能：
 * - 展示卡密列表（支持分页、多条件筛选）
 * - 生成新卡密（支持时长卡、次数卡两种类型）
 * - 单条卡密操作（启用/禁用、解绑设备、删除）
 *
 * 卡密类型体系：
 * - 时长卡（type='code'）：按时间授权，可选时长类型（小时/天/周/月/季/年/永久）
 * - 次数卡（type='single'）：按使用次数授权，设置初始次数
 *
 * 筛选逻辑：
 * - 应用 ID：精确匹配
 * - 状态：y(启用) / n(禁用)
 * - 类型：code(时长卡) / single(次数卡)
 * - 使用状态：unused(未使用) / used(已使用) / expired(已过期)
 *
 * 卡密生成参数：
 * - 应用 ID（必填）、卡密类型、时长/次数、生成数量、卡密长度、前缀、字符结构
 *
 * 对应后端端点：/api/admin/km/**
 */
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { getKmList, generateKm, deleteKm, toggleKm, unbindKm } from '@/api/admin'

/** 卡密列表数据 */
const list = ref([])

/** 列表总条数 */
const total = ref(0)

/** 当前页码 */
const currentPage = ref(1)

/** 每页条数 */
const pageSize = ref(20)

/** 列表加载状态 */
const loading = ref(false)

/** 生成卡密对话框是否可见 */
const genDialogVisible = ref(false)

/** 生成按钮加载状态 */
const generating = ref(false)

/**
 * 筛选条件
 * @property {string} appid - 应用 ID
 * @property {string} state - 启用状态（y/n）
 * @property {string} type - 卡密类型（code/single）
 * @property {string} useStatus - 使用状态（unused/used/expired）
 */
const filter = reactive({ appid: '', state: '', type: '', useStatus: '' })

/**
 * 卡密生成表单
 * @property {string|number} appid - 目标应用 ID（必填）
 * @property {string} type - 卡密类型（code时长卡/single次数卡）
 * @property {string} km_time - 时长类型（hour/day/week/month/season/year/longuse）
 * @property {number} amount - 次数卡的使用次数
 * @property {number} count - 批量生成数量
 * @property {number} length - 卡密字符长度（8-64）
 * @property {string} prefix - 卡密前缀（可选）
 * @property {number} structure - 字符结构（1混合大小写+数字/2大写+数字/3小写+数字/4小写/5大写/6纯数字/7大小写）
 */
const genForm = reactive({
  appid: '',
  type: 'code',
  km_time: 'day',
  amount: 1,
  count: 10,
  length: 16,
  prefix: '',
  structure: 1
})

/** 页面挂载时加载卡密列表 */
onMounted(() => loadData())

/**
 * 加载卡密列表
 * 将筛选条件中非空的字段作为查询参数传给后端
 */
async function loadData() {
  if (loading.value) return
  loading.value = true
  try {
    const params = { page: currentPage.value, size: pageSize.value }
    // 只添加非空的筛选条件
    if (filter.appid) params.appid = filter.appid
    if (filter.state) params.state = filter.state
    if (filter.type) params.type = filter.type
    if (filter.useStatus) params.useStatus = filter.useStatus
    const res = await getKmList(params)
    if (res.code === 200) {
      list.value = res.data?.records || []
      total.value = res.data?.total || 0
    }
  } finally {
    loading.value = false
  }
}

/** 打开生成卡密对话框 */
function showGenerateDialog() {
  genForm.appid = ''
  genForm.type = 'code'
  genForm.km_time = 'day'
  genForm.amount = 1
  genForm.count = 10
  genForm.length = 16
  genForm.prefix = ''
  genForm.structure = 1
  genDialogVisible.value = true
}

/**
 * 执行卡密生成
 * 验证应用 ID 必填后，调用批量生成 API
 */
async function handleGenerate() {
  if (!genForm.appid) {
    ElMessage.warning('请输入应用 ID')
    return
  }
  generating.value = true
  try {
    const res = await generateKm(genForm)
    if (res.code === 200) {
      ElMessage.success(`成功生成 ${res.data?.count || 0} 条卡密`)
      genDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '生成失败')
    }
  } finally {
    generating.value = false
  }
}

/**
 * 切换卡密启用/禁用状态
 * @param {Object} row - 卡密行数据
 */
async function handleToggle(row) {
  const res = await toggleKm(row.id, row.state === 'y' ? 'n' : 'y')
  if (res.code === 200) {
    ElMessage.success('操作成功')
    loadData()
  } else {
    ElMessage.error(res.msg || '操作失败')
  }
}

/**
 * 解绑卡密与设备的绑定关系（带二次确认）
 * @param {Object} row - 卡密行数据
 */
async function handleUnbind(row) {
  await ElMessageBox.confirm('确定解绑该卡密？', '确认', { type: 'warning' })
  const res = await unbindKm(row.id)
  if (res.code === 200) {
    ElMessage.success('解绑成功')
    loadData()
  } else {
    ElMessage.error(res.msg || '解绑失败')
  }
}

/**
 * 删除卡密（带二次确认）
 * @param {Object} row - 卡密行数据
 */
async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该卡密？', '确认删除', { type: 'warning' })
  const res = await deleteKm(row.id)
  if (res.code === 200) {
    ElMessage.success('删除成功')
    loadData()
  } else {
    ElMessage.error(res.msg || '删除失败')
  }
}
</script>

<style scoped lang="scss">
.km-page {
  max-width: 1200px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  .header-left {
    display: flex;
    align-items: baseline;
    gap: 12px;
  }

  .page-title {
    font-family: var(--font-display);
    font-size: 20px;
    font-weight: 700;
    color: var(--text-primary);

    .title-accent {
      font-family: var(--font-mono);
      color: var(--neon-cyan);
      font-size: 16px;
      margin-right: 4px;
    }
  }

  .title-meta {
    font-family: var(--font-mono);
    font-size: 12px;
    color: var(--text-dim);
  }
}

.filter-bar {
  margin-bottom: 16px;

  .filter-group {
    display: flex;
    gap: 8px;
    align-items: center;
  }
}

.filter-input,
.filter-select {
  font-family: var(--font-mono);
  font-size: 13px;
  padding: 8px 12px;
  background: var(--bg-surface);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-sm);
  color: var(--text-primary);
  outline: none;
  transition: all 0.2s ease;

  &:focus {
    border-color: var(--neon-cyan);
    box-shadow: 0 0 0 1px var(--neon-cyan), 0 0 8px rgba(0, 240, 255, 0.1);
  }

  &::placeholder {
    color: var(--text-dim);
  }
}

.filter-select {
  cursor: pointer;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%23666' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 10px center;
  padding-right: 28px;

  option {
    background: var(--bg-elevated);
    color: var(--text-primary);
  }
}

.filter-btn {
  font-family: var(--font-mono);
  font-size: 12px;
  font-weight: 500;
  padding: 8px 16px;
  background: rgba(0, 240, 255, 0.08);
  border: 1px solid rgba(0, 240, 255, 0.2);
  border-radius: var(--radius-sm);
  color: var(--neon-cyan);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s ease;

  &:hover {
    background: rgba(0, 240, 255, 0.15);
    border-color: rgba(0, 240, 255, 0.4);
  }
}

.table-wrapper {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.kami-text {
  font-family: var(--font-mono);
  font-size: 12px;
  color: var(--neon-cyan);
  letter-spacing: 0.02em;
}

.mono-text {
  font-family: var(--font-mono);
  font-size: 13px;
}

.type-tag {
  font-family: var(--font-mono);
  font-size: 11px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 4px;

  &.type-code {
    background: rgba(0, 240, 255, 0.1);
    color: var(--neon-cyan);
    border: 1px solid rgba(0, 240, 255, 0.2);
  }

  &.type-single {
    background: rgba(255, 170, 0, 0.1);
    color: var(--neon-amber);
    border: 1px solid rgba(255, 170, 0, 0.2);
  }
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-family: var(--font-mono);
  font-size: 11px;
  font-weight: 500;
  padding: 3px 10px;
  border-radius: 20px;

  &::before {
    content: '';
    width: 6px;
    height: 6px;
    border-radius: 50%;
  }

  &.status-on {
    background: rgba(0, 255, 136, 0.1);
    color: var(--neon-green);

    &::before {
      background: var(--neon-green);
      box-shadow: 0 0 6px rgba(0, 255, 136, 0.5);
    }
  }

  &.status-off {
    background: rgba(255, 45, 120, 0.1);
    color: var(--neon-magenta);

    &::before {
      background: var(--neon-magenta);
    }
  }
}

.action-btns {
  display: flex;
  gap: 6px;
}

.tbl-btn {
  font-family: var(--font-mono);
  font-size: 11px;
  font-weight: 500;
  padding: 4px 12px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-subtle);
  background: transparent;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--text-secondary);

  &:hover {
    border-color: var(--border-visible);
    background: rgba(255, 255, 255, 0.04);
  }

  &.tbl-btn-ghost {
    color: var(--neon-cyan);
    border-color: rgba(0, 240, 255, 0.2);

    &:hover {
      background: rgba(0, 240, 255, 0.08);
    }
  }

  &.tbl-btn-ok {
    color: var(--neon-green);
    border-color: rgba(0, 255, 136, 0.2);
    &:hover { background: rgba(0, 255, 136, 0.08); }
  }

  &.tbl-btn-warn {
    color: var(--neon-amber);
    border-color: rgba(255, 170, 0, 0.2);
    &:hover { background: rgba(255, 170, 0, 0.08); }
  }

  &.tbl-btn-danger {
    color: var(--neon-magenta);
    border-color: rgba(255, 45, 120, 0.2);
    &:hover { background: rgba(255, 45, 120, 0.08); }
  }
}

.pagination-bar {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
