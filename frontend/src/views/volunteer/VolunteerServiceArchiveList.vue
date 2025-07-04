<template>
  <div class="volunteer-service-archive">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span>志愿者服务档案管理</span>
          <div class="header-buttons">
            <el-button type="primary" :icon="Refresh" @click="loadServiceArchives">刷新</el-button>
          </div>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" inline>
          <el-form-item label="志愿者姓名">
            <el-input
              v-model="searchForm.volunteerName"
              placeholder="请输入志愿者姓名"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="服务内容">
            <el-input
              v-model="searchForm.serviceContent"
              placeholder="请输入服务内容"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="服务类型">
            <el-select
              v-model="searchForm.serviceType"
              placeholder="请选择服务类型"
              clearable
              style="width: 150px"
            >
              <el-option label="生活照料" value="生活照料" />
              <el-option label="医疗护理" value="医疗护理" />
              <el-option label="精神慰藉" value="精神慰藉" />
              <el-option label="文体娱乐" value="文体娱乐" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          <el-form-item label="服务时间">
            <el-date-picker
              v-model="searchForm.serviceTimeRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 350px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 统计区域 -->
      <div class="stats-area" v-if="stats">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-number">{{ stats.totalRecords || 0 }}</div>
              <div class="stat-label">总服务记录</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-number">{{ stats.totalVolunteers || 0 }}</div>
              <div class="stat-label">参与志愿者</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-number">{{ stats.totalHours || 0 }}</div>
              <div class="stat-label">总服务时长(小时)</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-number">{{ stats.totalElderlyServed || 0 }}</div>
              <div class="stat-label">服务老人数</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="serviceArchives"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="serviceProviderName" label="志愿者姓名" width="120" />
        <el-table-column prop="elderlyName" label="服务对象" width="120" />
        <el-table-column prop="serviceContent" label="服务内容" width="150" show-overflow-tooltip />
        <el-table-column prop="serviceType" label="服务类型" width="100" />
        <el-table-column prop="serviceTime" label="服务时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.serviceTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="serviceAddress" label="服务地点" width="450" show-overflow-tooltip />
        <el-table-column prop="serviceDuration" label="服务时长" width="100">
          <template #default="scope">
            {{ scope.row.serviceDuration }}小时
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="evaluationScore" label="评价" width="100">
          <template #default="scope">
            <div v-if="scope.row.evaluationScore" class="evaluation">
              <el-rate
                v-model="scope.row.evaluationScore"
                disabled
                show-score
                text-color="#ff9900"
                score-template="{value}分"
              />
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button type="text" size="small" @click="showDetailDialog(scope.row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 服务档案详情对话框 -->
    <ServiceArchiveDetailDialog
      v-model="detailDialogVisible"
      :service-record="selectedRecord"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, View } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'
import ServiceArchiveDetailDialog from './components/ServiceArchiveDetailDialog.vue'
import { serviceRecordApi } from '@/api/serviceRecord'

// 路由
const route = useRoute()

// 响应式数据
const loading = ref(false)
const serviceArchives = ref([])
const stats = ref(null)
const selectedRecord = ref(null)

// 搜索表单
const searchForm = reactive({
  volunteerName: '',
  serviceContent: '',
  serviceType: '',
  serviceTimeRange: []
})

// 分页信息
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

// 对话框显示状态
const detailDialogVisible = ref(false)

// 加载服务档案列表
const loadServiceArchives = async () => {
  loading.value = true
  try {
    const params = {
      serviceProviderType: '志愿者',
      serviceProviderName: searchForm.volunteerName,
      serviceContent: searchForm.serviceContent,
      serviceType: searchForm.serviceType,
      pageNum: pagination.page,
      pageSize: pagination.pageSize
    }

    // 处理时间范围
    if (searchForm.serviceTimeRange && searchForm.serviceTimeRange.length === 2) {
      params.startTime = searchForm.serviceTimeRange[0]
      params.endTime = searchForm.serviceTimeRange[1]
    }

    const response = await serviceRecordApi.getList(params)
    console.log('API响应数据:', response)
    
    // 处理不同的响应数据结构
    let data = response
    if (response.data) {
      data = response.data
    }
    
    // 检查数据结构
    if (data && Array.isArray(data.list)) {
      serviceArchives.value = data.list
      pagination.total = data.total || 0
    } else if (data && Array.isArray(data)) {
      // 如果直接返回数组
      serviceArchives.value = data
      pagination.total = data.length
    } else if (Array.isArray(response)) {
      // 如果响应本身就是数组
      serviceArchives.value = response
      pagination.total = response.length
    } else {
      console.warn('未知的响应数据结构:', response)
      serviceArchives.value = []
      pagination.total = 0
    }
    
    console.log('处理后的服务档案数据:', serviceArchives.value)
    console.log('总数:', pagination.total)
    
    // 计算统计数据
    calculateStats()
  } catch (error) {
    console.error('加载服务档案失败:', error)
    ElMessage.error('加载服务档案失败')
  } finally {
    loading.value = false
  }
}

// 计算统计数据
const calculateStats = () => {
  const totalRecords = pagination.total
  const volunteerSet = new Set()
  const elderlySet = new Set()
  let totalHours = 0

  serviceArchives.value.forEach(record => {
    if (record.serviceProviderName) {
      volunteerSet.add(record.serviceProviderName)
    }
    if (record.elderlyName) {
      elderlySet.add(record.elderlyName)
    }
    if (record.serviceDuration) {
      totalHours += parseFloat(record.serviceDuration) || 0
    }
  })

  stats.value = {
    totalRecords,
    totalVolunteers: volunteerSet.size,
    totalHours: totalHours.toFixed(1),
    totalElderlyServed: elderlySet.size
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadServiceArchives()
}

// 重置
const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    if (key === 'serviceTimeRange') {
      searchForm[key] = []
    } else {
      searchForm[key] = ''
    }
  })
  pagination.page = 1
  loadServiceArchives()
}

// 显示详情对话框
const showDetailDialog = (record) => {
  selectedRecord.value = record
  detailDialogVisible.value = true
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.page = 1
  loadServiceArchives()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadServiceArchives()
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    '待处理': 'warning',
    '进行中': 'primary',
    '已完成': 'success',
    '已评价': 'info'
  }
  return statusMap[status] || 'info'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 组件挂载时加载数据
onMounted(() => {
  // 处理URL查询参数
  if (route.query.volunteerId) {
    searchForm.providerName = route.query.volunteerName || ''
    // 注意：这里设置providerId用于后端查询
    searchForm.providerId = route.query.volunteerId
  }
  
  loadServiceArchives()
})
</script>

<style scoped>
.volunteer-service-archive {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.search-area {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.stats-area {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f0f9ff;
  border-radius: 4px;
}

.stat-card {
  text-align: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.evaluation {
  display: flex;
  align-items: center;
}
</style>