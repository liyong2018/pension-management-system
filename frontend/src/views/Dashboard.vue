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

    <!-- 顶部数据统计卡片 -->
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

    <!-- 左侧面板 -->
    <div class="left-panel" ref="leftPanelRef">
      <!-- 老人类型统计 -->
      <div class="dashboard-card">
        <div class="card-header">
          <h3>老人类型分析</h3>
        </div>
        <div class="card-content">
          <div ref="elderlyTypeChart" class="chart-container"></div>
        </div>
      </div>

      <!-- 能力评估统计 -->
      <div class="dashboard-card">
        <div class="card-header">
          <h3>能力评估</h3>
        </div>
        <div class="card-content">
          <div class="ability-stats">
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
        </div>
      </div>
    </div>

    <!-- 右侧面板 -->
    <div class="right-panel" ref="rightPanelRef">
      <!-- SOS报警设备 -->
      <div class="dashboard-card">
        <div class="card-header">
          <h3>SOS设备</h3>
          <div class="device-count">{{ formatNumber(dashboardData?.deviceStats?.sosDeviceCount) }}台</div>
        </div>
        <div class="card-content">
          <div class="device-status-ring">
            <div class="ring-chart">
              <div class="ring-progress" :style="{ '--progress': getSosProgress() + '%' }"></div>
              <div class="ring-center">
                <div class="ring-value">{{ getSosProgress() }}%</div>
                <div class="ring-label">在线率</div>
              </div>
            </div>
          </div>
        </div>
      </div>

                <!-- 未处理告警 -->
          <div class="dashboard-card alarm-card-auto">
            <div class="card-header">
              <h3>未处理告警</h3>
              <div class="alarm-controls">
                <span class="alarm-count">{{ formatNumber(dashboardData?.alarmStats?.unhandledCount) }}条</span>
                <button class="control-btn" @click="loadAlarmData">🔄</button>
              </div>
            </div>
            <div class="card-content alarm-card-content">
              <div class="alarm-list alarm-list-auto" v-loading="alarmLoading">
                <div v-for="alarm in alarmList" :key="alarm.time" class="alarm-item">
                  <div class="alarm-type" :class="getAlarmLevelClass(alarm.level)">
                    {{ alarm.type }}
                  </div>
                  <div class="alarm-content">
                    <div class="alarm-location">{{ alarm.location }}</div>
                    <div class="alarm-time">{{ alarm.time }}</div>
                  </div>
                </div>
                <!-- 如果没有告警数据 -->
                <div v-if="!alarmList.length && !alarmLoading" class="no-alarm">
                  <div class="no-alarm-icon">✅</div>
                  <div class="no-alarm-text">暂无未处理告警</div>
                  <div class="no-alarm-desc">系统运行正常</div>
                </div>
              </div>
            </div>
          </div>
    </div>
    <!-- 图例面板 -->
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
    <!-- 底部控制栏 -->
    <div class="bottom-controls">
      
      <!-- 地图控制按钮 -->
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
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted, computed } from 'vue';
import * as echarts from 'echarts';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import request from '@/utils/request';

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
// const showElderly = ref(false); // 移除老人数据展示
const showAlarms = ref(true);
const mapInstance = ref(null);
const mapLayers = ref({
  communities: null,
  organizations: null,
  // elderly: null, // 移除老人图层
  alarms: null
});

const topStatsRef = ref(null);
const leftPanelRef = ref(null);
const rightPanelRef = ref(null);

// 图表引用
const elderlyTypeChart = ref(null);
const ageChart = ref(null);
const mapContainer = ref(null);

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
    // case 'elderly': return mapData.elderly?.length || 0; // 移除老人数据计数
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
  const levelMap = {
    '紧急': 'emergency',
    '高': 'high',
    '中': 'medium',
    '低': 'low'
  };
  return levelMap[level] || 'low';
};

// 获取告警状态样式类
const getAlarmStatusClass = (status) => {
  const statusMap = {
    '未处理': 'pending',
    '处理中': 'processing',
    '已处理': 'completed'
  };
  return statusMap[status] || 'pending';
};

// 加载首页数据
const loadDashboardData = async () => {
  loading.value = true;
  try {
    const result = await request({
      url: '/dashboard/stats',
      method: 'GET'
    });
    
    if (result) {
      dashboardData.value = result;
      console.log('首页数据加载成功:', dashboardData.value);
      
      // 数据加载完成后初始化图表和地图
      await nextTick();
      initCharts();
      initMap();
    } else {
      console.error('加载首页数据失败: 返回数据为空');
      // 使用模拟数据
      loadMockData();
    }
  } catch (error) {
    console.error('加载首页数据异常:', error);
    // 使用模拟数据
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
  
  // 初始化图表和地图
  nextTick(() => {
    initCharts();
    initMap();
  });
};

// 加载告警数据
const loadAlarmData = async () => {
  alarmLoading.value = true;
  try {
    const result = await request({
      url: '/dashboard/alarms/recent',
      method: 'GET'
    });
    
    if (result) {
      alarmList.value = result;
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
  if (!mapContainer.value || !dashboardData.value?.mapData) return;

  // 创建地图实例（以北京为中心）
  mapInstance.value = L.map('leafletMap', {
    zoomControl: true,
    attributionControl: true
  }).setView([39.9042, 116.4074], 11);

  // 添加OpenStreetMap瓦片图层
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors',
    maxZoom: 18,
    opacity: 0.8
  }).addTo(mapInstance.value);

  // 创建图层组
  mapLayers.value.communities = L.layerGroup().addTo(mapInstance.value);
  mapLayers.value.organizations = L.layerGroup().addTo(mapInstance.value);
  // mapLayers.value.elderly = L.layerGroup(); // 移除老人图层
  mapLayers.value.alarms = L.layerGroup().addTo(mapInstance.value);

  // 添加各类标记
  addCommunityMarkers();
  addOrganizationMarkers();
  // addElderlyMarkers(); // 移除老人标记
  addAlarmMarkers();

  mapInstance.value.on('popupopen', (e) => {
    const popup = e.popup;
    const mapContainerElement = mapInstance.value.getContainer(); // Renamed to avoid conflict
    const popupElement = popup.getElement();

    if (!popupElement || !topStatsRef.value || !leftPanelRef.value || !rightPanelRef.value || !mapContainerElement) {
      return;
    }

    // 等待DOM更新，确保获取到正确的尺寸
    nextTick(() => {
      const mapRect = mapContainerElement.getBoundingClientRect();
      const popupRect = popupElement.getBoundingClientRect();
      
      const topStatsRect = topStatsRef.value.getBoundingClientRect();
      const leftPanelRect = leftPanelRef.value.getBoundingClientRect();
      const rightPanelRect = rightPanelRef.value.getBoundingClientRect();

      const safetyMargin = 20; // 20px的安全边距

      let panX = 0;
      let panY = 0;

      // 检查顶部遮挡
      // 计算 topStats 面板底部相对于地图容器顶部的 Y 坐标
      const topStatsBottomRelativeToMap = topStatsRect.bottom - mapRect.top;
      // 计算弹出框顶部相对于地图容器顶部的 Y 坐标
      const popupTopRelativeToMap = popupRect.top - mapRect.top;
      if (popupTopRelativeToMap < topStatsBottomRelativeToMap + safetyMargin) {
        panY = (topStatsBottomRelativeToMap + safetyMargin) - popupTopRelativeToMap;
      }

      // 检查左侧遮挡
      // 计算 leftPanel 面板右侧相对于地图容器左侧的 X 坐标
      const leftPanelRightRelativeToMap = leftPanelRect.right - mapRect.left;
      // 计算弹出框左侧相对于地图容器左侧的 X 坐标
      const popupLeftRelativeToMap = popupRect.left - mapRect.left;
      if (popupLeftRelativeToMap < leftPanelRightRelativeToMap + safetyMargin) {
        panX = (leftPanelRightRelativeToMap + safetyMargin) - popupLeftRelativeToMap;
      }

      // 检查右侧遮挡
      // 计算 rightPanel 面板左侧相对于地图容器左侧的 X 坐标
      const rightPanelLeftRelativeToMap = rightPanelRect.left - mapRect.left;
      // 计算弹出框右侧相对于地图容器左侧的 X 坐标
      const popupRightRelativeToMap = popupRect.right - mapRect.left;
      if (popupRightRelativeToMap > rightPanelLeftRelativeToMap - safetyMargin) {
        // 如果 panX 已经因为左侧面板有了值，我们优先处理左侧的遮挡，避免冲突
        // 这里简单处理，如果左侧已经需要向右移动，就不再因为右侧遮挡而向左移动
        // 更完善的逻辑可能需要判断哪边遮挡更多，或者是否同时遮挡
        if (panX <= 0) { 
          panX = - (popupRightRelativeToMap - (rightPanelLeftRelativeToMap - safetyMargin));
        }
      }
      
      // 如果有需要平移的量
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

// 添加老人分布标记
const addElderlyMarkers = () => {
  if (!dashboardData.value?.mapData?.elderly) return;

  dashboardData.value.mapData.elderly.forEach(elderly => {
    let iconColor = '#2ed573';
    let iconHtml = '👤';
    
    // 根据老人类型设置图标
    switch (elderly.elderlyType) {
      case 'empty_nest':
        iconColor = '#ff6b35';
        iconHtml = '🏠';
        break;
      case 'living_alone':
        iconColor = '#7b68ee';
        iconHtml = '👤';
        break;
      case 'disabled':
        iconColor = '#ff4757';
        iconHtml = '♿';
        break;
      case 'elderly':
        iconColor = '#ffa502';
        iconHtml = '👴';
        break;
      case 'low_income':
        iconColor = '#ff6348';
        iconHtml = '💰';
        break;
      case 'special_care':
        iconColor = '#ff9ff3';
        iconHtml = '🏥';
        break;
      default:
        iconColor = '#2ed573';
        iconHtml = '👤';
        break;
    }

    const customIcon = L.divIcon({
      html: `<div style="background: ${iconColor}; color: white; border-radius: 50%; width: 25px; height: 25px; display: flex; align-items: center; justify-content: center; font-size: 12px; border: 2px solid white; box-shadow: 0 2px 8px rgba(0,0,0,0.3);">${iconHtml}</div>`,
      className: 'elderly-marker',
      iconSize: [25, 25],
      iconAnchor: [12.5, 12.5]
    });

    const marker = L.marker([elderly.latitude, elderly.longitude], { icon: customIcon })
      .bindPopup(`
        <div style="color: #333; font-family: 'Microsoft YaHei';">
          <h4 style="margin: 0 0 8px 0; color: ${iconColor};">${elderly.elderlyName}</h4>
          <p style="margin: 0; font-size: 12px;">社区: ${elderly.community}</p>
          <p style="margin: 4px 0 0 0; font-size: 12px;">地址: ${elderly.address || '未填写'}</p>
          <p style="margin: 4px 0 0 0; font-size: 12px;">年龄: ${elderly.age}岁</p>
          <p style="margin: 4px 0 0 0; font-size: 12px;">性别: ${elderly.gender}</p>
          <p style="margin: 4px 0 0 0; font-size: 12px;">老人类型: ${getElderlyTypeLabel(elderly.elderlyType)}</p>
          <p style="margin: 4px 0 0 0; font-size: 12px;">能力评估: ${elderly.abilityAssessment || '未评估'}</p>
        </div>
      `, {
        maxWidth: 320,
        className: 'custom-popup-wrapper',
        offset: [10, 10],
        autoPan: false
      });

    mapLayers.value.elderly.addLayer(marker);
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
    // case 'elderly': // 移除老人图层切换逻辑
    //   showElderly.value = !showElderly.value;
    //   if (showElderly.value) {
    //     mapInstance.value.addLayer(layer);
    //   } else {
    //     mapInstance.value.removeLayer(layer);
    //   }
    //   break;
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
  
  // 老人类型统计饼图
  if (elderlyTypeChart.value) {
    const elderlyChart = echarts.init(elderlyTypeChart.value);
    const elderlyData = dashboardData.value.elderlyTypeStats;
    elderlyChart.setOption({
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
        itemHeight: 12
      },
      series: [{
        name: '老人类型',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['65%', '50%'],
        data: [
          { value: elderlyData?.normalCount || 0, name: '普通老人', itemStyle: { color: '#00d4ff' } },
          { value: elderlyData?.emptyNestCount || 0, name: '空巢老人', itemStyle: { color: '#ff6b35' } },
          { value: elderlyData?.livingAloneCount || 0, name: '独居老人', itemStyle: { color: '#7b68ee' } },
          { value: elderlyData?.disabledCount || 0, name: '失能老人', itemStyle: { color: '#ff4757' } },
          { value: elderlyData?.elderlyCount || 0, name: '高龄老人', itemStyle: { color: '#2ed573' } },
          { value: elderlyData?.lowIncomeCount || 0, name: '低收入老人', itemStyle: { color: '#ffa502' } },
          { value: elderlyData?.specialCareCount || 0, name: '特殊照护', itemStyle: { color: '#ff6348' } }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        label: {
          show: false
        }
      }]
    });
  }

  // 年龄分布柱状图
  if (ageChart.value) {
    const ageChartInstance = echarts.init(ageChart.value);
    
    // 使用真实的年龄分布数据
    const ageData = dashboardData.value?.ageDistribution;
    const maleData = [
      Math.round((ageData?.age60to69Count || 0) * 0.48), // 假设男性占48%
      Math.round((ageData?.age70to79Count || 0) * 0.47),
      Math.round((ageData?.age80to89Count || 0) * 0.45),
      Math.round((ageData?.age90PlusCount || 0) * 0.42)
    ];
    const femaleData = [
      Math.round((ageData?.age60to69Count || 0) * 0.52), // 假设女性占52%
      Math.round((ageData?.age70to79Count || 0) * 0.53),
      Math.round((ageData?.age80to89Count || 0) * 0.55),
      Math.round((ageData?.age90PlusCount || 0) * 0.58)
    ];
    
    ageChartInstance.setOption({
      backgroundColor: 'transparent',
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(0, 0, 0, 0.8)',
        borderColor: '#00d4ff',
        borderWidth: 1,
        textStyle: { color: '#fff' }
      },
      legend: {
        data: ['男', '女'],
        textStyle: { color: '#fff' },
        top: 10
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        top: '15%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: ['60-69岁', '70-79岁', '80-89岁', '90岁以上'],
        axisLine: { lineStyle: { color: '#00d4ff' } },
        axisLabel: { color: '#fff', fontSize: 10 }
      },
      yAxis: {
        type: 'value',
        axisLine: { lineStyle: { color: '#00d4ff' } },
        axisLabel: { color: '#fff', fontSize: 10 },
        splitLine: { lineStyle: { color: 'rgba(0, 212, 255, 0.2)' } }
      },
      series: [
        {
          name: '男',
          type: 'bar',
          data: maleData,
          itemStyle: { color: '#00d4ff' }
        },
        {
          name: '女',
          type: 'bar',
          data: femaleData,
          itemStyle: { color: '#ff6b35' }
        }
      ]
    });
  }
};

// 组件挂载
onMounted(() => {
  updateTime();
  timeInterval = setInterval(updateTime, 1000);
  loadWeatherData();
  loadDashboardData();
  loadAlarmData();
});

// 组件卸载
onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval);
  }
  
  // 清理地图实例
  if (mapInstance.value) {
    mapInstance.value.remove();
    mapInstance.value = null;
  }
});

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
  bottom: 5px;
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
  bottom: 5px;
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
  bottom: 8px;
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
</style> 