<template>
  <div class="dictionary-test">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>字典管理功能测试</span>
          <el-tag type="warning">测试模式</el-tag>
        </div>
      </template>

      <el-alert
        title="测试说明"
        type="info"
        :closable="false"
        show-icon
      >
        <p>此页面用于测试字典管理功能的前端界面，使用模拟数据。</p>
        <p>实际使用时需要确保：</p>
        <ul>
          <li>1. 后端服务正常运行</li>
          <li>2. 数据库中存在dictionary表</li>
          <li>3. 字典初始数据已插入</li>
        </ul>
      </el-alert>

      <div class="test-section">
        <h3>API连接测试</h3>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-button type="primary" @click="testCommunityAPI" :loading="testingCommunity">
              测试社区字典API
            </el-button>
          </el-col>
          <el-col :span="8">
            <el-button type="primary" @click="testPensionTypeAPI" :loading="testingPensionType">
              测试养老类型API
            </el-button>
          </el-col>
          <el-col :span="8">
            <el-button type="success" @click="testCreateAPI" :loading="testingCreate">
              测试创建API
            </el-button>
          </el-col>
        </el-row>
      </div>

      <div class="test-results" v-if="testResults.length > 0">
        <h3>测试结果</h3>
        <el-timeline>
          <el-timeline-item
            v-for="(result, index) in testResults"
            :key="index"
            :type="result.success ? 'success' : 'danger'"
            :timestamp="result.timestamp"
          >
            <el-card>
              <h4>{{ result.title }}</h4>
              <p><strong>状态:</strong> {{ result.success ? '成功' : '失败' }}</p>
              <p><strong>响应:</strong></p>
              <pre class="response-data">{{ result.response }}</pre>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>

      <div class="mock-data-section">
        <h3>模拟数据预览</h3>
        <el-tabs type="border-card">
          <el-tab-pane label="所属社区">
            <el-table :data="mockCommunityData" stripe border>
              <el-table-column prop="dictCode" label="字典编码" width="150" />
              <el-table-column prop="dictLabel" label="字典标签" width="150" />
              <el-table-column prop="dictValue" label="字典值" />
              <el-table-column prop="sortOrder" label="排序" width="80" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'danger'">
                    {{ scope.row.status === 'ACTIVE' ? '启用' : '禁用' }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="养老类型">
            <el-table :data="mockPensionTypeData" stripe border>
              <el-table-column prop="dictCode" label="字典编码" width="150" />
              <el-table-column prop="dictLabel" label="字典标签" width="150" />
              <el-table-column prop="dictValue" label="字典值" />
              <el-table-column prop="sortOrder" label="排序" width="80" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'danger'">
                    {{ scope.row.status === 'ACTIVE' ? '启用' : '禁用' }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { dictionaryApi } from '@/api/dictionary'

const testingCommunity = ref(false)
const testingPensionType = ref(false)
const testingCreate = ref(false)
const testResults = ref([])

// 模拟数据
const mockCommunityData = [
  { dictCode: 'CHAOYANGPARK', dictLabel: '朝阳公园社区', dictValue: '朝阳公园社区', sortOrder: 1, status: 'ACTIVE' },
  { dictCode: 'ZHONGGUANCUN', dictLabel: '中关村社区', dictValue: '中关村社区', sortOrder: 2, status: 'ACTIVE' },
  { dictCode: 'XIDAN', dictLabel: '西单社区', dictValue: '西单社区', sortOrder: 3, status: 'ACTIVE' },
  { dictCode: 'WANGFUJING', dictLabel: '王府井社区', dictValue: '王府井社区', sortOrder: 4, status: 'ACTIVE' },
  { dictCode: 'SANLITUN', dictLabel: '三里屯社区', dictValue: '三里屯社区', sortOrder: 5, status: 'ACTIVE' }
]

const mockPensionTypeData = [
  { dictCode: 'HOME_CARE', dictLabel: '居家养老', dictValue: '居家养老', sortOrder: 1, status: 'ACTIVE' },
  { dictCode: 'COMMUNITY_DAY_CARE', dictLabel: '社区养老（日照）', dictValue: '社区养老（日照）', sortOrder: 2, status: 'ACTIVE' },
  { dictCode: 'INSTITUTIONAL_CARE', dictLabel: '机构养老（养老院）', dictValue: '机构养老（养老院）', sortOrder: 3, status: 'ACTIVE' },
  { dictCode: 'MEDICAL_CARE', dictLabel: '医养结合', dictValue: '医养结合', sortOrder: 4, status: 'ACTIVE' },
  { dictCode: 'ASSISTED_LIVING', dictLabel: '辅助生活', dictValue: '辅助生活', sortOrder: 5, status: 'ACTIVE' }
]

// 添加测试结果
const addTestResult = (title, success, response) => {
  testResults.value.unshift({
    title,
    success,
    response: typeof response === 'object' ? JSON.stringify(response, null, 2) : response,
    timestamp: new Date().toLocaleString()
  })
}

// 测试社区字典API
const testCommunityAPI = async () => {
  testingCommunity.value = true
  try {
    const response = await dictionaryApi.getByType('community')
    addTestResult('社区字典API测试', true, response)
    ElMessage.success('社区字典API测试成功')
  } catch (error) {
    addTestResult('社区字典API测试', false, error.message || error)
    ElMessage.error('社区字典API测试失败')
  } finally {
    testingCommunity.value = false
  }
}

// 测试养老类型API
const testPensionTypeAPI = async () => {
  testingPensionType.value = true
  try {
    const response = await dictionaryApi.getByType('pensionType')
    addTestResult('养老类型API测试', true, response)
    ElMessage.success('养老类型API测试成功')
  } catch (error) {
    addTestResult('养老类型API测试', false, error.message || error)
    ElMessage.error('养老类型API测试失败')
  } finally {
    testingPensionType.value = false
  }
}

// 测试创建API
const testCreateAPI = async () => {
  testingCreate.value = true
  try {
    const testData = {
      dictType: 'community',
      dictCode: 'TEST_COMMUNITY',
      dictLabel: '测试社区',
      dictValue: '测试社区',
      sortOrder: 999,
      status: 'ACTIVE',
      remark: '这是一个测试数据'
    }
    const response = await dictionaryApi.create(testData)
    addTestResult('创建字典API测试', true, response)
    ElMessage.success('创建字典API测试成功')
  } catch (error) {
    addTestResult('创建字典API测试', false, error.message || error)
    ElMessage.error('创建字典API测试失败')
  } finally {
    testingCreate.value = false
  }
}
</script>

<style scoped>
.dictionary-test {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.test-section {
  margin: 20px 0;
}

.test-results {
  margin: 20px 0;
}

.mock-data-section {
  margin: 20px 0;
}

.response-data {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  font-size: 12px;
  max-height: 200px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
}

:deep(.el-alert) {
  margin-bottom: 20px;
}

:deep(.el-alert ul) {
  margin: 10px 0 0 20px;
  padding: 0;
}

:deep(.el-alert li) {
  margin: 5px 0;
}

:deep(.el-timeline-item__content) {
  padding-bottom: 20px;
}
</style> 