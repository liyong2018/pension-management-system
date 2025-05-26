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
          <span class="weather-icon">☀️</span>
          <span class="weather-text">晴天</span>
        </div>
      </div>
    </div>

    <!-- 顶部数据统计卡片 -->
    <div class="top-stats">
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

    <!-- 主要内容区域 -->
    <div class="main-dashboard">
      <!-- 左侧面板 -->
      <div class="left-panel">
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
                <div class="ability-value">{{ getAbilityCount('能力完好') }}人 ({{ getAbilityPercentage('能力完好') }}%)</div>
              </div>
              <div class="ability-item">
                <div class="ability-bar">
                  <div class="bar-fill blue" :style="{ width: getAbilityPercentage('轻度失能') + '%' }"></div>
                </div>
                <div class="ability-label">轻度失能</div>
                <div class="ability-value">{{ getAbilityCount('轻度失能') }}人 ({{ getAbilityPercentage('轻度失能') }}%)</div>
              </div>
              <div class="ability-item">
                <div class="ability-bar">
                  <div class="bar-fill orange" :style="{ width: getAbilityPercentage('中度失能') + '%' }"></div>
                </div>
                <div class="ability-label">中度失能</div>
                <div class="ability-value">{{ getAbilityCount('中度失能') }}人 ({{ getAbilityPercentage('中度失能') }}%)</div>
              </div>
              <div class="ability-item">
                <div class="ability-bar">
                  <div class="bar-fill red" :style="{ width: getAbilityPercentage('重度失能') + '%' }"></div>
                </div>
                <div class="ability-label">重度失能</div>
                <div class="ability-value">{{ getAbilityCount('重度失能') }}人 ({{ getAbilityPercentage('重度失能') }}%)</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 年龄分布 -->
        <div class="dashboard-card">
          <div class="card-header">
            <h3>长者年龄分布</h3>
            <div class="gender-legend">
              <span class="legend-item male">■ 男</span>
              <span class="legend-item female">■ 女</span>
            </div>
          </div>
          <div class="card-content">
            <div ref="ageChart" class="chart-container"></div>
          </div>
        </div>
      </div>

              <!-- 中间地图区域 -->
        <div class="center-panel">
          <div class="dashboard-card map-card">
            <div class="card-header">
              <h3>各社区养老机构分布</h3>
              <div class="map-controls">
                <div class="map-stats">
                  <div class="map-stat-item">
                    <span class="stat-dot blue"></span>
                    <span>日间照料: {{ facilityStats.dayCareCount }}个</span>
                  </div>
                  <div class="map-stat-item">
                    <span class="stat-dot orange"></span>
                    <span>养老院: {{ facilityStats.nursingHomeCount }}个</span>
                  </div>
                  <div class="map-stat-item">
                    <span class="stat-dot green"></span>
                    <span>居家服务: {{ facilityStats.homeCareCount }}个</span>
                  </div>
                </div>
                <div class="map-tools">
                  <button class="map-tool-btn" @click="toggleLayer('facilities')" :class="{ active: showFacilities }">
                    🏢 机构
                  </button>
                  <button class="map-tool-btn" @click="toggleLayer('elderly')" :class="{ active: showElderly }">
                    👥 老人
                  </button>
                  <button class="map-tool-btn" @click="toggleLayer('alarms')" :class="{ active: showAlarms }">
                    🚨 告警
                  </button>
                  <button class="map-tool-btn" @click="resetMapView">
                    🎯 重置
                  </button>
                </div>
              </div>
            </div>
            <div class="card-content">
              <div class="map-container">
                <div ref="mapContainer" id="leafletMap" class="leaflet-map"></div>
                <div class="map-legend">
                  <div class="legend-title">图例</div>
                  <div class="legend-items">
                    <div class="legend-item">
                      <span class="legend-marker blue"></span>
                      <span>日间照料中心</span>
                    </div>
                    <div class="legend-item">
                      <span class="legend-marker orange"></span>
                      <span>养老院</span>
                    </div>
                    <div class="legend-item">
                      <span class="legend-marker green"></span>
                      <span>居家服务点</span>
                    </div>
                    <div class="legend-item">
                      <span class="legend-marker red"></span>
                      <span>紧急告警</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

        <!-- 底部信息提示 -->
        <div class="info-panel">
          <div class="info-item">
            <div class="info-icon">📞</div>
            <div class="info-content">
              <div class="info-title">郭女士 (13577330798)</div>
              <div class="info-desc">不在郭女士的电子围栏电子围栏内</div>
              <div class="info-status emergency">【紧急报警】</div>
              <div class="info-time">2022-09-27 18:30:22</div>
            </div>
          </div>
          <div class="info-item">
            <div class="info-icon">🆘</div>
            <div class="info-content">
              <div class="info-title">郭女士 (13577330798)</div>
              <div class="info-desc">云南省红河哈尼族彝族自治州 蒙自市 丰泽街 靠近河滨作训学校</div>
              <div class="info-status sos">【SOS报警】</div>
              <div class="info-time">2022-09-27 17:32:39</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧面板 -->
      <div class="right-panel">
        <!-- SOS报警设备 -->
        <div class="dashboard-card">
          <div class="card-header">
            <h3>SOS报警设备</h3>
            <div class="device-count">{{ formatNumber(dashboardData?.deviceStats?.sosDeviceCount) }}/800(台数)</div>
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

        <!-- 烟感设备 -->
        <div class="dashboard-card">
          <div class="card-header">
            <h3>烟感设备</h3>
            <div class="device-count">良8.85</div>
          </div>
          <div class="card-content">
            <div class="device-status-ring">
              <div class="ring-chart">
                <div class="ring-progress orange" :style="{ '--progress': '88%' }"></div>
                <div class="ring-center">
                  <div class="ring-value">88%</div>
                  <div class="ring-label">正常率</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 设备运行状态 -->
        <div class="dashboard-card">
          <div class="card-header">
            <h3>设备运行状态</h3>
          </div>
          <div class="card-content">
            <div class="device-status-table">
              <div class="status-row header">
                <div class="status-col">设备名称</div>
                <div class="status-col">设备数量</div>
                <div class="status-col">设备故障数量</div>
              </div>
              <div class="status-row">
                <div class="status-col">红外探测器</div>
                <div class="status-col">2</div>
                <div class="status-col">0</div>
              </div>
              <div class="status-row">
                <div class="status-col">门磁传感器</div>
                <div class="status-col">1</div>
                <div class="status-col">0</div>
              </div>
              <div class="status-row">
                <div class="status-col">水浸传感器</div>
                <div class="status-col">2</div>
                <div class="status-col">0</div>
              </div>
              <div class="status-row">
                <div class="status-col">光照传感器</div>
                <div class="status-col">0</div>
                <div class="status-col">0</div>
              </div>
              <div class="status-row">
                <div class="status-col">紧急按钮</div>
                <div class="status-col">3</div>
                <div class="status-col">2</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 未处理告警 -->
        <div class="dashboard-card">
          <div class="card-header">
            <h3>未处理</h3>
            <div class="alarm-controls">
              <button class="control-btn">🔄</button>
              <button class="control-btn">📋</button>
            </div>
          </div>
          <div class="card-content">
            <div class="alarm-list" v-loading="alarmLoading">
              <div v-for="alarm in alarmList" :key="alarm.time" class="alarm-item">
                <div class="alarm-type" :class="getAlarmLevelClass(alarm.level)">
                  {{ alarm.type }}
                </div>
                <div class="alarm-content">
                  <div class="alarm-location">{{ alarm.location }}</div>
                  <div class="alarm-time">{{ alarm.time }}</div>
                </div>
                <div class="alarm-status" :class="getAlarmStatusClass(alarm.status)">
                  {{ alarm.status }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted, computed } from 'vue';
import * as echarts from 'echarts';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

// 响应式数据
const dashboardData = ref(null);
const alarmList = ref([]);
const loading = ref(false);
const alarmLoading = ref(false);
const currentTime = ref('');

// 地图相关状态
const showFacilities = ref(true);
const showElderly = ref(false);
const showAlarms = ref(true);
const mapInstance = ref(null);
const mapLayers = ref({
  facilities: null,
  elderly: null,
  alarms: null
});

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
    case '能力完好': return stats.normalCount || 0;
    case '轻度失能': return stats.mildCount || 0;
    case '中度失能': return stats.moderateCount || 0;
    case '重度失能': return stats.severeCount || 0;
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
  const total = dashboardData.value.deviceStats.sosDeviceCount || 800;
  const online = Math.floor(total * 0.85); // 假设85%在线率
  return Math.round((online / total) * 100);
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
    const response = await fetch('/api/dashboard/stats');
    if (response.ok) {
      const result = await response.json();
      if (result.success) {
        dashboardData.value = result.data;
        console.log('首页数据加载成功:', dashboardData.value);
        
        // 数据加载完成后初始化图表
        await nextTick();
        initCharts();
      } else {
        console.error('加载首页数据失败:', result.message);
        // 使用模拟数据
        loadMockData();
      }
    } else {
      console.error('加载首页数据失败:', response.status);
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
      gasLeakCount: 67
    },
    abilityStats: {
      normalCount: 8567,
      mildCount: 2341,
      moderateCount: 1456,
      severeCount: 576
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
          id: 1,
          name: '朝阳公园社区',
          lat: 39.9289,
          lng: 116.4203,
          elderlyCount: 1245,
          facilities: [
            { type: 'daycare', name: '朝阳日间照料中心', lat: 39.9289, lng: 116.4203 },
            { type: 'nursing', name: '朝阳养老院', lat: 39.9295, lng: 116.4210 }
          ]
        },
        {
          id: 2,
          name: '中关村社区',
          lat: 39.9831,
          lng: 116.3145,
          elderlyCount: 2156,
          facilities: [
            { type: 'daycare', name: '中关村日间照料中心', lat: 39.9831, lng: 116.3145 },
            { type: 'homecare', name: '中关村居家服务点', lat: 39.9825, lng: 116.3150 }
          ]
        },
        {
          id: 3,
          name: '东直门社区',
          lat: 39.9434,
          lng: 116.4217,
          elderlyCount: 1876,
          facilities: [
            { type: 'daycare', name: '东直门日间照料中心', lat: 39.9434, lng: 116.4217 }
          ]
        },
        {
          id: 4,
          name: '三里屯社区',
          lat: 39.9364,
          lng: 116.4472,
          elderlyCount: 987,
          facilities: [
            { type: 'homecare', name: '三里屯居家服务点', lat: 39.9364, lng: 116.4472 }
          ]
        },
        {
          id: 5,
          name: '丰台社区',
          lat: 39.8583,
          lng: 116.2867,
          elderlyCount: 1654,
          facilities: [
            { type: 'daycare', name: '丰台日间照料中心', lat: 39.8583, lng: 116.2867 }
          ]
        },
        {
          id: 6,
          name: '海淀社区',
          lat: 39.9590,
          lng: 116.2982,
          elderlyCount: 2345,
          facilities: [
            { type: 'daycare', name: '海淀日间照料中心', lat: 39.9590, lng: 116.2982 },
            { type: 'nursing', name: '海淀养老院', lat: 39.9585, lng: 116.2975 }
          ]
        }
      ],
      alarms: [
        { id: 1, type: 'sos', lat: 39.9289, lng: 116.4203, name: '张建国', time: '15:30:22' },
        { id: 2, type: 'smoke', lat: 39.9831, lng: 116.3145, name: '李秀英', time: '14:45:15' },
        { id: 3, type: 'fall', lat: 39.9434, lng: 116.4217, name: '王福寿', time: '13:20:08' }
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
  if (!mapContainer.value || !dashboardData.value?.mapData) return;

  // 创建地图实例（以北京为中心）
  mapInstance.value = L.map('leafletMap').setView([39.9042, 116.4074], 11);

  // 添加OpenStreetMap瓦片图层
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors',
    maxZoom: 18
  }).addTo(mapInstance.value);

  // 创建图层组
  mapLayers.value.facilities = L.layerGroup().addTo(mapInstance.value);
  mapLayers.value.elderly = L.layerGroup();
  mapLayers.value.alarms = L.layerGroup().addTo(mapInstance.value);

  // 添加养老机构标记
  addFacilityMarkers();
  
  // 添加老人分布标记
  addElderlyMarkers();
  
  // 添加告警标记
  addAlarmMarkers();
};

// 添加养老机构标记
const addFacilityMarkers = () => {
  if (!dashboardData.value?.mapData?.communities) return;

  dashboardData.value.mapData.communities.forEach(community => {
    community.facilities.forEach(facility => {
      let iconColor = '#00d4ff';
      let iconHtml = '🏢';
      
      switch (facility.type) {
        case 'daycare':
          iconColor = '#00d4ff';
          iconHtml = '🏢';
          break;
        case 'nursing':
          iconColor = '#ff6b35';
          iconHtml = '🏥';
          break;
        case 'homecare':
          iconColor = '#2ed573';
          iconHtml = '🏠';
          break;
      }

      const customIcon = L.divIcon({
        html: `<div style="background: ${iconColor}; color: white; border-radius: 50%; width: 30px; height: 30px; display: flex; align-items: center; justify-content: center; font-size: 16px; border: 2px solid white; box-shadow: 0 2px 8px rgba(0,0,0,0.3);">${iconHtml}</div>`,
        className: 'custom-marker',
        iconSize: [30, 30],
        iconAnchor: [15, 15]
      });

      const marker = L.marker([facility.lat, facility.lng], { icon: customIcon })
        .bindPopup(`
          <div style="color: #333; font-family: 'Microsoft YaHei';">
            <h4 style="margin: 0 0 8px 0; color: ${iconColor};">${facility.name}</h4>
            <p style="margin: 0; font-size: 12px;">所属社区: ${community.name}</p>
            <p style="margin: 4px 0 0 0; font-size: 12px;">老人数量: ${community.elderlyCount}人</p>
          </div>
        `);

      mapLayers.value.facilities.addLayer(marker);
    });
  });
};

// 添加老人分布标记
const addElderlyMarkers = () => {
  if (!dashboardData.value?.mapData?.communities) return;

  dashboardData.value.mapData.communities.forEach(community => {
    const radius = Math.sqrt(community.elderlyCount / 100) * 10;
    
    const circle = L.circle([community.lat, community.lng], {
      color: '#7b68ee',
      fillColor: '#7b68ee',
      fillOpacity: 0.3,
      radius: radius * 100
    }).bindPopup(`
      <div style="color: #333; font-family: 'Microsoft YaHei';">
        <h4 style="margin: 0 0 8px 0; color: #7b68ee;">${community.name}</h4>
        <p style="margin: 0; font-size: 12px;">老人数量: ${community.elderlyCount}人</p>
        <p style="margin: 4px 0 0 0; font-size: 12px;">机构数量: ${community.facilities.length}个</p>
      </div>
    `);

    mapLayers.value.elderly.addLayer(circle);
  });
};

// 添加告警标记
const addAlarmMarkers = () => {
  if (!dashboardData.value?.mapData?.alarms) return;

  dashboardData.value.mapData.alarms.forEach(alarm => {
    let iconColor = '#ff4757';
    let iconHtml = '🚨';
    let alarmType = '告警';
    
    switch (alarm.type) {
      case 'sos':
        iconColor = '#ff4757';
        iconHtml = '🆘';
        alarmType = 'SOS报警';
        break;
      case 'smoke':
        iconColor = '#ff6b35';
        iconHtml = '🔥';
        alarmType = '烟感报警';
        break;
      case 'fall':
        iconColor = '#ffa502';
        iconHtml = '⚠️';
        alarmType = '跌倒报警';
        break;
    }

    const customIcon = L.divIcon({
      html: `<div style="background: ${iconColor}; color: white; border-radius: 50%; width: 25px; height: 25px; display: flex; align-items: center; justify-content: center; font-size: 12px; border: 2px solid white; box-shadow: 0 2px 8px rgba(0,0,0,0.3); animation: pulse 2s infinite;">${iconHtml}</div>`,
      className: 'alarm-marker',
      iconSize: [25, 25],
      iconAnchor: [12.5, 12.5]
    });

    const marker = L.marker([alarm.lat, alarm.lng], { icon: customIcon })
      .bindPopup(`
        <div style="color: #333; font-family: 'Microsoft YaHei';">
          <h4 style="margin: 0 0 8px 0; color: ${iconColor};">${alarmType}</h4>
          <p style="margin: 0; font-size: 12px;">报警人: ${alarm.name}</p>
          <p style="margin: 4px 0 0 0; font-size: 12px;">时间: ${alarm.time}</p>
        </div>
      `);

    mapLayers.value.alarms.addLayer(marker);
  });
};

// 切换图层显示
const toggleLayer = (layerType) => {
  const layer = mapLayers.value[layerType];
  if (!layer || !mapInstance.value) return;

  switch (layerType) {
    case 'facilities':
      showFacilities.value = !showFacilities.value;
      if (showFacilities.value) {
        mapInstance.value.addLayer(layer);
      } else {
        mapInstance.value.removeLayer(layer);
      }
      break;
    case 'elderly':
      showElderly.value = !showElderly.value;
      if (showElderly.value) {
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
        data: ['60-65', '66-70', '71-75', '76-80', '81-85', '86-90', '90以上'],
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
          data: [1200, 1100, 950, 800, 650, 400, 200],
          itemStyle: { color: '#00d4ff' }
        },
        {
          name: '女',
          type: 'bar',
          data: [1350, 1250, 1100, 900, 750, 500, 300],
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
</script>

<style scoped>
.smart-dashboard {
  min-height: 100vh;
  background: linear-gradient(135deg, #0c1426 0%, #1a2332 50%, #0c1426 100%);
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 40px;
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
.top-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  padding: 20px 40px;
  margin-bottom: 20px;
}

.stat-card {
  background: rgba(0, 0, 0, 0.4);
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

/* 主要内容区域 */
.main-dashboard {
  display: grid;
  grid-template-columns: 320px 1fr 320px;
  gap: 20px;
  padding: 0 40px 40px;
  height: calc(100vh - 200px);
}

.left-panel,
.right-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.center-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 卡片样式 */
.dashboard-card {
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(0, 212, 255, 0.3);
  overflow: hidden;
  transition: all 0.3s ease;
}

.dashboard-card:hover {
  border-color: rgba(0, 212, 255, 0.6);
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
}

/* 地图卡片 */
.map-card {
  flex: 2;
}

.map-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.map-stats {
  display: flex;
  gap: 16px;
}

.map-stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #ffffff;
}

.stat-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.stat-dot.blue { background: #00d4ff; }
.stat-dot.orange { background: #ff6b35; }
.stat-dot.green { background: #2ed573; }

.map-tools {
  display: flex;
  gap: 8px;
}

.map-tool-btn {
  background: rgba(0, 212, 255, 0.2);
  border: 1px solid rgba(0, 212, 255, 0.3);
  color: #00d4ff;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 12px;
  white-space: nowrap;
}

.map-tool-btn:hover {
  background: rgba(0, 212, 255, 0.3);
  transform: translateY(-1px);
}

.map-tool-btn.active {
  background: rgba(0, 212, 255, 0.4);
  border-color: rgba(0, 212, 255, 0.6);
  box-shadow: 0 0 10px rgba(0, 212, 255, 0.3);
}

.map-container {
  height: 400px;
  position: relative;
  border-radius: 8px;
  overflow: hidden;
}

.leaflet-map {
  width: 100%;
  height: 100%;
  border-radius: 8px;
}

.map-legend {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 8px;
  padding: 12px;
  border: 1px solid rgba(0, 212, 255, 0.3);
  z-index: 1000;
}

.legend-title {
  font-size: 12px;
  font-weight: 600;
  color: #00d4ff;
  margin-bottom: 8px;
}

.legend-items {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  color: #ffffff;
}

.legend-marker {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 1px solid white;
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

/* 告警列表 */
.alarm-list {
  max-height: 200px;
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

/* 信息面板 */
.info-panel {
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(0, 212, 255, 0.3);
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  border-left: 3px solid #ff4757;
}

.info-icon {
  font-size: 20px;
  margin-top: 2px;
}

.info-content {
  flex: 1;
}

.info-title {
  font-size: 14px;
  color: #ffffff;
  font-weight: 600;
  margin-bottom: 4px;
}

.info-desc {
  font-size: 12px;
  color: #ffffff;
  opacity: 0.8;
  margin-bottom: 8px;
  line-height: 1.4;
}

.info-status {
  display: inline-block;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 500;
  margin-bottom: 4px;
}

.info-status.emergency {
  background: rgba(255, 71, 87, 0.2);
  color: #ff4757;
  border: 1px solid rgba(255, 71, 87, 0.3);
}

.info-status.sos {
  background: rgba(255, 107, 53, 0.2);
  color: #ff6b35;
  border: 1px solid rgba(255, 107, 53, 0.3);
}

.info-time {
  font-size: 11px;
  color: #ffffff;
  opacity: 0.6;
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .main-dashboard {
    grid-template-columns: 280px 1fr 280px;
  }
  
  .system-title {
    font-size: 28px;
  }
}

@media (max-width: 1200px) {
  .main-dashboard {
    grid-template-columns: 1fr;
    grid-template-rows: auto auto auto;
  }
  
  .left-panel,
  .right-panel {
    flex-direction: row;
    overflow-x: auto;
  }
  
  .left-panel .dashboard-card,
  .right-panel .dashboard-card {
    min-width: 300px;
  }
  
  .top-stats {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    padding: 15px 20px;
  }
  
  .system-title {
    font-size: 24px;
  }
  
  .top-stats {
    grid-template-columns: 1fr;
    padding: 15px 20px;
  }
  
  .main-dashboard {
    padding: 0 20px 20px;
    gap: 15px;
  }
  
  .card-content {
    padding: 15px;
  }
  
  .chart-container {
    height: 150px;
  }
}

/* Leaflet地图自定义样式 */
:deep(.leaflet-control-container) {
  font-family: 'Microsoft YaHei', sans-serif;
}

:deep(.leaflet-popup-content-wrapper) {
  background: rgba(0, 0, 0, 0.9);
  border-radius: 8px;
  border: 1px solid rgba(0, 212, 255, 0.3);
}

:deep(.leaflet-popup-tip) {
  background: rgba(0, 0, 0, 0.9);
  border: 1px solid rgba(0, 212, 255, 0.3);
}

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
</style> 