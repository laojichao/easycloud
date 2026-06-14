<template>
  <div class="docs-page">
    <div class="grid-bg"></div>

    <!-- Nav -->
    <nav class="topnav">
      <div class="nav-inner">
        <router-link to="/" class="logo-mark">
          <span class="logo-bracket">&lt;</span>
          <span class="logo-text">EC</span>
          <span class="logo-bracket">/&gt;</span>
        </router-link>
        <div class="nav-links">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/docs" class="nav-link active">API 文档</router-link>
          <router-link to="/admin/login" class="nav-cta">管理后台</router-link>
        </div>
      </div>
    </nav>

    <!-- Content -->
    <main class="docs-main">
      <div class="docs-header animate-in">
        <h1>API 接口文档</h1>
        <p>EasyCloud 验证系统对外 API 接口说明</p>
      </div>

      <div class="docs-body animate-stagger">
        <div v-for="api in apiList" :key="api.name" class="api-card">
          <div class="api-header">
            <span class="method-badge">POST</span>
            <code class="api-path">/api/legacy?app={appid}&amp;api={{ api.name }}</code>
            <span class="api-title">{{ api.title }}</span>
          </div>
          <p class="api-desc">{{ api.desc }}</p>

          <div class="api-section" v-if="api.params.length">
            <h4>请求参数</h4>
            <div class="param-table">
              <div class="param-row param-header">
                <span>参数名</span>
                <span>类型</span>
                <span>必填</span>
                <span>说明</span>
              </div>
              <div v-for="p in api.params" :key="p.name" class="param-row">
                <code>{{ p.name }}</code>
                <span>{{ p.type }}</span>
                <span :class="p.required ? 'req-yes' : 'req-no'">{{ p.required ? '是' : '否' }}</span>
                <span>{{ p.desc }}</span>
              </div>
            </div>
          </div>

          <div class="api-section">
            <h4>返回示例</h4>
            <pre class="code-block"><code>{{ api.response }}</code></pre>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
const apiList = [
  {
    name: 'ini',
    title: '获取应用配置',
    desc: '获取应用的版本、更新信息等基本配置。',
    params: [],
    response: JSON.stringify({
      code: 200,
      msg: { version: '1.0.0', version_info: '更新说明', app_update_url: '...', app_update_must: 'n', api_total: 1000 },
      time: ***REMOVED***0,
      check: 'md5签名'
    }, null, 2)
  },
  {
    name: 'notice',
    title: '获取公告',
    desc: '获取应用公告内容。',
    params: [],
    response: JSON.stringify({
      code: 200,
      msg: { app_gg: '公告内容' },
      time: ***REMOVED***0,
      check: 'md5签名'
    }, null, 2)
  },
  {
    name: 'getfile',
    title: '获取文件',
    desc: '获取应用下的文件列表。',
    params: [
      { name: 'id', type: 'number', required: false, desc: '文件 ID（可选）' }
    ],
    response: JSON.stringify({
      code: 200,
      msg: [{ file_url: 'https://...', date: '2024-01-01', note: '说明' }],
      time: ***REMOVED***0,
      check: 'md5签名'
    }, null, 2)
  },
  {
    name: 'kmlogon',
    title: '卡密登录',
    desc: '使用卡密进行登录验证，支持时长卡和次数卡。',
    params: [
      { name: 'kami', type: 'string', required: true, desc: '卡密内容' },
      { name: 'markcode', type: 'string', required: true, desc: '设备机器码' }
    ],
    response: JSON.stringify({
      code: 200,
      msg: { kami: 'xxxx-xxxx-xxxx', vip: '***REMOVED***0' },
      time: ***REMOVED***0,
      check: 'md5签名'
    }, null, 2)
  },
  {
    name: 'kmunmachine',
    title: '卡密解绑',
    desc: '解绑卡密与设备的绑定关系。',
    params: [
      { name: 'kami', type: 'string', required: true, desc: '卡密内容' },
      { name: 'markcode', type: 'string', required: true, desc: '设备机器码' }
    ],
    response: JSON.stringify({
      code: 200,
      msg: '卡密解绑成功',
      time: ***REMOVED***0,
      check: 'md5签名'
    }, null, 2)
  }
]
</script>

<style scoped lang="scss">
.docs-page {
  min-height: 100vh;
  background: var(--bg-void);
  position: relative;
}

.grid-bg {
  position: fixed;
  inset: 0;
  background-image:
    linear-gradient(rgba(0, 240, 255, 0.02) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 240, 255, 0.02) 1px, transparent 1px);
  background-size: 80px 80px;
  mask-image: radial-gradient(ellipse 60% 40% at 50% 20%, black 10%, transparent 60%);
  pointer-events: none;
}

/* Nav */
.topnav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(5, 5, 8, 0.85);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid var(--border-dim);
}

.nav-inner {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 32px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-mark {
  font-family: var(--font-mono);
  font-size: 20px;
  font-weight: 600;
  text-decoration: none;

  .logo-bracket { color: var(--neon-cyan); }
  .logo-text { color: var(--text-primary); }
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-link {
  font-family: var(--font-body);
  font-size: 14px;
  color: var(--text-secondary);
  padding: 8px 16px;
  border-radius: var(--radius-sm);
  transition: all 0.2s ease;
  text-decoration: none;

  &:hover, &.active {
    color: var(--text-primary);
    background: rgba(255, 255, 255, 0.04);
  }
}

.nav-cta {
  font-family: var(--font-body);
  font-size: 13px;
  font-weight: 600;
  color: var(--text-inverse);
  background: var(--neon-cyan);
  padding: 8px 20px;
  border-radius: var(--radius-sm);
  margin-left: 8px;
  text-decoration: none;
  transition: all 0.2s ease;

  &:hover {
    background: #33f3ff;
    box-shadow: var(--glow-cyan);
  }
}

/* Content */
.docs-main {
  max-width: 900px;
  margin: 0 auto;
  padding: 120px 32px 80px;
}

.docs-header {
  margin-bottom: 48px;

  h1 {
    font-family: var(--font-display);
    font-size: 36px;
    font-weight: 800;
    color: var(--text-primary);
    margin-bottom: 8px;
  }

  p {
    font-size: 16px;
    color: var(--text-secondary);
  }
}

.docs-body {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.api-card {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  padding: 28px;
  overflow: hidden;
}

.api-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.method-badge {
  font-family: var(--font-mono);
  font-size: 11px;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 4px;
  background: rgba(0, 255, 136, 0.1);
  color: var(--neon-green);
  border: 1px solid rgba(0, 255, 136, 0.2);
  letter-spacing: 0.05em;
}

.api-path {
  font-family: var(--font-mono);
  font-size: 13px;
  color: var(--text-secondary);
  background: rgba(255, 255, 255, 0.03);
  padding: 4px 10px;
  border-radius: var(--radius-sm);
}

.api-title {
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  margin-left: auto;
}

.api-desc {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: 20px;
}

.api-section {
  margin-top: 20px;

  h4 {
    font-family: var(--font-display);
    font-size: 13px;
    font-weight: 600;
    color: var(--neon-cyan);
    text-transform: uppercase;
    letter-spacing: 0.08em;
    margin-bottom: 12px;
  }
}

.param-table {
  border: 1px solid var(--border-dim);
  border-radius: var(--radius-sm);
  overflow: hidden;
}

.param-row {
  display: grid;
  grid-template-columns: 140px 80px 60px 1fr;
  gap: 12px;
  padding: 10px 16px;
  font-size: 13px;
  color: var(--text-secondary);
  border-bottom: 1px solid var(--border-dim);

  &:last-child { border-bottom: none; }

  &.param-header {
    background: rgba(0, 240, 255, 0.03);
    font-weight: 600;
    font-size: 11px;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    color: var(--text-dim);
  }

  code {
    font-family: var(--font-mono);
    color: var(--neon-cyan);
  }

  .req-yes { color: var(--neon-magenta); }
  .req-no { color: var(--text-dim); }
}

.code-block {
  background: var(--bg-surface);
  border: 1px solid var(--border-dim);
  border-radius: var(--radius-sm);
  padding: 16px 20px;
  overflow-x: auto;

  code {
    font-family: var(--font-mono);
    font-size: 12px;
    color: var(--text-secondary);
    line-height: 1.6;
  }
}
</style>
