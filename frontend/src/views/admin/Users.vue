<template>
  <div class="page-users">
    <el-card shadow="never" class="panel-card">
      <div class="toolbar">
        <el-input
          v-model="query.search"
          placeholder="搜索用户名 / QQ / 邮箱"
          clearable
          class="search-input"
          @keyup.enter="loadData"
          @clear="loadData"
        >
          <template #append>
            <el-button icon="Search" @click="loadData" />
          </template>
        </el-input>
      </div>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="uid" label="UID" width="80" />
        <el-table-column prop="user" label="用户名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="qq" label="QQ" width="120" show-overflow-tooltip />
        <el-table-column prop="email" label="邮箱" min-width="160" show-overflow-tooltip />
        <el-table-column prop="rmb" label="余额(元)" width="110" align="right">
          <template #default="scope">{{ formatRmb(scope.row.rmb) }}</template>
        </el-table-column>
        <el-table-column prop="invitecode" label="邀请码" width="110" />
        <el-table-column prop="regdate" label="注册时间" width="170" show-overflow-tooltip />
        <el-table-column label="操作" width="230" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="openRmb(scope.row)">调整余额</el-button>
            <el-button size="small" type="warning" link @click="handleResetPwd(scope.row)">重置密码</el-button>
            <el-popconfirm title="确定删除该用户及其全部数据？" @confirm="handleDelete(scope.row)">
              <template #reference>
                <el-button size="small" type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="pageSizes"
          layout="total, sizes, prev, pager, next"
          @change="loadData"
        />
      </div>
    </el-card>

    <el-dialog v-model="rmbVisible" title="调整用户余额" width="420px">
      <el-form label-width="90px">
        <el-form-item label="用户">
          <span>{{ rmbRow ? rmbRow.user + '（UID ' + rmbRow.uid + '）' : '' }}</span>
        </el-form-item>
        <el-form-item label="变动金额">
          <el-input-number v-model="rmbAmount" :precision="2" :step="10" />
          <div class="form-tip">正数增加余额，负数扣减余额（不能扣成负数）</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rmbVisible = false">取消</el-button>
        <el-button type="primary" :loading="rmbLoading" @click="submitRmb">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, updateUser, deleteUser, adjustUserRmb } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageSizes = [20, 50, 100]
const query = reactive({ page: 1, size: 20, search: '' })

const rmbVisible = ref(false)
const rmbRow = ref(null)
const rmbAmount = ref(0)
const rmbLoading = ref(false)

function formatRmb(v) {
  return Number(v || 0).toFixed(2)
}

async function loadData() {
  loading.value = true
  try {
    const res = await getUserList({ page: query.page, size: query.size, search: query.search })
    if (res.code === 200) {
      list.value = res.data.records || []
      total.value = Number(res.data.total || 0)
    }
  } finally {
    loading.value = false
  }
}

function openRmb(row) {
  rmbRow.value = row
  rmbAmount.value = 0
  rmbVisible.value = true
}

async function submitRmb() {
  rmbLoading.value = true
  try {
    const res = await adjustUserRmb(rmbRow.value.uid, rmbAmount.value)
    if (res.code === 200) {
      ElMessage.success(res.msg || '调整成功')
      rmbVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '调整失败')
    }
  } finally {
    rmbLoading.value = false
  }
}

async function handleResetPwd(row) {
  const input = await ElMessageBox.prompt('请输入 ' + row.user + ' 的新密码（至少6位）', '重置密码', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'password',
    inputValidator: (v) => (v && v.length >= 6 ? true : '密码至少6位')
  }).catch(() => null)
  if (!input) return
  const res = await updateUser(row.uid, { password: input.value })
  if (res.code === 200) {
    ElMessage.success('密码已重置')
  } else {
    ElMessage.error(res.msg || '重置失败')
  }
}

async function handleDelete(row) {
  const res = await deleteUser(row.uid)
  if (res.code === 200) {
    ElMessage.success('已删除')
    loadData()
  } else {
    ElMessage.error(res.msg || '删除失败')
  }
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.panel-card {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.search-input {
  width: 320px;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.form-tip {
  font-size: 12px;
  color: var(--text-dim);
  margin-top: 4px;
}
</style>
