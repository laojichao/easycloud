<template>
  <div class="dashboard-page">
    <h2>仪表盘</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>应用总数</template>
          <div class="stat-value">{{ stats.appCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>卡密总数</template>
          <div class="stat-value">{{ stats.kmCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>文件总数</template>
          <div class="stat-value">{{ stats.fileCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>今日调用</template>
          <div class="stat-value">{{ stats.todayCalls }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAppList, getKmList, getFileList } from '@/api/admin'

const stats = ref({
  appCount: 0,
  kmCount: 0,
  fileCount: 0,
  todayCalls: 0
})

onMounted(async () => {
  try {
    const [apps, kms, files] = await Promise.all([
      getAppList({ page: 1, size: 1 }),
      getKmList({ page: 1, size: 1 }),
      getFileList({ page: 1, size: 1 })
    ])
    stats.value.appCount = apps.data?.total || 0
    stats.value.kmCount = kms.data?.total || 0
    stats.value.fileCount = files.data?.total || 0
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
})
</script>

<style scoped lang="scss">
.dashboard-page {
  padding: 20px;
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  text-align: center;
}
</style>
