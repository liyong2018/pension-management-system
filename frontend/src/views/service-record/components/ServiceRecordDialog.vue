<template>
  <el-dialog
    :title="dialogTitle"
    v-model="visible"
    :close-on-click-modal="false"
    width="70%"
    destroy-on-close
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      :disabled="mode === 'view'"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="老人姓名" prop="elderlyId">
            <el-select
              v-model="form.elderlyId"
              placeholder="请选择老人"
              filterable
              remote
              :remote-method="searchElderly"
              :loading="elderlyLoading"
              @change="handleElderlyChange"
              style="width: 100%"
              popper-class="elderly-select-dropdown"
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
        <el-col :span="12">
          <el-form-item label="服务时间" prop="serviceTime">
            <el-date-picker
              v-model="form.serviceTime"
              type="datetime"
              placeholder="请选择服务时间"
              value-format="YYYY-MM-DD HH:mm:ss"
              format="YYYY-MM-DD HH:mm:ss"
              style="width: 100%"
            ></el-date-picker>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="服务内容" prop="serviceContent">
            <el-input
              v-model="form.serviceContent"
              type="textarea"
              :rows="3"
              placeholder="请输入服务内容"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="服务地址" prop="serviceAddress">
            <el-input v-model="form.serviceAddress" placeholder="请输入服务地址"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="服务类型" prop="serviceType">
            <el-select 
              v-model="form.serviceType" 
              placeholder="请选择服务类型"
              style="width: 100%"
              popper-class="service-type-select-dropdown"
            >
              <el-option
                v-for="option in serviceTypeOptions"
                :key="option.dictCode"
                :label="option.dictLabel"
                :value="option.dictValue"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="服务时长" prop="serviceDuration">
            <el-input-number
              v-model="form.serviceDuration"
              :precision="1"
              :min="0.1"
              :step="0.5"
              placeholder="请输入服务时长"
              style="width: 100%"
            >
              <template #append>小时</template>
            </el-input-number>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="服务提供者类型" prop="serviceProviderType">
            <el-select 
              v-model="form.serviceProviderType" 
              placeholder="请选择服务提供者类型"
              style="width: 100%"
              popper-class="provider-type-select-dropdown"
              @change="handleProviderTypeChange"
            >
              <el-option label="机构员工" value="机构员工"></el-option>
              <el-option label="志愿者" value="志愿者"></el-option>
              <el-option label="外包服务" value="外包服务"></el-option>
              <el-option label="家政服务" value="家政服务"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="服务提供者" prop="serviceProviderName">
            <!-- 当选择志愿者时显示下拉选择 -->
            <el-select
              v-if="form.serviceProviderType === '志愿者'"
              v-model="form.serviceProviderId"
              placeholder="请选择志愿者"
              filterable
              remote
              :remote-method="searchVolunteers"
              :loading="volunteerLoading"
              @change="handleVolunteerChange"
              style="width: 100%"
              popper-class="volunteer-select-dropdown"
              clearable
            >
              <el-option
                v-for="volunteer in volunteerOptions"
                :key="volunteer.id"
                :label="`${volunteer.name} - ${volunteer.occupation || ''}`"
                :value="volunteer.id"
              >
                <div class="volunteer-option">
                  <span class="volunteer-name">{{ volunteer.name }}</span>
                  <span class="volunteer-info">{{ volunteer.occupation || '' }} | {{ volunteer.phone || '' }}</span>
                </div>
              </el-option>
            </el-select>
            <!-- 其他类型时显示输入框 -->
            <el-input 
              v-else
              v-model="form.serviceProviderName" 
              placeholder="请输入服务提供者姓名"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="工单价格" prop="workOrderPrice">
            <el-input-number
              v-model="form.workOrderPrice"
              :precision="2"
              :min="0"
              placeholder="请输入工单价格"
              style="width: 100%"
            ></el-input-number>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="状态" prop="status">
            <el-select 
              v-model="form.status" 
              placeholder="请选择状态"
              style="width: 100%"
              popper-class="status-select-dropdown"
            >
              <el-option label="待处理" value="待处理"></el-option>
              <el-option label="进行中" value="进行中"></el-option>
              <el-option label="已完成" value="已完成"></el-option>
              <el-option label="已评价" value="已评价"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8" v-if="form.evaluationScore">
          <el-form-item label="评价分数">
            <el-rate v-model="form.evaluationScore" disabled show-score text-color="#ff9900"></el-rate>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20" v-if="form.evaluationComment">
        <el-col :span="24">
          <el-form-item label="评价内容">
            <el-input
              v-model="form.evaluationComment"
              type="textarea"
              :rows="3"
              disabled
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button v-if="mode !== 'view'" type="primary" @click="handleSubmit" :loading="submitLoading">
          {{ mode === 'add' ? '创建' : '更新' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { serviceRecordApi } from '@/api/serviceRecord'
import { elderlyProfileApi } from '@/api/elderlyProfile'
import { volunteerApi } from '@/api/volunteer'
import { dictionaryApi } from '@/api/dictionary'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  mode: {
    type: String,
    default: 'view', // view, add, edit
    validator: (value) => ['view', 'add', 'edit'].includes(value)
  },
  recordId: {
    type: [Number, String],
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

// 对话框显示控制
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 对话框标题
const dialogTitle = computed(() => {
  const titles = {
    view: '查看服务记录',
    add: '新增服务记录',
    edit: '编辑服务记录'
  }
  return titles[props.mode]
})

// 表单引用
const formRef = ref()
const submitLoading = ref(false)

// 老人选择相关
const elderlyOptions = ref([])
const elderlyLoading = ref(false)

// 志愿者选择相关
const volunteerOptions = ref([])
const volunteerLoading = ref(false)

// 服务类型选项
const serviceTypeOptions = ref([])

// 表单数据
const form = ref({
  elderlyId: null,
  serviceContent: '',
  serviceTime: '',
  serviceAddress: '',
  serviceType: '',
  serviceDuration: null,
  serviceProviderType: '',
  serviceProviderId: null,
  serviceProviderName: '',
  workOrderPrice: null,
  status: '待处理',
  evaluationScore: null,
  evaluationComment: ''
})

// 表单验证规则
const rules = {
  elderlyId: [
    { required: true, message: '请选择老人', trigger: 'change' }
  ],
  serviceContent: [
    { required: true, message: '请输入服务内容', trigger: 'blur' }
  ],
  serviceTime: [
    { required: true, message: '请选择服务时间', trigger: 'change' }
  ]
}

// 搜索老人
const searchElderly = async (query) => {
  if (query !== '') {
    try {
      elderlyLoading.value = true
      const res = await elderlyProfileApi.getList({
        name: query,
        pageNum: 1,
        pageSize: 20
      })
      elderlyOptions.value = res.list || []
    } catch (error) {
      console.error('搜索老人失败:', error)
      ElMessage.error('搜索老人失败')
    } finally {
      elderlyLoading.value = false
    }
  } else {
    // 如果查询为空，加载所有老人
    try {
      elderlyLoading.value = true
      const res = await elderlyProfileApi.getList({
        pageNum: 1,
        pageSize: 20
      })
      elderlyOptions.value = res.list || []
    } catch (error) {
      console.error('加载老人列表失败:', error)
    } finally {
      elderlyLoading.value = false
    }
  }
}

// 老人选择变化
const handleElderlyChange = (elderlyId) => {
  const selectedElderly = elderlyOptions.value.find(item => item.id === elderlyId)
  if (selectedElderly) {
    form.value.serviceAddress = selectedElderly.addressDetail || ''
  }
}

// 搜索志愿者
const searchVolunteers = async (query) => {
  try {
    volunteerLoading.value = true
    const params = {
      page: 1,
      pageSize: 20,
      status: 'ACTIVE' // 只搜索活跃的志愿者
    }
    
    if (query && query.trim() !== '') {
      params.name = query.trim()
    }
    
    const response = await volunteerApi.getVolunteerList(params)
    if (response.success) {
      volunteerOptions.value = response.data.volunteers || []
    } else {
      ElMessage.error('搜索志愿者失败')
      volunteerOptions.value = []
    }
  } catch (error) {
    console.error('搜索志愿者失败:', error)
    ElMessage.error('搜索志愿者失败')
    volunteerOptions.value = []
  } finally {
    volunteerLoading.value = false
  }
}

// 服务提供者类型变化
const handleProviderTypeChange = (type) => {
  // 清空相关字段
  form.value.serviceProviderId = null
  form.value.serviceProviderName = ''
  volunteerOptions.value = []
  
  console.log('服务提供者类型变化:', type)
  
  // 如果选择了志愿者，加载志愿者列表
  if (type === '志愿者') {
    searchVolunteers('')
  }
}

// 志愿者选择变化
const handleVolunteerChange = (volunteerId) => {
  console.log('志愿者选择变化:', volunteerId)
  
  if (volunteerId) {
    const selectedVolunteer = volunteerOptions.value.find(item => item.id === volunteerId)
    if (selectedVolunteer) {
      form.value.serviceProviderName = selectedVolunteer.name
      form.value.serviceProviderId = selectedVolunteer.id
      console.log('设置志愿者信息:', {
        id: selectedVolunteer.id,
        name: selectedVolunteer.name
      })
    }
  } else {
    // 清空选择
    form.value.serviceProviderName = ''
    form.value.serviceProviderId = null
    console.log('清空志愿者选择')
  }
}

// 重置表单
const resetForm = () => {
  form.value = {
    elderlyId: null,
    serviceContent: '',
    serviceTime: '',
    serviceAddress: '',
    serviceType: '',
    serviceDuration: null,
    serviceProviderType: '',
    serviceProviderId: null,
    serviceProviderName: '',
    workOrderPrice: null,
    status: '待处理',
    evaluationScore: null,
    evaluationComment: ''
  }
  elderlyOptions.value = []
  volunteerOptions.value = []
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

// 加载数据
const loadData = async () => {
  if (props.recordId && (props.mode === 'view' || props.mode === 'edit')) {
    try {
      const res = await serviceRecordApi.getById(props.recordId)
      
      console.log('加载的服务记录数据:', res)
      
      // 处理日期时间格式
      if (res.serviceTime) {
        res.serviceTime = res.serviceTime.replace('T', ' ').substring(0, 19)
      }
      
      // 复制数据到表单
      const formData = {
        elderlyId: res.elderlyId,
        serviceContent: res.serviceContent || '',
        serviceTime: res.serviceTime || '',
        serviceAddress: res.serviceAddress || '',
        serviceType: res.serviceType || '',
        serviceDuration: res.serviceDuration,
        serviceProviderType: res.serviceProviderType || '',
        serviceProviderId: res.serviceProviderId,
        serviceProviderName: res.serviceProviderName || '',
        workOrderPrice: res.workOrderPrice,
        status: res.status || '待处理',
        evaluationScore: res.evaluationScore,
        evaluationComment: res.evaluationComment || ''
      }
      
      form.value = formData
      
      console.log('设置表单数据:', formData)
      
      // 如果有老人信息，添加到选项中
      if (res.elderlyId && res.elderlyName) {
        elderlyOptions.value = [{
          id: res.elderlyId,
          name: res.elderlyName
        }]
      }
      
      // 如果是志愿者类型，处理志愿者信息
      if (res.serviceProviderType === '志愿者') {
        if (res.serviceProviderId) {
          // 有具体的志愿者ID，尝试加载志愿者详情
          try {
            const volunteerResponse = await volunteerApi.getVolunteerById(res.serviceProviderId)
            if (volunteerResponse.success) {
              volunteerOptions.value = [volunteerResponse.data]
              console.log('加载志愿者详情成功:', volunteerResponse.data)
            } else {
              console.log('志愿者详情加载失败，搜索活跃志愿者')
              await searchVolunteers('')
            }
          } catch (error) {
            console.error('加载志愿者信息失败:', error)
            // 如果获取失败，尝试搜索活跃志愿者
            await searchVolunteers('')
          }
        } else {
          // 没有具体志愿者ID，加载志愿者列表供选择
          console.log('没有志愿者ID，加载志愿者列表')
          await searchVolunteers('')
        }
      }
    } catch (error) {
      console.error('加载服务记录失败:', error)
      ElMessage.error('加载服务记录失败: ' + (error.message || '未知错误'))
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true
    
    const formData = { ...form.value }
    
    if (props.mode === 'add') {
      await serviceRecordApi.create(formData)
      ElMessage.success('创建成功')
    } else if (props.mode === 'edit') {
      await serviceRecordApi.update(props.recordId, formData)
      ElMessage.success('更新成功')
    }
    
    emit('success')
    handleCancel()
  } catch (error) {
    if (error.errors) {
      // 表单验证错误
      return
    }
    ElMessage.error(props.mode === 'add' ? '创建失败' : '更新失败')
  } finally {
    submitLoading.value = false
  }
}

// 取消操作
const handleCancel = () => {
  visible.value = false
  resetForm()
}

// 当对话框打开时自动加载数据
watch([() => props.modelValue, () => props.recordId, () => props.mode], 
  ([newVisible, newRecordId, newMode], [oldVisible, oldRecordId, oldMode]) => {
    if (newVisible) {
      // 重置表单
      resetForm()
      // 延迟加载数据
      nextTick(() => {
        loadData()
      })
    }
  }, 
  { immediate: true }
)

// 组件挂载时初始化老人选项
onMounted(() => {
  // 初始加载一些老人选项供搜索
  searchElderly('')
  // 加载服务类型选项
  loadServiceTypeOptions()
})

// 加载服务类型选项
const loadServiceTypeOptions = async () => {
  try {
    const res = await dictionaryApi.getByType('serviceType')
    serviceTypeOptions.value = res || []
  } catch (error) {
    console.error('加载服务类型选项失败:', error)
    ElMessage.error('加载服务类型选项失败')
  }
}
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}

.avatar-uploader .avatar {
  width: 100px;
  height: 100px;
  display: block;
}
</style>

<style>
/* 下拉框选项宽度样式 */
.elderly-select-dropdown {
  min-width: 200px !important;
  max-width: 300px !important;
}

.provider-type-select-dropdown {
  min-width: 120px !important;
}

.status-select-dropdown {
  min-width: 100px !important;
}

.volunteer-select-dropdown {
  min-width: 300px !important;
  max-width: 400px !important;
}

.elderly-select-dropdown .el-select-dropdown__item,
.provider-type-select-dropdown .el-select-dropdown__item,
.status-select-dropdown .el-select-dropdown__item {
  white-space: nowrap;
  overflow: visible;
  text-overflow: ellipsis;
}

.volunteer-select-dropdown .el-select-dropdown__item {
  height: auto;
  padding: 8px 20px;
  line-height: 1.4;
}

.volunteer-option {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.volunteer-name {
  font-weight: 600;
  color: #303133;
}

.volunteer-info {
  font-size: 12px;
  color: #909399;
}
</style> 