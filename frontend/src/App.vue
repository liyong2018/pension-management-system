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
        active-text-color="#ffd04b"
      >
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/organization-management">机构管理</el-menu-item>
        <el-menu-item index="/elderly-profiles">人员档案</el-menu-item>
        <el-sub-menu index="smart-device">
          <template #title>智能设备</template>
          <el-menu-item index="/smart-devices">设备管理</el-menu-item>
          <el-menu-item index="/device-alarms">告警管理</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/service-records">服务记录</el-menu-item>
        <!-- 后续其他模块的导航链接可以加在这里 -->
      </el-menu>
    </el-header>
    <el-main class="app-main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const activeIndex = ref(route.path);

// 监听路由变化，更新导航菜单的激活状态
watch(() => route.path, (newPath) => {
  activeIndex.value = newPath;
});

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
}

.logo-title {
  color: #fff;
  font-size: 20px;
  font-weight: bold;
  margin-right: 40px; /* Logo 和菜单之间的距离 */
}

/* Element Plus 菜单样式调整 */
.el-header .el-menu {
  border-bottom: none; /* 移除菜单底部的边框 */
  flex-grow: 1; /* 让菜单占据剩余空间 */
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