<template>
  <!-- Conditionally render login page or main app layout -->
  <router-view v-if="isAuthRoute" />
  <el-container v-else id="app-container" style="height: 100vh;" :class="{'el-aside-collapsed': isCollapsed}">
    <!-- 左侧边栏 -->
    <el-aside class="app-aside" :width="isCollapsed ? '64px' : '250px'">
      <!-- 系统标题区域 -->
      <div class="logo-section" @click="goHome">
        <div class="logo-title" v-show="!isCollapsed">
          <div class="logo-icon">🏥</div>
          <div class="title-text">养老信息管理系统</div>
        </div>
        <div class="logo-collapsed" v-show="isCollapsed">
          <div class="logo-icon-small">🏥</div>
        </div>
        <!-- 折叠按钮 -->
        <div class="collapse-trigger" @click.stop="toggleCollapse">
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
        @select="handleMenuSelect"
      >
        <!-- 动态渲染菜单 -->
        <template v-for="(menu, index) in visibleMenus" :key="menu.id">
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
            :index="menu.routePath || menu.permissionKey || `sub-menu-${index}`" 
            :disabled="!menu.status"
            class="custom-sub-menu"
            :popper-class="'dark-theme-popper'"
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
              <template v-for="(item, index) in breadcrumbItems" :key="index">
                <span 
                  v-if="index < breadcrumbItems.length - 1" 
                  class="breadcrumb-item clickable"
                  @click="navigateTo(item.path)"
                >
                  {{ item.name }}
                </span>
                <span v-else class="breadcrumb-item current">{{ item.name }}</span>
                <el-icon v-if="index < breadcrumbItems.length - 1" class="breadcrumb-separator">
                  <ArrowRight />
                </el-icon>
              </template>
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
import { ref, watch, onMounted, computed, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { 
  House, OfficeBuilding, User, Monitor, Warning, Avatar, 
  Setting, Key, Collection, Document, Menu, ArrowRight, ArrowLeft,
  Location, ArrowDown
} from '@element-plus/icons-vue';
import request from '@/utils/request';

const router = useRouter();
const route = useRoute();
const activeIndex = ref(route.path);
const menuLoading = ref(false);
const menuData = ref([]);
const isCollapsed = ref(true); // 默认收起状态
const showMask = ref(false);

// 添加token监听
const checkToken = () => {
  const token = localStorage.getItem('authToken');
  if (!token && route.path !== '/login') {
    console.log('🔒 检测到token不存在，跳转到登录页面');
    router.push('/login');
  }
};

// 监听localStorage中token的变化
window.addEventListener('storage', (e) => {
  if (e.key === 'authToken') {
    checkToken();
  }
});

// 定期检查token
const tokenCheckInterval = setInterval(checkToken, 1000);

// 组件卸载时清除定时器
onUnmounted(() => {
  clearInterval(tokenCheckInterval);
});

// Computed property to check if the current route is an authentication route
const isAuthRoute = computed(() => {
  return route.name === 'Login'; // Add other auth route names if needed, e.g., 'Register', 'ForgotPassword'
});

// 计算面包屑导航路径
const breadcrumbItems = computed(() => {
  const path = route.path;
  const items = [];
  
  // 根据路由路径生成面包屑
  if (path === '/') {
    items.push({ name: '首页', path: '/' });
  } else if (path.startsWith('/system')) {
    items.push({ name: '首页', path: '/' });
    items.push({ name: '系统管理', path: '/system' });
    
    // 根据具体的系统管理子页面添加对应的面包屑
    if (path === '/system/users') {
      items.push({ name: '用户管理', path: '/system/users' });
    } else if (path === '/system/roles') {
      items.push({ name: '角色管理', path: '/system/roles' });
    } else if (path === '/system/permissions') {
      items.push({ name: '权限管理', path: '/system/permissions' });
    } else if (path === '/system/menus') {
      items.push({ name: '菜单管理', path: '/system/menus' });
    } else if (path === '/system/logs') {
      items.push({ name: '日志管理', path: '/system/logs' });
    } else if (path === '/system/dictionaries') {
      items.push({ name: '字典管理', path: '/system/dictionaries' });
    } else if (path === '/system/dictionary-test') {
      items.push({ name: '字典管理测试', path: '/system/dictionary-test' });
    } else if (path === '/system/dictionary-diagnosis') {
      items.push({ name: '字典问题诊断', path: '/system/dictionary-diagnosis' });
    }
  } else if (path.startsWith('/smart-device') || path === '/smart-devices' || path === '/device-alarms') {
    items.push({ name: '首页', path: '/' });
    items.push({ name: '智能设备', path: '#' });
    
    if (path === '/smart-devices') {
      items.push({ name: '设备管理', path: '/smart-devices' });
    } else if (path === '/device-alarms') {
      items.push({ name: '告警管理', path: '/device-alarms' });
    }
  } else {
    // 其他单级页面
    items.push({ name: '首页', path: '/' });
    const title = route.meta.title || route.name || '未知页面';
    items.push({ name: title, path: path });
  }
  
  return items;
});

// 计算当前路由名称用于面包屑（保持兼容性）
const currentRouteName = computed(() => {
  return route.meta.title || route.name || '首页';
});

// 点击Logo返回首页
const goHome = () => {
  router.push('/');
};

// 导航方法
const navigateTo = (path) => {
  if (path && path !== '#') {
    router.push(path);
  }
};

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
    console.log('🌐 请求URL: /api/permissions/user-menu-tree');
    console.log('🔑 当前Token:', localStorage.getItem('authToken'));
    
    const response = await request.get('/permissions/user-menu-tree');
    console.log('📊 菜单API响应数据:', response);
    
    if (response && Array.isArray(response)) {
      menuData.value = response;
      console.log('✅ 菜单加载成功，数量:', response.length);
    } else {
      console.error('❌ 菜单数据格式不正确:', response);
      ElMessage.error('菜单数据格式不正确');
      menuData.value = getDefaultMenus(); // 使用默认菜单作为后备
    }
  } catch (error) {
    console.error('❌ 加载菜单失败:', error);
    console.error('❌ 错误详情:', error.response);
    
    if (error.response?.status === 403) {
      console.log('🔒 权限被拒绝，检查token:', localStorage.getItem('authToken'));
      ElMessage.error('没有权限访问菜单，请重新登录');
      localStorage.removeItem('authToken');
      router.push('/login');
    } else if (error.response?.status === 401) {
      console.log('🔑 未授权，需要重新登录');
      ElMessage.error('登录已过期，请重新登录');
      localStorage.removeItem('authToken');
      router.push('/login');
    } else {
      console.error('❌ 其他错误:', error.message);
      ElMessage.error('加载菜单失败: ' + (error.message || '未知错误'));
      menuData.value = getDefaultMenus(); // 使用默认菜单作为后备
    }
  } finally {
    menuLoading.value = false;
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
  
  // 如果从登录页面跳转到其他页面，并且有token，则刷新菜单
  if (newPath !== '/login' && localStorage.getItem('authToken') && menuData.value.length === 0) {
    console.log('📍 路由变化检测到需要刷新菜单');
    loadMenuData();
  }
});

// 处理菜单选择事件
const handleMenuSelect = (index, indexPath, item) => {
  // console.log('Menu selected:', index, indexPath, item);
  // 在移动端或小屏幕时，点击菜单项后自动收起
  if (window.innerWidth <= 768 && !isCollapsed.value) {
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
  console.log('🌐 当前路由:', route.path);
  console.log('🔑 当前Token:', localStorage.getItem('authToken') ? '存在' : '不存在');
  
  // 只有在非登录页面且有token时才加载菜单
  if (route.path !== '/login' && localStorage.getItem('authToken')) {
    loadMenuData();
  } else {
    console.log('⏭️ 跳过菜单加载：在登录页面或无token');
  }
  
  // 添加窗口大小变化监听
  window.addEventListener('resize', handleResize);
  
  // 添加页面可见性变化监听，确保页面重新激活时刷新菜单
  document.addEventListener('visibilitychange', () => {
    if (!document.hidden && route.path !== '/login' && localStorage.getItem('authToken')) {
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
  overflow: hidden !important; /* 强制隐藏侧边栏本身的滚动条 */
}

/* 系统标题区域样式 */
.logo-section {
  display: flex !important;
  align-items: center !important;
  justify-content: space-between !important;
  height: 60px !important; /* 固定高度 */
  padding: 0 16px !important; /* 调整内边距以适应固定高度 */
  box-sizing: border-box !important; /* 重要：确保padding和border不增加总高度 */
  border-bottom: 1px solid #34495e !important;
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%) !important;
  transition: all 0.3s ease !important;
  position: relative !important;
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
  border-right: none !important;
  height: calc(100vh - 60px) !important; /* 匹配 logo-section 的固定高度 */
  overflow-y: hidden !important; /* 强制隐藏菜单内部的垂直滚动条 */
  transition: all 0.3s ease !important;
  background-color: transparent !important;
  scrollbar-width: none !important; /* Firefox */
  -ms-overflow-style: none !important;  /* IE 10+ */
}

.sidebar-menu::-webkit-scrollbar {
  display: none !important; /* Chrome, Safari, Opera - 强制隐藏 */
}

/* 自定义菜单项样式（主菜单和子菜单通用）*/
.custom-menu-item,
.custom-sub-menu .el-sub-menu__title {
  padding: 0 16px !important; 
  color: #ecf0f1 !important; 
  height: 48px !important;
  line-height: 48px !important;
  margin: 4px 8px !important;
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
  position: relative !important;
  display: flex !important;
  align-items: center !important;
}

.custom-sub-menu-item { /* 展开状态下的子菜单项 */
  height: 44px !important; 
  line-height: 44px !important;
  margin: 2px 8px 2px 24px !important; 
  border-radius: 6px !important;
  padding: 0 16px !important; /* 与主菜单项的文字区域padding一致 */
  color: #bdc3c7 !important; 
  background-color: transparent !important; 
  display: flex !important;
  align-items: center !important;
  position: relative !important;
  transition: all 0.3s ease !important;
}

.custom-menu-item:hover,
.custom-sub-menu .el-sub-menu__title:hover,
.custom-sub-menu-item:hover {
  background-color: rgba(52, 152, 219, 0.15) !important;
  color: #5dade2 !important; 
}

.custom-menu-item.is-active,
.custom-sub-menu.is-active .el-sub-menu__title {
  background-color: #3498db !important;
  color: #ffffff !important;
  box-shadow: 0 3px 10px rgba(52, 152, 219, 0.4) !important;
}

.custom-sub-menu-item.is-active {
  background-color: #3498db !important; /* 子菜单激活时也用主色 */
  color: #ffffff !important; 
  box-shadow: none !important;
}

.custom-sub-menu.is-opened .el-sub-menu__title {
  background-color: rgba(52, 152, 219, 0.1) !important; 
  color: #5dade2 !important; 
}


/* 菜单图标和文字样式 */
.menu-icon {
  font-size: 18px !important;
  width: 20px !important; /* 图标容器宽度 */
  text-align: center !important; /* 图标居中 */
  margin-right: 10px !important;
  flex-shrink: 0 !important;
}

.menu-text {
  font-size: 14px !important;
  font-weight: 500 !important;
  transition: opacity 0.3s ease, max-width 0.3s ease !important;
  white-space: nowrap !important;
  overflow: hidden !important;
  text-overflow: ellipsis !important;
  opacity: 1;
  max-width: 120px; /* 根据需要调整，确保不换行 */
}

/* 隐藏Element Plus自带的展开/收起箭头 */
.custom-sub-menu .el-sub-menu__icon-arrow {
  display: none !important;
}

/* 折叠状态下的样式 */
.sidebar-menu.el-menu--collapse {
  width: 64px !important;
}

.sidebar-menu.el-menu--collapse .custom-menu-item,
.sidebar-menu.el-menu--collapse .custom-sub-menu .el-sub-menu__title {
  margin: 4px 8px !important;
  padding: 0 !important;
  justify-content: center !important;
}

.sidebar-menu.el-menu--collapse .menu-icon {
  margin-right: 0 !important;
  font-size: 20px !important;
}

.sidebar-menu.el-menu--collapse .menu-text {
  opacity: 0;
  max-width: 0;
  display: none; /* 强制隐藏 */
}

/* 折叠时弹出的子菜单容器样式 (el-menu-popper) - 使用自定义popper-class */
.dark-theme-popper { /* 定位我们自定义的popper类 */
  background-color: #2c3e50 !important;      /* 深色背景 */
  border: 1px solid #34495e !important;       /* 边框颜色协调 */
  border-radius: 8px !important;              /* 圆角与主菜单一致 */
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.35) !important; /* 更柔和自然的阴影 */
  padding: 8px !important;                      /* 容器内边距，给内部菜单项留出空间 */
}

/* 确保Popper内部的el-menu本身背景透明，无额外边框和内边距 */
.dark-theme-popper .el-menu {
  background-color: transparent !important;
  border: none !important;
  padding: 0 !important;
}

/* 折叠时弹出的子菜单项样式 - 继承主菜单风格 */
.dark-theme-popper .custom-sub-menu-item {
  display: flex !important;
  align-items: center !important;
  width: auto !important; /* 宽度自动，由内容和容器padding决定 */
  height: 44px !important; /* 高度可略小于主菜单项，或保持48px */
  line-height: 44px !important;
  padding: 0 12px !important; /* 内部左右边距 */
  margin: 2px 0 !important;   /* 上下外边距，控制项目间距 */
  color: #ecf0f1 !important; /* 默认浅色文字 */
  border-radius: 6px !important; /* 项目圆角 */
  background-color: transparent !important; /* 项目默认背景透明 */
  transition: background-color 0.2s ease, color 0.2s ease !important;
}

.dark-theme-popper .custom-sub-menu-item .menu-icon {
  color: #ecf0f1 !important; /* 图标颜色与文字一致 */
  margin-right: 10px !important;
  font-size: 16px !important; /* 图标大小 */
  flex-shrink: 0;
}

.dark-theme-popper .custom-sub-menu-item .menu-text {
  font-size: 14px !important; /* 文字大小 */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 弹出子菜单项的 hover 状态 */
.dark-theme-popper .custom-sub-menu-item:not(.is-disabled):hover {
  background-color: rgba(52, 152, 219, 0.25) !important; /* 悬停背景色，更明显一些 */
  color: #ffffff !important; /* 悬停文字颜色提亮 */
}

.dark-theme-popper .custom-sub-menu-item:not(.is-disabled):hover .menu-icon {
  color: #ffffff !important; /* 悬停图标颜色随文字提亮 */
}

/* 弹出子菜单项的 active 状态 */
.dark-theme-popper .custom-sub-menu-item.is-active {
  background-color: #3498db !important; /* 激活背景色 - 主题蓝 */
  color: #ffffff !important;           /* 激活文字颜色 - 白色 */
  font-weight: 500 !important;          /* 激活文字略加粗 */
  box-shadow: 0 2px 8px rgba(52, 152, 219, 0.3) !important; /* 激活时细微阴影 */
}

.dark-theme-popper .custom-sub-menu-item.is-active .menu-icon {
  color: #ffffff !important; /* 激活图标颜色 - 白色 */
}

/* 如果Element Plus在popper的el-menu上使用is-light或类似的主题类，尝试覆盖 */
/* 注意：当使用popper-class时，Element Plus通常不会再给popper添加 is-light 这类全局主题类， */
/* 但保留以下规则以防万一，或者如果popper-class没有完全覆盖所有情况 */
.dark-theme-popper.is-light { /* 理论上这种情况会比较少见 */
    background-color: #2c3e50 !important; 
    border-color: #34495e !important;
}
.dark-theme-popper.is-light .el-menu {
    background-color: transparent !important;
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

.breadcrumb-item {
  color: #666;
  transition: color 0.2s ease;
}

.breadcrumb-item.clickable {
  cursor: pointer;
  color: #409eff;
}

.breadcrumb-item.clickable:hover {
  color: #66b1ff;
}

.breadcrumb-item.current {
  color: #303133;
  font-weight: 500;
}

.breadcrumb-separator {
  color: #c0c4cc;
  font-size: 12px;
  margin: 0 4px;
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
  transition: all 0.3s ease;
  margin-left: 64px;
  width: calc(100% - 64px - 48px); /* 减去收起的侧边栏宽度和padding */
}

/* 当侧边栏展开时的主内容区域样式 */
.el-container:not(.el-aside-collapsed) .app-main {
  margin-left: 20px;
  width: calc(100% - 20px - 48px); /* 减去侧边栏宽度和padding */
}

/* 图表容器通用样式 */
.chart-container {
  width: 100%;
  height: 100%;
  transition: all 0.3s ease;
}

/* el-dialog样式覆盖 */
:deep(.el-dialog) {
  max-width: calc(100vw - 48px);
  margin: 0 auto;
  transition: all 0.3s ease;
}

:deep(.el-dialog.is-fullscreen) {
  width: 100vw !important;
  max-width: 100vw !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .app-main {
    margin-left: 0 !important;
    width: 100% !important;
  }
  
  .el-container:not(.el-aside-collapsed) .app-main,
  .el-container.el-aside-collapsed .app-main {
    margin-left: 0;
    width: 100%;
  }
  
  :deep(.el-dialog) {
    width: 90% !important;
    max-width: none !important;
  }
  
  .chart-container {
    width: 100% !important;
  }
}

/* 侧边栏过渡动画 */
.el-aside {
  transition: width 0.3s ease;
}

/* 内容区域过渡动画 */
.el-container {
  transition: margin-left 0.3s ease;
}

/* 图表容器过渡动画 */
.chart-wrapper {
  transition: all 0.3s ease;
  width: 100%;
  height: 100%;
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
</style> 