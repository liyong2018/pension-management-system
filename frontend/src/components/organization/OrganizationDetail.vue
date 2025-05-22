<template>
  <el-dialog
    v-model="dialogVisible"
    title="机构详情"
    width="80%"
    :before-close="handleClose"
    v-loading="loading"
  >
    <el-tabs v-model="activeTab">
      <!-- 基本信息 Tab -->
      <el-tab-pane label="机构基本信息" name="basic">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="机构名称">{{ organizationData.name }}</el-descriptions-item>
          <el-descriptions-item label="机构简称">{{ organizationData.shortName }}</el-descriptions-item>
          <el-descriptions-item label="机构类型">{{ organizationData.type }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ organizationData.phone }}</el-descriptions-item>
          <el-descriptions-item label="电子邮箱">{{ organizationData.email }}</el-descriptions-item>
          <el-descriptions-item label="网址">{{ organizationData.website }}</el-descriptions-item>
          <el-descriptions-item label="机构地址" :span="2">
            {{ getFullAddress() }}
          </el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>

      <!-- 运营信息 Tab -->
      <el-tab-pane label="机构运营信息" name="operation">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="成立时间">{{ formatDate(organizationData.establishmentDate) }}</el-descriptions-item>
          <el-descriptions-item label="许可证号">{{ organizationData.licenseNumber }}</el-descriptions-item>
          <el-descriptions-item label="床位数量">{{ organizationData.bedCount }}</el-descriptions-item>
          <el-descriptions-item label="实际服务人数">{{ organizationData.actualServiceCount }}</el-descriptions-item>
          <el-descriptions-item label="机构规模">{{ organizationData.area }}</el-descriptions-item>
          <el-descriptions-item label="经营范围" :span="2">{{ organizationData.businessScope }}</el-descriptions-item>
          <el-descriptions-item label="收费标准" :span="2">{{ organizationData.chargingStandard }}</el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>

      <!-- 人员信息 Tab -->
      <el-tab-pane label="机构人员信息" name="staff">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="负责人姓名">{{ organizationData.directorName }}</el-descriptions-item>
          <el-descriptions-item label="负责人联系方式">{{ organizationData.directorContact }}</el-descriptions-item>
          <el-descriptions-item label="员工总数">{{ organizationData.employeeCount }}</el-descriptions-item>
          <el-descriptions-item label="专业护理人员数量">{{ organizationData.professionalNurseCount }}</el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>

      <!-- 资质信息 Tab -->
      <el-tab-pane label="机构资质信息" name="qualification">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="消防许可证">{{ organizationData.fireLicense }}</el-descriptions-item>
          <el-descriptions-item label="卫生许可证">{{ organizationData.sanitaryLicense }}</el-descriptions-item>
          <el-descriptions-item label="医疗机构执业许可证">{{ organizationData.medicalLicense }}</el-descriptions-item>
          <el-descriptions-item label="其他资质证书" :span="2">{{ organizationData.otherQualifications }}</el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>
    </el-tabs>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { formatDate } from '@/utils/dateUtils';
import organizationService from '@/services/organizationService';

const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  },
  organizationId: {
    type: [Number, String],
    required: true
  }
});

const emit = defineEmits(['close']);

const dialogVisible = ref(props.visible);
const activeTab = ref('basic');
const loading = ref(false);
const organizationData = ref({});

// 获取机构详情数据
const fetchOrganizationDetail = async () => {
  loading.value = true;
  try {
    const response = await organizationService.getOrganizationById(props.organizationId);
    organizationData.value = response.data;
  } catch (error) {
    console.error('获取机构详情失败:', error);
    ElMessage.error('获取机构详情失败');
  } finally {
    loading.value = false;
  }
};

const getFullAddress = () => {
  const org = organizationData.value;
  return `${org.addressProvince || ''}${org.addressCity || ''}${org.addressDistrict || ''}${org.addressStreet || ''}${org.addressDetail || ''}`;
};

const handleClose = () => {
  emit('close');
};

// 监听visible属性变化
watch(() => props.visible, (newVal) => {
  dialogVisible.value = newVal;
  if (newVal) {
    fetchOrganizationDetail();
  }
});

// 组件挂载时获取数据
onMounted(() => {
  if (props.visible) {
    fetchOrganizationDetail();
  }
});
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style> 