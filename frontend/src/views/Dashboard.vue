<template>
  <div class="smart-dashboard">
    <!-- 顶部标题栏 -->
    <div class="dashboard-header">
      <div class="header-left">
        <div class="current-time">{{ currentTime }}</div>
      </div>
      <div class="header-center">
        <h1 class="system-title">银龄智慧养老服务系统</h1>
      </div>
      <div class="header-right">
        <div class="weather-info">
          <span class="weather-icon">{{ weatherData.icon }}</span>
          <span class="weather-text">{{ weatherData.text }}</span>
        </div>
      </div>
    </div>

    <!-- 背景地图 -->
    <div class="background-map">
      <div ref="mapContainer" id="leafletMap" class="leaflet-map"></div>
    </div>

    <!-- Draggable Panels -->
    <div
      v-for="panel in draggablePanels"
      :key="panel.id"
      class="dashboard-card draggable-panel"
      :style="{ top: panel.top + 'px', left: panel.left + 'px', width: panel.width + 'px', height: panel.height + 'px' }"
      :data-panel-id="panel.id"
    >
      <div class="card-header" @mousedown="onDragStart($event, panel.id)">
        <h3>{{ panel.title }}</h3>
        <div v-if="panel.id === 'alarms'" class="alarm-controls">
          <span class="alarm-count">{{ formatNumber(dashboardData?.alarmStats?.unhandledCount) }}条</span>
          <button class="control-btn" @click="loadAlarmData">🔄</button>
        </div>
      </div>
      <div class="card-content" :style="{ height: `calc(100% - 53px)` }">
        <!-- Elderly Type Chart -->
        <div v-if="panel.id === 'elderlyType'" :ref="el => setPanelContentRef(el, 'elderlyType')" class="chart-container" style="height: 100%;"></div>
        
        <!-- Ability Assessment -->
        <div v-if="panel.id === 'ability'" class="ability-stats" style="height: 100%; overflow-y: auto;">
            <div class="ability-item">
              <div class="ability-bar">
                <div class="bar-fill green" :style="{ width: getAbilityPercentage('能力完好') + '%' }"></div>
              </div>
              <div class="ability-label">能力完好</div>
              <div class="ability-value">{{ getAbilityCount('能力完好') }}人</div>
            </div>
            <div class="ability-item">
              <div class="ability-bar">
                <div class="bar-fill blue" :style="{ width: getAbilityPercentage('轻度失能') + '%' }"></div>
              </div>
              <div class="ability-label">轻度失能</div>
              <div class="ability-value">{{ getAbilityCount('轻度失能') }}人</div>
            </div>
            <div class="ability-item">
              <div class="ability-bar">
                <div class="bar-fill orange" :style="{ width: getAbilityPercentage('中度失能') + '%' }"></div>
              </div>
              <div class="ability-label">中度失能</div>
              <div class="ability-value">{{ getAbilityCount('中度失能') }}人</div>
            </div>
            <div class="ability-item">
              <div class="ability-bar">
                <div class="bar-fill red" :style="{ width: getAbilityPercentage('重度失能') + '%' }"></div>
              </div>
              <div class="ability-label">重度失能</div>
              <div class="ability-value">{{ getAbilityCount('重度失能') }}人</div>
            </div>
        </div>

        <!-- Device Status Chart -->
        <div v-if="panel.id === 'deviceStatus'" :ref="el => setPanelContentRef(el, 'deviceStatus')" class="chart-container" style="height: 100%;"></div>
        
        <!-- Unhandled Alarms -->
        <div v-if="panel.id === 'alarms'" class="alarm-list alarm-list-auto" v-loading="alarmLoading" style="height: 100%;">
            <div v-for="alarm in alarmList" :key="alarm.time" class="alarm-item">
              <div class="alarm-type" :class="getAlarmLevelClass(alarm.level)">
                {{ alarm.type }}
              </div>
              <div class="alarm-content">
                <div class="alarm-location">{{ alarm.location }}</div>
                <div class="alarm-time">{{ alarm.time }}</div>
              </div>
            </div>
            <div v-if="!alarmList.length && !alarmLoading" class="no-alarm">
              <div class="no-alarm-icon">✅</div>
              <div class="no-alarm-text">暂无未处理告警</div>
              <div class="no-alarm-desc">系统运行正常</div>
            </div>
        </div>
      </div>
      
      <!-- Resize Handles -->
      <div class="resizable-handle resizable-handle-l" @mousedown.stop="onResizeStart($event, panel.id, 'left')"></div>
      <div class="resizable-handle resizable-handle-r" @mousedown.stop="onResizeStart($event, panel.id, 'right')"></div>
      <div class="resizable-handle resizable-handle-t" @mousedown.stop="onResizeStart($event, panel.id, 'top')"></div>
      <div class="resizable-handle resizable-handle-b" @mousedown.stop="onResizeStart($event, panel.id, 'bottom')"></div>
      <div class="resizable-handle resizable-handle-tl" @mousedown.stop="onResizeStart($event, panel.id, 'top-left')"></div>
      <div class="resizable-handle resizable-handle-tr" @mousedown.stop="onResizeStart($event, panel.id, 'top-right')"></div>
      <div class="resizable-handle resizable-handle-bl" @mousedown.stop="onResizeStart($event, panel.id, 'bottom-left')"></div>
      <div class="resizable-handle resizable-handle-br" @mousedown.stop="onResizeStart($event, panel.id, 'bottom-right')"></div>
    </div>

    <!-- Top Stats Cards (Non-draggable or differently handled) -->
    <div class="top-stats" ref="topStatsRef">
      <div class="stat-card blue">
        <div class="stat-icon">👥</div>
        <div class="stat-content">
          <div class="stat-value">{{ formatNumber(dashboardData?.elderlyStats?.totalCount) }}</div>
          <div class="stat-label">老龄人口</div>
        </div>
      </div>
      <div class="stat-card orange">
        <div class="stat-icon">🏢</div>
        <div class="stat-content">
          <div class="stat-value">{{ formatNumber(dashboardData?.facilityStats?.totalCount) }}</div>
          <div class="stat-label">养老机构</div>
        </div>
      </div>
      <div class="stat-card purple">
        <div class="stat-icon">👨‍⚕️</div>
        <div class="stat-content">
          <div class="stat-value">{{ formatNumber(dashboardData?.staffStats?.totalCount) }}</div>
          <div class="stat-label">从业人员</div>
        </div>
      </div>
      <div class="stat-card green">
        <div class="stat-icon">💰</div>
        <div class="stat-content">
          <div class="stat-value">{{ formatMoney(dashboardData?.subsidyStats?.totalAmount) }}</div>
          <div class="stat-label">发放补贴</div>
        </div>
      </div>
    </div>
    
    <!-- Legend Panel -->
    <div class="legend-panel">
      <div class="legend-title">图例</div>
      <div class="legend-items">
        <div class="legend-item">
          <span class="legend-marker blue"></span>
          <span>社区</span>
        </div>
        <div class="legend-item">
          <span class="legend-marker orange"></span>
          <span>机构</span>
        </div>
        <div class="legend-item">
          <span class="legend-marker red"></span>
          <span>告警</span>
        </div>
      </div>
    </div>

    <!-- Bottom Controls -->
    <div class="bottom-controls">
      <!-- Map Controls -->
      <div class="map-controls">
        <button class="control-icon-btn" @click="toggleLayer('communities')" :class="{ active: showCommunities }" title="社区">
          🏘️
        </button>
        <button class="control-icon-btn" @click="toggleLayer('organizations')" :class="{ active: showOrganizations }" title="机构">
          🏢
        </button>
        <button class="control-icon-btn" @click="toggleLayer('alarms')" :class="{ active: showAlarms }" title="告警">
          🚨
        </button>
        <button class="control-icon-btn" @click="resetMapView" title="重置">
          🎯
        </button>
        <button class="control-icon-btn" @click="restoreLayout" title="还原布局">
          🔄
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted, computed, inject, watch } from 'vue';
import * as echarts from 'echarts';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import request from '@/utils/request';
import { ElMessage } from 'element-plus';

const isCollapsed = inject('isCollapsed', ref(false)); // a default value is provided

// Draggable Panels State
const draggablePanels = ref([]);

const getDefaultPanelsLayout = () => {
  const sidebarWidth = isCollapsed.value ? 64 : 250;
  return [
    { id: 'elderlyType', title: '老人类型分析', top: 120, left: sidebarWidth + 10, width: 280, height: 260 },
    { id: 'ability', title: '能力评估', top: 400, left: sidebarWidth + 10, width: 280, height: 260 },
    { id: 'deviceStatus', title: '设备状态概览', top: 120, left: window.innerWidth - 300, width: 280, height: 300 },
    { id: 'alarms', title: '未处理告警', top: 440, left: window.innerWidth - 300, width: 280, height: 300 },
  ];
};

watch(isCollapsed, (isNowCollapsed, wasPreviouslyCollapsed) => {
  const oldSidebarWidth = wasPreviouslyCollapsed ? 64 : 250;
  const newSidebarWidth = isNowCollapsed ? 64 : 250;
  const widthDifference = newSidebarWidth - oldSidebarWidth;

  if (widthDifference === 0) return;

  const leftPanelIds = ['elderlyType', 'ability'];

  draggablePanels.value.forEach(panel => {
    if (leftPanelIds.includes(panel.id)) {
      // Only adjust panels that are positioned relative to the sidebar
      // Using a threshold to decide. 250 (expanded width) + 10 (margin) + 40 (buffer) = 300
      if (panel.left < 300) {
        panel.left += widthDifference;
      }
    }
  });
  saveLayout();
});

const initPanels = () => {
  const savedLayout = JSON.parse(localStorage.getItem('dashboard-layout'));
  const defaultLayout = getDefaultPanelsLayout();
  
  if (savedLayout) {
    const savedIds = savedLayout.map(p => p.id);
    const defaultIds = defaultLayout.map(p => p.id);
    if (savedIds.length === defaultIds.length && defaultIds.every(id => savedIds.includes(id))) {
       draggablePanels.value = savedLayout;
    } else {
       draggablePanels.value = defaultLayout;
    }
  } else {
    draggablePanels.value = defaultLayout;
  }
};

const saveLayout = () => {
  localStorage.setItem('dashboard-layout', JSON.stringify(draggablePanels.value));
};

const restoreLayout = () => {
  localStorage.removeItem('dashboard-layout');
  draggablePanels.value = getDefaultPanelsLayout();
  ElMessage.success('布局已还原');
};

// Drag and Drop Logic
let dragState = {
  dragging: false,
  panelId: null,
  startX: 0,
  startY: 0,
  panelStartX: 0,
  panelStartY: 0
};

const onDragStart = (event, panelId) => {
  if (event.button !== 0) return;
  const panel = draggablePanels.value.find(p => p.id === panelId);
  if (!panel) return;
  
  dragState = {
    dragging: true,
    panelId: panelId,
    startX: event.clientX,
    startY: event.clientY,
    panelStartX: panel.left,
    panelStartY: panel.top
  };

  document.addEventListener('mousemove', onDragging);
  document.addEventListener('mouseup', onDragEnd);
};

const onDragging = (event) => {
  if (!dragState.dragging) return;
  
  const dx = event.clientX - dragState.startX;
  const dy = event.clientY - dragState.startY;
  
  const panel = draggablePanels.value.find(p => p.id === dragState.panelId);
  if (panel) {
    panel.left = dragState.panelStartX + dx;
    panel.top = dragState.panelStartY + dy;
  }
};

const onDragEnd = () => {
  if (!dragState.dragging) return;
  
  dragState.dragging = false;
  saveLayout();

  document.removeEventListener('mousemove', onDragging);
  document.removeEventListener('mouseup', onDragEnd);
};

// Resize Logic
let resizeState = {
  resizing: false,
  panelId: null,
  direction: null,
  startX: 0,
  startY: 0,
  startWidth: 0,
  startHeight: 0,
  startLeft: 0,
  startTop: 0
};

const onResizeStart = (event, panelId, direction) => {
  if (event.button !== 0) return;
  event.preventDefault();

  const panel = draggablePanels.value.find(p => p.id === panelId);
  if (!panel) return;
  
  resizeState = {
    resizing: true,
    panelId: panelId,
    direction: direction,
    startX: event.clientX,
    startY: event.clientY,
    startWidth: panel.width,
    startHeight: panel.height,
    startLeft: panel.left,
    startTop: panel.top
  };

  document.addEventListener('mousemove', onResizing);
  document.addEventListener('mouseup', onResizeEnd);
};

const onResizing = (event) => {
  if (!resizeState.resizing) return;
  
  const panel = draggablePanels.value.find(p => p.id === resizeState.panelId);
  if (!panel) return;
  
  const dx = event.clientX - resizeState.startX;
  const dy = event.clientY - resizeState.startY;

  const minWidth = 250;
  const minHeight = 200;

  // Handle horizontal resizing
  if (resizeState.direction.includes('right')) {
    panel.width = Math.max(minWidth, resizeState.startWidth + dx);
  }
  if (resizeState.direction.includes('left')) {
    const newWidth = Math.max(minWidth, resizeState.startWidth - dx);
    panel.width = newWidth;
    panel.left = resizeState.startLeft + dx;
  }
  
  // Handle vertical resizing
  if (resizeState.direction.includes('bottom')) {
    panel.height = Math.max(minHeight, resizeState.startHeight + dy);
  }
  if (resizeState.direction.includes('top')) {
    const newHeight = Math.max(minHeight, resizeState.startHeight - dy);
    panel.height = newHeight;
    panel.top = resizeState.startTop + dy;
  }
};

const onResizeEnd = () => {
  if (!resizeState.resizing) return;
  
  resizeState.resizing = false;
  saveLayout();

  document.removeEventListener('mousemove', onResizing);
  document.removeEventListener('mouseup', onResizeEnd);
};

// 响应式数据
const dashboardData = ref(null);
const alarmList = ref([]);
const loading = ref(false);
const alarmLoading = ref(false);
const currentTime = ref('');
const weatherData = ref({
  icon: '☀️',
  text: '晴天'
});

// 地图相关状态
const showCommunities = ref(true);
const showOrganizations = ref(true);
const showAlarms = ref(true);
const mapInstance = ref(null);
const mapLayers = ref({
  communities: null,
  organizations: null,
  alarms: null
});

const topStatsRef = ref(null);
const mapContainer = ref(null);

// New ref handling for dynamic components
const panelContentRefs = ref({});
const setPanelContentRef = (el, id) => {
    if (el) {
        panelContentRefs.value[id] = el;
    }
};

// 图表引用
const elderlyTypeChart = ref(null);
const ageChart = ref(null);
const deviceTypeStatChart = ref(null); // Added ref for the new chart

// 时间更新定时器
let timeInterval = null;

// 计算属性
const facilityStats = computed(() => {
  if (!dashboardData.value?.facilityStats) {
    return { dayCareCount: 0, nursingHomeCount: 0, homeCareCount: 0 };
  }
  return {
    dayCareCount: dashboardData.value.facilityStats.dayCareCount || 0,
    nursingHomeCount: dashboardData.value.facilityStats.nursingHomeCount || 0,
    homeCareCount: dashboardData.value.facilityStats.homeCareCount || 0
  };
});

// 获取地图数据数量
const getMapDataCount = (type) => {
  if (!dashboardData.value?.mapData) return 0;
  const mapData = dashboardData.value.mapData;
  switch (type) {
    case 'communities': return mapData.communities?.length || 0;
    case 'organizations': return mapData.organizations?.length || 0;
    case 'alarms': return mapData.alarms?.length || 0;
    default: return 0;
  }
};

// 获取老人类型标签
const getElderlyTypeLabel = (type) => {
  const typeMap = {
    'normal': '普通老人',
    'empty_nest': '空巢老人',
    'living_alone': '独居老人',
    'disabled': '失能老人',
    'elderly': '高龄老人',
    'low_income': '低收入老人',
    'special_care': '特殊照护'
  };
  return typeMap[type] || type || '未分类';
};

// 更新当前时间
const updateTime = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  const hours = String(now.getHours()).padStart(2, '0');
  const minutes = String(now.getMinutes()).padStart(2, '0');
  const seconds = String(now.getSeconds()).padStart(2, '0');
  currentTime.value = `${year}.${month}.${day} ${hours}:${minutes}:${seconds}`;
};

// 格式化数字
const formatNumber = (num) => {
  if (!num) return '0';
  return num.toLocaleString();
};

// 格式化金额
const formatMoney = (amount) => {
  if (!amount) return '¥0';
  return `¥${(amount / 10000).toFixed(1)}万`;
};

// 获取能力评估数量
const getAbilityCount = (ability) => {
  if (!dashboardData.value?.abilityStats) return 0;
  const stats = dashboardData.value.abilityStats;
  switch (ability) {
    case '能力完好': return stats.fullAbilityCount || 0;
    case '轻度失能': return stats.mildDisabilityCount || 0;
    case '中度失能': return stats.moderateDisabilityCount || 0;
    case '重度失能': return stats.severeDisabilityCount || 0;
    default: return 0;
  }
};

// 获取能力评估百分比
const getAbilityPercentage = (ability) => {
  if (!dashboardData.value?.abilityStats) return 0;
  const total = dashboardData.value.elderlyStats?.totalCount || 1;
  const count = getAbilityCount(ability);
  return Math.round((count / total) * 100);
};

// 获取SOS设备在线率
const getSosProgress = () => {
  if (!dashboardData.value?.deviceStats) return 0;
  const total = dashboardData.value.deviceStats.sosDeviceCount || 0;
  const online = dashboardData.value.deviceStats.onlineCount || 0;
  if (total === 0) return 0;
  // 计算SOS设备的在线率（假设SOS设备占总在线设备的一定比例）
  const sosOnlineRate = Math.min(Math.round((online / Math.max(total, 1)) * 100), 100);
  return sosOnlineRate;
};

// 获取烟感设备正常率
const getSmokeProgress = () => {
  if (!dashboardData.value?.deviceStats) return 0;
  const total = dashboardData.value.deviceStats.smokeDetectorCount || 0;
  const fault = dashboardData.value.deviceStats.faultCount || 0;
  if (total === 0) return 0;
  // 计算正常率（100% - 故障率）
  const normalRate = Math.max(0, Math.round(((total - fault) / total) * 100));
  return normalRate;
};

// 获取设备运行状态详细信息
const getDeviceStatusDetails = () => {
  if (!dashboardData.value?.deviceStats?.deviceStatusDetails) {
    return [];
  }
  return dashboardData.value.deviceStats.deviceStatusDetails;
};

// 获取告警级别样式类
const getAlarmLevelClass = (level) => {
  const levelMap = { '紧急': 'emergency', '高': 'high', '中': 'medium', '低': 'low' };
  return levelMap[level] || 'low';
};

// 获取告警状态样式类
const getAlarmStatusClass = (status) => {
  const statusMap = { '未处理': 'pending', '处理中': 'processing', '已处理': 'completed' };
  return statusMap[status] || 'pending';
};

// 加载首页数据
const loadDashboardData = async () => {
  loading.value = true;
  try {
    const response = await request.get('/dashboard/stats');
    if (response && response.success) {
      dashboardData.value = response.data;
      console.log('首页数据加载成功:', dashboardData.value);
      
      await nextTick();
      initMap();
      initCharts();
    } else {
      console.error('加载首页数据失败:', response ? response.message : '未知错误');
      loadMockData();
    }
  } catch (error) {
    console.error('加载首页数据异常:', error);
    loadMockData();
  } finally {
    loading.value = false;
  }
};

// 加载模拟数据
const loadMockData = () => {
  dashboardData.value = {
    elderlyStats: {
      totalCount: 12940,
      over80Count: 3245,
      livingAloneCount: 1876,
      disabledCount: 892,
      lowIncomeCount: 567
    },
    facilityStats: {
      totalCount: 422,
      homeCareCount: 285,
      dayCareCount: 137,
      nursingHomeCount: 45
    },
    staffStats: {
      totalCount: 452,
      nurseCount: 298,
      doctorCount: 154
    },
    subsidyStats: {
      totalAmount: 9253500,
      beneficiaryCount: 3456,
      monthlyAmount: 1250000
    },
    deviceStats: {
      sosDeviceCount: 256,
      smokeDetectorCount: 189,
      waterLeakCount: 145,
      fallDetectorCount: 98,
      gasLeakCount: 67,
      onlineCount: 230,
      faultCount: 12
    },
    abilityStats: {
      fullAbilityCount: 8567,
      mildDisabilityCount: 2341,
      moderateDisabilityCount: 1456,
      severeDisabilityCount: 576,
      notAssessedCount: 500
    },
    elderlyTypeStats: {
      normalCount: 6789,
      emptyNestCount: 2345,
      livingAloneCount: 1876,
      disabledCount: 892,
      elderlyCount: 1038,
      lowIncomeCount: 567,
      specialCareCount: 234
    },
    alarmStats: {
      todayAlarmCount: 15,
      unhandledCount: 3
    },
    mapData: {
      communities: [
        {
          name: '朝阳公园社区',
          latitude: 39.9289,
          longitude: 116.4203,
          elderlyCount: 1245,
          facilityCount: 2,
          type: '居家养老'
        },
        {
          name: '中关村社区',
          latitude: 39.9831,
          longitude: 116.3145,
          elderlyCount: 2156,
          facilityCount: 2,
          type: '日照'
        },
        {
          name: '东直门社区',
          latitude: 39.9434,
          longitude: 116.4217,
          elderlyCount: 1876,
          facilityCount: 1,
          type: '机构'
        },
        {
          name: '三里屯社区',
          latitude: 39.9364,
          longitude: 116.4472,
          elderlyCount: 987,
          facilityCount: 1,
          type: '居家养老'
        },
        {
          name: '丰台社区',
          latitude: 39.8583,
          longitude: 116.2867,
          elderlyCount: 1654,
          facilityCount: 1,
          type: '日照'
        },
        {
          name: '海淀社区',
          latitude: 39.9590,
          longitude: 116.2982,
          elderlyCount: 2345,
          facilityCount: 2,
          type: '机构'
        }
      ],
      organizations: [
        {
          name: '朝阳区养老院',
          latitude: 39.9389,
          longitude: 116.4303,
          type: '机构养老单位（养老院）',
          bedCount: 120,
          staffCount: 35,
          serviceCount: 8
        },
        {
          name: '中关村日间照料中心',
          latitude: 39.9931,
          longitude: 116.3245,
          type: '社区养老单位（日照）',
          bedCount: 0,
          staffCount: 12,
          serviceCount: 5
        },
        {
          name: '东直门居家养老服务中心',
          latitude: 39.9534,
          longitude: 116.4317,
          type: '居家养老单位',
          bedCount: 0,
          staffCount: 8,
          serviceCount: 6
        },
        {
          name: '海淀区颐养中心',
          latitude: 39.9690,
          longitude: 116.3082,
          type: '机构养老单位（养老院）',
          bedCount: 200,
          staffCount: 55,
          serviceCount: 12
        }
      ],
      alarms: [
        {
          alarmType: 'SOS紧急求救',
          location: '朝阳公园社区3号楼201室',
          latitude: 39.9189,
          longitude: 116.4103,
          alarmTime: '2025-01-26T15:30:22',
          processStatus: '未处理',
          alarmLevel: '紧急'
        },
        {
          alarmType: '烟感报警',
          location: '中关村社区12号楼305室',
          latitude: 39.9731,
          longitude: 116.3045,
          alarmTime: '2025-01-26T14:45:15',
          processStatus: '处理中',
          alarmLevel: '高'
        },
        {
          alarmType: '跌倒检测报警',
          location: '东直门社区7号楼102室',
          latitude: 39.9334,
          longitude: 116.4117,
          alarmTime: '2025-01-26T13:20:08',
          processStatus: '未处理',
          alarmLevel: '中'
        },
        {
          alarmType: '设备离线',
          location: '海淀社区智能设备监控中心',
          latitude: 39.9490,
          longitude: 116.2882,
          alarmTime: '2025-01-26T12:15:33',
          processStatus: '已处理',
          alarmLevel: '低'
        }
      ]
    }
  };
  
  nextTick(() => {
    initMap();
    initCharts();
  });
};

// 加载告警数据
const loadAlarmData = async () => {
  alarmLoading.value = true;
  try {
    const response = await fetch('/api/dashboard/alarms/recent');
    if (response.ok) {
      const result = await response.json();
      if (result.success) {
        alarmList.value = result.data;
      } else {
        // 使用模拟告警数据
        alarmList.value = [
          {
            type: 'SOS报警',
            location: '朝阳公园社区 张建国',
            time: '2025-01-26 15:30:22',
            status: '未处理',
            level: '紧急'
          },
          {
            type: '烟感报警',
            location: '中关村社区 李秀英',
            time: '2025-01-26 14:45:15',
            status: '处理中',
            level: '高'
          },
          {
            type: '跌倒报警',
            location: '东直门社区 王福寿',
            time: '2025-01-26 13:20:08',
            status: '未处理',
            level: '中'
          }
        ];
      }
    } else {
      // 使用模拟告警数据
      alarmList.value = [
        {
          type: 'SOS报警',
          location: '朝阳公园社区 张建国',
          time: '2025-01-26 15:30:22',
          status: '未处理',
          level: '紧急'
        },
        {
          type: '烟感报警',
          location: '中关村社区 李秀英',
          time: '2025-01-26 14:45:15',
          status: '处理中',
          level: '高'
        },
        {
          type: '跌倒报警',
          location: '东直门社区 王福寿',
          time: '2025-01-26 13:20:08',
          status: '未处理',
          level: '中'
        }
      ];
    }
  } catch (error) {
    console.error('加载告警数据异常:', error);
    // 使用模拟告警数据
    alarmList.value = [
      {
        type: 'SOS报警',
        location: '朝阳公园社区 张建国',
        time: '2025-01-26 15:30:22',
        status: '未处理',
        level: '紧急'
      },
      {
        type: '烟感报警',
        location: '中关村社区 李秀英',
        time: '2025-01-26 14:45:15',
        status: '处理中',
        level: '高'
      },
      {
        type: '跌倒报警',
        location: '东直门社区 王福寿',
        time: '2025-01-26 13:20:08',
        status: '未处理',
        level: '中'
      }
    ];
  } finally {
    alarmLoading.value = false;
  }
};

// 初始化地图
const initMap = () => {
  if (mapInstance.value) { // If map already exists, just return
      return;
  }
  if (!mapContainer.value || !dashboardData.value?.mapData) return;

  mapInstance.value = L.map('leafletMap', {
    zoomControl: true,
    attributionControl: true
  }).setView([39.9042, 116.4074], 11);

  const tiandituKey = '0252639b1589bd33a54817f48d982093'; // 用户提供的天地图Key

  L.tileLayer(`https://t{s}.tianditu.gov.cn/vec_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=vec&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}&tk=${tiandituKey}`, {
    subdomains: ['0', '1', '2', '3', '4', '5', '6', '7'],
    attribution: '天地图 - 矢量地图',
    maxZoom: 18,
    opacity: 1.0
  }).addTo(mapInstance.value);

  L.tileLayer(`https://t{s}.tianditu.gov.cn/cva_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=cva&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}&tk=${tiandituKey}`, {
    subdomains: ['0', '1', '2', '3', '4', '5', '6', '7'],
    attribution: '天地图 - 注记',
    maxZoom: 18,
    opacity: 1.0,
    pane: 'overlayPane' // 确保注记在其他图层之上
  }).addTo(mapInstance.value);

  mapLayers.value.communities = L.layerGroup().addTo(mapInstance.value);
  mapLayers.value.organizations = L.layerGroup().addTo(mapInstance.value);
  mapLayers.value.alarms = L.layerGroup().addTo(mapInstance.value);

  addCommunityMarkers();
  addOrganizationMarkers();
  addAlarmMarkers();

  mapInstance.value.on('popupopen', (e) => {
    const popup = e.popup;
    const mapContainerElement = mapInstance.value.getContainer();
    const popupElement = popup.getElement();

    if (!popupElement || !mapContainerElement) return;

    nextTick(() => {
      const mapRect = mapContainerElement.getBoundingClientRect();
      const popupRect = popupElement.getBoundingClientRect();
      const safetyMargin = 20;

      let panX = 0;
      let panY = 0;

      const topStatsRect = topStatsRef.value.getBoundingClientRect();
      if (popupRect.top < topStatsRect.bottom + safetyMargin) {
        panY = topStatsRect.bottom - popupRect.top + safetyMargin;
      }

      draggablePanels.value.forEach(panel => {
        const panelEl = document.querySelector(`[data-panel-id='${panel.id}']`);
        if (!panelEl) return;
        
        const panelRect = panelEl.getBoundingClientRect();

        if (popupRect.bottom > panelRect.top && popupRect.top < panelRect.bottom &&
            popupRect.right > panelRect.left && popupRect.left < panelRect.right) {
          
          if ((popupRect.left + popupRect.right) / 2 < (panelRect.left + panelRect.right) / 2) {
            const rightPush = panelRect.right - popupRect.left + safetyMargin;
            if (rightPush > panX) panX = rightPush;
          } else {
            const leftPush = panelRect.left - popupRect.right - safetyMargin;
            if (leftPush < panX) panX = leftPush;
          }
        }
      });
      
      if (panX !== 0 || panY !== 0) {
        mapInstance.value.panBy([panX, panY], { animate: true });
      }
    });
  });
};

// 添加社区标记
const addCommunityMarkers = () => {
  if (!dashboardData.value?.mapData?.communities) return;

  dashboardData.value.mapData.communities.forEach(community => {
    const iconColor = '#00d4ff';
    const iconHtml = '🏘️';

    const customIcon = L.divIcon({
      html: `<div style="background: ${iconColor}; color: white; border-radius: 50%; width: 30px; height: 30px; display: flex; align-items: center; justify-content: center; font-size: 16px; border: 2px solid white; box-shadow: 0 2px 8px rgba(0,0,0,0.3);">${iconHtml}</div>`,
      className: 'custom-marker',
      iconSize: [30, 30],
      iconAnchor: [15, 15]
    });

    const marker = L.marker([community.latitude, community.longitude], { icon: customIcon })
      .bindPopup(`
        <div class="custom-popup">
          <div class="popup-header">
            <div class="popup-icon" style="background: ${iconColor};">🏘️</div>
            <h4 class="popup-title">${community.name}</h4>
          </div>
          <div class="popup-content">
            <div class="popup-item">
              <span class="popup-label">类型:</span>
              <span class="popup-value">社区</span>
            </div>
            <div class="popup-item">
              <span class="popup-label">老人数量:</span>
              <span class="popup-value highlight">${community.elderlyCount}人</span>
            </div>
            <div class="popup-item">
              <span class="popup-label">机构数量:</span>
              <span class="popup-value highlight">${community.facilityCount}个</span>
            </div>
          </div>
        </div>
      `, {
        maxWidth: 350,
        className: 'custom-popup-wrapper',
        offset: [10, 10],
        autoPan: false
      });

    mapLayers.value.communities.addLayer(marker);
  });
};

// 添加机构标记
const addOrganizationMarkers = () => {
  if (!dashboardData.value?.mapData?.organizations) return;

  dashboardData.value.mapData.organizations.forEach(org => {
    let iconColor = '#ff6b35';
    let iconHtml = '🏢';
    
    switch (org.type) {
      case '机构养老单位（养老院）':
        iconColor = '#ff6b35';
        iconHtml = '🏥';
        break;
      case '社区养老单位（日照）':
        iconColor = '#00d4ff';
        iconHtml = '🏢';
        break;
      case '居家养老单位':
        iconColor = '#2ed573';
        iconHtml = '🏠';
        break;
      default:
        iconColor = '#ff6b35';
        iconHtml = '🏢';
        break;
    }

    const customIcon = L.divIcon({
      html: `<div style="background: ${iconColor}; color: white; border-radius: 50%; width: 30px; height: 30px; display: flex; align-items: center; justify-content: center; font-size: 16px; border: 2px solid white; box-shadow: 0 2px 8px rgba(0,0,0,0.3);">${iconHtml}</div>`,
      className: 'custom-marker',
      iconSize: [30, 30],
      iconAnchor: [15, 15]
    });

    const marker = L.marker([org.latitude, org.longitude], { icon: customIcon })
      .bindPopup(`
        <div class="custom-popup">
          <div class="popup-header">
            <div class="popup-icon" style="background: ${iconColor};">${iconHtml}</div>
            <h4 class="popup-title">${org.name}</h4>
          </div>
          <div class="popup-content">
            <div class="popup-item">
              <span class="popup-label">类型:</span>
              <span class="popup-value">${org.type}</span>
            </div>
            <div class="popup-item">
              <span class="popup-label">床位数:</span>
              <span class="popup-value highlight">${org.bedCount || 0}张</span>
            </div>
            <div class="popup-item">
              <span class="popup-label">员工数:</span>
              <span class="popup-value highlight">${org.staffCount || 0}人</span>
            </div>
            <div class="popup-item">
              <span class="popup-label">服务数:</span>
              <span class="popup-value highlight">${org.serviceCount || 0}项</span>
            </div>
          </div>
        </div>
      `, {
        maxWidth: 380,
        className: 'custom-popup-wrapper',
        offset: [10, 10],
        autoPan: false
      });

    mapLayers.value.organizations.addLayer(marker);
  });
};

// 添加告警标记
const addAlarmMarkers = () => {
  if (!dashboardData.value?.mapData?.alarms) return;

  dashboardData.value.mapData.alarms.forEach(alarm => {
    let iconColor = '#ff4757';
    let iconHtml = '🚨';
    
    // 根据告警类型设置图标
    if (alarm.alarmType.includes('SOS') || alarm.alarmType.includes('紧急')) {
      iconColor = '#ff4757';
      iconHtml = '🆘';
    } else if (alarm.alarmType.includes('烟感') || alarm.alarmType.includes('火灾')) {
      iconColor = '#ff6b35';
      iconHtml = '🔥';
    } else if (alarm.alarmType.includes('跌倒')) {
      iconColor = '#ffa502';
      iconHtml = '⚠️';
    } else if (alarm.alarmType.includes('健康') || alarm.alarmType.includes('医疗')) {
      iconColor = '#ff6348';
      iconHtml = '💊';
    } else if (alarm.alarmType.includes('设备') || alarm.alarmType.includes('故障')) {
      iconColor = '#ff9ff3';
      iconHtml = '🔧';
    }

    const customIcon = L.divIcon({
      html: `<div style="background: ${iconColor}; color: white; border-radius: 50%; width: 25px; height: 25px; display: flex; align-items: center; justify-content: center; font-size: 12px; border: 2px solid white; box-shadow: 0 2px 8px rgba(0,0,0,0.3); animation: pulse 2s infinite;">${iconHtml}</div>`,
      className: 'alarm-marker',
      iconSize: [25, 25],
      iconAnchor: [12.5, 12.5]
    });

    const marker = L.marker([alarm.latitude, alarm.longitude], { icon: customIcon })
      .bindPopup(`
        <div class="custom-popup alarm-popup">
          <div class="popup-header">
            <div class="popup-icon alarm-icon" style="background: ${iconColor};">🚨</div>
            <h4 class="popup-title">${alarm.alarmType}</h4>
            <span class="alarm-level ${alarm.alarmLevel}">${alarm.alarmLevel}</span>
          </div>
          <div class="popup-content">
            <div class="popup-item">
              <span class="popup-label">📍 位置:</span>
              <span class="popup-value">${alarm.location}</span>
            </div>
            <div class="popup-item">
              <span class="popup-label">⏰ 时间:</span>
              <span class="popup-value">${new Date(alarm.alarmTime).toLocaleString('zh-CN')}</span>
            </div>
            <div class="popup-item">
              <span class="popup-label">📋 状态:</span>
              <span class="popup-value status-${alarm.processStatus}">${alarm.processStatus}</span>
            </div>
          </div>
        </div>
      `, {
        maxWidth: 400,
        className: 'custom-popup-wrapper alarm-popup-wrapper',
        offset: [10, 10],
        autoPan: false
      });

    mapLayers.value.alarms.addLayer(marker);
  });
};

// 切换图层显示
const toggleLayer = (layerType) => {
  const layer = mapLayers.value[layerType];
  if (!layer || !mapInstance.value) return;

  switch (layerType) {
    case 'communities':
      showCommunities.value = !showCommunities.value;
      if (showCommunities.value) {
        mapInstance.value.addLayer(layer);
      } else {
        mapInstance.value.removeLayer(layer);
      }
      break;
    case 'organizations':
      showOrganizations.value = !showOrganizations.value;
      if (showOrganizations.value) {
        mapInstance.value.addLayer(layer);
      } else {
        mapInstance.value.removeLayer(layer);
      }
      break;
    case 'alarms':
      showAlarms.value = !showAlarms.value;
      if (showAlarms.value) {
        mapInstance.value.addLayer(layer);
      } else {
        mapInstance.value.removeLayer(layer);
      }
      break;
  }
};

// 重置地图视图
const resetMapView = () => {
  if (mapInstance.value) {
    mapInstance.value.setView([39.9042, 116.4074], 11);
  }
};

// 初始化图表
const initCharts = () => {
  if (!dashboardData.value) return;
  
  initElderlyTypeChart();
  initDeviceTypeStatChart();
};

const initElderlyTypeChart = () => {
  const container = panelContentRefs.value['elderlyType'];
  const elderlyData = dashboardData.value?.elderlyTypeStats;

  if (!container || !elderlyData) {
    console.warn('老人类型分析图表容器或数据不可用。');
    return;
  }

  const chartInstance = echarts.getInstanceByDom(container) || echarts.init(container);
  
  const colors = ['#00d4ff', '#ff6b35', '#7b68ee', '#ff4757', '#2ed573', '#ffa502', '#ff6348'];
  const rawData = [
      { value: elderlyData?.normalCount || 0, name: '普通老人' },
      { value: elderlyData?.emptyNestCount || 0, name: '空巢老人' },
      { value: elderlyData?.livingAloneCount || 0, name: '独居老人' },
      { value: elderlyData?.disabledCount || 0, name: '失能老人' },
      { value: elderlyData?.elderlyCount || 0, name: '高龄老人' },
      { value: elderlyData?.lowIncomeCount || 0, name: '低收入老人' },
      { value: elderlyData?.specialCareCount || 0, name: '特殊照护' }
  ];
  
  const chartData = rawData
    .map((item, index) => ({
      ...item,
      itemStyle: {
        color: colors[index % colors.length]
      }
    }))
    .filter(d => d.value > 0);

  const option = {
      backgroundColor: 'transparent',
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)',
        backgroundColor: 'rgba(0, 0, 0, 0.8)',
        borderColor: '#00d4ff',
        borderWidth: 1,
        textStyle: { color: '#fff' }
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        top: 'center',
        textStyle: { 
          fontSize: 12,
          color: '#fff'
        },
        itemWidth: 12,
        itemHeight: 12,
        data: chartData.map(item => item.name)
      },
      series: [{
        name: '老人类型',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['65%', '50%'],
        avoidLabelOverlap: false,
        label: { show: false },
        emphasis: {
            itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
        },
        labelLine: { show: false },
        data: chartData
      }]
  };

  chartInstance.setOption(option, true);
  
   const resizeChart = () => chartInstance.resize();
   window.addEventListener('resize', resizeChart);
   onUnmounted(() => {
     window.removeEventListener('resize', resizeChart);
      if (chartInstance && !chartInstance.isDisposed()) {
        chartInstance.dispose();
     }
   });
};

const initDeviceTypeStatChart = () => {
  const container = panelContentRefs.value['deviceStatus'];
  if (!container) return;

  const chartInstance = echarts.getInstanceByDom(container) || echarts.init(container);
  
  const aggregatedDeviceStats = getDeviceStatusDetails(); // This is already aggregated data
  console.log('Aggregated Device Stats for Chart:', JSON.stringify(aggregatedDeviceStats));

  let categories = [];
  let onlineData = [];
  let offlineData = [];
  let totalData = [];
  
  if (Array.isArray(aggregatedDeviceStats) && aggregatedDeviceStats.length > 0 && aggregatedDeviceStats.some(item => item.totalCount > 0)) {
    aggregatedDeviceStats.forEach(item => {
      // Prefer a dedicated deviceType field if available, otherwise use deviceName as category
      const categoryName = item.deviceType || item.deviceName || '未知类型'; 
      categories.push(categoryName);
      
      const total = parseInt(item.totalCount, 10) || 0;
      const online = parseInt(item.onlineCount, 10) || 0;
      const offline = total - online;

      onlineData.push(online);
      offlineData.push(offline);
      totalData.push(total);
    });
  } else {
    // Fallback or example data if aggregatedDeviceStats is empty or not in expected format
    categories = ['健康监测', '智能家居', '安全', '定位'];
    onlineData = [9, 6, 4, 2];
    offlineData = [1, 0, 0, 0];
    totalData = [10, 6, 4, 2];
    console.warn('Using fallback data for deviceTypeStatChart as aggregatedDeviceStats is empty or invalid.');
  }

  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: function (params) {
        const categoryName = params[0].name;
        let online = 0;
        let offline = 0;

        params.forEach(param => {
          if (param.seriesName === '在线') {
            online = param.value;
          } else if (param.seriesName === '离线') {
            offline = param.value;
          }
        });
        const total = online + offline;
        return `${categoryName}<br/>正常: ${online}<br/>异常: ${offline}<br/>总数: ${total}`;
      }
    },
    legend: {
        data: ['在线', '离线'],
        bottom: 5,
        textStyle: {
            color: '#CFD3DC'
        },
        itemWidth: 14,
        itemHeight: 14,
        icon: 'rect',
        formatter: name => name === '在线' ? '正常' : '异常'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '12%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: categories,
      axisLine: {
        lineStyle: {
          color: '#86909C'
        }
      },
      axisLabel: {
        color: '#CFD3DC',
        interval: 0, 
        rotate: categories.length > 5 ? 30 : (categories.length > 3 ? 15 : 0), // Adjust rotation based on label count
        formatter: function (value) {
            // Attempt to remove trailing '设备' if present for cleaner labels
            let label = value;
            if (typeof value === 'string' && value.endsWith('设备') && value.length > 2) {
                label = value.substring(0, value.length - 2);
            }
            return label.length > 6 ? label.substring(0, 5) + '...' : label;
        }
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: true,
        lineStyle: {
          color: '#86909C'
        }
      },
      axisLabel: {
        color: '#CFD3DC'
      },
      splitLine: {
        lineStyle: {
          color: '#3E4658',
          type: 'dashed'
        }
      }
    },
    series: [
      {
        name: '在线',
        type: 'bar',
        stack: 'total',
        barWidth: '40%',
        data: onlineData, 
        itemStyle: {
          color: '#2ed573' // Green for 'online'
        },
      },
      {
        name: '离线',
        type: 'bar',
        stack: 'total',
        barWidth: '40%',
        data: offlineData, 
        itemStyle: {
          color: '#ff4757', // Red for 'offline'
          borderRadius: [4, 4, 0, 0],
        },
        // Put label on the top-most stack item
        label: {
          show: true,
          position: 'top',
          color: '#CFD3DC',
          fontSize: 12,
          formatter: function(params) {
            const total = totalData[params.dataIndex];
            const online = onlineData[params.dataIndex];
            if (total > 0) {
              return `${online}/${total}`;
            }
            return '';
          }
        }
      }
    ]
  };
  chartInstance.setOption(option);
  
   const resizeChart = () => chartInstance.resize();
   window.addEventListener('resize', resizeChart);
   onUnmounted(() => {
     window.removeEventListener('resize', resizeChart);
     if (chartInstance && !chartInstance.isDisposed()) {
        chartInstance.dispose();
     }
   });
};

// 组件挂载
onMounted(async () => {
  initPanels();
  updateTime();
  timeInterval = setInterval(updateTime, 1000);
  loadDashboardData();
  loadWeatherData();
  loadAlarmData();
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  if (timeInterval) clearInterval(timeInterval);
  if (mapInstance.value) {
    mapInstance.value.remove();
    mapInstance.value = null;
  }
  window.removeEventListener('resize', handleResize);
  // Cleanup all potential global listeners
  document.removeEventListener('mousemove', onDragging);
  document.removeEventListener('mouseup', onDragEnd);
  document.removeEventListener('mousemove', onResizing);
  document.removeEventListener('mouseup', onResizeEnd);
});

const handleResize = () => {
    draggablePanels.value.forEach(panel => {
        if (panel.left + panel.width > window.innerWidth) {
            panel.left = window.innerWidth - panel.width - 20;
        }
        if (panel.top + panel.height > window.innerHeight) {
            panel.top = window.innerHeight - panel.height - 20;
        }
    });
    saveLayout();
};

// 获取天气信息
const loadWeatherData = async () => {
  try {
    // 可以接入真实的天气API，这里使用模拟数据
    const weatherOptions = [
      { icon: '☀️', text: '晴天' },
      { icon: '⛅', text: '多云' },
      { icon: '🌤️', text: '晴转多云' },
      { icon: '🌦️', text: '阵雨' },
      { icon: '❄️', text: '雪' }
    ];
    
    // 根据时间或随机选择天气
    const hour = new Date().getHours();
    if (hour >= 6 && hour <= 18) {
      weatherData.value = weatherOptions[0]; // 白天晴天
    } else {
      weatherData.value = { icon: '🌙', text: '晴夜' };
    }
  } catch (error) {
    console.error('获取天气信息失败:', error);
    // 保持默认值
  }
};

</script>

<style scoped>
.smart-dashboard {
  min-height: 100vh;
  background: #0c1426;
  background-image: 
    radial-gradient(circle at 20% 80%, rgba(0, 212, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 107, 53, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(123, 104, 238, 0.1) 0%, transparent 50%);
  color: #ffffff;
  font-family: 'Microsoft YaHei', sans-serif;
  overflow-x: hidden;
}

/* 顶部标题栏 */
.dashboard-header {
  display: none;
  justify-content: space-between;
  align-items: center;
  width: 90%;
  padding: 20px 40px;
  /* margin-bottom: 20px; */
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 212, 255, 0.3);
}

.current-time {
  font-size: 16px;
  color: #00d4ff;
  font-weight: 500;
}

.system-title {
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(45deg, #00d4ff, #ff6b35);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-align: center;
  margin: 0;
  text-shadow: 0 0 20px rgba(0, 212, 255, 0.5);
}

.weather-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  color: #ffffff;
}

/* 顶部统计卡片 */
/* 顶部标题栏 */
.dashboard-header {
  position: relative;
  z-index: 20;
  display: none;
  justify-content: space-between;
  align-items: center;
  padding: 15px 40px;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 212, 255, 0.3);
}

.current-time {
  font-size: 14px;
  color: #00d4ff;
  font-weight: 500;
}

.system-title {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(45deg, #00d4ff, #ff6b35);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-align: center;
  margin: 0;
  text-shadow: 0 0 20px rgba(0, 212, 255, 0.5);
}

.weather-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #ffffff;
}

/* 背景地图 */
.background-map {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  z-index: 1;
}

.background-map .leaflet-map {
  width: 100%;
  height: 100%;
}

/* 顶部数据统计卡片 */
.top-stats {
  position: fixed;
  top: 5px;
  left: 80px;
  right: 20px;
  z-index: 10;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  margin: 0 320px;
  transition: all 0.3s ease;
}

/* 当侧边栏展开时的顶部统计卡片调整 */
.el-container:not(.el-aside-collapsed) .top-stats {
  left: 270px;
  margin: 0 320px;
}

/* 当侧边栏收起时的顶部统计卡片调整 */
.el-container.el-aside-collapsed .top-stats {
  left: 80px;
  margin: 0 320px;
}

@media (max-width: 1600px) {
  .top-stats {
    margin: 0 300px;
  }
}

@media (max-width: 1400px) {
  .left-panel,
  .right-panel {
    width: 250px;
  }
  
  .top-stats {
    margin: 0 270px;
  }
}

@media (max-width: 1200px) {
  .top-stats {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
    margin: 0 250px;
  }
  
  .left-panel {
    left: 110px;
    width: 220px;
  }
  
  .right-panel {
    right: 20px;
    width: 220px;
  }
}

.stat-card {
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, transparent, var(--accent-color), transparent);
}

.stat-card.blue { --accent-color: #00d4ff; }
.stat-card.orange { --accent-color: #ff6b35; }
.stat-card.purple { --accent-color: #7b68ee; }
.stat-card.green { --accent-color: #2ed573; }

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 212, 255, 0.3);
}

.stat-icon {
  font-size: 32px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--accent-color);
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #ffffff;
  opacity: 0.8;
}

/* 地图控制面板 */
.map-control-panel {
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(0, 212, 255, 0.3);
  padding: 16px 20px;
  margin-bottom: 20px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.panel-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #00d4ff;
}

.panel-controls {
  display: flex;
  justify-content: center;
}

/* 图例面板 */
.legend-panel {
  position: fixed;
  left: 80px;
  bottom: 20px;
  width: 160px;
  z-index: 20;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 8px;
  padding: 12px 8px;
  border: 1px solid rgba(0, 212, 255, 0.3);
  transition: all 0.3s ease;
}

/* 当侧边栏展开时的图例位置调整 */
.el-container:not(.el-aside-collapsed) .legend-panel {
  left: 270px;
}

/* 当侧边栏收起时的图例位置调整 */
.el-container.el-aside-collapsed .legend-panel {
  left: 80px;
}

.legend-panel .legend-title {
  font-size: 12px;
  font-weight: 600;
  color: #00d4ff;
  margin-bottom: 12px;
  text-align: center;
}

.legend-panel .legend-items {
  display: flex;
  /* flex-direction: column; */
  gap: 12px;
}

.legend-panel .legend-item {
  display: flex;
  /* flex-direction: column; */
  align-items: center;
  gap: 4px;
  font-size: 10px;
  color: #ffffff;
  text-align: center;
}

.legend-panel .legend-marker {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 1px solid white;
}

/* 左侧面板 */
.left-panel {
  position: fixed;
  left: 20px;
  top: 5px;
  bottom: 120px;
  width: 280px;
  z-index: 10;
  display: flex;
  flex-direction: column;
  gap: 15px;
  overflow-y: auto;
  transition: all 0.3s ease;
}

/* 右侧面板 */
.right-panel {
  position: fixed;
  right: 20px;
  top: 5px;
  bottom: 20px;
  width: 280px;
  z-index: 10;
  display: flex;
  flex-direction: column;
  gap: 15px;
  overflow-y: auto;
  transition: all 0.3s ease;
}

/* 当侧边栏展开时的面板位置调整 */
.el-container:not(.el-aside-collapsed) .left-panel {
  left: 270px;
}

/* 当侧边栏收起时的面板位置调整 */
.el-container.el-aside-collapsed .left-panel {
  left: 80px;
}

/* 卡片样式 */
.dashboard-card {
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(0, 212, 255, 0.3);
  overflow: hidden;
  transition: all 0.3s ease;
}

.dashboard-card:hover {
  border-color: rgba(0, 212, 255, 0.7);
  box-shadow: 0 8px 25px rgba(0, 212, 255, 0.2);
}

.card-header {
  padding: 16px 20px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(0, 212, 255, 0.1);
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #00d4ff;
}

.card-content {
  padding: 20px;
}

.chart-container {
  height: 200px;
  width: 100%;
  transition: all 0.3s ease;
}



/* 底部控制栏 */
.bottom-controls {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 15;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 12px 20px;
  border: 1px solid rgba(0, 212, 255, 0.3);
}

/* 地图控制按钮 */
.map-controls {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.control-icon-btn {
  background: rgba(0, 212, 255, 0.2);
  border: 1px solid rgba(0, 212, 255, 0.3);
  color: #00d4ff;
  padding: 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 16px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.control-icon-btn:hover {
  background: rgba(0, 212, 255, 0.3);
  transform: translateY(-2px);
}

.control-icon-btn.active {
  background: rgba(0, 212, 255, 0.4);
  border-color: rgba(0, 212, 255, 0.6);
  box-shadow: 0 0 10px rgba(0, 212, 255, 0.3);
}

.legend-marker.blue { background: #00d4ff; }
.legend-marker.orange { background: #ff6b35; }
.legend-marker.green { background: #2ed573; }
.legend-marker.red { background: #ff4757; }

/* 能力评估 */
.ability-stats {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.ability-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.ability-bar {
  height: 8px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.8s ease;
}

.bar-fill.green { background: #2ed573; }
.bar-fill.blue { background: #00d4ff; }
.bar-fill.orange { background: #ff6b35; }
.bar-fill.red { background: #ff4757; }

.ability-label {
  font-size: 12px;
  color: #ffffff;
  font-weight: 500;
}

.ability-value {
  font-size: 11px;
  color: #ffffff;
  opacity: 0.8;
}

/* 年龄分布图例 */
.gender-legend {
  display: flex;
  gap: 12px;
}

.legend-item {
  font-size: 12px;
  color: #ffffff;
}

.legend-item.male { color: #00d4ff; }
.legend-item.female { color: #ff6b35; }

/* 设备状态环形图 */
.device-count {
  font-size: 12px;
  color: #ffffff;
  opacity: 0.8;
}

.device-status-ring {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 120px;
}

.ring-chart {
  position: relative;
  width: 80px;
  height: 80px;
}

.ring-progress {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: conic-gradient(#00d4ff 0deg, #00d4ff calc(var(--progress) * 3.6deg), rgba(255, 255, 255, 0.1) calc(var(--progress) * 3.6deg));
  position: relative;
}

.ring-progress.orange {
  background: conic-gradient(#ff6b35 0deg, #ff6b35 calc(var(--progress) * 3.6deg), rgba(255, 255, 255, 0.1) calc(var(--progress) * 3.6deg));
}

.ring-progress::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 60px;
  height: 60px;
  background: rgba(0, 0, 0, 0.4);
  border-radius: 50%;
  transform: translate(-50%, -50%);
}

.ring-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.ring-value {
  font-size: 16px;
  font-weight: 700;
  color: #00d4ff;
}

.ring-label {
  font-size: 10px;
  color: #ffffff;
  opacity: 0.8;
}

/* 设备状态表格 */
.device-status-table {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.status-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 8px;
  padding: 8px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.status-row.header {
  font-weight: 600;
  color: #00d4ff;
  border-bottom: 1px solid rgba(0, 212, 255, 0.3);
}

.status-col {
  font-size: 12px;
  color: #ffffff;
  text-align: center;
}

/* 告警控制按钮 */
.alarm-controls {
  display: flex;
  gap: 8px;
}

.control-btn {
  background: rgba(0, 212, 255, 0.2);
  border: 1px solid rgba(0, 212, 255, 0.3);
  color: #00d4ff;
  padding: 4px 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.control-btn:hover {
  background: rgba(0, 212, 255, 0.3);
}

/* 自动高度的告警卡片 */
.alarm-card-auto {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.alarm-card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.alarm-list-auto {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

/* 告警列表 */
.alarm-list {
  overflow-y: auto;
}

.alarm-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.alarm-item:hover {
  background: rgba(0, 212, 255, 0.1);
}

.alarm-item:last-child {
  border-bottom: none;
}

.alarm-type {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 500;
  white-space: nowrap;
}

.alarm-type.emergency {
  background: rgba(255, 71, 87, 0.2);
  color: #ff4757;
  border: 1px solid rgba(255, 71, 87, 0.3);
}

.alarm-type.high {
  background: rgba(255, 107, 53, 0.2);
  color: #ff6b35;
  border: 1px solid rgba(255, 107, 53, 0.3);
}

.alarm-type.medium {
  background: rgba(0, 212, 255, 0.2);
  color: #00d4ff;
  border: 1px solid rgba(0, 212, 255, 0.3);
}

.alarm-content {
  flex: 1;
}

.alarm-location {
  font-size: 12px;
  color: #ffffff;
  margin-bottom: 4px;
}

.alarm-time {
  font-size: 10px;
  color: #ffffff;
  opacity: 0.6;
}

.alarm-status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 500;
  white-space: nowrap;
}

.alarm-status.pending {
  background: rgba(255, 71, 87, 0.2);
  color: #ff4757;
}

.alarm-status.processing {
  background: rgba(255, 107, 53, 0.2);
  color: #ff6b35;
}

.alarm-status.completed {
  background: rgba(46, 213, 115, 0.2);
  color: #2ed573;
}

/* 无告警状态样式 */
.no-alarm {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
  height: 100%;
  min-height: 200px;
}

.no-alarm-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.8;
}

.no-alarm-text {
  font-size: 16px;
  color: #2ed573;
  font-weight: 600;
  margin-bottom: 8px;
}

.no-alarm-desc {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}



/* 响应式设计 */
@media (max-width: 1400px) {
  .left-panel,
  .right-panel {
    width: 250px;
  }
  
  .system-title {
    font-size: 22px;
  }
}

@media (max-width: 1200px) {
  .top-stats {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
    left: 110px;
  }
  
  .legend-panel {
    width: 80px;
    padding: 8px 6px;
  }
  
  .left-panel {
    left: 110px;
    width: 220px;
  }
  
  .right-panel {
    right: 15px;
    width: 220px;
    bottom: 5px;
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    padding: 10px 15px;
  }
  
  .dashboard-header .header-center {
    display: none;
  }
  
  .top-stats {
    top: 60px;
    left: 80px;
    right: 10px;
    grid-template-columns: 1fr;
    gap: 8px;
  }
  
  .stat-card {
    padding: 12px;
  }
  
  .stat-icon {
    font-size: 20px;
    width: 35px;
    height: 35px;
  }
  
  .stat-value {
    font-size: 18px;
  }
  
  .stat-label {
    font-size: 12px;
  }
  
  .legend-panel {
    left: 10px;
    width: 60px;
    padding: 8px 4px;
  }
  
  .legend-panel .legend-title {
    font-size: 10px;
    margin-bottom: 8px;
  }
  
  .legend-panel .legend-item {
    font-size: 9px;
    gap: 2px;
  }
  
  .legend-panel .legend-marker {
    width: 10px;
    height: 10px;
  }
  
  .left-panel {
    left: 80px;
    top: 350px;
    width: calc(50% - 45px);
  }
  
  .right-panel {
    right: 10px;
    top: 350px;
    bottom: 5px;
    width: calc(60% - 45px);
  }
  
  .dashboard-card {
    margin-bottom: 10px;
  }
  
  .card-header h3 {
    font-size: 14px;
  }
  
  .chart-container {
    height: 150px;
  }
  
  .bottom-controls {
    bottom: 10px;
    padding: 8px 12px;
  }
  
  .control-icon-btn {
    width: 35px;
    height: 35px;
    font-size: 14px;
  }
}

/* Leaflet地图自定义样式 */
:deep(.leaflet-control-container) {
  font-family: 'Microsoft YaHei', sans-serif;
}

/* 注释掉通用的弹出框样式，使用自定义样式 */
/* :deep(.leaflet-popup-content-wrapper) {
  background: rgba(0, 0, 0, 0.9);
  border-radius: 8px;
  border: 1px solid rgba(0, 212, 255, 0.3);
}

:deep(.leaflet-popup-tip) {
  background: rgba(0, 0, 0, 0.9);
  border: 1px solid rgba(0, 212, 255, 0.3);
} */

:deep(.leaflet-control-zoom a) {
  background: rgba(0, 0, 0, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.3);
  color: #00d4ff;
}

:deep(.leaflet-control-zoom a:hover) {
  background: rgba(0, 212, 255, 0.2);
}

:deep(.leaflet-control-attribution) {
  background: rgba(0, 0, 0, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.3);
  color: #ffffff;
  font-size: 10px;
}

/* 地图标记动画 */
@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(255, 71, 87, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(255, 71, 87, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(255, 71, 87, 0);
  }
}

:deep(.alarm-marker div) {
  animation: pulse 2s infinite;
}

:deep(.custom-marker) {
  background: transparent !important;
  border: none !important;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: rgba(0, 212, 255, 0.5);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 212, 255, 0.7);
}

/* 自定义弹出框样式 */
:deep(.custom-popup-wrapper .leaflet-popup-content-wrapper) {
  background: linear-gradient(145deg, #1a1a2e 0%, #16213e 50%, #0f1419 100%);
  border-radius: 16px;
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.4),
    0 8px 20px rgba(0, 212, 255, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
  border: 2px solid rgba(0, 212, 255, 0.3);
  backdrop-filter: blur(20px);
  padding: 30px !important;
  overflow: hidden;
  min-width: 320px !important;
  width: auto !important;
  box-sizing: border-box !important;
}

:deep(.custom-popup-wrapper .leaflet-popup-content) {
  margin: -10px !important;
  padding: 0 !important;
  font-family: 'Microsoft YaHei', sans-serif;
  width: auto !important;
  color: white !important;
  line-height: 2; /* 修改行高 */
}

:deep(.custom-popup-wrapper .leaflet-popup-content *) {
  color: white !important;
}

:deep(.custom-popup-wrapper .leaflet-popup-tip) {
  background: linear-gradient(145deg, #1a1a2e 0%, #16213e 50%, #0f1419 100%);
  border: 2px solid rgba(0, 212, 255, 0.3);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.custom-popup {
  color: white !important;
  min-width: 320px;
  font-family: 'Microsoft YaHei', sans-serif;
}

.custom-popup *,
.custom-popup h1,
.custom-popup h2,
.custom-popup h3,
.custom-popup h4,
.custom-popup h5,
.custom-popup h6,
.custom-popup p,
.custom-popup span,
.custom-popup div {
  color: white !important;
}

.popup-header {
  display: flex;
  align-items: center;
  padding: 20px 24px 16px 24px;
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.15) 0%, rgba(123, 104, 238, 0.1) 100%);
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
  position: relative;
  backdrop-filter: blur(10px);
}

.popup-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, #00d4ff, transparent);
}

.popup-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  margin-right: 16px;
  box-shadow: 
    0 4px 12px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.1);
}

.popup-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  flex: 1;
  color: white !important;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  letter-spacing: 0.5px;
}

.popup-content {
  padding: 20px 24px 24px 24px;
  background: rgba(0, 0, 0, 0.1);
}

.popup-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
  padding: 8px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.2s ease;
}

.popup-item:hover {
  background: rgba(0, 212, 255, 0.05);
  border-radius: 6px;
  padding: 8px 12px;
  margin: 4px -12px 8px -12px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.1);
}

.popup-item:last-child {
  margin-bottom: 0;
  border-bottom: none;
}

.popup-label {
  color: rgba(255, 255, 255, 0.7) !important;
  font-weight: 500;
  min-width: 80px;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.popup-value {
  color: white !important;
  font-weight: 600;
  text-align: right;
  font-size: 14px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.popup-value.highlight {
  color: #00d4ff !important;
  font-weight: 700;
  text-shadow: 0 0 8px rgba(0, 212, 255, 0.3);
}

/* 告警弹出框特殊样式 */
:deep(.alarm-popup-wrapper .leaflet-popup-content-wrapper) {
  background: linear-gradient(145deg, #2d1b1b 0%, #3d1a1a 50%, #4a1515 100%);
  border: 2px solid rgba(255, 71, 87, 0.4);
  box-shadow: 
    0 20px 60px rgba(255, 71, 87, 0.2),
    0 8px 20px rgba(255, 71, 87, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
  min-width: 350px !important;
  padding: 20px !important;
  box-sizing: border-box !important;
}

:deep(.alarm-popup-wrapper .leaflet-popup-tip) {
  background: linear-gradient(145deg, #2d1b1b 0%, #3d1a1a 50%, #4a1515 100%);
  border: 2px solid rgba(255, 71, 87, 0.4);
  box-shadow: 0 4px 12px rgba(255, 71, 87, 0.2);
}

:deep(.alarm-popup-wrapper .leaflet-popup-content) {
  color: white !important;
  margin: -10px !important;
  line-height: 2; /* 修改行高 */
}

:deep(.alarm-popup-wrapper .leaflet-popup-content *) {
  color: white !important;
}

.alarm-popup .popup-header {
  background: linear-gradient(135deg, rgba(255, 71, 87, 0.2) 0%, rgba(255, 107, 53, 0.15) 100%);
  border-bottom: 1px solid rgba(255, 71, 87, 0.3);
}

.alarm-popup .popup-header::before {
  background: linear-gradient(90deg, transparent, #ff4757, transparent);
}

.alarm-popup {
  color: white !important;
}

.alarm-popup *,
.alarm-popup h1,
.alarm-popup h2,
.alarm-popup h3,
.alarm-popup h4,
.alarm-popup h5,
.alarm-popup h6,
.alarm-popup p,
.alarm-popup span,
.alarm-popup div {
  color: white !important;
}

.alarm-level {
  position: absolute;
  top: 8px;
  right: 12px;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
}

.alarm-level.警告 {
  background: #ffa502;
  color: white;
}

.alarm-level.严重 {
  background: #ff4757;
  color: white;
}

.alarm-level.紧急 {
  background: #ff3838;
  color: white;
  animation: pulse-alarm 1.5s infinite;
}

.status-未处理 {
  color: #ff6b6b !important;
}

.status-处理中 {
  color: #ffa502 !important;
}

.status-已处理 {
  color: #2ed573 !important;
}

@keyframes pulse-alarm {
  0% {
    box-shadow: 0 0 0 0 rgba(255, 56, 56, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(255, 56, 56, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(255, 56, 56, 0);
      }
  }

/* New Draggable Panel Styles */
.draggable-panel {
  position: fixed; /* Use fixed to position relative to the viewport */
  z-index: 10;
  display: flex;
  flex-direction: column;
  gap: 15px;
  overflow: hidden; /* This was on the card, now on the panel */
  transition: all 0.3s ease;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(0, 212, 255, 0.3);
  min-width: 250px;
  min-height: 200px;
}

.draggable-panel .card-header {
  cursor: move; /* Indicate that the header is draggable */
  background: rgba(0, 212, 255, 0.1);
}

.draggable-panel .card-content {
  flex-grow: 1; /* Allow content to fill available space */
  padding: 20px;
  overflow-y: auto; /* Add scroll if content overflows */
}

/* Remove old panel containers as they are no longer used */
.left-panel,
.right-panel {
  display: none;
}

.resizable-handle {
  position: absolute;
  z-index: 20; /* Higher than content, lower than other UI elements if needed */
}

.resizable-handle-r {
  top: 0;
  right: -5px;
  bottom: 0;
  width: 10px;
  cursor: ew-resize;
}

.resizable-handle-b {
  left: 0;
  right: 0;
  bottom: -5px;
  height: 10px;
  cursor: ns-resize;
}

.resizable-handle-br {
  right: -5px;
  bottom: -5px;
  width: 20px;
  height: 20px;
  cursor: nwse-resize;
  z-index: 21;
}

.resizable-handle-l {
  top: 0;
  left: -5px;
  bottom: 0;
  width: 10px;
  cursor: ew-resize;
}

.resizable-handle-t {
  left: 0;
  right: 0;
  top: -5px;
  height: 10px;
  cursor: ns-resize;
}

.resizable-handle-tl {
  left: -5px;
  top: -5px;
  width: 20px;
  height: 20px;
  cursor: nwse-resize;
  z-index: 21;
}

.resizable-handle-tr {
  right: -5px;
  top: -5px;
  width: 20px;
  height: 20px;
  cursor: nesw-resize;
  z-index: 21;
}

.resizable-handle-bl {
  left: -5px;
  bottom: -5px;
  width: 20px;
  height: 20px;
  cursor: nesw-resize;
  z-index: 21;
}

</style> 