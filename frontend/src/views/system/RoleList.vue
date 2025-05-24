<template>
  <div class="role-management">
    <!-- 统计卡片 -->
    <div class="statistics-cards" v-if="stats">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon size="24"><Avatar /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">总角色数</div>
                <div class="stat-value total">{{ stats.totalRoles }}</div>
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
                <div class="stat-title">管理员角色</div>
                <div class="stat-value active">{{ stats.adminRoles }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon warning">
                <el-icon size="24"><Setting /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">自定义角色</div>
                <div class="stat-value warning">{{ stats.customRoles }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon info">
                <el-icon size="24"><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">已分配用户</div>
                <div class="stat-value info">{{ stats.assignedUsers }}</div>
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

        <el-form-item label="角色名称">
          <el-input 
            v-model="searchForm.name" 
            placeholder="请输入角色名称" 
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="角色标识">
          <el-input 
            v-model="searchForm.code" 
            placeholder="请输入角色标识" 
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
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item class="search-buttons-left">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
        <el-form-item class="add-button-right">
          <el-button type="primary" @click="showCreateDialog">
            <el-icon><Plus /></el-icon>
            添加角色
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table
        :data="roles"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        border
        stripe
        style="width: 100%"
        :header-cell-style="{ backgroundColor: '#f5f7fa', color: '#303133', fontWeight: '600' }"
        :row-style="{ height: '60px' }"
        class="role-table"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="roleName" label="角色名称" width="140">
          <template #default="scope">
            <div class="role-info">
              <el-avatar :size="32" class="role-avatar">
                <el-icon><Avatar /></el-icon>
              </el-avatar>
              <span class="role-name">{{ scope.row.roleName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="roleKey" label="角色标识" width="140" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="userCount" label="用户数" width="100" align="center">
          <template #default="scope">
            <el-tag type="info" size="small" effect="light">
              {{ scope.row.userCount || 0 }}
            </el-tag>
          </template>
        </el-table-column>
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
                    <el-dropdown-item command="permissions">
                      <el-icon><Lock /></el-icon>
                      权限配置
                    </el-dropdown-item>
                    <el-dropdown-item command="users">
                      <el-icon><User /></el-icon>
                      查看用户
                    </el-dropdown-item>
                    <el-dropdown-item command="copy">
                      <el-icon><CopyDocument /></el-icon>
                      复制角色
                    </el-dropdown-item>
                    <el-dropdown-item command="toggleStatus">
                      <el-icon><Switch /></el-icon>
                      {{ scope.row.status === '1' ? '禁用角色' : '启用角色' }}
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

    <!-- 角色详情/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogMode === 'create' ? '添加角色' : dialogMode === 'edit' ? '编辑角色' : '角色详情'" 
      width="600px" 
      class="role-dialog"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div class="role-form-container">
        <el-form
          ref="formRef"
          :model="currentRole"
          :rules="formRules"
          label-width="100px"
          :disabled="dialogMode === 'view'"
        >
          <el-form-item label="角色名称" prop="roleName">
            <el-input 
              v-model="currentRole.roleName" 
              placeholder="请输入角色名称"
              maxlength="50"
              show-word-limit
            />
          </el-form-item>
          <el-form-item label="角色标识" prop="roleKey">
            <el-input 
              v-model="currentRole.roleKey" 
              placeholder="请输入角色标识，如 ADMIN, USER"
              maxlength="50"
              show-word-limit
            />
          </el-form-item>
          <el-form-item label="角色描述" prop="description">
            <el-input 
              v-model="currentRole.description" 
              type="textarea" 
              :rows="3"
              placeholder="请输入角色描述"
              maxlength="255"
              show-word-limit
            />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="currentRole.status">
              <el-radio label="1">启用</el-radio>
              <el-radio label="0">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
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

    <!-- 权限配置对话框 -->
    <el-dialog 
      v-model="permissionDialogVisible" 
      title="配置角色权限" 
      width="800px" 
      class="permission-dialog"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div class="permission-content">
        <div class="permission-header">
          <span class="role-name">{{ selectedRole?.roleName }}</span>
          <span class="role-desc">{{ selectedRole?.description }}</span>
        </div>
        <div class="permission-tree" v-loading="permissionLoading">
          <el-tree
            ref="permissionTreeRef"
            :data="permissionTree"
            :props="{ children: 'children', label: 'name', disabled: 'disabled' }"
            show-checkbox
            node-key="id"
            :default-checked-keys="checkedPermissions"
            class="permission-tree-component"
          >
            <template #default="{ node, data }">
              <div class="tree-node">
                <el-icon v-if="data.icon" class="node-icon">
                  <component :is="data.icon" />
                </el-icon>
                <span class="node-label">{{ data.name }}</span>
                <span class="node-desc" v-if="data.description">{{ data.description }}</span>
              </div>
            </template>
          </el-tree>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSavePermissions" :loading="savePermissionLoading">
            保存权限
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
  Avatar, UserFilled, Setting, User, Plus, CircleCheck, CircleClose, 
  ArrowDown, Lock, CopyDocument, Switch, Delete
} from '@element-plus/icons-vue'

export default {
  name: 'RoleList',
  components: {
    Avatar, UserFilled, Setting, User, Plus, CircleCheck, CircleClose,
    ArrowDown, Lock, CopyDocument, Switch, Delete
  },
  setup() {
    // 响应式数据
    const loading = ref(false)
    const createLoading = ref(false)
    const editLoading = ref(false)
    const permissionLoading = ref(false)
    const roles = ref([])
    const selectedRole = ref(null)
    const multipleSelection = ref([])
    const stats = ref({
      totalRoles: 0,
      assignedUsers: 0,
      adminRoles: 0,
      customRoles: 0
    })

    // 搜索表单
    const searchForm = reactive({
      name: '',
      code: '',
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
    const permissionDialogVisible = ref(false)

    // 统一的对话框状态
    const dialogVisible = ref(false)
    const dialogMode = ref('view') // 'view', 'create', 'edit'
    const currentRole = reactive({
      roleName: '',
      roleKey: '',
      description: '',
      status: '1'
    })

    // 创建表单
    const createForm = reactive({
      roleName: '',
      roleKey: '',
      description: ''
    })

    // 编辑表单
    const editForm = reactive({
      id: '',
      roleName: '',
      roleKey: '',
      description: ''
    })

    // 权限相关
    const permissionTree = ref([])
    const selectedPermissions = ref([])
    const checkedPermissions = ref([])
    const savePermissionLoading = ref(false)
    const treeProps = {
      children: 'children',
      label: 'name'
    }
    const permissionTreeRef = ref()

    // 表单验证规则
    const formRules = {
      roleName: [
        { required: true, message: '请输入角色名称', trigger: 'blur' }
      ],
      roleKey: [
        { required: true, message: '请输入角色标识', trigger: 'blur' },
        { pattern: /^[A-Z_]+$/, message: '角色标识只能包含大写字母和下划线', trigger: 'blur' }
      ]
    }

    // 加载角色列表
    const loadRoles = async () => {
      loading.value = true
      try {
        let url = `/api/roles?page=${pagination.page}&size=${pagination.pageSize}`
        
        // 如果有搜索条件，使用搜索接口
        if (searchForm.name || searchForm.code) {
          const queryParams = new URLSearchParams({
            page: pagination.page,
            size: pagination.pageSize
          })
          
          if (searchForm.name) {
            queryParams.append('roleName', searchForm.name)
          }
          if (searchForm.code) {
            queryParams.append('roleKey', searchForm.code)
          }
          
          url = `/api/roles/search?${queryParams}`
        }
        
        const response = await fetch(url)
        const data = await response.json()
        
        if (response.ok) {
          // 处理分页数据
          if (data.list) {
            roles.value = data.list
            pagination.total = data.total
          } else {
            // 兼容直接返回数组的情况
            roles.value = Array.isArray(data) ? data : []
            pagination.total = roles.value.length
          }
          
          // 更新统计信息
          stats.value = {
            totalRoles: pagination.total,
            assignedUsers: roles.value.reduce((sum, role) => sum + (role.userCount || 0), 0),
            adminRoles: roles.value.filter(r => r.roleKey?.includes('ADMIN')).length || 0,
            customRoles: roles.value.filter(r => !r.roleKey?.includes('ADMIN')).length || 0
          }
        } else {
          ElMessage.error('加载角色列表失败')
        }
      } catch (error) {
        ElMessage.error('加载角色列表失败')
        console.error('Error loading roles:', error)
      } finally {
        loading.value = false
      }
    }

    // 获取角色标签类型
    const getRoleTagType = (code) => {
      if (code?.includes('ADMIN')) return 'danger'
      if (code?.includes('USER')) return 'info'
      if (code?.includes('ANALYST')) return 'warning'
      return 'primary'
    }

    // 搜索
    const handleSearch = () => {
      pagination.page = 1
      loadRoles()
    }

    // 重置
    const handleReset = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      pagination.page = 1
      loadRoles()
    }

    // 显示创建对话框
    const showCreateDialog = () => {
      Object.keys(currentRole).forEach(key => {
        if (key === 'status') {
          currentRole[key] = '1'
        } else {
          currentRole[key] = ''
        }
      })
      dialogMode.value = 'create'
      dialogVisible.value = true
    }

    // 创建角色
    const handleCreate = async () => {
      createLoading.value = true
      try {
        const response = await fetch('/api/roles', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(currentRole)
        })
        
        if (response.ok) {
          ElMessage.success('创建成功')
          dialogVisible.value = false
          loadRoles()
        } else {
          const errorData = await response.json()
          ElMessage.error(errorData.message || '创建失败')
        }
      } catch (error) {
        ElMessage.error('创建失败')
        console.error('Error creating role:', error)
      } finally {
        createLoading.value = false
      }
    }

    // 显示详情对话框
    const showDetailDialog = (role) => {
      selectedRole.value = { ...role }
      Object.keys(currentRole).forEach(key => {
        currentRole[key] = role[key] || (key === 'status' ? '1' : '')
      })
      dialogMode.value = 'view'
      dialogVisible.value = true
    }

    // 显示编辑对话框
    const showEditDialog = (role) => {
      selectedRole.value = role
      Object.keys(currentRole).forEach(key => {
        currentRole[key] = role[key] || (key === 'status' ? '1' : '')
      })
      dialogMode.value = 'edit'
      dialogVisible.value = true
    }

    // 更新角色
    const handleUpdate = async () => {
      editLoading.value = true
      try {
        const response = await fetch(`/api/roles/${selectedRole.value.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(currentRole)
        })
        
        if (response.ok) {
          ElMessage.success('更新成功')
          dialogVisible.value = false
          loadRoles()
        } else {
          const errorData = await response.json()
          ElMessage.error(errorData.message || '更新失败')
        }
      } catch (error) {
        ElMessage.error('更新失败')
        console.error('Error updating role:', error)
      } finally {
        editLoading.value = false
      }
    }

    // 显示权限分配对话框
    const showPermissionDialog = async (role) => {
      selectedRole.value = role
      // 模拟权限树数据
      permissionTree.value = [
        {
          id: 1,
          name: '系统管理',
          children: [
            { id: 11, name: '用户管理' },
            { id: 12, name: '角色管理' },
            { id: 13, name: '权限管理' }
          ]
        },
        {
          id: 2,
          name: '业务管理',
          children: [
            { id: 21, name: '机构管理' },
            { id: 22, name: '人员档案' },
            { id: 23, name: '服务记录' }
          ]
        }
      ]
      selectedPermissions.value = [11, 21] // 模拟已选权限
      permissionDialogVisible.value = true
    }

    // 分配权限
    const handlePermissionAssign = async () => {
      permissionLoading.value = true
      try {
        const checkedKeys = permissionTreeRef.value.getCheckedKeys()
        
        // 模拟API调用
        setTimeout(() => {
          ElMessage.success('权限分配成功')
          permissionDialogVisible.value = false
          permissionLoading.value = false
        }, 1000)
      } catch (error) {
        ElMessage.error('权限分配失败')
        permissionLoading.value = false
      }
    }

    // 删除角色
    const deleteRole = async (role) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除角色 "${role.roleName}" 吗？此操作不可恢复！`,
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        const response = await fetch(`/api/roles/${role.id}`, {
          method: 'DELETE'
        })
        
        if (response.ok) {
          ElMessage.success('删除成功')
          loadRoles()
        } else {
          const errorData = await response.json()
          ElMessage.error(errorData.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
          console.error('Error deleting role:', error)
        }
      }
    }

    // 分页
    const handleSizeChange = (size) => {
      pagination.pageSize = size
      pagination.page = 1
      loadRoles()
    }

    const handleCurrentChange = (page) => {
      pagination.page = page
      loadRoles()
    }

    // 表格选择变化
    const handleSelectionChange = (selection) => {
      multipleSelection.value = selection
    }

    // 批量删除
    const handleBatchDelete = async () => {
      try {
        await ElMessageBox.confirm(
          `确定要删除选中的 ${multipleSelection.value.length} 个角色吗？此操作不可恢复！`,
          '确认批量删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        for (const role of multipleSelection.value) {
          await fetch(`/api/roles/${role.id}`, {
            method: 'DELETE'
          })
        }
        
        ElMessage.success('批量删除成功')
        loadRoles()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('批量删除失败')
        }
      }
    }

    // 统一处理操作
    const handleAction = (command, role) => {
      switch (command) {
        case 'permissions':
          showPermissionDialog(role)
          break
        case 'copy':
          handleCopyRole(role)
          break
        case 'toggleStatus':
          handleToggleStatus(role)
          break
        case 'delete':
          deleteRole(role)
          break
      }
    }

    // 复制角色
    const handleCopyRole = (role) => {
      ElMessage.info('复制角色功能正在开发中...')
    }

    // 切换角色状态
    const handleToggleStatus = async (role) => {
      try {
        const response = await fetch(`/api/roles/${role.id}/status`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ status: role.status === '1' ? '0' : '1' })
        })
        
        if (response.ok) {
          role.status = role.status === '1' ? '0' : '1'
          ElMessage.success('状态更新成功')
        } else {
          ElMessage.error('状态更新失败')
        }
      } catch (error) {
        ElMessage.error('状态更新失败')
        console.error('Error toggling role status:', error)
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

    // 保存权限配置
    const handleSavePermissions = async () => {
      savePermissionLoading.value = true
      try {
        const checkedKeys = permissionTreeRef.value.getCheckedKeys()
        
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        ElMessage.success('权限配置保存成功')
        permissionDialogVisible.value = false
      } catch (error) {
        ElMessage.error('权限配置保存失败')
        console.error('Error saving permissions:', error)
      } finally {
        savePermissionLoading.value = false
      }
    }

    onMounted(() => {
      loadRoles()
    })

    return {
      loading,
      createLoading,
      editLoading,
      permissionLoading,
      roles,
      selectedRole,
      multipleSelection,
      stats,
      searchForm,
      pagination,
      createDialogVisible,
      detailDialogVisible,
      editDialogVisible,
      permissionDialogVisible,
      dialogVisible,
      dialogMode,
      currentRole,
      createForm,
      editForm,
      permissionTree,
      selectedPermissions,
      checkedPermissions,
      savePermissionLoading,
      treeProps,
      permissionTreeRef,
      formRules,
      loadRoles,
      getRoleTagType,
      handleSearch,
      handleReset,
      showCreateDialog,
      handleCreate,
      showDetailDialog,
      showEditDialog,
      handleUpdate,
      showPermissionDialog,
      handlePermissionAssign,
      deleteRole,
      handleSizeChange,
      handleCurrentChange,
      handleSelectionChange,
      handleBatchDelete,
      handleAction,
      submitLoading,
      formRef,
      handleSubmit,
      handleSavePermissions
    }
  }
}
</script>

<style scoped>
.role-management {
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
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: white;
}

.stat-icon.active {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.stat-icon.warning {
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
}

.stat-icon.info {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
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
  color: #409eff;
}

.stat-value.active {
  color: #67c23a;
}

.stat-value.warning {
  color: #e6a23c;
}

.stat-value.info {
  color: #f56c6c;
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

.role-table {
  width: 100% !important;
  font-size: 14px;
}

.role-info {
  display: flex;
  align-items: center;
}

.role-avatar {
  margin-right: 12px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.role-name {
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

.role-dialog {
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
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
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

.role-form-container {
  background: white;
  padding: 32px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.permission-dialog {
  :deep(.el-dialog) {
    border-radius: 16px;
    overflow: visible;
    box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  }
  
  :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    color: white;
    padding: 24px 32px;
  }
}

.permission-content {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.permission-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f0f2f5;
}

.permission-header .role-name {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin-right: 16px;
}

.permission-header .role-desc {
  font-size: 14px;
  color: #666;
}

.permission-tree-component {
  max-height: 400px;
  overflow-y: auto;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  padding: 4px 0;
}

.node-icon {
  color: #409eff;
}

.node-label {
  font-weight: 500;
  color: #2c3e50;
}

.node-desc {
  font-size: 12px;
  color: #999;
  margin-left: auto;
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
</style> 