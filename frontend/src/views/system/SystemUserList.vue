<template>
  <div class="system-user-management">
    <!-- 统计卡片 -->
    <div class="statistics-cards" v-if="stats">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon size="24"><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">总用户数</div>
                <div class="stat-value total">{{ stats.totalUsers }}</div>
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
                <div class="stat-title">活跃用户</div>
                <div class="stat-value active">{{ stats.activeUsers }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon admin">
                <el-icon size="24"><Avatar /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">管理员</div>
                <div class="stat-value admin">{{ stats.adminUsers }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon login">
                <el-icon size="24"><Clock /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">今日登录</div>
                <div class="stat-value login">{{ stats.todayLogins }}</div>
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

        <el-form-item label="用户名">
          <el-input 
            v-model="searchForm.username" 
            placeholder="请输入用户名" 
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input 
            v-model="searchForm.fullName" 
            placeholder="请输入姓名" 
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select 
            v-model="searchForm.isActive" 
            placeholder="请选择状态" 
            clearable
            style="width: 120px"
          >
            <el-option label="启用" :value="true" />
            <el-option label="禁用" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item class="search-buttons-left">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
        <el-form-item class="add-button-right">
          <el-button type="primary" @click="showCreateDialog">
            <el-icon><Plus /></el-icon>
            添加用户
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table
        :data="users"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        border
        stripe
        style="width: 100%"
        :header-cell-style="{ backgroundColor: '#f5f7fa', color: '#303133', fontWeight: '600' }"
        :row-style="{ height: '60px' }"
        class="user-table"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" width="120">
          <template #default="scope">
            <div class="user-info">
              <el-avatar :size="32" :src="scope.row.avatar" class="user-avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="username">{{ scope.row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="fullName" label="姓名" width="100" />
        <el-table-column prop="email" label="邮箱" min-width="200" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="organizationName" label="所属机构" min-width="150" show-overflow-tooltip />
        <el-table-column prop="isAdmin" label="管理员" width="90" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.isAdmin ? 'danger' : 'info'" size="small" effect="light">
              {{ scope.row.isAdmin ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isActive" label="状态" width="90" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.isActive ? 'success' : 'warning'" size="small" effect="light">
              <el-icon style="margin-right: 4px">
                <CircleCheck v-if="scope.row.isActive" />
                <CircleClose v-else />
              </el-icon>
              {{ scope.row.isActive ? '启用' : '禁用' }}
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
                    <el-dropdown-item command="assignRole">
                      <el-icon><Setting /></el-icon>
                      分配角色
                    </el-dropdown-item>
                    <el-dropdown-item command="resetPassword">
                      <el-icon><Key /></el-icon>
                      重置密码
                    </el-dropdown-item>
                    <el-dropdown-item command="toggleStatus">
                      <el-icon><Switch /></el-icon>
                      {{ scope.row.isActive ? '禁用账号' : '启用账号' }}
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

    <!-- 创建用户对话框 -->
    <el-dialog 
      v-model="createDialogVisible" 
      title="添加用户" 
      width="700px" 
      class="user-dialog"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form ref="createFormRef" :model="createForm" label-width="120px" :rules="createRules">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="createForm.username" placeholder="请输入用户名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input v-model="createForm.password" type="password" placeholder="请输入密码" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="fullName">
              <el-input v-model="createForm.fullName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="createForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="createForm.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属机构">
              <el-select v-model="createForm.organizationId" placeholder="请选择机构" style="width: 100%">
                <el-option label="系统管理" value="1" />
                <el-option label="阳光养老院" value="2" />
                <el-option label="康乐养老中心" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="管理员权限">
              <el-switch 
                v-model="createForm.isAdmin" 
                active-text="是" 
                inactive-text="否"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账号状态">
              <el-switch 
                v-model="createForm.isActive" 
                active-text="启用" 
                inactive-text="禁用"
              />
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

    <!-- 用户详情对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      title="用户详情" 
      width="600px" 
      class="user-dialog"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form label-width="120px" :disabled="true">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户ID">
              <el-input :value="selectedUser.id" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户名">
              <el-input v-model="selectedUser.username" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名">
              <el-input v-model="selectedUser.fullName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input :value="selectedUser.email || '未设置'" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input :value="selectedUser.phone || '未设置'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属机构">
              <el-input :value="selectedUser.organizationName || '未分配'" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="管理员权限">
              <el-tag :type="selectedUser.isAdmin ? 'danger' : 'info'" size="small">
                {{ selectedUser.isAdmin ? '是' : '否' }}
              </el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账号状态">
              <el-tag :type="selectedUser.isActive ? 'success' : 'warning'" size="small">
                {{ selectedUser.isActive ? '启用' : '禁用' }}
              </el-tag>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="创建时间">
              <el-input v-model="selectedUser.createTime" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="最后登录">
              <el-input :value="selectedUser.lastLoginTime || '从未登录'" disabled />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 编辑用户对话框 -->
    <el-dialog 
      v-model="editDialogVisible" 
      title="编辑用户" 
      width="700px" 
      class="user-dialog"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form ref="editFormRef" :model="editForm" label-width="120px" :rules="editRules">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="editForm.username" placeholder="请输入用户名" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="新密码">
              <el-input v-model="editForm.password" type="password" placeholder="留空则不修改密码" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="fullName">
              <el-input v-model="editForm.fullName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="editForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="editForm.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属机构">
              <el-select v-model="editForm.organizationId" placeholder="请选择机构" style="width: 100%">
                <el-option label="系统管理" value="1" />
                <el-option label="阳光养老院" value="2" />
                <el-option label="康乐养老中心" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="管理员权限">
              <el-switch 
                v-model="editForm.isAdmin" 
                active-text="是" 
                inactive-text="否"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账号状态">
              <el-switch 
                v-model="editForm.isActive" 
                active-text="启用" 
                inactive-text="禁用"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleUpdate" :loading="editLoading">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog 
      v-model="roleAssignDialogVisible" 
      title="分配角色" 
      width="600px" 
      class="user-dialog"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div v-if="selectedUser" class="role-assign-content">
        <div class="user-info-header">
          <el-avatar :size="40">
            <el-icon><User /></el-icon>
          </el-avatar>
          <div class="user-info-text">
            <div class="username">{{ selectedUser.username }}</div>
            <div class="fullname">{{ selectedUser.fullName }}</div>
          </div>
        </div>
        
        <el-divider />
        
        <div class="role-section">
          <h4>可用角色</h4>
          <el-checkbox-group v-model="selectedRoles" class="role-list">
            <div v-for="role in availableRoles" :key="role.id" class="role-item-container">
              <el-checkbox :value="role.id" class="role-checkbox-simple">
                <div class="role-content">
                  <div class="role-name">{{ role.name }}</div>
                  <div class="role-description">{{ role.description }}</div>
                </div>
              </el-checkbox>
            </div>
          </el-checkbox-group>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="roleAssignDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleRoleAssign" :loading="roleAssignLoading">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, User, UserFilled, Avatar, Clock, View, Edit, Setting, Delete,
  CircleCheck, CircleClose, ArrowDown, Key, Switch
} from '@element-plus/icons-vue'

export default {
  name: 'SystemUserList',
  components: {
    Plus, User, UserFilled, Avatar, Clock, View, Edit, Setting, Delete,
    CircleCheck, CircleClose, ArrowDown, Key, Switch
  },
  setup() {
    // 响应式数据
    const loading = ref(false)
    const createLoading = ref(false)
    const editLoading = ref(false)
    const roleAssignLoading = ref(false)
    const users = ref([])
    const selectedUser = ref(null)
    const multipleSelection = ref([])
    const stats = ref({
      totalUsers: 0,
      activeUsers: 0,
      adminUsers: 0,
      todayLogins: 0
    })

    // 搜索表单
    const searchForm = reactive({
      username: '',
      fullName: '',
      isActive: null
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
    const roleAssignDialogVisible = ref(false)

    // 创建表单
    const createForm = reactive({
      username: '',
      password: '',
      fullName: '',
      email: '',
      phone: '',
      organizationId: '',
      isAdmin: false,
      isActive: true
    })

    // 编辑表单
    const editForm = reactive({
      id: '',
      username: '',
      password: '',
      fullName: '',
      email: '',
      phone: '',
      organizationId: '',
      isAdmin: false,
      isActive: true
    })

    // 角色相关
    const availableRoles = ref([
      { id: 1, name: '系统管理员', description: '拥有所有系统权限' },
      { id: 2, name: '机构管理员', description: '管理机构相关业务' },
      { id: 3, name: '普通用户', description: '基础查看权限' },
      { id: 4, name: '数据分析师', description: '数据查看和分析权限' }
    ])
    const selectedRoles = ref([])

    // 表单验证规则
    const createRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
      ],
      fullName: [
        { required: true, message: '请输入姓名', trigger: 'blur' }
      ],
      email: [
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
      ],
      phone: [
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
      ]
    }

    const editRules = {
      fullName: [
        { required: true, message: '请输入姓名', trigger: 'blur' }
      ],
      email: [
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
      ],
      phone: [
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
      ]
    }

    // 加载用户列表
    const loadUsers = async () => {
      loading.value = true
      try {
        const queryParams = new URLSearchParams({
          page: pagination.page,
          size: pagination.pageSize,
          ...searchForm
        })
        
        const response = await fetch(`/api/system-users?${queryParams}`)
        const data = await response.json()
        
        users.value = data.list || []
        pagination.total = data.total || 0
        
        // 更新统计信息
        stats.value = {
          totalUsers: data.total || 0,
          activeUsers: data.list?.filter(u => u.isActive).length || 0,
          adminUsers: data.list?.filter(u => u.isAdmin).length || 0,
          todayLogins: Math.floor(Math.random() * 20) // 模拟数据
        }
      } catch (error) {
        ElMessage.error('加载用户列表失败')
        console.error('Error loading users:', error)
      } finally {
        loading.value = false
      }
    }

    // 搜索
    const handleSearch = () => {
      pagination.page = 1
      loadUsers()
    }

    // 重置
    const handleReset = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = key === 'isActive' ? null : ''
      })
      pagination.page = 1
      loadUsers()
    }

    // 显示创建对话框
    const showCreateDialog = () => {
      Object.keys(createForm).forEach(key => {
        if (key === 'isActive') {
          createForm[key] = true
        } else if (key === 'isAdmin') {
          createForm[key] = false
        } else {
          createForm[key] = ''
        }
      })
      createDialogVisible.value = true
    }

    // 创建用户
    const handleCreate = async () => {
      createLoading.value = true
      try {
        const response = await fetch('/api/system-users', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(createForm)
        })
        
        if (response.ok) {
          ElMessage.success('创建成功')
          createDialogVisible.value = false
          loadUsers()
        } else {
          const errorData = await response.json()
          ElMessage.error(errorData.message || '创建失败')
        }
      } catch (error) {
        ElMessage.error('创建失败')
        console.error('Error creating user:', error)
      } finally {
        createLoading.value = false
      }
    }

    // 显示详情对话框
    const showDetailDialog = (user) => {
      selectedUser.value = { ...user }
      detailDialogVisible.value = true
    }

    // 显示编辑对话框
    const showEditDialog = (user) => {
      selectedUser.value = user
      Object.keys(editForm).forEach(key => {
        editForm[key] = user[key] || ''
      })
      editForm.password = '' // 清空密码字段
      editDialogVisible.value = true
    }

    // 更新用户
    const handleUpdate = async () => {
      editLoading.value = true
      try {
        const updateData = { ...editForm }
        if (!updateData.password) {
          delete updateData.password // 如果密码为空，则不更新密码
        }

        const response = await fetch(`/api/system-users/${editForm.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(updateData)
        })
        
        if (response.ok) {
          ElMessage.success('更新成功')
          editDialogVisible.value = false
          loadUsers()
        } else {
          const errorData = await response.json()
          ElMessage.error(errorData.message || '更新失败')
        }
      } catch (error) {
        ElMessage.error('更新失败')
        console.error('Error updating user:', error)
      } finally {
        editLoading.value = false
      }
    }

    // 显示角色分配对话框
    const showRoleAssignDialog = async (user) => {
      selectedUser.value = user
      // 加载用户当前角色
      try {
        const response = await fetch(`/api/system-users/${user.id}/roles`)
        if (response.ok) {
          const data = await response.json()
          selectedRoles.value = data.map(role => role.id)
        } else {
          // 如果API不存在或出错，使用默认值
          console.warn('角色API暂未实现，使用默认角色')
          selectedRoles.value = user.isAdmin ? [1] : [3] // 管理员默认系统管理员角色，普通用户默认普通用户角色
        }
      } catch (error) {
        console.error('Error loading user roles:', error)
        selectedRoles.value = user.isAdmin ? [1] : [3] // 管理员默认系统管理员角色，普通用户默认普通用户角色
      }
      roleAssignDialogVisible.value = true
    }

    // 分配角色
    const handleRoleAssign = async () => {
      roleAssignLoading.value = true
      try {
        const response = await fetch(`/api/system-users/${selectedUser.value.id}/roles`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ roleIds: selectedRoles.value })
        })
        
        if (response.ok) {
          ElMessage.success('角色分配成功')
          roleAssignDialogVisible.value = false
          loadUsers()
        } else {
          // 如果API不存在，暂时给出提示
          console.warn('角色分配API暂未实现')
          ElMessage.warning('角色分配功能正在开发中，请稍后再试')
          roleAssignDialogVisible.value = false
        }
      } catch (error) {
        console.error('Error assigning roles:', error)
        ElMessage.warning('角色分配功能正在开发中，请稍后再试')
        roleAssignDialogVisible.value = false
      } finally {
        roleAssignLoading.value = false
      }
    }

    // 删除用户
    const deleteUser = async (user) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除用户 "${user.username}" 吗？此操作不可恢复！`,
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
            dangerouslyUseHTMLString: true
          }
        )
        
        const response = await fetch(`/api/system-users/${user.id}`, {
          method: 'DELETE'
        })
        
        if (response.ok) {
          ElMessage.success('删除成功')
          loadUsers()
        } else {
          const errorData = await response.json()
          ElMessage.error(errorData.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
          console.error('Error deleting user:', error)
        }
      }
    }

    // 分页
    const handleSizeChange = (size) => {
      pagination.pageSize = size
      pagination.page = 1
      loadUsers()
    }

    const handleCurrentChange = (page) => {
      pagination.page = page
      loadUsers()
    }

    // 表格选择变化
    const handleSelectionChange = (selection) => {
      multipleSelection.value = selection
    }

    // 批量删除
    const handleBatchDelete = async () => {
      try {
        await ElMessageBox.confirm(
          `确定要删除选中的 ${multipleSelection.value.length} 个用户吗？此操作不可恢复！`,
          '确认批量删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        for (const user of multipleSelection.value) {
          await fetch(`/api/system-users/${user.id}`, {
            method: 'DELETE'
          })
        }
        
        ElMessage.success('批量删除成功')
        loadUsers()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('批量删除失败')
        }
      }
    }

    // 下拉菜单操作
    const handleAction = async (command, user) => {
      switch (command) {
        case 'assignRole':
          showRoleAssignDialog(user)
          break
        case 'resetPassword':
          await handleResetPassword(user)
          break
        case 'toggleStatus':
          await handleToggleStatus(user)
          break
        case 'delete':
          await deleteUser(user)
          break
      }
    }

    // 重置密码
    const handleResetPassword = async (user) => {
      try {
        await ElMessageBox.confirm(
          `确定要重置用户 "${user.username}" 的密码吗？`,
          '确认重置密码',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        const response = await fetch(`/api/system-users/${user.id}/reset-password`, {
          method: 'POST'
        })
        
        if (response.ok) {
          ElMessage.success('密码重置成功')
        } else {
          ElMessage.error('密码重置失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('密码重置失败')
        }
      }
    }

    // 切换状态
    const handleToggleStatus = async (user) => {
      try {
        const action = user.isActive ? '禁用' : '启用'
        await ElMessageBox.confirm(
          `确定要${action}用户 "${user.username}" 吗？`,
          `确认${action}`,
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        const response = await fetch(`/api/system-users/${user.id}/toggle-status`, {
          method: 'PUT'
        })
        
        if (response.ok) {
          ElMessage.success(`${action}成功`)
          loadUsers()
        } else {
          ElMessage.error(`${action}失败`)
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('操作失败')
        }
      }
    }

    onMounted(() => {
      loadUsers()
    })

    return {
      loading,
      createLoading,
      editLoading,
      roleAssignLoading,
      users,
      selectedUser,
      multipleSelection,
      stats,
      searchForm,
      pagination,
      createDialogVisible,
      detailDialogVisible,
      editDialogVisible,
      roleAssignDialogVisible,
      createForm,
      editForm,
      availableRoles,
      selectedRoles,
      createRules,
      editRules,
      loadUsers,
      handleSearch,
      handleReset,
      showCreateDialog,
      handleCreate,
      showDetailDialog,
      showEditDialog,
      handleUpdate,
      showRoleAssignDialog,
      handleRoleAssign,
      deleteUser,
      handleSizeChange,
      handleCurrentChange,
      handleSelectionChange,
      handleBatchDelete,
      handleAction,
      handleResetPassword,
      handleToggleStatus
    }
  }
}
</script>

<style scoped>
.system-user-management {
  padding: 20px;
}

.header-section {
  margin-bottom: 30px;
}

.header-section h2 {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 8px 0;
}

.header-section .description {
  font-size: 14px;
  color: #909399;
  margin: 0;
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

.stat-icon.admin {
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
}

.stat-icon.login {
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

.stat-value.admin {
  color: #e6a23c;
}

.stat-value.login {
  color: #f56c6c;
}

.table-card {
  margin-bottom: 20px;
  width: 100%;
}

.table-card :deep(.el-card__body) {
  padding: 0;
}

.user-table {
  width: 100% !important;
  font-size: 14px;
}

.user-table :deep(.el-table__inner-wrapper) {
  width: 100% !important;
}

.user-table :deep(.el-table__body-wrapper) {
  width: 100% !important;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-avatar {
  margin-right: 12px;
}

.username {
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

.user-dialog {
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
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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

  :deep(.el-form) {
    background: white;
    padding: 32px;
    border-radius: 16px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.8);
  }

  :deep(.el-form-item__label) {
    color: #2c3e50;
    font-weight: 600;
    font-size: 14px;
  }

  :deep(.el-input) {
    border-radius: 12px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 12px;
    border: 2px solid #e1e8ed;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }

  :deep(.el-input__wrapper:hover) {
    border-color: #409eff;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
  }

  :deep(.el-input__wrapper.is-focus) {
    border-color: #409eff;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.25);
  }

  :deep(.el-select) {
    border-radius: 12px;
  }

  :deep(.el-switch) {
    --el-switch-on-color: #67c23a;
    --el-switch-off-color: #dcdfe6;
  }

  :deep(.el-switch__core) {
    border-radius: 20px;
    height: 24px;
    width: 48px;
  }
}

.role-assign-content {
  background: white;
  padding: 0;
  border-radius: 0;
  box-shadow: none;
  border: none;
}

.user-info-header {
  display: flex;
  align-items: center;
  margin-bottom: 48px;
  padding: 24px;
  background: linear-gradient(135deg, #667eea 10%, #764ba2 90%);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.user-info-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, rgba(255,255,255,0.1) 0%, transparent 100%);
  pointer-events: none;
}

.user-info-header .el-avatar {
  border: 3px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  background: rgba(255, 255, 255, 0.2);
  color: white;
  font-size: 18px;
  z-index: 1;
  position: relative;
}

.user-info-text {
  margin-left: 20px;
  z-index: 1;
  position: relative;
}

.user-info-text .username {
  font-size: 20px;
  font-weight: 700;
  color: white;
  margin-bottom: 6px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.user-info-text .fullname {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.85);
  font-weight: 500;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);
}

.role-section {
  margin-top: 0;
  padding-top: 0;
  clear: both;
  position: relative;
  z-index: 1;
}

.role-section h4 {
  margin: 0 0 32px 0;
  color: #2c3e50;
  font-weight: 700;
  font-size: 18px;
  padding: 0 0 12px 0;
  border-bottom: 3px solid #e1e8ed;
  position: relative;
}

.role-section h4::after {
  content: '';
  position: absolute;
  bottom: -3px;
  left: 0;
  width: 60px;
  height: 3px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.role-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-height: 250px;
  overflow-y: auto;
  padding-right: 8px;
}

.role-list::-webkit-scrollbar {
  width: 6px;
}

.role-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.role-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.role-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.role-item-container {
  width: 100%;
}

.role-checkbox-simple {
  width: 100%;
  margin: 0;
  padding: 0;
}

.role-checkbox-simple :deep(.el-checkbox) {
  width: 100%;
  display: flex;
  align-items: flex-start;
}

.role-checkbox-simple :deep(.el-checkbox__input) {
  margin-right: 16px;
  margin-top: 4px;
  flex-shrink: 0;
}

.role-checkbox-simple :deep(.el-checkbox__input .el-checkbox__inner) {
  width: 18px;
  height: 18px;
  border: 2px solid #dcdfe6;
  border-radius: 4px;
  background: white;
  transition: all 0.3s ease;
}

.role-checkbox-simple :deep(.el-checkbox__input:hover .el-checkbox__inner) {
  border-color: #409eff;
}

.role-checkbox-simple :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background: #409eff;
  border-color: #409eff;
}

.role-checkbox-simple :deep(.el-checkbox__label) {
  flex: 1;
  padding: 20px;
  border: 2px solid #f0f2f5;
  border-radius: 12px;
  background: white;
  transition: all 0.3s ease;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.role-checkbox-simple :deep(.el-checkbox__label):hover {
  border-color: #409eff;
  background: #f8fafe;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.1);
  transform: translateY(-1px);
}

.role-checkbox-simple :deep(.el-checkbox.is-checked .el-checkbox__label) {
  border-color: #409eff;
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f7ff 100%);
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.15);
}

.role-content {
  width: 100%;
}

.role-name {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
  line-height: 1.3;
}

.role-checkbox-simple :deep(.el-checkbox.is-checked) .role-name {
  color: #409eff;
}

.role-description {
  font-size: 14px;
  color: #6c757d;
  line-height: 1.5;
  margin: 0;
}

.role-checkbox-simple :deep(.el-checkbox.is-checked) .role-description {
  color: #409eff;
  opacity: 0.8;
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

:deep(.el-dropdown-menu__item .el-icon) {
  font-size: 14px;
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
  border-bottom: 2px solid #ebeef5;
}

:deep(.el-table td) {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.el-table .el-table__row:hover) {
  background-color: #f5f7fa;
}
</style> 