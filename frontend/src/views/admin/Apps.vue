<template>
  <div class="apps-page">
    <div class="page-header animate-in">
      <div class="header-left">
        <h2 class="page-title">
          <span class="title-accent">&gt;_</span> 应用管理
        </h2>
        <span class="title-meta">共 {{ total }} 个应用</span>
      </div>
      <div class="header-actions">
        <el-input v-model="keyword" placeholder="搜索应用名称/ID" clearable style="width: 200px"
          @keyup.enter="loadData" @clear="loadData">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" @click="showDialog()">
          <el-icon><Plus /></el-icon>新建应用
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
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="图标" width="60">
          <template #default="{ row }">
            <img v-if="row.img" :src="row.img" class="app-icon" />
            <div v-else class="app-icon-placeholder">{{ (row.name || '?')[0] }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="应用名称" min-width="140" />
        <el-table-column label="APPKEY" width="160">
          <template #default="{ row }">
            <div class="key-cell">
              <span class="mono-text key-text">{{ row.appkey ? row.appkey.slice(0, 12) + '...' : '-' }}</span>
              <button class="tbl-btn tbl-btn-ghost tbl-btn-xs" @click="copyText(row.appkey)" v-if="row.appkey">复制</button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="version" label="版本" width="90">
          <template #default="{ row }">
            <span class="mono-text">{{ row.version || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" width="120" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="note-text" @click="showNoteEdit(row)">{{ row.note || '点击编辑' }}</span>
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
        <el-table-column label="设备验证" width="80">
          <template #default="{ row }">
            <el-tag :type="row.logonCheckIn === 'y' ? 'success' : 'info'" size="small">
              {{ row.logonCheckIn === 'y' ? '开启' : '关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="IP验证" width="80">
          <template #default="{ row }">
            <el-tag :type="row.ipauth === 'y' ? 'success' : 'info'" size="small">
              {{ row.ipauth === 'y' ? '开启' : '关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="total" label="调用次数" width="90">
          <template #default="{ row }">
            <span class="mono-text">{{ row.total || '0' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <div class="action-btns">
              <button class="tbl-btn tbl-btn-ghost" @click="showDialog(row)">编辑</button>
              <button class="tbl-btn tbl-btn-ghost" @click="showSecurityDialog(row)">安全</button>
              <button class="tbl-btn tbl-btn-ghost" @click="showAuthDialog(row)">认证</button>
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

    <!-- Basic Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑应用' : '新建应用'" width="650px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="应用名称">
          <el-input v-model="form.name" placeholder="输入应用名称" />
        </el-form-item>
        <el-form-item label="应用图标">
          <div class="icon-upload">
            <img v-if="form.img" :src="form.img" class="icon-preview" />
            <el-upload
              :show-file-list="false"
              :before-upload="beforeIconUpload"
              :http-request="handleIconUpload"
              accept="image/*"
            >
              <el-button size="small">{{ form.img ? '更换' : '上传' }}</el-button>
            </el-upload>
            <el-button v-if="form.img" size="small" type="danger" @click="form.img = ''">移除</el-button>
          </div>
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
        <el-form-item label="更新地址类型">
          <el-radio-group v-model="form.appUpdateUrlType">
            <el-radio value="other">直链</el-radio>
            <el-radio value="lanzou">蓝奏云</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="更新地址">
          <el-input v-model="form.appUpdateUrl" placeholder="下载链接" />
        </el-form-item>
        <el-form-item label="蓝奏云密码" v-if="form.appUpdateUrlType === 'lanzou'">
          <el-input v-model="form.lanzouPass" placeholder="蓝奏云分享密码" />
        </el-form-item>
        <el-form-item label="强制更新">
          <el-switch v-model="form.appUpdateMust" active-value="y" inactive-value="n" />
        </el-form-item>
        <el-form-item label="显示更新">
          <el-input v-model="form.appUpdateShow" type="textarea" :rows="2" placeholder="更新提示内容" />
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
        <el-form-item label="备注">
          <el-input v-model="form.note" placeholder="应用备注（内部使用）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- Security Config Dialog -->
    <el-dialog v-model="securityDialogVisible" title="传输安全配置" width="550px">
      <el-form :model="securityForm" label-width="120px">
        <el-form-item label="加密传输">
          <el-switch v-model="securityForm.miState" active-value="y" inactive-value="n" />
        </el-form-item>
        <el-form-item label="加密方式">
          <el-select v-model="securityForm.miType" style="width: 100%">
            <el-option :value="0" label="明文 (不加密)" />
            <el-option :value="1" label="RC4 (GBK编码)" />
            <el-option :value="2" label="Base64" />
            <el-option :value="3" label="RC4 (原始)" />
            <el-option :value="4" label="AES-128-CBC" />
          </el-select>
        </el-form-item>
        <el-form-item label="RC4密钥" v-if="securityForm.miType !== 0 && securityForm.miType !== 2">
          <el-input v-model="securityForm.rc4Key" placeholder="加密密钥" />
        </el-form-item>
        <el-form-item label="签名验证">
          <el-switch v-model="securityForm.miSign" active-value="y" inactive-value="n" />
        </el-form-item>
        <el-form-item label="内置签名" v-if="securityForm.miSign === 'y'">
          <el-switch v-model="securityForm.miSignIn" active-value="y" inactive-value="n" />
          <div class="form-tip">开启后签名在加密数据内部，关闭则签名在外部参数</div>
        </el-form-item>
        <el-form-item label="输出签名">
          <el-switch v-model="securityForm.printSign" active-value="y" inactive-value="n" />
        </el-form-item>
        <el-form-item label="时间校验(秒)">
          <el-input-number v-model="securityForm.miTime" :min="0" :max="3600" />
          <div class="form-tip">0 表示不校验时间漂移</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="securityDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveSecurity">保存</el-button>
      </template>
    </el-dialog>

    <!-- Auth Config Dialog -->
    <el-dialog v-model="authDialogVisible" title="认证配置" width="550px">
      <el-form :model="authForm" label-width="120px">
        <el-form-item label="卡密验证">
          <el-switch v-model="authForm.switch" active-value="y" inactive-value="n" />
          <div class="form-tip">关闭后客户端免费使用，无需卡密</div>
        </el-form-item>
        <el-form-item label="设备码校验">
          <el-switch v-model="authForm.loginCheckIn" active-value="y" inactive-value="n" />
          <div class="form-tip">开启后同一卡密只能绑定一个设备</div>
        </el-form-item>
        <el-form-item label="IP绑定">
          <el-switch v-model="authForm.ipauth" active-value="y" inactive-value="n" />
        </el-form-item>
        <el-form-item label="允许解绑">
          <el-switch v-model="authForm.kmUnmachine" active-value="y" inactive-value="n" />
        </el-form-item>
        <el-form-item label="最大解绑次数">
          <el-input-number v-model="authForm.kmChange" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="解绑间隔(时)">
          <el-input-number v-model="authForm.kmChangeTime" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="时长卡扣时(时)">
          <el-input-number v-model="authForm.kmChangeNum" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="永久卡解绑次数">
          <el-input-number v-model="authForm.longuseKmChange" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="次数卡扣次">
          <el-input-number v-model="authForm.singleKmChangeNum" :min="0" :max="9999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="authDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveAuth">保存</el-button>
      </template>
    </el-dialog>

    <!-- Note Edit Dialog -->
    <el-dialog v-model="noteDialogVisible" title="编辑备注" width="400px">
      <el-input v-model="noteForm.note" type="textarea" :rows="3" placeholder="应用备注" />
      <template #footer>
        <el-button @click="noteDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveNote">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import {
  getAppList, createApp, updateApp, deleteApp, toggleApp, regenAppKey,
  batchApp, uploadAppImage, updateAppSecurity, updateAppAuth, updateAppInfo
} from '@/api/admin'

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

// Security dialog
const securityDialogVisible = ref(false)
const securityAppId = ref(null)
const securityForm = reactive({
  miState: 'y', miType: 0, rc4Key: '', miSign: 'y',
  miSignIn: 'y', printSign: 'n', miTime: 10
})

// Auth dialog
const authDialogVisible = ref(false)
const authAppId = ref(null)
const authForm = reactive({
  switch: 'y', loginCheckIn: 'y', ipauth: 'y', kmUnmachine: 'y',
  kmChange: 3, kmChangeTime: 24, kmChangeNum: 1,
  longuseKmChange: 3, singleKmChangeNum: 1
})

// Note dialog
const noteDialogVisible = ref(false)
const noteAppId = ref(null)
const noteForm = reactive({ note: '' })

const form = reactive({
  name: '', version: '', versionInfo: '', appGg: '',
  appUpdateUrl: '', appUpdateUrlType: 'other', lanzouPass: '',
  appUpdateMust: 'n', appUpdateShow: '',
  miType: 0, rc4Key: '', img: '', note: ''
})

onMounted(() => loadData())

async function loadData() {
  if (loading.value) return
  loading.value = true
  try {
    const params = { page: currentPage.value, size: pageSize.value }
    if (keyword.value) params.keyword = keyword.value
    const res = await getAppList(params)
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
      name: row.name || '', version: row.version || '',
      versionInfo: row.versionInfo || '', appGg: row.appGg || '',
      appUpdateUrl: row.appUpdateUrl || '',
      appUpdateUrlType: row.appUpdateUrlType || 'other',
      lanzouPass: row.lanzouPass || '',
      appUpdateMust: row.appUpdateMust || 'n',
      appUpdateShow: row.appUpdateShow || '',
      miType: row.miType || 0, rc4Key: row.rc4Key || '',
      img: row.img || '', note: row.note || ''
    })
  } else {
    editId.value = null
    Object.assign(form, {
      name: '', version: '', versionInfo: '', appGg: '',
      appUpdateUrl: '', appUpdateUrlType: 'other', lanzouPass: '',
      appUpdateMust: 'n', appUpdateShow: '',
      miType: 0, rc4Key: '', img: '', note: ''
    })
  }
  dialogVisible.value = true
}

function showSecurityDialog(row) {
  securityAppId.value = row.id
  Object.assign(securityForm, {
    miState: row.miState || 'y', miType: row.miType || 0,
    rc4Key: row.rc4Key || '', miSign: row.miSign || 'y',
    miSignIn: row.miSignIn || 'y', printSign: row.printSign || 'n',
    miTime: row.miTime ?? 10
  })
  securityDialogVisible.value = true
}

function showAuthDialog(row) {
  authAppId.value = row.id
  Object.assign(authForm, {
    switch: row.switch || 'y', loginCheckIn: row.logonCheckIn || 'y',
    ipauth: row.ipauth || 'y', kmUnmachine: row.kmUnmachine || 'y',
    kmChange: row.kmChange ?? 3, kmChangeTime: row.kmChangeTime ?? 24,
    kmChangeNum: row.kmChangeNum ?? 1,
    longuseKmChange: row.longuseKmChange ?? 3,
    singleKmChangeNum: row.singleKmChangeNum ?? 1
  })
  authDialogVisible.value = true
}

function showNoteEdit(row) {
  noteAppId.value = row.id
  noteForm.note = row.note || ''
  noteDialogVisible.value = true
}

async function handleSave() {
  saving.value = true
  try {
    const res = editId.value
      ? await updateAppInfo(editId.value, form)
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

async function handleSaveSecurity() {
  saving.value = true
  try {
    const res = await updateAppSecurity(securityAppId.value, securityForm)
    if (res.code === 200) {
      ElMessage.success('安全配置已保存')
      securityDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } finally {
    saving.value = false
  }
}

async function handleSaveAuth() {
  saving.value = true
  try {
    const res = await updateAppAuth(authAppId.value, authForm)
    if (res.code === 200) {
      ElMessage.success('认证配置已保存')
      authDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } finally {
    saving.value = false
  }
}

async function handleSaveNote() {
  saving.value = true
  try {
    const res = await updateAppInfo(noteAppId.value, { note: noteForm.note })
    if (res.code === 200) {
      ElMessage.success('备注已更新')
      noteDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } finally {
    saving.value = false
  }
}

async function handleToggle(row, field, value) {
  try {
    const res = await toggleApp(row.id, field, value)
    if (res.code === 200) {
      ElMessage.success('操作成功')
      loadData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该应用？此操作不可恢复。', '确认删除', {
    type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消'
  })
  try {
    const res = await deleteApp(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

async function handleBatch(action) {
  const labels = { enable: '启用', disable: '禁用', delete: '删除' }
  if (action === 'delete') {
    await ElMessageBox.confirm(`确定批量删除 ${selectedIds.value.length} 个应用？`, '确认', { type: 'warning' })
  }
  try {
    const res = await batchApp(action, selectedIds.value)
    if (res.code === 200) {
      ElMessage.success(`批量${labels[action]}成功`)
      selectedIds.value = []
      loadData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

function beforeIconUpload(file) {
  const ok = ['image/png', 'image/jpeg', 'image/gif', 'image/webp', 'image/bmp'].includes(file.type)
  if (!ok) ElMessage.error('仅支持 png/jpg/gif/webp/bmp 格式')
  const lt2m = file.size / 1024 / 1024 < 2
  if (!lt2m) ElMessage.error('图片大小不能超过 2MB')
  return ok && lt2m
}

async function handleIconUpload(options) {
  try {
    const res = await uploadAppImage(options.file)
    if (res.code === 200 && res.data?.url) {
      form.img = res.data.url
      ElMessage.success('图标上传成功')
    } else {
      ElMessage.error(res.msg || '上传失败')
    }
  } catch {
    ElMessage.error('上传失败')
  }
}

function copyText(text) {
  if (navigator.clipboard) {
    navigator.clipboard.writeText(text)
    ElMessage.success('已复制到剪贴板')
  }
}
</script>

<style scoped lang="scss">
.apps-page { max-width: 1400px; }

.page-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;
  .header-left { display: flex; align-items: baseline; gap: 12px; }
  .header-actions { display: flex; gap: 10px; align-items: center; }
  .page-title {
    font-family: var(--font-display); font-size: 20px; font-weight: 700; color: var(--text-primary);
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

.table-wrapper {
  background: var(--bg-card); border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg); overflow: hidden;
}

.app-icon { width: 32px; height: 32px; border-radius: 6px; object-fit: cover; }
.app-icon-placeholder {
  width: 32px; height: 32px; border-radius: 6px; display: flex; align-items: center; justify-content: center;
  background: rgba(0, 240, 255, 0.1); color: var(--neon-cyan); font-weight: 700; font-size: 14px;
}

.key-cell { display: flex; align-items: center; gap: 6px; }
.key-text { font-size: 11px; }
.note-text { font-size: 12px; color: var(--text-dim); cursor: pointer; &:hover { color: var(--neon-cyan); } }
.form-tip { font-size: 11px; color: var(--text-dim); margin-top: 4px; }
.icon-upload { display: flex; align-items: center; gap: 10px; }
.icon-preview { width: 48px; height: 48px; border-radius: 8px; object-fit: cover; }

.mono-text { font-family: var(--font-mono); font-size: 13px; }

.status-badge {
  display: inline-flex; align-items: center; gap: 6px; font-family: var(--font-mono);
  font-size: 11px; font-weight: 500; padding: 3px 10px; border-radius: 20px; letter-spacing: 0.03em;
  &::before { content: ''; width: 6px; height: 6px; border-radius: 50%; }
  &.status-on { background: rgba(0, 255, 136, 0.1); color: var(--neon-green);
    &::before { background: var(--neon-green); box-shadow: 0 0 6px rgba(0, 255, 136, 0.5); }
  }
  &.status-off { background: rgba(255, 45, 120, 0.1); color: var(--neon-magenta);
    &::before { background: var(--neon-magenta); }
  }
}

.mode-tag {
  font-family: var(--font-mono); font-size: 11px; font-weight: 500; padding: 2px 8px; border-radius: 4px;
  &.mode-paid { background: rgba(255, 170, 0, 0.1); color: var(--neon-amber); border: 1px solid rgba(255, 170, 0, 0.2); }
  &.mode-free { background: rgba(139, 92, 246, 0.1); color: var(--neon-purple); border: 1px solid rgba(139, 92, 246, 0.2); }
}

.action-btns { display: flex; gap: 4px; flex-wrap: wrap; }

.tbl-btn {
  font-family: var(--font-mono); font-size: 11px; font-weight: 500; padding: 4px 10px;
  border-radius: var(--radius-sm); border: 1px solid var(--border-subtle);
  background: transparent; cursor: pointer; transition: all 0.2s ease; color: var(--text-secondary);
  &:hover { border-color: var(--border-visible); background: rgba(255, 255, 255, 0.04); }
  &.tbl-btn-xs { padding: 2px 6px; font-size: 10px; }
  &.tbl-btn-ghost { color: var(--neon-cyan); border-color: rgba(0, 240, 255, 0.2);
    &:hover { background: rgba(0, 240, 255, 0.08); border-color: rgba(0, 240, 255, 0.4); }
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
