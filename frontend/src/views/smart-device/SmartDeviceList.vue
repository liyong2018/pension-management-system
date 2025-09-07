<template>
  <div class="smart-device-list">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="auto" :inline="true">
        <el-form-item label="设备名称">
          <el-input v-model="searchForm.deviceName" placeholder="请输入设备名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="设备类型">
          <el-select v-model="searchForm.deviceType" placeholder="请选择设备类型" clearable style="width: 150px">
            <el-option label="全部" value=""></el-option>
            <el-option label="健康监测设备" value="健康监测设备"></el-option>
            <el-option label="智能家居设备" value="智能家居设备"></el-option>
            <el-option label="安全设备" value="安全设备"></el-option>
            <el-option label="定位设备" value="定位设备"></el-option>
            <el-option label="人脸识别设备" value="人脸识别设备"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备状态">
          <el-select v-model="searchForm.deviceStatus" placeholder="请选择设备状态" clearable style="width: 150px">
            <el-option label="全部" value=""></el-option>
            <el-option label="在线" value="在线"></el-option>
            <el-option label="离线" value="离线"></el-option>
            <el-option label="故障" value="故障"></el-option>
            <el-option label="维护中" value="维护中"></el-option>
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
          <el-button type="primary" @click="handleAdd">新增设备</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <div class="statistics-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-value">{{ statusStats.在线 || 0 }}</div>
              <div class="stat-label">在线设备</div>
            </div>
            <el-icon class="stat-icon online"><VideoCameraFilled /></el-icon>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-value">{{ statusStats.故障 || 0 }}</div>
              <div class="stat-label">故障设备</div>
            </div>
            <el-icon class="stat-icon fault"><Warning /></el-icon>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-value">{{ statusStats.维护中 || 0 }}</div>
              <div class="stat-label">维护中</div>
            </div>
            <el-icon class="stat-icon maintenance"><Tools /></el-icon>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-value">{{ total }}</div>
              <div class="stat-label">设备总数</div>
            </div>
            <el-icon class="stat-icon total"><Monitor /></el-icon>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="smartDevices"
        @selection-change="handleSelectionChange"
        border
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="deviceCode" label="设备编号" width="120"></el-table-column>
        <el-table-column prop="deviceName" label="设备名称" min-width="150"></el-table-column>
        <el-table-column prop="deviceType" label="设备类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getDeviceTypeTag(row.deviceType)">{{ row.deviceType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deviceStatus" label="设备状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.deviceStatus)">{{ row.deviceStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="batteryLevel" label="电量" width="100">
          <template #default="{ row }">
            <el-progress
              :percentage="row.batteryLevel"
              :status="getBatteryStatus(row.batteryLevel)"
              :stroke-width="6"
            ></el-progress>
          </template>
        </el-table-column>
        <el-table-column prop="elderlyName" label="绑定老人" width="120"></el-table-column>
        <el-table-column prop="installationLocation" label="安装位置" min-width="150"></el-table-column>
        <el-table-column prop="lastCommunicationTime" label="最后通信时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.lastCommunicationTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleStatusChange(row)">状态</el-button>
            <el-button type="info" link @click="handleAlarmHistory(row)">告警历史</el-button>
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

    <!-- 设备详情/编辑对话框 -->
    <smart-device-dialog
      v-if="dialogVisible"
      v-model="dialogVisible"
      :mode="dialogMode"
      :device-id="selectedDeviceId"
      @success="handleDialogSuccess"
    ></smart-device-dialog>

    <!-- 状态更新对话框 -->
    <status-update-dialog
      v-if="statusDialogVisible"
      v-model="statusDialogVisible"
      :device="selectedDevice"
      @success="handleStatusUpdateSuccess"
    ></status-update-dialog>

    <!-- 告警历史对话框 -->
    <alarm-history-dialog
      v-if="alarmDialogVisible"
      v-model="alarmDialogVisible"
      :device="selectedDevice"
    ></alarm-history-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { VideoCameraFilled, Warning, Tools, Monitor } from '@element-plus/icons-vue'
import { smartDeviceApi } from '@/api/smartDevice'
import SmartDeviceDialog from './components/SmartDeviceDialog.vue'
import StatusUpdateDialog from './components/StatusUpdateDialog.vue'
import AlarmHistoryDialog from './components/AlarmHistoryDialog.vue'

// 数据列表
const smartDevices = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedIds = ref([])

// 统计数据
const statusStats = ref({})

// 搜索表单
const searchForm = ref({
  deviceName: '',
  deviceType: '',
  deviceStatus: ''
})

// 对话框控制
const dialogVisible = ref(false)
const dialogMode = ref('view')
const selectedDeviceId = ref(null)

const statusDialogVisible = ref(false)
const alarmDialogVisible = ref(false)
const selectedDevice = ref(null)

// 获取数据列表
const fetchData = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      ...searchForm.value
    }
    const res = await smartDeviceApi.searchDevices(params)
    smartDevices.value = res.list
    total.value = res.total
  } catch (error) {
    ElMessage.error('获取设备列表失败')
    console.error('获取设备列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const stats = await smartDeviceApi.getStatusStatistics()
    statusStats.value = stats
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
    deviceName: '',
    deviceType: '',
    deviceStatus: ''
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
const getDeviceTypeTag = (type) => {
  const map = {
    '健康监测设备': 'success',
    '智能家居设备': 'primary',
    '安全设备': 'warning',
    '定位设备': 'info',
    '人脸识别设备': 'danger'
  }
  return map[type] || ''
}

const getStatusTag = (status) => {
  const map = {
    '在线': 'success',
    '离线': 'info',
    '故障': 'danger',
    '维护中': 'warning'
  }
  return map[status] || ''
}

const getBatteryStatus = (level) => {
  if (level <= 20) return 'exception'
  if (level <= 50) return 'warning'
  return 'success'
}

// 时间格式化
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 新增/编辑/查看操作
const handleAdd = () => {
  dialogMode.value = 'add'
  selectedDeviceId.value = null
  dialogVisible.value = true
}

const handleView = (row) => {
  console.log('查看设备:', row)
  dialogMode.value = 'view'
  selectedDeviceId.value = row.id
  dialogVisible.value = true
}

const handleEdit = (row) => {
  console.log('编辑设备:', row)
  dialogMode.value = 'edit'
  selectedDeviceId.value = row.id
  dialogVisible.value = true
}

// 状态更改
const handleStatusChange = (row) => {
  selectedDevice.value = row
  statusDialogVisible.value = true
}

// 告警历史
const handleAlarmHistory = (row) => {
  selectedDevice.value = row
  alarmDialogVisible.value = true
}

// 删除操作
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个设备吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await smartDeviceApi.delete(row.id)
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
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个设备吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await smartDeviceApi.batchDelete(selectedIds.value)
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

const handleStatusUpdateSuccess = () => {
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
.smart-device-list {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-operations-left {
  margin-right: auto;
  float:right;

}

.search-buttons-left {
  margin-right: auto;
}

.add-button-right {
  margin-right: 15px;
  float:right;
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

.stat-icon.online {
  color: #67C23A;
}

.stat-icon.fault {
  color: #F56C6C;
}

.stat-icon.maintenance {
  color: #E6A23C;
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