<template>
  <el-dialog
    v-model="visible"
    :title="dialogTitle"
    width="80%"
    @close="handleClose"
    :close-on-click-modal="false"
  >
    <div v-loading="loading" class="dialog-content">
      <!-- 基本信息 -->
      <el-card class="section-card">
        <template #header>
          <div class="card-header">
            <span>基本信息</span>
            <el-tag v-if="mode !== 'add'" :type="getStatusTag(deviceForm.deviceStatus)">
              {{ deviceForm.deviceStatus || '未知' }}
            </el-tag>
          </div>
        </template>
        
        <el-form
          ref="formRef"
          :model="deviceForm"
          :rules="rules"
          label-width="120px"
          :disabled="mode === 'view'"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="设备编号" prop="deviceCode">
                <el-input v-model="deviceForm.deviceCode" placeholder="请输入设备编号"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="设备名称" prop="deviceName">
                <el-input v-model="deviceForm.deviceName" placeholder="请输入设备名称"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="设备类型" prop="deviceType">
                <el-select v-model="deviceForm.deviceType" placeholder="请选择设备类型">
                  <el-option label="健康监测设备" value="健康监测设备"></el-option>
                  <el-option label="智能家居设备" value="智能家居设备"></el-option>
                  <el-option label="安全设备" value="安全设备"></el-option>
                  <el-option label="定位设备" value="定位设备"></el-option>
                  <el-option label="人脸识别设备" value="人脸识别设备"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="设备品牌" prop="deviceBrand">
                <el-input v-model="deviceForm.deviceBrand" placeholder="请输入设备品牌"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="设备型号" prop="deviceModel">
                <el-input v-model="deviceForm.deviceModel" placeholder="请输入设备型号"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="购买日期" prop="purchaseDate">
                <el-date-picker
                  v-model="deviceForm.purchaseDate"
                  type="date"
                  placeholder="请选择购买日期"
                  value-format="YYYY-MM-DD"
                ></el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="安装位置" prop="installationLocation">
                <el-input v-model="deviceForm.installationLocation" placeholder="请输入安装位置"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="绑定老人" prop="elderlyId">
                <el-select
                  v-model="deviceForm.elderlyId"
                  placeholder="请选择绑定的老人"
                  filterable
                  remote
                  :remote-method="searchElderly"
                  :loading="elderlyLoading"
                  clearable
                >
                  <el-option
                    v-for="elderly in elderlyOptions"
                    :key="elderly.id"
                    :label="elderly.name"
                    :value="elderly.id"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-card>

      <!-- 配置信息 -->
      <el-card class="section-card">
        <template #header>
          <span>配置信息</span>
        </template>
        
        <el-form
          :model="deviceForm"
          label-width="120px"
          :disabled="mode === 'view'"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="IP地址">
                <el-input v-model="deviceForm.ipAddress" placeholder="请输入IP地址"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="MAC地址">
                <el-input v-model="deviceForm.macAddress" placeholder="请输入MAC地址"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="通信协议">
                <el-select v-model="deviceForm.communicationProtocol" placeholder="请选择通信协议">
                  <el-option label="TCP/IP" value="TCP/IP"></el-option>
                  <el-option label="MQTT" value="MQTT"></el-option>
                  <el-option label="LoRa" value="LoRa"></el-option>
                  <el-option label="Zigbee" value="Zigbee"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="数据采集频率">
                <el-input-number
                  v-model="deviceForm.dataCollectionFrequency"
                  :min="1"
                  :max="3600"
                  placeholder="秒"
                ></el-input-number>
                <span style="margin-left: 8px;">秒</span>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="电量阈值">
                <el-input-number
                  v-model="deviceForm.batteryThreshold"
                  :min="1"
                  :max="100"
                  placeholder="百分比"
                ></el-input-number>
                <span style="margin-left: 8px;">%</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="报警阈值">
                <el-input
                  v-model="deviceForm.alarmThreshold"
                  type="textarea"
                  :rows="2"
                  placeholder="JSON格式，如：{&quot;temperature&quot;: 35, &quot;humidity&quot;: 80}"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-card>

      <!-- 监控信息 -->
      <el-card v-if="mode !== 'add'" class="section-card">
        <template #header>
          <span>监控信息</span>
        </template>
        
        <el-form label-width="120px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="当前电量">
                <el-progress
                  :percentage="Number(deviceForm.batteryLevel) || 0"
                  :status="getBatteryStatus(deviceForm.batteryLevel)"
                ></el-progress>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="信号强度">
                <el-progress
                  :percentage="Number(deviceForm.signalStrength) || 0"
                  :color="getSignalColor(deviceForm.signalStrength)"
                ></el-progress>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="最后通信时间">
                <span>{{ formatDateTime(deviceForm.lastCommunicationTime) }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="数据上传状态">
                <el-tag :type="getUploadStatusTag(deviceForm.dataUploadStatus)">
                  {{ deviceForm.dataUploadStatus || '未知' }}
                </el-tag>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-form-item label="实时数据">
            <el-input
              v-model="deviceForm.realTimeData"
              type="textarea"
              :rows="3"
              readonly
              placeholder="暂无实时数据"
            ></el-input>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 维护信息 -->
      <el-card class="section-card">
        <template #header>
          <span>维护信息</span>
        </template>
        
        <el-form
          :model="deviceForm"
          label-width="120px"
          :disabled="mode === 'view'"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="保修期限">
                <el-date-picker
                  v-model="deviceForm.warrantyExpiryDate"
                  type="date"
                  placeholder="请选择保修期限"
                  value-format="YYYY-MM-DD"
                ></el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="维护周期">
                <el-input-number
                  v-model="deviceForm.maintenanceCycle"
                  :min="1"
                  :max="365"
                  placeholder="天"
                ></el-input-number>
                <span style="margin-left: 8px;">天</span>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20" v-if="mode !== 'add'">
            <el-col :span="12">
              <el-form-item label="下次维护时间">
                <span>{{ formatDate(deviceForm.nextMaintenanceDate) }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="故障次数">
                <span>{{ deviceForm.failureCount || 0 }} 次</span>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-card>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button v-if="mode !== 'view'" type="primary" @click="handleSubmit" :loading="submitting">
          {{ mode === 'add' ? '创建' : '保存' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, nextTick, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { smartDeviceApi } from '@/api/smartDevice'
import { elderlyProfileApi } from '@/api/elderlyProfile'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  mode: {
    type: String,
    default: 'view' // view, edit, add
  },
  deviceId: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const dialogTitle = computed(() => {
  const titleMap = {
    view: '查看设备',
    edit: '编辑设备',
    add: '新增设备'
  }
  return titleMap[props.mode] || '设备信息'
})

// 使用reactive而不是ref来确保表单数据完全响应式
const deviceForm = reactive({
  deviceCode: '',
  deviceName: '',
  deviceType: '',
  deviceBrand: '',
  deviceModel: '',
  deviceStatus: '离线',
  purchaseDate: '',
  installationLocation: '',
  elderlyId: null,
  ipAddress: '',
  macAddress: '',
  communicationProtocol: '',
  dataCollectionFrequency: 60,
  alarmThreshold: '',
  batteryThreshold: 20,
  realTimeData: '',
  batteryLevel: 0,
  signalStrength: 0,
  lastCommunicationTime: '',
  dataUploadStatus: '',
  warrantyExpiryDate: '',
  maintenanceCycle: 30,
  nextMaintenanceDate: '',
  failureCount: 0
})

// 表单验证规则
const rules = {
  deviceCode: [
    { required: true, message: '请输入设备编号', trigger: 'blur' }
  ],
  deviceName: [
    { required: true, message: '请输入设备名称', trigger: 'blur' }
  ],
  deviceType: [
    { required: true, message: '请选择设备类型', trigger: 'change' }
  ],
  deviceBrand: [
    { required: true, message: '请输入设备品牌', trigger: 'blur' }
  ],
  deviceModel: [
    { required: true, message: '请输入设备型号', trigger: 'blur' }
  ]
}

const formRef = ref(null)
const loading = ref(false)
const submitting = ref(false)

// 老人选择相关
const elderlyOptions = ref([])
const elderlyLoading = ref(false)

// 获取设备详情
const fetchDeviceDetail = async () => {
  if (!props.deviceId) {
    return
  }
  
  try {
    loading.value = true
    
    const response = await smartDeviceApi.getById(props.deviceId)
    
    // 直接赋值确保响应式更新
    deviceForm.deviceCode = response.deviceCode || ''
    deviceForm.deviceName = response.deviceName || ''
    deviceForm.deviceType = response.deviceType || ''
    deviceForm.deviceBrand = response.deviceBrand || ''
    deviceForm.deviceModel = response.deviceModel || ''
    deviceForm.deviceStatus = response.deviceStatus || '离线'
    deviceForm.purchaseDate = response.purchaseDate || ''
    deviceForm.installationLocation = response.installationLocation || ''
    deviceForm.elderlyId = response.elderlyId || null
    deviceForm.ipAddress = response.ipAddress || ''
    deviceForm.macAddress = response.macAddress || ''
    deviceForm.communicationProtocol = response.communicationProtocol || ''
    deviceForm.dataCollectionFrequency = response.dataCollectionFrequency || 60
    deviceForm.alarmThreshold = response.alarmThreshold || ''
    deviceForm.batteryThreshold = response.batteryThreshold || 20
    deviceForm.realTimeData = response.realTimeData || ''
    deviceForm.batteryLevel = response.batteryLevel || 0
    deviceForm.signalStrength = response.signalStrength || 0
    deviceForm.lastCommunicationTime = response.lastCommunicationTime || ''
    deviceForm.dataUploadStatus = response.dataUploadStatus || ''
    deviceForm.warrantyExpiryDate = response.warrantyExpiryDate || ''
    deviceForm.maintenanceCycle = response.maintenanceCycle || 30
    deviceForm.nextMaintenanceDate = response.nextMaintenanceDate || ''
    deviceForm.failureCount = response.failureCount || 0
    
    // 如果有绑定老人，添加到选项中
    if (response.elderlyId && response.elderlyName) {
      elderlyOptions.value = [
        { id: response.elderlyId, name: response.elderlyName }
      ]
    }
    
  } catch (error) {
    ElMessage.error('获取设备详情失败')
    console.error('获取设备详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索老人
const searchElderly = async (query) => {
  if (!query) {
    elderlyOptions.value = []
    return
  }
  
  try {
    elderlyLoading.value = true
    const response = await elderlyProfileApi.getList({
      name: query,
      page: 1,
      size: 20
    })
    elderlyOptions.value = response.list.map(item => ({
      id: item.id,
      name: item.name
    }))
  } catch (error) {
    console.error('搜索老人失败:', error)
    elderlyOptions.value = []
  } finally {
    elderlyLoading.value = false
  }
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

const getBatteryStatus = (level) => {
  if (level <= 20) return 'exception'
  if (level <= 50) return 'warning'
  return 'success'
}

const getUploadStatusTag = (status) => {
  const map = {
    '正常': 'success',
    '异常': 'danger',
    '延迟': 'warning'
  }
  return map[status] || 'info'
}

const getSignalColor = (strength) => {
  if (strength >= 80) return '#67c23a'
  if (strength >= 60) return '#e6a23c'
  if (strength >= 40) return '#f56c6c'
  return '#909399'
}

// 时间格式化
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    const valid = await formRef.value.validate()
    if (!valid) return
    
    submitting.value = true
    
    if (props.mode === 'add') {
      await smartDeviceApi.create(deviceForm)
      ElMessage.success('创建设备成功')
    } else {
      await smartDeviceApi.update(props.deviceId, deviceForm)
      ElMessage.success('更新设备成功')
    }
    
    emit('success')
    handleClose()
  } catch (error) {
    ElMessage.error(props.mode === 'add' ? '创建设备失败' : '更新设备失败')
    console.error('提交失败:', error)
  } finally {
    submitting.value = false
  }
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
  // 只在新增模式时重置表单，其他模式保留数据以便下次打开
  if (props.mode === 'add') {
    nextTick(() => {
      if (formRef.value) {
        formRef.value.resetFields()
      }
      resetForm()
    })
  }
}

// 监听对话框打开
watch(() => props.modelValue, async (newVal, oldVal) => {
  if (newVal && !oldVal) {
    // 对话框刚打开
    if (props.mode !== 'add' && props.deviceId) {
      // 等待DOM更新完成
      await nextTick()
      // 立即调用加载数据
      try {
        await fetchDeviceDetail()
      } catch (error) {
        console.error('加载设备数据失败:', error)
      }
    } else if (props.mode === 'add') {
      resetForm()
    }
  }
}, { immediate: true })

// 监听deviceId变化
watch(() => props.deviceId, async (newVal, oldVal) => {
  if (newVal && newVal !== oldVal && props.modelValue && props.mode !== 'add') {
    await nextTick()
    try {
      await fetchDeviceDetail()
    } catch (error) {
      console.error('重新加载设备数据失败:', error)
    }
  }
}, { immediate: true })

// 监听模式变化
watch(() => props.mode, (newMode, oldMode) => {
  if (newMode !== 'add' && props.deviceId && props.modelValue) {
    nextTick(async () => {
      try {
        await fetchDeviceDetail()
      } catch (error) {
        console.error('模式变化加载数据失败:', error)
      }
    })
  }
}, { immediate: true })

// 重置表单函数
const resetForm = () => {
  deviceForm.deviceCode = ''
  deviceForm.deviceName = ''
  deviceForm.deviceType = ''
  deviceForm.deviceBrand = ''
  deviceForm.deviceModel = ''
  deviceForm.deviceStatus = '离线'
  deviceForm.purchaseDate = ''
  deviceForm.installationLocation = ''
  deviceForm.elderlyId = null
  deviceForm.ipAddress = ''
  deviceForm.macAddress = ''
  deviceForm.communicationProtocol = ''
  deviceForm.dataCollectionFrequency = 60
  deviceForm.alarmThreshold = ''
  deviceForm.batteryThreshold = 20
  deviceForm.realTimeData = ''
  deviceForm.batteryLevel = 0
  deviceForm.signalStrength = 0
  deviceForm.lastCommunicationTime = ''
  deviceForm.dataUploadStatus = ''
  deviceForm.warrantyExpiryDate = ''
  deviceForm.maintenanceCycle = 30
  deviceForm.nextMaintenanceDate = ''
  deviceForm.failureCount = 0
  elderlyOptions.value = []
}
</script>

<style scoped>
.dialog-content {
  max-height: 70vh;
  overflow-y: auto;
}

.section-card {
  margin-bottom: 20px;
}

.section-card:last-child {
  margin-bottom: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  text-align: right;
}
</style>