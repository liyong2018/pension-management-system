<template>
  <div class="dictionary-diagnosis">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>字典管理问题诊断</span>
          <el-tag type="info">诊断工具</el-tag>
        </div>
      </template>

      <el-steps :active="currentStep" finish-status="success">
        <el-step title="检查后端连接" />
        <el-step title="检查字典API" />
        <el-step title="检查前端模块" />
        <el-step title="功能测试" />
      </el-steps>

      <div class="diagnosis-content">
        <!-- 步骤1：检查后端连接 -->
        <div v-if="currentStep === 0" class="step-content">
          <h3>步骤1：检查后端连接</h3>
          <el-button type="primary" @click="checkBackend" :loading="checking">
            开始检查后端连接
          </el-button>
          <div v-if="backendResult" class="result-box">
            <el-alert
              :title="backendResult.title"
              :type="backendResult.type"
              :description="backendResult.description"
              show-icon
              :closable="false"
            />
          </div>
        </div>

        <!-- 步骤2：检查字典API -->
        <div v-if="currentStep === 1" class="step-content">
          <h3>步骤2：检查字典API</h3>
          <el-button type="primary" @click="checkDictionaryAPI" :loading="checking">
            检查字典API
          </el-button>
          <div v-if="apiResult" class="result-box">
            <el-alert
              :title="apiResult.title"
              :type="apiResult.type"
              :description="apiResult.description"
              show-icon
              :closable="false"
            />
            <div v-if="apiResult.data" class="api-data">
              <h4>API返回数据示例：</h4>
              <pre>{{ JSON.stringify(apiResult.data, null, 2) }}</pre>
            </div>
          </div>
        </div>

        <!-- 步骤3：检查前端模块 -->
        <div v-if="currentStep === 2" class="step-content">
          <h3>步骤3：检查前端模块</h3>
          <el-button type="primary" @click="checkModules" :loading="checking">
            检查前端模块
          </el-button>
          <div v-if="moduleResult" class="result-box">
            <el-alert
              :title="moduleResult.title"
              :type="moduleResult.type"
              :description="moduleResult.description"
              show-icon
              :closable="false"
            />
            <div v-if="moduleResult.details" class="module-details">
              <h4>模块检查详情：</h4>
              <ul>
                <li v-for="(detail, index) in moduleResult.details" :key="index">
                  <el-tag :type="detail.status === 'success' ? 'success' : 'danger'" size="small">
                    {{ detail.status === 'success' ? '✓' : '✗' }}
                  </el-tag>
                  {{ detail.name }}: {{ detail.message }}
                </li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 步骤4：功能测试 -->
        <div v-if="currentStep === 3" class="step-content">
          <h3>步骤4：功能测试</h3>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-button type="success" @click="testCommunityAPI" :loading="testing">
                测试社区字典
              </el-button>
            </el-col>
            <el-col :span="8">
              <el-button type="success" @click="testPensionTypeAPI" :loading="testing">
                测试养老类型
              </el-button>
            </el-col>
            <el-col :span="8">
              <el-button type="warning" @click="testCreateAPI" :loading="testing">
                测试创建功能
              </el-button>
            </el-col>
          </el-row>
          <div v-if="testResults.length > 0" class="test-results">
            <h4>测试结果：</h4>
            <el-timeline>
              <el-timeline-item
                v-for="(result, index) in testResults"
                :key="index"
                :type="result.success ? 'success' : 'danger'"
                :timestamp="result.timestamp"
              >
                <h5>{{ result.title }}</h5>
                <p>状态: {{ result.success ? '成功' : '失败' }}</p>
                <details v-if="result.data">
                  <summary>查看详细数据</summary>
                  <pre class="test-data">{{ JSON.stringify(result.data, null, 2) }}</pre>
                </details>
              </el-timeline-item>
            </el-timeline>
          </div>
        </div>

        <!-- 导航按钮 -->
        <div class="navigation-buttons">
          <el-button v-if="currentStep > 0" @click="prevStep">上一步</el-button>
          <el-button v-if="currentStep < 3" type="primary" @click="nextStep">下一步</el-button>
          <el-button v-if="currentStep === 3" type="success" @click="goToDictionary">
            前往字典管理
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

const currentStep = ref(0)
const checking = ref(false)
const testing = ref(false)
const backendResult = ref(null)
const apiResult = ref(null)
const moduleResult = ref(null)
const testResults = ref([])

// 检查后端连接
const checkBackend = async () => {
  checking.value = true
  try {
    const response = await axios.get('/api/organizations')
    if (response.status === 200) {
      backendResult.value = {
        title: '后端连接正常',
        type: 'success',
        description: '成功连接到后端服务，基础API工作正常'
      }
    }
  } catch (error) {
    backendResult.value = {
      title: '后端连接失败',
      type: 'error',
      description: `无法连接到后端服务: ${error.message}`
    }
  } finally {
    checking.value = false
  }
}

// 检查字典API
const checkDictionaryAPI = async () => {
  checking.value = true
  try {
    const response = await axios.get('/api/dictionaries/type/community')
    if (response.status === 200 && response.data) {
      apiResult.value = {
        title: '字典API正常',
        type: 'success',
        description: `成功获取到 ${response.data.length} 条社区字典数据`,
        data: response.data.slice(0, 3) // 只显示前3条数据
      }
    }
  } catch (error) {
    apiResult.value = {
      title: '字典API异常',
      type: 'error',
      description: `字典API调用失败: ${error.message}`
    }
  } finally {
    checking.value = false
  }
}

// 检查前端模块
const checkModules = async () => {
  checking.value = true
  const details = []
  
  try {
    // 检查request模块
    try {
      const request = await import('@/utils/request')
      details.push({
        name: 'request工具',
        status: 'success',
        message: '模块加载成功'
      })
    } catch (error) {
      details.push({
        name: 'request工具',
        status: 'error',
        message: `模块加载失败: ${error.message}`
      })
    }

    // 检查dateUtils模块
    try {
      const dateUtils = await import('@/utils/dateUtils')
      details.push({
        name: 'dateUtils工具',
        status: 'success',
        message: '模块加载成功'
      })
    } catch (error) {
      details.push({
        name: 'dateUtils工具',
        status: 'error',
        message: `模块加载失败: ${error.message}`
      })
    }

    // 检查dictionary常量
    try {
      const constants = await import('@/constants/dictionary')
      details.push({
        name: 'dictionary常量',
        status: 'success',
        message: '模块加载成功'
      })
    } catch (error) {
      details.push({
        name: 'dictionary常量',
        status: 'error',
        message: `模块加载失败: ${error.message}`
      })
    }

    // 检查dictionaryApi
    try {
      const api = await import('@/api/dictionary')
      details.push({
        name: 'dictionaryApi',
        status: 'success',
        message: '模块加载成功'
      })
    } catch (error) {
      details.push({
        name: 'dictionaryApi',
        status: 'error',
        message: `模块加载失败: ${error.message}`
      })
    }

    const hasErrors = details.some(d => d.status === 'error')
    moduleResult.value = {
      title: hasErrors ? '前端模块检查发现问题' : '前端模块检查正常',
      type: hasErrors ? 'warning' : 'success',
      description: hasErrors ? '部分模块加载失败，可能影响功能正常使用' : '所有必需的前端模块都加载正常',
      details
    }
  } catch (error) {
    moduleResult.value = {
      title: '前端模块检查失败',
      type: 'error',
      description: `模块检查过程中发生错误: ${error.message}`
    }
  } finally {
    checking.value = false
  }
}

// 测试社区字典API
const testCommunityAPI = async () => {
  testing.value = true
  try {
    const response = await axios.get('/api/dictionaries/type/community')
    testResults.value.unshift({
      title: '社区字典API测试',
      success: true,
      data: response.data,
      timestamp: new Date().toLocaleString()
    })
    ElMessage.success('社区字典API测试成功')
  } catch (error) {
    testResults.value.unshift({
      title: '社区字典API测试',
      success: false,
      data: error.message,
      timestamp: new Date().toLocaleString()
    })
    ElMessage.error('社区字典API测试失败')
  } finally {
    testing.value = false
  }
}

// 测试养老类型API
const testPensionTypeAPI = async () => {
  testing.value = true
  try {
    const response = await axios.get('/api/dictionaries/type/pensionType')
    testResults.value.unshift({
      title: '养老类型API测试',
      success: true,
      data: response.data,
      timestamp: new Date().toLocaleString()
    })
    ElMessage.success('养老类型API测试成功')
  } catch (error) {
    testResults.value.unshift({
      title: '养老类型API测试',
      success: false,
      data: error.message,
      timestamp: new Date().toLocaleString()
    })
    ElMessage.error('养老类型API测试失败')
  } finally {
    testing.value = false
  }
}

// 测试创建API
const testCreateAPI = async () => {
  testing.value = true
  try {
    const testData = {
      dictType: 'community',
      dictCode: 'TEST_DIAGNOSIS',
      dictLabel: '诊断测试社区',
      dictValue: '诊断测试社区',
      sortOrder: 9999,
      status: 'ACTIVE',
      remark: '这是诊断工具创建的测试数据'
    }
    const response = await axios.post('/api/dictionaries', testData)
    testResults.value.unshift({
      title: '创建字典API测试',
      success: true,
      data: response.data,
      timestamp: new Date().toLocaleString()
    })
    ElMessage.success('创建字典API测试成功')
  } catch (error) {
    testResults.value.unshift({
      title: '创建字典API测试',
      success: false,
      data: error.message,
      timestamp: new Date().toLocaleString()
    })
    ElMessage.error('创建字典API测试失败')
  } finally {
    testing.value = false
  }
}

// 导航方法
const nextStep = () => {
  if (currentStep.value < 3) {
    currentStep.value++
  }
}

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

const goToDictionary = () => {
  router.push('/system/dictionaries')
}
</script>

<style scoped>
.dictionary-diagnosis {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.diagnosis-content {
  margin-top: 30px;
}

.step-content {
  min-height: 300px;
  padding: 20px 0;
}

.result-box {
  margin-top: 20px;
}

.api-data, .test-data {
  background-color: #f5f5f5;
  padding: 15px;
  border-radius: 4px;
  font-size: 12px;
  max-height: 300px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
}

.module-details ul {
  list-style: none;
  padding: 0;
}

.module-details li {
  margin: 10px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.test-results {
  margin-top: 20px;
}

.navigation-buttons {
  margin-top: 30px;
  text-align: center;
  border-top: 1px solid #eee;
  padding-top: 20px;
}

:deep(.el-steps) {
  margin-bottom: 20px;
}

:deep(.el-timeline-item__content) {
  padding-bottom: 20px;
}
</style> 