<template>
  <div class="volunteer-management">
    <el-card class="box-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>志愿者管理</span>
          <div class="header-buttons">
            <el-button type="success" @click="updateAllStats" :loading="updatingStats">
              <el-icon><Refresh /></el-icon>
              更新统计数据
            </el-button>
            <el-button type="primary" @click="showCreateDialog">
              <el-icon><Plus /></el-icon>
              添加志愿者
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" inline label-width="80px">
          <el-form-item label="姓名">
            <el-input v-model="searchForm.name" placeholder="请输入志愿者姓名" clearable />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="活跃" value="ACTIVE" />
              <el-option label="不活跃" value="INACTIVE" />
              <el-option label="待审核" value="PENDING_APPROVAL" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 统计信息 -->
      <div class="stats-area" v-if="stats">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-statistic title="总数" :value="stats.totalCount" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="活跃" :value="stats.activeCount" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="不活跃" :value="stats.inactiveCount" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="待审核" :value="stats.pendingCount" />
          </el-col>
        </el-row>
      </div>

      <!-- 表格 -->
      <el-table
        :data="volunteers"
        v-loading="loading"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column prop="phone" label="联系方式" width="130" />
        <el-table-column prop="education" label="学历" width="100" />
        <el-table-column prop="occupation" label="职业" width="120" />
        <el-table-column prop="serviceIntention" label="服务意向" min-width="150" show-overflow-tooltip />
        <el-table-column prop="totalServiceHours" label="服务时长" width="100">
          <template #default="scope">
            {{ scope.row.totalServiceHours || 0 }}小时
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag
              :type="getStatusType(scope.row.status)"
              size="small"
            >
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="registrationDate" label="注册日期" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="text" size="small" @click="showDetailDialog(scope.row)">
              详情
            </el-button>
            <el-button type="text" size="small" @click="showEditDialog(scope.row)">
              编辑
            </el-button>
            <el-dropdown @command="(command) => handleAction(command, scope.row)">
              <el-button type="text" size="small">
                更多<el-icon><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="updateStatus">变更状态</el-dropdown-item>
                  <el-dropdown-item command="updateStats">更新统计</el-dropdown-item>
                  <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 志愿者详情对话框 -->
    <VolunteerDetailDialog
      v-model="detailDialogVisible"
      :volunteer="selectedVolunteer"
    />

    <!-- 志愿者编辑对话框 -->
    <VolunteerEditDialog
      v-model="editDialogVisible"
      :volunteer="selectedVolunteer"
      @success="handleDialogSuccess"
    />

    <!-- 状态更新对话框 -->
    <VolunteerStatusDialog
      v-model="statusDialogVisible"
      :volunteer="selectedVolunteer"
      @success="handleDialogSuccess"
    />

    <!-- 统计更新对话框 -->
    <VolunteerStatsDialog
      v-model="statsDialogVisible"
      :volunteer="selectedVolunteer"
      @success="handleDialogSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, ArrowDown, Refresh } from '@element-plus/icons-vue'
import VolunteerDetailDialog from './components/VolunteerDetailDialog.vue'
import VolunteerEditDialog from './components/VolunteerEditDialog.vue'
import VolunteerStatusDialog from './components/VolunteerStatusDialog.vue'
import VolunteerStatsDialog from './components/VolunteerStatsDialog.vue'
import { volunteerApi } from '@/api/volunteer'

// 响应式数据
const loading = ref(false)
const updatingStats = ref(false)
const volunteers = ref([])
const stats = ref(null)
const selectedVolunteer = ref(null)
const multipleSelection = ref([])

// 搜索表单
const searchForm = reactive({
  name: '',
  phone: '',
  status: ''
})

// 分页信息
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

// 对话框显示状态
const detailDialogVisible = ref(false)
const editDialogVisible = ref(false)
const statusDialogVisible = ref(false)
const statsDialogVisible = ref(false)

// 加载志愿者列表（包含最新统计数据）
const loadVolunteers = async () => {
  loading.value = true
  try {
    const response = await volunteerApi.getVolunteerListWithStats({
      ...searchForm,
      page: pagination.page,
      pageSize: pagination.pageSize
    })
    
    if (response.success) {
      volunteers.value = response.data.volunteers
      pagination.total = response.data.total
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('加载志愿者列表失败')
  } finally {
    loading.value = false
  }
}

// 更新所有志愿者的统计数据
const updateAllStats = async () => {
  updatingStats.value = true
  try {
    const response = await volunteerApi.updateAllVolunteersServiceStats()
    if (response.success) {
      ElMessage.success(response.message || '统计数据更新成功')
      // 重新加载列表以显示最新数据
      await loadVolunteers()
    } else {
      ElMessage.error(response.message || '统计数据更新失败')
    }
  } catch (error) {
    ElMessage.error('统计数据更新失败')
  } finally {
    updatingStats.value = false
  }
}

// 加载统计信息
const loadStats = async () => {
  try {
    const response = await volunteerApi.getVolunteerStats()
    if (response.success) {
      stats.value = response.data
    }
  } catch (error) {
    console.error('加载统计信息失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadVolunteers()
}

// 重置
const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  pagination.page = 1
  loadVolunteers()
}

// 显示创建对话框
const showCreateDialog = () => {
  selectedVolunteer.value = null
  editDialogVisible.value = true
}

// 显示详情对话框
const showDetailDialog = (volunteer) => {
  selectedVolunteer.value = volunteer
  detailDialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (volunteer) => {
  selectedVolunteer.value = volunteer
  editDialogVisible.value = true
}

// 处理操作
const handleAction = (command, volunteer) => {
  selectedVolunteer.value = volunteer
  
  switch (command) {
    case 'updateStatus':
      statusDialogVisible.value = true
      break
    case 'updateStats':
      updateSingleVolunteerStats(volunteer)
      break
    case 'delete':
      handleDelete(volunteer)
      break
  }
}

// 更新单个志愿者的统计数据
const updateSingleVolunteerStats = async (volunteer) => {
  try {
    const response = await volunteerApi.updateVolunteerServiceStats(volunteer.id)
    if (response.success) {
      ElMessage.success(`${volunteer.name} 的统计数据更新成功`)
      // 重新加载列表以显示最新数据
      await loadVolunteers()
    } else {
      ElMessage.error(response.message || '统计数据更新失败')
    }
  } catch (error) {
    ElMessage.error('统计数据更新失败')
  }
}

// 删除志愿者
const handleDelete = async (volunteer) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除志愿者 "${volunteer.name}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await volunteerApi.deleteVolunteer(volunteer.id)
    if (response.success) {
      ElMessage.success('删除成功')
      loadVolunteers()
      loadStats()
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 对话框成功回调
const handleDialogSuccess = () => {
  loadVolunteers()
  loadStats()
}

// 表格选择
const handleSelectionChange = (selection) => {
  multipleSelection.value = selection
}

// 分页
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.page = 1
  loadVolunteers()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadVolunteers()
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

// 组件挂载时加载数据
onMounted(() => {
  loadVolunteers()
  loadStats()
})
</script>

<style scoped>
.volunteer-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.search-area {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.stats-area {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f0f9ff;
  border-radius: 4px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style> 