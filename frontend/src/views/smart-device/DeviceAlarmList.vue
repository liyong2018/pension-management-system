<template>
  <div class="device-alarm-list">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="auto" :inline="true">
        <el-form-item label="告警类型">
          <el-select v-model="searchForm.alarmType" placeholder="请选择告警类型" clearable style="width: 150px">
            <el-option label="全部" value=""></el-option>
            <el-option label="设备故障" value="设备故障"></el-option>
            <el-option label="数据异常" value="数据异常"></el-option>
            <el-option label="低电量" value="低电量"></el-option>
            <el-option label="网络断连" value="网络断连"></el-option>
            <el-option label="健康异常" value="健康异常"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="告警级别">
          <el-select v-model="searchForm.alarmLevel" placeholder="请选择告警级别" clearable style="width: 150px">
            <el-option label="全部" value=""></el-option>
            <el-option label="严重" value="严重"></el-option>
            <el-option label="警告" value="警告"></el-option>
            <el-option label="提醒" value="提醒"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="searchForm.processStatus" placeholder="请选择处理状态" clearable style="width: 150px">
            <el-option label="全部" value=""></el-option>
            <el-option label="未处理" value="未处理"></el-option>
            <el-option label="处理中" value="处理中"></el-option>
            <el-option label="已处理" value="已处理"></el-option>
            <el-option label="已忽略" value="已忽略"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="search-buttons-left">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
        <el-form-item class="table-operations-left">
          <el-button type="danger" :disabled="!selectedIds.length" @click="handleBatchDelete">
            批量删除
          </el-button>
        </el-form-item>
        <el-form-item class="add-button-right">
          <el-button type="primary" @click="handleAdd">新增告警</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <div class="statistics-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-value">{{ statusStats.未处理 || 0 }}</div>
              <div class="stat-label">未处理告警</div>
            </div>
            <el-icon class="stat-icon unprocessed"><Warning /></el-icon>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-value">{{ levelStats.严重 || 0 }}</div>
              <div class="stat-label">严重告警</div>
            </div>
            <el-icon class="stat-icon critical"><CircleCloseFilled /></el-icon>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-value">{{ statusStats.已处理 || 0 }}</div>
              <div class="stat-label">已处理</div>
            </div>
            <el-icon class="stat-icon processed"><CircleCheckFilled /></el-icon>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-value">{{ total }}</div>
              <div class="stat-label">告警总数</div>
            </div>
            <el-icon class="stat-icon total"><Bell /></el-icon>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="alarmList"
        @selection-change="handleSelectionChange"
        border
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="deviceCode" label="设备编号" width="120"></el-table-column>
        <el-table-column prop="deviceName" label="设备名称" min-width="150"></el-table-column>
        <el-table-column prop="alarmType" label="告警类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getAlarmTypeTag(row.alarmType || '未知')">
              {{ row.alarmType || '未知' }}
            </el-tag>
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
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button 
              v-if="row.processStatus === '未处理' || row.processStatus === '处理中'"
              type="warning" 
              link 
              @click="handleProcess(row)"
            >
              处理
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        ></el-pagination>
      </div>
    </el-card>

    <!-- 告警详情/处理对话框 -->
    <alarm-detail-dialog
      v-if="dialogVisible"
      v-model="dialogVisible"
      :mode="dialogMode"
      :alarm="selectedAlarm"
      :device="selectedDevice"
      @success="handleDialogSuccess"
    ></alarm-detail-dialog>

    <!-- 设备选择对话框 -->
    <device-select-dialog
      v-if="deviceSelectVisible"
      v-model="deviceSelectVisible"
      @select="handleDeviceSelect"
    ></device-select-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Warning, CircleCloseFilled, CircleCheckFilled, Bell } from '@element-plus/icons-vue'
import { deviceAlarmApi } from '@/api/deviceAlarm'
import { smartDeviceApi } from '@/api/smartDevice'
import AlarmDetailDialog from './components/AlarmDetailDialog.vue'
import DeviceSelectDialog from './components/DeviceSelectDialog.vue'

// 数据列表
const alarmList = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedIds = ref([])

// 统计数据
const statusStats = ref({})
const levelStats = ref({})

// 搜索表单
const searchForm = ref({
  alarmType: '',
  alarmLevel: '',
  processStatus: ''
})

// 对话框控制
const dialogVisible = ref(false)
const dialogMode = ref('view')
const selectedAlarm = ref(null)
const selectedDevice = ref(null)

const deviceSelectVisible = ref(false)

// 获取数据列表
const fetchData = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      ...searchForm.value
    }
    const res = await deviceAlarmApi.getList(params)
    alarmList.value = res.list
    total.value = res.total
  } catch (error) {
    ElMessage.error('获取告警列表失败')
    console.error('获取告警列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const [levelData, statusData] = await Promise.all([
      deviceAlarmApi.getLevelStatistics(),
      deviceAlarmApi.getStatusStatistics()
    ])
    levelStats.value = levelData
    statusStats.value = statusData
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 搜索和重置
const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const handleReset = () => {
  searchForm.value = {
    alarmType: '',
    alarmLevel: '',
    processStatus: ''
  }
  currentPage.value = 1
  fetchData()
}

// 分页操作
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchData()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchData()
}

// 表格选择
const handleSelectionChange = (val) => {
  selectedIds.value = val.map(item => item.id)
}

// 标签颜色
const getAlarmTypeTag = (type) => {
  const map = {
    '设备故障': 'danger',
    '数据异常': 'warning',
    '低电量': 'warning',
    '网络断连': 'info',
    '健康异常': 'danger',
    '未知': 'info'
  }
  return map[type] || 'info'
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

// 新增/查看/处理操作
const handleAdd = () => {
  deviceSelectVisible.value = true
}

const handleView = (row) => {
  console.log('点击查看告警，行数据:', row)
  dialogMode.value = 'view'
  selectedAlarm.value = row
  selectedDevice.value = {
    id: row.deviceId,
    deviceCode: row.deviceCode,
    deviceName: row.deviceName
  }
  console.log('设置selectedAlarm:', selectedAlarm.value)
  console.log('设置selectedDevice:', selectedDevice.value)
  dialogVisible.value = true
}

const handleProcess = (row) => {
  dialogMode.value = 'process'
  selectedAlarm.value = row
  selectedDevice.value = {
    id: row.deviceId,
    deviceCode: row.deviceCode,
    deviceName: row.deviceName
  }
  dialogVisible.value = true
}

// 设备选择回调
const handleDeviceSelect = (device) => {
  dialogMode.value = 'add'
  selectedAlarm.value = null
  selectedDevice.value = device
  dialogVisible.value = true
}

// 删除操作
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条告警记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deviceAlarmApi.delete(row.id)
    ElMessage.success('删除成功')
    fetchData()
    fetchStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error('删除失败:', error)
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条告警记录吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deviceAlarmApi.batchDelete(selectedIds.value)
    ElMessage.success('批量删除成功')
    fetchData()
    fetchStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
      console.error('批量删除失败:', error)
    }
  }
}

// 对话框成功回调
const handleDialogSuccess = () => {
  fetchData()
  fetchStatistics()
}

// 初始化
onMounted(() => {
  fetchData()
  fetchStatistics()
})
</script>

<style scoped>
.device-alarm-list {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-operations-left {
  margin-right: 5px;
  float: right;
}

.search-buttons-left {
  margin-right: auto;
}

.add-button-right {
  margin-right: 10px;
  float: right;
}

.statistics-cards {
  margin-bottom: 20px;
}

.stat-card {
  padding: 10px;
  border-radius: 8px;
  position: relative;
  overflow: hidden;
}

.stat-content {
  text-align: center;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.stat-icon {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 24px;
  opacity: 0.3;
}

.stat-icon.unprocessed {
  color: #F56C6C;
}

.stat-icon.critical {
  color: #F56C6C;
}

.stat-icon.processed {
  color: #67C23A;
}

.stat-icon.total {
  color: #409EFF;
}

.table-card {
  min-height: 400px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style> 