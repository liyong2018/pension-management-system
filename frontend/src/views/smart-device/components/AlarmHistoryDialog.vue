<template>
  <el-dialog
    v-model="visible"
    title="设备告警历史"
    width="90%"
    @close="handleClose"
  >
    <div class="device-info">
      <el-descriptions :column="3" border>
        <el-descriptions-item label="设备编号">{{ device?.deviceCode }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ device?.deviceName }}</el-descriptions-item>
        <el-descriptions-item label="设备状态">
          <el-tag :type="getStatusTag(device?.deviceStatus)">{{ device?.deviceStatus }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="auto" :inline="true">
        <el-form-item label="告警类型">
          <el-select v-model="searchForm.alarmType" placeholder="请选择告警类型" clearable>
            <el-option label="设备故障" value="设备故障"></el-option>
            <el-option label="数据异常" value="数据异常"></el-option>
            <el-option label="低电量" value="低电量"></el-option>
            <el-option label="网络断连" value="网络断连"></el-option>
            <el-option label="健康异常" value="健康异常"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="告警级别">
          <el-select v-model="searchForm.alarmLevel" placeholder="请选择告警级别" clearable>
            <el-option label="严重" value="严重"></el-option>
            <el-option label="警告" value="警告"></el-option>
            <el-option label="提醒" value="提醒"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="searchForm.processStatus" placeholder="请选择处理状态" clearable>
            <el-option label="未处理" value="未处理"></el-option>
            <el-option label="处理中" value="处理中"></el-option>
            <el-option label="已处理" value="已处理"></el-option>
            <el-option label="已忽略" value="已忽略"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleCreateAlarm">新增告警</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="alarmList"
      border
      style="margin-top: 20px;"
    >
      <el-table-column prop="alarmType" label="告警类型" width="120">
        <template #default="{ row }">
          <el-tag :type="getAlarmTypeTag(row.alarmType)">{{ row.alarmType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="alarmLevel" label="告警级别" width="100">
        <template #default="{ row }">
          <el-tag :type="getAlarmLevelTag(row.alarmLevel)">{{ row.alarmLevel }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="alarmContent" label="告警内容" min-width="200"></el-table-column>
      <el-table-column prop="alarmTime" label="告警时间" width="160">
        <template #default="{ row }">
          {{ formatDateTime(row.alarmTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="processStatus" label="处理状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getProcessStatusTag(row.processStatus)">{{ row.processStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="processPerson" label="处理人员" width="120"></el-table-column>
      <el-table-column prop="processTime" label="处理时间" width="160">
        <template #default="{ row }">
          {{ formatDateTime(row.processTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleViewAlarm(row)">查看</el-button>
          <el-button 
            v-if="row.processStatus === '未处理' || row.processStatus === '处理中'"
            type="warning" 
            link 
            @click="handleProcessAlarm(row)"
          >
            处理
          </el-button>
          <el-button type="danger" link @click="handleDeleteAlarm(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      ></el-pagination>
    </div>

    <!-- 告警详情/处理对话框 -->
    <alarm-detail-dialog
      v-if="alarmDetailVisible"
      v-model="alarmDetailVisible"
      :mode="alarmMode"
      :alarm="selectedAlarm"
      :device="device"
      @success="handleAlarmSuccess"
    ></alarm-detail-dialog>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deviceAlarmApi } from '@/api/deviceAlarm'
import AlarmDetailDialog from './AlarmDetailDialog.vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  device: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:modelValue'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 数据列表
const alarmList = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 搜索表单
const searchForm = ref({
  alarmType: '',
  alarmLevel: '',
  processStatus: ''
})

// 告警详情对话框
const alarmDetailVisible = ref(false)
const alarmMode = ref('view')
const selectedAlarm = ref(null)

// 获取告警列表
const fetchAlarmList = async () => {
  if (!props.device?.id) return
  
  try {
    loading.value = true
    const params = {
      deviceId: props.device.id,
      page: currentPage.value,
      size: pageSize.value,
      ...searchForm.value
    }
    const response = await deviceAlarmApi.getList(params)
    alarmList.value = response.list
    total.value = response.total
  } catch (error) {
    ElMessage.error('获取告警列表失败')
    console.error('获取告警列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索和重置
const handleSearch = () => {
  currentPage.value = 1
  fetchAlarmList()
}

const handleReset = () => {
  searchForm.value = {
    alarmType: '',
    alarmLevel: '',
    processStatus: ''
  }
  currentPage.value = 1
  fetchAlarmList()
}

// 分页操作
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchAlarmList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchAlarmList()
}

// 标签类型
const getStatusTag = (status) => {
  const map = {
    '在线': 'success',
    '离线': 'info',
    '故障': 'danger',
    '维护中': 'warning'
  }
  return map[status] || ''
}

const getAlarmTypeTag = (type) => {
  const map = {
    '设备故障': 'danger',
    '数据异常': 'warning',
    '低电量': 'warning',
    '网络断连': 'info',
    '健康异常': 'danger'
  }
  return map[type] || ''
}

const getAlarmLevelTag = (level) => {
  const map = {
    '严重': 'danger',
    '警告': 'warning',
    '提醒': 'info'
  }
  return map[level] || ''
}

const getProcessStatusTag = (status) => {
  const map = {
    '未处理': 'danger',
    '处理中': 'warning',
    '已处理': 'success',
    '已忽略': 'info'
  }
  return map[status] || ''
}

// 时间格式化
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 告警操作
const handleCreateAlarm = () => {
  alarmMode.value = 'add'
  selectedAlarm.value = null
  alarmDetailVisible.value = true
}

const handleViewAlarm = (alarm) => {
  alarmMode.value = 'view'
  selectedAlarm.value = alarm
  alarmDetailVisible.value = true
}

const handleProcessAlarm = (alarm) => {
  alarmMode.value = 'process'
  selectedAlarm.value = alarm
  alarmDetailVisible.value = true
}

const handleDeleteAlarm = async (alarm) => {
  try {
    await ElMessageBox.confirm('确定要删除这条告警记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deviceAlarmApi.delete(alarm.id)
    ElMessage.success('删除成功')
    fetchAlarmList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error('删除失败:', error)
    }
  }
}

// 告警操作成功回调
const handleAlarmSuccess = () => {
  fetchAlarmList()
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
}

// 监听对话框打开
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    fetchAlarmList()
  }
})
</script>

<style scoped>
.device-info {
  margin-bottom: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}
</style> 