import request from './request'

// ========== 认证 ==========
export function adminLogin(username, password) {
  return request.post('/api/admin/login', { username, password })
}

export function getAdminInfo() {
  return request.get('/api/admin/info')
}

// ========== 应用管理 ==========
export function getAppList(params) {
  return request.get('/api/admin/app/list', { params })
}

export function getApp(id) {
  return request.get(`/api/admin/app/${id}`)
}

export function createApp(data) {
  return request.post('/api/admin/app', data)
}

export function updateApp(id, data) {
  return request.put(`/api/admin/app/${id}`, data)
}

export function deleteApp(id) {
  return request.delete(`/api/admin/app/${id}`)
}

export function toggleApp(id, field, value) {
  return request.post(`/api/admin/app/${id}/toggle`, { field, value })
}

export function regenAppKey(id) {
  return request.post(`/api/admin/app/${id}/regenkey`)
}

export function batchApp(action, ids) {
  return request.post('/api/admin/app/batch', { action, ids })
}

// ========== 卡密管理 ==========
export function getKmList(params) {
  return request.get('/api/admin/km/list', { params })
}

export function generateKm(data) {
  return request.post('/api/admin/km/generate', data)
}

export function deleteKm(id) {
  return request.delete(`/api/admin/km/${id}`)
}

export function toggleKm(id, value) {
  return request.post(`/api/admin/km/${id}/toggle`, { value })
}

export function unbindKm(id) {
  return request.post(`/api/admin/km/${id}/unbind`)
}

export function batchKm(action, ids) {
  return request.post('/api/admin/km/batch', { action, ids })
}

export function cleanKm(data) {
  return request.post('/api/admin/km/clean', data)
}

// ========== 文件管理 ==========
export function getFileList(params) {
  return request.get('/api/admin/file/list', { params })
}

export function createFile(data) {
  return request.post('/api/admin/file', data)
}

export function updateFile(id, data) {
  return request.put(`/api/admin/file/${id}`, data)
}

export function deleteFile(id) {
  return request.delete(`/api/admin/file/${id}`)
}

export function batchFile(action, ids) {
  return request.post('/api/admin/file/batch', { action, ids })
}

// ========== 系统设置 ==========
export function getSettings() {
  return request.get('/api/admin/setting')
}

export function saveSettings(data) {
  return request.post('/api/admin/setting', data)
}

export function refreshCache() {
  return request.post('/api/admin/setting/refresh-cache')
}

export function changePassword(data) {
  return request.post('/api/admin/setting/change-password', data)
}
