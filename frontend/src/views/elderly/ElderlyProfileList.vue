<template>
  <div class="elderly-profile-list">
    <!-- 页面标题 -->
    <!-- <div class="page-header">
      <h2>人员档案管理</h2>
      <el-button type="primary" @click="handleAdd">新增人员</el-button>
    </div> -->

    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="auto" :inline="true">
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable></el-input>
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="searchForm.idCardNumber" placeholder="请输入身份证号" clearable></el-input>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="searchForm.phone" placeholder="请输入联系电话" clearable></el-input>
        </el-form-item>
        <el-form-item label="老人类型">
          <el-select 
            v-model="searchForm.elderlyType" 
            placeholder="请选择老人类型" 
            clearable
            :loading="elderlyTypeLoading"
            filterable
          >
            <el-option
              v-for="item in elderlyTypeOptions"
              :key="item.dictCode"
              :label="item.dictLabel"
              :value="item.dictValue"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="search-buttons-left">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
        <el-form-item class="add-button-right">
          <el-button type="primary" @click="handleAdd">新增人员</el-button>
        </el-form-item>
        <el-form-item class="table-operations-left">
          <el-button type="danger" :disabled="!selectedIds.length" @click="handleBatchDelete">
            批量删除
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <!-- <div class="table-operations">
        <el-button type="danger" :disabled="!selectedIds.length" @click="handleBatchDelete">
          批量删除
        </el-button>
      </div> -->

      <el-table
        v-loading="loading"
        :data="elderlyProfiles"
        @selection-change="handleSelectionChange"
        border
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="name" label="姓名" min-width="100"></el-table-column>
        <el-table-column prop="gender" label="性别" width="80"></el-table-column>
        <el-table-column prop="addressDetail" label="住址" min-width="150"></el-table-column>
        <el-table-column prop="phone" label="联系电话" width="120"></el-table-column>
        <el-table-column prop="community" label="所属社区" width="120"></el-table-column>
        <el-table-column prop="pensionType" label="养老类型" width="200">
          <template #default="{ row }">
            <el-tag :type="getPensionTypeTag(row.pensionType)">{{ row.pensionType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="elderlyType" label="老人类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getElderlyTypeTag(row.elderlyType)" v-if="row.elderlyTypeLabel">
              {{ row.elderlyTypeLabel }}
            </el-tag>
            <span v-else class="text-gray-400">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="console.log('查看按钮被点击, row:', row); handleView(row)">查看</el-button>
            <el-button type="primary" link @click="console.log('编辑按钮被点击, row:', row); handleEdit(row)">编辑</el-button>
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
    <elderly-profile-dialog
      v-if="dialogVisible"
      v-model="dialogVisible"
      :mode="dialogMode"
      :elderly-id="selectedElderlyId"
      @success="handleDialogSuccess"
    ></elderly-profile-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { elderlyProfileApi } from '@/api/elderlyProfile'
import { dictionaryApi } from '@/api/dictionary'
import ElderlyProfileDialog from './components/ElderlyProfileDialog.vue'

// 数据列表
const elderlyProfiles = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedIds = ref([])

// 搜索表单
const searchForm = ref({
  name: '',
  idCardNumber: '',
  phone: '',
  elderlyType: ''
})

// 对话框控制
const dialogVisible = ref(false)
const dialogMode = ref('view')
const selectedElderlyId = ref(null)

// 老人类型字典数据
const elderlyTypeOptions = ref([])
const elderlyTypeLoading = ref(false)

// 获取数据列表
const fetchData = async () => {
  try {
    loading.value = true
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      ...searchForm.value
    }
    const res = await elderlyProfileApi.getList(params)
    elderlyProfiles.value = res.list
    total.value = res.total
  } catch (error) {
    ElMessage.error('获取人员列表失败')
    console.error('获取人员列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索和重置
const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const handleReset = () => {
  searchForm.value = {
    name: '',
    idCardNumber: '',
    phone: '',
    elderlyType: ''
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

// 养老类型标签
const getPensionTypeTag = (type) => {
  const map = {
    '居家养老': '',
    '社区养老': 'success',
    '机构养老': 'warning'
  }
  return map[type] || 'info'
}

// 老人类型标签
const getElderlyTypeTag = (type) => {
  const map = {
    'normal': '',
    'empty_nest': 'warning',
    'living_alone': 'danger',
    'disabled': 'danger',
    'elderly': 'warning',
    'low_income': 'info',
    'special_care': 'danger'
  }
  return map[type] || 'info'
}

// 新增/编辑/查看操作
const handleAdd = () => {
  console.log('handleAdd called');
  dialogMode.value = 'add'
  selectedElderlyId.value = null
  dialogVisible.value = true
}

const handleView = (row) => {
  console.log('handleView called with row:', row);
  if (row && row.id) {
    dialogMode.value = 'view'
    selectedElderlyId.value = row.id
    console.log(`handleView: Setting dialogVisible to true. Mode: ${dialogMode.value}, ID: ${selectedElderlyId.value}`);
    dialogVisible.value = true
  } else {
    console.error('handleView: row or row.id is missing', row);
    ElMessage.error('无法查看：选中行数据不完整。');
  }
}

const handleEdit = (row) => {
  console.log('handleEdit called with row:', row);
  if (row && row.id) {
    dialogMode.value = 'edit'
    selectedElderlyId.value = row.id
    console.log(`handleEdit: Setting dialogVisible to true. Mode: ${dialogMode.value}, ID: ${selectedElderlyId.value}`);
    dialogVisible.value = true
  } else {
    console.error('handleEdit: row or row.id is missing', row);
    ElMessage.error('无法编辑：选中行数据不完整。');
  }
}

// 删除操作
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该人员档案吗？', '提示', {
      type: 'warning'
    })
    await elderlyProfileApi.delete(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error('删除失败:', error)
    }
  }
}

const handleBatchDelete = async () => {
  if (!selectedIds.value.length) return
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条记录吗？`, '提示', {
      type: 'warning'
    })
    await elderlyProfileApi.batchDelete(selectedIds.value)
    ElMessage.success('批量删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
      console.error('批量删除失败:', error)
    }
  }
}

// 对话框回调
const handleDialogSuccess = () => {
  dialogVisible.value = false
  fetchData()
}

// 获取老人类型字典数据
const fetchElderlyTypeOptions = async () => {
  elderlyTypeLoading.value = true
  try {
    const data = await dictionaryApi.getByType('elderly_type')
    elderlyTypeOptions.value = data.filter(item => item.status === 'ACTIVE') || []
    console.log('获取老人类型字典数据成功:', elderlyTypeOptions.value)
  } catch (error) {
    console.error('获取老人类型字典数据失败:', error)
    ElMessage.error('获取老人类型选项失败')
    elderlyTypeOptions.value = []
  } finally {
    elderlyTypeLoading.value = false
  }
}

// 初始化
onMounted(() => {
  fetchData()
  fetchElderlyTypeOptions()
})
</script>

<style scoped>
.elderly-profile-list {
  padding: 20px;
}

/* .page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
} */

.search-card {
  margin-bottom: 20px;
}

.table-card {
  /* 如果需要，可以为表格卡片添加样式 */
}

.table-operations {
  margin-bottom: 16px;
  display: flex;
  justify-content: flex-start;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.el-form--inline .el-form-item {
  margin-right: 10px;
}

.search-card .el-form {
  display: flex;
  align-items: center; /* 垂直居中对齐表单项 */
  flex-wrap: wrap; /* 允许换行 */
}

.table-operations-left {
  margin-right: auto; /* 将批量删除按钮推到最左边 */
}

.search-buttons-left {
  /* 搜索和重置按钮默认靠左，可以根据需要调整 margin-left */
  /* margin-left: 10px; */
}

.add-button-right {
  margin-left: auto; /* 将新增人员按钮推到最右边 */
}
</style> 