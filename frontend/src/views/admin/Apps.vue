<template>
  <div class="apps-page">
    <div class="page-header animate-in">
      <div class="header-left">
        <h2 class="page-title">
          <span class="title-accent">&gt;_</span> 应用管理
        </h2>
        <span class="title-meta">共 {{ total }} 个应用</span>
      </div>
      <el-button type="primary" @click="showDialog()">
        <el-icon><Plus /></el-icon>
        新建应用
      </el-button>
    </div>

    <div class="table-wrapper animate-in" style="animation-delay: 0.15s">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="应用名称" min-width="150" />
        <el-table-column prop="version" label="版本" width="100">
          <template #default="{ row }">
            <span class="mono-text">{{ row.version || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <span class="status-badge" :class="row.active === 'y' ? 'status-on' : 'status-off'">
              {{ row.active === 'y' ? '运行中' : '已停止' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="模式" width="80">
          <template #default="{ row }">
            <span class="mode-tag" :class="row.switch === 'y' ? 'mode-paid' : 'mode-free'">
              {{ row.switch === 'y' ? '付费' : '免费' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="total" label="调用次数" width="100">
          <template #default="{ row }">
            <span class="mono-text">{{ row.total || '0' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <div class="action-btns">
              <button class="tbl-btn tbl-btn-ghost" @click="showDialog(row)">编辑</button>
              <button
                class="tbl-btn"
                :class="row.active === 'y' ? 'tbl-btn-warn' : 'tbl-btn-ok'"
                @click="handleToggle(row, 'active', row.active === 'y' ? 'n' : 'y')"
              >
                {{ row.active === 'y' ? '停止' : '启动' }}
              </button>
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

    <!-- Dialog -->
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑应用' : '新建应用'" width="600px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="应用名称">
          <el-input v-model="form.name" placeholder="输入应用名称" />
        </el-form-item>
        <el-form-item label="版本号">
          <el-input v-model="form.version" placeholder="如 1.0.0" />
        </el-form-item>
        <el-form-item label="版本信息">
          <el-input v-model="form.versionInfo" type="textarea" :rows="3" placeholder="版本更新说明" />
        </el-form-item>
        <el-form-item label="公告">
          <el-input v-model="form.appGg" type="textarea" :rows="3" placeholder="应用公告内容" />
        </el-form-item>
        <el-form-item label="更新地址">
          <el-input v-model="form.appUpdateUrl" placeholder="下载链接" />
        </el-form-item>
        <el-form-item label="加密类型">
          <el-select v-model="form.miType" style="width: 100%">
            <el-option :value="0" label="明文 (不加密)" />
            <el-option :value="1" label="RC4 (GBK编码)" />
            <el-option :value="2" label="Base64" />
            <el-option :value="3" label="RC4 (原始)" />
            <el-option :value="4" label="AES-128-CBC" />
          </el-select>
        </el-form-item>
        <el-form-item label="RC4密钥" v-if="form.miType === 1 || form.miType === 3 || form.miType === 4">
          <el-input v-model="form.rc4Key" placeholder="加密密钥" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getAppList, createApp, updateApp, deleteApp, toggleApp } from '@/api/admin'

const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const editId = ref(null)

const form = reactive({
  name: '',
  version: '',
  versionInfo: '',
  appGg: '',
  appUpdateUrl: '',
  miType: 0,
  rc4Key: ''
})

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res = await getAppList({ page: currentPage.value, size: pageSize.value })
    if (res.code === 200) {
      list.value = res.data?.records || []
      total.value = res.data?.total || 0
    }
  } finally {
    loading.value = false
  }
}

function showDialog(row) {
  if (row) {
    editId.value = row.id
    Object.assign(form, {
      name: row.name || '',
      version: row.version || '',
      versionInfo: row.versionInfo || '',
      appGg: row.appGg || '',
      appUpdateUrl: row.appUpdateUrl || '',
      miType: row.miType || 0,
      rc4Key: row.rc4Key || ''
    })
  } else {
    editId.value = null
    Object.assign(form, {
      name: '', version: '', versionInfo: '', appGg: '',
      appUpdateUrl: '', miType: 0, rc4Key: ''
    })
  }
  dialogVisible.value = true
}

async function handleSave() {
  saving.value = true
  try {
    const res = editId.value
      ? await updateApp(editId.value, form)
      : await createApp(form)
    if (res.code === 200) {
      ElMessage.success('保存成功')
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } finally {
    saving.value = false
  }
}

async function handleToggle(row, field, value) {
  const res = await toggleApp(row.id, field, value)
  if (res.code === 200) {
    ElMessage.success('操作成功')
    loadData()
  } else {
    ElMessage.error(res.msg || '操作失败')
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该应用？此操作不可恢复。', '确认删除', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消'
  })
  const res = await deleteApp(row.id)
  if (res.code === 200) {
    ElMessage.success('删除成功')
    loadData()
  }
}
</script>

<style scoped lang="scss">
.apps-page {
  max-width: 1200px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

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

.table-wrapper {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.mono-text {
  font-family: var(--font-mono);
  font-size: 13px;
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
  letter-spacing: 0.03em;

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

.mode-tag {
  font-family: var(--font-mono);
  font-size: 11px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 4px;

  &.mode-paid {
    background: rgba(255, 170, 0, 0.1);
    color: var(--neon-amber);
    border: 1px solid rgba(255, 170, 0, 0.2);
  }

  &.mode-free {
    background: rgba(139, 92, 246, 0.1);
    color: var(--neon-purple);
    border: 1px solid rgba(139, 92, 246, 0.2);
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
      border-color: rgba(0, 240, 255, 0.4);
    }
  }

  &.tbl-btn-ok {
    color: var(--neon-green);
    border-color: rgba(0, 255, 136, 0.2);

    &:hover {
      background: rgba(0, 255, 136, 0.08);
    }
  }

  &.tbl-btn-warn {
    color: var(--neon-amber);
    border-color: rgba(255, 170, 0, 0.2);

    &:hover {
      background: rgba(255, 170, 0, 0.08);
    }
  }

  &.tbl-btn-danger {
    color: var(--neon-magenta);
    border-color: rgba(255, 45, 120, 0.2);

    &:hover {
      background: rgba(255, 45, 120, 0.08);
    }
  }
}

.pagination-bar {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
