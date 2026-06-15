<template>
  <div class="landing">
    <!-- Background effects -->
    <div class="grid-bg"></div>
    <div class="glow-orb orb-cyan"></div>
    <div class="glow-orb orb-magenta"></div>

    <!-- Navigation -->
    <nav class="topnav">
      <div class="nav-inner">
        <div class="logo-mark">
          <span class="logo-bracket">&lt;</span>
          <span class="logo-text">EC</span>
          <span class="logo-bracket">/&gt;</span>
        </div>
        <div class="nav-links">
          <router-link to="/" class="nav-link active">首页</router-link>
          <router-link to="/docs" class="nav-link">API 文档</router-link>
          <router-link to="/admin/login" class="nav-cta">管理后台</router-link>
        </div>
      </div>
    </nav>

    <!-- Hero -->
    <section class="hero">
      <div class="hero-content animate-in">
        <div class="hero-badge">
          <span class="badge-dot"></span>
          <span>应用授权验证平台</span>
        </div>
        <h1 class="hero-title">
          <span class="line-1">为你的应用</span>
          <span class="line-2">构建<span class="highlight">安全验证</span>体系</span>
        </h1>
        <p class="hero-desc">
          EasyCloud 提供卡密验证、设备绑定、数据加密等完整的 API 服务，
          帮助开发者快速接入应用授权系统。
        </p>
        <div class="hero-actions">
          <router-link to="/docs" class="btn-primary">
            <span>查看文档</span>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M5 12h14M12 5l7 7-7 7"/>
            </svg>
          </router-link>
          <router-link to="/admin/login" class="btn-ghost">
            进入控制台
          </router-link>
        </div>
      </div>
    </section>

    <!-- Features -->
    <section class="features">
      <div class="features-grid animate-stagger">
        <div v-for="f in features" :key="f.title" class="feature-card">
          <div class="feature-icon" :style="{ color: f.color }">
            <span class="icon-symbol">{{ f.symbol }}</span>
          </div>
          <h3>{{ f.title }}</h3>
          <p>{{ f.desc }}</p>
          <div class="feature-glow" :style="{ background: f.glowBg }"></div>
        </div>
      </div>
    </section>

    <!-- Tech stack -->
    <section class="tech-section">
      <div class="tech-inner animate-in">
        <h2 class="tech-title">技术栈</h2>
        <div class="tech-grid">
          <div v-for="t in techStack" :key="t.name" class="tech-item">
            <span class="tech-name">{{ t.name }}</span>
            <span class="tech-desc">{{ t.desc }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="landing-footer">
      <div class="footer-inner">
        <span class="footer-logo">&lt;EC/&gt;</span>
        <span class="footer-text">EasyCloud · 应用授权验证系统</span>
      </div>
    </footer>
  </div>
</template>

<script setup>
/**
 * 首页（Landing Page）
 *
 * 页面结构：
 * 1. 顶部导航栏：Logo、首页链接、API 文档链接、管理后台入口
 * 2. Hero 区域：产品标语、简介、行动按钮（查看文档/进入控制台）
 * 3. 特性展示：四张功能卡片（卡密验证、数据加密、设备绑定、高性能 API）
 * 4. 技术栈展示：六项核心技术（Spring Boot 3、MyBatis-Plus、Vue 3 等）
 * 5. 页脚：版权信息
 *
 * 所有数据均为静态配置，无需调用后端 API
 */

/**
 * 产品特性列表
 * @type {Array<{symbol: string, title: string, desc: string, color: string, glowBg: string}>}
 */
const features = [
  {
    symbol: '⚿',
    title: '卡密验证',
    desc: '支持时长卡、次数卡、永久卡等多种授权类型，灵活配置过期策略。',
    color: 'var(--neon-cyan)',
    glowBg: 'rgba(0, 240, 255, 0.08)'
  },
  {
    symbol: '🔐',
    title: '数据加密',
    desc: '内置 RC4、AES、Base64、RSA 多种加密方式，保障数据传输安全。',
    color: 'var(--neon-magenta)',
    glowBg: 'rgba(255, 45, 120, 0.08)'
  },
  {
    symbol: '⊞',
    title: '设备绑定',
    desc: '设备码绑定、IP 验证、解绑管理，防止授权滥用。',
    color: 'var(--neon-purple)',
    glowBg: 'rgba(139, 92, 246, 0.08)'
  },
  {
    symbol: '⚡',
    title: '高性能 API',
    desc: 'Spring Boot 后端 + Redis 缓存，毫秒级响应，稳定可靠。',
    color: 'var(--neon-green)',
    glowBg: 'rgba(0, 255, 136, 0.08)'
  }
]

/**
 * 技术栈列表
 * @type {Array<{name: string, desc: string}>}
 */
const techStack = [
  { name: 'Spring Boot 3', desc: '后端框架' },
  { name: 'MyBatis-Plus', desc: 'ORM 持久层' },
  { name: 'Vue 3', desc: '前端框架' },
  { name: 'Element Plus', desc: 'UI 组件库' },
  { name: 'Redis', desc: '缓存中间件' },
  { name: 'JWT', desc: '身份认证' },
]
</script>

<style scoped lang="scss">
.landing {
  min-height: 100vh;
  background: var(--bg-void);
  position: relative;
  overflow: hidden;
}

.grid-bg {
  position: fixed;
  inset: 0;
  background-image:
    linear-gradient(rgba(0, 240, 255, 0.025) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 240, 255, 0.025) 1px, transparent 1px);
  background-size: 80px 80px;
  mask-image: radial-gradient(ellipse 70% 50% at 50% 30%, black 10%, transparent 60%);
}

.glow-orb {
  position: fixed;
  border-radius: 50%;
  filter: blur(120px);
  pointer-events: none;
}

.orb-cyan {
  width: 500px;
  height: 500px;
  background: var(--neon-cyan);
  top: -150px;
  right: -100px;
  opacity: 0.08;
}

.orb-magenta {
  width: 400px;
  height: 400px;
  background: var(--neon-magenta);
  bottom: -100px;
  left: -100px;
  opacity: 0.06;
}

/* Nav */
.topnav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(5, 5, 8, 0.8);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid var(--border-dim);
}

.nav-inner {
  max-width: 1200px;
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
  transition: all 0.2s ease;

  &:hover {
    background: #33f3ff;
    box-shadow: var(--glow-cyan);
  }
}

/* Hero */
.hero {
  padding-top: 180px;
  padding-bottom: 100px;
  display: flex;
  justify-content: center;
}

.hero-content {
  max-width: 700px;
  text-align: center;
  padding: 0 32px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-family: var(--font-mono);
  font-size: 12px;
  color: var(--neon-cyan);
  background: rgba(0, 240, 255, 0.06);
  border: 1px solid rgba(0, 240, 255, 0.15);
  padding: 6px 16px;
  border-radius: 20px;
  margin-bottom: 32px;
  letter-spacing: 0.05em;

  .badge-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: var(--neon-cyan);
    animation: glowPulse 2s ease-in-out infinite;
  }
}

.hero-title {
  font-family: var(--font-display);
  font-size: 52px;
  font-weight: 800;
  line-height: 1.15;
  margin-bottom: 24px;

  .line-1, .line-2 {
    display: block;
    color: var(--text-primary);
  }

  .highlight {
    background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple));
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
}

.hero-desc {
  font-family: var(--font-body);
  font-size: 17px;
  color: var(--text-secondary);
  line-height: 1.7;
  margin-bottom: 40px;
  max-width: 520px;
  margin-left: auto;
  margin-right: auto;
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-family: var(--font-display);
  font-size: 15px;
  font-weight: 600;
  color: var(--text-inverse);
  background: var(--neon-cyan);
  padding: 12px 28px;
  border-radius: var(--radius-md);
  transition: all 0.3s ease;

  &:hover {
    background: #33f3ff;
    box-shadow: var(--glow-cyan);
    transform: translateY(-1px);
  }
}

.btn-ghost {
  font-family: var(--font-display);
  font-size: 15px;
  font-weight: 600;
  color: var(--text-secondary);
  padding: 12px 28px;
  border-radius: var(--radius-md);
  border: 1px solid var(--border-subtle);
  transition: all 0.3s ease;

  &:hover {
    color: var(--text-primary);
    border-color: var(--border-visible);
    background: rgba(255, 255, 255, 0.03);
  }
}

/* Features */
.features {
  padding: 0 32px 80px;
  display: flex;
  justify-content: center;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  max-width: 1200px;
  width: 100%;
}

.feature-card {
  position: relative;
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  padding: 32px 24px;
  overflow: hidden;
  transition: all 0.3s ease;

  &:hover {
    border-color: var(--border-visible);
    transform: translateY(-4px);
  }

  .feature-icon {
    width: 48px;
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    margin-bottom: 16px;
  }

  h3 {
    font-family: var(--font-display);
    font-size: 17px;
    font-weight: 700;
    color: var(--text-primary);
    margin-bottom: 8px;
  }

  p {
    font-size: 13px;
    color: var(--text-secondary);
    line-height: 1.6;
  }

  .feature-glow {
    position: absolute;
    bottom: -30px;
    right: -30px;
    width: 120px;
    height: 120px;
    border-radius: 50%;
    filter: blur(50px);
  }
}

/* Tech */
.tech-section {
  padding: 0 32px 100px;
  display: flex;
  justify-content: center;
}

.tech-inner {
  max-width: 800px;
  width: 100%;
  text-align: center;
}

.tech-title {
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 32px;
}

.tech-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.tech-item {
  background: var(--bg-card);
  border: 1px solid var(--border-dim);
  border-radius: var(--radius-md);
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 4px;

  .tech-name {
    font-family: var(--font-mono);
    font-size: 14px;
    font-weight: 600;
    color: var(--neon-cyan);
  }

  .tech-desc {
    font-size: 12px;
    color: var(--text-dim);
  }
}

/* Footer */
.landing-footer {
  border-top: 1px solid var(--border-dim);
  padding: 24px 32px;
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;

  .footer-logo {
    font-family: var(--font-mono);
    font-size: 16px;
    font-weight: 600;
    color: var(--neon-cyan);
  }

  .footer-text {
    font-size: 12px;
    color: var(--text-dim);
  }
}
</style>
