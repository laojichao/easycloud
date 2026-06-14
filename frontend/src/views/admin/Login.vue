<template>
  <div class="login-scene">
    <!-- Animated background grid -->
    <div class="grid-bg"></div>
    <div class="glow-orb orb-1"></div>
    <div class="glow-orb orb-2"></div>

    <div class="login-card animate-in">
      <!-- Top accent line -->
      <div class="accent-line"></div>

      <div class="card-header">
        <div class="logo-mark">
          <span class="logo-bracket">&lt;</span>
          <span class="logo-text">EC</span>
          <span class="logo-bracket">/&gt;</span>
        </div>
        <h1>EasyCloud</h1>
        <p class="subtitle">管理后台 · 身份验证</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" size="large" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名"
            prefix-icon="User"
            class="neon-input"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            show-password
            class="neon-input"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            <span v-if="!loading">进 入 系 统</span>
            <span v-else>验证中...</span>
          </el-button>
        </el-form-item>
      </el-form>

      <div class="card-footer">
        <span class="footer-text">v1.0.0 · Powered by Spring Boot</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await userStore.login(form.username, form.password)
    if (res.code === 200) {
      ElMessage.success({ message: '登录成功', duration: 1500 })
      router.push('/admin/dashboard')
    } else {
      ElMessage.error(res.msg || '登录失败')
    }
  } catch (e) {
    ElMessage.error('登录失败')
  } finally {
    loading.value = false
  }
}
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
  opacity: 0.4;
  animation: float 8s ease-in-out infinite;
}

.orb-1 {
  width: 400px;
  height: 400px;
  background: var(--neon-cyan);
  top: -100px;
  right: -100px;
  opacity: 0.15;
}

.orb-2 {
  width: 300px;
  height: 300px;
  background: var(--neon-magenta);
  bottom: -80px;
  left: -80px;
  opacity: 0.12;
  animation-delay: -4s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(20px, -20px) scale(1.05); }
}

.login-card {
  position: relative;
  width: 420px;
  background: var(--bg-card);
  backdrop-filter: blur(24px);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-xl);
  padding: 48px 40px 36px;
  z-index: 10;
}

.accent-line {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60%;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--neon-cyan), transparent);
  border-radius: 1px;
}

.card-header {
  text-align: center;
  margin-bottom: 40px;

  .logo-mark {
    font-family: var(--font-mono);
    font-size: 28px;
    font-weight: 600;
    margin-bottom: 12px;
    letter-spacing: 0.05em;

    .logo-bracket {
      color: var(--neon-cyan);
    }
    .logo-text {
      color: var(--text-primary);
    }
  }

  h1 {
    font-family: var(--font-display);
    font-size: 22px;
    font-weight: 700;
    color: var(--text-primary);
    letter-spacing: 0.02em;
  }

  .subtitle {
    font-family: var(--font-mono);
    font-size: 12px;
    color: var(--text-dim);
    margin-top: 8px;
    letter-spacing: 0.15em;
    text-transform: uppercase;
  }
}

.login-btn {
  width: 100%;
  height: 48px;
  font-family: var(--font-display);
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.1em;
  border-radius: var(--radius-md) !important;
  position: relative;
  overflow: hidden;

  &::after {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(90deg, transparent, rgba(255,255,255,0.15), transparent);
    transform: translateX(-100%);
    transition: transform 0.6s;
  }

  &:hover::after {
    transform: translateX(100%);
  }
}

.card-footer {
  text-align: center;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--border-dim);

  .footer-text {
    font-family: var(--font-mono);
    font-size: 11px;
    color: var(--text-dim);
    letter-spacing: 0.05em;
  }
}
</style>
