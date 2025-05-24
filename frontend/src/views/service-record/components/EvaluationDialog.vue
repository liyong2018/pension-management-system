<template>
  <el-dialog
    title="服务评价"
    v-model="visible"
    :close-on-click-modal="false"
    width="50%"
    destroy-on-close
  >
    <div class="evaluation-content">
      <!-- 服务记录信息 -->
      <el-card class="record-info">
        <template #header>
          <span>服务记录信息</span>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="老人姓名">{{ record?.elderlyName }}</el-descriptions-item>
          <el-descriptions-item label="服务时间">{{ formatDateTime(record?.serviceTime) }}</el-descriptions-item>
          <el-descriptions-item label="服务内容" :span="2">{{ record?.serviceContent }}</el-descriptions-item>
          <el-descriptions-item label="服务提供者">{{ record?.serviceProviderName }}</el-descriptions-item>
          <el-descriptions-item label="工单价格">
            <span v-if="record?.workOrderPrice">￥{{ record.workOrderPrice }}</span>
            <span v-else>-</span>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 评价表单 -->
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="evaluation-form"
      >
        <el-form-item label="评价分数" prop="score">
          <el-rate
            v-model="form.score"
            :max="5"
            show-text
            :texts="['极差', '失望', '一般', '满意', '惊喜']"
            text-color="#ff9900"
          ></el-rate>
        </el-form-item>
        
        <el-form-item label="评价内容" prop="comment">
          <el-input
            v-model="form.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入您对本次服务的评价..."
            maxlength="500"
            show-word-limit
          ></el-input>
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          提交评价
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { serviceRecordApi } from '@/api/serviceRecord'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  record: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

// 对话框显示控制
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 表单引用
const formRef = ref()
const submitLoading = ref(false)

// 表单数据
const form = ref({
  score: 5,
  comment: ''
})

// 表单验证规则
const rules = {
  score: [
    { required: true, message: '请选择评价分数', trigger: 'change' }
  ],
  comment: [
    { required: true, message: '请输入评价内容', trigger: 'blur' },
    { min: 10, message: '评价内容至少10个字符', trigger: 'blur' }
  ]
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 重置表单
const resetForm = () => {
  form.value = {
    score: 5,
    comment: ''
  }
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

// 提交评价
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true
    
    await serviceRecordApi.evaluate(props.record.id, {
      score: form.value.score,
      comment: form.value.comment
    })
    
    ElMessage.success('评价提交成功')
    emit('success')
    handleCancel()
  } catch (error) {
    if (error.errors) {
      // 表单验证错误
      return
    }
    ElMessage.error('评价提交失败')
    console.error('评价提交失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 取消操作
const handleCancel = () => {
  visible.value = false
  resetForm()
}

// 监听对话框显示
watch(visible, (newVal) => {
  if (newVal) {
    resetForm()
  }
})
</script>

<style scoped>
.evaluation-content {
  margin-bottom: 20px;
}

.record-info {
  margin-bottom: 20px;
}

.evaluation-form {
  margin-top: 20px;
}

.dialog-footer {
  text-align: right;
}
</style> 