<template>
  <el-dialog
    v-model="visible"
    :title="`${volunteer?.name || '志愿者'} - 服务记录`"
    width="1000px"
    destroy-on-close
  >
    <div v-if="volunteer" class="service-records-container">
      <!-- 志愿者基本信息 -->
      <el-card class="volunteer-info-card" shadow="never">
        <div class="volunteer-header">
          <div class="volunteer-avatar">
            <el-avatar :size="60" :src="volunteer.avatar">
              {{ volunteer.name?.charAt(0) }}
            </el-avatar>
          </div>
          <div class="volunteer-details">
            <h3>{{ volunteer.name }}</h3>
            <p class="volunteer-meta">
              <el-tag type="info" size="small">{{ volunteer.occupation || '未知职业' }}</el-tag>
              <span class="volunteer-phone">{{ volunteer.phone }}</span>
            </p>
            <p class="volunteer-stats">
              <span class="stat-item">
                <el-icon><Clock /></el-icon>
                服务时长: {{ volunteer.totalServiceHours || 0 }}小时
              </span>
              <span class="stat-item">
                <el-icon><Star /></el-icon>
                积分: {{ volunteer.points || 0 }}
              </span>
            </p>
          </div>
        </div>
      </el-card>

      <!-- 统计图表 -->
      <el-row :gutter="20" class="stats-section">
        <el-col :span="12">
          <el-card title="服务类型分布" shadow="never">
            <div class="chart-container">
              <canvas ref="serviceTypeChart" width="400" height="300"></canvas>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card title="服务老人分布" shadow="never">
            <div class="chart-container">
              <canvas ref="elderlyChart" width="400" height="300"></canvas>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 服务记录列表 -->
      <el-card class="records-list-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>服务记录列表</span>
            <div class="header-actions">
              <el-input
                v-model="searchQuery"
                placeholder="搜索服务内容或老人姓名"
                prefix-icon="Search"
                style="width: 200px"
                clearable
                @input="handleSearch"
              />
            </div>
          </div>
        </template>

        <el-table
          :data="filteredRecords"
          v-loading="loading"
          border
          stripe
          style="width: 100%"
        >
          <el-table-column prop="elderlyName" label="老人姓名" width="100" />
          <el-table-column prop="serviceContent" label="服务内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="serviceTime" label="服务时间" width="160">
            <template #default="{ row }">
              {{ formatDateTime(row.serviceTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="serviceAddress" label="服务地址" min-width="150" show-overflow-tooltip />
          <el-table-column prop="workOrderPrice" label="工单价格" width="100">
            <template #default="{ row }">
              <span v-if="row.workOrderPrice">￥{{ row.workOrderPrice }}</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusTag(row.status)">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="evaluationScore" label="评价" width="120">
            <template #default="{ row }">
              <div v-if="row.evaluationScore" class="evaluation">
                <el-rate v-model="row.evaluationScore" disabled show-score text-color="#ff9900" size="small"></el-rate>
              </div>
              <span v-else>-</span>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container" v-if="total > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Clock, Star, Search } from '@element-plus/icons-vue'
import { serviceRecordApi } from '@/api/serviceRecord'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  volunteer: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue'])

// 响应式数据
const loading = ref(false)
const serviceRecords = ref([])
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 图表引用
const serviceTypeChart = ref()
const elderlyChart = ref()

// 对话框显示控制
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 过滤后的记录
const filteredRecords = computed(() => {
  if (!searchQuery.value) return serviceRecords.value
  
  const query = searchQuery.value.toLowerCase()
  return serviceRecords.value.filter(record => 
    record.elderlyName?.toLowerCase().includes(query) ||
    record.serviceContent?.toLowerCase().includes(query)
  )
})

// 加载志愿者的服务记录
const loadServiceRecords = async () => {
  if (!props.volunteer?.id) return
  
  loading.value = true
  try {
    console.log('Loading service records for volunteer:', props.volunteer.id)
    
    // 获取该志愿者的所有服务记录
    const response = await serviceRecordApi.getByVolunteerId(props.volunteer.id, {
      pageNum: currentPage.value,
      pageSize: pageSize.value * 10 // 获取更多数据用于图表
    })
    
    console.log('API Response:', response)
    
    // 根据后端PageInfo结构处理响应
    if (response && response.list) {
      // 手动过滤志愿者的记录（因为后端过滤可能有问题）
      const volunteerRecords = response.list.filter(record => 
        record.serviceProviderType === '志愿者' && 
        record.serviceProviderId === props.volunteer.id
      )
      
      serviceRecords.value = volunteerRecords
      total.value = volunteerRecords.length
      
      console.log('Filtered volunteer records:', volunteerRecords)
      
      // 生成图表
      nextTick(() => {
        generateCharts()
      })
    } else {
      ElMessage.error('加载服务记录失败：响应格式不正确')
      console.error('Unexpected response format:', response)
    }
  } catch (error) {
    console.error('加载服务记录失败:', error)
    ElMessage.error('加载服务记录失败')
  } finally {
    loading.value = false
  }
}

// 生成图表
const generateCharts = () => {
  generateServiceTypeChart()
  generateElderlyChart()
}

// 生成服务类型分布图表
const generateServiceTypeChart = () => {
  if (!serviceTypeChart.value) return
  
  const ctx = serviceTypeChart.value.getContext('2d')
  
  // 统计服务类型
  const serviceTypes = {}
  serviceRecords.value.forEach(record => {
    const type = record.serviceContent || '其他'
    serviceTypes[type] = (serviceTypes[type] || 0) + 1
  })
  
  const data = Object.entries(serviceTypes).map(([type, count]) => ({ type, count }))
  
  // 简单的饼图绘制
  drawPieChart(ctx, data, '服务类型分布')
}

// 生成服务老人分布图表
const generateElderlyChart = () => {
  if (!elderlyChart.value) return
  
  const ctx = elderlyChart.value.getContext('2d')
  
  // 统计服务老人
  const elderlyStats = {}
  serviceRecords.value.forEach(record => {
    const elderly = record.elderlyName || '未知'
    elderlyStats[elderly] = (elderlyStats[elderly] || 0) + 1
  })
  
  const data = Object.entries(elderlyStats).map(([name, count]) => ({ type: name, count }))
  
  // 简单的柱状图绘制
  drawBarChart(ctx, data, '服务老人分布')
}

// 绘制饼图
const drawPieChart = (ctx, data, title) => {
  const canvas = ctx.canvas
  const centerX = canvas.width / 2
  const centerY = canvas.height / 2
  const radius = Math.min(centerX, centerY) - 50
  
  ctx.clearRect(0, 0, canvas.width, canvas.height)
  
  if (data.length === 0) {
    ctx.fillStyle = '#666'
    ctx.font = '16px Arial'
    ctx.textAlign = 'center'
    ctx.fillText('暂无数据', centerX, centerY)
    return
  }
  
  const total = data.reduce((sum, item) => sum + item.count, 0)
  const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#c71585', '#ff8c00', '#9acd32']
  
  let currentAngle = 0
  
  data.forEach((item, index) => {
    const angle = (item.count / total) * 2 * Math.PI
    
    // 绘制扇形
    ctx.beginPath()
    ctx.arc(centerX, centerY, radius, currentAngle, currentAngle + angle)
    ctx.lineTo(centerX, centerY)
    ctx.fillStyle = colors[index % colors.length]
    ctx.fill()
    
    // 绘制标签
    const labelAngle = currentAngle + angle / 2
    const labelX = centerX + Math.cos(labelAngle) * (radius + 20)
    const labelY = centerY + Math.sin(labelAngle) * (radius + 20)
    
    ctx.fillStyle = '#333'
    ctx.font = '12px Arial'
    ctx.textAlign = 'center'
    ctx.fillText(`${item.type}(${item.count})`, labelX, labelY)
    
    currentAngle += angle
  })
  
  // 绘制标题
  ctx.fillStyle = '#333'
  ctx.font = 'bold 16px Arial'
  ctx.textAlign = 'center'
  ctx.fillText(title, centerX, 30)
}

// 绘制柱状图
const drawBarChart = (ctx, data, title) => {
  const canvas = ctx.canvas
  const padding = 50
  const chartWidth = canvas.width - padding * 2
  const chartHeight = canvas.height - padding * 2 - 30 // 留出标题空间
  
  ctx.clearRect(0, 0, canvas.width, canvas.height)
  
  if (data.length === 0) {
    ctx.fillStyle = '#666'
    ctx.font = '16px Arial'
    ctx.textAlign = 'center'
    ctx.fillText('暂无数据', canvas.width / 2, canvas.height / 2)
    return
  }
  
  const maxCount = Math.max(...data.map(item => item.count))
  const barWidth = chartWidth / data.length - 10
  const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399']
  
  data.forEach((item, index) => {
    const barHeight = (item.count / maxCount) * chartHeight
    const x = padding + index * (barWidth + 10)
    const y = padding + 30 + chartHeight - barHeight
    
    // 绘制柱子
    ctx.fillStyle = colors[index % colors.length]
    ctx.fillRect(x, y, barWidth, barHeight)
    
    // 绘制标签
    ctx.fillStyle = '#333'
    ctx.font = '12px Arial'
    ctx.textAlign = 'center'
    ctx.fillText(item.type, x + barWidth / 2, y + barHeight + 15)
    ctx.fillText(item.count.toString(), x + barWidth / 2, y - 5)
  })
  
  // 绘制标题
  ctx.fillStyle = '#333'
  ctx.font = 'bold 16px Arial'
  ctx.textAlign = 'center'
  ctx.fillText(title, canvas.width / 2, 25)
}

// 搜索处理
const handleSearch = () => {
  // 搜索由计算属性处理
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadServiceRecords()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadServiceRecords()
}

// 状态标签
const getStatusTag = (status) => {
  const map = {
    '待处理': 'warning',
    '进行中': 'primary',
    '已完成': 'success',
    '已评价': 'info'
  }
  return map[status] || 'info'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 监听对话框打开
watch(visible, (newVal) => {
  if (newVal && props.volunteer) {
    searchQuery.value = ''
    currentPage.value = 1
    loadServiceRecords()
  }
})
</script>

<style scoped>
.service-records-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.volunteer-info-card {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.volunteer-header {
  display: flex;
  align-items: center;
  gap: 20px;
}

.volunteer-details h3 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 20px;
}

.volunteer-meta {
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.volunteer-phone {
  color: #606266;
  font-size: 14px;
}

.volunteer-stats {
  margin: 0;
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #606266;
  font-size: 14px;
}

.stats-section {
  margin: 20px 0;
}

.chart-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.records-list-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.evaluation {
  display: flex;
  align-items: center;
}

.dialog-footer {
  text-align: right;
}
</style> 