<template>
  <div class="apps-page">
    <div class="page-header">
      <h2>应用管理</h2>
      <el-button type="primary" @click="showDialog()">新建应用</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="应用名称" />
      <el-table-column prop="version" label="版本" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.active === 'y' ? 'success' : 'danger'">
            {{ row.active === 'y' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="付费模式" width="100">
        <template #default="{ row }">
          <el-tag :type="row.switch === 'y' ? 'warning' : 'info'">
            {{ row.switch === 'y' ? '付费' : '免费' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="total" label="调用次数" width="100" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showDialog(row)">编辑</el-button>
          <el-button size="small" :type="row.active === 'y' ? 'warning' : 'success'"
            @click="handleToggle(row, 'active', row.active === 'y' ? 'n' : 'y')">
            {{ row.active === 'y' ? '禁用' : '启用' }}
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-if="total > 0" layout="total, prev, pager, next" :total="total"
      :page-size="pageSize" v-model:current-page="currentPage" @current-change="loadData" />

    <!-- 新建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑应用' : '新建应用'" width="600px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="应用名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="版本号">
          <el-input v-model="form.version" />
        </el-form-item>
        <el-form-item label="版本信息">
          <el-input v-model="form.versionInfo" type="textarea" />
        </el-form-item>
        <el-form-item label="公告">
          <el-input v-model="form.appGg" type="textarea" />
        </el-form-item>
        <el-form-item label="更新地址">
          <el-input v-model="form.appUpdateUrl" />
        </el-form-item>
        <el-form-item label="加密类型">
          <el-select v-model="form.miType">
            <el-option :value="0" label="明文" />
            <el-option :value="1" label="RC4(GBK)" />
            <el-option :value="2" label="Base64" />
            <el-option :value="3" label="RC4(hex)" />
            <el-option :value="4" label="AES" />
          </el-select>
        </el-form-item>
        <el-form-item label="RC4密钥">
          <el-input v-model="form.rc4Key" />
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
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该应用？', '提示', { type: 'warning' })
  const res = await deleteApp(row.id)
  if (res.code === 200) {
    ElMessage.success('删除成功')
    loadData()
  }
}
</script>

<style scoped lang="scss">
.apps-page {
  padding: 20px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
