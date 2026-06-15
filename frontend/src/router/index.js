/**
 * 路由配置模块
 *
 * 路由结构分为两大部分：
 * 1. 用户端路由（/）：首页、登录、注册、用户中心、API 文档
 * 2. 管理端路由（/admin）：管理后台布局下的仪表盘、应用管理、卡密管理、文件管理、系统设置
 *
 * 认证逻辑（路由守卫 beforeEach）：
 * - 标记 meta.requiresAuth 的路由需要登录才能访问
 * - 根据 meta.role 或路径前缀判断需要哪种角色的 token：
 *   - /admin/* 需要管理员 token（admin_token）
 *   - /user 需要普通用户 token（user_token）
 * - 未认证时自动重定向到对应的登录页
 *
 * 所有路由组件均使用懒加载（动态 import）以优化首屏加载性能
 */
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  // ==================== 用户端路由 ====================

  /** 首页（产品介绍、功能展示、技术栈说明） */
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/home/Index.vue')
  },

  /** 用户登录页 */
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/home/Login.vue')
  },

  /** 用户注册页（支持用户名、密码、QQ、邮箱注册） */
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/home/Register.vue')
  },

  /** 用户中心（需要 user 角色认证：签到、工单、邀请、积分、提现） */
  {
    path: '/user',
    name: 'UserCenter',
    component: () => import('../views/home/UserCenter.vue'),
    meta: { requiresAuth: true, role: 'user' }
  },

  /** API 接口文档页（展示卡密验证、解绑等对外接口说明） */
  {
    path: '/docs',
    name: 'Docs',
    component: () => import('../views/home/Docs.vue')
  },

  // ==================== 管理端路由 ====================

  /** 管理员登录页（独立于用户登录，使用 admin_token） */
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('../views/admin/Login.vue')
  },

  /**
   * 管理后台主布局（侧边栏 + 顶栏 + 内容区）
   * 所有子路由需要管理员 token 认证
   */
  {
    path: '/admin',
    component: () => import('../views/admin/Layout.vue'),
    meta: { requiresAuth: true },
    redirect: '/admin/dashboard',
    children: [
      /** 仪表盘：统计数据卡片、快速操作入口、系统信息 */
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('../views/admin/Dashboard.vue')
      },

      /** 应用管理：应用 CRUD、状态切换、加密配置 */
      {
        path: 'apps',
        name: 'AdminApps',
        component: () => import('../views/admin/Apps.vue')
      },

      /** 卡密管理：卡密生成、筛选、启停、解绑、删除 */
      {
        path: 'km',
        name: 'AdminKm',
        component: () => import('../views/admin/Km.vue')
      },

      /** 文件管理：文件链接管理（直链/蓝奏云） */
      {
        path: 'files',
        name: 'AdminFiles',
        component: () => import('../views/admin/Files.vue')
      },

      /** 系统设置：站点配置、密码修改、缓存维护 */
      {
        path: 'settings',
        name: 'AdminSettings',
        component: () => import('../views/admin/Settings.vue')
      }
    ]
  }
]

/** 创建路由实例，使用 HTML5 History 模式 */
const router = createRouter({
  history: createWebHistory(),
  routes
})

/**
 * 全局前置路由守卫
 *
 * 认证判断逻辑：
 * 1. 检查目标路由（或其父路由）是否标记了 meta.requiresAuth
 * 2. 确定所需角色：优先使用路由 meta.role，否则根据路径前缀推断
 *    - /admin/* -> admin 角色
 *    - 其他 -> user 角色
 * 3. 验证对应角色的登录状态：
 *    - admin 角色：检查 isAdminLoggedIn（admin_token 是否存在）
 *    - user 角色：检查 isUserLoggedIn（user_token 是否存在）
 * 4. 未认证则重定向到对应登录页
 */
router.beforeEach((to, from, next) => {
  if (to.matched.some(record => record.meta.requiresAuth)) {
    const userStore = useUserStore()
    // 确定所需角色：meta.role 优先，否则根据路径前缀推断
    const role = to.meta.role || (to.path.startsWith('/admin') ? 'admin' : 'user')

    if (role === 'admin') {
      // /admin/* 需要管理员 token
      if (!userStore.isAdminLoggedIn) {
        next({ name: 'AdminLogin' })
      } else {
        next()
      }
    } else if (role === 'user') {
      // /user 需要用户 token
      if (!userStore.isUserLoggedIn) {
        next({ name: 'Login' })
      } else {
        next()
      }
    } else {
      if (!userStore.isLoggedIn) {
        next({ name: 'Login' })
      } else {
        next()
      }
    }
  } else {
    // 无需认证的路由直接放行
    next()
  }
})

export default router
