<template>
  <div class="settings-page">
    <h2>系统设置</h2>

    <el-tabs v-model="activeTab">
      <!-- 站点设置 -->
      <el-tab-pane label="站点设置" name="site">
        <el-form :model="settings" label-width="120px" style="max-width: 600px">
          <el-form-item label="站点名称">
            <el-input v-model="settings.sitename" />
          </el-form-item>
          <el-form-item label="站点URL">
            <el-input v-model="settings.siteurl" />
          </el-form-item>
          <el-form-item label="客服QQ">
            <el-input v-model="settings.kfqq" />
          </el-form-item>
          <el-form-item label="备案号">
            <el-input v-model="settings.beian" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="saving" @click="handleSave">保存设置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- 修改密码 -->
      <el-tab-pane label="修改密码" name="password">
        <el-form :model="pwdForm" label-width="120px" style="max-width: 600px">
          <el-form-item label="旧密码">
            <el-input v-model="pwdForm.old_password" type="password" show-password />
          </el-form-item>
          <el-form-item label="新密码">
            <el-input v-model="pwdForm.new_password" type="password" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="changingPwd" @click="handleChangePwd">修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- 系统维护 -->
      <el-tab-pane label="系统维护" name="maintenance">
        <el-space direction="vertical" :size="20">
          <el-button type="warning" @click="handleRefreshCache">刷新缓存</el-button>
        </el-space>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSettings, saveSettings, refreshCache, changePassword } from '@/api/admin'

const activeTab = ref('site')
const saving = ref(false)
const changingPwd = ref(false)

const settings = reactive({
  sitename: '',
  siteurl: '',
  kfqq: '',
  beian: ''
})

const pwdForm = reactive({
  old_password: '',
  new_password: ''
})

onMounted(async () => {
  const res = await getSettings()
  if (res.code === 200 && res.data) {
    Object.keys(settings).forEach(key => {
      if (res.data[key] !== undefined) {
        settings[key] = res.data[key]
      }
    })
  }
})

async function handleSave() {
  saving.value = true
  try {
    const res = await saveSettings(settings)
    if (res.code === 200) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } finally {
    saving.value = false
  }
}

async function handleChangePwd() {
  if (!pwdForm.old_password || !pwdForm.new_password) {
    ElMessage.warning('请填写完整')
    return
  }
  changingPwd.value = true
  try {
    const res = await changePassword(pwdForm)
    if (res.code === 200) {
      ElMessage.success('密码修改成功')
      pwdForm.old_password = ''
      pwdForm.new_password = ''
    } else {
      ElMessage.error(res.msg || '修改失败')
    }
  } finally {
    changingPwd.value = false
  }
}

async function handleRefreshCache() {
  const res = await refreshCache()
  if (res.code === 200) {
    ElMessage.success('缓存刷新成功')
  }
}
</script>

<style scoped lang="scss">
.settings-page {
  padding: 20px;
  h2 { margin-bottom: 20px; }
}
</style>
