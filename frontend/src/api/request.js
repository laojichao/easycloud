/**
 * Axios 请求封装模块
 *
 * 功能：
 * - 创建统一的 Axios 实例，设置 baseURL 和超时时间
 * - 请求拦截器：自动从 localStorage 读取 token 并附加到请求头
 * - 响应拦截器：统一处理 401 认证过期、网络错误等异常情况
 *
 * Token 优先级：admin_token > user_token
 * 401 处理策略：
 *   - HTTP 200 但 body code=401 -> 跳转到用户登录页 /login
 *   - HTTP 状态码 401 -> 跳转到管理员登录页 /admin/login
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

/**
 * 创建 Axios 实例
 * @property {string} baseURL - API 基础路径（空字符串表示使用相对路径，由 nginx 代理）
 * @property {number} timeout - 请求超时时间（毫秒）
 */
const request = axios.create({
  baseURL: '',
  timeout: 15000
})

/**
 * 请求拦截器
 * 在每个请求发出前自动附加 Authorization 头
 * 优先使用 admin_token，其次 user_token
 */
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('admin_token') || localStorage.getItem('user_token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 * 统一处理响应数据和异常状态码
 *
 * 正常响应：直接返回 response.data（跳过 axios 默认包装）
 * 异常处理：
 *   - body.code === 401：后端业务层返回的未授权，清除 token 后跳转登录页
 *   - HTTP 401：HTTP 层面的未授权，清除 token 后跳转登录页
 *   - 其他 HTTP 错误：展示后端返回的错误信息
 *   - 网络错误：提示用户检查网络连接
 */
request.interceptors.response.use(
  (response) => {
    const data = response.data
    // 后端拦截器返回 HTTP 200 但 body 中 code=401
    if (data && data.code === 401) {
      clearAllTokens()
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
      return Promise.reject(new Error('未授权'))
    }
    return data
  },
  (error) => {
    if (error.response) {
      if (error.response.status === 401) {
        clearAllTokens()
        // 根据当前路径判断跳转到哪个登录页
        const currentPath = router.currentRoute.value.path
        if (currentPath.startsWith('/admin')) {
          router.push('/admin/login')
        } else {
          router.push('/login')
        }
        ElMessage.error('登录已过期，请重新登录')
      } else {
        ElMessage.error(error.response.data?.message || error.response.data?.msg || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查连接')
    }
    return Promise.reject(error)
  }
)

/**
 * 清除所有认证 token
 */
function clearAllTokens() {
  localStorage.removeItem('admin_token')
  localStorage.removeItem('admin_username')
  localStorage.removeItem('user_token')
  localStorage.removeItem('user_username')
  localStorage.removeItem('user_role')
}

export default request
