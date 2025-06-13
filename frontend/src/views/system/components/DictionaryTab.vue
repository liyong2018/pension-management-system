<template>
  <div class="dictionary-tab">
    <!-- 搜索栏 -->
    <el-row :gutter="20" class="search-bar" type="flex" align="middle">
      <el-col :span="6">
        <el-input
          v-model="searchForm.dictCode"
          placeholder="请输入字典编码"
          clearable
          @keyup.enter="handleSearch"
        />
      </el-col>
      <el-col :span="6">
        <el-input
          v-model="searchForm.dictLabel"
          placeholder="请输入字典标签"
          clearable
          @keyup.enter="handleSearch"
        />
      </el-col>
      <el-col :span="4">
        <el-select
          v-model="searchForm.status"
          placeholder="状态"
          clearable
        >
          <el-option 
            v-for="option in statusOptions" 
            :key="option.value"
            :label="option.label" 
            :value="option.value" 
          />
        </el-select>
      </el-col>
      <el-col :span="6">
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
        <el-button type="success" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增
        </el-button>
      </el-col>
      <el-col :span="2" style="margin-left:auto;display:flex;justify-content:flex-end;align-items:center;">
        <el-button 
          type="danger" 
          :disabled="selectedRows.length === 0"
          @click="handleBatchDelete"
        >
          <el-icon><Delete /></el-icon>
          批量删除 ({{ selectedRows.length }})
        </el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      stripe
      border
      @selection-change="handleSelectionChange"
      empty-text="暂无数据"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="dictCode" label="字典编码" width="150" sortable />
      <el-table-column prop="dictLabel" label="字典标签" width="150" sortable />
      <el-table-column prop="dictValue" label="字典值" min-width="200" show-overflow-tooltip />
      <el-table-column prop="sortOrder" label="排序" width="80" sortable />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">
            {{ getStatusName(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="180" sortable>
        <template #default="scope">
          {{ formatDateTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <el-button
            type="primary"
            size="small"
            @click="handleEdit(scope.row)"
          >
            编辑
          </el-button>
          <el-button
            type="danger"
            size="small"
            @click="handleDelete(scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      class="pagination"
    />

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="字典编码" prop="dictCode">
          <el-input
            v-model="formData.dictCode"
            placeholder="请输入字典编码，如：COMMUNITY_001"
            :disabled="isEdit"
            maxlength="100"
            show-word-limit
          />
          <div class="form-tip" v-if="!isEdit">
            字典编码用于程序内部识别，建议使用英文大写字母和下划线
          </div>
        </el-form-item>
        <el-form-item label="字典标签" prop="dictLabel">
          <el-input
            v-model="formData.dictLabel"
            placeholder="请输入字典标签，如：朝阳公园社区"
            maxlength="100"
            show-word-limit
          />
          <div class="form-tip">
            字典标签是显示给用户看的名称
          </div>
        </el-form-item>
        <el-form-item label="字典值" prop="dictValue">
          <el-input
            v-model="formData.dictValue"
            placeholder="请输入字典值，通常与标签相同"
            maxlength="200"
            show-word-limit
          />
          <div class="form-tip">
            字典值是实际存储的数据值
          </div>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number
            v-model="formData.sortOrder"
            :min="0"
            :max="9999"
            placeholder="请输入排序"
            style="width: 200px;"
          />
          <div class="form-tip">
            数字越小排序越靠前
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio 
              v-for="option in statusOptions" 
              :key="option.value"
              :value="option.value"
            >
              {{ option.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息（可选）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import { dictionaryApi } from '@/api/dictionary'
import { formatDateTime } from '@/utils/dateUtils'
import { DICT_STATUS_OPTIONS, DICT_STATUS_NAMES } from '@/constants/dictionary'

const props = defineProps({
  dictType: {
    type: String,
    required: true
  },
  dictTypeName: {
    type: String,
    required: true
  }
})

// 定义事件发射
const emit = defineEmits(['data-change'])

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const selectedRows = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

// 状态选项
const statusOptions = DICT_STATUS_OPTIONS

// 搜索表单
const searchForm = reactive({
  dictCode: '',
  dictLabel: '',
  status: ''
})

// 分页数据
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 表单数据
const formData = reactive({
  id: null,
  dictType: props.dictType,
  dictCode: '',
  dictLabel: '',
  dictValue: '',
  sortOrder: 0,
  status: 'ACTIVE',
  remark: ''
})

// 表单验证规则
const formRules = {
  dictCode: [
    { required: true, message: '请输入字典编码', trigger: 'blur' },
    { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' },
    { pattern: /^[A-Z0-9_]+$/, message: '字典编码只能包含大写字母、数字和下划线', trigger: 'blur' }
  ],
  dictLabel: [
    { required: true, message: '请输入字典标签', trigger: 'blur' },
    { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  dictValue: [
    { required: true, message: '请输入字典值', trigger: 'blur' },
    { min: 1, max: 200, message: '长度在 1 到 200 个字符', trigger: 'blur' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序', trigger: 'blur' },
    { type: 'number', min: 0, max: 9999, message: '排序必须在 0 到 9999 之间', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 计算属性
const dialogTitle = computed(() => {
  return isEdit.value ? `编辑${props.dictTypeName}` : `新增${props.dictTypeName}`
})

// 获取状态名称
const getStatusName = (status) => {
  return DICT_STATUS_NAMES[status] || status
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  return status === 'ACTIVE' ? 'success' : 'danger'
}

// 方法
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      dictType: props.dictType,
      dictCode: searchForm.dictCode || undefined,
      dictLabel: searchForm.dictLabel || undefined,
      status: searchForm.status || undefined,
      page: pagination.page,
      size: pagination.size
    }
    
    const response = await dictionaryApi.getList(params)
    tableData.value = response.list || []
    pagination.total = response.total || 0
  } catch (error) {
    console.error('获取字典数据失败:', error)
    ElMessage.error('获取字典数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

const handleReset = () => {
  searchForm.dictCode = ''
  searchForm.dictLabel = ''
  searchForm.status = ''
  pagination.page = 1
  fetchData()
}

const handleAdd = () => {
  isEdit.value = false
  resetFormData()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除字典"${row.dictLabel}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await dictionaryApi.delete(row.id)
    ElMessage.success('删除成功')
    fetchData()
    // 发射数据变化事件
    emit('data-change')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除字典失败:', error)
      ElMessage.error('删除字典失败')
    }
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 条记录吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const ids = selectedRows.value.map(row => row.id)
    await dictionaryApi.batchDelete(ids)
    ElMessage.success('批量删除成功')
    selectedRows.value = []
    fetchData()
    // 发射数据变化事件
    emit('data-change')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除字典失败:', error)
      ElMessage.error('批量删除字典失败')
    }
  }
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchData()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  fetchData()
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true
    
    if (isEdit.value) {
      await dictionaryApi.update(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await dictionaryApi.create(formData)
      ElMessage.success('创建成功')
    }
    
    dialogVisible.value = false
    fetchData()
    // 发射数据变化事件
    emit('data-change')
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败: ' + (error.message || '未知错误'))
  } finally {
    submitLoading.value = false
  }
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetFormData()
}

const resetFormData = () => {
  formData.id = null
  formData.dictType = props.dictType
  formData.dictCode = ''
  formData.dictLabel = ''
  formData.dictValue = ''
  formData.sortOrder = 0
  formData.status = 'ACTIVE'
  formData.remark = ''
}

// 监听字典类型变化
watch(() => props.dictType, () => {
  fetchData()
}, { immediate: false })

// 组件挂载时获取数据
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.dictionary-tab {
  padding: 0;
}

.search-bar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.4;
}

:deep(.el-table) {
  margin-bottom: 20px;
}

:deep(.el-table .el-table__empty-text) {
  color: #909399;
}

:deep(.el-input-number) {
  width: 100%;
}

/* 表格行悬停效果 */
:deep(.el-table .el-table__row:hover) {
  background-color: #f5f7fa;
}

/* 选中行样式 */
:deep(.el-table .el-table__row.current-row) {
  background-color: #ecf5ff;
}
</style> 