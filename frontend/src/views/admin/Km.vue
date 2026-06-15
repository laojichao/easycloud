<template>
  <div class="km-page">
    <div class="page-header animate-in">
      <div class="header-left">
        <h2 class="page-title">
          <span class="title-accent">&gt;_</span> 卡密管理
        </h2>
        <span class="title-meta">共 {{ total }} 条记录</span>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="showGenerateDialog">
          <el-icon><Plus /></el-icon>生成卡密
        </el-button>
      </div>
    </div>

    <!-- Filters -->
    <div class="filter-bar animate-in" style="animation-delay: 0.1s">
      <div class="filter-group">
        <input v-model="filter.appid" class="filter-input" placeholder="应用 ID" />
        <input v-model="filter.keyword" class="filter-input" placeholder="搜索卡密/ID/备注" style="width: 180px" />
        <select v-model="filter.state" class="filter-select">
          <option value="">全部状态</option>
          <option value="y">启用</option>
          <option value="n">禁用</option>
        </select>
        <select v-model="filter.type" class="filter-select">
          <option value="">全部类型</option>
          <option value="code">时长卡</option>
          <option value="single">次数卡</option>
          <option value="vip">会员卡</option>
          <option value="fen">积分卡</option>
        </select>
        <select v-model="filter.useStatus" class="filter-select">
          <option value="">全部状态</option>
          <option value="unused">未使用</option>
          <option value="used">已使用</option>
          <option value="expired">已过期</option>
        </select>
        <button class="filter-btn" @click="loadData">
          <el-icon><Search /></el-icon>搜索
        </button>
      </div>
    </div>

    <!-- Batch operations bar -->
    <div class="batch-bar animate-in" v-if="selectedIds.length > 0" style="animation-delay: 0.15s">
      <span class="batch-info">已选 {{ selectedIds.length }} 项</span>
      <el-button size="small" type="success" @click="handleBatch('enable')">启用</el-button>
      <el-button size="small" type="warning" @click="handleBatch('disable')">禁用</el-button>
      <el-button size="small" type="danger" @click="handleBatch('delete')">删除</el-button>
      <el-button size="small" @click="handleBatch('unbind')">解绑</el-button>
      <el-button size="small" @click="showTimeDialog('add')">加时长</el-button>
      <el-button size="small" @click="showTimeDialog('sub')">减时长</el-button>
      <el-button size="small" @click="handleExport('selected')">导出选中</el-button>
      <el-button size="small" @click="selectedIds = []">取消</el-button>
    </div>

    <!-- Action bar -->
    <div class="action-bar animate-in" style="animation-delay: 0.15s" v-if="selectedIds.length === 0">
      <el-button size="small" @click="handleExport('all')">导出全部</el-button>
      <el-dropdown @command="handleClean" trigger="click">
        <el-button size="small" type="danger" plain>清理卡密 <el-icon><ArrowDown /></el-icon></el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="all">清空全部</el-dropdown-item>
            <el-dropdown-item command="used">清空已使用</el-dropdown-item>
            <el-dropdown-item command="unused">清空未使用</el-dropdown-item>
            <el-dropdown-item command="expired">清空已过期</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- Table -->
    <div class="table-wrapper animate-in" style="animation-delay: 0.2s">
      <el-table :data="list" v-loading="loading" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="45" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="kami" label="卡密" min-width="160">
          <template #default="{ row }">
            <span class="kami-text">{{ row.kami }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="appid" label="应用" width="70" />
        <el-table-column label="类型" width="70">
          <template #default="{ row }">
            <span class="type-tag" :class="'type-' + row.type">
              {{ typeLabels[row.type] || row.type }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="详情" width="120">
          <template #default="{ row }">
            <span class="mono-text" style="font-size: 11px">
              <template v-if="row.type === 'code'">{{ row.kmTime || '-' }}</template>
              <template v-else>{{ row.amount ?? '-' }}次</template>
            </span>
          </template>
        </el-table-column>
        <el-table-column label="备注" width="100" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="note-text">{{ row.note || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="使用者" width="100" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="mono-text" style="font-size: 11px">{{ row.user || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="登录IP" width="110">
          <template #default="{ row }">
            <span class="mono-text" style="font-size: 11px">{{ row.userIp || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="解绑次数" width="80">
          <template #default="{ row }">
            <span class="mono-text">{{ row.kmChange ?? 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="到期时间" width="140">
          <template #default="{ row }">
            <span class="mono-text" style="font-size: 11px">
              <template v-if="row.endTime === '4102243200'">永久</template>
              <template v-else-if="row.endTime">{{ formatTimestamp(row.endTime) }}</template>
              <template v-else>-</template>
            </span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <span class="status-badge" :class="row.state === 'y' ? 'status-on' : 'status-off'">
              {{ row.state === 'y' ? '启用' : '禁用' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-btns">
              <button class="tbl-btn" :class="row.state === 'y' ? 'tbl-btn-warn' : 'tbl-btn-ok'" @click="handleToggle(row)">
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
      <el-pagination layout="total, prev, pager, next" :total="total" :page-size="pageSize"
        v-model:current-page="currentPage" @current-change="loadData" />
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
            <el-option value="vip" label="会员兑换卡" />
            <el-option value="fen" label="积分兑换卡" />
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
        <el-form-item label="备注">
          <el-input v-model="genForm.note" placeholder="可选备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="genDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="generating" @click="handleGenerate">生成</el-button>
      </template>
    </el-dialog>

    <!-- Time adjust dialog -->
    <el-dialog v-model="timeDialogVisible" :title="timeAction === 'add' ? '批量加时长' : '批量减时长'" width="400px">
      <el-form label-width="80px">
        <el-form-item label="时长(时)">
          <el-input-number v-model="timeHours" :min="1" :max="99999" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="timeDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="batching" @click="handleTimeAdjust">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, ArrowDown } from '@element-plus/icons-vue'
import { getKmList, generateKm, deleteKm, toggleKm, unbindKm, batchKmWithParams, cleanKm } from '@/api/admin'

const typeLabels = { code: '时长', single: '次数', vip: '会员', fen: '积分' }
const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const loading = ref(false)
const genDialogVisible = ref(false)
const generating = ref(false)
const batching = ref(false)
const selectedIds = ref([])

const timeDialogVisible = ref(false)
const timeAction = ref('add')
const timeHours = ref(24)

const filter = reactive({ appid: '', keyword: '', state: '', type: '', useStatus: '' })

const genForm = reactive({
  appid: '', type: 'code', km_time: 'day', amount: 1,
  count: 10, length: 16, prefix: '', structure: 1, note: ''
})

onMounted(() => loadData())

async function loadData() {
  if (loading.value) return
  loading.value = true
  try {
    const params = { page: currentPage.value, size: pageSize.value }
    if (filter.appid) params.appid = filter.appid
    if (filter.keyword) params.keyword = filter.keyword
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

function handleSelectionChange(rows) {
  selectedIds.value = rows.map(r => r.id)
}

function showGenerateDialog() {
  Object.assign(genForm, {
    appid: '', type: 'code', km_time: 'day', amount: 1,
    count: 10, length: 16, prefix: '', structure: 1, note: ''
  })
  genDialogVisible.value = true
}

function showTimeDialog(action) {
  timeAction.value = action
  timeHours.value = 24
  timeDialogVisible.value = true
}

function formatTimestamp(ts) {
  try {
    const d = new Date(parseInt(ts) * 1000)
    return d.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
  } catch { return ts }
}

async function handleGenerate() {
  if (!genForm.appid) { ElMessage.warning('请输入应用 ID'); return }
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

async function handleToggle(row) {
  const res = await toggleKm(row.id, row.state === 'y' ? 'n' : 'y')
  if (res.code === 200) { ElMessage.success('操作成功'); loadData() }
  else ElMessage.error(res.msg || '操作失败')
}

async function handleUnbind(row) {
  await ElMessageBox.confirm('确定解绑该卡密？', '确认', { type: 'warning' })
  const res = await unbindKm(row.id)
  if (res.code === 200) { ElMessage.success('解绑成功'); loadData() }
  else ElMessage.error(res.msg || '解绑失败')
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该卡密？', '确认删除', { type: 'warning' })
  const res = await deleteKm(row.id)
  if (res.code === 200) { ElMessage.success('删除成功'); loadData() }
  else ElMessage.error(res.msg || '删除失败')
}

async function handleBatch(action) {
  const labels = { enable: '启用', disable: '禁用', delete: '删除', unbind: '解绑' }
  if (action === 'delete') {
    await ElMessageBox.confirm(`确定批量删除 ${selectedIds.value.length} 条卡密？`, '确认', { type: 'warning' })
  }
  batching.value = true
  try {
    const res = await batchKmWithParams({ action, ids: selectedIds.value })
    if (res.code === 200) {
      ElMessage.success(`批量${labels[action] || action}成功`)
      selectedIds.value = []
      loadData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } finally {
    batching.value = false
  }
}

async function handleTimeAdjust() {
  const action = timeAction.value === 'add' ? 'add_time' : 'sub_time'
  batching.value = true
  try {
    const res = await batchKmWithParams({ action, ids: selectedIds.value, hours: timeHours.value })
    if (res.code === 200) {
      ElMessage.success('操作成功')
      timeDialogVisible.value = false
      selectedIds.value = []
      loadData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } finally {
    batching.value = false
  }
}

async function handleExport(scope) {
  const params = scope === 'all' ? { action: 'export_all', appid: filter.appid || undefined } : { action: 'export', ids: selectedIds.value }
  batching.value = true
  try {
    const res = await batchKmWithParams(params)
    if (res.code === 200 && res.data) {
      // Download as text file
      const content = Array.isArray(res.data) ? res.data.join('\n') : String(res.data)
      const blob = new Blob([content], { type: 'text/plain;charset=utf-8' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url; a.download = `km-export-${Date.now()}.txt`; a.click()
      URL.revokeObjectURL(url)
      ElMessage.success('导出成功')
    } else {
      ElMessage.error(res.msg || '导出失败')
    }
  } finally {
    batching.value = false
  }
}

async function handleClean(useStatus) {
  const labels = { all: '全部', used: '已使用', unused: '未使用', expired: '已过期' }
  await ElMessageBox.confirm(`确定清空${labels[useStatus]}卡密？此操作不可恢复！`, '确认清理', { type: 'warning' })
  const res = await cleanKm({ appid: filter.appid || undefined, useStatus })
  if (res.code === 200) {
    ElMessage.success('清理成功')
    loadData()
  } else {
    ElMessage.error(res.msg || '清理失败')
  }
}
</script>

<style scoped lang="scss">
.km-page { max-width: 1400px; }

.page-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;
  .header-left { display: flex; align-items: baseline; gap: 12px; }
  .header-actions { display: flex; gap: 10px; }
  .page-title { font-family: var(--font-display); font-size: 20px; font-weight: 700; color: var(--text-primary);
    .title-accent { font-family: var(--font-mono); color: var(--neon-cyan); font-size: 16px; margin-right: 4px; }
  }
  .title-meta { font-family: var(--font-mono); font-size: 12px; color: var(--text-dim); }
}

.filter-bar { margin-bottom: 12px;
  .filter-group { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
}

.batch-bar {
  display: flex; align-items: center; gap: 8px; margin-bottom: 12px; flex-wrap: wrap;
  padding: 10px 16px; background: rgba(0, 240, 255, 0.05); border-radius: var(--radius-md);
  border: 1px solid rgba(0, 240, 255, 0.15);
  .batch-info { font-family: var(--font-mono); font-size: 12px; color: var(--neon-cyan); }
}

.action-bar {
  display: flex; align-items: center; gap: 8px; margin-bottom: 12px;
}

.filter-input, .filter-select {
  font-family: var(--font-mono); font-size: 13px; padding: 8px 12px;
  background: var(--bg-surface); border: 1px solid var(--border-subtle);
  border-radius: var(--radius-sm); color: var(--text-primary); outline: none; transition: all 0.2s ease;
  &:focus { border-color: var(--neon-cyan); box-shadow: 0 0 0 1px var(--neon-cyan), 0 0 8px rgba(0, 240, 255, 0.1); }
  &::placeholder { color: var(--text-dim); }
}

.filter-select {
  cursor: pointer; appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%23666' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E");
  background-repeat: no-repeat; background-position: right 10px center; padding-right: 28px;
  option { background: var(--bg-elevated); color: var(--text-primary); }
}

.filter-btn {
  font-family: var(--font-mono); font-size: 12px; font-weight: 500; padding: 8px 16px;
  background: rgba(0, 240, 255, 0.08); border: 1px solid rgba(0, 240, 255, 0.2);
  border-radius: var(--radius-sm); color: var(--neon-cyan); cursor: pointer;
  display: flex; align-items: center; gap: 6px; transition: all 0.2s ease;
  &:hover { background: rgba(0, 240, 255, 0.15); border-color: rgba(0, 240, 255, 0.4); }
}

.table-wrapper { background: var(--bg-card); border: 1px solid var(--border-subtle); border-radius: var(--radius-lg); overflow: hidden; }

.kami-text { font-family: var(--font-mono); font-size: 12px; color: var(--neon-cyan); letter-spacing: 0.02em; }
.mono-text { font-family: var(--font-mono); font-size: 13px; }
.note-text { font-size: 12px; color: var(--text-dim); }

.type-tag {
  font-family: var(--font-mono); font-size: 11px; font-weight: 500; padding: 2px 8px; border-radius: 4px;
  &.type-code { background: rgba(0, 240, 255, 0.1); color: var(--neon-cyan); border: 1px solid rgba(0, 240, 255, 0.2); }
  &.type-single { background: rgba(255, 170, 0, 0.1); color: var(--neon-amber); border: 1px solid rgba(255, 170, 0, 0.2); }
  &.type-vip { background: rgba(139, 92, 246, 0.1); color: var(--neon-purple); border: 1px solid rgba(139, 92, 246, 0.2); }
  &.type-fen { background: rgba(0, 255, 136, 0.1); color: var(--neon-green); border: 1px solid rgba(0, 255, 136, 0.2); }
}

.status-badge {
  display: inline-flex; align-items: center; gap: 6px; font-family: var(--font-mono);
  font-size: 11px; font-weight: 500; padding: 3px 10px; border-radius: 20px;
  &::before { content: ''; width: 6px; height: 6px; border-radius: 50%; }
  &.status-on { background: rgba(0, 255, 136, 0.1); color: var(--neon-green);
    &::before { background: var(--neon-green); box-shadow: 0 0 6px rgba(0, 255, 136, 0.5); }
  }
  &.status-off { background: rgba(255, 45, 120, 0.1); color: var(--neon-magenta);
    &::before { background: var(--neon-magenta); }
  }
}

.action-btns { display: flex; gap: 4px; }

.tbl-btn {
  font-family: var(--font-mono); font-size: 11px; font-weight: 500; padding: 4px 10px;
  border-radius: var(--radius-sm); border: 1px solid var(--border-subtle);
  background: transparent; cursor: pointer; transition: all 0.2s ease; color: var(--text-secondary);
  &:hover { border-color: var(--border-visible); background: rgba(255, 255, 255, 0.04); }
  &.tbl-btn-ghost { color: var(--neon-cyan); border-color: rgba(0, 240, 255, 0.2);
    &:hover { background: rgba(0, 240, 255, 0.08); }
  }
  &.tbl-btn-ok { color: var(--neon-green); border-color: rgba(0, 255, 136, 0.2);
    &:hover { background: rgba(0, 255, 136, 0.08); }
  }
  &.tbl-btn-warn { color: var(--neon-amber); border-color: rgba(255, 170, 0, 0.2);
    &:hover { background: rgba(255, 170, 0, 0.08); }
  }
  &.tbl-btn-danger { color: var(--neon-magenta); border-color: rgba(255, 45, 120, 0.2);
    &:hover { background: rgba(255, 45, 120, 0.08); }
  }
}

.pagination-bar { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
