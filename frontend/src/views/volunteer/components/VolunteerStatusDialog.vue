<template>
  <el-dialog
    v-model="visible"
    title="变更志愿者状态"
    width="400px"
    :before-close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="80px"
    >
      <el-form-item label="志愿者">
        <span>{{ volunteer?.name }}</span>
      </el-form-item>
      <el-form-item label="当前状态">
        <el-tag :type="getStatusType(volunteer?.status)">
          {{ getStatusText(volunteer?.status) }}
        </el-tag>
      </el-form-item>
      <el-form-item label="新状态" prop="status">
        <el-select v-model="formData.status" placeholder="请选择新状态">
          <el-option label="活跃" value="ACTIVE" />
          <el-option label="不活跃" value="INACTIVE" />
          <el-option label="待审核" value="PENDING_APPROVAL" />
        </el-select>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          确定
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { volunteerApi } from '@/api/volunteer'

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

const emit = defineEmits(['update:modelValue', 'success'])

const formRef = ref()
const loading = ref(false)

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 表单数据
const formData = ref({
  status: ''
})

// 表单验证规则
const rules = {
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 监听志愿者数据变化
watch(() => props.volunteer, (newVolunteer) => {
  if (newVolunteer) {
    formData.value.status = newVolunteer.status
  }
}, { immediate: true })

// 关闭对话框
const handleClose = () => {
  visible.value = false
  formData.value.status = ''
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    const response = await volunteerApi.updateVolunteerStatus(props.volunteer.id, formData.value.status)

    if (response.success) {
      ElMessage.success('状态更新成功')
      emit('success')
      handleClose()
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    if (error !== false) {
      ElMessage.error('状态更新失败')
    }
  } finally {
    loading.value = false
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    'ACTIVE': 'success',
    'INACTIVE': 'info',
    'PENDING_APPROVAL': 'warning'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'ACTIVE': '活跃',
    'INACTIVE': '不活跃',
    'PENDING_APPROVAL': '待审核'
  }
  return statusMap[status] || '未知'
}
</script> 