<template>
  <el-dialog
    v-model="visible"
    title="更新志愿者统计"
    width="500px"
    :before-close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="120px"
    >
      <el-form-item label="志愿者">
        <span>{{ volunteer?.name }}</span>
      </el-form-item>
      <el-form-item label="当前服务时长">
        <span>{{ volunteer?.totalServiceHours || 0 }}小时</span>
      </el-form-item>
      <el-form-item label="当前积分">
        <span>{{ volunteer?.points || 0 }}分</span>
      </el-form-item>
      <el-form-item label="增加服务时长" prop="hours">
        <el-input-number
          v-model="formData.hours"
          :min="0"
          :precision="1"
          placeholder="请输入增加的服务时长"
          style="width: 100%"
        />
        <span style="margin-left: 8px; color: #909399;">小时</span>
      </el-form-item>
      <el-form-item label="增加积分" prop="points">
        <el-input-number
          v-model="formData.points"
          :min="0"
          placeholder="请输入增加的积分"
          style="width: 100%"
        />
        <span style="margin-left: 8px; color: #909399;">分</span>
      </el-form-item>
      <el-alert
        title="注意：这里输入的是要增加的数值，不是最终的总数值"
        type="info"
        :closable="false"
        show-icon
      />
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
import { ref, computed } from 'vue'
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
  hours: 0,
  points: 0
})

// 表单验证规则
const rules = {
  hours: [
    { type: 'number', min: 0, message: '服务时长不能为负数', trigger: 'blur' }
  ],
  points: [
    { type: 'number', min: 0, message: '积分不能为负数', trigger: 'blur' }
  ]
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
  formData.value = {
    hours: 0,
    points: 0
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    if (formData.value.hours === 0 && formData.value.points === 0) {
      ElMessage.warning('请至少输入一项要更新的数据')
      return
    }
    
    loading.value = true

    const response = await volunteerApi.updateServiceStats(
      props.volunteer.id,
      formData.value.hours,
      formData.value.points
    )

    if (response.success) {
      ElMessage.success('统计更新成功')
      emit('success')
      handleClose()
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    if (error !== false) {
      ElMessage.error('统计更新失败')
    }
  } finally {
    loading.value = false
  }
}
</script> 