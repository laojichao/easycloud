/**
 * API 接口模块
 *
 * 包含所有前后端交互的 API 函数，按功能分为以下几类：
 * - 管理员认证：登录、获取管理员信息
 * - 应用管理：CRUD、状态切换、密钥重置、批量操作、图片上传、安全/认证/信息配置
 * - 卡密管理：列表、生成、删除、启停、解绑、批量、清理
 * - 文件管理：CRUD、状态切换、批量操作
 * - 系统设置：统计、设置读写、缓存刷新、密码修改、账号修改
 * - 用户端：注册、登录、签到、工单、邀请、积分、提现
 */
import request from './request'

// ==================== 管理员认证 ====================

/**
 * 管理员登录
 * @param {string} username - 管理员用户名
 * @param {string} password - 管理员密码
 * @returns {Promise<Object>} 返回 { code, data: { token, username } }
 * @endpoint POST /api/admin/login
 */
export function adminLogin(username, password) {
  return request.post('/api/admin/login', { username, password })
}

/**
 * 获取当前管理员信息
 * @returns {Promise<Object>} 管理员信息
 * @endpoint GET /api/admin/info
 */
export function getAdminInfo() {
  return request.get('/api/admin/info')
}

// ==================== 应用管理 ====================

/**
 * 获取应用列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} [params.page] - 页码
 * @param {number} [params.size] - 每页条数
 * @returns {Promise<Object>} 返回 { code, data: { records, total } }
 * @endpoint GET /api/admin/app/list
 */
export function getAppList(params) {
  return request.get('/api/admin/app/list', { params })
}

/**
 * 获取单个应用详情
 * @param {number|string} id - 应用 ID
 * @returns {Promise<Object>} 应用详情数据
 * @endpoint GET /api/admin/app/{id}
 */
export function getApp(id) {
  return request.get(`/api/admin/app/${id}`)
}

/**
 * 创建新应用
 * @param {Object} data - 应用数据
 * @param {string} data.name - 应用名称
 * @param {string} [data.version] - 版本号
 * @param {string} [data.versionInfo] - 版本说明
 * @param {string} [data.appGg] - 应用公告
 * @param {string} [data.appUpdateUrl] - 更新地址
 * @param {number} [data.miType] - 加密类型（0明文/1RC4-GBK/2Base64/3RC4原始/4AES）
 * @param {string} [data.rc4Key] - 加密密钥
 * @returns {Promise<Object>} 创建结果
 * @endpoint POST /api/admin/app
 */
export function createApp(data) {
  return request.post('/api/admin/app', data)
}

/**
 * 更新应用信息
 * @param {number|string} id - 应用 ID
 * @param {Object} data - 更新的应用数据（字段同 createApp）
 * @returns {Promise<Object>} 更新结果
 * @endpoint PUT /api/admin/app/{id}
 */
export function updateApp(id, data) {
  return request.put(`/api/admin/app/${id}`, data)
}

/**
 * 删除应用
 * @param {number|string} id - 应用 ID
 * @returns {Promise<Object>} 删除结果
 * @endpoint DELETE /api/admin/app/{id}
 */
export function deleteApp(id) {
  return request.delete(`/api/admin/app/${id}`)
}

/**
 * 切换应用字段状态（如启停用、付费/免费模式切换）
 * @param {number|string} id - 应用 ID
 * @param {string} field - 要切换的字段名（如 'active', 'switch'）
 * @param {*} value - 目标值（如 'y'/'n'）
 * @returns {Promise<Object>} 操作结果
 * @endpoint POST /api/admin/app/{id}/toggle
 */
export function toggleApp(id, field, value) {
  return request.post(`/api/admin/app/${id}/toggle`, { field, value })
}

/**
 * 重新生成应用密钥
 * @param {number|string} id - 应用 ID
 * @returns {Promise<Object>} 返回新密钥信息
 * @endpoint POST /api/admin/app/{id}/regenkey
 */
export function regenAppKey(id) {
  return request.post(`/api/admin/app/${id}/regenkey`)
}

/**
 * 批量操作应用
 * @param {string} action - 操作类型（enable/disable/delete）
 * @param {number[]} ids - 应用 ID 数组
 * @returns {Promise<Object>} 批量操作结果
 * @endpoint POST /api/admin/app/batch
 */
export function batchApp(action, ids) {
  return request.post('/api/admin/app/batch', { action, ids })
}

/**
 * 上传应用图片
 * @param {File} file - 图片文件对象
 * @returns {Promise<Object>} 返回 { code, data: { url } }
 * @endpoint POST /api/admin/app/upload-image
 */
export function uploadAppImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/api/admin/app/upload-image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/**
 * 更新应用安全配置（如签名校验、IP 白名单等）
 * @param {number|string} id - 应用 ID
 * @param {Object} data - 安全配置数据
 * @returns {Promise<Object>} 更新结果
 * @endpoint PUT /api/admin/app/{id}/security
 */
export function updateAppSecurity(id, data) {
  return request.put(`/api/admin/app/${id}/security`, data)
}

/**
 * 更新应用认证配置（如设备绑定策略、解绑次数限制等）
 * @param {number|string} id - 应用 ID
 * @param {Object} data - 认证配置数据
 * @returns {Promise<Object>} 更新结果
 * @endpoint PUT /api/admin/app/{id}/auth
 */
export function updateAppAuth(id, data) {
  return request.put(`/api/admin/app/${id}/auth`, data)
}

/**
 * 更新应用基本信息（名称、版本、公告等）
 * @param {number|string} id - 应用 ID
 * @param {Object} data - 基本信息数据
 * @returns {Promise<Object>} 更新结果
 * @endpoint PUT /api/admin/app/{id}/info
 */
export function updateAppInfo(id, data) {
  return request.put(`/api/admin/app/${id}/info`, data)
}

// ==================== 卡密管理 ====================

/**
 * 获取卡密列表（支持分页和多条件筛选）
 * @param {Object} params - 查询参数
 * @param {number} [params.page] - 页码
 * @param {number} [params.size] - 每页条数
 * @param {string} [params.appid] - 应用 ID 筛选
 * @param {string} [params.state] - 状态筛选（y启用/n禁用）
 * @param {string} [params.type] - 类型筛选（code时长卡/single次数卡）
 * @param {string} [params.useStatus] - 使用状态筛选（unused/used/expired）
 * @returns {Promise<Object>} 返回 { code, data: { records, total } }
 * @endpoint GET /api/admin/km/list
 */
export function getKmList(params) {
  return request.get('/api/admin/km/list', { params })
}

/**
 * 批量生成卡密
 * @param {Object} data - 生成参数
 * @param {number} data.appid - 目标应用 ID
 * @param {string} data.type - 卡密类型（code时长卡/single次数卡）
 * @param {string} [data.km_time] - 时长类型（hour/day/week/month/season/year/longuse）
 * @param {number} [data.amount] - 次数卡的使用次数
 * @param {number} [data.count] - 生成数量
 * @param {number} [data.length] - 卡密长度（8-64）
 * @param {string} [data.prefix] - 卡密前缀
 * @param {number} [data.structure] - 字符结构（1混合/2大写+数字/3小写+数字/4小写/5大写/6纯数字/7大小写）
 * @returns {Promise<Object>} 返回 { code, data: { count } }
 * @endpoint POST /api/admin/km/generate
 */
export function generateKm(data) {
  return request.post('/api/admin/km/generate', data)
}

/**
 * 删除单条卡密
 * @param {number|string} id - 卡密 ID
 * @returns {Promise<Object>} 删除结果
 * @endpoint DELETE /api/admin/km/{id}
 */
export function deleteKm(id) {
  return request.delete(`/api/admin/km/${id}`)
}

/**
 * 切换卡密启用/禁用状态
 * @param {number|string} id - 卡密 ID
 * @param {string} value - 目标状态（'y'启用 / 'n'禁用）
 * @returns {Promise<Object>} 操作结果
 * @endpoint POST /api/admin/km/{id}/toggle
 */
export function toggleKm(id, value) {
  return request.post(`/api/admin/km/${id}/toggle`, { value })
}

/**
 * 解绑卡密（清除卡密与设备的绑定关系）
 * @param {number|string} id - 卡密 ID
 * @returns {Promise<Object>} 解绑结果
 * @endpoint POST /api/admin/km/{id}/unbind
 */
export function unbindKm(id) {
  return request.post(`/api/admin/km/${id}/unbind`)
}

/**
 * 批量操作卡密（启用/禁用/删除/解绑/加减时长）
 * @param {string} action - 操作类型
 * @param {number[]} ids - 卡密 ID 数组
 * @returns {Promise<Object>} 批量操作结果
 * @endpoint POST /api/admin/km/batch
 */
export function batchKm(action, ids) {
  return request.post('/api/admin/km/batch', { action, ids })
}

/**
 * 批量操作卡密（带额外参数，如加减时长的时长值）
 * @param {Object} data - 包含 action、ids 和其他参数的完整数据
 * @returns {Promise<Object>} 批量操作结果
 * @endpoint POST /api/admin/km/batch
 */
export function batchKmWithParams(data) {
  return request.post('/api/admin/km/batch', data)
}

/**
 * 清理卡密（按使用状态批量清理）
 * @param {Object} data - 清理条件
 * @param {string} [data.appid] - 应用 ID
 * @param {string} [data.useStatus] - 要清理的使用状态
 * @returns {Promise<Object>} 清理结果
 * @endpoint POST /api/admin/km/clean
 */
export function cleanKm(data) {
  return request.post('/api/admin/km/clean', data)
}

// ==================== 文件管理 ====================

/**
 * 获取文件列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} [params.page] - 页码
 * @param {number} [params.size] - 每页条数
 * @returns {Promise<Object>} 返回 { code, data: { records, total } }
 * @endpoint GET /api/admin/file/list
 */
export function getFileList(params) {
  return request.get('/api/admin/file/list', { params })
}

/**
 * 创建文件记录
 * @param {Object} data - 文件数据
 * @param {number} data.appid - 关联的应用 ID
 * @param {string} data.fileUrl - 文件下载链接
 * @param {string} data.type - 链接类型（direct直链/lanzou蓝奏云）
 * @param {string} [data.lanzouPass] - 蓝奏云分享密码
 * @param {string} [data.note] - 备注说明
 * @returns {Promise<Object>} 创建结果
 * @endpoint POST /api/admin/file
 */
export function createFile(data) {
  return request.post('/api/admin/file', data)
}

/**
 * 更新文件记录
 * @param {number|string} id - 文件 ID
 * @param {Object} data - 更新的文件数据（字段同 createFile）
 * @returns {Promise<Object>} 更新结果
 * @endpoint PUT /api/admin/file/{id}
 */
export function updateFile(id, data) {
  return request.put(`/api/admin/file/${id}`, data)
}

/**
 * 删除文件记录
 * @param {number|string} id - 文件 ID
 * @returns {Promise<Object>} 删除结果
 * @endpoint DELETE /api/admin/file/{id}
 */
export function deleteFile(id) {
  return request.delete(`/api/admin/file/${id}`)
}

/**
 * 切换文件启用/禁用状态
 * @param {number|string} id - 文件 ID
 * @returns {Promise<Object>} 操作结果
 * @endpoint POST /api/admin/file/{id}/toggle
 */
export function toggleFile(id) {
  return request.post(`/api/admin/file/${id}/toggle`)
}

/**
 * 批量操作文件（启用/禁用/删除）
 * @param {string} action - 操作类型
 * @param {number[]} ids - 文件 ID 数组
 * @returns {Promise<Object>} 批量操作结果
 * @endpoint POST /api/admin/file/batch
 */
export function batchFile(action, ids) {
  return request.post('/api/admin/file/batch', { action, ids })
}

// ==================== 系统设置 ====================

/**
 * 获取系统统计数据（仪表盘展示用）
 * @returns {Promise<Object>} 返回 { code, data: { appCount, kmCount, fileCount, todayKm, todayLogs, ... } }
 * @endpoint GET /api/admin/stats
 */
export function getStats() {
  return request.get('/api/admin/stats')
}

/**
 * 获取系统设置
 * @returns {Promise<Object>} 返回 { code, data: { sitename, siteurl, kfqq, beian, ... } }
 * @endpoint GET /api/admin/setting
 */
export function getSettings() {
  return request.get('/api/admin/setting')
}

/**
 * 保存系统设置
 * @param {Object} data - 设置数据（sitename, siteurl, kfqq, beian 等）
 * @returns {Promise<Object>} 保存结果
 * @endpoint POST /api/admin/setting
 */
export function saveSettings(data) {
  return request.post('/api/admin/setting', data)
}

/**
 * 刷新系统缓存（从数据库重新加载配置到 Redis）
 * @returns {Promise<Object>} 刷新结果
 * @endpoint POST /api/admin/setting/refresh-cache
 */
export function refreshCache() {
  return request.post('/api/admin/setting/refresh-cache')
}

/**
 * 修改管理员密码
 * @param {Object} data - 密码数据
 * @param {string} data.old_password - 旧密码
 * @param {string} data.new_password - 新密码
 * @returns {Promise<Object>} 修改结果
 * @endpoint POST /api/admin/setting/change-password
 */
export function changePassword(data) {
  return request.post('/api/admin/setting/change-password', data)
}

/**
 * 修改管理员账号名
 * @param {Object} data - 账号数据
 * @returns {Promise<Object>} 修改结果
 * @endpoint POST /api/admin/setting/change-account
 */
export function changeAccount(data) {
  return request.post('/api/admin/setting/change-account', data)
}

// ==================== 用户端 API ====================

/**
 * 用户注册
 * @param {Object} data - 注册信息
 * @param {string} data.username - 用户名（3-20位）
 * @param {string} data.password - 密码（至少6位）
 * @param {string} data.qq - QQ 号
 * @param {string} [data.email] - 邮箱（可选）
 * @returns {Promise<Object>} 注册结果
 * @endpoint POST /api/user/register
 */
export function userRegister(data) {
  return request.post('/api/user/register', data)
}

/**
 * 用户登录
 * @param {Object} data - 登录信息
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @returns {Promise<Object>} 返回 { code, data: { token, username } }
 * @endpoint POST /api/user/login
 */
export function userLogin(data) {
  return request.post('/api/user/login', data)
}

/**
 * 用户签到（每日一次，获取积分奖励）
 * @returns {Promise<Object>} 返回 { code, data: { points } }
 * @endpoint GET /api/user/checkin
 */
export function userCheckin() {
  return request.get('/api/user/checkin')
}

/**
 * 获取用户签到记录列表
 * @param {Object} params - 查询参数
 * @param {number} [params.page] - 页码
 * @param {number} [params.size] - 每页条数
 * @returns {Promise<Object>} 返回 { code, data: { records } }
 * @endpoint GET /api/user/checkin/list
 */
export function userCheckinList(params) {
  return request.get('/api/user/checkin/list', { params })
}

/**
 * 创建工单（用户提交问题反馈）
 * @param {Object} data - 工单数据
 * @param {string} data.title - 工单标题
 * @param {string} data.content - 工单详细内容
 * @returns {Promise<Object>} 创建结果
 * @endpoint POST /api/user/workorder
 */
export function createWorkOrder(data) {
  return request.post('/api/user/workorder', data)
}

/**
 * 获取用户工单列表
 * @param {Object} params - 查询参数
 * @param {number} [params.page] - 页码
 * @param {number} [params.size] - 每页条数
 * @returns {Promise<Object>} 返回 { code, data: { records } }
 * @endpoint GET /api/user/workorder/list
 */
export function userWorkOrderList(params) {
  return request.get('/api/user/workorder/list', { params })
}

/**
 * 获取用户邀请记录列表
 * @param {Object} params - 查询参数
 * @param {number} [params.page] - 页码
 * @param {number} [params.size] - 每页条数
 * @returns {Promise<Object>} 返回 { code, data: { records } }
 * @endpoint GET /api/user/invite/list
 */
export function userInviteList(params) {
  return request.get('/api/user/invite/list', { params })
}

/**
 * 获取用户积分变动记录列表
 * @param {Object} params - 查询参数
 * @param {number} [params.page] - 页码
 * @param {number} [params.size] - 每页条数
 * @returns {Promise<Object>} 返回 { code, data: { records } }
 * @endpoint GET /api/user/point/list
 */
export function userPointList(params) {
  return request.get('/api/user/point/list', { params })
}

/**
 * 申请提现（使用余额兑换现金）
 * @param {Object} data - 提现信息
 * @param {number} data.money - 提现金额
 * @param {string} data.account - 收款账号（支付宝/微信）
 * @param {string} [data.name] - 收款人姓名
 * @param {string} [data.type] - 提现类型（如 alipay）
 * @returns {Promise<Object>} 申请结果
 * @endpoint POST /api/user/tixian
 */
export function applyTixian(data) {
  return request.post('/api/user/tixian', data)
}

/**
 * 获取用户提现记录列表
 * @param {Object} params - 查询参数
 * @param {number} [params.page] - 页码
 * @param {number} [params.size] - 每页条数
 * @returns {Promise<Object>} 返回 { code, data: { records } }
 * @endpoint GET /api/user/tixian/list
 */
export function userTixianList(params) {
  return request.get('/api/user/tixian/list', { params })
}

// ==================== 用户管理（管理员） ====================

/**
 * 获取用户列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} [params.page] - 页码
 * @param {number} [params.size] - 每页条数
 * @param {string} [params.keyword] - 搜索关键词
 * @returns {Promise<Object>} 返回 { code, data: { records, total } }
 * @endpoint GET /api/admin/users
 */
export function getUserList(params) {
  return request.get('/api/admin/users', { params })
}

/**
 * 获取单个用户详情
 * @param {number|string} uid - 用户 UID
 * @returns {Promise<Object>} 用户详情
 * @endpoint GET /api/admin/users/{uid}
 */
export function getUser(uid) {
  return request.get(`/api/admin/users/${uid}`)
}

/**
 * 创建用户
 * @param {Object} data - 用户数据
 * @param {string} data.user - 用户名
 * @param {string} data.pwd - 密码
 * @param {string} [data.qq] - QQ号
 * @param {string} [data.email] - 邮箱
 * @returns {Promise<Object>} 创建结果
 * @endpoint POST /api/admin/users
 */
export function createUser(data) {
  return request.post('/api/admin/users', data)
}

/**
 * 更新用户信息
 * @param {number|string} uid - 用户 UID
 * @param {Object} data - 更新数据
 * @returns {Promise<Object>} 更新结果
 * @endpoint PUT /api/admin/users/{uid}
 */
export function updateUser(uid, data) {
  return request.put(`/api/admin/users/${uid}`, data)
}

/**
 * 删除用户
 * @param {number|string} uid - 用户 UID
 * @returns {Promise<Object>} 删除结果
 * @endpoint DELETE /api/admin/users/{uid}
 */
export function deleteUser(uid) {
  return request.delete(`/api/admin/users/${uid}`)
}

/**
 * 调整用户余额
 * @param {number|string} uid - 用户 UID
 * @param {Object} data - 包含 amount（正数增加，负数减少）
 * @returns {Promise<Object>} 操作结果
 * @endpoint POST /api/admin/users/{uid}/rmb
 */
export function adjustUserRmb(uid, data) {
  return request.post(`/api/admin/users/${uid}/rmb`, data)
}

// ==================== 工单管理（管理员） ====================

/**
 * 获取工单列表（支持状态筛选）
 * @param {Object} params - 查询参数
 * @param {number} [params.page] - 页码
 * @param {number} [params.size] - 每页条数
 * @param {number} [params.status] - 状态筛选（0待处理/1已处理/2已关闭）
 * @returns {Promise<Object>} 返回 { code, data: { records, total } }
 * @endpoint GET /api/admin/workorders
 */
export function getAdminWorkOrderList(params) {
  return request.get('/api/admin/workorders', { params })
}

/**
 * 回复工单
 * @param {number|string} id - 工单 ID
 * @param {Object} data - 包含 reply（回复内容）
 * @returns {Promise<Object>} 操作结果
 * @endpoint POST /api/admin/workorders/{id}/reply
 */
export function replyWorkOrder(id, data) {
  return request.post(`/api/admin/workorders/${id}/reply`, data)
}

/**
 * 关闭工单
 * @param {number|string} id - 工单 ID
 * @returns {Promise<Object>} 操作结果
 * @endpoint POST /api/admin/workorders/{id}/close
 */
export function closeWorkOrder(id) {
  return request.post(`/api/admin/workorders/${id}/close`)
}

// ==================== 提现管理（管理员） ====================

/**
 * 获取提现列表（支持状态筛选）
 * @param {Object} params - 查询参数
 * @param {number} [params.page] - 页码
 * @param {number} [params.size] - 每页条数
 * @param {number} [params.status] - 状态筛选（0待处理/1已处理/2已拒绝）
 * @returns {Promise<Object>} 返回 { code, data: { records, total } }
 * @endpoint GET /api/admin/tixian
 */
export function getAdminTixianList(params) {
  return request.get('/api/admin/tixian', { params })
}

/**
 * 批准提现
 * @param {number|string} id - 提现 ID
 * @param {Object} data - 包含 realmoney（实际到账金额）
 * @returns {Promise<Object>} 操作结果
 * @endpoint POST /api/admin/tixian/{id}/approve
 */
export function approveTixian(id, data) {
  return request.post(`/api/admin/tixian/${id}/approve`, data)
}

/**
 * 拒绝提现
 * @param {number|string} id - 提现 ID
 * @returns {Promise<Object>} 操作结果（余额自动退回）
 * @endpoint POST /api/admin/tixian/{id}/reject
 */
export function rejectTixian(id) {
  return request.post(`/api/admin/tixian/${id}/reject`)
}

// ==================== 支付订单管理（管理员） ====================

/**
 * 获取支付订单列表
 * @param {Object} params - 查询参数
 * @param {number} [params.page] - 页码
 * @param {number} [params.size] - 每页条数
 * @param {string} [params.status] - 状态筛选（pending/paid/failed/refunded）
 * @returns {Promise<Object>} 返回 { code, data: { records, total } }
 * @endpoint GET /api/admin/pay/orders
 */
export function getPayOrderList(params) {
  return request.get('/api/admin/pay/orders', { params })
}

/**
 * 获取订单详情
 * @param {string} orderNo - 订单号
 * @returns {Promise<Object>} 订单详情
 * @endpoint GET /api/admin/pay/orders/{orderNo}
 */
export function getPayOrderDetail(orderNo) {
  return request.get(`/api/admin/pay/orders/${orderNo}`)
}

/**
 * 订单退款
 * @param {string} orderNo - 订单号
 * @returns {Promise<Object>} 退款结果
 * @endpoint POST /api/admin/pay/refund/{orderNo}
 */
export function refundPayOrder(orderNo) {
  return request.post(`/api/admin/pay/refund/${orderNo}`)
}
