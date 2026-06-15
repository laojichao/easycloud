<template>
  <div class="files-page">
    <div class="page-header animate-in">
      <div class="header-left">
        <h2 class="page-title">
          <span class="title-accent">&gt;_</span> 文件管理
        </h2>
        <span class="title-meta">共 {{ total }} 个文件</span>
      </div>
      <div class="header-actions">
        <el-input v-model="keyword" placeholder="搜索文件备注/链接" clearable style="width: 200px"
          @keyup.enter="loadData" @clear="loadData">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" @click="showDialog()">
          <el-icon><Plus /></el-icon>添加文件
        </el-button>
      </div>
    </div>

    <!-- Batch operations bar -->
    <div class="batch-bar animate-in" v-if="selectedIds.length > 0" style="animation-delay: 0.1s">
      <span class="batch-info">已选 {{ selectedIds.length }} 项</span>
      <el-button size="small" type="success" @click="handleBatch('enable')">批量启用</el-button>
      <el-button size="small" type="warning" @click="handleBatch('disable')">批量禁用</el-button>
      <el-button size="small" type="danger" @click="handleBatch('delete')">批量删除</el-button>
      <el-button size="small" @click="selectedIds = []">取消选择</el-button>
    </div>

    <div class="table-wrapper animate-in" style="animation-delay: 0.15s">
      <el-table :data="list" v-loading="loading" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="45" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="appid" label="应用" width="70" />
        <el-table-column prop="fileUrl" label="文件链接" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="link-text">{{ row.fileUrl }}</span>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="90">
          <template #default="{ row }">
            <span class="type-tag" :class="row.type === 'lanzou' ? 'type-lanzou' : 'type-direct'">
              {{ row.type === 'lanzou' ? '蓝奏云' : '直链' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="note" label="备注" width="140" show-overflow-tooltip />
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
              <button class="tbl-btn tbl-btn-ghost" @click="showDialog(row)">编辑</button>
              <button class="tbl-btn" :class="row.state === 'y' ? 'tbl-btn-warn' : 'tbl-btn-ok'" @click="handleToggle(row)">
                {{ row.state === 'y' ? '禁用' : '启用' }}
              </button>
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

    <el-dialog v-model="dialogVisible" :title="editId ? '编辑文件' : '添加文件'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="应用 ID">
          <el-input v-model.number="form.appid" placeholder="关联的应用 ID" />
        </el-form-item>
        <el-form-item label="文件链接">
          <el-input v-model="form.fileUrl" placeholder="下载链接" />
        </el-form-item>
        <el-form-item label="链接类型">
          <el-select v-model="form.type" style="width: 100%">
            <el-option value="direct" label="直链" />
            <el-option value="lanzou" label="蓝奏云" />
          </el-select>
        </el-form-item>
        <el-form-item label="蓝奏云密码" v-if="form.type === 'lanzou'">
          <el-input v-model="form.lanzouPass" placeholder="分享密码（可选）" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.note" placeholder="文件说明" />
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
import { Plus, Search } from '@element-plus/icons-vue'
import { getFileList, createFile, updateFile, deleteFile, toggleFile, batchFile } from '@/api/admin'

const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const editId = ref(null)
const keyword = ref('')
const selectedIds = ref([])

const form = reactive({ appid: '', fileUrl: '', type: 'direct', lanzouPass: '', note: '' })

onMounted(() => loadData())

async function loadData() {
  if (loading.value) return
  loading.value = true
  try {
    const params = { page: currentPage.value, size: pageSize.value }
    if (keyword.value) params.keyword = keyword.value
    const res = await getFileList(params)
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

function showDialog(row) {
  if (row) {
    editId.value = row.id
    Object.assign(form, {
      appid: row.appid, fileUrl: row.fileUrl || '',
      type: row.type || 'direct', lanzouPass: row.lanzouPass || '', note: row.note || ''
    })
  } else {
    editId.value = null
    Object.assign(form, { appid: '', fileUrl: '', type: 'direct', lanzouPass: '', note: '' })
  }
  dialogVisible.value = true
}

async function handleSave() {
  saving.value = true
  try {
    const res = editId.value ? await updateFile(editId.value, form) : await createFile(form)
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

async function handleToggle(row) {
  try {
    const res = await toggleFile(row.id)
    if (res.code === 200) { ElMessage.success('操作成功'); loadData() }
    else ElMessage.error(res.msg || '操作失败')
  } catch (e) { ElMessage.error('操作失败') }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除？', '确认删除', { type: 'warning' })
  try {
    const res = await deleteFile(row.id)
    if (res.code === 200) { ElMessage.success('删除成功'); loadData() }
    else ElMessage.error(res.msg || '删除失败')
  } catch (e) { ElMessage.error('删除失败') }
}

async function handleBatch(action) {
  const labels = { enable: '启用', disable: '禁用', delete: '删除' }
  if (action === 'delete') {
    await ElMessageBox.confirm(`确定批量删除 ${selectedIds.value.length} 个文件？`, '确认', { type: 'warning' })
  }
  try {
    const res = await batchFile(action, selectedIds.value)
    if (res.code === 200) {
      ElMessage.success(`批量${labels[action]}成功`)
      selectedIds.value = []
      loadData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) { ElMessage.error('操作失败') }
}
</script>

<style scoped lang="scss">
.files-page { max-width: 1200px; }

.page-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;
  .header-left { display: flex; align-items: baseline; gap: 12px; }
  .header-actions { display: flex; gap: 10px; align-items: center; }
  .page-title { font-family: var(--font-display); font-size: 20px; font-weight: 700; color: var(--text-primary);
    .title-accent { font-family: var(--font-mono); color: var(--neon-cyan); font-size: 16px; margin-right: 4px; }
  }
  .title-meta { font-family: var(--font-mono); font-size: 12px; color: var(--text-dim); }
}

.batch-bar {
  display: flex; align-items: center; gap: 10px; margin-bottom: 12px;
  padding: 10px 16px; background: rgba(0, 240, 255, 0.05); border-radius: var(--radius-md);
  border: 1px solid rgba(0, 240, 255, 0.15);
  .batch-info { font-family: var(--font-mono); font-size: 12px; color: var(--neon-cyan); }
}

.table-wrapper { background: var(--bg-card); border: 1px solid var(--border-subtle); border-radius: var(--radius-lg); overflow: hidden; }

.link-text { font-family: var(--font-mono); font-size: 12px; color: var(--text-secondary); }

.type-tag {
  font-family: var(--font-mono); font-size: 11px; font-weight: 500; padding: 2px 8px; border-radius: 4px;
  &.type-lanzou { background: rgba(139, 92, 246, 0.1); color: var(--neon-purple); border: 1px solid rgba(139, 92, 246, 0.2); }
  &.type-direct { background: rgba(0, 240, 255, 0.1); color: var(--neon-cyan); border: 1px solid rgba(0, 240, 255, 0.2); }
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

.action-btns { display: flex; gap: 6px; }

.tbl-btn {
  font-family: var(--font-mono); font-size: 11px; font-weight: 500; padding: 4px 12px;
  border-radius: var(--radius-sm); border: 1px solid var(--border-subtle);
  background: transparent; cursor: pointer; transition: all 0.2s ease; color: var(--text-secondary);
  &.tbl-btn-ghost { color: var(--neon-cyan); border-color: rgba(0, 240, 255, 0.2);
    &:hover { background: rgba(0, 240, 255, 0.08); }
  }
  &.tbl-btn-danger { color: var(--neon-magenta); border-color: rgba(255, 45, 120, 0.2);
    &:hover { background: rgba(255, 45, 120, 0.08); }
  }
  &.tbl-btn-ok { color: var(--neon-green); border-color: rgba(0, 255, 136, 0.2);
    &:hover { background: rgba(0, 255, 136, 0.08); }
  }
  &.tbl-btn-warn { color: var(--neon-amber); border-color: rgba(255, 170, 0, 0.2);
    &:hover { background: rgba(255, 170, 0, 0.08); }
  }
}

.pagination-bar { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
