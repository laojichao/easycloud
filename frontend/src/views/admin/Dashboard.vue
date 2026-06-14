<template>
  <div class="dashboard">
    <!-- Stats grid -->
    <div class="stats-grid animate-stagger">
      <div v-for="stat in statsCards" :key="stat.label" class="stat-card">
        <div class="stat-icon" :style="{ background: stat.iconBg }">
          <el-icon :size="22" :color="stat.iconColor"><component :is="stat.icon" /></el-icon>
        </div>
        <div class="stat-body">
          <span class="stat-value">{{ stat.value }}</span>
          <span class="stat-label">{{ stat.label }}</span>
        </div>
        <div class="stat-glow" :style="{ background: stat.glowColor }"></div>
      </div>
    </div>

    <!-- Quick actions -->
    <div class="section-block animate-in" style="animation-delay: 0.4s">
      <h3 class="section-title">
        <span class="title-accent">//</span> 快速操作
      </h3>
      <div class="quick-actions">
        <router-link to="/admin/apps" class="action-card">
          <div class="action-icon" style="color: var(--neon-cyan)">
            <el-icon :size="24"><Grid /></el-icon>
          </div>
          <span class="action-label">管理应用</span>
          <span class="action-desc">创建、编辑、配置应用</span>
        </router-link>
        <router-link to="/admin/km" class="action-card">
          <div class="action-icon" style="color: var(--neon-magenta)">
            <el-icon :size="24"><Key /></el-icon>
          </div>
          <span class="action-label">生成卡密</span>
          <span class="action-desc">批量生成授权卡密</span>
        </router-link>
        <router-link to="/admin/files" class="action-card">
          <div class="action-icon" style="color: var(--neon-purple)">
            <el-icon :size="24"><Folder /></el-icon>
          </div>
          <span class="action-label">文件管理</span>
          <span class="action-desc">管理下载链接和文件</span>
        </router-link>
        <router-link to="/admin/settings" class="action-card">
          <div class="action-icon" style="color: var(--neon-amber)">
            <el-icon :size="24"><Setting /></el-icon>
          </div>
          <span class="action-label">系统设置</span>
          <span class="action-desc">站点配置和安全设置</span>
        </router-link>
      </div>
    </div>

    <!-- System info -->
    <div class="section-block animate-in" style="animation-delay: 0.6s">
      <h3 class="section-title">
        <span class="title-accent">//</span> 系统信息
      </h3>
      <div class="info-grid">
        <div class="info-item">
          <span class="info-key">后端框架</span>
          <span class="info-val">Spring Boot 3.2.5</span>
        </div>
        <div class="info-item">
          <span class="info-key">前端框架</span>
          <span class="info-val">Vue 3 + Element Plus</span>
        </div>
        <div class="info-item">
          <span class="info-key">数据库</span>
          <span class="info-val">MySQL 8.0 / H2</span>
        </div>
        <div class="info-item">
          <span class="info-key">缓存</span>
          <span class="info-val">Redis</span>
        </div>
        <div class="info-item">
          <span class="info-key">认证方式</span>
          <span class="info-val">JWT (HS256)</span>
        </div>
        <div class="info-item">
          <span class="info-key">API 文档</span>
          <span class="info-val">Knife4j / OpenAPI 3</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getStats } from '@/api/admin'
import { Grid, Key, Folder, Setting, DataAnalysis } from '@element-plus/icons-vue'

const stats = ref({
  appCount: 0,
  kmCount: 0,
  fileCount: 0,
  todayApps: 0,
  todayKm: 0,
  todayLogs: 0,
  kmUsed: 0,
  kmUnused: 0
})

const statsCards = computed(() => [
  {
    label: '应用总数',
    value: stats.value.appCount,
    icon: 'Grid',
    iconBg: 'rgba(0, 240, 255, 0.1)',
    iconColor: '#00f0ff',
    glowColor: 'rgba(0, 240, 255, 0.05)'
  },
  {
    label: '卡密总数',
    value: stats.value.kmCount,
    icon: 'Key',
    iconBg: 'rgba(255, 45, 120, 0.1)',
    iconColor: '#ff2d78',
    glowColor: 'rgba(255, 45, 120, 0.05)'
  },
  {
    label: '文件总数',
    value: stats.value.fileCount,
    icon: 'Folder',
    iconBg: 'rgba(139, 92, 246, 0.1)',
    iconColor: '#8b5cf6',
    glowColor: 'rgba(139, 92, 246, 0.05)'
  },
  {
    label: '文件总数',
    value: stats.value.fileCount,
    icon: 'Folder',
    iconBg: 'rgba(139, 92, 246, 0.1)',
    iconColor: '#8b5cf6',
    glowColor: 'rgba(139, 92, 246, 0.05)'
  },
  {
    label: '今日日志',
    value: stats.value.todayLogs,
    icon: 'DataAnalysis',
    iconBg: 'rgba(0, 255, 136, 0.1)',
    iconColor: '#00ff88',
    glowColor: 'rgba(0, 255, 136, 0.05)'
  }
])

onMounted(async () => {
  try {
    const res = await getStats()
    if (res.code === 200 && res.data) {
      Object.assign(stats.value, res.data)
    }
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
})
</script>

<style scoped lang="scss">
.dashboard {
  max-width: 1200px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 32px;
}

.stat-card {
  position: relative;
  background: var(--bg-card);
  backdrop-filter: blur(10px);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  overflow: hidden;
  transition: all 0.3s ease;

  &:hover {
    border-color: var(--border-visible);
    transform: translateY(-2px);
  }

  .stat-icon {
    width: 48px;
    height: 48px;
    border-radius: var(--radius-md);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .stat-body {
    display: flex;
    flex-direction: column;
    z-index: 1;

    .stat-value {
      font-family: var(--font-display);
      font-size: 28px;
      font-weight: 700;
      color: var(--text-primary);
      line-height: 1;
    }

    .stat-label {
      font-family: var(--font-body);
      font-size: 13px;
      color: var(--text-secondary);
      margin-top: 6px;
    }
  }

  .stat-glow {
    position: absolute;
    bottom: -20px;
    right: -20px;
    width: 100px;
    height: 100px;
    border-radius: 50%;
    filter: blur(40px);
  }
}

.section-block {
  margin-bottom: 32px;
}

.section-title {
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;

  .title-accent {
    font-family: var(--font-mono);
    color: var(--neon-cyan);
    margin-right: 8px;
    opacity: 0.7;
  }
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.action-card {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    border-color: var(--border-visible);
    transform: translateY(-2px);

    .action-icon {
      transform: scale(1.1);
    }
  }

  .action-icon {
    width: 44px;
    height: 44px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: transform 0.3s ease;
  }

  .action-label {
    font-family: var(--font-display);
    font-size: 15px;
    font-weight: 600;
    color: var(--text-primary);
  }

  .action-desc {
    font-size: 12px;
    color: var(--text-dim);
  }
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.info-item {
  background: var(--bg-card);
  border: 1px solid var(--border-dim);
  border-radius: var(--radius-md);
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .info-key {
    font-family: var(--font-body);
    font-size: 13px;
    color: var(--text-secondary);
  }

  .info-val {
    font-family: var(--font-mono);
    font-size: 12px;
    color: var(--neon-cyan);
    letter-spacing: 0.02em;
  }
}
</style>
