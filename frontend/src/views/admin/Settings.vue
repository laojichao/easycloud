<template>
  <div class="settings-page">
    <div class="page-header animate-in">
      <h2 class="page-title">
        <span class="title-accent">&gt;_</span> 系统设置
      </h2>
    </div>

    <div class="settings-content animate-in" style="animation-delay: 0.15s">
      <div class="settings-tabs">
        <button v-for="tab in tabs" :key="tab.key" class="tab-btn"
          :class="{ active: activeTab === tab.key }" @click="activeTab = tab.key">
          <el-icon :size="16"><component :is="tab.icon" /></el-icon>
          {{ tab.label }}
        </button>
      </div>

      <div class="tab-content">
        <!-- Site settings -->
        <div v-if="activeTab === 'site'" class="form-section">
          <h3 class="section-label">站点基本配置</h3>
          <div class="form-grid">
            <div class="form-item">
              <label>站点名称</label>
              <input v-model="settings.sitename" class="neon-input" placeholder="极简云验证" />
            </div>
            <div class="form-item">
              <label>标题后缀</label>
              <input v-model="settings.title" class="neon-input" placeholder="不止于此" />
            </div>
            <div class="form-item">
              <label>站点 URL</label>
              <input v-model="settings.siteurl" class="neon-input" placeholder="https://example.com" />
            </div>
            <div class="form-item">
              <label>客服 QQ</label>
              <input v-model="settings.kfqq" class="neon-input" placeholder="123456" />
            </div>
            <div class="form-item">
              <label>备案号</label>
              <input v-model="settings.icp" class="neon-input" placeholder="ICP备案号" />
            </div>
            <div class="form-item">
              <label>QQ 群号</label>
              <input v-model="settings.qunhao" class="neon-input" placeholder="QQ群号" />
            </div>
            <div class="form-item full-width">
              <label>关键词</label>
              <input v-model="settings.keywords" class="neon-input" placeholder="SEO关键词" />
            </div>
            <div class="form-item full-width">
              <label>站点描述</label>
              <textarea v-model="settings.description" class="neon-input" rows="3" placeholder="SEO描述"></textarea>
            </div>
            <div class="form-item full-width">
              <label>关于我们</label>
              <textarea v-model="settings.orgname" class="neon-input" rows="4" placeholder="关于页面HTML内容"></textarea>
            </div>
            <div class="form-item full-width">
              <label>首页公告</label>
              <textarea v-model="settings.index_notice" class="neon-input" rows="3" placeholder="首页公告内容"></textarea>
            </div>
            <div class="form-item full-width">
              <label>页脚内容</label>
              <textarea v-model="settings.footer" class="neon-input" rows="3" placeholder="页脚HTML内容"></textarea>
            </div>
          </div>
          <div class="form-actions">
            <el-button type="primary" :loading="saving" @click="handleSave">
              <el-icon><Check /></el-icon>保存设置
            </el-button>
          </div>
        </div>

        <!-- Personal info -->
        <div v-if="activeTab === 'personal'" class="form-section">
          <h3 class="section-label">管理员个人信息</h3>
          <div class="form-grid" style="max-width: 500px">
            <div class="form-item">
              <label>管理员 QQ</label>
              <input v-model="settings.admin_qq" class="neon-input" placeholder="QQ号" />
            </div>
            <div class="form-item">
              <label>邮箱</label>
              <input v-model="settings.email" class="neon-input" placeholder="admin@example.com" />
            </div>
            <div class="form-item">
              <label>手机号</label>
              <input v-model="settings.phone" class="neon-input" placeholder="手机号" />
            </div>
            <div class="form-item full-width">
              <label>个性签名</label>
              <input v-model="settings.gxqm" class="neon-input" placeholder="个性签名" />
            </div>
          </div>
          <div class="form-actions">
            <el-button type="primary" :loading="saving" @click="handleSave">
              <el-icon><Check /></el-icon>保存
            </el-button>
          </div>
        </div>

        <!-- Account -->
        <div v-if="activeTab === 'account'" class="form-section">
          <h3 class="section-label">修改管理员账号</h3>
          <div class="form-grid" style="max-width: 400px">
            <div class="form-item">
              <label>新用户名</label>
              <input v-model="accountForm.username" class="neon-input" placeholder="新用户名" />
            </div>
            <div class="form-item">
              <label>当前密码</label>
              <input v-model="accountForm.password" type="password" class="neon-input" placeholder="确认当前密码" />
            </div>
          </div>
          <div class="form-actions">
            <el-button type="primary" :loading="changingAccount" @click="handleChangeAccount">
              <el-icon><User /></el-icon>修改账号
            </el-button>
          </div>

          <h3 class="section-label" style="margin-top: 36px">修改管理员密码</h3>
          <div class="form-grid" style="max-width: 400px">
            <div class="form-item">
              <label>旧密码</label>
              <input v-model="pwdForm.old_password" type="password" class="neon-input" placeholder="当前密码" />
            </div>
            <div class="form-item">
              <label>新密码</label>
              <input v-model="pwdForm.new_password" type="password" class="neon-input" placeholder="新密码（≥6位）" />
            </div>
          </div>
          <div class="form-actions">
            <el-button type="primary" :loading="changingPwd" @click="handleChangePwd">
              <el-icon><Lock /></el-icon>修改密码
            </el-button>
          </div>
        </div>

        <!-- Maintenance -->
        <div v-if="activeTab === 'maintenance'" class="form-section">
          <h3 class="section-label">系统维护</h3>
          <div class="maintenance-actions">
            <div class="maint-card">
              <div class="maint-icon" style="color: var(--neon-amber)">
                <el-icon :size="28"><Refresh /></el-icon>
              </div>
              <div class="maint-info">
                <span class="maint-title">刷新缓存</span>
                <span class="maint-desc">从数据库重新加载配置到 Redis 缓存</span>
              </div>
              <el-button type="warning" @click="handleRefreshCache">执行</el-button>
            </div>
            <div class="maint-card">
              <div class="maint-icon" style="color: var(--neon-cyan)">
                <el-icon :size="28"><Coin /></el-icon>
              </div>
              <div class="maint-info">
                <span class="maint-title">优化数据库</span>
                <span class="maint-desc">对所有表执行 OPTIMIZE TABLE 操作</span>
              </div>
              <el-button type="primary" @click="handleDbOptim">执行</el-button>
            </div>
            <div class="maint-card">
              <div class="maint-icon" style="color: var(--neon-green)">
                <el-icon :size="28"><SetUp /></el-icon>
              </div>
              <div class="maint-info">
                <span class="maint-title">修复数据库</span>
                <span class="maint-desc">对所有表执行 REPAIR TABLE 操作</span>
              </div>
              <el-button type="success" @click="handleDbRepair">执行</el-button>
            </div>
            <div class="maint-card">
              <div class="maint-icon" style="color: var(--neon-purple)">
                <el-icon :size="28"><Message /></el-icon>
              </div>
              <div class="maint-info">
                <span class="maint-title">邮件测试</span>
                <span class="maint-desc">发送测试邮件验证邮件配置是否正确</span>
              </div>
              <el-button @click="handleMailTest">测试</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting, Lock, Refresh, Check, User, Coin, SetUp, Message } from '@element-plus/icons-vue'
import { getSettings, saveSettings, refreshCache, changePassword, changeAccount } from '@/api/admin'
import request from '@/api/request'

const activeTab = ref('site')
const saving = ref(false)
const changingPwd = ref(false)
const changingAccount = ref(false)

const tabs = [
  { key: 'site', label: '站点设置', icon: 'Setting' },
  { key: 'personal', label: '个人信息', icon: 'User' },
  { key: 'account', label: '账号安全', icon: 'Lock' },
  { key: 'maintenance', label: '系统维护', icon: 'Refresh' },
]

const settings = reactive({
  sitename: '', siteurl: '', kfqq: '', icp: '', title: '',
  keywords: '', description: '', orgname: '', qunhao: '',
  index_notice: '', footer: '',
  admin_qq: '', email: '', phone: '', gxqm: ''
})

const pwdForm = reactive({ old_password: '', new_password: '' })
const accountForm = reactive({ username: '', password: '' })

onMounted(async () => {
  try {
    const res = await getSettings()
    if (res.code === 200 && res.data) {
      Object.keys(settings).forEach(key => {
        if (res.data[key] !== undefined) settings[key] = res.data[key]
      })
    }
  } catch (e) {
    console.error('加载设置失败', e)
  }
})

async function handleSave() {
  saving.value = true
  try {
    const res = await saveSettings(settings)
    if (res.code === 200) ElMessage.success('保存成功')
    else ElMessage.error(res.msg || '保存失败')
  } finally {
    saving.value = false
  }
}

async function handleChangePwd() {
  if (!pwdForm.old_password || !pwdForm.new_password) { ElMessage.warning('请填写完整'); return }
  changingPwd.value = true
  try {
    const res = await changePassword(pwdForm)
    if (res.code === 200) {
      ElMessage.success('密码修改成功')
      pwdForm.old_password = ''
      pwdForm.new_password = ''
    } else ElMessage.error(res.msg || '修改失败')
  } finally {
    changingPwd.value = false
  }
}

async function handleChangeAccount() {
  if (!accountForm.username || !accountForm.password) { ElMessage.warning('请填写完整'); return }
  changingAccount.value = true
  try {
    const res = await changeAccount(accountForm)
    if (res.code === 200) {
      ElMessage.success('账号修改成功，请重新登录')
      accountForm.username = ''
      accountForm.password = ''
    } else ElMessage.error(res.msg || '修改失败')
  } finally {
    changingAccount.value = false
  }
}

async function handleRefreshCache() {
  try {
    await ElMessageBox.confirm('确定刷新系统缓存？', '确认操作', { type: 'info', confirmButtonText: '执行', cancelButtonText: '取消' })
  } catch { return }
  const res = await refreshCache()
  if (res.code === 200) ElMessage.success('缓存刷新成功')
  else ElMessage.error(res.msg || '缓存刷新失败')
}

async function handleDbOptim() {
  try {
    await ElMessageBox.confirm('确定优化数据库？此操作可能需要一些时间。', '确认操作', { type: 'warning', confirmButtonText: '执行', cancelButtonText: '取消' })
  } catch { return }
  try {
    const res = await request.post('/api/admin/setting/db-optim')
    if (res.code === 200) ElMessage.success('数据库优化完成')
    else ElMessage.error(res.msg || '操作失败')
  } catch { ElMessage.error('操作失败') }
}

async function handleDbRepair() {
  try {
    await ElMessageBox.confirm('确定修复数据库？此操作将对所有表执行 REPAIR TABLE。', '确认操作', { type: 'warning', confirmButtonText: '执行', cancelButtonText: '取消' })
  } catch { return }
  try {
    const res = await request.post('/api/admin/setting/db-repair')
    if (res.code === 200) ElMessage.success('数据库修复完成')
    else ElMessage.error(res.msg || '操作失败')
  } catch { ElMessage.error('操作失败') }
}

async function handleMailTest() {
  try {
    await ElMessageBox.confirm('确定发送测试邮件？', '确认操作', { type: 'info', confirmButtonText: '发送', cancelButtonText: '取消' })
  } catch { return }
  try {
    const res = await request.post('/api/admin/setting/mail-test')
    if (res.code === 200) ElMessage.success('测试邮件已发送')
    else ElMessage.error(res.msg || '发送失败')
  } catch { ElMessage.error('发送失败') }
}
</script>

<style scoped lang="scss">
.settings-page { max-width: 900px; }

.page-header {
  margin-bottom: 24px;
  .page-title { font-family: var(--font-display); font-size: 20px; font-weight: 700; color: var(--text-primary);
    .title-accent { font-family: var(--font-mono); color: var(--neon-cyan); font-size: 16px; margin-right: 4px; }
  }
}

.settings-content { background: var(--bg-card); border: 1px solid var(--border-subtle); border-radius: var(--radius-lg); overflow: hidden; }

.settings-tabs { display: flex; border-bottom: 1px solid var(--border-dim); padding: 0 8px; flex-wrap: wrap; }

.tab-btn {
  font-family: var(--font-body); font-size: 14px; font-weight: 500; color: var(--text-secondary);
  background: transparent; border: none; padding: 16px 18px; cursor: pointer;
  display: flex; align-items: center; gap: 8px; position: relative; transition: all 0.2s ease;
  &::after { content: ''; position: absolute; bottom: 0; left: 18px; right: 18px; height: 2px; background: transparent; transition: all 0.2s ease; }
  &:hover { color: var(--text-primary); }
  &.active { color: var(--neon-cyan);
    &::after { background: var(--neon-cyan); box-shadow: 0 0 8px rgba(0, 240, 255, 0.3); }
  }
}

.tab-content { padding: 28px; }

.section-label {
  font-family: var(--font-display); font-size: 15px; font-weight: 600; color: var(--text-primary);
  margin-bottom: 20px; padding-bottom: 12px; border-bottom: 1px solid var(--border-dim);
}

.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }

.form-item {
  display: flex; flex-direction: column; gap: 8px;
  &.full-width { grid-column: 1 / -1; }
  label { font-family: var(--font-body); font-size: 13px; color: var(--text-secondary); font-weight: 500; }
}

.neon-input {
  font-family: var(--font-mono); font-size: 13px; padding: 10px 14px;
  background: var(--bg-surface); border: 1px solid var(--border-subtle);
  border-radius: var(--radius-sm); color: var(--text-primary); outline: none; transition: all 0.2s ease; width: 100%;
  &:focus { border-color: var(--neon-cyan); box-shadow: 0 0 0 1px var(--neon-cyan), 0 0 8px rgba(0, 240, 255, 0.1); }
  &::placeholder { color: var(--text-dim); }
}

textarea.neon-input { resize: vertical; min-height: 60px; }

.form-actions { margin-top: 28px; padding-top: 20px; border-top: 1px solid var(--border-dim); }

.maintenance-actions { display: flex; flex-direction: column; gap: 12px; }

.maint-card {
  display: flex; align-items: center; gap: 16px; padding: 20px;
  background: var(--bg-surface); border: 1px solid var(--border-dim); border-radius: var(--radius-md);
  .maint-icon { width: 48px; height: 48px; display: flex; align-items: center; justify-content: center;
    background: rgba(255, 170, 0, 0.08); border-radius: var(--radius-sm); flex-shrink: 0; }
  .maint-info { flex: 1; display: flex; flex-direction: column; gap: 4px;
    .maint-title { font-family: var(--font-display); font-size: 14px; font-weight: 600; color: var(--text-primary); }
    .maint-desc { font-size: 12px; color: var(--text-dim); }
  }
}
</style>
