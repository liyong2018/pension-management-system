<template>
  <el-container id="app-container" style="height: 100vh;">
    <el-header class="app-header">
      <div class="logo-title">养老信息管理系统</div>
      <el-menu
        mode="horizontal"
        :default-active="activeIndex"
        router
        background-color="#545c64"
        text-color="#fff"
        text-align="center"
        active-text-color="#ffd04b"
        v-loading="menuLoading"
      >
        <!-- 动态渲染菜单 -->
        <template v-for="menu in visibleMenus" :key="menu.id">
          <!-- 普通菜单项 -->
          <el-menu-item 
            v-if="menu.type === 'MENU' && (!menu.children || menu.children.length === 0)"
            :index="menu.routePath"
            :disabled="!menu.status"
          >
            <el-icon v-if="menu.icon" style="margin-right: 8px;">
              <component :is="getIconComponent(menu.icon)" />
            </el-icon>
            {{ menu.name }}
          </el-menu-item>
          
          <!-- 子菜单 -->
          <el-sub-menu 
            v-else-if="menu.type === 'CATALOG' && menu.children && menu.children.length > 0"
            :index="menu.routePath || menu.permissionKey"
            :disabled="!menu.status"
          >
            <template #title>
              <el-icon v-if="menu.icon" style="margin-right: 8px;">
                <component :is="getIconComponent(menu.icon)" />
              </el-icon>
              {{ menu.name }}
            </template>
            
            <!-- 递归渲染子菜单 -->
            <template v-for="child in menu.children" :key="child.id">
              <el-menu-item 
                v-if="child.isVisible && child.status && child.type === 'MENU'"
                :index="child.routePath"
                :disabled="!child.status"
              >
                <el-icon v-if="child.icon" style="margin-right: 8px;">
                  <component :is="getIconComponent(child.icon)" />
                </el-icon>
                {{ child.name }}
              </el-menu-item>
            </template>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-header>
    <el-main class="app-main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { 
  House, OfficeBuilding, User, Monitor, Warning, Avatar, 
  Setting, Key, Collection, Document, Menu 
} from '@element-plus/icons-vue';

const route = useRoute();
const activeIndex = ref(route.path);
const menuLoading = ref(false);
const menuData = ref([]);

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

// 组件挂载时加载菜单数据
onMounted(() => {
  console.log('🚀 App组件挂载，开始初始化菜单...');
  
  // 清除所有可能的缓存
  localStorage.clear();
  sessionStorage.clear();
  
  // 强制刷新菜单数据
  loadMenuData();
  
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

.app-header {
  display: flex;
  align-items: center; /* 垂直居中导航项 */
  background-color: #545c64;
  padding: 0 20px; /* 移除默认的左右 padding，让 menu 占满 */
  justify-content: space-between; /* 让标题和菜单分布在两端 */
}

.logo-title {
  color: #fff;
  font-size: 20px;
  font-weight: bold;
  margin-right: 40px; /* Logo 和菜单之间的距离 */
  flex-shrink: 0; /* 防止标题被压缩 */
}

/* Element Plus 菜单样式调整 */
.el-header .el-menu {
  border-bottom: none; /* 移除菜单底部的边框 */
  flex-grow: 1; /* 让菜单占据剩余空间 */
  justify-content: center; /* 菜单项居中显示 */
}

.app-main {
  padding: 20px;
  background-color: #f4f5f7; /* 给主内容区一个背景色 */
  height: calc(100vh - 60px); /* 减去header的高度 */
  overflow-y: auto; /* 如果内容超出则显示滚动条 */
}

/* Element Plus 组件的某些全局覆盖 (谨慎使用) */
.el-card__header {
    font-weight: bold;
}
</style> 