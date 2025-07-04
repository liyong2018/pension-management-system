<template>
  <el-dialog
    :model-value="visible"
    title="人员详情"
    width="900px"
    :before-close="handleClose"
    destroy-on-close
  >
    <div v-loading="loading" class="staff-detail">
      <div v-if="staffData" class="detail-content">
        <!-- 基本信息卡片 -->
        <el-card class="info-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">基本信息</span>
            </div>
          </template>
          <div class="staff-profile">
            <div class="avatar-section">
              <el-avatar :size="80" class="staff-avatar">
                <el-icon size="40"><User /></el-icon>
              </el-avatar>
              <div class="status-badge">
                <el-tag :type="getStatusTagType(staffData.status)" size="large" effect="light">
                  {{ staffData.status === 'ACTIVE' ? '在职' : staffData.status === 'INACTIVE' ? '离职' : staffData.status === 'SUSPENDED' ? '停职' : staffData.status }}
                </el-tag>
              </div>
            </div>
            <div class="basic-info">
              <h3 class="staff-name">{{ staffData.name }}</h3>
              <p class="staff-position">
                <el-tag :type="getPositionTagType(staffData.position)" effect="light">
                  {{ staffData.position }}
                </el-tag>
              </p>
              <div class="info-grid">
                <div class="info-item">
                  <span class="label">工号：</span>
                  <span class="value">{{ staffData.employeeId }}</span>
                </div>
                <div class="info-item">
                  <span class="label">性别：</span>
                  <span class="value">{{ staffData.gender }}</span>
                </div>
                <div class="info-item">
                  <span class="label">年龄：</span>
                  <span class="value">{{ staffData.age }}岁</span>
                </div>
                <div class="info-item">
                  <span class="label">入职时间：</span>
                  <span class="value">{{ staffData.hireDate }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 详细信息 -->
        <el-row :gutter="20" style="margin-top: 20px;">
          <!-- 个人信息 -->
          <el-col :span="12">
            <el-card class="info-card" shadow="never">
              <template #header>
                <div class="card-header">
                  <span class="card-title">个人信息</span>
                </div>
              </template>
              <div class="detail-list">
                <div class="detail-item">
                  <span class="detail-label">身份证号：</span>
                  <span class="detail-value">{{ staffData.idCard || '-' }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">联系电话：</span>
                  <span class="detail-value">{{ staffData.phone || '-' }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">学历：</span>
                  <span class="detail-value">{{ staffData.education || '-' }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">工作经验：</span>
                  <span class="detail-value">{{ staffData.workExperience || 0 }}年</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">家庭住址：</span>
                  <span class="detail-value">{{ staffData.address || '-' }}</span>
                </div>
              </div>
            </el-card>
          </el-col>

          <!-- 工作信息 -->
          <el-col :span="12">
            <el-card class="info-card" shadow="never">
              <template #header>
                <div class="card-header">
                  <span class="card-title">工作信息</span>
                </div>
              </template>
              <div class="detail-list">
                <div class="detail-item">
                  <span class="detail-label">所属机构：</span>
                  <span class="detail-value">{{ staffData.organizationName || '-' }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">职位：</span>
                  <span class="detail-value">
                    <el-tag :type="getPositionTagType(staffData.position)" size="small" effect="light">
                      {{ staffData.position }}
                    </el-tag>
                  </span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">工作状态：</span>
                  <span class="detail-value">
                    <el-tag :type="getStatusTagType(staffData.status)" size="small" effect="light">
                      {{ staffData.status === 'ACTIVE' ? '在职' : staffData.status === 'INACTIVE' ? '离职' : staffData.status === 'SUSPENDED' ? '停职' : staffData.status }}
                    </el-tag>
                  </span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">入职时间：</span>
                  <span class="detail-value">{{ staffData.hireDate || '-' }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">专业技能：</span>
                  <span class="detail-value">{{ staffData.skills || '-' }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 紧急联系人信息 -->
        <el-card class="info-card" shadow="never" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span class="card-title">紧急联系人</span>
            </div>
          </template>
          <el-row :gutter="40">
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">联系人姓名：</span>
                <span class="detail-value">{{ staffData.emergencyContact || '-' }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">联系电话：</span>
                <span class="detail-value">{{ staffData.emergencyPhone || '-' }}</span>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 备注信息 -->
        <el-card class="info-card" shadow="never" style="margin-top: 20px;" v-if="staffData.remarks">
          <template #header>
            <div class="card-header">
              <span class="card-title">备注信息</span>
            </div>
          </template>
          <div class="remarks-content">
            {{ staffData.remarks }}
          </div>
        </el-card>

        <!-- 操作记录 -->
        <el-card class="info-card" shadow="never" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span class="card-title">操作记录</span>
            </div>
          </template>
          <div class="detail-list">
            <div class="detail-item">
              <span class="detail-label">创建时间：</span>
              <span class="detail-value">{{ staffData.createTime || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">更新时间：</span>
              <span class="detail-value">{{ staffData.updateTime || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">创建人：</span>
              <span class="detail-value">{{ staffData.createBy || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">更新人：</span>
              <span class="detail-value">{{ staffData.updateBy || '-' }}</span>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="handleEdit">
          <el-icon><Edit /></el-icon>
          编辑
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { User, Edit } from '@element-plus/icons-vue';
import serviceStaffService from '@/services/serviceStaffService';

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
const emit = defineEmits(['close', 'edit']);

// 响应式数据
const loading = ref(false);
const staffData = ref(null);

// 获取人员详情
const fetchStaffDetail = async () => {
  if (!props.staffId) return;
  
  loading.value = true;
  try {
    const response = await serviceStaffService.getStaffDetail(props.staffId);
    if (response) {
      staffData.value = response;
    }
  } catch (error) {
    console.error("Failed to fetch staff detail:", error);
    ElMessage.error('获取人员详情失败');
  } finally {
    loading.value = false;
  }
};

// 职位标签类型
const getPositionTagType = (position) => {
  const typeMap = {
    '医生': 'danger',
    '护士': 'success',
    '护理员': 'primary',
    '康复师': 'warning',
    '营养师': 'info',
    '社工': '',
    '管理员': 'danger',
    '其他': 'info'
  };
  return typeMap[position] || 'info';
};

// 状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    'ACTIVE': 'success',
    'INACTIVE': 'danger',
    'SUSPENDED': 'warning'
  };
  return typeMap[status] || 'info';
};

// 处理关闭
const handleClose = () => {
  emit('close');
};

// 处理编辑
const handleEdit = () => {
  emit('edit', staffData.value);
};

// 监听对话框显示状态
watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      fetchStaffDetail();
    } else {
      staffData.value = null;
    }
  },
  { immediate: true }
);
</script>

<style scoped>
.staff-detail {
  min-height: 400px;
}

.info-card {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.staff-profile {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.staff-avatar {
  background-color: #f0f9ff;
  color: #1890ff;
  border: 3px solid #e6f7ff;
}

.status-badge {
  margin-top: 8px;
}

.basic-info {
  flex: 1;
}

.staff-name {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.staff-position {
  margin: 0 0 16px 0;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: center;
}

.info-item .label {
  font-weight: 500;
  color: #606266;
  margin-right: 8px;
  min-width: 80px;
}

.info-item .value {
  color: #303133;
}

.detail-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
  padding: 8px 0;
  border-bottom: 1px solid #f5f7fa;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-label {
  font-weight: 500;
  color: #606266;
  min-width: 100px;
  margin-right: 16px;
  flex-shrink: 0;
}

.detail-value {
  color: #303133;
  flex: 1;
  word-break: break-all;
}

.remarks-content {
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 6px;
  color: #606266;
  line-height: 1.6;
  white-space: pre-wrap;
}

.dialog-footer {
  text-align: right;
}

:deep(.el-card__header) {
  padding: 16px 20px;
  background-color: #fafafa;
  border-bottom: 1px solid #e4e7ed;
}

:deep(.el-card__body) {
  padding: 20px;
}
</style>