<template>
  <div class="dictionary-management-simple">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>字典管理</span>
          <el-tag type="info" size="small">
            简化版本
          </el-tag>
        </div>
      </template>

      <!-- 连接状态检查 -->
      <el-alert
        v-if="!backendConnected"
        title="后端连接异常"
        type="warning"
        :closable="false"
        show-icon
      >
        <p>无法连接到后端服务，请确保：</p>
        <ul>
          <li>1. 后端Spring Boot服务正常运行</li>
          <li>2. 数据库中存在dictionary表</li>
          <li>3. 字典相关的Java类已正确加载</li>
        </ul>
        <el-button type="primary" size="small" @click="checkBackendConnection" :loading="checking">
          重新检查连接
        </el-button>
      </el-alert>

      <!-- 功能选择 -->
      <div class="function-selector" v-if="backendConnected">
        <el-radio-group v-model="selectedDictType" @change="handleDictTypeChange">
          <el-radio-button value="community">所属社区</el-radio-button>
          <el-radio-button value="pensionType">养老类型</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons" v-if="backendConnected">
        <el-button type="primary" @click="loadData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
        <el-button type="success" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          新增字典
        </el-button>
        <el-button 
          type="danger" 
          :disabled="selectedRows.length === 0"
          @click="handleBatchDelete"
        >
          <el-icon><Delete /></el-icon>
          批量删除 ({{ selectedRows.length }})
        </el-button>
      </div>

      <!-- 数据表格 -->
      <el-table
        v-if="backendConnected"
        v-loading="loading"
        :data="tableData"
        stripe
        border
        @selection-change="handleSelectionChange"
        empty-text="暂无数据"
        style="margin-top: 20px;"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="dictCode" label="字典编码" width="150" />
        <el-table-column prop="dictLabel" label="字典标签" width="150" />
        <el-table-column prop="dictValue" label="字典值" min-width="200" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'danger'">
              {{ scope.row.status === 'ACTIVE' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
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

      <!-- 新增/编辑对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="600px"
        @close="handleDialogClose"
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
            />
          </el-form-item>
          <el-form-item label="字典标签" prop="dictLabel">
            <el-input
              v-model="formData.dictLabel"
              placeholder="请输入字典标签，如：朝阳公园社区"
            />
          </el-form-item>
          <el-form-item label="字典值" prop="dictValue">
            <el-input
              v-model="formData.dictValue"
              placeholder="请输入字典值，通常与标签相同"
            />
          </el-form-item>
          <el-form-item label="排序" prop="sortOrder">
            <el-input-number
              v-model="formData.sortOrder"
              :min="0"
              :max="9999"
              placeholder="请输入排序"
            />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="formData.status">
              <el-radio value="ACTIVE">启用</el-radio>
              <el-radio value="INACTIVE">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="formData.remark"
              type="textarea"
              :rows="3"
              placeholder="请输入备注信息（可选）"
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
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Plus, Delete } from '@element-plus/icons-vue'
import axios from 'axios'

// 响应式数据
const backendConnected = ref(false)
const checking = ref(false)
const loading = ref(false)
const submitLoading = ref(false)
const selectedDictType = ref('community')
const tableData = ref([])
const selectedRows = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

// 表单数据
const formData = reactive({
  id: null,
  dictType: 'community',
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
    { required: true, message: '请输入字典编码', trigger: 'blur' }
  ],
  dictLabel: [
    { required: true, message: '请输入字典标签', trigger: 'blur' }
  ],
  dictValue: [
    { required: true, message: '请输入字典值', trigger: 'blur' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 计算属性
const dialogTitle = ref('')

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  if (isNaN(date.getTime())) return ''
  
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 检查后端连接
const checkBackendConnection = async () => {
  checking.value = true
  try {
    // 先测试基础API
    const response = await axios.get('/api/organizations')
    if (response.status === 200) {
      // 再测试字典API
      try {
        await axios.get('/api/dictionaries/type/community')
        backendConnected.value = true
        ElMessage.success('后端连接正常')
        loadData()
      } catch (dictError) {
        console.error('字典API连接失败:', dictError)
        ElMessage.warning('基础API正常，但字典API不可用，请检查后端字典模块')
        backendConnected.value = false
      }
    }
  } catch (error) {
    console.error('后端连接失败:', error)
    ElMessage.error('后端连接失败，请检查服务状态')
    backendConnected.value = false
  } finally {
    checking.value = false
  }
}

// 加载数据
const loadData = async () => {
  if (!backendConnected.value) return
  
  loading.value = true
  try {
    const response = await axios.get(`/api/dictionaries/type/${selectedDictType.value}`)
    tableData.value = response.data || []
    ElMessage.success('数据加载成功')
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
    tableData.value = []
  } finally {
    loading.value = false
  }
}

// 处理字典类型变化
const handleDictTypeChange = (value) => {
  selectedDictType.value = value
  formData.dictType = value
  loadData()
}

// 显示新增对话框
const showAddDialog = () => {
  isEdit.value = false
  dialogTitle.value = `新增${selectedDictType.value === 'community' ? '所属社区' : '养老类型'}`
  resetFormData()
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = `编辑${selectedDictType.value === 'community' ? '所属社区' : '养老类型'}`
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 处理删除
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
    
    await axios.delete(`/api/dictionaries/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 处理批量删除
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
    await axios.delete('/api/dictionaries/batch', { data: ids })
    ElMessage.success('批量删除成功')
    selectedRows.value = []
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 处理提交
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true
    
    if (isEdit.value) {
      await axios.put(`/api/dictionaries/${formData.id}`, formData)
      ElMessage.success('更新成功')
    } else {
      await axios.post('/api/dictionaries', formData)
      ElMessage.success('创建成功')
    }
    
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  } finally {
    submitLoading.value = false
  }
}

// 处理对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetFormData()
}

// 重置表单数据
const resetFormData = () => {
  formData.id = null
  formData.dictType = selectedDictType.value
  formData.dictCode = ''
  formData.dictLabel = ''
  formData.dictValue = ''
  formData.sortOrder = 0
  formData.status = 'ACTIVE'
  formData.remark = ''
}

// 组件挂载时检查连接
onMounted(() => {
  checkBackendConnection()
})
</script>

<style scoped>
.dictionary-management-simple {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.function-selector {
  margin: 20px 0;
}

.action-buttons {
  margin: 20px 0;
}

.dialog-footer {
  text-align: right;
}

:deep(.el-alert) {
  margin-bottom: 20px;
}

:deep(.el-alert ul) {
  margin: 10px 0 0 20px;
  padding: 0;
}

:deep(.el-alert li) {
  margin: 5px 0;
}
</style> 