import request from './request'

// ========== 用户认证 ==========
export function userRegister(data) {
  return request.post('/api/user/register', data)
}

export function userLogin(data) {
  return request.post('/api/user/login', data)
}

// ========== 验证码 ==========
export function getCaptcha() {
  return request.get('/api/captcha')
}

// ========== 用户中心 ==========
export function getUserInfo() {
  return request.get('/api/user/info')
}

export function doCheckin() {
  return request.get('/api/user/checkin')
}

export function getCheckinList(params) {
  return request.get('/api/user/checkin/list', { params })
}

export function getPointList(params) {
  return request.get('/api/user/point/list', { params })
}

export function getInviteList(params) {
  return request.get('/api/user/invite/list', { params })
}

// ========== 提现 ==========
export function applyTixian(data) {
  return request.post('/api/user/tixian', data)
}

export function getTixianList(params) {
  return request.get('/api/user/tixian/list', { params })
}

// ========== 工单 ==========
export function createWorkOrder(data) {
  return request.post('/api/user/workorder', data)
}

export function getWorkOrderList(params) {
  return request.get('/api/user/workorder/list', { params })
}

// ========== 充值支付 ==========
export function createPayOrder(data) {
  return request.post('/api/pay/create', data)
}

export function getPayStatus(orderNo) {
  return request.get('/api/pay/status/' + orderNo)
}
