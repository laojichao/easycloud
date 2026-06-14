<template>
  <div class="settings-page">
    <div class="page-header animate-in">
      <h2 class="page-title">
        <span class="title-accent">&gt;_</span> 系统设置
      </h2>
    </div>

    <div class="settings-content animate-in" style="animation-delay: 0.15s">
      <div class="settings-tabs">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          class="tab-btn"
          :class="{ active: activeTab === tab.key }"
          @click="activeTab = tab.key"
        >
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
              <input v-model="settings.sitename" class="neon-input" placeholder="EasyCloud" />
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
              <input v-model="settings.beian" class="neon-input" placeholder="ICP备案号" />
            </div>
          </div>
          <div class="form-actions">
            <el-button type="primary" :loading="saving" @click="handleSave">
              <el-icon><Check /></el-icon>
              保存设置
            </el-button>
          </div>
        </div>

        <!-- Password -->
        <div v-if="activeTab === 'password'" class="form-section">
          <h3 class="section-label">修改管理员密码</h3>
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
              <el-icon><Lock /></el-icon>
              修改密码
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
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Setting, Lock, Refresh, Check } from '@element-plus/icons-vue'
import { getSettings, saveSettings, refreshCache, changePassword } from '@/api/admin'

const activeTab = ref('site')
const saving = ref(false)
const changingPwd = ref(false)

const tabs = [
  { key: 'site', label: '站点设置', icon: 'Setting' },
  { key: 'password', label: '修改密码', icon: 'Lock' },
  { key: 'maintenance', label: '系统维护', icon: 'Refresh' },
]

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
  try {
    const res = await getSettings()
    if (res.code === 200 && res.data) {
      Object.keys(settings).forEach(key => {
        if (res.data[key] !== undefined) {
          settings[key] = res.data[key]
        }
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
  max-width: 900px;
}

.page-header {
  margin-bottom: 24px;

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
}

.settings-content {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.settings-tabs {
  display: flex;
  border-bottom: 1px solid var(--border-dim);
  padding: 0 8px;
}

.tab-btn {
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  background: transparent;
  border: none;
  padding: 16px 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  position: relative;
  transition: all 0.2s ease;

  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 20px;
    right: 20px;
    height: 2px;
    background: transparent;
    transition: all 0.2s ease;
  }

  &:hover {
    color: var(--text-primary);
  }

  &.active {
    color: var(--neon-cyan);

    &::after {
      background: var(--neon-cyan);
      box-shadow: 0 0 8px rgba(0, 240, 255, 0.3);
    }
  }
}

.tab-content {
  padding: 28px;
}

.section-label {
  font-family: var(--font-display);
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-dim);
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;

  label {
    font-family: var(--font-body);
    font-size: 13px;
    color: var(--text-secondary);
    font-weight: 500;
  }
}

.neon-input {
  font-family: var(--font-mono);
  font-size: 13px;
  padding: 10px 14px;
  background: var(--bg-surface);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-sm);
  color: var(--text-primary);
  outline: none;
  transition: all 0.2s ease;
  width: 100%;

  &:focus {
    border-color: var(--neon-cyan);
    box-shadow: 0 0 0 1px var(--neon-cyan), 0 0 8px rgba(0, 240, 255, 0.1);
  }

  &::placeholder {
    color: var(--text-dim);
  }
}

.form-actions {
  margin-top: 28px;
  padding-top: 20px;
  border-top: 1px solid var(--border-dim);
}

.maintenance-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.maint-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: var(--bg-surface);
  border: 1px solid var(--border-dim);
  border-radius: var(--radius-md);

  .maint-icon {
    width: 48px;
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(255, 170, 0, 0.08);
    border-radius: var(--radius-sm);
    flex-shrink: 0;
  }

  .maint-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 4px;

    .maint-title {
      font-family: var(--font-display);
      font-size: 14px;
      font-weight: 600;
      color: var(--text-primary);
    }

    .maint-desc {
      font-size: 12px;
      color: var(--text-dim);
    }
  }
}
</style>
