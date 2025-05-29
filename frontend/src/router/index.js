import { createRouter, createWebHistory } from 'vue-router';
// 尝试查找 HomeView.vue，如果不存在，则需要创建一个简单的占位组件或实际的首页组件
// import HomeView from '../views/HomeView.vue'; 
import LoginView from '../views/LoginView.vue'; // Import LoginView

// 导入首页组件
const HomeView = () => import('../views/Dashboard.vue');

const routes = [
  {
    path: '/login', // Add login route
    name: 'Login',
    component: LoginView,
    meta: { title: '用户登录' }
  },
  {
    path: '/',
    name: 'Home',
    component: HomeView,
    meta: { title: '首页' }
  },
  // {
  //   path: '/about',
  //   name: 'About',
  //   // route level code-splitting
  //   // this generates a separate chunk (About.[hash].js) for this route
  //   // which is lazy-loaded when the route is visited.
  //   component: () => import('../views/AboutView.vue') // 示例：懒加载
  // },
  {
    path: '/organization-management',
    name: 'OrganizationManagement',
    component: () => import('@/views/organization/OrganizationIndex.vue'), // 懒加载机构管理视图
    meta: { title: '机构管理', requiresAuth: true } // 示例：添加路由元信息，如标题和是否需要认证
  },
  {
    path: '/elderly-profiles',
    name: 'ElderlyProfiles',
    component: () => import('@/views/elderly/ElderlyProfileList.vue'),
    meta: { title: '人员档案', requiresAuth: true }
  },
  // 智能设备模块路由
  {
    path: '/smart-devices',
    name: 'SmartDevices',
    component: () => import('@/views/smart-device/SmartDeviceList.vue'),
    meta: { title: '智能设备管理', requiresAuth: true }
  },
  {
    path: '/device-alarms',
    name: 'DeviceAlarms',
    component: () => import('@/views/smart-device/DeviceAlarmList.vue'),
    meta: { title: '设备告警管理', requiresAuth: true }
  },
  // 服务记录模块路由
  {
    path: '/service-records',
    name: 'ServiceRecords',
    component: () => import('@/views/service-record/ServiceRecordList.vue'),
    meta: { title: '服务记录管理', requiresAuth: true }
  },
  // 志愿者管理模块路由
  {
    path: '/volunteers',
    name: 'Volunteers',
    component: () => import('@/views/volunteer/VolunteerListSimple.vue'),
    meta: { title: '志愿者管理', requiresAuth: true }
  },
  // 系统管理模块路由
  {
    path: '/system',
    name: 'SystemManagement',
    component: () => import('@/views/system/SystemManagement.vue'),
    meta: { title: '系统管理', requiresAuth: true }
  },
  {
    path: '/system/users',
    name: 'SystemUsers',
    component: () => import('@/views/system/SystemUserList.vue'),
    meta: { title: '用户管理', requiresAuth: true }
  },
  {
    path: '/system/roles',
    name: 'SystemRoles',
    component: () => import('@/views/system/RoleList.vue'),
    meta: { title: '角色管理', requiresAuth: true }
  },
  {
    path: '/system/permissions',
    name: 'SystemPermissions',
    component: () => import('@/views/system/PermissionList.vue'),
    meta: { title: '权限管理', requiresAuth: true }
  },
  {
    path: '/system/menus',
    name: 'SystemMenus',
    component: () => import('@/views/system/MenuList.vue'),
    meta: { title: '菜单管理', requiresAuth: true }
  },
  {
    path: '/system/logs',
    name: 'SystemLogs',
    component: () => import('@/views/system/LogList.vue'),
    meta: { title: '日志管理', requiresAuth: true }
  },
  {
    path: '/system/dictionaries',
    name: 'SystemDictionaries',
    component: () => import('@/views/system/DictionaryManagement.vue'),
    meta: { title: '字典管理', requiresAuth: true }
  },
  {
    path: '/system/dictionary-test',
    name: 'DictionaryTest',
    component: () => import('@/views/system/DictionaryTest.vue'),
    meta: { title: '字典管理测试', requiresAuth: true }
  },
  {
    path: '/system/dictionary-diagnosis',
    name: 'DictionaryDiagnosis',
    component: () => import('@/views/system/DictionaryDiagnosis.vue'),
    meta: { title: '字典问题诊断', requiresAuth: true }
  },
  {
    path: '/system/api-test',
    name: 'ApiTest',
    component: () => import('@/views/system/ApiTest.vue'),
    meta: { title: '接口测试', requiresAuth: true }
  }
  // ... 其他模块的路由
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL), // 使用 Vite 的环境变量
  routes
});

router.beforeEach((to, from, next) => {
  // 路由守卫示例：设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 养老信息管理系统`;
  }

  const isAuthenticated = !!localStorage.getItem('authToken');
  console.log('🔒 路由守卫检查认证状态:', isAuthenticated);
  console.log('🎯 目标路由:', to.path);

  // 如果用户未认证且不是去登录页
  if (!isAuthenticated && to.path !== '/login') {
    console.log('⚠️ 未认证，重定向到登录页');
    next({ path: '/login' });
    return;
  }

  // 如果用户已认证且尝试访问登录页
  if (isAuthenticated && to.path === '/login') {
    console.log('✅ 已认证，重定向到首页');
    next({ path: '/' });
    return;
  }

  // 其他情况正常放行
  next();
});

export default router; 