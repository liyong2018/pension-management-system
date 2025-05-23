<template>
  <el-dialog
    v-model="visible"
    title="更新设备状态"
    width="500px"
    @close="handleClose"
  >
    <div class="device-info">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="设备编号">{{ device?.deviceCode }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ device?.deviceName }}</el-descriptions-item>
        <el-descriptions-item label="当前状态">
          <el-tag :type="getStatusTag(device?.deviceStatus)">{{ device?.deviceStatus }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <el-form
      ref="formRef"
      :model="statusForm"
      :rules="rules"
      label-width="120px"
      style="margin-top: 20px;"
    >
      <el-form-item label="新状态" prop="status">
        <el-select v-model="statusForm.status" placeholder="请选择新状态">
          <el-option label="在线" value="在线">
            <span>在线</span>
            <el-tag type="success" size="small" style="margin-left: 8px;">正常运行</el-tag>
          </el-option>
          <el-option label="离线" value="离线">
            <span>离线</span>
            <el-tag type="info" size="small" style="margin-left: 8px;">未连接</el-tag>
          </el-option>
          <el-option label="故障" value="故障">
            <span>故障</span>
            <el-tag type="danger" size="small" style="margin-left: 8px;">需要修复</el-tag>
          </el-option>
          <el-option label="维护中" value="维护中">
            <span>维护中</span>
            <el-tag type="warning" size="small" style="margin-left: 8px;">正在维护</el-tag>
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="电量状态" prop="batteryLevel">
        <el-input-number
          v-model="statusForm.batteryLevel"
          :min="0"
          :max="100"
          placeholder="电量百分比"
          style="width: 200px;"
        ></el-input-number>
        <span style="margin-left: 8px;">%</span>
        <el-progress
          :percentage="statusForm.batteryLevel || 0"
          :status="getBatteryStatus(statusForm.batteryLevel)"
          style="margin-top: 8px;"
        ></el-progress>
      </el-form-item>

      <el-form-item label="实时数据">
        <el-input
          v-model="statusForm.realTimeData"
          type="textarea"
          :rows="3"
          placeholder="JSON格式的实时数据，如：{&quot;temperature&quot;: 25.5, &quot;humidity&quot;: 60}"
        ></el-input>
      </el-form-item>

      <el-form-item label="备注">
        <el-input
          v-model="statusForm.remarks"
          type="textarea"
          :rows="2"
          placeholder="状态更新的原因或备注信息"
        ></el-input>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          更新状态
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { smartDeviceApi } from '@/api/smartDevice'

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

const emit = defineEmits(['update:modelValue', 'success'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 表单数据
const statusForm = ref({
  status: '',
  batteryLevel: 0,
  realTimeData: '',
  remarks: ''
})

// 表单验证规则
const rules = {
  status: [
    { required: true, message: '请选择设备状态', trigger: 'change' }
  ]
}

const formRef = ref(null)
const submitting = ref(false)

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

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    const valid = await formRef.value.validate()
    if (!valid) return
    
    submitting.value = true
    
    // 更新设备状态
    await smartDeviceApi.updateStatus(props.device.id, statusForm.value.status)
    
    // 如果有电量更新，同时更新电量
    if (statusForm.value.batteryLevel !== props.device.batteryLevel) {
      await smartDeviceApi.updateBattery(props.device.id, statusForm.value.batteryLevel)
    }
    
    // 如果有实时数据更新，同时更新实时数据
    if (statusForm.value.realTimeData && statusForm.value.realTimeData !== props.device.realTimeData) {
      await smartDeviceApi.updateRealtimeData(props.device.id, statusForm.value.realTimeData)
    }
    
    ElMessage.success('设备状态更新成功')
    emit('success')
    handleClose()
  } catch (error) {
    ElMessage.error('设备状态更新失败')
    console.error('状态更新失败:', error)
  } finally {
    submitting.value = false
  }
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
  // 重置表单
  statusForm.value = {
    status: '',
    batteryLevel: 0,
    realTimeData: '',
    remarks: ''
  }
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 监听对话框打开，初始化表单数据
watch(() => props.modelValue, (newVal) => {
  if (newVal && props.device) {
    statusForm.value = {
      status: props.device.deviceStatus || '',
      batteryLevel: props.device.batteryLevel || 0,
      realTimeData: props.device.realTimeData || '',
      remarks: ''
    }
  }
})
</script>

<style scoped>
.device-info {
  margin-bottom: 20px;
}

.dialog-footer {
  text-align: right;
}
</style> 