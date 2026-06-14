<template>
  <div class="admin-shell">
    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="logo-mark">
          <span class="logo-bracket">&lt;</span>
          <span class="logo-text">EC</span>
          <span class="logo-bracket">/&gt;</span>
        </div>
        <div class="logo-label">
          <span class="label-main">EasyCloud</span>
          <span class="label-sub">Admin Panel</span>
        </div>
      </div>

      <nav class="sidebar-nav">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: $route.path === item.path }"
        >
          <div class="nav-icon">
            <el-icon :size="18"><component :is="item.icon" /></el-icon>
          </div>
          <span class="nav-label">{{ item.label }}</span>
          <div class="nav-indicator"></div>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <div class="status-dot"></div>
        <span class="status-text">系统运行中</span>
      </div>
    </aside>

    <!-- Main content -->
    <div class="main-area">
      <!-- Top bar -->
      <header class="top-bar">
        <div class="top-left">
          <span class="page-title">{{ currentPageTitle }}</span>
        </div>
        <div class="top-right">
          <div class="user-info">
            <div class="user-avatar">{{ userStore.username?.charAt(0)?.toUpperCase() || 'A' }}</div>
            <span class="user-name">{{ userStore.username || 'Admin' }}</span>
          </div>
          <button class="logout-btn" @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
          </button>
        </div>
      </header>

      <!-- Page content -->
      <main class="content-area">
        <router-view v-slot="{ Component, route }">
          <transition name="content" mode="out-in">
            <component :is="Component" :key="route.path" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { DataAnalysis, Grid, Key, Folder, Setting, SwitchButton } from '@element-plus/icons-vue'

const route = useRoute()
const userStore = useUserStore()

const navItems = [
  { path: '/admin/dashboard', label: '仪表盘', icon: 'DataAnalysis' },
  { path: '/admin/apps', label: '应用管理', icon: 'Grid' },
  { path: '/admin/km', label: '卡密管理', icon: 'Key' },
  { path: '/admin/files', label: '文件管理', icon: 'Folder' },
  { path: '/admin/settings', label: '系统设置', icon: 'Setting' },
]

const currentPageTitle = computed(() => {
  const item = navItems.find(n => route.path.startsWith(n.path))
  return item?.label || '管理后台'
})

function handleLogout() {
  userStore.logout()
}
</script>

<style scoped lang="scss">
.admin-shell {
  display: flex;
  min-height: 100vh;
  background: var(--bg-void);
}

/* ===== SIDEBAR ===== */
.sidebar {
  width: var(--sidebar-width);
  background: var(--bg-surface);
  border-right: 1px solid var(--border-dim);
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 100;

  &::after {
    content: '';
    position: absolute;
    top: 0;
    right: -1px;
    width: 1px;
    height: 100%;
    background: linear-gradient(
      180deg,
      transparent 0%,
      rgba(0, 240, 255, 0.2) 30%,
      rgba(0, 240, 255, 0.05) 70%,
      transparent 100%
    );
  }
}

.sidebar-header {
  padding: 24px 20px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid var(--border-dim);

  .logo-mark {
    font-family: var(--font-mono);
    font-size: 20px;
    font-weight: 600;
    flex-shrink: 0;

    .logo-bracket { color: var(--neon-cyan); }
    .logo-text { color: var(--text-primary); }
  }

  .logo-label {
    display: flex;
    flex-direction: column;

    .label-main {
      font-family: var(--font-display);
      font-size: 15px;
      font-weight: 700;
      color: var(--text-primary);
      letter-spacing: 0.02em;
    }

    .label-sub {
      font-family: var(--font-mono);
      font-size: 10px;
      color: var(--text-dim);
      letter-spacing: 0.1em;
      text-transform: uppercase;
      margin-top: 1px;
    }
  }
}

.sidebar-nav {
  flex: 1;
  padding: 16px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  transition: all 0.2s ease;
  position: relative;
  cursor: pointer;

  .nav-icon {
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: var(--radius-sm);
    background: rgba(255, 255, 255, 0.03);
    transition: all 0.2s ease;
  }

  .nav-label {
    font-family: var(--font-body);
    font-size: 14px;
    font-weight: 500;
    letter-spacing: 0.01em;
  }

  .nav-indicator {
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 3px;
    height: 0;
    background: var(--neon-cyan);
    border-radius: 0 2px 2px 0;
    transition: height 0.2s ease;
    box-shadow: var(--glow-cyan);
  }

  &:hover {
    background: rgba(0, 240, 255, 0.04);
    color: var(--text-primary);

    .nav-icon {
      background: rgba(0, 240, 255, 0.08);
    }
  }

  &.active {
    background: rgba(0, 240, 255, 0.06);
    color: var(--neon-cyan);

    .nav-icon {
      background: rgba(0, 240, 255, 0.12);
      color: var(--neon-cyan);
    }

    .nav-indicator {
      height: 24px;
    }
  }
}

.sidebar-footer {
  padding: 16px 20px;
  border-top: 1px solid var(--border-dim);
  display: flex;
  align-items: center;
  gap: 8px;

  .status-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: var(--neon-green);
    box-shadow: 0 0 8px rgba(0, 255, 136, 0.5);
    animation: glowPulse 2s ease-in-out infinite;
  }

  .status-text {
    font-family: var(--font-mono);
    font-size: 11px;
    color: var(--text-dim);
    letter-spacing: 0.05em;
  }
}

/* ===== MAIN AREA ===== */
.main-area {
  flex: 1;
  margin-left: var(--sidebar-width);
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.top-bar {
  height: var(--header-height);
  background: var(--bg-surface);
  border-bottom: 1px solid var(--border-dim);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  position: sticky;
  top: 0;
  z-index: 50;
  backdrop-filter: blur(12px);
}

.page-title {
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  letter-spacing: 0.01em;
}

.top-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-sm);
  background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple));
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-display);
  font-size: 14px;
  font-weight: 700;
  color: var(--text-inverse);
}

.user-name {
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--text-secondary);
}

.logout-btn {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-subtle);
  background: transparent;
  color: var(--text-dim);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;

  &:hover {
    border-color: var(--neon-magenta);
    color: var(--neon-magenta);
    background: rgba(255, 45, 120, 0.08);
  }
}

.content-area {
  flex: 1;
  padding: 28px;
  overflow-y: auto;
}

/* Content transition */
.content-enter-active {
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}
.content-leave-active {
  transition: all 0.15s ease-in;
}
.content-enter-from {
  opacity: 0;
  transform: translateY(12px);
}
.content-leave-to {
  opacity: 0;
}
</style>
