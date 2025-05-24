<template>
  <el-dialog
    v-model="visible"
    title="志愿者详情"
    width="800px"
    :close-on-click-modal="false"
    destroy-on-close
  >
    <div v-if="volunteer" class="volunteer-detail">
      <el-form label-width="120px" :disabled="true">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名">
              <el-input v-model="volunteer.name" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-input v-model="volunteer.gender" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年龄">
              <el-input :value="volunteer.age" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系方式">
              <el-input v-model="volunteer.phone" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="身份证号">
              <el-input v-model="volunteer.idCardNumber" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="邮箱">
              <el-input v-model="volunteer.email" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="详细地址">
              <el-input v-model="volunteer.addressDetail" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="户籍地址">
              <el-input v-model="volunteer.householdAddress" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学历">
              <el-input v-model="volunteer.education" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="毕业院校">
              <el-input v-model="volunteer.graduationSchool" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职业">
              <el-input v-model="volunteer.occupation" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作单位">
              <el-input v-model="volunteer.workUnit" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="专业技能">
              <el-input
                v-model="volunteer.professionalSkills"
                type="textarea"
                :rows="3"
                disabled
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="兴趣爱好">
              <el-input
                v-model="volunteer.hobbies"
                type="textarea"
                :rows="3"
                disabled
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="语言能力">
              <el-input v-model="volunteer.languageAbility" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="服务意向">
              <el-input
                v-model="volunteer.serviceIntention"
                type="textarea"
                :rows="3"
                disabled
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="可服务时间">
              <el-input
                v-model="volunteer.availableTime"
                type="textarea"
                :rows="3"
                disabled
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="服务经验">
              <el-input
                v-model="volunteer.serviceExperience"
                type="textarea"
                :rows="3"
                disabled
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="紧急联系人">
              <el-input v-model="volunteer.emergencyContactName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="关系">
              <el-input v-model="volunteer.emergencyContactRelationship" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="紧急联系电话">
              <el-input v-model="volunteer.emergencyContactPhone" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="状态">
              <el-tag :type="getStatusType(volunteer.status)">
                {{ getStatusText(volunteer.status) }}
              </el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="服务时长">
              <el-input :value="(volunteer.totalServiceHours || 0) + '小时'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="积分">
              <el-input :value="volunteer.points || 0" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="注册日期">
              <el-input v-model="volunteer.registrationDate" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建时间">
              <el-input v-model="volunteer.createTime" disabled />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'

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

const emit = defineEmits(['update:modelValue'])

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const handleClose = () => {
  visible.value = false
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

<style scoped>
.volunteer-detail {
  max-height: 600px;
  overflow-y: auto;
}
</style> 