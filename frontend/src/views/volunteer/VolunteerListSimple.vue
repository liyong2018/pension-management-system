<template>
  <div class="volunteer-management">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="auto" :inline="true">
        <el-form-item class="table-operations-left">
          <el-button type="danger" :disabled="!multipleSelection.length" @click="handleBatchDelete">
            批量删除
          </el-button>
        </el-form-item>

        <el-form-item label="姓名">
          <el-input 
            v-model="searchForm.name" 
            placeholder="请输入志愿者姓名" 
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input 
            v-model="searchForm.phone" 
            placeholder="请输入手机号" 
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select 
            v-model="searchForm.status" 
            placeholder="请选择状态" 
            clearable
            style="width: 120px"
          >
            <el-option label="活跃" value="ACTIVE" />
            <el-option label="不活跃" value="INACTIVE" />
            <el-option label="待审核" value="PENDING_APPROVAL" />
          </el-select>
        </el-form-item>
        <el-form-item class="search-buttons-left">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
        <el-form-item class="add-button-right">
          <el-button type="primary" @click="showCreateDialog">
            <el-icon><Plus /></el-icon>
            添加志愿者
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <div class="statistics-cards" v-if="stats">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-title">总数</div>
              <div class="stat-value total">{{ stats.totalCount }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-title">活跃</div>
              <div class="stat-value active">{{ stats.activeCount }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-title">不活跃</div>
              <div class="stat-value inactive">{{ stats.inactiveCount }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-title">待审核</div>
              <div class="stat-value pending">{{ stats.pendingCount }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table
        :data="volunteers"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            {{ getGenderText(scope.row.gender) }}
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column prop="phone" label="联系方式" width="130" />
        <el-table-column prop="education" label="学历" width="100" />
        <el-table-column prop="occupation" label="职业" width="120" />
        <el-table-column prop="serviceIntention" label="服务意向" min-width="150" show-overflow-tooltip />
        <el-table-column prop="totalServiceHours" label="服务时长" width="100">
          <template #default="scope">
            <span class="service-hours">{{ scope.row.totalServiceHours || 0 }}小时</span>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="80">
          <template #default="scope">
            <span class="points">{{ scope.row.points || 0 }}</span>
          </template>
        </el-table-column>
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
                    <el-dropdown-item command="serviceRecords">
                      <el-icon><Document /></el-icon>
                      访问记录
                    </el-dropdown-item>
                    <el-dropdown-item command="updateStatus">
                      <el-icon><Edit /></el-icon>
                      变更状态
                    </el-dropdown-item>
                    <el-dropdown-item command="updateStats">
                      <el-icon><TrendCharts /></el-icon>
                      更新统计
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

    <!-- 创建志愿者对话框 -->
    <el-dialog v-model="createDialogVisible" title="添加志愿者" width="900px" class="volunteer-dialog">
      <el-form ref="createFormRef" :model="createForm" label-width="100px" :rules="createRules">
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="createForm.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="createForm.gender" placeholder="请选择性别" style="width: 100%">
                <el-option label="男" value="Male" />
                <el-option label="女" value="Female" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="createForm.age" :min="1" :max="120" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系方式" prop="phone">
              <el-input v-model="createForm.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="createForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学历">
              <el-select v-model="createForm.education" placeholder="请选择学历" style="width: 100%">
                <el-option label="小学" value="小学" />
                <el-option label="初中" value="初中" />
                <el-option label="高中" value="高中" />
                <el-option label="中专" value="中专" />
                <el-option label="大专" value="大专" />
                <el-option label="本科" value="Bachelor" />
                <el-option label="硕士" value="Master" />
                <el-option label="博士" value="博士" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="24">
            <el-form-item label="详细地址">
              <el-input v-model="createForm.addressDetail" placeholder="请输入详细地址" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="职业">
              <el-input v-model="createForm.occupation" placeholder="请输入职业" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作单位">
              <el-input v-model="createForm.workUnit" placeholder="请输入工作单位" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="24">
            <el-form-item label="专业技能">
              <el-input v-model="createForm.professionalSkills" type="textarea" :rows="3" placeholder="请输入专业技能" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="24">
            <el-form-item label="服务意向">
              <el-input v-model="createForm.serviceIntention" type="textarea" :rows="3" placeholder="请输入服务意向" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="紧急联系人">
              <el-input v-model="createForm.emergencyContactName" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系电话">
              <el-input v-model="createForm.emergencyContactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="关系">
              <el-input v-model="createForm.emergencyContactRelationship" placeholder="请输入关系" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleCreate" :loading="createLoading">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="志愿者详情" width="800px" class="volunteer-dialog">
      <div v-if="selectedVolunteer" class="volunteer-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="姓名">{{ selectedVolunteer.name }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ selectedVolunteer.gender }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ selectedVolunteer.age }}</el-descriptions-item>
          <el-descriptions-item label="联系方式">{{ selectedVolunteer.phone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ selectedVolunteer.email }}</el-descriptions-item>
          <el-descriptions-item label="地址">{{ selectedVolunteer.addressDetail }}</el-descriptions-item>
          <el-descriptions-item label="学历">{{ selectedVolunteer.education }}</el-descriptions-item>
          <el-descriptions-item label="职业">{{ selectedVolunteer.occupation }}</el-descriptions-item>
          <el-descriptions-item label="专业技能" :span="2">{{ selectedVolunteer.professionalSkills }}</el-descriptions-item>
          <el-descriptions-item label="服务意向" :span="2">{{ selectedVolunteer.serviceIntention }}</el-descriptions-item>
          <el-descriptions-item label="服务时长">
            <span class="service-hours">{{ selectedVolunteer.totalServiceHours }}小时</span>
          </el-descriptions-item>
          <el-descriptions-item label="积分">
            <span class="points">{{ selectedVolunteer.points }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedVolunteer.status)">
              {{ getStatusText(selectedVolunteer.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注册日期">{{ selectedVolunteer.registrationDate }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑志愿者" width="900px" class="volunteer-dialog">
      <el-form ref="editFormRef" :model="editForm" label-width="100px" :rules="createRules" v-if="editDialogVisible">
        <!-- 编辑表单内容与创建表单相同 -->
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="editForm.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="editForm.gender" placeholder="请选择性别" style="width: 100%">
                <el-option label="男" value="Male" />
                <el-option label="女" value="Female" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="editForm.age" :min="1" :max="120" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系方式" prop="phone">
              <el-input v-model="editForm.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="editForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学历">
              <el-select v-model="editForm.education" placeholder="请选择学历" style="width: 100%">
                <el-option label="小学" value="小学" />
                <el-option label="初中" value="初中" />
                <el-option label="高中" value="高中" />
                <el-option label="中专" value="中专" />
                <el-option label="大专" value="大专" />
                <el-option label="本科" value="Bachelor" />
                <el-option label="硕士" value="Master" />
                <el-option label="博士" value="博士" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="24">
            <el-form-item label="详细地址">
              <el-input v-model="editForm.addressDetail" placeholder="请输入详细地址" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="职业">
              <el-input v-model="editForm.occupation" placeholder="请输入职业" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作单位">
              <el-input v-model="editForm.workUnit" placeholder="请输入工作单位" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="24">
            <el-form-item label="专业技能">
              <el-input v-model="editForm.professionalSkills" type="textarea" :rows="3" placeholder="请输入专业技能" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="24">
            <el-form-item label="服务意向">
              <el-input v-model="editForm.serviceIntention" type="textarea" :rows="3" placeholder="请输入服务意向" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="紧急联系人">
              <el-input v-model="editForm.emergencyContactName" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系电话">
              <el-input v-model="editForm.emergencyContactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="关系">
              <el-input v-model="editForm.emergencyContactRelationship" placeholder="请输入关系" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleUpdate" :loading="editLoading">更新</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 志愿者服务记录对话框 -->
    <VolunteerServiceRecordsDialog
      v-model="serviceRecordsDialogVisible"
      :volunteer="selectedVolunteer"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, ArrowDown, Edit, TrendCharts, Delete, Document } from '@element-plus/icons-vue'
import { volunteerApi } from '@/api/volunteer'
import VolunteerServiceRecordsDialog from './components/VolunteerServiceRecordsDialog.vue'

// 响应式数据
const loading = ref(false)
const createLoading = ref(false)
const editLoading = ref(false)
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
const createDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const editDialogVisible = ref(false)
const serviceRecordsDialogVisible = ref(false)

// 创建表单
const createForm = reactive({
  name: '',
  gender: '',
  age: null,
  phone: '',
  email: '',
  addressDetail: '',
  education: '',
  occupation: '',
  workUnit: '',
  professionalSkills: '',
  serviceIntention: '',
  emergencyContactName: '',
  emergencyContactPhone: '',
  emergencyContactRelationship: ''
})

// 编辑表单
const editForm = reactive({
  name: '',
  gender: '',
  age: null,
  phone: '',
  email: '',
  addressDetail: '',
  education: '',
  occupation: '',
  workUnit: '',
  professionalSkills: '',
  serviceIntention: '',
  emergencyContactName: '',
  emergencyContactPhone: '',
  emergencyContactRelationship: ''
})

// 表单验证规则
const createRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 加载志愿者列表
const loadVolunteers = async () => {
  loading.value = true
  try {
    const response = await volunteerApi.getVolunteerList({
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
  Object.keys(createForm).forEach(key => {
    createForm[key] = ''
  })
  createForm.age = null
  createDialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (volunteer) => {
  selectedVolunteer.value = volunteer
  Object.keys(editForm).forEach(key => {
    editForm[key] = volunteer[key] || ''
  })
  // 处理性别值的映射
  if (volunteer.gender === 'Male') {
    editForm.gender = 'Male'
  } else if (volunteer.gender === 'Female') {
    editForm.gender = 'Female'
  }
  editDialogVisible.value = true
}

// 创建志愿者
const handleCreate = async () => {
  createLoading.value = true
  try {
    const response = await volunteerApi.createVolunteer(createForm)
    if (response.success) {
      ElMessage.success('创建成功')
      createDialogVisible.value = false
      loadVolunteers()
      loadStats()
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('创建失败')
  } finally {
    createLoading.value = false
  }
}

// 更新志愿者
const handleUpdate = async () => {
  editLoading.value = true
  try {
    const response = await volunteerApi.updateVolunteer(selectedVolunteer.value.id, editForm)
    if (response.success) {
      ElMessage.success('更新成功')
      editDialogVisible.value = false
      loadVolunteers()
      loadStats()
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('更新失败')
  } finally {
    editLoading.value = false
  }
}

// 显示详情对话框
const showDetailDialog = (volunteer) => {
  selectedVolunteer.value = volunteer
  detailDialogVisible.value = true
}

// 处理操作
const handleAction = (command, volunteer) => {
  selectedVolunteer.value = volunteer
  
  switch (command) {
    case 'serviceRecords':
      serviceRecordsDialogVisible.value = true
      break
    case 'updateStatus':
      // TODO: 实现状态更新对话框
      ElMessage.info('状态更新功能开发中')
      break
    case 'updateStats':
      // TODO: 实现统计更新对话框
      ElMessage.info('统计更新功能开发中')
      break
    case 'delete':
      deleteVolunteer(volunteer)
      break
  }
}

// 删除志愿者
const deleteVolunteer = async (volunteer) => {
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

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${multipleSelection.value.length} 个志愿者吗？`,
      '确认批量删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // TODO: 实现批量删除API
    for (const volunteer of multipleSelection.value) {
      await volunteerApi.deleteVolunteer(volunteer.id)
    }
    
    ElMessage.success('批量删除成功')
    loadVolunteers()
    loadStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
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

// 获取性别文本
const getGenderText = (gender) => {
  const genderMap = {
    'Male': '男',
    'Female': '女'
  }
  return genderMap[gender] || '未知'
}

// 初始化
onMounted(() => {
  loadVolunteers()
  loadStats()
})
</script>

<style scoped>
.volunteer-management {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-operations-left {
  margin-right: auto;
}

.search-buttons-left {
  margin-left: 20px;
}

.add-button-right {
  margin-left: auto;
}

.statistics-cards {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-content {
  padding: 15px;
}

.stat-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  font-weight: 500;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-value.total {
  color: #409eff;
}

.stat-value.active {
  color: #67c23a;
}

.stat-value.inactive {
  color: #909399;
}

.stat-value.pending {
  color: #e6a23c;
}

.table-card {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.volunteer-dialog .dialog-footer {
  text-align: right;
}

.volunteer-detail {
  max-height: 500px;
  overflow-y: auto;
}

.service-hours {
  color: #67c23a;
  font-weight: 500;
}

.points {
  color: #409eff;
  font-weight: 500;
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background-color: #f8f9fa;
  color: #333;
  font-weight: 600;
}

:deep(.el-table td) {
  padding: 12px 0;
}

:deep(.el-table .el-table__row:hover) {
  background-color: #f5f7fa;
}

/* 卡片样式优化 */
:deep(.el-card) {
  border-radius: 8px;
  border: 1px solid #ebeef5;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
}

:deep(.el-card__header) {
  background-color: #fafafa;
  border-bottom: 1px solid #ebeef5;
  padding: 18px 20px;
}

/* 按钮样式优化 */
:deep(.el-button--primary) {
  background-color: #409eff;
  border-color: #409eff;
}

:deep(.el-button--primary:hover) {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

/* 标签样式优化 */
:deep(.el-tag) {
  border-radius: 4px;
  font-weight: 500;
}

/* 操作按钮样式优化 */
.action-buttons {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: nowrap;
  justify-content: flex-start;
}

.action-buttons .el-button {
  margin: 0;
  padding: 6px 12px;
  min-width: 48px;
  white-space: nowrap;
  font-size: 13px;
  border-radius: 4px;
}

.action-buttons .el-button--primary.is-link {
  color: #409eff;
  background: transparent;
  border: 1px solid transparent;
  transition: all 0.2s ease;
}

.action-buttons .el-button--primary.is-link:hover {
  color: #ffffff;
  background: #409eff;
  border-color: #409eff;
}

.more-btn {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 下拉菜单样式优化 */
:deep(.el-dropdown-menu) {
  min-width: 120px;
  padding: 8px 0;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  color: #606266;
  font-size: 14px;
}

:deep(.el-dropdown-menu__item:hover) {
  background-color: #f5f7fa;
  color: #409eff;
}

:deep(.el-dropdown-menu__item.delete-item) {
  color: #f56c6c;
}

:deep(.el-dropdown-menu__item.delete-item:hover) {
  background-color: #fef0f0;
  color: #f56c6c;
}

:deep(.el-dropdown-menu__item .el-icon) {
  font-size: 14px;
}

/* 对话框样式优化 */
.volunteer-dialog {
  :deep(.el-dialog) {
    border-radius: 12px;
    overflow: hidden;
  }
  
  :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
    color: white;
    padding: 20px 24px;
    margin: 0;
  }
  
  :deep(.el-dialog__title) {
    color: white;
    font-size: 18px;
    font-weight: 600;
  }
  
  :deep(.el-dialog__headerbtn .el-dialog__close) {
    color: white;
    font-size: 20px;
  }
  
  :deep(.el-dialog__body) {
    padding: 24px;
    background-color: #fafbfc;
  }
  
  :deep(.el-form) {
    background: white;
    padding: 24px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  
  :deep(.el-form-item) {
    margin-bottom: 20px;
  }
  
  :deep(.el-form-item__label) {
    color: #606266;
    font-weight: 500;
    font-size: 14px;
  }
  
  :deep(.el-input__wrapper) {
    border-radius: 6px;
    border: 1px solid #dcdfe6;
    transition: all 0.2s ease;
  }
  
  :deep(.el-input__wrapper:hover) {
    border-color: #c0c4cc;
  }
  
  :deep(.el-input__wrapper.is-focus) {
    border-color: #409eff;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
  }
  
  :deep(.el-textarea__inner) {
    border-radius: 6px;
    border: 1px solid #dcdfe6;
    transition: all 0.2s ease;
  }
  
  :deep(.el-textarea__inner:hover) {
    border-color: #c0c4cc;
  }
  
  :deep(.el-textarea__inner:focus) {
    border-color: #409eff;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
  }
  
  .dialog-footer {
    text-align: right;
    background-color: #fafbfc;
    border-top: 1px solid #ebeef5;
  }
  
  .dialog-footer .el-button {
    padding: 10px 20px;
    font-size: 14px;
    border-radius: 6px;
  }
}
</style>