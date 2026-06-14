import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { adminLogin } from '../api/admin'
import router from '../router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const username = ref(localStorage.getItem('admin_username') || '')

  const isLoggedIn = computed(() => !!token.value)

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

  function logout() {
    token.value = ''
    username.value = ''
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_username')
    router.push('/admin/login')
  }

  return {
    token,
    username,
    isLoggedIn,
    login,
    logout,
  }
})
