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

export function uploadAppImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/api/admin/app/upload-image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function updateAppSecurity(id, data) {
  return request.put(`/api/admin/app/${id}/security`, data)
}

export function updateAppAuth(id, data) {
  return request.put(`/api/admin/app/${id}/auth`, data)
}

export function updateAppInfo(id, data) {
  return request.put(`/api/admin/app/${id}/info`, data)
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

export function batchKmWithParams(data) {
  return request.post('/api/admin/km/batch', data)
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

export function toggleFile(id) {
  return request.post(`/api/admin/file/${id}/toggle`)
}

export function batchFile(action, ids) {
  return request.post('/api/admin/file/batch', { action, ids })
}

// ========== 系统设置 ==========
export function getStats() {
  return request.get('/api/admin/stats')
}

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

export function changeAccount(data) {
  return request.post('/api/admin/setting/change-account', data)
}

// ========== 用户管理 ==========
export function getUserList(params) {
  return request.get('/api/admin/users', { params })
}

export function getUser(uid) {
  return request.get('/api/admin/users/' + uid)
}

export function createUser(data) {
  return request.post('/api/admin/users', data)
}

export function updateUser(uid, data) {
  return request.put('/api/admin/users/' + uid, data)
}

export function deleteUser(uid) {
  return request.delete('/api/admin/users/' + uid)
}

export function adjustUserRmb(uid, amount) {
  return request.post('/api/admin/users/' + uid + '/rmb', { amount })
}

// ========== 工单管理 ==========
export function getWorkOrderList(params) {
  return request.get('/api/admin/workorders', { params })
}

export function replyWorkOrder(id, reply) {
  return request.post('/api/admin/workorders/' + id + '/reply', { reply })
}

export function closeWorkOrder(id) {
  return request.post('/api/admin/workorders/' + id + '/close')
}

// ========== 提现审核 ==========
export function getTixianList(params) {
  return request.get('/api/admin/tixian', { params })
}

export function approveTixian(id, realmoney) {
  return request.post('/api/admin/tixian/' + id + '/approve', { realmoney })
}

export function rejectTixian(id) {
  return request.post('/api/admin/tixian/' + id + '/reject')
}

// ========== 支付订单 ==========
export function getPayOrderList(params) {
  return request.get('/api/admin/pay/orders', { params })
}

export function getPayOrderDetail(orderNo) {
  return request.get('/api/admin/pay/orders/' + orderNo)
}

export function refundPayOrder(orderNo) {
  return request.post('/api/admin/pay/refund/' + orderNo)
}

// ========== 系统设置扩展 ==========
export function getMessages(params) {
  return request.get('/api/admin/setting/messages', { params })
}

export function createMessage(data) {
  return request.post('/api/admin/setting/messages', data)
}

export function mailTest(to) {
  return request.post('/api/admin/setting/mail-test', { to })
}

export function dbOptim() {
  return request.post('/api/admin/setting/db-optim')
}

export function dbRepair() {
  return request.post('/api/admin/setting/db-repair')
}

export function getApiKey() {
  return request.get('/api/admin/setting/api-key')
}

export function generateApiKey() {
  return request.post('/api/admin/setting/api-key')
}

export function getApiIp() {
  return request.get('/api/admin/setting/api-ip')
}

export function saveApiIp(data) {
  return request.post('/api/admin/setting/api-ip', { data })
}

export function getSites(params) {
  return request.get('/api/admin/setting/sites', { params })
}

export function updateSiteEndtime(id, num) {
  return request.post('/api/admin/setting/sites/' + id + '/endtime', { num })
}

export function getTransferConfig() {
  return request.get('/api/admin/setting/transfer-config')
}

export function saveTransferConfig(data) {
  return request.post('/api/admin/setting/transfer-config', data)
}

export function doTransfer(id) {
  return request.post('/api/admin/setting/transfer', { id })
}

export function getApiJk(proid) {
  return request.get('/api/admin/setting/api-jk', { params: { proid } })
}

// ========== 统计扩展 ==========
export function getCheckinStats() {
  return request.get('/api/admin/stats/checkin-stats')
}

export function getPendingCounts() {
  return request.get('/api/admin/stats/pending-counts')
}
