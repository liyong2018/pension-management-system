<template>
  <el-dialog
    :title="organizationId ? '编辑机构' : '新增机构'"
    v-model="dialogVisible"
    width="60%"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      class="organization-form"
    >
      <el-tabs v-model="activeTab" type="border-card">
        <el-tab-pane label="基本信息" name="basic">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="机构名称" prop="name">
                <el-input v-model="form.name" placeholder="请输入机构名称"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="机构简称" prop="shortName">
                <el-input v-model="form.shortName" placeholder="请输入机构简称"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="机构类型" prop="type">
                <el-select v-model="form.type" placeholder="请选择机构类型" style="width: 100%">
                  <el-option label="机构养老单位" value="机构养老单位"></el-option>
                  <el-option label="社区养老单位" value="社区养老单位"></el-option>
                  <el-option label="居家养老单位" value="居家养老单位"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系电话" prop="phone">
                <el-input v-model="form.phone" placeholder="请输入联系电话"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-form-item label="机构地址" prop="address">
            <el-input v-model="form.address" type="textarea" :rows="3" placeholder="请输入机构地址"></el-input>
          </el-form-item>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="电子邮箱" prop="email">
                <el-input v-model="form.email" placeholder="请输入电子邮箱"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="网址" prop="website">
                <el-input v-model="form.website" placeholder="请输入网址"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
           <el-form-item label="机构描述" prop="description">
            <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入机构描述"></el-input>
          </el-form-item>
        </el-tab-pane>

        <el-tab-pane label="运营信息" name="operation">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="成立时间" prop="establishmentDate">
                <el-date-picker v-model="form.establishmentDate" type="date" placeholder="选择成立时间" style="width: 100%"></el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="许可证号" prop="licenseNumber">
                <el-input v-model="form.licenseNumber" placeholder="请输入许可证号"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="经营范围" prop="businessScope">
            <el-input v-model="form.businessScope" type="textarea" :rows="3" placeholder="请输入经营范围"></el-input>
          </el-form-item>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="床位数量" prop="bedCount">
                <el-input-number v-model="form.bedCount" :min="0" style="width: 100%"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="实际服务人数" prop="actualServiceCount">
                <el-input-number v-model="form.actualServiceCount" :min="0" style="width: 100%"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="机构面积" prop="area">
                <el-input v-model="form.area" placeholder="例如：占地1000平米，建筑500平米"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="收费标准" prop="chargingStandard">
            <el-input v-model="form.chargingStandard" type="textarea" :rows="3" placeholder="请输入收费标准"></el-input>
          </el-form-item>
        </el-tab-pane>

        <el-tab-pane label="人员信息" name="staff">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="负责人姓名" prop="directorName">
                <el-input v-model="form.directorName" placeholder="请输入负责人姓名"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="负责人联系方式" prop="directorContact">
                <el-input v-model="form.directorContact" placeholder="请输入负责人联系方式"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="员工总数" prop="employeeCount">
                <el-input-number v-model="form.employeeCount" :min="0" style="width: 100%"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="专业护理人员数量" prop="professionalNurseCount">
                <el-input-number v-model="form.professionalNurseCount" :min="0" style="width: 100%"></el-input-number>
              </el-form-item>
            </el-col>
          </el-row>
        </el-tab-pane>

        <el-tab-pane label="资质信息" name="qualification">
          <el-form-item label="消防许可证" prop="fireLicense">
            <el-input v-model="form.fireLicense" placeholder="请输入消防许可证号或上传附件链接"></el-input>
          </el-form-item>
          <el-form-item label="卫生许可证" prop="sanitaryLicense">
            <el-input v-model="form.sanitaryLicense" placeholder="请输入卫生许可证号或上传附件链接"></el-input>
          </el-form-item>
          <el-form-item label="医疗机构执业许可证" prop="medicalLicense">
            <el-input v-model="form.medicalLicense" placeholder="请输入医疗机构执业许可证号或上传附件链接"></el-input>
          </el-form-item>
          <el-form-item label="其他资质证书" prop="otherQualifications">
            <el-input v-model="form.otherQualifications" type="textarea" :rows="3" placeholder="请输入其他资质证书说明或上传附件链接"></el-input>
          </el-form-item>
        </el-tab-pane>
      </el-tabs>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取 消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">确 定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue';
import { ElMessage } from 'element-plus';
import organizationService from '@/services/organizationService';

const props = defineProps({
  visible: Boolean,
  organizationId: [Number, String]
});

const emit = defineEmits(['close', 'success']);

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => {
    if (!val) {
      emit('close');
    }
  }
});

const formRef = ref(null);
const loading = ref(false);
const activeTab = ref('basic');

const form = reactive({
  name: '',
  shortName: '',
  type: '',
  address: '', // 单一地址字段
  phone: '',
  email: '',
  website: '',
  description: '',
  establishmentDate: '',
  licenseNumber: '',
  businessScope: '',
  bedCount: 0,
  actualServiceCount: 0,
  chargingStandard: '',
  area: '',
  directorName: '',
  directorContact: '',
  employeeCount: 0,
  professionalNurseCount: 0,
  fireLicense: '',
  sanitaryLicense: '',
  medicalLicense: '',
  otherQualifications: ''
});

const rules = {
  name: [{ required: true, message: '请输入机构名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择机构类型', trigger: 'change' }],
  address: [{ required: true, message: '请输入机构地址', trigger: 'blur' }], // 地址校验
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$|^0\d{2,3}-?\d{7,8}$/, message: '请输入正确的电话号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  directorName: [{ required: true, message: '请输入负责人姓名', trigger: 'blur' }],
  directorContact: [
      { required: true, message: '请输入负责人联系方式', trigger: 'blur' },
      { pattern: /^1[3-9]\d{9}$|^0\d{2,3}-?\d{7,8}$/, message: '请输入正确的电话号码', trigger: 'blur' }
  ]
};

const loadOrganizationData = async () => {
  if (!props.organizationId) return;
  
  try {
    loading.value = true;
    const response = await organizationService.getOrganizationById(props.organizationId);
    // 后端返回的 address 字段直接赋值给 form.address
    Object.assign(form, response.data);
  } catch (error) {
    console.error('Failed to load organization:', error);
    ElMessage.error('加载机构信息失败: ' + (error.response?.data?.message || error.message));
  } finally {
    loading.value = false;
  }
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    loading.value = true;
    
    const payload = { ...form };
    // 不需要再手动拼接 address 字段，因为它已经是单一字段了

    if (props.organizationId) {
      await organizationService.updateOrganization(props.organizationId, payload);
      ElMessage.success('更新成功');
    } else {
      await organizationService.createOrganization(payload);
      ElMessage.success('创建成功');
    }
    
    emit('success');
    handleClose();
  } catch (error) {
    console.error('Failed to save organization:', error);
    ElMessage.error('保存失败: ' + (error.response?.data?.message || error.message));
  } finally {
    loading.value = false;
  }
};

const handleClose = () => {
  formRef.value?.resetFields(); // 重置表单
  activeTab.value = 'basic'; // 重置活动标签页
  // 手动重置非表单直接绑定的响应式数据（如果需要）
  Object.keys(form).forEach(key => {
    if (typeof form[key] === 'string') {
      form[key] = '';
    } else if (typeof form[key] === 'number') {
      form[key] = 0;
    } else {
      form[key] = null; // 或者根据需要设置默认值
    }
  });
  emit('close');
};

watch(() => props.organizationId, (newId) => {
  if (newId) {
    loadOrganizationData();
  } else {
    // 清空表单，因为是新增操作
    formRef.value?.resetFields();
    Object.keys(form).forEach(key => {
        if (typeof form[key] === 'string') {
        form[key] = '';
        } else if (typeof form[key] === 'number') {
        form[key] = 0;
        } else {
        form[key] = null;
        }
    });
    activeTab.value = 'basic';
  }
}, { immediate: true });


onMounted(() => {
  // 如果编辑时有 organizationId，则加载数据
  // 这个逻辑已经通过 watch immediate:true 处理了，所以这里可能不需要再次调用
  // if (props.organizationId) {
  //   loadOrganizationData();
  // }
});

</script>

<style scoped>
.organization-form {
  margin: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}

:deep(.el-tabs__content) {
  padding: 20px;
}

:deep(.el-tabs--border-card) {
  box-shadow: none;
  border: 1px solid #dcdfe6;
}

.dialog-footer {
  text-align: right;
  padding-top: 20px;
}
</style> 