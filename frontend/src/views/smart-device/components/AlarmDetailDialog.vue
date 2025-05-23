<template>
  <el-dialog
    v-model="visible"
    :title="dialogTitle"
    width="60%"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="告警类型" prop="alarmType">
            <el-select v-model="formData.alarmType" placeholder="请选择告警类型" :disabled="mode === 'view'">
              <el-option label="设备故障" value="设备故障"></el-option>
              <el-option label="数据异常" value="数据异常"></el-option>
              <el-option label="低电量" value="低电量"></el-option>
              <el-option label="网络断连" value="网络断连"></el-option>
              <el-option label="健康异常" value="健康异常"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="告警级别" prop="alarmLevel">
            <el-select v-model="formData.alarmLevel" placeholder="请选择告警级别" :disabled="mode === 'view'">
              <el-option label="严重" value="严重"></el-option>
              <el-option label="警告" value="警告"></el-option>
              <el-option label="提醒" value="提醒"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-form-item label="告警内容" prop="alarmContent">
        <el-input
          v-model="formData.alarmContent"
          type="textarea"
          :rows="3"
          placeholder="请输入告警内容"
          :disabled="mode === 'view'"
        ></el-input>
      </el-form-item>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="告警时间" prop="alarmTime">
            <el-date-picker
              v-model="formData.alarmTime"
              type="datetime"
              placeholder="请选择告警时间"
              style="width: 100%"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DDTHH:mm:ss"
              :disabled="mode === 'view'"
            ></el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="处理状态" prop="processStatus">
            <el-select v-model="formData.processStatus" placeholder="请选择处理状态" :disabled="mode === 'view'">
              <el-option label="未处理" value="未处理"></el-option>
              <el-option label="处理中" value="处理中"></el-option>
              <el-option label="已处理" value="已处理"></el-option>
              <el-option label="已忽略" value="已忽略"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      
      <!-- 处理模式下的额外字段 -->
      <template v-if="mode === 'process'">
        <el-form-item label="处理人员" prop="processPerson">
          <el-input v-model="formData.processPerson" placeholder="请输入处理人员"></el-input>
        </el-form-item>
        
        <el-form-item label="处理时间" prop="processTime">
          <el-date-picker
            v-model="formData.processTime"
            type="datetime"
            placeholder="请选择处理时间"
            style="width: 100%"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
          ></el-date-picker>
        </el-form-item>
        
        <el-form-item label="处理备注" prop="processRemark">
          <el-input
            v-model="formData.processRemark"
            type="textarea"
            :rows="3"
            placeholder="请输入处理备注"
          ></el-input>
        </el-form-item>
      </template>
      
      <!-- 查看模式下的只读信息 -->
      <template v-if="mode === 'view' && formData.processPerson">
        <el-divider content-position="left">处理信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="处理人员">
              <span>{{ formData.processPerson || '-' }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="处理时间">
              <span>{{ formatDateTime(formData.processTime) || '-' }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="处理备注">
          <span>{{ formData.processRemark || '-' }}</span>
        </el-form-item>
      </template>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button 
          v-if="mode !== 'view'"
          type="primary" 
          :loading="loading"
          @click="handleSubmit"
        >
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, nextTick, toRaw } from 'vue'
import { ElMessage } from 'element-plus'
import { deviceAlarmApi } from '@/api/deviceAlarm'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  mode: {
    type: String,
    default: 'view', // view, add, process
    validator: (value) => ['view', 'add', 'process'].includes(value)
  },
  alarm: {
    type: Object,
    default: () => ({})
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

const dialogTitle = computed(() => {
  const titles = {
    view: '查看告警详情',
    add: '新增告警',
    process: '处理告警'
  }
  return titles[props.mode] || '告警详情'
})

// 表单数据
const formRef = ref()
const loading = ref(false)
const formData = ref({
  alarmType: '',
  alarmLevel: '',
  alarmContent: '',
  alarmTime: '',
  processStatus: '未处理',
  processPerson: '',
  processTime: '',
  processRemark: ''
})

// 表单验证规则
const formRules = ref({
  alarmType: [
    { required: true, message: '请选择告警类型', trigger: 'change' }
  ],
  alarmLevel: [
    { required: true, message: '请选择告警级别', trigger: 'change' }
  ],
  alarmContent: [
    { required: true, message: '请输入告警内容', trigger: 'blur' }
  ],
  alarmTime: [
    { required: true, message: '请选择告警时间', trigger: 'change' }
  ],
  processStatus: [
    { required: true, message: '请选择处理状态', trigger: 'change' }
  ]
})

// 时间格式化
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 初始化表单数据
const initFormData = () => {
  if (props.mode === 'add') {
    formData.value = {
      alarmType: '',
      alarmLevel: '',
      alarmContent: '',
      alarmTime: new Date().toISOString().slice(0, 19),
      processStatus: '未处理',
      processPerson: '',
      processTime: '',
      processRemark: ''
    }
  } else if (props.mode === 'view' || props.mode === 'process') {
    const alarm = toRaw(props.alarm) || {}
    
    // 处理时间格式
    const formatTime = (timeStr) => {
      if (!timeStr) return ''
      try {
        // 如果是完整的ISO时间格式，截取前19位
        if (timeStr.includes('T')) {
          return timeStr.slice(0, 19)
        }
        // 如果是带空格的格式，转换为T格式
        if (timeStr.includes(' ')) {
          return timeStr.replace(' ', 'T')
        }
        return timeStr
      } catch (e) {
        console.error('时间格式处理错误:', e)
        return ''
      }
    }
    
    formData.value = {
      alarmType: alarm.alarmType || '',
      alarmLevel: alarm.alarmLevel || '',
      alarmContent: alarm.alarmContent || '',
      alarmTime: formatTime(alarm.alarmTime),
      processStatus: alarm.processStatus || '未处理',
      processPerson: alarm.processPerson || '',
      processTime: props.mode === 'process' && !alarm.processTime 
        ? new Date().toISOString().slice(0, 19)
        : formatTime(alarm.processTime),
      processRemark: alarm.processRemark || ''
    }
    
    if (props.mode === 'process' && !formData.value.processPerson) {
      formData.value.processPerson = '当前用户'
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    const submitData = {
      ...formData.value,
      deviceId: props.device.id
    }
    
    if (props.mode === 'add') {
      await deviceAlarmApi.create(submitData)
      ElMessage.success('新增告警成功')
    } else if (props.mode === 'process') {
      await deviceAlarmApi.processAlarm(props.alarm.id, {
        processStatus: formData.value.processStatus,
        processPerson: formData.value.processPerson,
        processTime: formData.value.processTime,
        processRemark: formData.value.processRemark
      })
      ElMessage.success('处理告警成功')
    }
    
    emit('success')
    handleClose()
  } catch (error) {
    console.error('提交失败:', error)
    if (error.message && error.message !== 'validation failed') {
      ElMessage.error('操作失败：' + error.message)
    }
  } finally {
    loading.value = false
  }
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 监听对话框打开和数据变化
watch([() => props.modelValue, () => props.alarm, () => props.mode], ([visible, alarm, mode]) => {
  if (visible) {
    nextTick(() => {
      initFormData()
    })
  }
}, { immediate: true, deep: true })
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style> 