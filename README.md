# EasyCloud 验证系统

> 基于 Spring Boot 3 + Vue 3 的应用授权验证平台，从 PHP 原版完整迁移到 Java。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.2.5 + MyBatis-Plus 3.5.5 + JWT |
| 前端 | Vue 3 + Element Plus + Vite 5 + Pinia |
| 数据库 | MySQL 8.0（生产） / H2（开发） |
| 缓存 | Redis |
| API 文档 | Knife4j / OpenAPI 3 |

## 项目结构

```
easycloud/
├── backend/                    # Java 后端
│   ├── pom.xml
│   └── src/main/java/com/easycloud/
│       ├── common/             # 通用工具类
│       │   ├── ApiCrypto.java  # RC4/AES/RSA/Base64 加解密
│       │   ├── ApiSignature.java # API 签名验证
│       │   ├── JwtUtil.java    # JWT Token 工具
│       │   └── Result.java     # 统一响应封装
│       ├── config/             # 配置类
│       │   ├── MyBatisPlusConfig.java
│       │   ├── SecurityConfig.java
│       │   └── WebMvcConfig.java
│       ├── controller/
│       │   ├── admin/          # 管理后台 API
│       │   │   ├── AdminAuthController.java
│       │   │   ├── AdminAppController.java
│       │   │   ├── AdminKmController.java
│       │   │   ├── AdminFileController.java
│       │   │   └── AdminSettingController.java
│       │   └── api/            # 对外 API（兼容 PHP 客户端）
│       │       ├── ApiController.java
│       │       ├── IniHandler.java
│       │       ├── NoticeHandler.java
│       │       ├── GetfileHandler.java
│       │       ├── KmlogonHandler.java
│       │       └── KmunmachineHandler.java
│       ├── entity/             # 数据库实体
│       ├── interceptor/        # 拦截器
│       ├── mapper/             # MyBatis-Plus Mapper
│       └── service/            # 业务服务
├── frontend/                   # Vue 3 前端
│   ├── src/
│   │   ├── api/                # API 调用层
│   │   ├── router/             # 路由配置
│   │   ├── stores/             # Pinia 状态管理
│   │   ├── views/
│   │   │   ├── admin/          # 管理后台页面
│   │   │   └── home/           # 前台页面
│   │   └── styles/             # 全局样式
│   └── vite.config.js
└── api/                        # 原 PHP 源码（参考）
```

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+（生产）/ H2（开发免安装）
- Redis（可选，开发环境可不用）

### 后端启动

```bash
cd backend

# 开发模式（使用 H2 内存数据库，无需 MySQL）
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 生产模式（需要 MySQL + Redis）
# 先修改 src/main/resources/application.yml 中的数据库配置
mvn spring-boot:run
```

后端启动后访问：
- API 服务：http://localhost:8080
- API 文档：http://localhost:8080/doc.html
- H2 控制台：http://localhost:8080/h2-console（仅 dev 模式）

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端启动后访问：http://localhost:3000

### 生产部署

```bash
# 构建前端
cd frontend
npm run build

# 复制到后端静态资源
cp -r dist/* ../backend/src/main/resources/static/

# 打包后端
cd ../backend
mvn package -DskipTests

# 运行
java -jar target/easycloud-backend-1.0.0-SNAPSHOT.jar
```

## API 接口

### 对外 API（兼容 PHP 客户端）

| 接口 | 说明 | 参数 |
|------|------|------|
| `POST /api/legacy?app={id}&api=ini` | 获取应用配置 | app, api |
| `POST /api/legacy?app={id}&api=notice` | 获取公告 | app, api |
| `POST /api/legacy?app={id}&api=getfile` | 获取文件列表 | app, api, [id] |
| `POST /api/legacy?app={id}&api=kmlogon` | 卡密登录 | app, api, kami, markcode |
| `POST /api/legacy?app={id}&api=kmunmachine` | 卡密解绑 | app, api, kami, markcode |

支持加密模式：明文(0)、RC4-GBK(1)、Base64(2)、RC4-raw(3)、AES(4)、Base64v2(5)

### 管理后台 API（JWT 认证）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/admin/login` | 管理员登录 |
| GET | `/api/admin/app/list` | 应用列表 |
| POST | `/api/admin/app` | 创建应用 |
| PUT | `/api/admin/app/{id}` | 更新应用 |
| PUT | `/api/admin/app/{id}/security` | 更新安全配置 |
| PUT | `/api/admin/app/{id}/auth` | 更新认证配置 |
| PUT | `/api/admin/app/{id}/info` | 更新应用信息 |
| DELETE | `/api/admin/app/{id}` | 删除应用（级联） |
| POST | `/api/admin/app/{id}/toggle` | 切换状态 |
| POST | `/api/admin/app/{id}/regenkey` | 重新生成 appkey |
| GET | `/api/admin/km/list` | 卡密列表 |
| POST | `/api/admin/km/generate` | 生成卡密 |
| POST | `/api/admin/km/batch` | 批量操作 |
| POST | `/api/admin/km/clean` | 清理卡密 |
| GET | `/api/admin/file/list` | 文件列表 |
| POST | `/api/admin/file/{id}/toggle` | 切换文件状态 |
| GET | `/api/admin/setting` | 获取设置 |
| POST | `/api/admin/setting` | 保存设置 |
| POST | `/api/admin/setting/change-password` | 修改密码 |

## 数据库表

| 表名 | 说明 |
|------|------|
| `yixi_apps` | 应用配置表 |
| `yixi_appkm` | 卡密表 |
| `yixi_appfile` | 文件链接表 |
| `yixi_config` | 系统配置表（KV） |
| `yixi_log` | 操作日志表 |
| `yixi_cache` | 缓存表 |
| `yixi_sig` | 签名记录表 |

## 安全说明

- 密码存储：支持 PHP 明文兼容 + Java MD5 加盐（`!@#%!s!0`）
- JWT 认证：管理后台使用 Bearer Token
- API 签名：`md5(sorted_params + appkey)`
- 响应校验：`md5(time + appkey + value)`
- SQL 注入防护：MyBatis-Plus 参数化查询
- CORS：可通过 `WebMvcConfig` 配置允许的源

## 许可证

MIT
