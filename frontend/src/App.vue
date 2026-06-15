<!--
  根组件 App.vue

  结构说明：
  - 使用 <router-view> 的作用域插槽获取当前路由对应的组件
  - 通过 <transition> 实现页面切换动画（淡入淡出 + 微位移）
  - :key="route.path" 确保不同路由路径触发组件重新渲染

  页面过渡动画：
  - 进入：从下方 8px 处淡入上移，使用 cubic-bezier 缓动曲线
  - 离开：向上 4px 淡出，使用 ease-in 缓动
-->
<template>
  <router-view v-slot="{ Component, route }">
    <transition name="page" mode="out-in">
      <component :is="Component" :key="route.path" />
    </transition>
  </router-view>
</template>

<script setup>
/**
 * 根组件
 * 无业务逻辑，仅提供路由视图容器和页面过渡动画
 */
</script>

<style>
/* 页面进入动画 - 淡入 + 从下方上移 */
.page-enter-active {
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}
/* 页面离开动画 - 淡出 + 向上微移 */
.page-leave-active {
  transition: all 0.2s ease-in;
}
/* 进入起始状态：透明 + 下移 8px */
.page-enter-from {
  opacity: 0;
  transform: translateY(8px);
}
/* 离开结束状态：透明 + 上移 4px */
.page-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>
