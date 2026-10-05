import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '',
  timeout: 15000
})

// 管理端与用户端双 Token：按请求 URL 前缀选择
function isAdminUrl(url) {
  return typeof url === 'string' && url.indexOf('/api/admin') === 0
}

function pickToken(url) {
  return localStorage.getItem(isAdminUrl(url) ? 'admin_token' : 'user_token')
}

function clearSession(url) {
  if (isAdminUrl(url)) {
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_username')
    router.push('/admin/login')
    ElMessage.error('登录已过期，请重新登录')
  } else {
    localStorage.removeItem('user_token')
    localStorage.removeItem('user_username')
    localStorage.removeItem('user_role')
    router.push('/login')
    ElMessage.error('登录已过期，请重新登录')
  }
}

// 请求拦截器：添加 Authorization header
request.interceptors.request.use(
  (config) => {
    const token = pickToken(config.url)
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器：处理 401 跳转登录
request.interceptors.response.use(
  (response) => {
    const data = response.data
    // 后端拦截器返回 HTTP 200 但 body 中 code=401
    if (data && data.code === 401) {
      clearSession(response.config?.url)
      return Promise.reject(new Error('未授权'))
    }
    return data
  },
  (error) => {
    if (error.response) {
      if (error.response.status === 401) {
        clearSession(error.response.config?.url)
      } else {
        ElMessage.error(error.response.data?.message || error.response.data?.msg || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查连接')
    }
    return Promise.reject(error)
  }
)

export default request
