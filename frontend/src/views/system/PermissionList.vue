<template>
  <div class="permission-management">
    <!-- 统计卡片 -->
    <div class="statistics-cards" v-if="stats">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon size="24"><Lock /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">总权限数</div>
                <div class="stat-value total">{{ stats.totalPermissions }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon active">
                <el-icon size="24"><Menu /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">菜单权限</div>
                <div class="stat-value active">{{ stats.menuPermissions }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon warning">
                <el-icon size="24"><Operation /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">操作权限</div>
                <div class="stat-value warning">{{ stats.operationPermissions }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon info">
                <el-icon size="24"><Avatar /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">已分配角色</div>
                <div class="stat-value info">{{ stats.assignedRoles }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="auto" :inline="true">
        <el-form-item class="table-operations-left">
          <el-button type="danger" :disabled="!multipleSelection.length" @click="handleBatchDelete">
            批量删除
          </el-button>
        </el-form-item>

        <el-form-item label="权限名称">
          <el-input 
            v-model="searchForm.name" 
            placeholder="请输入权限名称" 
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="权限标识">
          <el-input 
            v-model="searchForm.code" 
            placeholder="请输入权限标识" 
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="权限类型">
          <el-select 
            v-model="searchForm.type" 
            placeholder="请选择类型" 
            clearable
            style="width: 120px"
          >
            <el-option label="菜单" value="MENU" />
            <el-option label="按钮" value="BUTTON" />
            <el-option label="接口" value="API" />
          </el-select>
        </el-form-item>
        <el-form-item class="search-buttons-left">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
        <el-form-item class="add-button-right">
          <el-button type="primary" @click="showCreateDialog">
            <el-icon><Plus /></el-icon>
            添加权限
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table
        :data="permissions"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        border
        stripe
        style="width: 100%"
        :header-cell-style="{ backgroundColor: '#f5f7fa', color: '#303133', fontWeight: '600' }"
        :row-style="{ height: '60px' }"
        class="permission-table"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="权限名称" width="160">
          <template #default="scope">
            <div class="permission-info">
              <el-avatar :size="32" class="permission-avatar">
                <el-icon><Lock /></el-icon>
              </el-avatar>
              <span class="permission-name">{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="code" label="权限标识" width="180" />
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getTypeColor(scope.row.type)" size="small" effect="light">
              <el-icon style="margin-right: 4px">
                <Menu v-if="scope.row.type === 'MENU'" />
                <Operation v-else-if="scope.row.type === 'BUTTON'" />
                <Link v-else />
              </el-icon>
              {{ getTypeText(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="url" label="路径/接口" min-width="200" show-overflow-tooltip />
        <el-table-column prop="parentName" label="父级权限" width="140" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === '1' ? 'success' : 'warning'" size="small" effect="light">
              <el-icon style="margin-right: 4px">
                <CircleCheck v-if="scope.row.status === '1'" />
                <CircleClose v-else />
              </el-icon>
              {{ scope.row.status === '1' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
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
                    <el-dropdown-item command="addChild">
                      <el-icon><Plus /></el-icon>
                      添加子权限
                    </el-dropdown-item>
                    <el-dropdown-item command="roles">
                      <el-icon><Avatar /></el-icon>
                      查看角色
                    </el-dropdown-item>
                    <el-dropdown-item command="copy">
                      <el-icon><CopyDocument /></el-icon>
                      复制权限
                    </el-dropdown-item>
                    <el-dropdown-item command="toggleStatus">
                      <el-icon><Switch /></el-icon>
                      {{ scope.row.status === '1' ? '禁用权限' : '启用权限' }}
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

    <!-- 权限详情/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogMode === 'create' ? '添加权限' : dialogMode === 'edit' ? '编辑权限' : '权限详情'" 
      width="700px" 
      class="permission-dialog"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div class="permission-form-container">
        <el-form
          ref="formRef"
          :model="currentPermission"
          :rules="formRules"
          label-width="100px"
          :disabled="dialogMode === 'view'"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="权限名称" prop="name">
                <el-input 
                  v-model="currentPermission.name" 
                  placeholder="请输入权限名称"
                  maxlength="50"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="权限标识" prop="code">
                <el-input 
                  v-model="currentPermission.code" 
                  placeholder="请输入权限标识"
                  maxlength="100"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="权限类型" prop="type">
                <el-select v-model="currentPermission.type" placeholder="请选择权限类型" style="width: 100%">
                  <el-option label="菜单" value="MENU" />
                  <el-option label="按钮" value="BUTTON" />
                  <el-option label="接口" value="API" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="父级权限" prop="parentId">
                <el-select v-model="currentPermission.parentId" placeholder="请选择父级权限" style="width: 100%" clearable>
                  <el-option label="无" :value="null" />
                  <el-option 
                    v-for="item in parentPermissions" 
                    :key="item.id" 
                    :label="item.name" 
                    :value="item.id" 
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="路径/接口" prop="url">
            <el-input 
              v-model="currentPermission.url" 
              placeholder="请输入菜单路径或API接口地址"
              maxlength="255"
              show-word-limit
            />
          </el-form-item>
          <el-form-item label="权限描述" prop="description">
            <el-input 
              v-model="currentPermission.description" 
              type="textarea" 
              :rows="3"
              placeholder="请输入权限描述"
              maxlength="255"
              show-word-limit
            />
          </el-form-item>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="排序" prop="sort">
                <el-input-number 
                  v-model="currentPermission.sort" 
                  :min="0" 
                  :max="999" 
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="状态" prop="status">
                <el-radio-group v-model="currentPermission.status">
                  <el-radio label="1">启用</el-radio>
                  <el-radio label="0">禁用</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button v-if="dialogMode !== 'view'" type="primary" @click="handleSubmit" :loading="submitLoading">
            {{ dialogMode === 'create' ? '添加' : '保存' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Lock, Menu, Operation, Avatar, Plus, CircleCheck, CircleClose, 
  ArrowDown, Link, CopyDocument, Switch, Delete
} from '@element-plus/icons-vue'

export default {
  name: 'PermissionList',
  components: {
    Lock, Menu, Operation, Avatar, Plus, CircleCheck, CircleClose,
    ArrowDown, Link, CopyDocument, Switch, Delete
  },
  setup() {
    // 响应式数据
    const loading = ref(false)
    const createLoading = ref(false)
    const editLoading = ref(false)
    const roleLoading = ref(false)
    const permissions = ref([])
    const selectedPermission = ref(null)
    const multipleSelection = ref([])
    const parentPermissions = ref([])
    const availableRoles = ref([])
    const selectedRoles = ref([])
    const stats = ref({
      totalPermissions: 0,
      menuPermissions: 0,
      operationPermissions: 0,
      assignedRoles: 0
    })

    // 搜索表单
    const searchForm = reactive({
      name: '',
      code: '',
      type: ''
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
    const roleDialogVisible = ref(false)

    // 统一的对话框状态
    const dialogVisible = ref(false)
    const dialogMode = ref('view') // 'view', 'create', 'edit'
    const currentPermission = reactive({
      name: '',
      code: '',
      type: 'MENU',
      parentId: null,
      url: '',
      description: '',
      sort: 0,
      status: '1'
    })

    // 表单验证规则
    const formRules = {
      name: [
        { required: true, message: '请输入权限名称', trigger: 'blur' }
      ],
      code: [
        { required: true, message: '请输入权限标识', trigger: 'blur' }
      ],
      type: [
        { required: true, message: '请选择权限类型', trigger: 'change' }
      ]
    }

    // 模拟权限数据
    const mockPermissions = [
      { id: 1, name: '用户管理', code: 'user:manage', type: 'MENU', description: '用户管理菜单权限', parentId: null, parentName: null, createTime: '2024-01-01 10:00:00' },
      { id: 2, name: '创建用户', code: 'user:create', type: 'ACTION', description: '创建用户操作权限', parentId: 1, parentName: '用户管理', createTime: '2024-01-01 10:00:00' },
      { id: 3, name: '编辑用户', code: 'user:edit', type: 'ACTION', description: '编辑用户操作权限', parentId: 1, parentName: '用户管理', createTime: '2024-01-01 10:00:00' },
      { id: 4, name: '删除用户', code: 'user:delete', type: 'ACTION', description: '删除用户操作权限', parentId: 1, parentName: '用户管理', createTime: '2024-01-01 10:00:00' },
      { id: 5, name: '角色管理', code: 'role:manage', type: 'MENU', description: '角色管理菜单权限', parentId: null, parentName: null, createTime: '2024-01-01 10:00:00' },
      { id: 6, name: '机构管理', code: 'org:manage', type: 'MENU', description: '机构管理菜单权限', parentId: null, parentName: null, createTime: '2024-01-01 10:00:00' }
    ]

    // 模拟角色数据
    const mockRoles = [
      { id: 1, name: '系统管理员', roleKey: 'SYSTEM_ADMIN', description: '拥有所有系统权限' },
      { id: 2, name: '机构管理员', roleKey: 'ORG_ADMIN', description: '管理机构相关业务' },
      { id: 3, name: '普通用户', roleKey: 'USER', description: '基础查看权限' },
      { id: 4, name: '数据分析师', roleKey: 'DATA_ANALYST', description: '数据查看和分析权限' }
    ]

    // 加载权限列表
    const loadPermissions = async () => {
      loading.value = true
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 500))
        
        let filteredData = [...mockPermissions]
        
        // 模拟搜索
        if (searchForm.name) {
          filteredData = filteredData.filter(p => 
            p.name.includes(searchForm.name)
          )
        }
        if (searchForm.code) {
          filteredData = filteredData.filter(p => 
            p.code.includes(searchForm.code)
          )
        }
        if (searchForm.type) {
          filteredData = filteredData.filter(p => 
            p.type === searchForm.type
          )
        }
        
        // 模拟分页
        const startIndex = (pagination.page - 1) * pagination.pageSize
        const endIndex = startIndex + pagination.pageSize
        permissions.value = filteredData.slice(startIndex, endIndex)
        pagination.total = filteredData.length
        
        // 更新统计信息
        stats.value = {
          totalPermissions: mockPermissions.length,
          menuPermissions: mockPermissions.filter(p => p.type === 'MENU').length,
          operationPermissions: mockPermissions.filter(p => p.type === 'ACTION').length,
          assignedRoles: mockRoles.length
        }
        
        // 设置父级权限选项
        parentPermissions.value = mockPermissions.filter(p => p.type === 'MENU')
        
        // 设置可用角色
        availableRoles.value = mockRoles
      } catch (error) {
        ElMessage.error('加载权限列表失败')
        console.error('Error loading permissions:', error)
      } finally {
        loading.value = false
      }
    }

    // 获取权限类型文本
    const getTypeText = (type) => {
      const typeMap = {
        'MENU': '菜单权限',
        'ACTION': '操作权限', 
        'API': '接口权限'
      }
      return typeMap[type] || type
    }

    // 获取权限类型颜色
    const getTypeColor = (type) => {
      const colorMap = {
        'MENU': 'primary',
        'ACTION': 'success',
        'API': 'warning'
      }
      return colorMap[type] || 'info'
    }

    // 搜索
    const handleSearch = () => {
      pagination.page = 1
      loadPermissions()
    }

    // 重置
    const handleReset = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      pagination.page = 1
      loadPermissions()
    }

    // 显示创建对话框
    const showCreateDialog = () => {
      Object.keys(currentPermission).forEach(key => {
        currentPermission[key] = key === 'parentId' ? null : ''
      })
      dialogMode.value = 'create'
      dialogVisible.value = true
    }

    // 创建权限
    const handleCreate = async () => {
      createLoading.value = true
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        ElMessage.success('创建成功')
        dialogVisible.value = false
        loadPermissions()
      } catch (error) {
        ElMessage.error('创建失败')
        console.error('Error creating permission:', error)
      } finally {
        createLoading.value = false
      }
    }

    // 显示详情对话框
    const showDetailDialog = (permission) => {
      selectedPermission.value = { ...permission }
      dialogMode.value = 'view'
      dialogVisible.value = true
    }

    // 显示编辑对话框
    const showEditDialog = (permission) => {
      selectedPermission.value = permission
      Object.keys(currentPermission).forEach(key => {
        currentPermission[key] = permission[key] || (key === 'parentId' ? null : '')
      })
      dialogMode.value = 'edit'
      dialogVisible.value = true
    }

    // 更新权限
    const handleUpdate = async () => {
      editLoading.value = true
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        ElMessage.success('更新成功')
        dialogVisible.value = false
        loadPermissions()
      } catch (error) {
        ElMessage.error('更新失败')
        console.error('Error updating permission:', error)
      } finally {
        editLoading.value = false
      }
    }

    // 显示角色分配对话框
    const showRoleDialog = async (permission) => {
      selectedPermission.value = permission
      // 模拟已分配的角色
      selectedRoles.value = [1, 2] // 假设该权限已分配给系统管理员和机构管理员
      roleDialogVisible.value = true
    }

    // 分配角色
    const handleRoleAssign = async () => {
      roleLoading.value = true
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        ElMessage.success('角色分配成功')
        roleDialogVisible.value = false
      } catch (error) {
        ElMessage.error('角色分配失败')
        console.error('Error assigning roles:', error)
      } finally {
        roleLoading.value = false
      }
    }

    // 删除权限
    const deletePermission = async (permission) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除权限 "${permission.name}" 吗？此操作不可恢复！`,
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        ElMessage.success('删除成功')
        loadPermissions()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
          console.error('Error deleting permission:', error)
        }
      }
    }

    // 分页
    const handleSizeChange = (size) => {
      pagination.pageSize = size
      pagination.page = 1
      loadPermissions()
    }

    const handleCurrentChange = (page) => {
      pagination.page = page
      loadPermissions()
    }

    // 表格选择变化
    const handleSelectionChange = (selection) => {
      multipleSelection.value = selection
    }

    // 批量删除
    const handleBatchDelete = async () => {
      try {
        await ElMessageBox.confirm(
          `确定要删除选中的 ${multipleSelection.value.length} 个权限吗？此操作不可恢复！`,
          '确认批量删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        ElMessage.success('批量删除成功')
        loadPermissions()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('批量删除失败')
        }
      }
    }

    // 统一处理对话框操作
    const handleAction = (command, permission) => {
      switch (command) {
        case 'addChild':
          showCreateDialog()
          break
        case 'roles':
          showRoleDialog(permission)
          break
        case 'copy':
          // 实现复制权限逻辑
          ElMessage.info('复制权限功能正在开发中...')
          break
        case 'toggleStatus':
          toggleStatus(permission)
          break
        case 'delete':
          deletePermission(permission)
          break
      }
    }

    // 切换权限状态
    const toggleStatus = async (permission) => {
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 300))
        
        permission.status = permission.status === '1' ? '0' : '1'
        ElMessage.success('状态更新成功')
        loadPermissions()
      } catch (error) {
        ElMessage.error('切换状态失败')
        console.error('Error toggling status:', error)
      }
    }

    // 表单提交
    const submitLoading = ref(false)
    const formRef = ref()
    
    const handleSubmit = async () => {
      if (!formRef.value) return
      
      try {
        await formRef.value.validate()
        submitLoading.value = true
        
        if (dialogMode.value === 'create') {
          await handleCreate()
        } else if (dialogMode.value === 'edit') {
          await handleUpdate()
        }
      } catch (error) {
        console.error('Form validation failed:', error)
      } finally {
        submitLoading.value = false
      }
    }

    onMounted(() => {
      loadPermissions()
    })

    return {
      loading,
      createLoading,
      editLoading,
      roleLoading,
      permissions,
      selectedPermission,
      multipleSelection,
      parentPermissions,
      availableRoles,
      selectedRoles,
      stats,
      searchForm,
      pagination,
      createDialogVisible,
      detailDialogVisible,
      editDialogVisible,
      roleDialogVisible,
      dialogVisible,
      dialogMode,
      currentPermission,
      formRules,
      loadPermissions,
      getTypeText,
      getTypeColor,
      handleSearch,
      handleReset,
      showCreateDialog,
      handleCreate,
      showDetailDialog,
      showEditDialog,
      handleUpdate,
      showRoleDialog,
      handleRoleAssign,
      deletePermission,
      handleSizeChange,
      handleCurrentChange,
      handleSelectionChange,
      handleBatchDelete,
      handleAction,
      handleSubmit,
      submitLoading,
      formRef
    }
  }
}
</script>

<style scoped>
.permission-management {
  padding: 20px;
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
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.stat-icon.active {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-icon.warning {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-icon.info {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.stat-info {
  flex: 1;
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
  margin: 0;
}

.stat-value.total {
  color: #4facfe;
}

.stat-value.active {
  color: #43e97b;
}

.stat-value.warning {
  color: #fa709a;
}

.stat-value.info {
  color: #a8edea;
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

.table-card {
  margin-bottom: 20px;
  width: 100%;
}

.table-card :deep(.el-card__body) {
  padding: 0;
}

.permission-table {
  width: 100% !important;
  font-size: 14px;
}

.permission-info {
  display: flex;
  align-items: center;
}

.permission-avatar {
  margin-right: 12px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.permission-name {
  font-weight: 500;
  color: #303133;
}

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

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.permission-dialog {
  :deep(.el-dialog) {
    border-radius: 16px;
    overflow: visible;
    box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
    border: 1px solid rgba(255, 255, 255, 0.2);
    max-height: 85vh;
    display: flex;
    flex-direction: column;
  }
  
  :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    color: white;
    padding: 24px 32px;
    margin: 0;
    position: relative;
    overflow: hidden;
  }

  :deep(.el-dialog__header)::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(45deg, rgba(255,255,255,0.1) 0%, transparent 100%);
    pointer-events: none;
  }
  
  :deep(.el-dialog__title) {
    color: white;
    font-size: 20px;
    font-weight: 600;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  :deep(.el-dialog__headerbtn) {
    top: 24px;
    right: 32px;
    width: 32px;
    height: 32px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    transition: all 0.3s ease;
  }

  :deep(.el-dialog__headerbtn):hover {
    background: rgba(255, 255, 255, 0.2);
    transform: scale(1.1);
  }
  
  :deep(.el-dialog__headerbtn .el-dialog__close) {
    color: white;
    font-size: 18px;
    font-weight: bold;
  }
  
  :deep(.el-dialog__body) {
    padding: 32px;
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    min-height: 200px;
    flex: 1;
    overflow-y: auto;
  }

  :deep(.el-dialog__footer) {
    background: white;
    padding: 20px 32px;
    border-top: 1px solid #ebeef5;
    flex-shrink: 0;
  }
}

.permission-form-container {
  background: white;
  padding: 32px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

/* 全局样式优化 */
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

:deep(.el-button--primary) {
  background-color: #409eff;
  border-color: #409eff;
}

:deep(.el-button--primary:hover) {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

:deep(.el-tag) {
  border-radius: 4px;
  font-weight: 500;
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

:deep(.el-form-item__label) {
  color: #2c3e50;
  font-weight: 600;
  font-size: 14px;
}

:deep(.el-input) {
  border-radius: 8px;
}

:deep(.el-textarea) {
  border-radius: 8px;
}

:deep(.el-select) {
  border-radius: 8px;
}
</style> 