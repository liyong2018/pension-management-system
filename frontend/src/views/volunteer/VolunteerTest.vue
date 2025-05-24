<template>
  <div class="volunteer-test">
    <el-card>
      <template #header>
        <span>志愿者管理测试页面</span>
      </template>
      
      <el-button @click="testGetList" :loading="loading">测试获取列表</el-button>
      <el-button @click="testGetStats" :loading="loading">测试获取统计</el-button>
      
      <div v-if="result" style="margin-top: 20px;">
        <h3>测试结果：</h3>
        <pre>{{ JSON.stringify(result, null, 2) }}</pre>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { volunteerApi } from '@/api/volunteer'

const loading = ref(false)
const result = ref(null)

const testGetList = async () => {
  loading.value = true
  try {
    const response = await volunteerApi.getVolunteerList({ page: 1, pageSize: 10 })
    result.value = response
    ElMessage.success('API调用成功')
  } catch (error) {
    result.value = { error: error.message }
    ElMessage.error('API调用失败')
  } finally {
    loading.value = false
  }
}

const testGetStats = async () => {
  loading.value = true
  try {
    const response = await volunteerApi.getVolunteerStats()
    result.value = response
    ElMessage.success('API调用成功')
  } catch (error) {
    result.value = { error: error.message }
    ElMessage.error('API调用失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
pre {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
}
</style> 