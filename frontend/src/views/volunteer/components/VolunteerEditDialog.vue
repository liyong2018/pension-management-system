<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '编辑志愿者' : '添加志愿者'"
    width="900px"
    :close-on-click-modal="false"
    destroy-on-close
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="120px"
      class="volunteer-form"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="formData.name" placeholder="请输入姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="性别" prop="gender">
            <el-select v-model="formData.gender" placeholder="请选择性别" style="width: 100%">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="年龄" prop="age">
            <el-input-number v-model="formData.age" :min="1" :max="120" placeholder="请输入年龄" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系方式" prop="phone">
            <el-input v-model="formData.phone" placeholder="请输入手机号" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="身份证号" prop="idCardNumber">
            <el-input v-model="formData.idCardNumber" placeholder="请输入身份证号" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="formData.email" placeholder="请输入邮箱" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="详细地址" prop="addressDetail">
            <el-input v-model="formData.addressDetail" placeholder="请输入详细地址" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="户籍地址" prop="householdAddress">
            <el-input v-model="formData.householdAddress" placeholder="请输入户籍地址" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="学历" prop="education">
            <el-select v-model="formData.education" placeholder="请选择学历" style="width: 100%">
              <el-option label="小学" value="小学" />
              <el-option label="初中" value="初中" />
              <el-option label="高中" value="高中" />
              <el-option label="中专" value="中专" />
              <el-option label="大专" value="大专" />
              <el-option label="本科" value="本科" />
              <el-option label="硕士" value="硕士" />
              <el-option label="博士" value="博士" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="毕业院校" prop="graduationSchool">
            <el-input v-model="formData.graduationSchool" placeholder="请输入毕业院校" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="职业" prop="occupation">
            <el-input v-model="formData.occupation" placeholder="请输入职业" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="工作单位" prop="workUnit">
            <el-input v-model="formData.workUnit" placeholder="请输入工作单位" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="专业技能" prop="professionalSkills">
            <el-input
              v-model="formData.professionalSkills"
              type="textarea"
              :rows="3"
              placeholder="请输入专业技能"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="兴趣爱好" prop="hobbies">
            <el-input
              v-model="formData.hobbies"
              type="textarea"
              :rows="3"
              placeholder="请输入兴趣爱好"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="语言能力" prop="languageAbility">
            <el-input v-model="formData.languageAbility" placeholder="请输入语言能力" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="服务意向" prop="serviceIntention">
            <el-input
              v-model="formData.serviceIntention"
              type="textarea"
              :rows="3"
              placeholder="请输入服务意向"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="可服务时间" prop="availableTime">
            <el-input
              v-model="formData.availableTime"
              type="textarea"
              :rows="3"
              placeholder="请输入可服务时间"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="服务经验" prop="serviceExperience">
            <el-input
              v-model="formData.serviceExperience"
              type="textarea"
              :rows="3"
              placeholder="请输入服务经验"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="紧急联系人" prop="emergencyContactName">
            <el-input v-model="formData.emergencyContactName" placeholder="请输入紧急联系人" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="关系" prop="emergencyContactRelationship">
            <el-input v-model="formData.emergencyContactRelationship" placeholder="请输入关系" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="紧急联系电话" prop="emergencyContactPhone">
            <el-input v-model="formData.emergencyContactPhone" placeholder="请输入紧急联系电话" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
              <el-option label="活跃" value="ACTIVE" />
              <el-option label="不活跃" value="INACTIVE" />
              <el-option label="待审核" value="PENDING_APPROVAL" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="注册日期" prop="registrationDate">
            <el-date-picker
              v-model="formData.registrationDate"
              type="date"
              placeholder="请选择注册日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          {{ isEdit ? '更新' : '创建' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
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

const isEdit = computed(() => !!props.volunteer?.id)

// 表单数据
const formData = ref({
  name: '',
  gender: '',
  age: null,
  idCardNumber: '',
  phone: '',
  email: '',
  addressDetail: '',
  householdAddress: '',
  education: '',
  graduationSchool: '',
  occupation: '',
  workUnit: '',
  professionalSkills: '',
  hobbies: '',
  languageAbility: '',
  serviceIntention: '',
  availableTime: '',
  serviceExperience: '',
  emergencyContactName: '',
  emergencyContactRelationship: '',
  emergencyContactPhone: '',
  registrationDate: '',
  status: 'ACTIVE'
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  idCardNumber: [
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ]
}

// 监听志愿者数据变化
watch(() => props.volunteer, (newVolunteer) => {
  if (newVolunteer) {
    // 编辑模式，填充表单数据
    Object.keys(formData.value).forEach(key => {
      if (newVolunteer[key] !== undefined) {
        formData.value[key] = newVolunteer[key]
      }
    })
  } else {
    // 新增模式，重置表单
    resetForm()
  }
}, { immediate: true })

// 重置表单
const resetForm = () => {
  formData.value = {
    name: '',
    gender: '',
    age: null,
    idCardNumber: '',
    phone: '',
    email: '',
    addressDetail: '',
    householdAddress: '',
    education: '',
    graduationSchool: '',
    occupation: '',
    workUnit: '',
    professionalSkills: '',
    hobbies: '',
    languageAbility: '',
    serviceIntention: '',
    availableTime: '',
    serviceExperience: '',
    emergencyContactName: '',
    emergencyContactRelationship: '',
    emergencyContactPhone: '',
    registrationDate: '',
    status: 'ACTIVE'
  }
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
  resetForm()
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    let response
    if (isEdit.value) {
      response = await volunteerApi.updateVolunteer(props.volunteer.id, formData.value)
    } else {
      response = await volunteerApi.createVolunteer(formData.value)
    }

    if (response.success) {
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      emit('success')
      handleClose()
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    if (error !== false) { // 表单验证失败时不显示错误
      ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.volunteer-form {
  max-height: 600px;
  overflow-y: auto;
}
</style> 