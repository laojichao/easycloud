/**
 * 用户状态管理 Store
 *
 * 使用 Pinia 管理应用的认证状态，支持管理员（admin）和普通用户（user）两种角色。
 *
 * 角色区别：
 * - admin：管理后台登录，token 存储在 admin_token，拥有应用/卡密/文件/设置的管理权限
 * - user：用户端登录，token 存储在 user_token，拥有签到/工单/邀请/积分/提现等用户功能
 *
 * 两种角色的 token 和用户名独立存储，互不干扰。
 * localStorage 持久化确保页面刷新后登录状态不丢失。
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { adminLogin, userLogin as userLoginApi } from '../api/admin'
import router from '../router'

export const useUserStore = defineStore('user', () => {
  // ==================== Admin 状态 ====================

  /** 管理员 JWT Token（从 localStorage 恢复） */
  const adminToken = ref(localStorage.getItem('admin_token') || '')

  /** 管理员用户名（从 localStorage 恢复） */
  const adminUsername = ref(localStorage.getItem('admin_username') || '')

  // ==================== User 状态 ====================

  /** 普通用户 JWT Token（从 localStorage 恢复） */
  const userToken = ref(localStorage.getItem('user_token') || '')

  /** 普通用户用户名（从 localStorage 恢复） */
  const userUsername = ref(localStorage.getItem('user_username') || '')

  /**
   * 当前活跃角色标识
   * @type {'admin' | 'user' | ''}
   * 用于判断当前是哪种角色登录状态
   */
  const userRole = ref(localStorage.getItem('user_role') || '')

  // ==================== 计算属性 ====================

  /** 是否已登录为管理员 */
  const isAdminLoggedIn = computed(() => !!adminToken.value)

  /** 是否已登录为普通用户 */
  const isUserLoggedIn = computed(() => !!userToken.value)

  /** 是否已登录（管理员或普通用户任一即可） */
  const isLoggedIn = computed(() => !!adminToken.value || !!userToken.value)

  // ==================== Actions ====================

  /**
   * 管理员登录
   * 调用管理员登录 API，成功后将 token 和用户名存入 state 和 localStorage
   *
   * @param {string} loginUsername - 管理员用户名
   * @param {string} password - 管理员密码
   * @returns {Promise<Object>} 登录结果（包含 code 和 data.token/data.username）
   */
  async function login(loginUsername, password) {
    const res = await adminLogin(loginUsername, password)
    if (res.code === 200) {
      adminToken.value = res.data.token
      adminUsername.value = res.data.username
      userRole.value = 'admin'
      localStorage.setItem('admin_token', res.data.token)
      localStorage.setItem('admin_username', res.data.username)
      localStorage.setItem('user_role', 'admin')
    }
    return res
  }

  /**
   * 普通用户登录
   * 调用用户登录 API，成功后将 token 和用户名存入 state 和 localStorage
   *
   * @param {string} loginUsername - 用户名
   * @param {string} password - 密码
   * @returns {Promise<Object>} 登录结果（包含 code 和 data.token/data.username）
   */
  async function userLogin(loginUsername, password) {
    const res = await userLoginApi({ username: loginUsername, password })
    if (res.code === 200) {
      userToken.value = res.data.token
      userUsername.value = res.data.username || loginUsername
      userRole.value = 'user'
      localStorage.setItem('user_token', res.data.token)
      localStorage.setItem('user_username', res.data.username || loginUsername)
      localStorage.setItem('user_role', 'user')
    }
    return res
  }

  /**
   * 管理员登出
   * 清除管理员相关的 state 和 localStorage，跳转到管理员登录页
   */
  function logout() {
    adminToken.value = ''
    adminUsername.value = ''
    userRole.value = ''
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_username')
    localStorage.removeItem('user_role')
    router.push('/admin/login')
  }

  /**
   * 普通用户登出
   * 清除用户相关的 state 和 localStorage，跳转到用户登录页
   */
  function userLogout() {
    userToken.value = ''
    userUsername.value = ''
    userRole.value = ''
    localStorage.removeItem('user_token')
    localStorage.removeItem('user_username')
    localStorage.removeItem('user_role')
    router.push('/login')
  }

  return {
    adminToken,
    adminUsername,
    userToken,
    userUsername,
    userRole,
    isAdminLoggedIn,
    isUserLoggedIn,
    isLoggedIn,
    login,
    userLogin,
    logout,
    userLogout,
  }
})
