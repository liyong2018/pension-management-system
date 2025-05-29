<template>
  <div class="api-test">
    <!-- 数据库初始化提醒 -->
    <el-alert
      title="数据库初始化提醒"
      type="warning"
      :closable="false"
      show-icon
      class="init-alert"
    >
      <template #default>
        <div class="alert-content">
          <p>如果权限相关功能出现问题，请执行以下初始化脚本：</p>
          <div class="script-commands">
            <el-tag type="info" class="script-tag">方案一：执行完整脚本 backend/src/main/resources/schema_fixed.sql</el-tag>
            <el-tag type="success" class="script-tag">方案二：执行快速脚本 backend/init_permissions.sql</el-tag>
          </div>
          <p class="script-note">
            <el-icon><InfoFilled /></el-icon>
            建议先点击"检查数据库状态"按钮确认是否需要初始化
          </p>
        </div>
      </template>
    </el-alert>

    <el-card class="test-card">
      <template #header>
        <div class="card-header">
          <span>系统管理接口测试</span>
          <el-button type="primary" @click="runAllTests">运行所有测试</el-button>
        </div>
      </template>

      <div class="test-section">
        <h3>用户管理接口测试</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-button @click="testGetUsers" :loading="loading.users">获取用户列表</el-button>
            <el-button @click="testCreateUser" :loading="loading.createUser">创建用户</el-button>
            <el-button @click="testUpdateUser" :loading="loading.updateUser">更新用户</el-button>
            <el-button @click="testDeleteUser" :loading="loading.deleteUser">删除用户</el-button>
          </el-col>
          <el-col :span="12">
            <el-button @click="testResetPassword" :loading="loading.resetPassword">重置密码</el-button>
            <el-button @click="testToggleStatus" :loading="loading.toggleStatus">切换状态</el-button>
            <el-button @click="testAssignRoles" :loading="loading.assignRoles">分配角色</el-button>
            <el-button @click="testGetUserStats" :loading="loading.userStats">用户统计</el-button>
          </el-col>
        </el-row>
      </div>

      <div class="test-section">
        <h3>角色管理接口测试</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-button @click="testGetRoles" :loading="loading.roles">获取角色列表</el-button>
            <el-button @click="testCreateRole" :loading="loading.createRole">创建角色</el-button>
            <el-button @click="testUpdateRole" :loading="loading.updateRole">更新角色</el-button>
            <el-button @click="testDeleteRole" :loading="loading.deleteRole">删除角色</el-button>
          </el-col>
        </el-row>
      </div>

      <div class="test-section">
        <h3>权限管理接口测试</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-button @click="testGetPermissions" :loading="loading.permissions">获取权限列表</el-button>
            <el-button @click="testGetUserMenuTree" :loading="loading.userMenuTree">获取用户菜单树</el-button>
          </el-col>
        </el-row>
      </div>

      <div class="test-section">
        <h3>机构管理接口测试</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-button @click="testGetOrganizations" :loading="loading.organizations">获取机构列表</el-button>
          </el-col>
        </el-row>
      </div>

      <div class="test-section">
        <h3>操作日志接口测试</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-button @click="testGetOperationLogs" :loading="loading.operationLogs">获取操作日志</el-button>
          </el-col>
        </el-row>
      </div>

      <div class="test-section">
        <h3>权限调试接口测试</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-button @click="testCheckDatabaseStatus" :loading="loading.databaseStatus">检查数据库状态</el-button>
            <el-button @click="testGetPermissionsTree" :loading="loading.permissionsTree">获取权限树</el-button>
            <el-button @click="testGetPermissionsDebug" :loading="loading.permissionsDebug">调试权限数据</el-button>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <el-card class="result-card" v-if="testResults.length > 0">
      <template #header>
        <span>测试结果</span>
        <el-button type="danger" size="small" @click="clearResults">清空结果</el-button>
      </template>
      
      <div class="test-results">
        <div v-for="(result, index) in testResults" :key="index" class="test-result-item">
          <el-tag :type="result.success ? 'success' : 'danger'" class="result-tag">
            {{ result.success ? '成功' : '失败' }}
          </el-tag>
          <span class="result-name">{{ result.name }}</span>
          <span class="result-time">{{ result.time }}</span>
          <div class="result-detail" v-if="result.error">
            <span class="error-message">{{ result.error }}</span>
          </div>
          <div class="result-detail" v-if="result.data && result.success">
            <span class="success-message">数据量: {{ result.dataCount }}</span>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'
import request from '@/utils/request'

export default {
  name: 'ApiTest',
  components: {
    InfoFilled
  },
  setup() {
    const loading = reactive({
      users: false,
      createUser: false,
      updateUser: false,
      deleteUser: false,
      resetPassword: false,
      toggleStatus: false,
      assignRoles: false,
      userStats: false,
      roles: false,
      createRole: false,
      updateRole: false,
      deleteRole: false,
      permissions: false,
      userMenuTree: false,
      organizations: false,
      operationLogs: false,
      permissionsTree: false,
      permissionsDebug: false,
      databaseStatus: false
    })

    const testResults = ref([])

    const addTestResult = (name, success, error = null, data = null) => {
      const result = {
        name,
        success,
        error,
        data,
        dataCount: data ? (Array.isArray(data) ? data.length : (data.list ? data.list.length : 1)) : 0,
        time: new Date().toLocaleTimeString()
      }
      testResults.value.unshift(result)
    }

    const testGetUsers = async () => {
      loading.users = true
      try {
        const data = await request({
          url: 'system-users',
          method: 'get',
          params: { page: 1, size: 10 }
        })
        addTestResult('获取用户列表', true, null, data)
        ElMessage.success('用户列表获取成功')
      } catch (error) {
        addTestResult('获取用户列表', false, error.message)
        ElMessage.error('用户列表获取失败: ' + error.message)
      } finally {
        loading.users = false
      }
    }

    const testCreateUser = async () => {
      loading.createUser = true
      try {
        const testUserData = {
          username: 'testuser_' + Date.now(),
          password: '123456',
          fullName: '测试用户',
          email: 'test@example.com',
          phone: '13800138000',
          isAdmin: false,
          isActive: true
        }
        const data = await request({
          url: 'system-users',
          method: 'post',
          data: testUserData
        })
        addTestResult('创建用户', true, null, data)
        ElMessage.success('用户创建成功')
      } catch (error) {
        addTestResult('创建用户', false, error.message)
        ElMessage.error('用户创建失败: ' + error.message)
      } finally {
        loading.createUser = false
      }
    }

    const testUpdateUser = async () => {
      loading.updateUser = true
      try {
        // 先获取一个用户
        const usersData = await request({
          url: 'system-users',
          method: 'get',
          params: { page: 1, size: 1 }
        })
        
        if (usersData.list && usersData.list.length > 0) {
          const user = usersData.list[0]
          const updateData = {
            ...user,
            fullName: '测试更新_' + Date.now()
          }
          
          const data = await request({
            url: `system-users/${user.id}`,
            method: 'put',
            data: updateData
          })
          addTestResult('更新用户', true, null, data)
          ElMessage.success('用户更新成功')
        } else {
          throw new Error('没有找到可更新的用户')
        }
      } catch (error) {
        addTestResult('更新用户', false, error.message)
        ElMessage.error('用户更新失败: ' + error.message)
      } finally {
        loading.updateUser = false
      }
    }

    const testGetRoles = async () => {
      loading.roles = true
      try {
        const data = await request({
          url: 'roles/all',
          method: 'get'
        })
        addTestResult('获取角色列表', true, null, data)
        ElMessage.success('角色列表获取成功')
      } catch (error) {
        addTestResult('获取角色列表', false, error.message)
        ElMessage.error('角色列表获取失败: ' + error.message)
      } finally {
        loading.roles = false
      }
    }

    const testGetPermissions = async () => {
      loading.permissions = true
      try {
        const data = await request({
          url: 'permissions/all',
          method: 'get'
        })
        addTestResult('获取权限列表', true, null, data)
        ElMessage.success('权限列表获取成功')
      } catch (error) {
        addTestResult('获取权限列表', false, error.message)
        ElMessage.error('权限列表获取失败: ' + error.message)
      } finally {
        loading.permissions = false
      }
    }

    const testGetUserMenuTree = async () => {
      loading.userMenuTree = true
      try {
        const data = await request({
          url: 'permissions/user-menu-tree',
          method: 'get'
        })
        addTestResult('获取用户菜单树', true, null, data)
        ElMessage.success('用户菜单树获取成功')
      } catch (error) {
        addTestResult('获取用户菜单树', false, error.message)
        ElMessage.error('用户菜单树获取失败: ' + error.message)
      } finally {
        loading.userMenuTree = false
      }
    }

    const testGetOrganizations = async () => {
      loading.organizations = true
      try {
        const data = await request({
          url: 'organizations',
          method: 'get',
          params: { page: 1, size: 10 }
        })
        addTestResult('获取机构列表', true, null, data)
        ElMessage.success('机构列表获取成功')
      } catch (error) {
        addTestResult('获取机构列表', false, error.message)
        ElMessage.error('机构列表获取失败: ' + error.message)
      } finally {
        loading.organizations = false
      }
    }

    const testGetOperationLogs = async () => {
      loading.operationLogs = true
      try {
        const data = await request({
          url: 'operation-logs',
          method: 'get',
          params: { page: 1, size: 10 }
        })
        addTestResult('获取操作日志', true, null, data)
        ElMessage.success('操作日志获取成功')
      } catch (error) {
        addTestResult('获取操作日志', false, error.message)
        ElMessage.error('操作日志获取失败: ' + error.message)
      } finally {
        loading.operationLogs = false
      }
    }

    const testGetUserStats = async () => {
      loading.userStats = true
      try {
        const data = await request({
          url: 'system-users/statistics',
          method: 'get'
        })
        addTestResult('获取用户统计', true, null, data)
        ElMessage.success('用户统计获取成功')
      } catch (error) {
        addTestResult('获取用户统计', false, error.message)
        ElMessage.error('用户统计获取失败: ' + error.message)
      } finally {
        loading.userStats = false
      }
    }

    const testGetPermissionsTree = async () => {
      loading.permissionsTree = true
      try {
        const data = await request({
          url: 'permissions/tree',
          method: 'get'
        })
        addTestResult('获取权限树', true, null, data)
        ElMessage.success('权限树获取成功')
      } catch (error) {
        addTestResult('获取权限树', false, error.message)
        ElMessage.error('权限树获取失败: ' + error.message)
      } finally {
        loading.permissionsTree = false
      }
    }

    const testGetPermissionsDebug = async () => {
      loading.permissionsDebug = true
      try {
        const data = await request({
          url: 'permissions/debug',
          method: 'get'
        })
        addTestResult('调试权限数据', true, null, data)
        ElMessage.success('权限数据调试成功')
      } catch (error) {
        addTestResult('调试权限数据', false, error.message)
        ElMessage.error('权限数据调试失败: ' + error.message)
      } finally {
        loading.permissionsDebug = false
      }
    }

    const testCheckDatabaseStatus = async () => {
      loading.databaseStatus = true
      try {
        const data = await request({
          url: 'permissions/db-status',
          method: 'get'
        })
        addTestResult('检查数据库状态', true, null, data)
        ElMessage.success('数据库状态检查成功')
      } catch (error) {
        addTestResult('检查数据库状态', false, error.message)
        ElMessage.error('数据库状态检查失败: ' + error.message)
      } finally {
        loading.databaseStatus = false
      }
    }

    const runAllTests = async () => {
      ElMessage.info('开始运行所有测试...')
      
      await testGetUsers()
      await new Promise(resolve => setTimeout(resolve, 500))
      
      await testGetRoles()
      await new Promise(resolve => setTimeout(resolve, 500))
      
      await testGetPermissions()
      await new Promise(resolve => setTimeout(resolve, 500))
      
      await testGetUserMenuTree()
      await new Promise(resolve => setTimeout(resolve, 500))
      
      await testGetOrganizations()
      await new Promise(resolve => setTimeout(resolve, 500))
      
      await testGetOperationLogs()
      await new Promise(resolve => setTimeout(resolve, 500))
      
      await testGetUserStats()
      
      await testGetPermissionsTree()
      await new Promise(resolve => setTimeout(resolve, 500))
      
      await testGetPermissionsDebug()
      
      await testCheckDatabaseStatus()
      
      ElMessage.success('所有测试完成')
    }

    const clearResults = () => {
      testResults.value = []
    }

    return {
      loading,
      testResults,
      testGetUsers,
      testCreateUser,
      testUpdateUser,
      testGetRoles,
      testGetPermissions,
      testGetUserMenuTree,
      testGetOrganizations,
      testGetOperationLogs,
      testGetUserStats,
      testGetPermissionsTree,
      testGetPermissionsDebug,
      testCheckDatabaseStatus,
      runAllTests,
      clearResults
    }
  }
}
</script>

<style scoped>
.api-test {
  padding: 20px;
}

.init-alert {
  margin-bottom: 20px;
}

.alert-content p {
  margin: 8px 0;
}

.script-commands {
  margin: 12px 0;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.script-tag {
  margin: 2px 0;
}

.script-note {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #606266;
  font-size: 13px;
  margin-top: 8px;
}

.test-card, .result-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.test-section {
  margin-bottom: 30px;
}

.test-section h3 {
  margin-bottom: 15px;
  color: #409eff;
  border-bottom: 2px solid #409eff;
  padding-bottom: 5px;
}

.test-section .el-button {
  margin: 5px;
}

.test-results {
  max-height: 500px;
  overflow-y: auto;
}

.test-result-item {
  padding: 10px;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  gap: 10px;
}

.result-tag {
  flex-shrink: 0;
}

.result-name {
  font-weight: 500;
  flex: 1;
}

.result-time {
  color: #999;
  font-size: 12px;
}

.result-detail {
  width: 100%;
  margin-top: 5px;
  font-size: 12px;
}

.error-message {
  color: #f56c6c;
}

.success-message {
  color: #67c23a;
}
</style> 