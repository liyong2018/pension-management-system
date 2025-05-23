<template>
  <el-dialog
    v-model="visible"
    title="选择设备"
    width="80%"
    @close="handleClose"
  >
    <!-- 搜索区域 -->
    <el-form :model="searchForm" label-width="auto" :inline="true" style="margin-bottom: 20px;">
      <el-form-item label="设备名称">
        <el-input v-model="searchForm.deviceName" placeholder="请输入设备名称" clearable></el-input>
      </el-form-item>
      <el-form-item label="设备类型">
        <el-select v-model="searchForm.deviceType" placeholder="请选择设备类型" clearable>
          <el-option label="健康监测设备" value="健康监测设备"></el-option>
          <el-option label="智能家居设备" value="智能家居设备"></el-option>
          <el-option label="安全设备" value="安全设备"></el-option>
          <el-option label="定位设备" value="定位设备"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="设备状态">
        <el-select v-model="searchForm.deviceStatus" placeholder="请选择设备状态" clearable>
          <el-option label="在线" value="在线"></el-option>
          <el-option label="离线" value="离线"></el-option>
          <el-option label="故障" value="故障"></el-option>
          <el-option label="维护中" value="维护中"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 设备列表 -->
    <el-table
      v-loading="loading"
      :data="deviceList"
      border
      @row-click="handleRowClick"
      highlight-current-row
    >
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
      <el-table-column prop="elderlyName" label="绑定老人" width="120"></el-table-column>
      <el-table-column prop="installationLocation" label="安装位置" min-width="150"></el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button type="primary" @click="handleSelect(row)">选择</el-button>
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

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { smartDeviceApi } from '@/api/smartDevice'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'select'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 数据列表
const deviceList = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 搜索表单
const searchForm = ref({
  deviceName: '',
  deviceType: '',
  deviceStatus: ''
})

// 获取设备列表
const fetchDeviceList = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      ...searchForm.value
    }
    const res = await smartDeviceApi.searchDevices(params)
    deviceList.value = res.list
    total.value = res.total
  } catch (error) {
    ElMessage.error('获取设备列表失败')
    console.error('获取设备列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索和重置
const handleSearch = () => {
  currentPage.value = 1
  fetchDeviceList()
}

const handleReset = () => {
  searchForm.value = {
    deviceName: '',
    deviceType: '',
    deviceStatus: ''
  }
  currentPage.value = 1
  fetchDeviceList()
}

// 分页操作
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchDeviceList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchDeviceList()
}

// 标签类型
const getDeviceTypeTag = (type) => {
  const map = {
    '健康监测设备': 'success',
    '智能家居设备': 'primary',
    '安全设备': 'warning',
    '定位设备': 'info'
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

// 选择设备
const handleRowClick = (row) => {
  // 双击行选择
}

const handleSelect = (device) => {
  emit('select', device)
  handleClose()
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
}

// 监听对话框打开
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    fetchDeviceList()
  }
})
</script>

<style scoped>
.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}
</style> 