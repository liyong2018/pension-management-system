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
        <el-form-item label="所属机构">
          <el-select 
            v-model="searchForm.organizationId" 
            placeholder="请选择机构" 
            clearable
            filterable
            style="width: 180px"
            :loading="organizationsLoading" 
          >
            <el-option 
              v-for="org in organizations"
              :key="org.id" 
              :label="org.name" 
              :value="org.id"
            />
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
        <el-form-item class="table-operations-left">
          <el-button type="danger" :disabled="!multipleSelection.length" @click="handleBatchDelete">
            批量删除
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
        <el-table-column prop="username" label="用户名" width="200">
          <template #default="scope">
            <div class="user-info">
              <el-avatar :size="32" class="user-avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="username">{{ scope.row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="fullName" label="姓名" width="120" />
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
      width="900px" 
      class="user-dialog"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-tabs v-model="createActiveTab" class="create-user-tabs">
        <!-- 基本信息选项卡 -->
        <el-tab-pane label="基本信息" name="basic">
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
                    <el-option 
                      v-for="org in organizations" 
                      :key="org.id" 
                      :label="org.name" 
                      :value="org.id" 
                    />
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
        </el-tab-pane>

        <!-- 服务人员信息选项卡 -->
        <el-tab-pane label="服务人员信息" name="serviceStaff">
          <el-form ref="createServiceStaffFormRef" :model="createForm" label-width="120px">
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="关联服务人员">
                  <el-select 
                    v-model="createForm.serviceStaffId" 
                    placeholder="请选择服务人员（可选）" 
                    style="width: 100%"
                    clearable
                    filterable
                  >
                    <el-option
                      v-for="staff in serviceStaffList"
                      :key="staff.id"
                      :label="`${staff.fullName} (${staff.employeeId})`"
                      :value="staff.id"
                    >
                      <span style="float: left">{{ staff.fullName }}</span>
                      <span style="float: right; color: #8492a6; font-size: 13px">{{ staff.employeeId }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20" v-if="createForm.serviceStaffId">
              <el-col :span="24">
                <el-alert
                  title="提示"
                  type="info"
                  description="该用户将关联到选中的服务人员，可以在服务人员管理中查看和编辑详细信息。"
                  show-icon
                  :closable="false"
                />
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      
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
      width="800px" 
      class="user-dialog"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-tabs v-model="activeTab" class="user-detail-tabs">
        <!-- 基本信息选项卡 -->
        <el-tab-pane label="基本信息" name="basic">
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
        </el-tab-pane>

        <!-- 操作日志选项卡 -->
        <el-tab-pane label="操作日志" name="logs">
          <div class="operation-logs">
            <el-table 
              :data="userLogs" 
              v-loading="logsLoading"
              border
              stripe
              style="width: 100%"
              max-height="400"
            >
              <el-table-column prop="operationType" label="操作类型" width="120">
                <template #default="scope">
                  <el-tag :type="getLogTypeColor(scope.row.operationType)" size="small">
                    {{ scope.row.operationType }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="operationDesc" label="操作描述" min-width="200" show-overflow-tooltip />
              <el-table-column prop="module" label="模块" width="100" />
              <el-table-column prop="ipAddress" label="IP地址" width="120" />
              <el-table-column prop="createTime" label="操作时间" width="160" />
            </el-table>
            
            <div v-if="userLogs.length === 0 && !logsLoading" class="no-logs">
              <el-empty description="暂无操作日志" />
            </div>
          </div>
        </el-tab-pane>

        <!-- 服务人员信息选项卡 -->
        <el-tab-pane label="服务人员信息" name="serviceStaff" v-if="selectedUser.serviceStaffInfo">
          <el-form label-width="120px" :disabled="true">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="员工编号">
                  <el-input :value="selectedUser.serviceStaffInfo.employeeId || '未设置'" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="性别">
                  <el-tag :type="selectedUser.serviceStaffInfo.gender === 'MALE' ? 'primary' : 'danger'" size="small">
                    {{ selectedUser.serviceStaffInfo.gender === 'MALE' ? '男' : selectedUser.serviceStaffInfo.gender === 'FEMALE' ? '女' : '未设置' }}
                  </el-tag>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="出生日期">
                  <el-input :value="selectedUser.serviceStaffInfo.birthDate || '未设置'" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="身份证号">
                  <el-input :value="selectedUser.serviceStaffInfo.idCard || '未设置'" disabled />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="地址">
                  <el-input :value="selectedUser.serviceStaffInfo.address || '未设置'" disabled />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="职位">
                  <el-input :value="selectedUser.serviceStaffInfo.position || '未设置'" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="部门">
                  <el-input :value="selectedUser.serviceStaffInfo.department || '未设置'" disabled />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="工作类型">
                  <el-input :value="selectedUser.serviceStaffInfo.workType || '未设置'" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="入职日期">
                  <el-input :value="selectedUser.serviceStaffInfo.hireDate || '未设置'" disabled />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="基本工资">
                  <el-input :value="selectedUser.serviceStaffInfo.baseSalary ? `¥${selectedUser.serviceStaffInfo.baseSalary}` : '未设置'" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="工作班次">
                  <el-input :value="selectedUser.serviceStaffInfo.workShift || '未设置'" disabled />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="教育程度">
                  <el-input :value="selectedUser.serviceStaffInfo.education || '未设置'" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="专业技能">
                  <el-input :value="selectedUser.serviceStaffInfo.skills || '未设置'" disabled />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20" v-if="selectedUser.serviceStaffInfo.emergencyContact">
              <el-col :span="24">
                <el-form-item label="紧急联系人">
                  <el-input :value="selectedUser.serviceStaffInfo.emergencyContact || '未设置'" disabled />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>

        <!-- 角色权限选项卡 -->
        <el-tab-pane label="角色权限" name="roles">
          <div class="user-roles">
            <h4>当前角色</h4>
            <div class="role-tags">
              <el-tag 
                v-for="role in userRoles" 
                :key="role.id" 
                type="primary" 
                size="large"
                class="role-tag"
              >
                {{ role.roleName }}
              </el-tag>
              <el-tag v-if="userRoles.length === 0" type="info" size="large">
                暂无分配角色
              </el-tag>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
      
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
      width="900px" 
      class="user-dialog"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-tabs v-model="editActiveTab" class="edit-user-tabs">
        <!-- 基本信息选项卡 -->
        <el-tab-pane label="基本信息" name="basic">
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
                    <el-option 
                      v-for="org in organizations" 
                      :key="org.id" 
                      :label="org.name" 
                      :value="org.id" 
                    />
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
        </el-tab-pane>

        <!-- 服务人员信息选项卡 -->
        <el-tab-pane label="服务人员信息" name="serviceStaff">
          <el-form ref="serviceStaffFormRef" :model="editForm" label-width="120px">
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="关联服务人员">
                  <el-select 
                    v-model="editForm.serviceStaffId" 
                    placeholder="请选择服务人员（可选）" 
                    style="width: 100%"
                    clearable
                    filterable
                  >
                    <el-option
                      v-for="staff in serviceStaffList"
                      :key="staff.id"
                      :label="`${staff.fullName} (${staff.employeeId})`"
                      :value="staff.id"
                    >
                      <span style="float: left">{{ staff.fullName }}</span>
                      <span style="float: right; color: #8492a6; font-size: 13px">{{ staff.employeeId }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20" v-if="editForm.serviceStaffId">
              <el-col :span="24">
                <el-alert
                  title="提示"
                  type="info"
                  description="该用户将关联到选中的服务人员，可以在服务人员管理中查看和编辑详细信息。"
                  show-icon
                  :closable="false"
                />
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      
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
      width="800px" 
      class="role-assign-dialog"
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
          <el-tag type="info" size="small" class="selected-count-tag">
            已选择 {{ selectedRoles.length }} 个角色
          </el-tag>
        </div>
        
        <el-divider />
        
        <div class="role-section">
          <h4>可用角色</h4>
          <div class="role-list" v-loading="roleAssignLoading">
            <el-empty v-if="availableRoles.length === 0" description="暂无可分配角色" />
            <div v-else class="role-cards">
              <div 
                v-for="role in availableRoles" 
                :key="role.id" 
                class="role-card"
                :class="{ 'selected': selectedRoles.includes(role.id) }"
                @click="toggleRoleSelection(role.id)"
              >
                <div class="role-avatar">
                  <el-avatar :size="40" class="role-icon">
                    <el-icon><Avatar /></el-icon>
                  </el-avatar>
                </div>
                <div class="role-info">
                  <div class="role-name">{{ role.name }}</div>
                  <div class="role-key">{{ role.roleKey || role.key }}</div>
                  <div class="role-description" v-if="role.description">{{ role.description }}</div>
                </div>
                <div class="role-status">
                  <el-icon v-if="selectedRoles.includes(role.id)" class="selected-icon" color="#409eff" size="20">
                    <CircleCheck />
                  </el-icon>
                  <el-icon v-else class="unselected-icon" color="#dcdfe6" size="20">
                    <CircleClose />
                  </el-icon>
                </div>
              </div>
            </div>
          </div>
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
import organizationService from '@/services/organizationService'
import request from '@/utils/request'

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
    const logsLoading = ref(false)
    const organizationsLoading = ref(false)
    const users = ref([])
    const selectedUser = ref(null)
    const multipleSelection = ref([])
    const organizations = ref([]) // 添加机构列表
    const userLogs = ref([])
    const userRoles = ref([])
    const activeTab = ref('basic')
    const editActiveTab = ref('basic')
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
      isActive: null,
      organizationId: '' // 新增：机构ID搜索条件
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
    const createActiveTab = ref('basic')

    // 创建表单
    const createForm = reactive({
      username: '',
      password: '',
      fullName: '',
      email: '',
      phone: '',
      organizationId: '',
      isAdmin: false,
      isActive: true,
      serviceStaffId: null
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
      isActive: true,
      serviceStaffId: null
    })

    // 服务人员列表
    const serviceStaffList = ref([])
    const serviceStaffLoading = ref(false)

    // 角色相关
    const availableRoles = ref([])
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
        const queryParams = {
          page: pagination.page,
          size: pagination.pageSize
        }
        
        let hasSearchParams = false;
        // 添加搜索条件
        if (searchForm.username) {
          queryParams.username = searchForm.username
          hasSearchParams = true;
        }
        if (searchForm.fullName) {
          queryParams.fullName = searchForm.fullName
          hasSearchParams = true;
        }
        if (searchForm.isActive !== null) {
          queryParams.isActive = searchForm.isActive
          hasSearchParams = true;
        }
        if (searchForm.organizationId) {
          queryParams.organizationId = searchForm.organizationId
          hasSearchParams = true;
        }
        
        console.log('正在加载用户数据，请求参数:', queryParams)
        
        const requestUrl = hasSearchParams ? 'system-users/search' : 'system-users';

        const data = await request({
          url: requestUrl, // 使用动态URL
          method: 'get',
          params: queryParams
        })
        
        console.log('API响应数据:', data)
        
        // 处理后端返回的数据格式
        if (data && data.list && Array.isArray(data.list)) {
          // 后端返回格式: { total: 6, list: [...], pageNum: 1, pageSize: 10, ... }
          users.value = data.list
          pagination.total = data.total || 0
          
          console.log('成功加载用户数据:', users.value.length, '条')
          console.log('总数据量:', pagination.total)
          
          // 加载统计数据
          await loadUserStats()
        } else {
          console.warn('API返回数据格式异常:', data)
          users.value = []
          pagination.total = 0
          stats.value = {
            totalUsers: 0,
            activeUsers: 0,
            adminUsers: 0,
            todayLogins: 0
          }
        }
      } catch (error) {
        console.error('加载用户列表异常:', error)
        ElMessage.error('加载用户列表失败: ' + error.message)
        
        // 使用空数据
        users.value = []
        pagination.total = 0
        stats.value = {
          totalUsers: 0,
          activeUsers: 0,
          adminUsers: 0,
          todayLogins: 0
        }
      } finally {
        loading.value = false
      }
    }

    // 加载用户统计数据
    const loadUserStats = async () => {
      try {
        console.log('开始加载用户统计数据...')
        
        // 获取所有用户数据进行统计
        const data = await request({
          url: 'system-users',
          method: 'get',
          params: { page: 1, size: 1000 }
        })
        
        console.log('统计API响应数据:', data)
        
        if (data && data.list && Array.isArray(data.list)) {
          const allUsers = data.list
          const totalUsers = data.total || allUsers.length
          const activeUsers = allUsers.filter(u => u.isActive).length
          const adminUsers = allUsers.filter(u => u.isAdmin).length
          
          // 获取今日登录数据
          let todayLogins = 0
          try {
            const today = new Date().toISOString().split('T')[0]
            const logsData = await request({
              url: 'operation-logs',
              method: 'get',
              params: {
                operationType: 'LOGIN',
                date: today,
                page: 1,
                size: 1000
              }
            })
            
            todayLogins = logsData.total || (logsData.list ? logsData.list.length : 0)
          } catch (error) {
            console.warn('获取今日登录数据失败，使用估算值:', error)
            todayLogins = Math.floor(activeUsers * 0.3)
          }
          
          stats.value = {
            totalUsers,
            activeUsers,
            adminUsers,
            todayLogins
          }
          
          console.log('统计数据更新完成:', stats.value)
        } else {
          console.warn('统计API返回数据格式异常')
          stats.value = {
            totalUsers: pagination.total,
            activeUsers: users.value.filter(u => u.isActive).length,
            adminUsers: users.value.filter(u => u.isAdmin).length,
            todayLogins: 0
          }
        }
      } catch (error) {
        console.error('加载用户统计数据异常:', error)
        // 使用当前页面数据作为备选
        stats.value = {
          totalUsers: pagination.total,
          activeUsers: users.value.filter(u => u.isActive).length,
          adminUsers: users.value.filter(u => u.isAdmin).length,
          todayLogins: 0
        }
      }
    }

    // 加载机构列表
    const loadOrganizations = async () => {
      organizationsLoading.value = true // 新增：开始加载时设置loading为true
      try {
        console.log('开始加载机构数据...')
        
        const data = await request({
          url: 'organizations',
          method: 'get',
          params: {
            page: 1,
            size: 100
          }
        })
        
        console.log('机构API响应数据:', data)
        
        // 处理不同的API响应格式
        let orgList = []
        if (data.list) {
          orgList = data.list
        } else if (data.content) {
          orgList = data.content
        } else if (Array.isArray(data)) {
          orgList = data
        } else if (data.data) {
          orgList = data.data.list || data.data.content || []
        }
        
        organizations.value = orgList
        console.log('加载的机构数据:', organizations.value)
        
        if (organizations.value.length === 0) {
          console.warn('没有获取到机构数据')
          ElMessage.warning('没有获取到机构数据，请检查后端服务')
        }
      } catch (error) {
        console.error('加载机构列表异常:', error)
        ElMessage.error('加载机构数据失败: ' + error.message)
        organizations.value = []
      } finally {
        organizationsLoading.value = false // 新增：加载结束或失败时设置loading为false
      }
    }

    // 加载角色列表
    const loadRoles = async () => {
      try {
        console.log('开始加载角色数据...')
        
        const data = await request({
          url: 'roles/all',
          method: 'get'
        })
        
        console.log('角色API响应数据:', data)
        
        // 处理API响应格式
        let roleList = []
        if (Array.isArray(data)) {
          roleList = data
        } else if (data.data && Array.isArray(data.data)) {
          roleList = data.data
        } else if (data.list && Array.isArray(data.list)) {
          roleList = data.list
        }
        
        // 转换为前端需要的格式
        availableRoles.value = roleList.map(role => ({
          id: role.id,
          name: role.roleName || role.name,
          roleKey: role.roleKey || role.key,
          description: role.description || '暂无描述'
        }))
        
        console.log('加载的角色数据:', availableRoles.value)
        
        if (availableRoles.value.length === 0) {
          console.warn('没有获取到角色数据')
          ElMessage.warning('没有获取到角色数据，请检查后端服务')
        }
      } catch (error) {
        console.error('加载角色列表异常:', error)
        ElMessage.error('加载角色数据失败: ' + error.message)
        availableRoles.value = []
      }
    }

    // 加载服务人员列表
    const loadServiceStaff = async () => {
      serviceStaffLoading.value = true
      try {
        console.log('开始加载服务人员数据...')
        
        const data = await request({
          url: 'service-staff',
          method: 'get',
          params: {
            page: 1,
            size: 1000
          }
        })
        
        console.log('服务人员API响应数据:', data)
        
        // 处理不同的API响应格式
        let staffList = []
        if (data.list) {
          staffList = data.list
        } else if (data.content) {
          staffList = data.content
        } else if (Array.isArray(data)) {
          staffList = data
        } else if (data.data) {
          staffList = data.data.list || data.data.content || []
        }
        
        serviceStaffList.value = staffList
        console.log('加载的服务人员数据:', serviceStaffList.value)
        
        if (serviceStaffList.value.length === 0) {
          console.warn('没有获取到服务人员数据')
        }
      } catch (error) {
        console.error('加载服务人员列表异常:', error)
        // 不显示错误消息，因为服务人员是可选的
        serviceStaffList.value = []
      } finally {
        serviceStaffLoading.value = false
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
      searchForm.organizationId = '' // 新增：重置时清空机构ID
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
        } else if (key === 'serviceStaffId') {
          createForm[key] = null
        } else {
          createForm[key] = ''
        }
      })
      createActiveTab.value = 'basic'
      createDialogVisible.value = true
    }

    // 创建用户
    const handleCreate = async () => {
      createLoading.value = true
      try {
        const requestData = { ...createForm }

        console.log('正在创建用户:', requestData)
        
        const response = await request({
          url: 'system-users',
          method: 'post',
          data: requestData
        })
        
        console.log('创建用户响应:', response)
        ElMessage.success('创建成功')
        createDialogVisible.value = false
        loadUsers()
      } catch (error) {
        console.error('创建用户异常:', error)
        ElMessage.error('创建失败: ' + (error.message || '请稍后重试'))
      } finally {
        createLoading.value = false
      }
    }

    // 显示详情对话框
    const showDetailDialog = (user) => {
      selectedUser.value = { ...user }
      activeTab.value = 'basic'
      detailDialogVisible.value = true
      
      // 加载用户操作日志和角色信息
      loadUserLogs(user.id)
      loadUserRoles(user.id)
    }

    // 加载用户操作日志
    const loadUserLogs = async (userId) => {
      logsLoading.value = true
      try {
        console.log('正在加载用户操作日志:', userId)
        
        const data = await request({
          url: `operation-logs/user/${userId}`,
          method: 'get',
          params: {
            page: 1,
            size: 20
          }
        })
        
        console.log('用户日志数据:', data)
        
        if (data.list && Array.isArray(data.list)) {
          userLogs.value = data.list
        } else if (Array.isArray(data)) {
          userLogs.value = data
        } else {
          userLogs.value = []
          console.warn('用户日志数据格式异常')
        }
      } catch (error) {
        console.error('加载用户日志异常:', error)
        userLogs.value = []
        
        // 如果API不存在或出错，使用模拟数据
        console.warn('使用模拟用户日志数据')
        userLogs.value = [
          {
            operationType: 'LOGIN',
            operationDesc: '用户登录系统',
            module: '用户认证',
            ipAddress: '192.168.1.100',
            createTime: '2024-01-15 09:30:00'
          },
          {
            operationType: 'UPDATE',
            operationDesc: '修改个人信息',
            module: '用户管理',
            ipAddress: '192.168.1.100',
            createTime: '2024-01-15 10:15:00'
          }
        ]
      } finally {
        logsLoading.value = false
      }
    }

    // 加载用户角色信息
    const loadUserRoles = async (userId) => {
      try {
        console.log('正在加载用户角色信息:', userId)
        
        const userData = await request({
          url: `system-users/${userId}`,
          method: 'get'
        })
        
        console.log('用户详情数据:', userData)
        
        if (userData.roles && Array.isArray(userData.roles)) {
          userRoles.value = userData.roles
        } else {
          userRoles.value = []
          console.warn('用户角色数据格式异常')
        }
      } catch (error) {
        console.error('加载用户角色异常:', error)
        userRoles.value = []
      }
    }

    // 获取日志类型颜色
    const getLogTypeColor = (type) => {
      const colorMap = {
        'LOGIN': 'success',
        'LOGOUT': 'info',
        'CREATE': 'primary',
        'UPDATE': 'warning',
        'DELETE': 'danger',
        'VIEW': 'info',
        'EXPORT': 'warning'
      }
      return colorMap[type] || 'info'
    }

    // 显示编辑对话框
    const showEditDialog = (user) => {
      selectedUser.value = user
      Object.keys(editForm).forEach(key => {
        editForm[key] = user[key] || (key === 'serviceStaffId' ? null : '')
      })
      editForm.password = '' // 清空密码字段
      editActiveTab.value = 'basic' // 重置到基本信息选项卡
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

        console.log('正在更新用户数据:', updateData)

        const response = await request({
          url: `system-users/${editForm.id}`,
          method: 'put',
          data: updateData
        })
        
        console.log('更新用户API响应数据:', response)
        ElMessage.success('更新成功')
        editDialogVisible.value = false
        loadUsers()
      } catch (error) {
        console.error('更新用户异常:', error)
        ElMessage.error('更新失败: ' + error.message)
      } finally {
        editLoading.value = false
      }
    }

    // 显示角色分配对话框
    const showRoleAssignDialog = async (user) => {
      selectedUser.value = user
      console.log('为用户分配角色:', user.username)
      
      // 从用户详情API获取角色信息
      try {
        console.log('正在获取用户详情:', user.id)
        
        const userData = await request({
          url: `system-users/${user.id}`,
          method: 'get'
        })
        
        console.log('用户详情数据:', userData)
        
        // 从用户详情中提取角色信息
        if (userData.roles && Array.isArray(userData.roles)) {
          selectedRoles.value = userData.roles.map(role => role.id)
          console.log('用户当前角色ID:', selectedRoles.value)
        } else if (userData.roleIds && Array.isArray(userData.roleIds)) {
          selectedRoles.value = userData.roleIds
          console.log('用户当前角色ID:', selectedRoles.value)
        } else {
          console.warn('用户详情中没有角色信息，使用默认值')
          // 根据用户信息推断角色
          if (userData.isAdmin) {
            selectedRoles.value = [1] // 超级管理员
          } else {
            selectedRoles.value = [3] // 普通用户
          }
        }
      } catch (error) {
        console.error('获取用户详情异常:', error)
        // 根据用户信息推断角色
        if (user.isAdmin) {
          selectedRoles.value = [1] // 超级管理员
        } else if (user.roles && user.roles.length > 0) {
          selectedRoles.value = user.roles.map(role => role.id)
        } else {
          selectedRoles.value = [3] // 普通用户
        }
      }
      
      roleAssignDialogVisible.value = true
    }

    // 分配角色
    const handleRoleAssign = async () => {
      roleAssignLoading.value = true
      try {
        console.log('正在分配角色:', selectedRoles.value, '给用户:', selectedUser.value.username)
        
        if (!selectedUser.value || !selectedUser.value.id) {
          throw new Error('用户信息无效')
        }

        if (!Array.isArray(selectedRoles.value) || selectedRoles.value.length === 0) {
          throw new Error('请至少选择一个角色')
        }

        // 修改请求数据格式
        const requestData = {
          roleIds: selectedRoles.value
        }

        console.log('发送角色分配请求:', {
          url: `system-users/${selectedUser.value.id}/roles`,
          method: 'put',
          data: requestData
        })

        const response = await request({
          url: `system-users/${selectedUser.value.id}/roles`,
          method: 'put',
          data: requestData,
          headers: {
            'Content-Type': 'application/json'
          }
        })
        
        console.log('角色分配响应:', response)

        // 更健壮的响应处理
        // 如果请求没有抛出异常，就认为是成功的（因为后端返回200状态码）
        ElMessage.success('角色分配成功')
        roleAssignDialogVisible.value = false
        
        // 重新加载用户列表和角色信息
        await Promise.all([
          loadUsers(),
          detailDialogVisible.value && loadUserRoles(selectedUser.value.id)
        ])
        
        console.log('角色分配成功，已重新加载数据')
      } catch (error) {
        console.error('角色分配异常:', {
          error: error.message,
          user: selectedUser.value?.username,
          roles: selectedRoles.value,
          stack: error.stack
        })
        
        // 显示详细的错误信息
        let errorMessage = error.message || '角色分配失败，请稍后重试'
        if (error.response) {
          const responseError = error.response.data?.message || error.response.data?.msg || error.response.data?.error
          if (responseError) {
            errorMessage = `角色分配失败: ${responseError}`
          }
        }
        
        ElMessage.error(errorMessage)
      } finally {
        roleAssignLoading.value = false
      }
    }

    // 切换角色选择状态
    const toggleRoleSelection = (roleId) => {
      const index = selectedRoles.value.indexOf(roleId)
      if (index > -1) {
        // 如果已选中，则取消选择
        selectedRoles.value.splice(index, 1)
      } else {
        // 如果未选中，则添加选择
        selectedRoles.value.push(roleId)
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
        
        console.log('正在删除用户:', user.id)
        
        await request({
          url: `system-users/${user.id}`,
          method: 'delete'
        })
        
        ElMessage.success('删除成功')
        loadUsers()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除用户异常:', error)
          ElMessage.error('删除失败: ' + (error.message || '请稍后重试'))
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
          await deleteUser(user)
        }
        
        ElMessage.success('批量删除成功')
        loadUsers()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('批量删除异常:', error)
          ElMessage.error('批量删除失败: ' + (error.message || '请稍后重试'))
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
        
        console.log('正在重置用户密码:', user.id)
        
        await request({
          url: `system-users/${user.id}/reset-password`,
          method: 'post'
        })
        
        ElMessage.success('密码重置成功')
      } catch (error) {
        if (error !== 'cancel') {
          console.error('重置密码异常:', error)
          ElMessage.error('密码重置失败: ' + (error.message || '请稍后重试'))
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
        
        console.log('正在切换用户状态:', user.id, action)
        
        await request({
          url: `system-users/${user.id}/toggle-status`,
          method: 'put'
        })
        
        ElMessage.success(`${action}成功`)
        loadUsers()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('切换用户状态异常:', error)
          ElMessage.error('操作失败: ' + (error.message || '请稍后重试'))
        }
      }
    }

    // 初始化
    onMounted(() => {
      loadUsers()
      loadOrganizations()
      loadRoles()
      loadServiceStaff()
    })

    return {
      loading,
      createLoading,
      editLoading,
      roleAssignLoading,
      logsLoading,
      organizationsLoading,
      users,
      selectedUser,
      multipleSelection,
      organizations,
      userLogs,
      userRoles,
      activeTab,
      editActiveTab,
      createActiveTab,
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
      serviceStaffList,
      serviceStaffLoading,
      createRules,
      editRules,
      loadUsers,
      loadUserStats,
      loadOrganizations,
      loadRoles,
      loadServiceStaff,
      handleSearch,
      handleReset,
      showCreateDialog,
      handleCreate,
      showDetailDialog,
      showEditDialog,
      handleUpdate,
      showRoleAssignDialog,
      handleRoleAssign,
      toggleRoleSelection,
      deleteUser,
      handleSizeChange,
      handleCurrentChange,
      handleSelectionChange,
      handleBatchDelete,
      handleAction,
      handleResetPassword,
      handleToggleStatus,
      getLogTypeColor
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
  float:right;
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
  min-height: 80px;
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
  flex-shrink: 0;
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
  text-align: left;
}

.stat-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  font-weight: 500;
  line-height: 1.2;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin: 0;
  line-height: 1.2;
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
  }
  
  :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    color: white;
    padding: 24px 32px;
  }
}

.role-assign-content {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-info-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 12px;
  border: 1px solid #dee2e6;
}

.user-info-text {
  margin-left: 16px;
  flex: 1;
}

.username {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
}

.fullname {
  font-size: 14px;
  color: #6c757d;
}

.selected-count-tag {
  margin-left: 12px;
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
  max-height: 400px;
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

.role-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.role-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border: 2px solid #dee2e6;
  transition: all 0.3s ease;
  cursor: pointer;
}

.role-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #409eff;
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f7ff 100%);
}

.role-card.selected {
  border-color: #409eff;
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f7ff 100%);
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.15);
}

.role-avatar {
  flex-shrink: 0;
}

.role-avatar .el-avatar {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.role-card.selected .role-avatar .el-avatar {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.role-info {
  flex: 1;
  min-width: 0;
}

.role-name {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
  line-height: 1.3;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.role-card.selected .role-name {
  color: #409eff;
}

.role-key {
  font-size: 14px;
  color: #6c757d;
  line-height: 1.5;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.role-card.selected .role-key {
  color: #409eff;
  opacity: 0.8;
}

.role-description {
  font-size: 14px;
  color: #6c757d;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.role-card.selected .role-description {
  color: #409eff;
  opacity: 0.8;
}

.role-status {
  margin-left: auto;
  flex-shrink: 0;
}

.selected-icon {
  color: #409eff;
}

.unselected-icon {
  color: #dcdfe6;
}
</style>