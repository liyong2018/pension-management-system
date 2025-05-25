<template>
  <el-container id="app-container" style="height: 100vh;">
    <!-- 左侧边栏 -->
    <el-aside class="app-aside" :width="isCollapsed ? '64px' : '250px'">
      <!-- 系统标题区域 -->
      <div class="logo-section">
        <div class="logo-title" v-show="!isCollapsed">
          <div class="logo-icon">🏥</div>
          <div class="title-text">养老信息管理系统</div>
        </div>
        <div class="logo-collapsed" v-show="isCollapsed">
          <div class="logo-icon-small">🏥</div>
        </div>
        <!-- 折叠按钮 -->
        <div class="collapse-trigger" @click="toggleCollapse">
          <el-icon :size="16">
            <ArrowRight v-if="isCollapsed" />
            <ArrowLeft v-else />
          </el-icon>
        </div>
      </div>
      
      <!-- 侧边菜单 -->
      <el-menu
        :default-active="activeIndex"
        router
        background-color="transparent"
        text-color="#ecf0f1"
        active-text-color="#ffffff"
        v-loading="menuLoading"
        class="sidebar-menu"
        :collapse="isCollapsed"
        :unique-opened="true"
        @click="handleMenuClick"
      >
        <!-- 动态渲染菜单 -->
        <template v-for="menu in visibleMenus" :key="menu.id">
          <!-- 普通菜单项 -->
          <el-menu-item 
            v-if="menu.type === 'MENU' && (!menu.children || menu.children.length === 0)"
            :index="menu.routePath"
            :disabled="!menu.status"
            class="custom-menu-item"
          >
            <el-icon class="menu-icon">
              <component :is="getIconComponent(menu.icon)" />
            </el-icon>
            <span class="menu-text">{{ menu.name }}</span>
          </el-menu-item>
          
          <!-- 子菜单（手风琴样式） -->
          <el-sub-menu 
            v-else-if="menu.type === 'CATALOG' && menu.children && menu.children.length > 0"
            :index="menu.routePath || menu.permissionKey"
            :disabled="!menu.status"
            class="custom-sub-menu"
          >
            <template #title>
              <el-icon class="menu-icon">
                <component :is="getIconComponent(menu.icon)" />
              </el-icon>
              <span class="menu-text">{{ menu.name }}</span>
            </template>
            
            <!-- 递归渲染子菜单 -->
            <template v-for="child in menu.children" :key="child.id">
              <el-menu-item 
                v-if="child.isVisible && child.status && child.type === 'MENU'"
                :index="child.routePath"
                :disabled="!child.status"
                class="custom-sub-menu-item"
              >
                <el-icon class="menu-icon">
                  <component :is="getIconComponent(child.icon)" />
                </el-icon>
                <span class="menu-text">{{ child.name }}</span>
              </el-menu-item>
            </template>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>
    
    <!-- 主内容区域 -->
    <el-container>
      <!-- 顶部头部 -->
      <el-header class="app-header" height="60px">
        <div class="header-content">
          <div class="header-left">
            <div class="breadcrumb">
              <el-icon><Location /></el-icon>
              <span>当前位置</span>
            </div>
          </div>
          <div class="header-right">
            <el-dropdown>
              <div class="user-info">
                <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
                <span class="username">管理员</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>个人中心</el-dropdown-item>
                  <el-dropdown-item>修改密码</el-dropdown-item>
                  <el-dropdown-item divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      
      <!-- 主内容 -->
      <el-main class="app-main" @click="handleMainClick">
        <router-view />
      </el-main>
    </el-container>
    
    <!-- 遮罩层，用于点击外部区域收起菜单 -->
    <div 
      v-if="!isCollapsed && showMask" 
      class="sidebar-mask" 
      @click="handleMaskClick"
    ></div>
  </el-container>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { 
  House, OfficeBuilding, User, Monitor, Warning, Avatar, 
  Setting, Key, Collection, Document, Menu, ArrowRight, ArrowLeft,
  Location, ArrowDown
} from '@element-plus/icons-vue';

const route = useRoute();
const activeIndex = ref(route.path);
const menuLoading = ref(false);
const menuData = ref([]);
const isCollapsed = ref(true); // 默认收起状态
const showMask = ref(false);

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
  'Menu': Menu
};

// 获取图标组件
const getIconComponent = (iconName) => {
  return iconMap[iconName] || Menu;
};

// 计算可见的菜单（只显示启用且可见的菜单）
const visibleMenus = computed(() => {
  const filtered = menuData.value.filter(menu => 
    menu.isVisible && menu.status && (menu.type === 'MENU' || menu.type === 'CATALOG')
  );
  console.log('🔍 visibleMenus 计算结果:');
  console.log(`   - 原始菜单数量: ${menuData.value.length}`);
  console.log(`   - 过滤后菜单数量: ${filtered.length}`);
  console.log(`   - 过滤后菜单: ${filtered.map(m => m.name).join(', ')}`);
  return filtered;
});

// 加载菜单数据
const loadMenuData = async () => {
  menuLoading.value = true;
  try {
    console.log('🔄 开始加载顶部菜单数据...');
    console.log('🌐 请求URL: /api/permissions/tree');
    
    const response = await fetch('/api/permissions/tree');
    console.log('📡 菜单API响应状态:', response.status);
    
    if (response.ok) {
      const data = await response.json();
      console.log('📊 菜单API响应数据:', data);
      console.log('📋 数据类型:', Array.isArray(data) ? '数组' : typeof data);
      console.log('📈 数据长度:', Array.isArray(data) ? data.length : 'N/A');
      
      if (Array.isArray(data) && data.length > 0) {
        // 强制使用API数据
        menuData.value = data;
        console.log('✅ 顶部菜单数据加载成功:', data.length, '条');
        console.log('🎯 菜单列表:', data.map(m => `${m.name}(${m.type})`).join(', '));
        
        // 详细调试每个菜单项的数据结构
        data.forEach((menu, index) => {
          console.log(`📋 菜单${index + 1}: ${menu.name}`);
          console.log(`   - type: ${menu.type}`);
          console.log(`   - isVisible: ${menu.isVisible}`);
          console.log(`   - status: ${menu.status}`);
          console.log(`   - routePath: ${menu.routePath}`);
          console.log(`   - icon: ${menu.icon}`);
          if (menu.children && menu.children.length > 0) {
            console.log(`   - 子菜单数量: ${menu.children.length}`);
            menu.children.forEach((child, childIndex) => {
              console.log(`     ${childIndex + 1}. ${child.name} (${child.type}) - visible: ${child.isVisible}, status: ${child.status}`);
            });
          }
        });
        
        // 清除可能的缓存
        localStorage.removeItem('menuCache');
        sessionStorage.removeItem('menuCache');
      } else {
        console.warn('⚠️ 菜单API返回数据为空或格式异常:', data);
        // 即使API返回空数据，也不使用默认菜单，而是显示错误
        menuData.value = [];
        ElMessage.error('菜单数据为空，请检查后端数据');
      }
    } else {
      console.error('❌ 菜单API请求失败，状态码:', response.status);
      const errorText = await response.text();
      console.error('📄 错误响应:', errorText);
      
      // 不使用默认菜单，显示错误信息
      menuData.value = [];
      ElMessage.error(`菜单加载失败: HTTP ${response.status}`);
    }
  } catch (error) {
    console.error('💥 加载菜单数据异常:', error);
    console.error('🔍 错误详情:', error.message);
    console.error('📍 错误堆栈:', error.stack);
    
    // 不使用默认菜单，显示错误信息
    menuData.value = [];
    ElMessage.error('菜单加载异常: ' + error.message);
  } finally {
    menuLoading.value = false;
    console.log('🏁 菜单加载完成，当前菜单数量:', menuData.value.length);
  }
};

// 获取默认菜单（作为后备方案）
const getDefaultMenus = () => {
  return [
    {
      id: 1,
      name: '首页',
      type: 'MENU',
      routePath: '/',
      icon: 'House',
      isVisible: true,
      status: true,
      sortOrder: 1
    },
    {
      id: 2,
      name: '机构管理',
      type: 'MENU',
      routePath: '/organization-management',
      icon: 'OfficeBuilding',
      isVisible: true,
      status: true,
      sortOrder: 2
    },
    {
      id: 3,
      name: '人员档案',
      type: 'MENU',
      routePath: '/elderly-profiles',
      icon: 'User',
      isVisible: true,
      status: true,
      sortOrder: 3
    },
    {
      id: 4,
      name: '智能设备',
      type: 'CATALOG',
      routePath: '/smart-device',
      icon: 'Monitor',
      isVisible: true,
      status: true,
      sortOrder: 4,
      children: [
        {
          id: 11,
          name: '设备管理',
          type: 'MENU',
          routePath: '/smart-devices',
          icon: 'Monitor',
          isVisible: true,
          status: true,
          sortOrder: 1
        },
        {
          id: 12,
          name: '告警管理',
          type: 'MENU',
          routePath: '/device-alarms',
          icon: 'Warning',
          isVisible: true,
          status: true,
          sortOrder: 2
        }
      ]
    },
    {
      id: 5,
      name: '服务记录',
      type: 'MENU',
      routePath: '/service-records',
      icon: 'Document',
      isVisible: true,
      status: true,
      sortOrder: 5
    },
    {
      id: 6,
      name: '志愿者管理',
      type: 'MENU',
      routePath: '/volunteers',
      icon: 'Avatar',
      isVisible: true,
      status: true,
      sortOrder: 6
    },
    {
      id: 7,
      name: '系统管理',
      type: 'CATALOG',
      routePath: '/system',
      icon: 'Setting',
      isVisible: true,
      status: true,
      sortOrder: 7,
      children: [
        {
          id: 21,
          name: '用户管理',
          type: 'MENU',
          routePath: '/system/users',
          icon: 'User',
          isVisible: true,
          status: true,
          sortOrder: 1
        },
        {
          id: 22,
          name: '角色管理',
          type: 'MENU',
          routePath: '/system/roles',
          icon: 'Avatar',
          isVisible: true,
          status: true,
          sortOrder: 2
        },
        {
          id: 23,
          name: '权限管理',
          type: 'MENU',
          routePath: '/system/permissions',
          icon: 'Key',
          isVisible: true,
          status: true,
          sortOrder: 3
        },
        {
          id: 24,
          name: '菜单管理',
          type: 'MENU',
          routePath: '/system/menus',
          icon: 'Menu',
          isVisible: true,
          status: true,
          sortOrder: 4
        },
        {
          id: 25,
          name: '日志管理',
          type: 'MENU',
          routePath: '/system/logs',
          icon: 'Document',
          isVisible: true,
          status: true,
          sortOrder: 5
        },
        {
          id: 26,
          name: '字典管理',
          type: 'MENU',
          routePath: '/system/dictionaries',
          icon: 'Collection',
          isVisible: true,
          status: true,
          sortOrder: 6
        }
      ]
    }
  ];
};

// 监听路由变化，更新导航菜单的激活状态
watch(() => route.path, (newPath) => {
  activeIndex.value = newPath;
});

// 处理菜单点击事件
const handleMenuClick = () => {
  // 在移动端或小屏幕时，点击菜单项后自动收起
  if (window.innerWidth <= 768) {
    isCollapsed.value = true;
    showMask.value = false;
  }
};

// 处理主内容区域点击事件
const handleMainClick = () => {
  // 点击主内容区域时收起菜单（仅在展开状态下）
  if (!isCollapsed.value) {
    isCollapsed.value = true;
    showMask.value = false;
  }
};

// 处理遮罩层点击事件
const handleMaskClick = () => {
  // 点击遮罩层收起菜单
  isCollapsed.value = true;
  showMask.value = false;
};

// 处理折叠按钮点击事件
const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value;
  
  // 展开时显示遮罩层（在小屏幕上）
  if (!isCollapsed.value && window.innerWidth <= 768) {
    showMask.value = true;
  } else {
    showMask.value = false;
  }
};

// 监听窗口大小变化
const handleResize = () => {
  if (window.innerWidth > 768) {
    showMask.value = false;
  } else if (!isCollapsed.value) {
    showMask.value = true;
  }
};

// 组件挂载时加载菜单数据
onMounted(() => {
  console.log('🚀 App组件挂载，开始初始化菜单...');
  
  // 清除所有可能的缓存
  localStorage.clear();
  sessionStorage.clear();
  
  // 强制刷新菜单数据
  loadMenuData();
  
  // 添加窗口大小变化监听
  window.addEventListener('resize', handleResize);
  
  // 添加页面可见性变化监听，确保页面重新激活时刷新菜单
  document.addEventListener('visibilitychange', () => {
    if (!document.hidden) {
      console.log('📱 页面重新激活，刷新菜单数据...');
      loadMenuData();
    }
  });
});

// 暴露刷新菜单的方法，供其他组件调用
window.refreshTopMenu = () => {
  console.log('🔄 手动刷新菜单...');
  loadMenuData();
};

</script>

<style>
/* 全局样式 */
html, body {
  height: 100%;
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app-container {
  /* height: 100%; 已通过内联样式设置 */
}

/* 左侧边栏样式 */
.app-aside {
  background-color: #2c3e50;
  box-shadow: 2px 0 6px rgba(0, 0, 0, 0.1);
  transition: width 0.3s ease;
  position: relative;
  z-index: 1001;
}

/* 系统标题区域样式 */
.logo-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 16px;
  border-bottom: 1px solid #34495e;
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
  transition: all 0.3s ease;
  position: relative;
}

/* 系统标题样式 */
.logo-title {
  color: #ecf0f1;
  font-size: 16px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
  overflow: hidden;
}

.logo-icon {
  font-size: 24px;
}

.title-text {
  font-size: 14px;
}

.logo-collapsed {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.logo-icon-small {
  font-size: 28px;
}

/* 折叠按钮样式 */
.collapse-trigger {
  width: 20px;
  height: 20px;
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #ecf0f1;
  transition: all 0.3s ease;
}

.collapse-trigger:hover {
  background-color: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}

/* 侧边菜单样式 */
.sidebar-menu {
  border-right: none;
  height: calc(100vh - 80px);
  overflow-y: auto;
  transition: all 0.3s ease;
  background-color: transparent;
}

/* 自定义菜单项样式 */
.custom-menu-item {
  height: 48px !important;
  line-height: 48px !important;
  margin: 4px 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
  position: relative;
}

.custom-menu-item:hover {
  background-color: rgba(52, 152, 219, 0.1) !important;
  color: #3498db !important;
}

.custom-menu-item.is-active {
  background-color: #3498db !important;
  color: #ffffff !important;
  box-shadow: 0 2px 8px rgba(52, 152, 219, 0.3);
}

.custom-sub-menu .el-sub-menu__title {
  height: 48px !important;
  line-height: 48px !important;
  margin: 4px 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
  color: #ecf0f1 !important;
}

.custom-sub-menu .el-sub-menu__title:hover {
  background-color: rgba(52, 152, 219, 0.1) !important;
  color: #3498db !important;
}

.custom-sub-menu-item {
  height: 40px !important;
  line-height: 40px !important;
  margin: 2px 16px;
  border-radius: 6px;
  background-color: rgba(52, 73, 94, 0.3);
  transition: all 0.3s ease;
}

.custom-sub-menu-item:hover {
  background-color: rgba(52, 152, 219, 0.2) !important;
  color: #3498db !important;
}

.custom-sub-menu-item.is-active {
  background-color: #3498db !important;
  color: #ffffff !important;
}

/* 菜单图标和文字样式 */
.menu-icon {
  font-size: 18px;
  width: 18px;
  height: 18px;
  margin-right: 12px;
  flex-shrink: 0;
}

.menu-text {
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

/* 折叠状态下的样式 */
.sidebar-menu.el-menu--collapse {
  width: 64px;
}

.sidebar-menu.el-menu--collapse .custom-menu-item,
.sidebar-menu.el-menu--collapse .custom-sub-menu .el-sub-menu__title {
  margin: 4px 8px;
  padding: 0 !important;
  text-align: center;
  justify-content: center;
}

.sidebar-menu.el-menu--collapse .menu-icon {
  margin-right: 0;
  font-size: 20px;
}

.sidebar-menu.el-menu--collapse .menu-text {
  display: none;
}

.sidebar-menu.el-menu--collapse .custom-sub-menu .el-menu-item {
  display: none;
}

.sidebar-menu.el-menu--collapse .el-sub-menu__icon-arrow {
  display: none;
}

/* 顶部头部样式 */
.app-header {
  background-color: #ffffff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 24px;
}

.header-left {
  flex: 1;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.username {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* 主内容区域样式 */
.app-main {
  padding: 24px;
  background-color: #f8f9fa;
  height: calc(100vh - 60px);
  overflow-y: auto;
  transition: margin-left 0.3s ease;
}

/* 遮罩层样式 */
.sidebar-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.3);
  z-index: 1000;
  transition: opacity 0.3s ease;
}

/* Element Plus 组件的某些全局覆盖 */
.el-card__header {
    font-weight: bold;
}

/* 滚动条样式优化 */
.sidebar-menu::-webkit-scrollbar {
  width: 4px;
}

.sidebar-menu::-webkit-scrollbar-track {
  background: transparent;
}

.sidebar-menu::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
}

.sidebar-menu::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .app-aside {
    position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    z-index: 1001;
  }
  
  .app-main {
    margin-left: 0 !important;
  }
  
  .header-content {
    padding: 0 16px;
  }
  
  .username {
    display: none;
  }
}
</style> 