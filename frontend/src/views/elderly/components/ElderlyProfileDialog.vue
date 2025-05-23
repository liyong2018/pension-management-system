<template>
  <el-dialog
    :title="dialogTitle"
    v-model="visible"
    :close-on-click-modal="false"
    width="80%"
    destroy-on-close
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      :disabled="mode === 'view'"
    >
      <el-tabs v-model="activeTab">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="姓名" prop="name">
                <el-input v-model="form.name" placeholder="请输入姓名"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="性别" prop="gender">
                <el-select v-model="form.gender" placeholder="请选择性别">
                  <el-option label="男" value="男"></el-option>
                  <el-option label="女" value="女"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="出生日期" prop="birthDate">
                <el-date-picker
                  v-model="form.birthDate"
                  type="date"
                  placeholder="请选择出生日期"
                  value-format="YYYY-MM-DD"
                ></el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="身份证号" prop="idCardNumber">
                <el-input v-model="form.idCardNumber" placeholder="请输入身份证号"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="联系电话" prop="phone">
                <el-input v-model="form.phone" placeholder="请输入联系电话"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="照片" prop="photoUrl">
                <el-upload
                  class="avatar-uploader"
                  action="/api/upload"
                  name="file"
                  :show-file-list="false"
                  :on-success="handlePhotoSuccess"
                  :on-error="handlePhotoError"
                  :before-upload="beforePhotoUpload"
                  :headers="uploadHeaders"
                  accept="image/*"
                >
                  <img 
                    v-if="form.photoUrl" 
                    :src="form.photoUrl" 
                    class="avatar"
                    @error="handleImageError"
                  >
                  <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                </el-upload>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="16">
              <el-form-item label="家庭住址" prop="addressDetail">
                <el-input v-model="form.addressDetail" placeholder="请输入详细地址"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="所属社区" prop="community">
                <el-select v-model="form.community" placeholder="请选择所属社区">
                  <el-option
                    v-for="item in communityOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="养老类型" prop="pensionType">
                <el-select v-model="form.pensionType" placeholder="请选择养老类型">
                  <el-option label="居家养老" value="居家养老"></el-option>
                  <el-option label="社区养老" value="社区养老"></el-option>
                  <el-option label="机构养老" value="机构养老"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8" v-if="form.pensionType === '机构养老'">
              <el-form-item label="所属机构" prop="organizationId">
                <el-select v-model="form.organizationId" placeholder="请选择所属机构">
                  <el-option
                    v-for="org in organizationOptions"
                    :key="org.id"
                    :label="org.name"
                    :value="org.id"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-tab-pane>

        <!-- 健康信息 -->
        <el-tab-pane label="健康信息" name="health">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="过往病史" prop="medicalHistory">
                <el-input
                  v-model="form.medicalHistory"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入过往病史"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="过敏史" prop="allergyHistory">
                <el-input
                  v-model="form.allergyHistory"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入过敏史"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="体检报告" prop="physicalExamReport">
                <el-input
                  v-model="form.physicalExamReport"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入体检报告关键信息"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="当前健康状况" prop="currentHealthStatus">
                <el-input
                  v-model="form.currentHealthStatus"
                  placeholder="请输入当前健康状况"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="护理等级" prop="careLevel">
                <el-select v-model="form.careLevel" placeholder="请选择护理等级">
                  <el-option label="1级护理" value="1级护理"></el-option>
                  <el-option label="2级护理" value="2级护理"></el-option>
                  <el-option label="3级护理" value="3级护理"></el-option>
                  <el-option label="4级护理" value="4级护理"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="能力评估" prop="abilityAssessment">
                <el-select v-model="form.abilityAssessment" placeholder="请选择能力评估">
                  <el-option label="能力完好" value="能力完好"></el-option>
                  <el-option label="轻度失能" value="轻度失能"></el-option>
                  <el-option label="中度失能" value="中度失能"></el-option>
                  <el-option label="重度失能" value="重度失能"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-tab-pane>

        <!-- 生活信息 -->
        <el-tab-pane label="生活信息" name="life">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="生活习惯" prop="livingHabits">
                <el-input
                  v-model="form.livingHabits"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入生活习惯"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="兴趣爱好" prop="hobbies">
                <el-input
                  v-model="form.hobbies"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入兴趣爱好"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="宗教信仰" prop="religiousBelief">
                <el-input
                  v-model="form.religiousBelief"
                  placeholder="请输入宗教信仰"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-tab-pane>

        <!-- 家属信息 -->
        <el-tab-pane label="家属信息" name="family">
          <div class="family-members-header">
            <h3>家属列表</h3>
            <el-button
              v-if="mode !== 'view'"
              type="primary"
              @click="handleAddFamilyMember"
            >添加家属</el-button>
          </div>
          
          <el-table :data="form.familyMembers" border>
            <el-table-column prop="name" label="姓名" width="150">
              <template #default="{ row, $index }">
                <el-input
                  v-if="mode !== 'view'"
                  v-model="row.name"
                  placeholder="请输入姓名"
                  :class="{ 'is-error': familyMemberErrors[$index]?.name }"
                  @blur="validateFamilyMember($index, 'name')"
                ></el-input>
                <span v-else>{{ row.name }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="relationship" label="与老人关系" width="150">
              <template #default="{ row, $index }">
                <el-select
                  v-if="mode !== 'view'"
                  v-model="row.relationship"
                  placeholder="请选择关系"
                  :class="{ 'is-error': familyMemberErrors[$index]?.relationship }"
                  @change="validateFamilyMember($index, 'relationship')"
                >
                  <el-option label="儿子" value="儿子"></el-option>
                  <el-option label="女儿" value="女儿"></el-option>
                  <el-option label="儿媳" value="儿媳"></el-option>
                  <el-option label="女婿" value="女婿"></el-option>
                  <el-option label="配偶" value="配偶"></el-option>
                  <el-option label="父亲" value="父亲"></el-option>
                  <el-option label="母亲" value="母亲"></el-option>
                  <el-option label="兄弟" value="兄弟"></el-option>
                  <el-option label="姐妹" value="姐妹"></el-option>
                  <el-option label="孙子" value="孙子"></el-option>
                  <el-option label="孙女" value="孙女"></el-option>
                  <el-option label="其他亲属" value="其他亲属"></el-option>
                </el-select>
                <span v-else>{{ row.relationship }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="phone" label="联系电话" width="150">
              <template #default="{ row, $index }">
                <el-input
                  v-if="mode !== 'view'"
                  v-model="row.phone"
                  placeholder="请输入电话"
                  :class="{ 'is-error': familyMemberErrors[$index]?.phone }"
                  @blur="validateFamilyMember($index, 'phone')"
                ></el-input>
                <span v-else>{{ row.phone }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" v-if="mode !== 'view'">
              <template #default="{ $index }">
                <el-button
                  type="danger"
                  link
                  @click="handleRemoveFamilyMember($index)"
                >删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 家属信息错误提示 -->
          <div v-if="familyMemberErrors.length > 0" class="family-errors">
            <div v-for="(error, index) in familyMemberErrors" :key="index" class="error-item">
              <span v-if="error && Object.keys(error).length > 0" class="error-text">
                第{{ index + 1 }}行家属信息：
                <span v-if="error.name">姓名不能为空；</span>
                <span v-if="error.relationship">请选择与老人关系；</span>
                <span v-if="error.phone">{{ error.phone }}；</span>
              </span>
            </div>
          </div>
        </el-tab-pane>

        <!-- 备注信息 -->
        <el-tab-pane label="备注信息" name="remarks">
          <el-form-item label="备注" prop="remarks">
            <el-input
              v-model="form.remarks"
              type="textarea"
              :rows="6"
              placeholder="请输入备注信息"
            ></el-input>
          </el-form-item>
        </el-tab-pane>
      </el-tabs>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取 消</el-button>
        <el-button v-if="mode !== 'view'" type="primary" @click="handleSubmit">确 定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { elderlyProfileApi } from '@/api/elderlyProfile'
import organizationService from '@/services/organizationService'
import { Plus } from '@element-plus/icons-vue'
console.log('--- ElderlyProfileDialog.vue SCRIPT SETUP EXECUTING --- v2');
const props = defineProps({
  modelValue: Boolean,
  mode: {
    type: String,
    default: 'add'
  },
  elderlyId: [String, Number]
})

console.log('ElderlyProfileDialog: Initial props received:', JSON.parse(JSON.stringify(props)));

const emit = defineEmits(['update:modelValue', 'success'])

// 表单ref
const formRef = ref(null)

// 对话框可见性
const visible = computed({
  get: () => {
    // console.log(`Computed visible GET: props.modelValue is ${props.modelValue}`);
    return props.modelValue;
  },
  set: (val) => emit('update:modelValue', val)
})

console.log('ElderlyProfileDialog: Before watch registration. Initial visible.value:', visible.value);

// 当前激活的标签页
const activeTab = ref('basic')

// 表单数据
const form = ref({
  name: '',
  gender: '',
  birthDate: '',
  idCardNumber: '',
  phone: '',
  photoUrl: '',
  addressDetail: '',
  community: '',
  pensionType: '',
  organizationId: null,
  medicalHistory: '',
  allergyHistory: '',
  physicalExamReport: '',
  currentHealthStatus: '',
  careLevel: '',
  abilityAssessment: '',
  livingHabits: '',
  hobbies: '',
  religiousBelief: '',
  remarks: '',
  familyMembers: []
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  birthDate: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
  idCardNumber: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  addressDetail: [{ required: true, message: '请输入家庭住址', trigger: 'blur' }],
  community: [{ required: true, message: '请选择所属社区', trigger: 'change' }],
  pensionType: [{ required: true, message: '请选择养老类型', trigger: 'change' }]
}

// 社区选项（示例数据，实际应该从后端获取）
const communityOptions = [
  { value: '阳光社区', label: '阳光社区' },
  { value: '春晖社区', label: '春晖社区' },
  { value: '德胜社区', label: '德胜社区' },
  { value: '东直门社区', label: '东直门社区' }
]

// 机构选项
const organizationOptions = ref([])

// 家属信息验证错误
const familyMemberErrors = ref([])

// 对话框标题
const dialogTitle = computed(() => {
  const titles = {
    add: '新增人员档案',
    edit: '编辑人员档案',
    view: '查看人员档案'
  }
  return titles[props.mode]
})

// 获取机构列表
const fetchOrganizations = async () => {
  try {
    const res = await organizationService.getOrganizations({ pageNum: 1, pageSize: 500 })
    organizationOptions.value = res.list || []
  } catch (error) {
    console.error('获取机构列表失败:', error)
    ElMessage.error('获取机构下拉列表失败')
    organizationOptions.value = []
  }
}

// 获取老人档案详情
const fetchElderlyProfile = async () => {
  if (!props.elderlyId) {
    console.warn('fetchElderlyProfile: elderlyId is missing');
    return;
  }
  console.log(`fetchElderlyProfile: Fetching details for elderlyId: ${props.elderlyId}`);
  try {
    const data = await elderlyProfileApi.getById(props.elderlyId)
    console.log('fetchElderlyProfile: Data received from API:', data);
    if (data) {
      Object.assign(form.value, data)
      // 确保 familyMembers 也是响应式的，如果API返回的 data.familyMembers 不是数组，则设为空数组
      form.value.familyMembers = Array.isArray(data.familyMembers) ? data.familyMembers : [];
    } else {
      console.warn('fetchElderlyProfile: Received null or undefined data from API');
      ElMessage.error('获取到的人员档案数据为空');
    }
  } catch (error) {
    ElMessage.error('获取人员档案详情失败')
    console.error('获取人员档案详情失败:', error)
  }
}

// 处理照片上传成功
const handlePhotoSuccess = (response) => {
  if (response.success) {
    form.value.photoUrl = response.url
    ElMessage.success('照片上传成功')
  } else {
    ElMessage.error(response.message || '照片上传失败')
  }
}

// 处理照片上传错误
const handlePhotoError = (error) => {
  ElMessage.error('照片上传失败，请检查网络连接')
  console.error('照片上传错误:', error)
}

// 上传前验证
const beforePhotoUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

// 上传头部信息（包含token）
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { 'Authorization': `Bearer ${token}` } : {}
})

// 处理图片加载错误
const handleImageError = () => {
  ElMessage.error('图片加载失败，请检查网络连接')
}

// 处理添加家属
const handleAddFamilyMember = () => {
  form.value.familyMembers.push({
    name: '',
    relationship: '',
    phone: ''
  })
  // 添加对应的错误状态
  familyMemberErrors.value.push({})
}

// 处理删除家属
const handleRemoveFamilyMember = (index) => {
  form.value.familyMembers.splice(index, 1)
  familyMemberErrors.value.splice(index, 1)
}

// 验证家属信息
const validateFamilyMember = (index, field) => {
  if (!familyMemberErrors.value[index]) {
    familyMemberErrors.value[index] = {}
  }
  
  const member = form.value.familyMembers[index]
  const errors = familyMemberErrors.value[index]
  
  switch (field) {
    case 'name':
      if (!member.name || member.name.trim() === '') {
        errors.name = '姓名不能为空'
      } else {
        delete errors.name
      }
      break
    case 'relationship':
      if (!member.relationship || member.relationship.trim() === '') {
        errors.relationship = '请选择与老人关系'
      } else {
        delete errors.relationship
      }
      break
    case 'phone':
      if (!member.phone || member.phone.trim() === '') {
        errors.phone = '联系电话不能为空'
      } else if (!/^1[3-9]\d{9}$/.test(member.phone)) {
        errors.phone = '请输入正确的手机号码'
      } else {
        delete errors.phone
      }
      break
  }
}

// 验证所有家属信息
const validateAllFamilyMembers = () => {
  let isValid = true
  familyMemberErrors.value = []
  
  form.value.familyMembers.forEach((member, index) => {
    familyMemberErrors.value[index] = {}
    
    // 验证姓名
    if (!member.name || member.name.trim() === '') {
      familyMemberErrors.value[index].name = '姓名不能为空'
      isValid = false
    }
    
    // 验证关系
    if (!member.relationship || member.relationship.trim() === '') {
      familyMemberErrors.value[index].relationship = '请选择与老人关系'
      isValid = false
    }
    
    // 验证电话
    if (!member.phone || member.phone.trim() === '') {
      familyMemberErrors.value[index].phone = '联系电话不能为空'
      isValid = false
    } else if (!/^1[3-9]\d{9}$/.test(member.phone)) {
      familyMemberErrors.value[index].phone = '请输入正确的手机号码'
      isValid = false
    }
  })
  
  return isValid
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    // 验证基本表单
    await formRef.value.validate()
    
    // 验证家属信息
    if (!validateAllFamilyMembers()) {
      ElMessage.error('请完善家属信息')
      // 切换到家属信息标签页
      activeTab.value = 'family'
      return
    }
    
    if (props.mode === 'add') {
      await elderlyProfileApi.create(form.value)
      ElMessage.success('添加成功')
    } else {
      await elderlyProfileApi.update(props.elderlyId, form.value)
      ElMessage.success('更新成功')
    }
    
    visible.value = false
    emit('success')
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error(props.mode === 'add' ? '添加失败' : '更新失败')
    }
    console.error(props.mode === 'add' ? '添加失败:' : '更新失败:', error)
  }
}

// 监听对话框显示和 elderlyId 变化来加载数据
watch([() => props.modelValue, () => props.elderlyId], async ([newVisible, newElderlyId], [oldVisible, oldElderlyId]) => {
  console.log(`>>> Dialog WATCH EXECUTED. Visible: ${newVisible} (was ${oldVisible}), ElderlyID: ${newElderlyId} (was ${oldElderlyId}), Mode: ${props.mode}`);
  if (newVisible) {
    // 重置表单
    if (formRef.value) {
      console.log('Dialog watch: Resetting form fields');
      formRef.value.resetFields();
    }
    Object.keys(form.value).forEach(key => {
      if (Array.isArray(form.value[key])) {
        form.value[key] = [];
      } else if (typeof form.value[key] === 'object' && form.value[key] !== null) {
        form.value[key] = {}; 
      } else {
        form.value[key] = typeof form.value[key] === 'number' ? 0 : '';
      }
    });
    form.value.familyMembers = []; 
    // 重置家属信息错误状态
    familyMemberErrors.value = []
    console.log('Dialog watch: Form reset complete. Current form value:', JSON.parse(JSON.stringify(form.value)));

    console.log('Dialog watch: Fetching organizations');
    await fetchOrganizations();
    
    if (props.mode !== 'add' && newElderlyId) {
      console.log(`Dialog watch: Mode is ${props.mode} and elderlyId is ${newElderlyId}. Fetching elderly profile.`);
      await fetchElderlyProfile(); 
    } else if (props.mode !== 'add' && !newElderlyId) {
      console.warn('Dialog watch: Mode is for view/edit, but elderlyId is missing.');
    } else {
      console.log('Dialog watch: Mode is add, or no elderlyId. Skipping fetchElderlyProfile.');
    }
  } else {
    console.log('Dialog watch: Dialog is not visible (newVisible is false).');
  }
}, { immediate: true }); // Set immediate: true to see if it runs on setup
</script>

<style scoped>
.avatar-uploader {
  text-align: center;
}

.avatar-uploader .avatar {
  width: 100px;
  height: 100px;
  border-radius: 6px;
  object-fit: cover;
  display: block;
  border: 1px solid var(--el-border-color);
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.family-members-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.family-members-header h3 {
  margin: 0;
}

.family-errors {
  margin-top: 16px;
}

.error-item {
  margin-bottom: 8px;
}

.error-text {
  color: var(--el-color-danger);
  font-size: 14px;
}

.is-error {
  border-color: var(--el-color-danger) !important;
}

.is-error .el-input__wrapper {
  border-color: var(--el-color-danger) !important;
}

.is-error .el-select__wrapper {
  border-color: var(--el-color-danger) !important;
}
</style> 