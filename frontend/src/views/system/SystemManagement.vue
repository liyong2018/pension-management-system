<template>
  <div class="system-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">
        <el-icon><Setting /></el-icon>
        系统管理
      </h1>
      <p class="page-description">管理系统用户、角色权限、菜单配置等核心功能</p>
    </div>

    <!-- 统计概览 -->
    <div class="statistics-overview" v-if="stats">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card user-stat">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon size="32"><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">系统用户</div>
                <div class="stat-value">{{ stats.totalUsers }}</div>
                <div class="stat-desc">活跃用户 {{ stats.activeUsers }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card role-stat">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon size="32"><Avatar /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">系统角色</div>
                <div class="stat-value">{{ stats.totalRoles }}</div>
                <div class="stat-desc">管理员角色 {{ stats.adminRoles }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card permission-stat">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon size="32"><Lock /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">权限数量</div>
                <div class="stat-value">{{ stats.totalPermissions }}</div>
                <div class="stat-desc">菜单权限 {{ stats.menuPermissions }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card menu-stat">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon size="32"><Menu /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">菜单配置</div>
                <div class="stat-value">{{ stats.totalMenus }}</div>
                <div class="stat-desc">顶级菜单 {{ stats.topLevelMenus }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 快捷入口 -->
    <div class="quick-actions">
      <el-card class="quick-actions-card">
        <template #header>
          <div class="card-header">
            <span class="card-title">
              <el-icon><Operation /></el-icon>
              快捷操作
            </span>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="quick-action-item" @click="navigateToUsers">
              <div class="action-icon user-icon">
                <el-icon size="24"><User /></el-icon>
              </div>
              <div class="action-content">
                <div class="action-title">用户管理</div>
                <div class="action-desc">管理系统用户账户</div>
              </div>
              <el-icon class="action-arrow"><ArrowRight /></el-icon>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="quick-action-item" @click="navigateToRoles">
              <div class="action-icon role-icon">
                <el-icon size="24"><Avatar /></el-icon>
              </div>
              <div class="action-content">
                <div class="action-title">角色管理</div>
                <div class="action-desc">配置角色权限体系</div>
              </div>
              <el-icon class="action-arrow"><ArrowRight /></el-icon>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="quick-action-item" @click="navigateToPermissions">
              <div class="action-icon permission-icon">
                <el-icon size="24"><Lock /></el-icon>
              </div>
              <div class="action-content">
                <div class="action-title">权限管理</div>
                <div class="action-desc">配置系统访问权限</div>
              </div>
              <el-icon class="action-arrow"><ArrowRight /></el-icon>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="8">
            <div class="quick-action-item" @click="navigateToMenus">
              <div class="action-icon menu-icon">
                <el-icon size="24"><Menu /></el-icon>
              </div>
              <div class="action-content">
                <div class="action-title">菜单管理</div>
                <div class="action-desc">配置系统菜单结构</div>
              </div>
              <el-icon class="action-arrow"><ArrowRight /></el-icon>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="quick-action-item" @click="navigateToLogs">
              <div class="action-icon log-icon">
                <el-icon size="24"><Document /></el-icon>
              </div>
              <div class="action-content">
                <div class="action-title">日志管理</div>
                <div class="action-desc">查看系统操作日志</div>
              </div>
              <el-icon class="action-arrow"><ArrowRight /></el-icon>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="quick-action-item" @click="showSettings">
              <div class="action-icon setting-icon">
                <el-icon size="24"><Setting /></el-icon>
              </div>
              <div class="action-content">
                <div class="action-title">系统设置</div>
                <div class="action-desc">配置系统参数</div>
              </div>
              <el-icon class="action-arrow"><ArrowRight /></el-icon>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <!-- 最近操作日志 -->
    <div class="recent-activities">
      <el-card class="activities-card">
        <template #header>
          <div class="card-header">
            <span class="card-title">
              <el-icon><Clock /></el-icon>
              最近操作
            </span>
            <el-button type="primary" link @click="navigateToLogs">
              查看全部
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </template>
        <div class="activity-list" v-loading="activityLoading">
          <div 
            v-for="activity in recentActivities" 
            :key="activity.id" 
            class="activity-item"
          >
            <div class="activity-icon">
              <el-icon :class="getActivityIconClass(activity.type)">
                <component :is="getActivityIcon(activity.type)" />
              </el-icon>
            </div>
            <div class="activity-content">
              <div class="activity-title">{{ activity.title }}</div>
              <div class="activity-desc">{{ activity.description }}</div>
              <div class="activity-meta">
                <span class="activity-user">{{ activity.user }}</span>
                <span class="activity-time">{{ activity.time }}</span>
              </div>
            </div>
          </div>
          <div v-if="!recentActivities.length" class="no-activities">
            <el-empty description="暂无操作记录" />
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Setting, User, Avatar, Lock, Menu, Operation, ArrowRight, 
  Clock, Document, Plus, Edit, Delete, View
} from '@element-plus/icons-vue'

export default {
  name: 'SystemManagement',
  components: {
    Setting, User, Avatar, Lock, Menu, Operation, ArrowRight,
    Clock, Document, Plus, Edit, Delete, View
  },
  setup() {
    const router = useRouter()
    const stats = ref({
      totalUsers: 0,
      activeUsers: 0,
      totalRoles: 0,
      adminRoles: 0,
      totalPermissions: 0,
      menuPermissions: 0,
      totalMenus: 0,
      topLevelMenus: 0
    })
    const recentActivities = ref([])
    const activityLoading = ref(false)

    // 加载统计数据
    const loadStats = async () => {
      try {
        // 并行请求所有统计数据
        const [usersResponse, rolesResponse, permissionsResponse] = await Promise.all([
          request({
            url: 'system-users',
            method: 'get',
            params: { page: 1, size: 1 }
          }),
          request({
            url: 'roles',
            method: 'get',
            params: { page: 1, size: 1 }
          }),
          request({
            url: 'permissions',
            method: 'get',
            params: { page: 1, size: 1 }
          })
        ])

        stats.value = {
          totalUsers: usersResponse?.total || 0,
          activeUsers: Math.floor((usersResponse?.total || 0) * 0.8), // 模拟活跃用户
          totalRoles: rolesResponse?.total || 0,
          adminRoles: Math.floor((rolesResponse?.total || 0) * 0.3), // 模拟管理员角色
          totalPermissions: permissionsResponse?.total || 0,
          menuPermissions: Math.floor((permissionsResponse?.total || 0) * 0.6), // 模拟菜单权限
          totalMenus: 15, // 模拟菜单数量
          topLevelMenus: 5 // 模拟顶级菜单
        }
      } catch (error) {
        console.error('加载统计数据失败:', error)
        ElMessage.error('加载统计数据失败')
        
        // 使用默认值
        stats.value = {
          totalUsers: 0,
          activeUsers: 0,
          totalRoles: 0,
          adminRoles: 0,
          totalPermissions: 0,
          menuPermissions: 0,
          totalMenus: 0,
          topLevelMenus: 0
        }
      }
    }

    // 加载最近操作记录
    const loadRecentActivities = async () => {
      activityLoading.value = true
      try {
        // 模拟最近操作数据
        await new Promise(resolve => setTimeout(resolve, 500))
        recentActivities.value = [
          {
            id: 1,
            type: 'user_create',
            title: '创建用户',
            description: '创建了新用户 "张三"',
            user: 'admin',
            time: '2024-01-20 14:30:00'
          },
          {
            id: 2,
            type: 'role_update',
            title: '更新角色',
            description: '修改了角色 "机构管理员" 的权限配置',
            user: 'admin',
            time: '2024-01-20 13:15:00'
          },
          {
            id: 3,
            type: 'permission_assign',
            title: '分配权限',
            description: '为角色 "数据分析师" 分配了新的数据访问权限',
            user: 'admin',
            time: '2024-01-20 11:45:00'
          },
          {
            id: 4,
            type: 'menu_create',
            title: '创建菜单',
            description: '添加了新的菜单项 "报表中心"',
            user: 'admin',
            time: '2024-01-20 10:20:00'
          },
          {
            id: 5,
            type: 'user_delete',
            title: '删除用户',
            description: '删除了无效用户 "test001"',
            user: 'admin',
            time: '2024-01-20 09:10:00'
          }
        ]
      } catch (error) {
        console.error('加载最近操作失败:', error)
        ElMessage.error('加载最近操作失败')
      } finally {
        activityLoading.value = false
      }
    }

    // 获取操作图标
    const getActivityIcon = (type) => {
      const iconMap = {
        user_create: 'Plus',
        user_update: 'Edit',
        user_delete: 'Delete',
        role_create: 'Plus',
        role_update: 'Edit',
        role_delete: 'Delete',
        permission_assign: 'Lock',
        menu_create: 'Plus',
        menu_update: 'Edit',
        menu_delete: 'Delete'
      }
      return iconMap[type] || 'View'
    }

    // 获取操作图标样式类
    const getActivityIconClass = (type) => {
      if (type.includes('create')) return 'activity-icon-success'
      if (type.includes('update')) return 'activity-icon-warning'
      if (type.includes('delete')) return 'activity-icon-danger'
      return 'activity-icon-info'
    }

    // 导航方法
    const navigateToUsers = () => {
      router.push('/system/users')
    }

    const navigateToRoles = () => {
      router.push('/system/roles')
    }

    const navigateToPermissions = () => {
      router.push('/system/permissions')
    }

    const navigateToMenus = () => {
      router.push('/system/menus')
    }

    const navigateToLogs = () => {
      router.push('/system/logs')
    }

    const showSettings = () => {
      ElMessage.info('系统设置功能正在开发中...')
    }

    onMounted(() => {
      loadStats()
      loadRecentActivities()
    })

    return {
      stats,
      recentActivities,
      activityLoading,
      getActivityIcon,
      getActivityIconClass,
      navigateToUsers,
      navigateToRoles,
      navigateToPermissions,
      navigateToMenus,
      navigateToLogs,
      showSettings
    }
  }
}
</script>

<style scoped>
.system-management {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 32px;
  text-align: center;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 12px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.page-title .el-icon {
  color: #409eff;
}

.page-description {
  font-size: 16px;
  color: #666;
  margin: 0;
}

.statistics-overview {
  margin-bottom: 32px;
}

.stat-card {
  transition: all 0.3s ease;
  border-radius: 12px;
  overflow: hidden;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 24px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  color: white;
}

.user-stat .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.role-stat .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.permission-stat .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.menu-stat .stat-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
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
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 4px;
}

.stat-desc {
  font-size: 12px;
  color: #999;
}

.quick-actions {
  margin-bottom: 32px;
}

.quick-actions-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 8px;
}

.quick-action-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border: 2px solid #f0f2f5;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.quick-action-item:hover {
  border-color: #409eff;
  background: #f8fbff;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.15);
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  color: white;
}

.user-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.role-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.permission-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.menu-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.log-icon {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.setting-icon {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.action-content {
  flex: 1;
}

.action-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
}

.action-desc {
  font-size: 14px;
  color: #666;
}

.action-arrow {
  color: #c0c4cc;
  transition: all 0.3s ease;
}

.quick-action-item:hover .action-arrow {
  color: #409eff;
  transform: translateX(4px);
}

.recent-activities {
  margin-bottom: 32px;
}

.activities-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.activity-list {
  max-height: 400px;
  overflow-y: auto;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  padding: 16px 0;
  border-bottom: 1px solid #f0f2f5;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-item .activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  color: white;
}

.activity-icon-success {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.activity-icon-warning {
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
}

.activity-icon-danger {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
}

.activity-icon-info {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.activity-content {
  flex: 1;
}

.activity-title {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
}

.activity-desc {
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
}

.activity-meta {
  display: flex;
  align-items: center;
  gap: 16px;
}

.activity-user {
  font-size: 12px;
  color: #409eff;
  font-weight: 500;
}

.activity-time {
  font-size: 12px;
  color: #999;
}

.no-activities {
  padding: 40px 0;
  text-align: center;
}

:deep(.el-card__header) {
  background: #fafbfc;
  border-bottom: 1px solid #f0f2f5;
  padding: 20px 24px;
}

:deep(.el-card__body) {
  padding: 24px;
}

:deep(.el-button--link) {
  color: #409eff;
  font-weight: 500;
}

:deep(.el-button--link:hover) {
  color: #66b1ff;
}
</style> 