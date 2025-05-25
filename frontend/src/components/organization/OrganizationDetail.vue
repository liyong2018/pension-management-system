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

      <!-- 人员档案 Tab -->
      <el-tab-pane label="人员档案" name="elderly">
        <div class="elderly-list-container">
          <div class="elderly-list-header">
            <el-row :gutter="20" style="margin-bottom: 20px;">
              <el-col :span="12">
                <h4>机构老人列表 (共 {{ elderlyList.length }} 人)</h4>
              </el-col>
              <el-col :span="12" style="text-align: right;">
                <el-button @click="refreshElderlyList" :loading="elderlyLoading">
                  <el-icon><Refresh /></el-icon>
                  刷新
                </el-button>
              </el-col>
            </el-row>
          </div>
          
          <el-table 
            :data="elderlyList" 
            v-loading="elderlyLoading"
            stripe
            border
            style="width: 100%"
            empty-text="暂无老人档案数据"
          >
            <el-table-column prop="name" label="姓名" width="100" />
            <el-table-column prop="gender" label="性别" width="80" />
            <el-table-column prop="age" label="年龄" width="80">
              <template #default="scope">
                {{ calculateAge(scope.row.birthDate) }}岁
              </template>
            </el-table-column>
            <el-table-column prop="phone" label="联系电话" width="130" />
            <el-table-column prop="pensionType" label="养老类型" width="120" />
            <el-table-column prop="careLevel" label="护理等级" width="100" />
            <el-table-column prop="abilityAssessment" label="能力评估" width="100" />
            <el-table-column prop="currentHealthStatus" label="健康状况" min-width="150" show-overflow-tooltip />
            <el-table-column prop="createTime" label="入档时间" width="120">
              <template #default="scope">
                {{ formatDate(scope.row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
        </div>
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
import { Refresh } from '@element-plus/icons-vue';
import { formatDate } from '@/utils/dateUtils';
import organizationService from '@/services/organizationService';
import { elderlyProfileApi } from '@/api/elderlyProfile';

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

// 老人列表相关数据
const elderlyList = ref([]);
const elderlyLoading = ref(false);

// 获取机构详情数据
const fetchOrganizationDetail = async () => {
  loading.value = true;
  try {
    const response = await organizationService.getOrganizationById(props.organizationId);
    organizationData.value = response;
  } catch (error) {
    console.error('获取机构详情失败:', error);
    ElMessage.error('获取机构详情失败');
    organizationData.value = {};
  } finally {
    loading.value = false;
  }
};

// 获取机构老人列表
const fetchElderlyList = async () => {
  if (!props.organizationId) return;
  
  elderlyLoading.value = true;
  try {
    const response = await elderlyProfileApi.getByOrganizationId(props.organizationId);
    elderlyList.value = response || [];
  } catch (error) {
    console.error('获取老人列表失败:', error);
    ElMessage.error('获取老人列表失败');
    elderlyList.value = [];
  } finally {
    elderlyLoading.value = false;
  }
};

// 刷新老人列表
const refreshElderlyList = () => {
  fetchElderlyList();
};

// 计算年龄
const calculateAge = (birthDate) => {
  if (!birthDate) return '-';
  const birth = new Date(birthDate);
  const today = new Date();
  let age = today.getFullYear() - birth.getFullYear();
  const monthDiff = today.getMonth() - birth.getMonth();
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
    age--;
  }
  return age;
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
    fetchElderlyList();
  }
});

// 监听activeTab变化，当切换到人员档案tab时加载数据
watch(activeTab, (newTab) => {
  if (newTab === 'elderly' && props.visible) {
    fetchElderlyList();
  }
});

// 组件挂载时获取数据
onMounted(() => {
  if (props.visible) {
    fetchOrganizationDetail();
    if (activeTab.value === 'elderly') {
      fetchElderlyList();
    }
  }
});
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}

.elderly-list-container {
  padding: 10px 0;
}

.elderly-list-header h4 {
  margin: 0;
  color: #303133;
  font-weight: 600;
}

.el-table {
  margin-top: 10px;
}

.el-table .el-table__cell {
  padding: 8px 0;
}

/* 表格行悬停效果 */
.el-table .el-table__row:hover {
  background-color: #f5f7fa;
}

/* 空数据状态样式 */
.el-table .el-table__empty-text {
  color: #909399;
  font-size: 14px;
}
</style> 