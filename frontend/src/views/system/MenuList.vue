<template>
  <div class="menu-management">
    <!-- 统计卡片 -->
    <div class="statistics-cards" v-if="stats">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon size="24"><Menu /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">总菜单数</div>
                <div class="stat-value total">{{ stats.totalMenus }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon active">
                <el-icon size="24"><FolderOpened /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">顶级菜单</div>
                <div class="stat-value active">{{ stats.topLevelMenus }}</div>
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
                <div class="stat-title">功能菜单</div>
                <div class="stat-value warning">{{ stats.functionMenus }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon info">
                <el-icon size="24"><Link /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">外部链接</div>
                <div class="stat-value info">{{ stats.externalLinks }}</div>
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

        <el-form-item label="菜单名称">
          <el-input 
            v-model="searchForm.name" 
            placeholder="请输入菜单名称" 
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="菜单类型">
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
            添加菜单
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table
        ref="tableRef"
        :data="menus"
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
        class="menu-table"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="菜单名称" min-width="200">
          <template #default="scope">
            <div class="menu-info">
              <el-icon v-if="scope.row.icon" :size="20" class="menu-icon">
                <component :is="scope.row.icon" />
              </el-icon>
              <el-icon v-else :size="20" class="menu-icon default-icon">
                <Menu />
              </el-icon>
              <span class="menu-name">{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
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
        <el-table-column prop="path" label="路由路径" width="180" show-overflow-tooltip />
        <el-table-column prop="component" label="组件路径" width="180" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="visible" label="显示" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.visible === '1' ? 'success' : 'info'" size="small" effect="light">
              <el-icon style="margin-right: 4px">
                <View v-if="scope.row.visible === '1'" />
                <Hide v-else />
              </el-icon>
              {{ scope.row.visible === '1' ? '显示' : '隐藏' }}
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
                    <el-dropdown-item command="addChild" v-if="scope.row.type !== 'BUTTON'">
                      <el-icon><Plus /></el-icon>
                      添加子菜单
                    </el-dropdown-item>
                    <el-dropdown-item command="copy">
                      <el-icon><CopyDocument /></el-icon>
                      复制菜单
                    </el-dropdown-item>
                    <el-dropdown-item command="toggleVisible">
                      <el-icon><View /></el-icon>
                      {{ scope.row.visible === '1' ? '隐藏菜单' : '显示菜单' }}
                    </el-dropdown-item>
                    <el-dropdown-item command="toggleStatus">
                      <el-icon><Switch /></el-icon>
                      {{ scope.row.status === '1' ? '禁用菜单' : '启用菜单' }}
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

    <!-- 菜单详情/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogMode === 'create' ? '添加菜单' : dialogMode === 'edit' ? '编辑菜单' : '菜单详情'" 
      width="800px" 
      class="menu-dialog"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div class="menu-form-container">
        <el-form
          ref="formRef"
          :model="currentMenu"
          :rules="formRules"
          label-width="100px"
          :disabled="dialogMode === 'view'"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="菜单名称" prop="name">
                <el-input 
                  v-model="currentMenu.name" 
                  placeholder="请输入菜单名称"
                  maxlength="50"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="菜单类型" prop="type">
                <el-select v-model="currentMenu.type" placeholder="请选择菜单类型" style="width: 100%">
                  <el-option label="目录" value="CATALOG" />
                  <el-option label="菜单" value="MENU" />
                  <el-option label="按钮" value="BUTTON" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="父级菜单" prop="parentId">
                <el-select v-model="currentMenu.parentId" placeholder="请选择父级菜单" style="width: 100%" clearable>
                  <el-option label="顶级菜单" :value="null" />
                  <el-option 
                    v-for="item in parentMenus" 
                    :key="item.id" 
                    :label="item.name" 
                    :value="item.id" 
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="菜单图标" prop="icon">
                <el-input 
                  v-model="currentMenu.icon" 
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
          <el-row :gutter="20" v-if="currentMenu.type !== 'BUTTON'">
            <el-col :span="12">
              <el-form-item label="路由路径" prop="path">
                <el-input 
                  v-model="currentMenu.path" 
                  placeholder="请输入路由路径"
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="组件路径" prop="component" v-if="currentMenu.type === 'MENU'">
                <el-input 
                  v-model="currentMenu.component" 
                  placeholder="请输入组件路径"
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20" v-if="currentMenu.type === 'BUTTON'">
            <el-col :span="12">
              <el-form-item label="权限标识" prop="permission">
                <el-input 
                  v-model="currentMenu.permission" 
                  placeholder="请输入权限标识"
                  maxlength="100"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="排序" prop="sort">
                <el-input-number 
                  v-model="currentMenu.sort" 
                  :min="0" 
                  :max="999" 
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="显示状态" prop="visible">
                <el-radio-group v-model="currentMenu.visible">
                  <el-radio label="1">显示</el-radio>
                  <el-radio label="0">隐藏</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="菜单状态" prop="status">
                <el-radio-group v-model="currentMenu.status">
                  <el-radio label="1">启用</el-radio>
                  <el-radio label="0">禁用</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="备注说明" prop="remark">
            <el-input 
              v-model="currentMenu.remark" 
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
  </div>
</template>

<script>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Menu, FolderOpened, Operation, Link, Plus, CircleCheck, CircleClose, 
  ArrowDown, Expand, Fold, Folder, Document, View, Hide, CopyDocument, 
  Switch, Delete
} from '@element-plus/icons-vue'

export default {
  name: 'MenuList',
  components: {
    Menu, FolderOpened, Operation, Link, Plus, CircleCheck, CircleClose,
    ArrowDown, Expand, Fold, Folder, Document, View, Hide, CopyDocument,
    Switch, Delete
  },
  setup() {
    // 响应式数据
    const loading = ref(false)
    const createLoading = ref(false)
    const editLoading = ref(false)
    const menuTree = ref([])
    const selectedMenu = ref(null)
    const multipleSelection = ref([])
    const parentMenuOptions = ref([])
    const parentMenuForCreate = ref(null)
    const showIconSelectorDialog = ref(false)
    const menuTableRef = ref()
    const stats = ref({
      totalMenus: 0,
      topLevelMenus: 0,
      functionMenus: 0,
      externalLinks: 0
    })

    // 搜索表单
    const searchForm = reactive({
      name: '',
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

    // 统一的对话框状态
    const dialogVisible = ref(false)
    const dialogMode = ref('view') // 'view', 'create', 'edit'
    const currentMenu = reactive({
      name: '',
      parentId: null,
      sort: 0,
      path: '',
      component: '',
      type: 'CATALOG',
      visible: '1',
      status: '1',
      permission: '',
      icon: '',
      remark: ''
    })

    // 表单验证规则
    const formRules = {
      name: [
        { required: true, message: '请输入菜单名称', trigger: 'blur' }
      ],
      sort: [
        { required: true, message: '请输入排序', trigger: 'blur' }
      ],
      type: [
        { required: true, message: '请选择菜单类型', trigger: 'change' }
      ],
      path: [
        { required: true, message: '请输入路由路径', trigger: 'blur' }
      ]
    }

    // 模拟菜单数据
    const mockMenus = [
      {
        id: 1,
        name: '系统管理',
        parentId: null,
        sort: 1,
        path: '/system',
        component: null,
        type: 'CATALOG',
        visible: '1',
        status: '1',
        permission: null,
        icon: 'Setting',
        createTime: '2024-01-01 10:00:00',
        remark: '系统管理目录',
        children: [
          {
            id: 11,
            name: '用户管理',
            parentId: 1,
            sort: 1,
            path: '/system/users',
            component: 'views/system/SystemUserList',
            type: 'MENU',
            visible: '1',
            status: '1',
            permission: 'system:user:list',
            icon: 'User',
            createTime: '2024-01-01 10:00:00',
            remark: '用户管理菜单'
          },
          {
            id: 12,
            name: '角色管理',
            parentId: 1,
            sort: 2,
            path: '/system/roles',
            component: 'views/system/RoleList',
            type: 'MENU',
            visible: '1',
            status: '1',
            permission: 'system:role:list',
            icon: 'Avatar',
            createTime: '2024-01-01 10:00:00',
            remark: '角色管理菜单'
          }
        ]
      },
      {
        id: 2,
        name: '业务管理',
        parentId: null,
        sort: 2,
        path: '/business',
        component: null,
        type: 'CATALOG',
        visible: '1',
        status: '1',
        permission: null,
        icon: 'Management',
        createTime: '2024-01-01 10:00:00',
        remark: '业务管理目录',
        children: [
          {
            id: 21,
            name: '机构管理',
            parentId: 2,
            sort: 1,
            path: '/business/organizations',
            component: 'views/organization/OrganizationList',
            type: 'MENU',
            visible: '1',
            status: '1',
            permission: 'business:org:list',
            icon: 'OfficeBuilding',
            createTime: '2024-01-01 10:00:00',
            remark: '机构管理菜单'
          }
        ]
      }
    ]

    // 计算统计数据
    const calculateStats = (menus) => {
      let total = 0
      let topLevel = 0
      let functionMenus = 0
      let external = 0

      const traverse = (menuList, isTopLevel = true) => {
        for (const menu of menuList) {
          total++
          if (isTopLevel) {
            topLevel++
          } else {
            functionMenus++
          }
          if (menu.path && menu.path.startsWith('http')) {
            external++
          }
          if (menu.children && menu.children.length > 0) {
            traverse(menu.children, false)
          }
        }
      }

      traverse(menus)
      
      return {
        totalMenus: total,
        topLevelMenus: topLevel,
        functionMenus: functionMenus,
        externalLinks: external
      }
    }

    // 加载菜单树
    const loadMenuTree = async () => {
      loading.value = true
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 500))
        
        let filteredData = JSON.parse(JSON.stringify(mockMenus))
        
        // 模拟搜索
        if (searchForm.name || searchForm.type || searchForm.status) {
          const filterMenus = (menus) => {
            return menus.filter(menu => {
              let matches = true
              
              if (searchForm.name) {
                matches = matches && menu.name.includes(searchForm.name)
              }
              
              if (searchForm.type) {
                matches = matches && menu.type === searchForm.type
              }
              
              if (searchForm.status) {
                matches = matches && menu.status === searchForm.status
              }
              
              if (menu.children && menu.children.length > 0) {
                menu.children = filterMenus(menu.children)
                // 如果子菜单有匹配的，保留父菜单
                if (menu.children.length > 0) {
                  matches = true
                }
              }
              
              return matches
            })
          }
          
          filteredData = filterMenus(filteredData)
        }
        
        menuTree.value = filteredData
        stats.value = calculateStats(filteredData)
        
        // 设置父级菜单选项（扁平化处理）
        const flattenMenus = (menus, level = 0) => {
          let result = []
          for (const menu of menus) {
            if (menu.type !== 'BUTTON') { // 排除按钮类型
              result.push({
                id: menu.id,
                name: '　'.repeat(level) + menu.name,
                level: level
              })
              if (menu.children && menu.children.length > 0) {
                result = result.concat(flattenMenus(menu.children, level + 1))
              }
            }
          }
          return result
        }
        
        parentMenuOptions.value = flattenMenus(filteredData)
        
      } catch (error) {
        ElMessage.error('加载菜单列表失败')
        console.error('Error loading menus:', error)
      } finally {
        loading.value = false
      }
    }

    // 获取菜单类型文本
    const getMenuTypeText = (type) => {
      const typeMap = {
        'CATALOG': '目录',
        'MENU': '菜单',
        'BUTTON': '按钮'
      }
      return typeMap[type] || type
    }

    // 获取菜单类型颜色
    const getMenuTypeColor = (type) => {
      const colorMap = {
        'CATALOG': 'primary',
        'MENU': 'success',
        'BUTTON': 'warning'
      }
      return colorMap[type] || 'info'
    }

    // 搜索
    const handleSearch = () => {
      loadMenuTree()
    }

    // 重置
    const handleReset = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      loadMenuTree()
    }

    // 展开所有
    const expandAll = () => {
      // Element Plus的表格展开所有行的方法
      if (menuTableRef.value) {
        const allRows = getAllRows(menuTree.value)
        allRows.forEach(row => {
          if (row.children && row.children.length > 0) {
            menuTableRef.value.toggleRowExpansion(row, true)
          }
        })
      }
    }

    // 收起所有
    const collapseAll = () => {
      if (menuTableRef.value) {
        const allRows = getAllRows(menuTree.value)
        allRows.forEach(row => {
          if (row.children && row.children.length > 0) {
            menuTableRef.value.toggleRowExpansion(row, false)
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
    const showCreateDialog = (parentMenu = null) => {
      parentMenuForCreate.value = parentMenu
      Object.keys(currentMenu).forEach(key => {
        if (key === 'parentId') {
          currentMenu[key] = parentMenu ? parentMenu.id : null
        } else if (key === 'sort') {
          currentMenu[key] = 0
        } else if (key === 'visible') {
          currentMenu[key] = '1'
        } else if (key === 'status') {
          currentMenu[key] = '1'
        } else if (key === 'type') {
          currentMenu[key] = 'CATALOG'
        } else {
          currentMenu[key] = ''
        }
      })
      dialogVisible.value = true
    }

    // 创建菜单
    const handleCreate = async () => {
      createLoading.value = true
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        ElMessage.success('创建成功')
        dialogVisible.value = false
        loadMenuTree()
      } catch (error) {
        ElMessage.error('创建失败')
        console.error('Error creating menu:', error)
      } finally {
        createLoading.value = false
      }
    }

    // 显示详情对话框
    const showDetailDialog = (menu) => {
      selectedMenu.value = { ...menu }
      dialogVisible.value = true
    }

    // 显示编辑对话框
    const showEditDialog = (menu) => {
      selectedMenu.value = menu
      Object.keys(currentMenu).forEach(key => {
        currentMenu[key] = menu[key] || (key === 'parentId' ? null : (key === 'sort' ? 0 : (key === 'visible' ? '1' : '')))
      })
      dialogVisible.value = true
    }

    // 更新菜单
    const handleUpdate = async () => {
      editLoading.value = true
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        ElMessage.success('更新成功')
        dialogVisible.value = false
        loadMenuTree()
      } catch (error) {
        ElMessage.error('更新失败')
        console.error('Error updating menu:', error)
      } finally {
        editLoading.value = false
      }
    }

    // 状态变化
    const handleStatusChange = async (menu) => {
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 300))
        
        ElMessage.success('状态更新成功')
      } catch (error) {
        // 恢复原状态
        menu.visible = menu.visible === '1' ? '0' : '1'
        ElMessage.error('状态更新失败')
      }
    }

    // 删除菜单
    const deleteMenu = async (menu) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除菜单 "${menu.name}" 吗？此操作不可恢复！`,
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        ElMessage.success('删除成功')
        loadMenuTree()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
          console.error('Error deleting menu:', error)
        }
      }
    }

    // 表格选择变化
    const handleSelectionChange = (selection) => {
      multipleSelection.value = selection
    }

    // 批量删除
    const handleBatchDelete = async () => {
      try {
        await ElMessageBox.confirm(
          `确定要删除选中的 ${multipleSelection.value.length} 个菜单吗？此操作不可恢复！`,
          '确认批量删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        ElMessage.success('批量删除成功')
        loadMenuTree()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('批量删除失败')
        }
      }
    }

    // 分页处理函数
    const handleSizeChange = (size) => {
      pagination.pageSize = size
      pagination.page = 1
      // 如果是树形结构，可能不需要重新加载
      // loadMenuTree()
    }

    const handleCurrentChange = (page) => {
      pagination.page = page
      // 如果是树形结构，可能不需要重新加载
      // loadMenuTree()
    }

    // 处理操作
    const handleAction = (command, menu) => {
      switch (command) {
        case 'addChild':
          showCreateDialog(menu)
          break
        case 'copy':
          handleCopyMenu(menu)
          break
        case 'toggleVisible':
          handleToggleVisible(menu)
          break
        case 'toggleStatus':
          handleToggleStatus(menu)
          break
        case 'delete':
          deleteMenu(menu)
          break
      }
    }

    // 复制菜单
    const handleCopyMenu = (menu) => {
      ElMessage.info('复制菜单功能正在开发中...')
    }

    // 切换显示状态
    const handleToggleVisible = async (menu) => {
      try {
        menu.visible = menu.visible === '1' ? '0' : '1'
        ElMessage.success('显示状态更新成功')
      } catch (error) {
        menu.visible = menu.visible === '1' ? '0' : '1'
        ElMessage.error('显示状态更新失败')
      }
    }

    // 切换启用状态
    const handleToggleStatus = async (menu) => {
      try {
        menu.status = menu.status === '1' ? '0' : '1'
        ElMessage.success('启用状态更新成功')
      } catch (error) {
        menu.status = menu.status === '1' ? '0' : '1'
        ElMessage.error('启用状态更新失败')
      }
    }

    // 显示图标选择器
    const showIconSelector = () => {
      ElMessage.info('图标选择功能正在开发中...')
    }

    // 统一的提交处理
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

    // 获取父级菜单列表
    const parentMenus = ref([])
    const loadParentMenus = () => {
      // 从menuTree中提取可作为父级的菜单（目录和菜单类型）
      const extractParentMenus = (menus) => {
        const result = []
        for (const menu of menus) {
          if (menu.type !== 'BUTTON') {
            result.push({
              id: menu.id,
              name: menu.name,
              type: menu.type
            })
            if (menu.children) {
              result.push(...extractParentMenus(menu.children))
            }
          }
        }
        return result
      }
      parentMenus.value = extractParentMenus(menuTree.value)
    }

    onMounted(() => {
      loadMenuTree()
    })

    return {
      loading,
      createLoading,
      editLoading,
      menuTree,
      selectedMenu,
      multipleSelection,
      parentMenuOptions,
      parentMenuForCreate,
      showIconSelectorDialog,
      showIconSelector,
      menuTableRef,
      stats,
      searchForm,
      pagination,
      createDialogVisible,
      detailDialogVisible,
      editDialogVisible,
      dialogVisible,
      dialogMode,
      currentMenu,
      formRules,
      loadMenuTree,
      getMenuTypeText,
      getMenuTypeColor,
      handleSearch,
      handleReset,
      expandAll,
      collapseAll,
      showCreateDialog,
      handleCreate,
      showDetailDialog,
      showEditDialog,
      handleUpdate,
      handleStatusChange,
      deleteMenu,
      handleSelectionChange,
      handleBatchDelete,
      handleSizeChange,
      handleCurrentChange,
      handleAction,
      handleCopyMenu,
      handleToggleVisible,
      handleToggleStatus,
      submitLoading,
      formRef,
      handleSubmit,
      parentMenus,
      loadParentMenus
    }
  }
}
</script>

<style scoped>
.menu-management {
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
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
}

.stat-icon.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
  color: #43e97b;
}

.stat-value.active {
  color: #667eea;
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
  display: flex;
  gap: 8px;
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

.menu-table {
  width: 100% !important;
  font-size: 14px;
}

.menu-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.menu-icon {
  color: #43e97b;
  flex-shrink: 0;
}

.menu-icon.default-icon {
  color: #909399;
}

.menu-name {
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

.menu-dialog {
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
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
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

.menu-form-container {
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

/* 树形表格样式 */
:deep(.el-table__expand-icon) {
  color: #43e97b;
}

:deep(.el-table__expand-icon--expanded) {
  transform: rotate(90deg);
}

:deep(.el-table__indent) {
  padding-left: 18px;
}
</style> 