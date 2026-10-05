import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/home/Index.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/home/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/home/Register.vue')
  },
  {
    path: '/user',
    name: 'UserCenter',
    component: () => import('../views/home/UserCenter.vue'),
    meta: { requiresUser: true }
  },
  {
    path: '/docs',
    name: 'Docs',
    component: () => import('../views/home/Docs.vue')
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('../views/admin/Login.vue')
  },
  {
    path: '/admin',
    component: () => import('../views/admin/Layout.vue'),
    meta: { requiresAuth: true },
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('../views/admin/Dashboard.vue')
      },
      {
        path: 'apps',
        name: 'AdminApps',
        component: () => import('../views/admin/Apps.vue')
      },
      {
        path: 'km',
        name: 'AdminKm',
        component: () => import('../views/admin/Km.vue')
      },
      {
        path: 'files',
        name: 'AdminFiles',
        component: () => import('../views/admin/Files.vue')
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('../views/admin/Users.vue')
      },
      {
        path: 'workorders',
        name: 'AdminWorkOrders',
        component: () => import('../views/admin/WorkOrders.vue')
      },
      {
        path: 'tixian',
        name: 'AdminTixian',
        component: () => import('../views/admin/Tixian.vue')
      },
      {
        path: 'payorders',
        name: 'AdminPayOrders',
        component: () => import('../views/admin/PayOrders.vue')
      },
      {
        path: 'settings',
        name: 'AdminSettings',
        component: () => import('../views/admin/Settings.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：/admin/* 需要管理员会话，/user 需要用户会话
router.beforeEach((to, from, next) => {
  if (to.matched.some(record => record.meta.requiresAuth)) {
    const userStore = useUserStore()
    if (!userStore.isLoggedIn) {
      next({ name: 'AdminLogin' })
    } else {
      next()
    }
  } else if (to.matched.some(record => record.meta.requiresUser)) {
    const userStore = useUserStore()
    if (!userStore.isUserLoggedIn) {
      next({ name: 'Login' })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
