<template>
  <div class="home-page">
    <el-container>
      <el-header class="home-header">
        <div class="logo">EasyCloud</div>
        <div class="nav-links">
          <el-button link @click="$router.push('/')">首页</el-button>
          <el-button link @click="$router.push('/docs')">API文档</el-button>
          <el-button type="primary" @click="$router.push('/admin/login')">管理后台</el-button>
        </div>
      </el-header>
      <el-main class="home-main">
        <div class="hero">
          <h1>EasyCloud 验证系统</h1>
          <p>高效、安全、易用的应用授权验证平台</p>
          <div class="hero-actions">
            <el-button type="primary" size="large" @click="$router.push('/docs')">查看文档</el-button>
            <el-button size="large" @click="$router.push('/admin/login')">进入后台</el-button>
          </div>
        </div>
        <div class="features">
          <el-row :gutter="40">
            <el-col :span="8">
              <el-card shadow="hover">
                <h3>卡密验证</h3>
                <p>支持时长卡、次数卡、永久卡等多种卡密类型</p>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover">
                <h3>数据加密</h3>
                <p>支持 RC4、AES、Base64 等多种加密方式</p>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover">
                <h3>设备绑定</h3>
                <p>支持设备码绑定、IP验证、解绑管理</p>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
</script>

<style scoped lang="scss">
.home-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.home-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0 40px;

  .logo {
    font-size: 24px;
    font-weight: bold;
    color: #409eff;
  }

  .nav-links {
    display: flex;
    gap: 16px;
    align-items: center;
  }
}

.home-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 20px;
}

.hero {
  text-align: center;
  margin-bottom: 80px;

  h1 {
    font-size: 48px;
    color: #303133;
    margin-bottom: 16px;
  }

  p {
    font-size: 20px;
    color: #606266;
    margin-bottom: 32px;
  }

  .hero-actions {
    display: flex;
    gap: 16px;
    justify-content: center;
  }
}

.features {
  .el-card {
    text-align: center;
    padding: 20px;

    h3 {
      color: #409eff;
      margin-bottom: 12px;
    }

    p {
      color: #909399;
      line-height: 1.6;
    }
  }
}
</style>
