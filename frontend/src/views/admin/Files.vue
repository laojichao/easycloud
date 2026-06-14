<template>
  <div class="files-page">
    <div class="page-header">
      <h2>文件管理</h2>
      <el-button type="primary" @click="showDialog()">添加文件</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="appid" label="应用ID" width="80" />
      <el-table-column prop="fileUrl" label="文件链接" show-overflow-tooltip />
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.type === 'lanzou' ? 'warning' : ''">
            {{ row.type === 'lanzou' ? '蓝奏云' : '直链' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="note" label="备注" width="150" show-overflow-tooltip />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.state === 'y' ? 'success' : 'danger'" size="small">
            {{ row.state === 'y' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-if="total > 0" layout="total, prev, pager, next" :total="total"
      :page-size="pageSize" v-model:current-page="currentPage" @current-change="loadData" />

    <el-dialog v-model="dialogVisible" :title="editId ? '编辑文件' : '添加文件'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="应用ID">
          <el-input v-model.number="form.appid" />
        </el-form-item>
        <el-form-item label="文件链接">
          <el-input v-model="form.fileUrl" />
        </el-form-item>
        <el-form-item label="链接类型">
          <el-select v-model="form.type">
            <el-option value="direct" label="直链" />
            <el-option value="lanzou" label="蓝奏云" />
          </el-select>
        </el-form-item>
        <el-form-item label="蓝奏云密码" v-if="form.type === 'lanzou'">
          <el-input v-model="form.lanzouPass" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.note" />
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
import { getFileList, createFile, updateFile, deleteFile } from '@/api/admin'

const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const editId = ref(null)

const form = reactive({
  appid: '',
  fileUrl: '',
  type: 'direct',
  lanzouPass: '',
  note: ''
})

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res = await getFileList({ page: currentPage.value, size: pageSize.value })
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
      appid: row.appid,
      fileUrl: row.fileUrl || '',
      type: row.type || 'direct',
      lanzouPass: row.lanzouPass || '',
      note: row.note || ''
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
    const res = editId.value
      ? await updateFile(editId.value, form)
      : await createFile(form)
    if (res.code === 200) {
      ElMessage.success('保存成功')
      dialogVisible.value = false
      loadData()
    }
  } finally {
    saving.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  const res = await deleteFile(row.id)
  if (res.code === 200) {
    ElMessage.success('删除成功')
    loadData()
  }
}
</script>

<style scoped lang="scss">
.files-page { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
