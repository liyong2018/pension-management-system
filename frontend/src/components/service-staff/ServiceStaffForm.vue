<template>
  <el-dialog
    :model-value="visible"
    :title="isEdit ? '编辑人员' : '新增人员'"
    width="800px"
    :before-close="handleClose"
    destroy-on-close
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      class="staff-form"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="formData.name" placeholder="请输入姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="工号" prop="employeeId">
            <el-input v-model="formData.employeeId" placeholder="请输入工号" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="formData.gender">
              <el-radio label="男">男</el-radio>
              <el-radio label="女">女</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="身份证号" prop="idCard">
            <el-input v-model="formData.idCard" placeholder="请输入身份证号" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="formData.phone" placeholder="请输入联系电话" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="职位" prop="position">
            <el-select v-model="formData.position" placeholder="请选择职位" style="width: 100%">
              <el-option label="护理员" value="护理员" />
              <el-option label="医生" value="医生" />
              <el-option label="护士" value="护士" />
              <el-option label="康复师" value="康复师" />
              <el-option label="营养师" value="营养师" />
              <el-option label="社工" value="社工" />
              <el-option label="管理员" value="管理员" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
              <el-option label="在职" value="ACTIVE" />
              <el-option label="离职" value="INACTIVE" />
              <el-option label="停职" value="SUSPENDED" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="工作类型" prop="workType">
            <el-select v-model="formData.workType" placeholder="请选择工作类型" style="width: 100%">
              <el-option label="全职" value="FULL_TIME" />
              <el-option label="兼职" value="PART_TIME" />
              <el-option label="合同工" value="CONTRACT" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="所属机构" prop="organizationId">
            <el-select
              v-model="formData.organizationId"
              placeholder="请选择机构"
              filterable
              style="width: 100%"
              :loading="organizationsLoading"
            >
              <el-option
                v-for="org in organizations"
                :key="org.id"
                :label="org.name"
                :value="org.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="入职时间" prop="hireDate">
            <el-date-picker
              v-model="formData.hireDate"
              type="date"
              placeholder="请选择入职时间"
              style="width: 100%"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="学历" prop="education">
            <el-select v-model="formData.education" placeholder="请选择学历" style="width: 100%">
              <el-option label="初中及以下" value="初中及以下" />
              <el-option label="高中/中专" value="高中/中专" />
              <el-option label="大专" value="大专" />
              <el-option label="本科" value="本科" />
              <el-option label="硕士" value="硕士" />
              <el-option label="博士" value="博士" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="紧急联系人" prop="emergencyContact">
            <el-input v-model="formData.emergencyContact" placeholder="请输入紧急联系人" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="联系人关系" prop="emergencyRelation">
            <el-input v-model="formData.emergencyRelation" placeholder="请输入关系" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="紧急联系电话" prop="emergencyPhone">
            <el-input v-model="formData.emergencyPhone" placeholder="请输入紧急联系电话" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="家庭住址" prop="address">
            <el-input
              v-model="formData.address"
              type="textarea"
              :rows="2"
              placeholder="请输入家庭住址"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="专业技能" prop="skills">
            <el-input
              v-model="formData.skills"
              type="textarea"
              :rows="2"
              placeholder="请输入专业技能描述"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="备注" prop="remarks">
            <el-input
              v-model="formData.remarks"
              type="textarea"
              :rows="3"
              placeholder="请输入备注信息"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          {{ isEdit ? '更新' : '创建' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import serviceStaffService from '@/services/serviceStaffService';
import organizationService from '@/services/organizationService';

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  staffId: {
    type: [String, Number],
    default: null
  }
});

// Emits
const emit = defineEmits(['close', 'success']);

// 响应式数据
const formRef = ref();
const submitLoading = ref(false);
const organizations = ref([]);
const organizationsLoading = ref(false);

// 计算属性
const isEdit = computed(() => !!props.staffId);

// 表单数据
const formData = reactive({
  name: '',
  employeeId: '',
  gender: '男',
  age: null,
  idCard: '',
  phone: '',
  position: '',
  workType: 'FULL_TIME',
  status: 'ACTIVE',
  organizationId: '',
  hireDate: '',
  education: '',
  workExperience: 0,
  emergencyContact: '',
  emergencyRelation: '',
  emergencyPhone: '',
  address: '',
  skills: '',
  remarks: ''
});

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  employeeId: [
    { required: true, message: '请输入工号', trigger: 'blur' },
    { min: 3, max: 20, message: '工号长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  // 移除年龄验证规则
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  position: [
    { required: true, message: '请选择职位', trigger: 'change' }
  ],
  workType: [
    { required: true, message: '请选择工作类型', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  organizationId: [
    { required: true, message: '请选择所属机构', trigger: 'change' }
  ],
  hireDate: [
    { required: true, message: '请选择入职时间', trigger: 'change' }
  ],
  emergencyPhone: [
    { pattern: /^1[3-9]\d{9}$/, message: '紧急联系电话格式不正确', trigger: 'blur' }
  ]
};

// 获取机构列表
const fetchOrganizations = async () => {
  organizationsLoading.value = true;
  try {
    const response = await organizationService.getOrganizations({ pageSize: 1000 });
    if (response && response.list) {
      organizations.value = response.list;
    }
  } catch (error) {
    console.error("Failed to fetch organizations:", error);
    ElMessage.error('获取机构列表失败');
  } finally {
    organizationsLoading.value = false;
  }
};

// 获取人员详情
const fetchStaffDetail = async () => {
  if (!props.staffId) return;
  
  try {
    const response = await serviceStaffService.getStaffDetail(props.staffId);
    if (response) {
      Object.keys(formData).forEach(key => {
        if (response[key] !== undefined) {
          formData[key] = response[key];
        }
      });
    }
  } catch (error) {
    console.error("Failed to fetch staff detail:", error);
    ElMessage.error('获取人员详情失败');
  }
};

// 重置表单
const resetForm = () => {
  Object.keys(formData).forEach(key => {
    if (key === 'gender') {
      formData[key] = '男';
    } else if (key === 'status') {
      formData[key] = 'ACTIVE';
    } else if (key === 'workType') {
      formData[key] = 'FULL_TIME';
    } else if (key === 'workExperience') {
      formData[key] = 0;
    } else {
      formData[key] = key === 'age' ? null : '';
    }
  });
  formRef.value?.clearValidate();
};

// 处理提交
const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    
    // 过滤掉年龄和工作经验字段，这些字段由后端根据出生日期和入职日期计算
    const submitData = { ...formData };
    delete submitData.age;
    delete submitData.workExperience;
    
    if (isEdit.value) {
      await serviceStaffService.updateStaff(props.staffId, submitData);
      ElMessage.success('更新人员信息成功');
    } else {
      await serviceStaffService.createStaff(submitData);
      ElMessage.success('创建人员成功');
    }
    
    emit('success');
  } catch (error) {
    if (error.message) {
      console.error("Failed to submit staff:", error);
      ElMessage.error(error.message || '操作失败');
    }
  } finally {
    submitLoading.value = false;
  }
};

// 处理关闭
const handleClose = () => {
  emit('close');
};

// 监听对话框显示状态
watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      if (isEdit.value) {
        fetchStaffDetail();
      } else {
        resetForm();
      }
    }
  },
  { immediate: true }
);

// 生命周期
onMounted(() => {
  fetchOrganizations();
});
</script>

<style scoped>
.staff-form {
  padding: 20px 0;
}

.dialog-footer {
  text-align: right;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-textarea__inner) {
  resize: none;
}
</style>