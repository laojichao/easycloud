<template>
  <div class="km-page">
    <div class="page-header">
      <h2>卡密管理</h2>
      <el-button type="primary" @click="showGenerateDialog">生成卡密</el-button>
    </div>

    <!-- 筛选栏 -->
    <el-form :inline="true" class="filter-form">
      <el-form-item label="应用ID">
        <el-input v-model="filter.appid" placeholder="应用ID" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="filter.state" clearable placeholder="全部">
          <el-option value="y" label="启用" />
          <el-option value="n" label="禁用" />
        </el-select>
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="filter.type" clearable placeholder="全部">
          <el-option value="code" label="时长卡" />
          <el-option value="single" label="次数卡" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">搜索</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="kami" label="卡密" width="200" show-overflow-tooltip />
      <el-table-column prop="appid" label="应用ID" width="80" />
      <el-table-column prop="type" label="类型" width="80">
        <template #default="{ row }">
          <el-tag :type="row.type === 'code' ? '' : 'warning'">
            {{ row.type === 'code' ? '时长卡' : '次数卡' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="kmTime" label="时长类型" width="80" />
      <el-table-column prop="amount" label="次数/数量" width="100" />
      <el-table-column prop="user" label="使用者" width="120" show-overflow-tooltip />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.state === 'y' ? 'success' : 'danger'" size="small">
            {{ row.state === 'y' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button size="small" :type="row.state === 'y' ? 'warning' : 'success'"
            @click="handleToggle(row)">
            {{ row.state === 'y' ? '禁用' : '启用' }}
          </el-button>
          <el-button size="small" @click="handleUnbind(row)">解绑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-if="total > 0" layout="total, prev, pager, next" :total="total"
      :page-size="pageSize" v-model:current-page="currentPage" @current-change="loadData" />

    <!-- 生成卡密对话框 -->
    <el-dialog v-model="genDialogVisible" title="生成卡密" width="500px">
      <el-form :model="genForm" label-width="100px">
        <el-form-item label="应用ID">
          <el-input v-model.number="genForm.appid" />
        </el-form-item>
        <el-form-item label="卡密类型">
          <el-select v-model="genForm.type">
            <el-option value="code" label="时长卡" />
            <el-option value="single" label="次数卡" />
          </el-select>
        </el-form-item>
        <el-form-item label="时长类型" v-if="genForm.type === 'code'">
          <el-select v-model="genForm.km_time">
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
          <el-input-number v-model="genForm.amount" :min="1" />
        </el-form-item>
        <el-form-item label="生成数量">
          <el-input-number v-model="genForm.count" :min="1" :max="10000" />
        </el-form-item>
        <el-form-item label="卡密长度">
          <el-input-number v-model="genForm.length" :min="8" :max="64" />
        </el-form-item>
        <el-form-item label="前缀">
          <el-input v-model="genForm.prefix" placeholder="可选" />
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
    ElMessage.warning('请输入应用ID')
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
  await ElMessageBox.confirm('确定解绑该卡密？', '提示', { type: 'warning' })
  const res = await unbindKm(row.id)
  if (res.code === 200) {
    ElMessage.success('解绑成功')
    loadData()
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该卡密？', '提示', { type: 'warning' })
  const res = await deleteKm(row.id)
  if (res.code === 200) {
    ElMessage.success('删除成功')
    loadData()
  }
}
</script>

<style scoped lang="scss">
.km-page {
  padding: 20px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.filter-form {
  margin-bottom: 20px;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
