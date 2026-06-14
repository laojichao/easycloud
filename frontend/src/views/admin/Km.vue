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
      </el-form>
      <template #footer>
        <el-button @click="genDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="generating" @click="handleGenerate">生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { getKmList, generateKm, deleteKm, toggleKm, unbindKm } from '@/api/admin'

const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const loading = ref(false)
const genDialogVisible = ref(false)
const generating = ref(false)

const filter = reactive({ appid: '', state: '', type: '' })

const genForm = reactive({
  appid: '',
  type: 'code',
  km_time: 'day',
  amount: 1,
  count: 10,
  length: 16,
  prefix: ''
})

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const params = { page: currentPage.value, size: pageSize.value }
    if (filter.appid) params.appid = filter.appid
    if (filter.state) params.state = filter.state
    if (filter.type) params.type = filter.type
    const res = await getKmList(params)
    if (res.code === 200) {
      list.value = res.data?.records || []
      total.value = res.data?.total || 0
    }
  } finally {
    loading.value = false
  }
}

function showGenerateDialog() {
  genDialogVisible.value = true
}

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

async function handleToggle(row) {
  const res = await toggleKm(row.id, row.state === 'y' ? 'n' : 'y')
  if (res.code === 200) {
    ElMessage.success('操作成功')
    loadData()
  }
}

async function handleUnbind(row) {
  await ElMessageBox.confirm('确定解绑该卡密？', '确认', { type: 'warning' })
  const res = await unbindKm(row.id)
  if (res.code === 200) {
    ElMessage.success('解绑成功')
    loadData()
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该卡密？', '确认删除', { type: 'warning' })
  const res = await deleteKm(row.id)
  if (res.code === 200) {
    ElMessage.success('删除成功')
    loadData()
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
