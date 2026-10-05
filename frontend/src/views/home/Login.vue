<template>
  <div class="login-scene">
    <div class="grid-bg"></div>
    <div class="glow-orb orb-1"></div>

    <div class="login-card animate-in">
      <div class="accent-line"></div>

      <div class="card-header">
        <div class="logo-mark">
          <span class="logo-bracket">&lt;</span>
          <span class="logo-text">EC</span>
          <span class="logo-bracket">/&gt;</span>
        </div>
        <h1>用户登录</h1>
        <p class="subtitle">授权验证系统</p>
      </div>

      <el-form :model="form" label-width="0" size="large" @keyup.enter="handleLogin">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item v-if="captchaEnabled">
          <div class="captcha-row">
            <el-input v-model="form.captchaCode" placeholder="验证码" prefix-icon="Key" />
            <img
              v-if="captchaImage"
              :src="captchaImage"
              class="captcha-img"
              title="点击刷新验证码"
              @click="loadCaptcha"
            />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>

      <div class="card-footer">
        <router-link to="/register" class="footer-link">注册账号</router-link>
        <router-link to="/" class="footer-link">返回首页</router-link>
        <router-link to="/admin/login" class="footer-link">管理后台</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCaptcha } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const captchaEnabled = ref(false)
const captchaImage = ref('')

const form = reactive({
  username: '',
  password: '',
  captchaId: '',
  captchaCode: ''
})

async function loadCaptcha() {
  try {
    const res = await getCaptcha()
    if (res.code === 200) {
      captchaEnabled.value = !!res.data?.enabled
      if (captchaEnabled.value) {
        captchaImage.value = res.data.image || ''
        form.captchaId = res.data.captchaId || ''
        form.captchaCode = ''
      }
    }
  } catch (e) {
    captchaEnabled.value = false
  }
}

async function handleLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  if (captchaEnabled.value && !form.captchaCode) {
    ElMessage.warning('请输入验证码')
    return
  }
  loading.value = true
  try {
    const res = await userStore.userLogin({
      username: form.username,
      password: form.password,
      captchaId: form.captchaId || undefined,
      captchaCode: form.captchaCode || undefined
    })
    if (res.code === 200) {
      ElMessage.success('登录成功')
      router.push('/user')
    } else {
      ElMessage.error(res.msg || '登录失败')
      if (captchaEnabled.value) loadCaptcha()
    }
  } catch (e) {
    if (captchaEnabled.value) loadCaptcha()
  } finally {
    loading.value = false
  }
}

onMounted(loadCaptcha)
</script>

<style scoped lang="scss">
.login-scene {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: var(--bg-void);
  position: relative;
  overflow: hidden;
}

.grid-bg {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(0, 240, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 240, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
  mask-image: radial-gradient(ellipse 60% 60% at 50% 50%, black 20%, transparent 70%);
}

.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.3;
  animation: float 8s ease-in-out infinite;
}

.orb-1 {
  width: 350px;
  height: 350px;
  background: var(--neon-purple);
  top: -80px;
  left: -80px;
  opacity: 0.12;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(15px, -15px) scale(1.03); }
}

.login-card {
  position: relative;
  width: 400px;
  background: var(--bg-card);
  backdrop-filter: blur(24px);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-xl);
  padding: 44px 36px 32px;
  z-index: 10;
}

.accent-line {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 50%;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--neon-purple), transparent);
}

.card-header {
  text-align: center;
  margin-bottom: 36px;

  .logo-mark {
    font-family: var(--font-mono);
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 10px;

    .logo-bracket { color: var(--neon-purple); }
    .logo-text { color: var(--text-primary); }
  }

  h1 {
    font-family: var(--font-display);
    font-size: 20px;
    font-weight: 700;
    color: var(--text-primary);
  }

  .subtitle {
    font-family: var(--font-mono);
    font-size: 11px;
    color: var(--text-dim);
    margin-top: 6px;
    letter-spacing: 0.12em;
    text-transform: uppercase;
  }
}

.captcha-row {
  display: flex;
  gap: 10px;
  width: 100%;

  .captcha-img {
    height: 40px;
    border-radius: var(--radius-sm);
    cursor: pointer;
    border: 1px solid var(--border-subtle);
  }
}

.login-btn {
  width: 100%;
  height: 46px;
  font-family: var(--font-display);
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.08em;
  border-radius: var(--radius-md) !important;
}

.card-footer {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid var(--border-dim);

  .footer-link {
    font-family: var(--font-body);
    font-size: 13px;
    color: var(--text-dim);
    transition: color 0.2s ease;

    &:hover {
      color: var(--neon-purple);
    }
  }
}
</style>
