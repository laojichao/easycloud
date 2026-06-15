<template>
  <div class="register-scene">
    <div class="grid-bg"></div>
    <div class="glow-orb orb-1"></div>

    <div class="register-card animate-in">
      <div class="accent-line"></div>

      <div class="card-header">
        <div class="logo-mark">
          <span class="logo-bracket">&lt;</span>
          <span class="logo-text">EC</span>
          <span class="logo-bracket">/&gt;</span>
        </div>
        <h1>注册账号</h1>
        <p class="subtitle">EasyCloud 用户注册</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" size="large" @submit.prevent="handleRegister">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码（至少6位）" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="qq">
          <el-input v-model="form.qq" placeholder="QQ 号" prefix-icon="ChatDotRound" />
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="邮箱（可选）" prefix-icon="Message" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="register-btn" :loading="loading" @click="handleRegister">注 册</el-button>
        </el-form-item>
      </el-form>

      <div class="card-footer">
        <router-link to="/" class="footer-link">返回首页</router-link>
        <router-link to="/login" class="footer-link">已有账号？去登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 用户注册页面
 *
 * 注册流程：
 * 1. 用户填写用户名、密码、确认密码、QQ号、邮箱（可选）
 * 2. 表单验证（el-form rules）：
 *    - 用户名：必填，3-20位
 *    - 密码：必填，至少6位
 *    - 确认密码：必填，必须与密码一致（自定义校验器 validateConfirmPassword）
 *    - QQ号：必填，5-15位纯数字
 *    - 邮箱：可选，格式校验
 * 3. 调用 POST /api/user/register 提交注册
 * 4. 成功后跳转到登录页 /login
 * 5. 失败则显示错误提示
 *
 * 对应后端端点：POST /api/user/register
 */
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userRegister } from '@/api/admin'

const router = useRouter()

/** 表单引用，用于触发表单验证 */
const formRef = ref(null)

/** 注册按钮加载状态 */
const loading = ref(false)

/**
 * 注册表单数据
 * @property {string} username - 用户名（3-20位）
 * @property {string} password - 密码（至少6位）
 * @property {string} confirmPassword - 确认密码（需与密码一致）
 * @property {string} qq - QQ 号（5-15位纯数字）
 * @property {string} email - 邮箱（可选）
 */
const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  qq: '',
  email: ''
})

/**
 * 确认密码自定义校验器
 * 验证两次输入的密码是否一致
 * @param {Object} rule - 校验规则
 * @param {string} value - 当前字段值
 * @param {Function} callback - 校验回调（无错误传空，有错误传 Error）
 */
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

/**
 * 表单验证规则
 * - 用户名：必填 + 长度 3-20 位
 * - 密码：必填 + 至少 6 位
 * - 确认密码：必填 + 与密码一致（自定义校验器）
 * - QQ 号：必填 + 5-15 位纯数字正则
 * - 邮箱：可选，格式校验
 */
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度 3-20 位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  qq: [
    { required: true, message: '请输入 QQ 号', trigger: 'blur' },
    { pattern: /^\d{5,15}$/, message: '请输入正确的 QQ 号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

/**
 * 处理用户注册
 * 验证表单 -> 调用注册 API -> 成功跳转登录页 / 失败提示错误
 */
async function handleRegister() {
  if (!formRef.value) return
  // 触发 el-form 全字段验证
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await userRegister({
      username: form.username,
      password: form.password,
      qq: form.qq,
      email: form.email
    })
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.msg || '注册失败')
    }
  } catch (e) {
    ElMessage.error(e.message || '注册失败，请检查网络')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.register-scene {
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
  background: var(--neon-cyan);
  top: -80px;
  right: -80px;
  opacity: 0.12;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(15px, -15px) scale(1.03); }
}

.register-card {
  position: relative;
  width: 420px;
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
  background: linear-gradient(90deg, transparent, var(--neon-cyan), transparent);
}

.card-header {
  text-align: center;
  margin-bottom: 32px;

  .logo-mark {
    font-family: var(--font-mono);
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 10px;

    .logo-bracket { color: var(--neon-cyan); }
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

.register-btn {
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
      color: var(--neon-cyan);
    }
  }
}
</style>
