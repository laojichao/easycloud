/**
 * 应用入口文件
 *
 * 负责初始化 Vue 应用实例并注册全局插件：
 * - Element Plus：UI 组件库（表单、表格、对话框、分页等）
 * - Element Plus Icons：全局注册所有图标组件（如 Plus、Search、Setting 等）
 * - Pinia：状态管理库（管理用户认证状态等全局状态）
 * - Vue Router：路由管理（用户端和管理端路由切换）
 *
 * 全局样式通过 global.scss 引入，定义了 CSS 变量（颜色、字体、圆角等）
 */
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'
import './styles/global.scss'

/** 创建 Vue 应用实例 */
const app = createApp(App)

/**
 * 全局注册 Element Plus 所有图标组件
 * 注册后可在任意组件中通过 <el-icon><Plus /></el-icon> 方式使用
 */
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

/** 注册 Element Plus UI 组件库 */
app.use(ElementPlus)

/** 注册 Pinia 状态管理 */
app.use(createPinia())

/** 注册 Vue Router 路由 */
app.use(router)

/** 挂载应用到 #app DOM 节点 */
app.mount('#app')
