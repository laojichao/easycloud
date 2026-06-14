# EasyCloud PHP → Java+Vue 迁移实施计划

> 日期: 2026-06-15
> 技术栈: Spring Boot 3 + MyBatis-Plus + Vue 3 + Element Plus
> 架构: Monorepo (backend/ + frontend/)

---

## 阶段 1: 项目脚手架搭建

### Task 1.1: 初始化 Spring Boot 后端项目
- 创建 `backend/pom.xml`，引入依赖：
  - spring-boot-starter-web
  - mybatis-plus-spring-boot3-starter
  - mysql-connector-j
  - spring-boot-starter-data-redis
  - spring-boot-starter-security
  - jjwt (JWT)
  - knife4j-openapi3-jakarta-spring-boot-starter
  - lombok
  - spring-boot-starter-test
- 创建 `backend/src/main/resources/application.yml`
- 创建 `EasyCloudApplication.java` 启动类
- **验证**: `mvn spring-boot:run` 启动成功，访问 `/doc.html` 看到 Swagger

### Task 1.2: 初始化 Vue 3 前端项目
- 在 `frontend/` 下执行 `npm create vite@latest . -- --template vue`
- 安装依赖：vue-router, pinia, axios, element-plus, @element-plus/icons-vue
- 配置 `vite.config.js` 代理 `/api` 到后端 8080
- 创建基础目录结构：`views/`, `api/`, `router/`, `stores/`, `components/`
- **验证**: `npm run dev` 启动成功，浏览器看到 Vue 欢迎页

---

## 阶段 2: 数据库实体映射

### Task 2.1: 创建数据库配置 + 实体基类
- `application.yml` 配置 MySQL 连接（读取原 `config.php` 的数据库信息）
- 创建 `BaseEntity.java`：id, date 字段
- 配置 MyBatis-Plus：表名前缀 `yixi_`，逻辑删除，自动填充
- **验证**: 启动无报错，数据库连接成功

### Task 2.2: 创建 yixi_apps 实体 + Mapper
- `App.java` 实体类，映射 `yixi_apps` 全部字段
- `AppMapper.java` 继承 BaseMapper
- 编写测试：查询一条应用数据
- **验证**: 测试通过，能读取 yixi_apps 数据

### Task 2.3: 创建 yixi_appkm 实体 + Mapper
- `AppKm.java` 实体类，映射 `yixi_appkm` 全部字段
- `AppKmMapper.java` 继承 BaseMapper
- 编写测试：查询卡密数据
- **验证**: 测试通过

### Task 2.4: 创建其余实体 + Mapper
- `AppFile.java` → `yixi_appfile`
- `Config.java` → `yixi_config`
- `Cache.java` → `yixi_cache`
- `Log.java` → `yixi_log`
- `Sig.java` → `yixi_sig`
- 对应 Mapper 接口
- **验证**: 所有 Mapper 注册成功，启动无报错

---

## 阶段 3: 通用组件层

### Task 3.1: 统一响应封装
- `Result.java`：code, msg, data 字段
- `Result.ok()` / `Result.fail()` 静态方法
- 编写测试验证 JSON 序列化格式
- **验证**: 测试通过

### Task 3.2: 配置服务 (ConfigService)
- 从 `yixi_config` 表读取配置，缓存到 Redis
- `getSetting(key)` → 先查 Redis，miss 则查 DB
- `saveSetting(key, value)` → 写 DB + 更新 Redis
- 启动时预热全部配置到 Redis
- 编写测试
- **验证**: 测试通过，Redis 中有缓存

### Task 3.3: API 加解密工具类 - RC4
- `ApiCrypto.java`：实现 `mi_rc4()` 精确复刻
  - GBK 编码转换
  - 自定义 RC4 算法（与 PHP `mi_rc4()` 字节一致）
  - hex 编码/解码
- 编写测试：用已知的 key + plaintext，对比 PHP 输出的 ciphertext
- **验证**: Java 加密结果与 PHP 完全一致

### Task 3.4: API 加解密工具类 - AES + Base64 + RSA
- AES-128-CBC：key = `'y' + substr(rc4_key,0,13) + 'gg'`，IV = `0102030405060708`
- Base64 编解码
- RSA 私钥解密
- 编写测试：每种加密方式对比 PHP 输出
- **验证**: 所有加密类型测试通过

### Task 3.5: API 签名验证工具类
- `ApiSignature.java`：
  - `verify(params, sign, appkey)` → `md5(sorted_params + appkey)`
  - `generate(data, appkey, value)` → `md5(time + appkey + value)`
  - 排除 key 列表：sign, app, api, value, PHPSESSID, sec_defend, sidenav-state
- 编写测试
- **验证**: 测试通过，签名结果与 PHP 一致

### Task 3.6: 管理员认证 - JWT
- `JwtUtil.java`：生成/解析 JWT Token
- `AdminAuthInterceptor.java`：拦截 `/admin/**` 请求，验证 JWT
- 密码加密：保持 `md5(pwd + '!@#%!s!0')` 兼容原数据库
- 编写测试：token 生成 + 验证
- **验证**: 测试通过

---

## 阶段 4: 对外 API 接口（兼容层）

### Task 4.1: API 路由入口
- `ApiController.java`：`POST /api/legacy?app={appid}&api={apiName}&other={action}`
- 解析参数：app, api, sign, data, value
- 加载 App 配置
- 加密类型分发（0-5）
- 签名验证
- 时间漂移检查
- 路由到具体 handler
- **验证**: 发送明文请求，返回正确 JSON

### Task 4.2: API - ini（获取应用配置）
- `ApiIniHandler.java`
- 返回：version, version_info, app_update_show, app_update_url, app_update_must, api_total
- 处理蓝奏云 URL 解析
- 编写测试
- **验证**: 测试通过

### Task 4.3: API - notice（获取公告）
- `ApiNoticeHandler.java`
- 返回：`{app_gg}`
- 编写测试
- **验证**: 测试通过

### Task 4.4: API - getfile（获取文件链接）
- `ApiGetfileHandler.java`
- 查询 `yixi_appfile`，处理蓝奏云链接
- 返回文件列表 JSON
- 编写测试
- **验证**: 测试通过

### Task 4.5: API - kmlogon（卡密登录）⭐ 核心
- `ApiKmlogonHandler.java`
- 免费模式判断
- 卡密查找 + 校验（存在、启用、设备码、IP）
- 设备绑定
- 时长卡逻辑：首次使用计算 end_time，后续返回已有 end_time
- 次数卡逻辑：扣减 amount，返回 1 小时 session
- 永久卡 sentinel: 4102243200
- 编写完整的单元测试覆盖所有卡类型
- **验证**: 所有卡密类型测试通过

### Task 4.6: API - kmunmachine（卡密解绑）
- `ApiKmunmachineHandler.java`
- 验证：卡存在、启用、设备码匹配、已使用
- 时长卡解绑：永久卡检查次数限制+时间间隔，普通卡扣除时长
- 次数卡解绑：扣除 amount
- 编写测试
- **验证**: 测试通过

### Task 4.7: API 响应加密输出
- 在 `ApiController` 中统一处理响应加密
- 根据 app.mi_type 选择加密方式
- 生成 check 字段：`md5(time + appkey + value)`
- 编写测试：加密响应解密后内容正确
- **验证**: 端到端测试，请求加密 → 处理 → 加密响应 → 解密验证

---

## 阶段 5: 管理后台 API

### Task 5.1: 管理员登录
- `AdminAuthController.java`
- `POST /admin/login`：验证用户名密码，返回 JWT
- 密码验证：`md5(input_pwd + salt)` === 数据库中的 `admin_pwd`
- 编写测试
- **验证**: 登录成功返回 token

### Task 5.2: 应用管理 API
- `AdminAppController.java`
- CRUD：创建、编辑、删除、列表
- 开关切换：active, switch, ipauth, login_check
- appkey 重新生成
- 批量操作：启用、禁用、删除
- 编写测试
- **验证**: 所有端点测试通过

### Task 5.3: 卡密管理 API
- `AdminKmController.java`
- 生成卡密：支持多种类型、自定义长度/前缀/结构
- 列表查询：分页、搜索、筛选
- 单个操作：删除、启用/禁用、解绑
- 批量操作：11 种批量操作
- 清理操作：清空全部/已用/未用/过期
- 编写测试
- **验证**: 所有端点测试通过

### Task 5.4: 文件管理 API
- `AdminFileController.java`
- CRUD：添加、编辑、删除、列表
- 批量操作：启用、禁用、删除
- 编写测试
- **验证**: 测试通过

### Task 5.5: 系统设置 API
- `AdminSettingController.java`
- 读取/保存系统设置
- 数据库优化/修复
- 邮件测试
- 编写测试
- **验证**: 测试通过

---

## 阶段 6: Vue 前端 - 管理后台

### Task 6.1: 前端基础架构
- Axios 封装：baseURL、JWT token 拦截器、错误处理
- Vue Router 配置：/admin/login, /admin/dashboard, /admin/apps, /admin/km, /admin/files, /admin/settings
- Pinia store：user（登录状态、token）
- Element Plus 全局配置
- **验证**: 路由跳转正常

### Task 6.2: 管理员登录页
- `views/admin/Login.vue`
- Element Plus 表单：用户名 + 密码
- 调用 `/admin/login` 接口
- 登录成功存储 token 到 localStorage + Pinia
- 路由守卫：未登录跳转登录页
- **验证**: 登录成功跳转到 dashboard

### Task 6.3: 管理后台布局
- `components/admin/Layout.vue`
- Element Plus 侧边栏导航：应用管理、卡密管理、文件管理、系统设置
- 顶部栏：站点名称、退出登录
- 响应式布局
- **验证**: 布局正常显示，导航可切换

### Task 6.4: 应用管理页
- `views/admin/Apps.vue`
- 应用列表表格（Element Plus Table）
- 创建/编辑对话框
- 开关切换（active, switch, ipauth）
- 安全设置编辑
- appkey 显示 + 重新生成
- **验证**: CRUD 操作正常

### Task 6.5: 卡密管理页
- `views/admin/Km.vue`
- 卡密列表：分页、搜索、按应用/状态筛选
- 生成卡密对话框：选择类型、数量、长度
- 批量操作工具栏
- 导出功能
- **验证**: 列表显示、生成、批量操作正常

### Task 6.6: 文件管理页
- `views/admin/Files.vue`
- 文件列表表格
- 添加/编辑对话框
- 批量操作
- **验证**: CRUD 操作正常

### Task 6.7: 系统设置页
- `views/admin/Settings.vue`
- 站点基本设置表单
- 支付配置
- 邮件配置
- 保存/读取
- **验证**: 设置读写正常

---

## 阶段 7: Vue 前端 - 前台页面

### Task 7.1: 前台布局 + 路由
- 前台路由：/, /login, /register, /docs, /apps
- 前台 Layout 组件
- **验证**: 路由正常

### Task 7.2: 首页
- `views/home/Index.vue`
- 站点介绍 + 公告展示
- 应用列表
- **验证**: 页面正常显示

### Task 7.3: API 文档页
- `views/home/Docs.vue`
- 展示各 API 接口的参数、返回值说明
- 替代原 template/doc/ 的 HTML 片段
- **验证**: 文档内容完整

---

## 阶段 8: 集成测试 + 部署

### Task 8.1: 端到端测试
- 用原 PHP 客户端调用新 Java API，验证完全兼容
- 测试所有加密类型的请求/响应
- 测试卡密登录 + 解绑完整流程
- **验证**: 原客户端无需修改即可正常使用

### Task 8.2: 前端打包集成到后端
- `npm run build` 输出到 `backend/src/main/resources/static/`
- Spring Boot 配置静态资源 serving
- 单 jar 部署
- **验证**: 访问根路径看到前端页面

---

## 执行模式

计划已保存。请选择执行方式：

1. **Subagent 驱动（推荐）**：我用子代理自动执行每个 Task，自动验证，自动修复
2. **手动执行**：你按计划逐个 Task 执行，我提供指导
