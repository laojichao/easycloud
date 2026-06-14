<template>
  <div class="docs-page">
    <el-container>
      <el-header class="docs-header">
        <div class="logo" @click="$router.push('/')">EasyCloud</div>
        <el-button @click="$router.push('/')">返回首页</el-button>
      </el-header>
      <el-main class="docs-main">
        <h1>API 接口文档</h1>

        <el-card v-for="api in apiList" :key="api.name" class="api-card">
          <template #header>
            <div class="api-header">
              <el-tag type="success" size="small">POST</el-tag>
              <span class="api-path">/api/legacy?app={appid}&api={{ api.name }}</span>
              <span class="api-title">{{ api.title }}</span>
            </div>
          </template>
          <p class="api-desc">{{ api.desc }}</p>
          <h4>请求参数</h4>
          <el-table :data="api.params" size="small" border>
            <el-table-column prop="name" label="参数名" width="150" />
            <el-table-column prop="type" label="类型" width="100" />
            <el-table-column prop="required" label="必填" width="80">
              <template #default="{ row }">
                <el-tag :type="row.required ? 'danger' : 'info'" size="small">
                  {{ row.required ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="desc" label="说明" />
          </el-table>
          <h4>返回示例</h4>
          <pre class="code-block">{{ api.response }}</pre>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
const apiList = [
  {
    name: 'ini',
    title: '获取应用配置',
    desc: '获取应用的版本、更新信息等基本配置',
    params: [],
    response: JSON.stringify({
      code: 200,
      msg: 'success',
      time: ***REMOVED***0,
      data: {
        version: '1.0.0',
        version_info: '更新说明',
        app_update_show: '更新内容',
        app_update_url: 'https://example.com/download',
        app_update_must: 'n',
        api_total: 1000
      }
    }, null, 2)
  },
  {
    name: 'notice',
    title: '获取公告',
    desc: '获取应用公告内容',
    params: [],
    response: JSON.stringify({
      code: 200,
      msg: 'success',
      time: ***REMOVED***0,
      data: { app_gg: '公告内容' }
    }, null, 2)
  },
  {
    name: 'getfile',
    title: '获取文件',
    desc: '获取应用下的文件列表',
    params: [
      { name: 'id', type: 'number', required: false, desc: '文件ID（可选，不传返回全部）' }
    ],
    response: JSON.stringify({
      code: 200,
      msg: 'success',
      time: ***REMOVED***0,
      data: [
        { file_url: 'https://example.com/file.zip', date: '2024-01-01', note: '文件说明' }
      ]
    }, null, 2)
  },
  {
    name: 'kmlogon',
    title: '卡密登录',
    desc: '使用卡密进行登录验证，支持时长卡和次数卡',
    params: [
      { name: 'kami', type: 'string', required: true, desc: '卡密内容' },
      { name: 'markcode', type: 'string', required: true, desc: '设备机器码' }
    ],
    response: JSON.stringify({
      code: 200,
      msg: 'success',
      time: ***REMOVED***0,
      data: { kami: 'xxxx-xxxx-xxxx', vip: '***REMOVED***0' }
    }, null, 2)
  },
  {
    name: 'kmunmachine',
    title: '卡密解绑',
    desc: '解绑卡密与设备的绑定关系',
    params: [
      { name: 'kami', type: 'string', required: true, desc: '卡密内容' },
      { name: 'markcode', type: 'string', required: true, desc: '设备机器码' }
    ],
    response: JSON.stringify({
      code: 200,
      msg: '卡密解绑成功',
      time: ***REMOVED***0
    }, null, 2)
  }
]
</script>

<style scoped lang="scss">
.docs-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.docs-header {
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
    cursor: pointer;
  }
}

.docs-main {
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px 20px;

  h1 {
    margin-bottom: 30px;
    color: #303133;
  }
}

.api-card {
  margin-bottom: 20px;

  .api-header {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .api-path {
    font-family: monospace;
    color: #606266;
  }

  .api-title {
    font-weight: bold;
    color: #303133;
  }

  .api-desc {
    color: #606266;
    margin-bottom: 16px;
  }

  h4 {
    margin: 16px 0 8px;
    color: #303133;
  }
}

.code-block {
  background: #1e1e1e;
  color: #d4d4d4;
  padding: 16px;
  border-radius: 4px;
  font-size: 13px;
  overflow-x: auto;
  line-height: 1.5;
}
</style>
