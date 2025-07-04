<template>
  <el-dialog
    v-model="visible"
    title="服务档案详情"
    width="800px"
    :before-close="handleClose"
  >
    <div v-if="serviceRecord" class="service-detail">
      <!-- 基本信息 -->
      <el-card class="detail-card" shadow="never">
        <template #header>
          <span class="card-title">基本信息</span>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="志愿者姓名">
            {{ serviceRecord.serviceProviderName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="服务对象">
            {{ serviceRecord.elderlyName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="服务内容">
            {{ serviceRecord.serviceContent || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="服务类型">
            {{ serviceRecord.serviceType || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="服务时间">
            {{ formatDateTime(serviceRecord.serviceTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="服务时长">
            {{ serviceRecord.serviceDuration }}小时
          </el-descriptions-item>
          <el-descriptions-item label="服务地点" :span="2">
            {{ serviceRecord.serviceAddress || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="服务费用">
            {{ serviceRecord.workOrderPrice ? `¥${serviceRecord.workOrderPrice}` : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="服务状态">
            <el-tag :type="getStatusType(serviceRecord.status)">{{ serviceRecord.status }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 评价信息 -->
      <el-card class="detail-card" shadow="never" v-if="serviceRecord.evaluationScore || serviceRecord.evaluationComment">
        <template #header>
          <span class="card-title">评价信息</span>
        </template>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="评价分数" v-if="serviceRecord.evaluationScore">
            <div class="evaluation-score">
              <el-rate
                v-model="serviceRecord.evaluationScore"
                disabled
                show-score
                text-color="#ff9900"
                score-template="{value}分"
              />
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="评价内容" v-if="serviceRecord.evaluationComment">
            <div class="evaluation-comment">
              {{ serviceRecord.evaluationComment }}
            </div>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 时间信息 -->
      <el-card class="detail-card" shadow="never">
        <template #header>
          <span class="card-title">时间信息</span>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="创建时间">
            {{ formatDateTime(serviceRecord.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间">
            {{ formatDateTime(serviceRecord.updateTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 服务档案统计 -->
      <el-card class="detail-card" shadow="never" v-if="volunteerStats">
        <template #header>
          <span class="card-title">志愿者服务统计</span>
        </template>
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ volunteerStats.totalServices || 0 }}</div>
              <div class="stat-label">总服务次数</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ volunteerStats.totalHours || 0 }}</div>
              <div class="stat-label">总服务时长(小时)</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ volunteerStats.averageScore || 0 }}</div>
              <div class="stat-label">平均评分</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ volunteerStats.elderlyServed || 0 }}</div>
              <div class="stat-label">服务老人数</div>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="exportRecord" v-if="serviceRecord">
          导出档案
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { serviceRecordApi } from '@/api/serviceRecord'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  serviceRecord: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue'])

// 响应式数据
const volunteerStats = ref(null)

// 计算属性
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 关闭对话框
const handleClose = () => {
  visible.value = false
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

// 加载志愿者统计数据
const loadVolunteerStats = async (volunteerId) => {
  if (!volunteerId) return
  
  try {
    const response = await serviceRecordApi.getList({
      serviceProviderId: volunteerId,
      serviceProviderType: '志愿者',
      pageNum: 1,
      pageSize: 1000 // 获取所有记录用于统计
    })
    
    if (response.data && response.data.list) {
      const records = response.data.list
      const totalServices = records.length
      const totalHours = records.reduce((sum, record) => {
        return sum + (parseFloat(record.serviceDuration) || 0)
      }, 0)
      const scores = records.filter(record => record.evaluationScore).map(record => record.evaluationScore)
      const averageScore = scores.length > 0 ? (scores.reduce((sum, score) => sum + score, 0) / scores.length).toFixed(1) : 0
      const elderlySet = new Set(records.map(record => record.elderlyName).filter(name => name))
      
      volunteerStats.value = {
        totalServices,
        totalHours: totalHours.toFixed(1),
        averageScore,
        elderlyServed: elderlySet.size
      }
    }
  } catch (error) {
    console.error('加载志愿者统计数据失败:', error)
  }
}

// 导出服务档案
const exportRecord = () => {
  if (!props.serviceRecord) return
  
  const record = props.serviceRecord
  const content = `
服务档案详情

基本信息：
志愿者姓名：${record.serviceProviderName || '-'}
服务对象：${record.elderlyName || '-'}
服务内容：${record.serviceContent || '-'}
服务类型：${record.serviceType || '-'}
服务时间：${formatDateTime(record.serviceTime)}
服务时长：${record.serviceDuration}小时
服务地点：${record.serviceAddress || '-'}
服务费用：${record.workOrderPrice ? `¥${record.workOrderPrice}` : '-'}
服务状态：${record.status}

评价信息：
评价分数：${record.evaluationScore ? `${record.evaluationScore}分` : '-'}
评价内容：${record.evaluationComment || '-'}

时间信息：
创建时间：${formatDateTime(record.createTime)}
更新时间：${formatDateTime(record.updateTime)}
  `.trim()
  
  const blob = new Blob([content], { type: 'text/plain;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `服务档案_${record.serviceProviderName}_${formatDateTime(record.serviceTime).replace(/[\s:]/g, '_')}.txt`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
  
  ElMessage.success('服务档案导出成功')
}

// 监听服务记录变化
watch(() => props.serviceRecord, (newRecord) => {
  if (newRecord && newRecord.serviceProviderId) {
    loadVolunteerStats(newRecord.serviceProviderId)
  }
}, { immediate: true })
</script>

<style scoped>
.service-detail {
  max-height: 600px;
  overflow-y: auto;
}

.detail-card {
  margin-bottom: 20px;
}

.detail-card:last-child {
  margin-bottom: 0;
}

.card-title {
  font-weight: bold;
  color: #303133;
}

.evaluation-score {
  display: flex;
  align-items: center;
}

.evaluation-comment {
  line-height: 1.6;
  color: #606266;
}

.stat-item {
  text-align: center;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.dialog-footer {
  text-align: right;
}
</style>