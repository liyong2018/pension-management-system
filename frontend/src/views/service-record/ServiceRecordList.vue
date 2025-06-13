<template>
  <div class="service-record-list">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="auto" :inline="true">
        <el-form-item label="老人姓名">
          <el-input 
            v-model="searchForm.elderlyName" 
            placeholder="请输入老人姓名" 
            clearable
            style="width: 180px"
          ></el-input>
        </el-form-item>
        <el-form-item label="服务内容">
          <el-input 
            v-model="searchForm.serviceContent" 
            placeholder="请输入服务内容" 
            clearable
            style="width: 180px"
          ></el-input>
        </el-form-item>
        <el-form-item label="服务提供者">
          <el-input 
            v-model="searchForm.serviceProviderName" 
            placeholder="请输入服务提供者" 
            clearable
            style="width: 180px"
          ></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select 
            v-model="searchForm.status" 
            placeholder="请选择状态" 
            clearable
            style="width: 120px"
            popper-class="status-filter-dropdown"
          >
            <el-option label="待处理" value="待处理"></el-option>
            <el-option label="进行中" value="进行中"></el-option>
            <el-option label="已完成" value="已完成"></el-option>
            <el-option label="已评价" value="已评价"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="服务时间">
          <el-date-picker
            v-model="searchForm.serviceTimeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          ></el-date-picker>
        </el-form-item>
        <el-form-item class="search-buttons-left">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
        <el-form-item class="table-operations-left">
          <el-button type="danger" :disabled="!selectedIds.length" @click="handleBatchDelete">
            批量删除
          </el-button>
        </el-form-item>
        <el-form-item class="add-button-right">
          <el-button type="primary" @click="handleAdd">新增服务记录</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <div class="statistics-cards">
      <el-row :gutter="20">
        <el-col :span="6" v-for="(stat, key) in statusStatistics" :key="key">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-title">{{ key }}</div>
              <div class="stat-value">{{ stat }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="serviceRecords"
        @selection-change="handleSelectionChange"
        border
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="elderlyName" label="老人姓名" width="100"></el-table-column>
        <el-table-column prop="serviceContent" label="服务内容" min-width="150"></el-table-column>
        <el-table-column prop="serviceType" label="服务类型" width="120"></el-table-column>
        <el-table-column prop="serviceDuration" label="服务时长" width="100">
          <template #default="{ row }">
            <span v-if="row.serviceDuration">{{ row.serviceDuration }}小时</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="serviceTime" label="服务时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.serviceTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="serviceAddress" label="服务地址" min-width="150"></el-table-column>
        <el-table-column prop="serviceProviderName" label="服务提供者" width="120"></el-table-column>
        <el-table-column prop="workOrderPrice" label="工单价格" width="100">
          <template #default="{ row }">
            <span v-if="row.workOrderPrice">￥{{ row.workOrderPrice }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="evaluationScore" label="评价" width="80">
          <template #default="{ row }">
            <div v-if="row.evaluationScore" class="evaluation">
              <el-rate v-model="row.evaluationScore" disabled show-score text-color="#ff9900"></el-rate>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button 
              v-if="row.status === '已完成'" 
              type="success" 
              link 
              @click="handleEvaluate(row)"
            >
              评价
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        ></el-pagination>
      </div>
    </el-card>

    <!-- 详情/编辑对话框 -->
    <service-record-dialog
      v-model="dialogVisible"
      :mode="dialogMode"
      :record-id="selectedRecordId"
      @success="handleDialogSuccess"
    ></service-record-dialog>

    <!-- 评价对话框 -->
    <evaluation-dialog
      v-model="evaluationDialogVisible"
      :record="selectedRecord"
      @success="handleEvaluationSuccess"
    ></evaluation-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { serviceRecordApi } from '@/api/serviceRecord'
import ServiceRecordDialog from './components/ServiceRecordDialog.vue'
import EvaluationDialog from './components/EvaluationDialog.vue'

// 数据列表
const serviceRecords = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedIds = ref([])

// 统计数据
const statusStatistics = ref({})

// 搜索表单
const searchForm = ref({
  elderlyName: '',
  serviceContent: '',
  serviceProviderName: '',
  status: '',
  serviceTimeRange: []
})

// 对话框控制
const dialogVisible = ref(false)
const dialogMode = ref('view')
const selectedRecordId = ref(null)

// 评价对话框控制
const evaluationDialogVisible = ref(false)
const selectedRecord = ref(null)

// 获取数据列表
const fetchData = async () => {
  try {
    loading.value = true
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      ...searchForm.value
    }
    
    // 处理时间范围
    if (searchForm.value.serviceTimeRange && searchForm.value.serviceTimeRange.length === 2) {
      params.startTime = searchForm.value.serviceTimeRange[0]
      params.endTime = searchForm.value.serviceTimeRange[1]
    }
    delete params.serviceTimeRange

    const res = await serviceRecordApi.getList(params)
    serviceRecords.value = res.list
    total.value = res.total
  } catch (error) {
    ElMessage.error('获取服务记录列表失败')
    console.error('获取服务记录列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const res = await serviceRecordApi.getStatusStatistics()
    statusStatistics.value = res
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 搜索和重置
const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const handleReset = () => {
  searchForm.value = {
    elderlyName: '',
    serviceContent: '',
    serviceProviderName: '',
    status: '',
    serviceTimeRange: []
  }
  currentPage.value = 1
  fetchData()
}

// 分页操作
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchData()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchData()
}

// 表格选择
const handleSelectionChange = (val) => {
  selectedIds.value = val.map(item => item.id)
}

// 状态标签
const getStatusTag = (status) => {
  const map = {
    '待处理': 'warning',
    '进行中': 'primary',
    '已完成': 'success',
    '已评价': 'info'
  }
  return map[status] || 'info'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 新增/编辑/查看操作
const handleAdd = () => {
  dialogMode.value = 'add'
  selectedRecordId.value = null
  dialogVisible.value = true
}

const handleView = (row) => {
  dialogMode.value = 'view'
  selectedRecordId.value = row.id
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogMode.value = 'edit'
  selectedRecordId.value = row.id
  dialogVisible.value = true
}

// 评价操作
const handleEvaluate = (row) => {
  selectedRecord.value = row
  evaluationDialogVisible.value = true
}

// 删除操作
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条服务记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await serviceRecordApi.delete(row.id)
    ElMessage.success('删除成功')
    fetchData()
    fetchStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error('删除失败:', error)
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条服务记录吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await serviceRecordApi.batchDelete(selectedIds.value)
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    fetchData()
    fetchStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
      console.error('批量删除失败:', error)
    }
  }
}

// 对话框成功回调
const handleDialogSuccess = () => {
  fetchData()
  fetchStatistics()
}

// 评价成功回调
const handleEvaluationSuccess = () => {
  fetchData()
  fetchStatistics()
}

// 初始化
onMounted(() => {
  fetchData()
  fetchStatistics()
})
</script>

<style scoped>
.service-record-list {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-operations-left {
  margin-right: 5px;
  float: right;
}

.search-buttons-left {
  margin-left: 20px;
}

.add-button-right {
  margin-right: 15px;
  float: right;
}

.statistics-cards {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-content {
  padding: 10px;
}

.stat-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.table-card {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.evaluation {
  display: flex;
  align-items: center;
}
</style>

<style>
/* 状态筛选下拉框样式 */
.status-filter-dropdown {
  min-width: 100px !important;
}

.status-filter-dropdown .el-select-dropdown__item {
  white-space: nowrap;
  padding: 0 12px;
  text-align: center;
}
</style> 