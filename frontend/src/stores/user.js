import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { adminLogin } from '../api/admin'
import { userLogin as userLoginApi } from '../api/user'
import router from '../router'

export const useUserStore = defineStore('user', () => {
  // 管理端会话
  const token = ref(localStorage.getItem('admin_token') || '')
  const username = ref(localStorage.getItem('admin_username') || '')

  // 用户端会话
  const userToken = ref(localStorage.getItem('user_token') || '')
  const userUsername = ref(localStorage.getItem('user_username') || '')
  const userRole = ref(localStorage.getItem('user_role') || 'user')

  const isLoggedIn = computed(() => !!token.value)
  const isUserLoggedIn = computed(() => !!userToken.value)

  async function login(loginUsername, password) {
    const res = await adminLogin(loginUsername, password)
    if (res.code === 200) {
      token.value = res.data.token
      username.value = res.data.username
      localStorage.setItem('admin_token', res.data.token)
      localStorage.setItem('admin_username', res.data.username)
    }
    return res
  }

  async function userLogin(payload) {
    const res = await userLoginApi(payload)
    if (res.code === 200) {
      userToken.value = res.data.token
      userUsername.value = res.data.username
      userRole.value = res.data.role || 'user'
      localStorage.setItem('user_token', res.data.token)
      localStorage.setItem('user_username', res.data.username)
      localStorage.setItem('user_role', res.data.role || 'user')
    }
    return res
  }

  function logout() {
    token.value = ''
    username.value = ''
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_username')
    router.push('/admin/login')
  }

  function userLogout() {
    userToken.value = ''
    userUsername.value = ''
    userRole.value = 'user'
    localStorage.removeItem('user_token')
    localStorage.removeItem('user_username')
    localStorage.removeItem('user_role')
    router.push('/login')
  }

  return {
    token,
    username,
    isLoggedIn,
    login,
    logout,
    userToken,
    userUsername,
    userRole,
    isUserLoggedIn,
    userLogin,
    userLogout,
  }
})
