<template>
  <el-dialog
    :model-value="visible"
    :title="isEditMode ? '编辑机构' : '新增机构'"
    width="500px"
    @close="handleClose"
    :close-on-click-modal="false"
  >
    <el-form
      ref="organizationFormRef"
      :model="formModel"
      :rules="formRules"
      label-width="100px"
      v-loading="loading"
    >
      <el-form-item label="机构名称" prop="name">
        <el-input v-model="formModel.name" placeholder="请输入机构名称"></el-input>
      </el-form-item>
      <el-form-item label="机构类型" prop="type">
        <el-select v-model="formModel.type" placeholder="请选择机构类型" style="width:100%;">
          <el-option label="公立" value="公立"></el-option>
          <el-option label="私立" value="私立"></el-option>
          <el-option label="公私合营" value="公私合营"></el-option>
          <el-option label="其他" value="其他"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="联系人" prop="contactPersonName">
        <el-input v-model="formModel.contactPersonName" placeholder="请输入联系人姓名"></el-input>
      </el-form-item>
      <el-form-item label="联系电话" prop="contactPersonPhone">
        <el-input v-model="formModel.contactPersonPhone" placeholder="请输入联系电话"></el-input>
      </el-form-item>
      <el-form-item label="地址" prop="address">
        <el-input type="textarea" v-model="formModel.address" placeholder="请输入机构地址"></el-input>
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input type="textarea" v-model="formModel.description" placeholder="请输入机构描述"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, reactive, computed } from 'vue';
import { ElMessage } from 'element-plus';
import organizationService from '@/services/organizationService';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  organizationId: {
    type: Number,
    default: null,
  },
});

const emit = defineEmits(['close', 'success']);

const organizationFormRef = ref(null);
const loading = ref(false);
const submitting = ref(false);

const initialFormModel = () => ({
  name: '',
  type: '',
  address: '',
  contactPersonName: '',
  contactPersonPhone: '',
  description: '',
});

const formModel = reactive(initialFormModel());

const formRules = {
  name: [{ required: true, message: '机构名称不能为空', trigger: 'blur' }],
  type: [{ required: true, message: '机构类型不能为空', trigger: 'change' }],
  contactPersonPhone: [
    { required: true, message: '联系电话不能为空', trigger: 'blur' },
    // 简单的电话号码格式校验，可以根据需要替换为更复杂的正则表达式
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  // 其他字段可以根据需要添加校验规则
};

const isEditMode = computed(() => !!props.organizationId);

watch(
  () => props.visible,
  async (newVal) => {
    if (newVal) {
      // 重置表单和校验状态
      organizationFormRef.value?.resetFields();
      Object.assign(formModel, initialFormModel());
      submitting.value = false;

      if (props.organizationId) {
        // 编辑模式，加载机构数据
        loading.value = true;
        try {
          const response = await organizationService.getOrganizationById(props.organizationId);
          Object.assign(formModel, response.data);
        } catch (error) {
          console.error('Failed to fetch organization details:', error);
          ElMessage.error(error.response?.data?.message || '获取机构详情失败');
          emit('close'); // 获取失败则关闭弹窗
        } finally {
          loading.value = false;
        }
      }
    } 
  }
);

const handleClose = () => {
  emit('close');
};

const handleSubmit = async () => {
  if (!organizationFormRef.value) return;

  await organizationFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        if (isEditMode.value) {
          await organizationService.updateOrganization(props.organizationId, formModel);
          ElMessage.success('机构信息更新成功');
        } else {
          await organizationService.createOrganization(formModel);
          ElMessage.success('机构新增成功');
        }
        emit('success');
      } catch (error) {
        console.error('Failed to save organization:', error);
        ElMessage.error(error.response?.data?.message || '操作失败，请重试');
      } finally {
        submitting.value = false;
      }
    }
  });
};

</script>

<style scoped>
/* Add any specific styles for the form if needed */
</style> 