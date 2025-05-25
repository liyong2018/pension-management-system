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
                <div class="stat-title">目录权限</div>
                <div class="stat-value info">{{ stats.catalogPermissions }}</div>
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
          <el-button type="warning" @click="expandAll">
            <el-icon><Expand /></el-icon>
            展开全部
          </el-button>
          <el-button type="info" @click="collapseAll">
            <el-icon><Fold /></el-icon>
            收起全部
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
            <el-option label="目录" value="CATALOG" />
            <el-option label="菜单" value="MENU" />
            <el-option label="按钮" value="BUTTON" />
          </el-select>
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
            添加权限
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table
        ref="tableRef"
        :data="permissionTree"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        border
        stripe
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="false"
        style="width: 100%"
        :header-cell-style="{ backgroundColor: '#f5f7fa', color: '#303133', fontWeight: '600' }"
        :row-style="{ height: '60px' }"
        class="permission-table"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="权限名称" min-width="200">
          <template #default="scope">
            <div class="permission-info">
              <el-icon v-if="scope.row.icon" :size="20" class="permission-icon">
                <component :is="getIconComponent(scope.row.icon)" />
              </el-icon>
              <el-icon v-else :size="20" class="permission-icon default-icon">
                <Lock />
              </el-icon>
              <span class="permission-name">{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="permissionKey" label="权限标识" width="180" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getTypeColor(scope.row.type)" size="small" effect="light">
              <el-icon style="margin-right: 4px">
                <Folder v-if="scope.row.type === 'CATALOG'" />
                <Document v-else-if="scope.row.type === 'MENU'" />
                <Operation v-else />
              </el-icon>
              {{ getTypeText(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="routePath" label="路由路径" width="180" show-overflow-tooltip />
        <el-table-column prop="componentPath" label="组件路径" width="180" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column prop="isVisible" label="显示" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.isVisible ? 'success' : 'info'" size="small" effect="light">
              <el-icon style="margin-right: 4px">
                <View v-if="scope.row.isVisible" />
                <Hide v-else />
              </el-icon>
              {{ scope.row.isVisible ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status ? 'success' : 'warning'" size="small" effect="light">
              <el-icon style="margin-right: 4px">
                <CircleCheck v-if="scope.row.status" />
                <CircleClose v-else />
              </el-icon>
              {{ scope.row.status ? '启用' : '禁用' }}
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
                    <el-dropdown-item command="addChild" v-if="scope.row.type !== 'BUTTON'">
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
                    <el-dropdown-item command="toggleVisible">
                      <el-icon><View /></el-icon>
                      {{ scope.row.isVisible ? '隐藏权限' : '显示权限' }}
                    </el-dropdown-item>
                    <el-dropdown-item command="toggleStatus">
                      <el-icon><Switch /></el-icon>
                      {{ scope.row.status ? '禁用权限' : '启用权限' }}
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
      width="800px" 
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
              <el-form-item label="权限类型" prop="type">
                <el-select v-model="currentPermission.type" placeholder="请选择权限类型" style="width: 100%">
                  <el-option label="目录" value="CATALOG" />
                  <el-option label="菜单" value="MENU" />
                  <el-option label="按钮" value="BUTTON" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="父级权限" prop="parentId">
                <el-select v-model="currentPermission.parentId" placeholder="请选择父级权限" style="width: 100%" clearable>
                  <el-option label="顶级权限" :value="null" />
                  <el-option 
                    v-for="item in parentPermissions" 
                    :key="item.id" 
                    :label="item.name" 
                    :value="item.id" 
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="权限图标" prop="icon">
                <el-input 
                  v-model="currentPermission.icon" 
                  placeholder="请输入图标名称"
                  maxlength="50"
                >
                  <template #append>
                    <el-button @click="showIconSelector">选择</el-button>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20" v-if="currentPermission.type !== 'BUTTON'">
            <el-col :span="12">
              <el-form-item label="路由路径" prop="routePath">
                <el-input 
                  v-model="currentPermission.routePath" 
                  placeholder="请输入路由路径"
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="组件路径" prop="componentPath" v-if="currentPermission.type === 'MENU'">
                <el-input 
                  v-model="currentPermission.componentPath" 
                  placeholder="请输入组件路径"
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20" v-if="currentPermission.type === 'BUTTON'">
            <el-col :span="12">
              <el-form-item label="权限标识" prop="permissionKey">
                <el-input 
                  v-model="currentPermission.permissionKey" 
                  placeholder="请输入权限标识"
                  maxlength="100"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="排序" prop="sortOrder">
                <el-input-number 
                  v-model="currentPermission.sortOrder" 
                  :min="0" 
                  :max="999" 
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="显示状态" prop="isVisible">
                <el-radio-group v-model="currentPermission.isVisible">
                  <el-radio :label="true">显示</el-radio>
                  <el-radio :label="false">隐藏</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="权限状态" prop="status">
                <el-radio-group v-model="currentPermission.status">
                  <el-radio :label="true">启用</el-radio>
                  <el-radio :label="false">禁用</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="备注说明" prop="remark">
            <el-input 
              v-model="currentPermission.remark" 
              type="textarea" 
              :rows="3"
              placeholder="请输入备注说明"
              maxlength="255"
              show-word-limit
            />
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

    <!-- 查看角色对话框 -->
    <el-dialog 
      v-model="roleDialogVisible" 
      title="查看拥有该权限的角色" 
      width="800px" 
      class="role-dialog"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div class="role-content">
        <div class="permission-header" v-if="selectedPermission">
          <div class="permission-info">
            <el-icon v-if="selectedPermission.icon" :size="24" class="permission-icon">
              <component :is="getIconComponent(selectedPermission.icon)" />
            </el-icon>
            <el-icon v-else :size="24" class="permission-icon default-icon">
              <Lock />
            </el-icon>
            <div class="permission-details">
              <div class="permission-name">{{ selectedPermission.name }}</div>
              <div class="permission-key">{{ selectedPermission.permissionKey }}</div>
              <div class="permission-desc" v-if="selectedPermission.remark">{{ selectedPermission.remark }}</div>
            </div>
          </div>
          <el-tag :type="getTypeColor(selectedPermission.type)" size="large" effect="light">
            {{ getTypeText(selectedPermission.type) }}
          </el-tag>
        </div>
        
        <el-divider />
        
        <div class="roles-section">
          <div class="section-header">
            <h4>拥有该权限的角色</h4>
            <el-tag type="info" size="small" class="role-count-tag">
              共 {{ availableRoles.filter(role => selectedRoles.includes(role.id)).length }} 个角色
            </el-tag>
          </div>
          
          <div class="roles-list" v-loading="roleLoading">
            <el-empty v-if="availableRoles.filter(role => selectedRoles.includes(role.id)).length === 0" 
                     description="暂无角色拥有该权限" />
            <div v-else class="role-cards">
              <div 
                v-for="role in availableRoles.filter(role => selectedRoles.includes(role.id))" 
                :key="role.id" 
                class="role-card"
              >
                <div class="role-avatar">
                  <el-avatar :size="40" class="role-icon">
                    <el-icon><Avatar /></el-icon>
                  </el-avatar>
                </div>
                <div class="role-info">
                  <div class="role-name">{{ role.roleName || role.name }}</div>
                  <div class="role-key">{{ role.roleKey || role.key }}</div>
                  <div class="role-description" v-if="role.description">{{ role.description }}</div>
                </div>
                <div class="role-status">
                  <el-tag :type="role.status === '1' || role.status === true ? 'success' : 'warning'" size="small">
                    {{ role.status === '1' || role.status === true ? '启用' : '禁用' }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
          
          <div class="all-roles-section" style="margin-top: 20px;">
            <div class="section-header">
              <h4>所有角色</h4>
              <el-tag type="info" size="small">
                共 {{ availableRoles.length }} 个角色
              </el-tag>
            </div>
            <div class="roles-list">
              <div class="role-cards">
                <div 
                  v-for="role in availableRoles" 
                  :key="role.id" 
                  class="role-card"
                  :class="{ 'has-permission': selectedRoles.includes(role.id) }"
                >
                  <div class="role-avatar">
                    <el-avatar :size="40" class="role-icon">
                      <el-icon><Avatar /></el-icon>
                    </el-avatar>
                  </div>
                  <div class="role-info">
                    <div class="role-name">{{ role.roleName || role.name }}</div>
                    <div class="role-key">{{ role.roleKey || role.key }}</div>
                    <div class="role-description" v-if="role.description">{{ role.description }}</div>
                  </div>
                  <div class="role-status">
                    <el-tag v-if="selectedRoles.includes(role.id)" type="success" size="small">
                      <el-icon style="margin-right: 4px"><CircleCheck /></el-icon>
                      拥有权限
                    </el-tag>
                    <el-tag v-else type="info" size="small">
                      <el-icon style="margin-right: 4px"><CircleClose /></el-icon>
                      无权限
                    </el-tag>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="roleDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="navigateToRoleManagement">
            前往角色管理
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
  ArrowDown, Link, CopyDocument, Switch, Delete, Expand, Fold, 
  Folder, Document, View, Hide, House, OfficeBuilding, User, 
  Monitor, Warning, Setting, Key, Collection
} from '@element-plus/icons-vue'

export default {
  name: 'PermissionList',
  components: {
    Lock, Menu, Operation, Avatar, Plus, CircleCheck, CircleClose,
    ArrowDown, Link, CopyDocument, Switch, Delete, Expand, Fold,
    Folder, Document, View, Hide, House, OfficeBuilding, User,
    Monitor, Warning, Setting, Key, Collection
  },
  setup() {
    // 响应式数据
    const loading = ref(false)
    const createLoading = ref(false)
    const editLoading = ref(false)
    const roleLoading = ref(false)
    const permissionTree = ref([])
    const selectedPermission = ref(null)
    const multipleSelection = ref([])
    const parentPermissions = ref([])
    const availableRoles = ref([])
    const selectedRoles = ref([])
    const tableRef = ref()
    const stats = ref({
      totalPermissions: 0,
      menuPermissions: 0,
      operationPermissions: 0,
      catalogPermissions: 0
    })

    // 搜索表单
    const searchForm = reactive({
      name: '',
      code: '',
      type: '',
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
    const roleDialogVisible = ref(false)

    // 统一的对话框状态
    const dialogVisible = ref(false)
    const dialogMode = ref('view') // 'view', 'create', 'edit'
    const currentPermission = reactive({
      name: '',
      parentId: null,
      sortOrder: 0,
      routePath: '',
      componentPath: '',
      type: 'CATALOG',
      isVisible: true,
      status: true,
      permissionKey: '',
      icon: '',
      remark: ''
    })

    // 表单验证规则
    const formRules = {
      name: [
        { required: true, message: '请输入权限名称', trigger: 'blur' }
      ],
      sortOrder: [
        { required: true, message: '请输入排序', trigger: 'blur' }
      ],
      type: [
        { required: true, message: '请选择权限类型', trigger: 'change' }
      ]
    }

    // 图标映射
    const iconMap = {
      'House': House,
      'OfficeBuilding': OfficeBuilding,
      'User': User,
      'Monitor': Monitor,
      'Warning': Warning,
      'Avatar': Avatar,
      'Setting': Setting,
      'Key': Key,
      'Collection': Collection,
      'Document': Document,
      'Menu': Menu,
      'Lock': Lock
    }

    // 获取图标组件
    const getIconComponent = (iconName) => {
      return iconMap[iconName] || Lock
    }

    // 计算统计数据
    const calculateStats = (permissions) => {
      let total = 0
      let menuPermissions = 0
      let operationPermissions = 0
      let catalogPermissions = 0

      const traverse = (permissionList) => {
        for (const permission of permissionList) {
          total++
          if (permission.type === 'MENU') {
            menuPermissions++
          } else if (permission.type === 'BUTTON') {
            operationPermissions++
          } else if (permission.type === 'CATALOG') {
            catalogPermissions++
          }
          if (permission.children && permission.children.length > 0) {
            traverse(permission.children)
          }
        }
      }

      traverse(permissions)
      
      return {
        totalPermissions: total,
        menuPermissions: menuPermissions,
        operationPermissions: operationPermissions,
        catalogPermissions: catalogPermissions
      }
    }

    // 加载权限树
    const loadPermissions = async () => {
      loading.value = true
      try {
        console.log('开始加载权限数据...')
        
        // 调用后端API获取权限树
        const response = await fetch('/api/permissions/tree')
        console.log('权限API响应状态:', response.status)
        
        if (response.ok) {
          const data = await response.json()
          console.log('权限API响应数据:', data)
          
          // 转换后端数据格式为前端需要的格式
          const convertPermissionData = (permissions) => {
            return permissions.map(permission => ({
              id: permission.id,
              name: permission.name,
              parentId: permission.parentId,
              sortOrder: permission.sortOrder || 0,
              routePath: permission.routePath || '',
              componentPath: permission.componentPath || '',
              type: permission.type,
              isVisible: permission.isVisible,
              status: permission.status,
              permissionKey: permission.permissionKey || '',
              icon: permission.icon || '',
              createTime: permission.createTime || '',
              remark: permission.remark || '',
              children: permission.children ? convertPermissionData(permission.children) : []
            }))
          }
          
          let permissionData = []
          if (Array.isArray(data)) {
            permissionData = convertPermissionData(data)
          } else {
            console.warn('权限API返回数据格式异常:', data)
            permissionData = []
          }
          
          // 应用搜索过滤
          let filteredData = JSON.parse(JSON.stringify(permissionData))
          
          if (searchForm.name || searchForm.code || searchForm.type || searchForm.status) {
            const filterPermissions = (permissions) => {
              return permissions.filter(permission => {
                let matches = true
                
                if (searchForm.name) {
                  matches = matches && permission.name.includes(searchForm.name)
                }
                
                if (searchForm.code) {
                  matches = matches && permission.permissionKey && permission.permissionKey.includes(searchForm.code)
                }
                
                if (searchForm.type) {
                  matches = matches && permission.type === searchForm.type
                }
                
                if (searchForm.status) {
                  const statusValue = searchForm.status === '1'
                  matches = matches && permission.status === statusValue
                }
                
                if (permission.children && permission.children.length > 0) {
                  permission.children = filterPermissions(permission.children)
                  // 如果子权限有匹配的，保留父权限
                  if (permission.children.length > 0) {
                    matches = true
                  }
                }
                
                return matches
              })
            }
            
            filteredData = filterPermissions(filteredData)
          }
          
          permissionTree.value = filteredData
          stats.value = calculateStats(filteredData)
          
          // 设置父级权限选项（扁平化处理）
          const flattenPermissions = (permissions, level = 0) => {
            let result = []
            for (const permission of permissions) {
              if (permission.type !== 'BUTTON') { // 排除按钮类型
                result.push({
                  id: permission.id,
                  name: '　'.repeat(level) + permission.name,
                  level: level
                })
                if (permission.children && permission.children.length > 0) {
                  result = result.concat(flattenPermissions(permission.children, level + 1))
                }
              }
            }
            return result
          }
          
          parentPermissions.value = flattenPermissions(filteredData)
          
          console.log('权限数据加载成功:', filteredData.length, '条')
          
        } else {
          console.error('权限API请求失败，状态码:', response.status)
          const errorText = await response.text()
          console.error('错误响应:', errorText)
          
          // 如果API失败，使用空数据
          permissionTree.value = []
          stats.value = {
            totalPermissions: 0,
            menuPermissions: 0,
            operationPermissions: 0,
            catalogPermissions: 0
          }
          parentPermissions.value = []
          
          ElMessage.error(`加载权限数据失败: ${response.status}`)
        }
        
      } catch (error) {
        console.error('加载权限列表异常:', error)
        ElMessage.error('加载权限列表失败: ' + error.message)
        
        // 异常情况下使用空数据
        permissionTree.value = []
        stats.value = {
          totalPermissions: 0,
          menuPermissions: 0,
          operationPermissions: 0,
          catalogPermissions: 0
        }
        parentPermissions.value = []
        
      } finally {
        loading.value = false
      }
    }

    // 获取权限类型文本
    const getTypeText = (type) => {
      const typeMap = {
        'CATALOG': '目录',
        'MENU': '菜单',
        'BUTTON': '按钮'
      }
      return typeMap[type] || type
    }

    // 获取权限类型颜色
    const getTypeColor = (type) => {
      const colorMap = {
        'CATALOG': 'primary',
        'MENU': 'success',
        'BUTTON': 'warning'
      }
      return colorMap[type] || 'info'
    }

    // 搜索
    const handleSearch = () => {
      loadPermissions()
    }

    // 重置
    const handleReset = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      loadPermissions()
    }

    // 展开所有
    const expandAll = () => {
      if (tableRef.value) {
        const allRows = getAllRows(permissionTree.value)
        allRows.forEach(row => {
          if (row.children && row.children.length > 0) {
            tableRef.value.toggleRowExpansion(row, true)
          }
        })
      }
    }

    // 收起所有
    const collapseAll = () => {
      if (tableRef.value) {
        const allRows = getAllRows(permissionTree.value)
        allRows.forEach(row => {
          if (row.children && row.children.length > 0) {
            tableRef.value.toggleRowExpansion(row, false)
          }
        })
      }
    }

    // 获取所有行数据（递归）
    const getAllRows = (data) => {
      let result = []
      for (const item of data) {
        result.push(item)
        if (item.children && item.children.length > 0) {
          result = result.concat(getAllRows(item.children))
        }
      }
      return result
    }

    // 显示创建对话框
    const showCreateDialog = (parentPermission = null) => {
      Object.keys(currentPermission).forEach(key => {
        if (key === 'parentId') {
          currentPermission[key] = parentPermission ? parentPermission.id : null
        } else if (key === 'sortOrder') {
          currentPermission[key] = 0
        } else if (key === 'isVisible') {
          currentPermission[key] = true
        } else if (key === 'status') {
          currentPermission[key] = true
        } else if (key === 'type') {
          currentPermission[key] = 'CATALOG'
        } else {
          currentPermission[key] = ''
        }
      })
      dialogMode.value = 'create'
      dialogVisible.value = true
    }

    // 创建权限
    const handleCreate = async () => {
      createLoading.value = true
      try {
        console.log('开始创建权限:', currentPermission)
        
        // 构建创建数据
        const createData = {
          name: currentPermission.name,
          parentId: currentPermission.parentId,
          type: currentPermission.type,
          permissionKey: currentPermission.permissionKey,
          routePath: currentPermission.routePath,
          componentPath: currentPermission.componentPath,
          icon: currentPermission.icon,
          sortOrder: currentPermission.sortOrder,
          isVisible: currentPermission.isVisible,
          status: currentPermission.status,
          remark: currentPermission.remark
        }
        
        console.log('创建权限数据:', createData)
        
        // 调用后端API创建权限
        const response = await fetch('/api/permissions', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(createData)
        })
        
        console.log('创建权限API响应状态:', response.status)
        
        if (response.ok) {
          const result = await response.json()
          console.log('创建权限API响应数据:', result)
          ElMessage.success('创建成功')
          dialogVisible.value = false
          loadPermissions()
          
          // 刷新顶部菜单
          if (window.refreshTopMenu) {
            window.refreshTopMenu()
          }
        } else {
          const errorText = await response.text()
          console.error('创建权限失败，状态码:', response.status)
          console.error('错误响应:', errorText)
          
          let errorMessage = '创建失败'
          try {
            const errorData = JSON.parse(errorText)
            errorMessage = errorData.message || errorData.error || errorMessage
          } catch (e) {
            errorMessage = `创建失败 (${response.status}): ${errorText}`
          }
          
          ElMessage.error(errorMessage)
        }
      } catch (error) {
        console.error('创建权限异常:', error)
        ElMessage.error('创建失败: ' + error.message)
      } finally {
        createLoading.value = false
      }
    }

    // 显示详情对话框
    const showDetailDialog = (permission) => {
      selectedPermission.value = { ...permission }
      // 设置表单数据
      Object.keys(currentPermission).forEach(key => {
        currentPermission[key] = permission[key] || (key === 'parentId' ? null : (key === 'sortOrder' ? 0 : (key === 'isVisible' ? true : '')))
      })
      dialogMode.value = 'view'
      dialogVisible.value = true
    }

    // 显示编辑对话框
    const showEditDialog = (permission) => {
      selectedPermission.value = permission
      // 设置表单数据
      Object.keys(currentPermission).forEach(key => {
        currentPermission[key] = permission[key] || (key === 'parentId' ? null : (key === 'sortOrder' ? 0 : (key === 'isVisible' ? true : '')))
      })
      dialogMode.value = 'edit'
      dialogVisible.value = true
    }

    // 更新权限
    const handleUpdate = async () => {
      editLoading.value = true
      try {
        console.log('开始更新权限:', selectedPermission.value.id, currentPermission)
        
        // 构建更新数据
        const updateData = {
          id: selectedPermission.value.id,
          name: currentPermission.name,
          parentId: currentPermission.parentId,
          type: currentPermission.type,
          permissionKey: currentPermission.permissionKey,
          routePath: currentPermission.routePath,
          componentPath: currentPermission.componentPath,
          icon: currentPermission.icon,
          sortOrder: currentPermission.sortOrder,
          isVisible: currentPermission.isVisible,
          status: currentPermission.status,
          remark: currentPermission.remark
        }
        
        console.log('更新权限数据:', updateData)
        
        // 调用后端API更新权限
        const response = await fetch(`/api/permissions/${selectedPermission.value.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(updateData)
        })
        
        console.log('更新权限API响应状态:', response.status)
        
        if (response.ok) {
          const result = await response.json()
          console.log('更新权限API响应数据:', result)
          ElMessage.success('更新成功')
          dialogVisible.value = false
          loadPermissions()
          
          // 刷新顶部菜单
          if (window.refreshTopMenu) {
            window.refreshTopMenu()
          }
        } else {
          const errorText = await response.text()
          console.error('更新权限失败，状态码:', response.status)
          console.error('错误响应:', errorText)
          
          let errorMessage = '更新失败'
          try {
            const errorData = JSON.parse(errorText)
            errorMessage = errorData.message || errorData.error || errorMessage
          } catch (e) {
            errorMessage = `更新失败 (${response.status}): ${errorText}`
          }
          
          ElMessage.error(errorMessage)
        }
      } catch (error) {
        console.error('更新权限异常:', error)
        ElMessage.error('更新失败: ' + error.message)
      } finally {
        editLoading.value = false
      }
    }

    // 显示角色分配对话框
    const showRoleDialog = async (permission) => {
      selectedPermission.value = permission
      roleLoading.value = true
      
      try {
        console.log('开始加载角色数据和权限关联信息...')
        
        // 1. 加载所有角色
        const rolesResponse = await fetch('/api/roles/all')
        console.log('角色API响应状态:', rolesResponse.status)
        
        if (rolesResponse.ok) {
          const rolesData = await rolesResponse.json()
          console.log('角色数据:', rolesData)
          
          // 处理角色数据格式
          let roleList = []
          if (Array.isArray(rolesData)) {
            roleList = rolesData
          } else if (rolesData.data && Array.isArray(rolesData.data)) {
            roleList = rolesData.data
          } else if (rolesData.list && Array.isArray(rolesData.list)) {
            roleList = rolesData.list
          }
          
          availableRoles.value = roleList.map(role => ({
            id: role.id,
            roleName: role.roleName || role.name,
            roleKey: role.roleKey || role.key,
            description: role.description || '暂无描述',
            status: role.status
          }))
          
          console.log('处理后的角色数据:', availableRoles.value)
        } else {
          console.warn('加载角色失败，使用默认角色')
          availableRoles.value = [
            { id: 1, roleName: '超级管理员', roleKey: 'SUPER_ADMIN', description: '拥有所有权限', status: '1' },
            { id: 2, roleName: '机构管理员', roleKey: 'ORG_ADMIN', description: '管理本机构相关事务', status: '1' },
            { id: 3, roleName: '普通用户', roleKey: 'USER', description: '基本查看权限', status: '1' },
            { id: 4, roleName: '机构负责人', roleKey: 'ORG_LEADER', description: '机构负责人，负责机构日常管理和运营', status: '1' }
          ]
        }
        
        // 2. 加载拥有该权限的角色
        try {
          const permissionRolesResponse = await fetch(`/api/permissions/${permission.id}/roles`)
          console.log('权限角色关联API响应状态:', permissionRolesResponse.status)
          
          if (permissionRolesResponse.ok) {
            const permissionRolesData = await permissionRolesResponse.json()
            console.log('权限角色关联数据:', permissionRolesData)
            
            // 处理权限角色关联数据
            let roleIds = []
            if (Array.isArray(permissionRolesData)) {
              // 如果返回的是角色对象数组
              roleIds = permissionRolesData.map(role => role.id).filter(id => id)
            } else if (permissionRolesData.roleIds && Array.isArray(permissionRolesData.roleIds)) {
              // 如果返回的是包含roleIds的对象
              roleIds = permissionRolesData.roleIds
            } else {
              console.warn('权限角色关联数据格式不识别:', permissionRolesData)
              roleIds = []
            }
            
            selectedRoles.value = roleIds
            console.log('拥有该权限的角色ID:', roleIds)
          } else {
            console.warn('加载权限角色关联失败，使用默认值')
            // 根据权限类型推断默认角色
            if (permission.type === 'CATALOG' || permission.permissionKey === 'system:manage') {
              selectedRoles.value = [1] // 超级管理员
            } else if (permission.type === 'MENU') {
              selectedRoles.value = [1, 2] // 超级管理员和机构管理员
            } else {
              selectedRoles.value = [1, 2, 3] // 所有角色
            }
          }
        } catch (error) {
          console.error('加载权限角色关联异常:', error)
          selectedRoles.value = [1] // 默认只有超级管理员
        }
        
      } catch (error) {
        console.error('加载角色数据异常:', error)
        ElMessage.error('加载角色数据失败: ' + error.message)
        
        // 异常情况下使用默认数据
        availableRoles.value = [
          { id: 1, roleName: '超级管理员', roleKey: 'SUPER_ADMIN', description: '拥有所有权限', status: '1' },
          { id: 2, roleName: '机构管理员', roleKey: 'ORG_ADMIN', description: '管理本机构相关事务', status: '1' },
          { id: 3, roleName: '普通用户', roleKey: 'USER', description: '基本查看权限', status: '1' }
        ]
        selectedRoles.value = [1]
      } finally {
        roleLoading.value = false
      }
      
      roleDialogVisible.value = true
    }

    // 导航到角色管理页面
    const navigateToRoleManagement = () => {
      roleDialogVisible.value = false
      // 使用Vue Router导航到角色管理页面
      // 假设角色管理页面的路由是 /system/roles
      window.location.href = '/system/roles'
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
        
        console.log('开始删除权限:', permission.id)
        
        // 调用后端API删除权限
        const response = await fetch(`/api/permissions/${permission.id}`, {
          method: 'DELETE'
        })
        
        console.log('删除权限API响应状态:', response.status)
        
        if (response.ok) {
          ElMessage.success('删除成功')
          loadPermissions()
          
          // 刷新顶部菜单
          if (window.refreshTopMenu) {
            window.refreshTopMenu()
          }
        } else {
          const errorText = await response.text()
          console.error('删除权限失败，状态码:', response.status)
          console.error('错误响应:', errorText)
          
          let errorMessage = '删除失败'
          try {
            const errorData = JSON.parse(errorText)
            errorMessage = errorData.message || errorData.error || errorMessage
          } catch (e) {
            errorMessage = `删除失败 (${response.status}): ${errorText}`
          }
          
          ElMessage.error(errorMessage)
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除权限异常:', error)
          ElMessage.error('删除失败: ' + error.message)
        }
      }
    }

    // 分页
    const handleSizeChange = (size) => {
      pagination.pageSize = size
      pagination.page = 1
      // 如果是树形结构，可能不需要重新加载
      // loadPermissions()
    }

    const handleCurrentChange = (page) => {
      pagination.page = page
      // 如果是树形结构，可能不需要重新加载
      // loadPermissions()
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
          showCreateDialog(permission)
          break
        case 'roles':
          showRoleDialog(permission)
          break
        case 'copy':
          handleCopyPermission(permission)
          break
        case 'toggleVisible':
          handleToggleVisible(permission)
          break
        case 'toggleStatus':
          handleToggleStatus(permission)
          break
        case 'delete':
          deletePermission(permission)
          break
      }
    }

    // 复制权限
    const handleCopyPermission = (permission) => {
      ElMessage.info('复制权限功能正在开发中...')
    }

    // 切换显示状态
    const handleToggleVisible = async (permission) => {
      try {
        const newVisible = !permission.isVisible
        console.log('切换权限显示状态:', permission.id, '从', permission.isVisible, '到', newVisible)
        
        // 构建更新数据
        const updateData = {
          id: permission.id,
          name: permission.name,
          parentId: permission.parentId,
          type: permission.type,
          permissionKey: permission.permissionKey,
          routePath: permission.routePath,
          componentPath: permission.componentPath,
          icon: permission.icon,
          sortOrder: permission.sortOrder,
          isVisible: newVisible,
          status: permission.status,
          remark: permission.remark
        }
        
        // 调用后端API更新权限
        const response = await fetch(`/api/permissions/${permission.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(updateData)
        })
        
        if (response.ok) {
          permission.isVisible = newVisible
          ElMessage.success(`权限已${newVisible ? '显示' : '隐藏'}`)
          
          // 刷新顶部菜单
          if (window.refreshTopMenu) {
            window.refreshTopMenu()
          }
        } else {
          const errorText = await response.text()
          console.error('更新权限显示状态失败:', response.status, errorText)
          ElMessage.error('更新显示状态失败')
        }
      } catch (error) {
        console.error('切换权限显示状态异常:', error)
        ElMessage.error('更新显示状态失败: ' + error.message)
      }
    }

    // 切换启用状态
    const handleToggleStatus = async (permission) => {
      try {
        const newStatus = !permission.status
        console.log('切换权限启用状态:', permission.id, '从', permission.status, '到', newStatus)
        
        // 构建更新数据
        const updateData = {
          id: permission.id,
          name: permission.name,
          parentId: permission.parentId,
          type: permission.type,
          permissionKey: permission.permissionKey,
          routePath: permission.routePath,
          componentPath: permission.componentPath,
          icon: permission.icon,
          sortOrder: permission.sortOrder,
          isVisible: permission.isVisible,
          status: newStatus,
          remark: permission.remark
        }
        
        // 调用后端API更新权限
        const response = await fetch(`/api/permissions/${permission.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(updateData)
        })
        
        if (response.ok) {
          permission.status = newStatus
          ElMessage.success(`权限已${newStatus ? '启用' : '禁用'}`)
          
          // 刷新顶部菜单
          if (window.refreshTopMenu) {
            window.refreshTopMenu()
          }
        } else {
          const errorText = await response.text()
          console.error('更新权限启用状态失败:', response.status, errorText)
          ElMessage.error('更新启用状态失败')
        }
      } catch (error) {
        console.error('切换权限启用状态异常:', error)
        ElMessage.error('更新启用状态失败: ' + error.message)
      }
    }

    // 显示图标选择器
    const showIconSelector = () => {
      ElMessage.info('图标选择功能正在开发中...')
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
      permissionTree,
      selectedPermission,
      multipleSelection,
      parentPermissions,
      availableRoles,
      selectedRoles,
      tableRef,
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
      getIconComponent,
      handleSearch,
      handleReset,
      expandAll,
      collapseAll,
      showCreateDialog,
      handleCreate,
      showDetailDialog,
      showEditDialog,
      handleUpdate,
      showRoleDialog,
      handleRoleAssign,
      navigateToRoleManagement,
      deletePermission,
      handleSizeChange,
      handleCurrentChange,
      handleSelectionChange,
      handleBatchDelete,
      handleAction,
      handleCopyPermission,
      handleToggleVisible,
      handleToggleStatus,
      showIconSelector,
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

/* 角色对话框样式 */
.role-dialog .role-content {
  padding: 0;
}

.role-dialog .permission-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0;
}

.role-dialog .permission-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.role-dialog .permission-icon {
  color: #409eff;
}

.role-dialog .permission-details .permission-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.role-dialog .permission-details .permission-key {
  font-size: 14px;
  color: #909399;
  margin-bottom: 4px;
}

.role-dialog .permission-details .permission-desc {
  font-size: 13px;
  color: #606266;
}

.role-dialog .section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.role-dialog .section-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.role-dialog .role-count-tag {
  margin-left: 8px;
}

.role-dialog .role-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.role-dialog .role-card {
  display: flex;
  align-items: center;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background-color: #fff;
  transition: all 0.3s ease;
}

.role-dialog .role-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.role-dialog .role-card.has-permission {
  border-color: #67c23a;
  background-color: #f0f9ff;
}

.role-dialog .role-avatar {
  margin-right: 12px;
}

.role-dialog .role-icon {
  background-color: #409eff;
}

.role-dialog .role-info {
  flex: 1;
}

.role-dialog .role-info .role-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.role-dialog .role-info .role-key {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.role-dialog .role-info .role-description {
  font-size: 12px;
  color: #606266;
  line-height: 1.4;
}

.role-dialog .role-status {
  margin-left: 12px;
}

.role-dialog .dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .role-dialog .role-cards {
    grid-template-columns: 1fr;
  }
  
  .role-dialog .permission-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .role-dialog .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style> 