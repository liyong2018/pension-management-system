<template>
  <div class="service-staff-management">
    <!-- 统计卡片 -->
    <!-- <div class="statistics-cards" v-if="stats">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon size="24"><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">总人员数</div>
                <div class="stat-value total">{{ stats.totalStaff }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon active">
                <el-icon size="24"><UserFilled /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">在职人员</div>
                <div class="stat-value active">{{ stats.activeStaff }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon nurse">
                <el-icon size="24"><Avatar /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">护理人员</div>
                <div class="stat-value nurse">{{ stats.nurseStaff }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon doctor">
                <el-icon size="24"><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">医护人员</div>
                <div class="stat-value doctor">{{ stats.doctorStaff }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div> -->

    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="auto" :inline="true">
        <el-form-item label="姓名">
          <el-input 
            v-model="searchForm.name" 
            placeholder="请输入姓名" 
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="工号">
          <el-input 
            v-model="searchForm.employeeId" 
            placeholder="请输入工号" 
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <!-- <el-form-item label="职位">
          <el-select 
            v-model="searchForm.position" 
            placeholder="请选择职位" 
            clearable
            style="width: 150px"
          >
            <el-option label="护理员" value="护理员" />
            <el-option label="医生" value="医生" />
            <el-option label="护士" value="护士" />
            <el-option label="康复师" value="康复师" />
            <el-option label="营养师" value="营养师" />
            <el-option label="社工" value="社工" />
            <el-option label="管理员" value="管理员" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item> -->
        <el-form-item label="状态">
          <el-select 
            v-model="searchForm.status" 
            placeholder="请选择状态" 
            clearable
            style="width: 120px"
          >
            <el-option label="在职" value="ACTIVE" />
            <el-option label="离职" value="INACTIVE" />
            <el-option label="停职" value="SUSPENDED" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属机构">
          <el-select 
            v-model="searchForm.organizationId" 
            placeholder="请选择机构" 
            clearable
            filterable
            style="width: 180px"
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
        <el-form-item class="search-buttons-left">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
        <el-form-item class="add-button-right">
          <el-button type="primary" @click="showCreateDialog">
            <el-icon><Plus /></el-icon>
            添加人员
          </el-button>
        </el-form-item>
        <el-form-item class="table-operations-left">
          <el-button type="danger" :disabled="!multipleSelection.length" @click="handleBatchDelete">
            批量删除
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table
        :data="staffList"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        border
        stripe
        style="width: 100%"
        :header-cell-style="{ backgroundColor: '#f5f7fa', color: '#303133', fontWeight: '600' }"
        :row-style="{ height: '60px' }"
        class="staff-table"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="employeeId" label="工号" width="120" />
        <el-table-column prop="name" label="姓名" width="120">
          <template #default="scope">
            <div class="staff-info">
              <span class="staff-name">{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.gender === '男' ? 'primary' : 'danger'" size="small" effect="light">
              {{ scope.row.gender }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="80" align="center" />
        <el-table-column prop="position" label="职位" width="120">
          <template #default="scope">
            <el-tag :type="getPositionTagType(scope.row.position)" size="small" effect="light">
              {{ scope.row.position }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="organizationName" label="所属机构" min-width="150" show-overflow-tooltip />
        <el-table-column prop="hireDate" label="入职时间" width="120" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)" size="small" effect="light">
              <el-icon style="margin-right: 4px">
                <CircleCheck v-if="scope.row.status === 'ACTIVE'" />
                <CircleClose v-else-if="scope.row.status === 'INACTIVE'" />
                <Clock v-else />
              </el-icon>
              {{ scope.row.status === 'ACTIVE' ? '在职' : scope.row.status === 'INACTIVE' ? '离职' : scope.row.status === 'SUSPENDED' ? '停职' : scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button type="primary" link size="small" @click="showDetailDialog(scope.row)">
                详情
              </el-button>
              <el-button type="primary" link size="small" @click="showEditDialog(scope.row)">
                编辑
              </el-button>
              <el-dropdown @command="(command) => handleAction(command, scope.row)" trigger="click">
                <el-button type="primary" link size="small" class="more-btn">
                  更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="changeStatus">
                      <el-icon><Switch /></el-icon>
                      变更状态
                    </el-dropdown-item>
                    <el-dropdown-item command="viewSchedule">
                      <el-icon><Calendar /></el-icon>
                      查看排班
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" divided class="delete-item">
                      <el-icon><Delete /></el-icon>
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <el-pagination
        style="margin-top: 20px;"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalElements"
        :page-sizes="[10, 20, 50, 100]"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <ServiceStaffForm 
      v-if="dialogVisible" 
      :visible="dialogVisible" 
      :staff-id="selectedStaffId"
      @close="handleCloseDialog" 
      @success="handleFormSuccess" 
    />

    <!-- 详情对话框 -->
    <ServiceStaffDetail 
      v-if="detailDialogVisible" 
      :visible="detailDialogVisible"
      :staff-id="selectedStaffId" 
      @close="handleCloseDetailDialog" 
    />
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  User, UserFilled, Avatar, Plus, ArrowDown, Switch, Calendar, Delete,
  CircleCheck, CircleClose, Clock
} from '@element-plus/icons-vue';
import serviceStaffService from '@/services/serviceStaffService';
import organizationService from '@/services/organizationService';
import ServiceStaffForm from '@/components/service-staff/ServiceStaffForm.vue';
import ServiceStaffDetail from '@/components/service-staff/ServiceStaffDetail.vue';

// 响应式数据
const staffList = ref([]);
const loading = ref(false);
const totalElements = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const multipleSelection = ref([]);

// 统计数据
const stats = ref({
  totalStaff: 0,
  activeStaff: 0,
  nurseStaff: 0,
  doctorStaff: 0
});

// 搜索表单
const searchForm = reactive({
  name: '',
  employeeId: '',
  position: '',
  status: '',
  organizationId: ''
});

// 机构数据
const organizations = ref([]);
const organizationsLoading = ref(false);

// 对话框控制
const dialogVisible = ref(false);
const detailDialogVisible = ref(false);
const selectedStaffId = ref(null);

// 获取人员列表
const fetchStaffList = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      name: searchForm.name || undefined,
      employeeId: searchForm.employeeId || undefined,
      position: searchForm.position || undefined,
      status: searchForm.status || undefined,
      organizationId: searchForm.organizationId || undefined
    };

    const response = await serviceStaffService.getStaffList(params);
    if (response && response.list) {
      staffList.value = response.list;
      totalElements.value = response.total;
      currentPage.value = response.pageNum;
      pageSize.value = response.pageSize;
    } else {
      staffList.value = [];
      totalElements.value = 0;
      ElMessage.warning(response?.list ? '人员数据为空' : '未获取到人员数据或数据格式不正确');
    }
  } catch (error) {
    console.error("Failed to fetch staff list:", error);
    ElMessage.error('获取人员列表失败');
    staffList.value = [];
    totalElements.value = 0;
  } finally {
    loading.value = false;
  }
};

// 获取统计数据
const fetchStats = async () => {
  try {
    const response = await serviceStaffService.getStaffStats();
    if (response) {
      stats.value = response;
    }
  } catch (error) {
    console.error("Failed to fetch stats:", error);
  }
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

// 事件处理
const handleSearch = () => {
  currentPage.value = 1;
  fetchStaffList();
};

const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = '';
  });
  currentPage.value = 1;
  fetchStaffList();
};

const handleSelectionChange = (selection) => {
  multipleSelection.value = selection;
};

const handleBatchDelete = () => {
  if (multipleSelection.value.length === 0) {
    return;
  }

  ElMessageBox.confirm(
    `确定要删除选中的 ${multipleSelection.value.length} 个人员吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      loading.value = true;
      try {
        const ids = multipleSelection.value.map(staff => staff.id);
        await serviceStaffService.batchDeleteStaff(ids);
        ElMessage({
          type: 'success',
          message: '批量删除成功',
          duration: 2000
        });
        multipleSelection.value = [];
        await fetchStaffList();
        await fetchStats();
      } catch (error) {
        console.error("Failed to delete staff:", error);
        ElMessage({
          type: 'error',
          message: error.message || '批量删除失败，请重试',
          duration: 5000
        });
      } finally {
        loading.value = false;
      }
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消删除操作',
        duration: 2000
      });
    });
};

const showCreateDialog = () => {
  selectedStaffId.value = null;
  dialogVisible.value = true;
};

const showEditDialog = (staff) => {
  selectedStaffId.value = staff.id;
  dialogVisible.value = true;
};

const showDetailDialog = (staff) => {
  selectedStaffId.value = staff.id;
  detailDialogVisible.value = true;
};

const handleAction = (command, staff) => {
  switch (command) {
    case 'changeStatus':
      handleChangeStatus(staff);
      break;
    case 'viewSchedule':
      handleViewSchedule(staff);
      break;
    case 'delete':
      handleDelete(staff.id);
      break;
  }
};

const handleChangeStatus = (staff) => {
  // 实现状态变更逻辑
  ElMessage.info('状态变更功能待实现');
};

const handleViewSchedule = (staff) => {
  // 实现查看排班逻辑
  ElMessage.info('查看排班功能待实现');
};

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该人员吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await serviceStaffService.deleteStaff(id);
        ElMessage.success('删除成功');
        fetchStaffList();
        fetchStats();
      } catch (error) {
        console.error("Failed to delete staff:", error);
        ElMessage.error(error.response?.data?.message || '删除失败');
      }
    })
    .catch(() => {
      // 用户取消删除
    });
};

const handleCloseDialog = () => {
  dialogVisible.value = false;
  selectedStaffId.value = null;
};

const handleFormSuccess = () => {
  handleCloseDialog();
  fetchStaffList();
  fetchStats();
};

const handleCloseDetailDialog = () => {
  detailDialogVisible.value = false;
  selectedStaffId.value = null;
};

const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1;
  fetchStaffList();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchStaffList();
};

// 生命周期
onMounted(() => {
  fetchStaffList();
  fetchStats();
  fetchOrganizations();
});
</script>

<style scoped>
.service-staff-management {
  padding: 20px;
}

/* 统计卡片样式 */
.statistics-cards {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  color: white;
}

.stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.active {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.nurse {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.doctor {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  line-height: 1;
}

.stat-value.total {
  color: #667eea;
}

.stat-value.active {
  color: #f5576c;
}

.stat-value.nurse {
  color: #4facfe;
}

.stat-value.doctor {
  color: #43e97b;
}

/* 搜索卡片样式 */
.search-card {
  margin-bottom: 20px;
}

.search-buttons-left {
  margin-left: 0;
}

.add-button-right {
  margin-left: auto;
}

.table-operations-left {
  margin-left: 0;
}

/* 表格样式 */
.table-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.staff-table {
  border-radius: 8px;
  overflow: hidden;
}

.staff-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.staff-avatar {
  background-color: #f0f9ff;
  color: #1890ff;
}

.staff-name {
  font-weight: 500;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.more-btn {
  margin-left: 4px;
}

.delete-item {
  color: #f56c6c;
}

.delete-item:hover {
  background-color: #fef0f0;
}
</style>