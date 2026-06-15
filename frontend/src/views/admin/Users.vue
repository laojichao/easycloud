<template>
  <div class="users-page">
    <div class="page-header animate-in">
      <div class="header-left">
        <h2 class="page-title">
          <span class="title-accent">&gt;_</span> 用户管理
        </h2>
        <span class="title-meta">共 {{ total }} 个用户</span>
      </div>
      <div class="header-actions">
        <el-input v-model="keyword" placeholder="搜索用户名/QQ/邮箱" clearable style="width: 200px"
          @keyup.enter="loadData" @clear="loadData">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" @click="showDialog()">
          <el-icon><Plus /></el-icon>添加用户
        </el-button>
      </div>
    </div>

    <div class="table-wrapper animate-in" style="animation-delay: 0.15s">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="uid" label="UID" width="70" />
        <el-table-column prop="user" label="用户名" min-width="120" />
        <el-table-column prop="qq" label="QQ" width="120" />
        <el-table-column prop="email" label="邮箱" width="160" show-overflow-tooltip />
        <el-table-column label="余额" width="100">
          <template #default="{ row }">
            <span class="mono-text">{{ row.rmb ?? '0.00' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="注册IP" width="130" />
        <el-table-column label="注册时间" width="170">
          <template #default="{ row }">
            <span class="mono-text" style="font-size: 11px">{{ row.addtime || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <div class="action-btns">
              <button class="tbl-btn tbl-btn-ghost" @click="showDialog(row)">编辑</button>
              <button class="tbl-btn tbl-btn-ghost" @click="showRmbDialog(row)">调余额</button>
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

    <!-- Edit dialog -->
    <el-dialog v-model="dialogVisible" :title="editUid ? '编辑用户' : '添加用户'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.user" placeholder="用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.pwd" type="password" :placeholder="editUid ? '留空不修改' : '用户密码'" />
        </el-form-item>
        <el-form-item label="QQ">
          <el-input v-model="form.qq" placeholder="QQ号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="邮箱（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- RMB adjust dialog -->
    <el-dialog v-model="rmbDialogVisible" title="调整余额" width="400px">
      <p style="margin-bottom: 16px; color: var(--text-secondary); font-size: 13px">
        当前用户: <strong style="color: var(--neon-cyan)">{{ rmbUser?.user }}</strong>
        ，当前余额: <strong style="color: var(--neon-amber)">{{ rmbUser?.rmb ?? '0.00' }}</strong>
      </p>
      <el-form label-width="80px">
        <el-form-item label="调整金额">
          <el-input v-model="rmbAmount" type="number" placeholder="正数增加，负数减少" />
          <div class="form-tip">正数增加余额，负数减少余额</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rmbDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="adjusting" @click="handleAdjustRmb">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { getUserList, createUser, updateUser, deleteUser, adjustUserRmb } from '@/api/admin'

const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const editUid = ref(null)
const keyword = ref('')

const rmbDialogVisible = ref(false)
const rmbUser = ref(null)
const rmbAmount = ref('')
const adjusting = ref(false)

const form = reactive({ user: '', pwd: '', qq: '', email: '' })

onMounted(() => loadData())

async function loadData() {
  if (loading.value) return
  loading.value = true
  try {
    const params = { page: currentPage.value, size: pageSize.value }
    if (keyword.value) params.keyword = keyword.value
    const res = await getUserList(params)
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
    editUid.value = row.uid
    Object.assign(form, { user: row.user || '', pwd: '', qq: row.qq || '', email: row.email || '' })
  } else {
    editUid.value = null
    Object.assign(form, { user: '', pwd: '', qq: '', email: '' })
  }
  dialogVisible.value = true
}

function showRmbDialog(row) {
  rmbUser.value = row
  rmbAmount.value = ''
  rmbDialogVisible.value = true
}

async function handleSave() {
  saving.value = true
  try {
    const res = editUid.value
      ? await updateUser(editUid.value, form)
      : await createUser(form)
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

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除用户 ${row.user}？此操作不可恢复。`, '确认删除', { type: 'warning' })
  const res = await deleteUser(row.uid)
  if (res.code === 200) {
    ElMessage.success('删除成功')
    loadData()
  } else {
    ElMessage.error(res.msg || '删除失败')
  }
}

async function handleAdjustRmb() {
  if (!rmbAmount.value || Number(rmbAmount.value) === 0) {
    ElMessage.warning('请输入有效金额')
    return
  }
  adjusting.value = true
  try {
    const res = await adjustUserRmb(rmbUser.value.uid, { amount: Number(rmbAmount.value) })
    if (res.code === 200) {
      ElMessage.success('余额调整成功')
      rmbDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '调整失败')
    }
  } finally {
    adjusting.value = false
  }
}
</script>

<style scoped lang="scss">
.users-page { max-width: 1400px; }

.page-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;
  .header-left { display: flex; align-items: baseline; gap: 12px; }
  .header-actions { display: flex; gap: 10px; align-items: center; }
  .page-title { font-family: var(--font-display); font-size: 20px; font-weight: 700; color: var(--text-primary);
    .title-accent { font-family: var(--font-mono); color: var(--neon-cyan); font-size: 16px; margin-right: 4px; }
  }
  .title-meta { font-family: var(--font-mono); font-size: 12px; color: var(--text-dim); }
}

.table-wrapper { background: var(--bg-card); border: 1px solid var(--border-subtle); border-radius: var(--radius-lg); overflow: hidden; }
.mono-text { font-family: var(--font-mono); font-size: 13px; }
.form-tip { font-size: 11px; color: var(--text-dim); margin-top: 4px; }

.action-btns { display: flex; gap: 4px; }

.tbl-btn {
  font-family: var(--font-mono); font-size: 11px; font-weight: 500; padding: 4px 10px;
  border-radius: var(--radius-sm); border: 1px solid var(--border-subtle);
  background: transparent; cursor: pointer; transition: all 0.2s ease; color: var(--text-secondary);
  &:hover { border-color: var(--border-visible); background: rgba(255, 255, 255, 0.04); }
  &.tbl-btn-ghost { color: var(--neon-cyan); border-color: rgba(0, 240, 255, 0.2);
    &:hover { background: rgba(0, 240, 255, 0.08); }
  }
  &.tbl-btn-danger { color: var(--neon-magenta); border-color: rgba(255, 45, 120, 0.2);
    &:hover { background: rgba(255, 45, 120, 0.08); }
  }
}

.pagination-bar { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
