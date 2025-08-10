// 健康管理页面
Page({
  data: {
    loading: true,
    currentDate: '',
    currentChart: 'bloodPressure',
    
    // 健康数据
    healthData: {
      bloodPressure: {
        systolic: 120,
        diastolic: 80,
        status: 'normal',
        statusText: '正常'
      },
      heartRate: {
        value: 72,
        status: 'normal',
        statusText: '正常'
      },
      bloodSugar: {
        value: 5.6,
        status: 'normal',
        statusText: '正常'
      },
      temperature: {
        value: 36.5,
        status: 'normal',
        statusText: '正常'
      }
    },
    
    // 图表选项卡
    chartTabs: [
      { type: 'bloodPressure', name: '血压' },
      { type: 'heartRate', name: '心率' },
      { type: 'bloodSugar', name: '血糖' },
      { type: 'temperature', name: '体温' }
    ],
    
    chartSummary: '您的血压在过去一周内保持稳定，建议继续保持良好的生活习惯。',
    
    // 用药提醒
    medications: [
      {
        id: 1,
        name: '降压药',
        dosage: '1片',
        time: '08:00',
        taken: true
      },
      {
        id: 2,
        name: '维生素D',
        dosage: '2粒',
        time: '12:00',
        taken: false
      },
      {
        id: 3,
        name: '钙片',
        dosage: '1片',
        time: '18:00',
        taken: false
      }
    ],
    
    // 健康建议
    healthSuggestions: [
      {
        id: 1,
        type: 'diet',
        icon: '/images/suggestion-diet.png',
        title: '饮食建议',
        description: '建议多食用富含纤维的蔬菜水果，减少盐分摄入'
      },
      {
        id: 2,
        type: 'exercise',
        icon: '/images/suggestion-exercise.png',
        title: '运动建议',
        description: '每天进行30分钟的轻度运动，如散步或太极'
      },
      {
        id: 3,
        type: 'sleep',
        icon: '/images/suggestion-sleep.png',
        title: '睡眠建议',
        description: '保持规律作息，每晚睡眠时间不少于7小时'
      }
    ],
    
    // 最近记录
    recentRecords: [
      {
        id: 1,
        day: '15',
        month: '12月',
        type: '血压',
        value: '120/80 mmHg',
        time: '08:30',
        status: 'normal',
        statusText: '正常'
      },
      {
        id: 2,
        day: '14',
        month: '12月',
        type: '血糖',
        value: '5.8 mmol/L',
        time: '07:45',
        status: 'normal',
        statusText: '正常'
      },
      {
        id: 3,
        day: '13',
        month: '12月',
        type: '心率',
        value: '75 次/分',
        time: '09:15',
        status: 'normal',
        statusText: '正常'
      }
    ]
  },

  onLoad(options) {
    this.initPage();
    this.loadHealthData();
  },

  onShow() {
    this.refreshHealthData();
  },

  onPullDownRefresh() {
    this.loadHealthData().then(() => {
      wx.stopPullDownRefresh();
    });
  },

  // 初始化页面
  initPage() {
    const now = new Date();
    const currentDate = `${now.getMonth() + 1}月${now.getDate()}日`;
    this.setData({ currentDate });
  },

  // 加载健康数据
  async loadHealthData() {
    try {
      this.setData({ loading: true });
      
      // 模拟API调用
      await this.simulateApiCall();
      
      // 更新健康数据
      await this.updateHealthData();
      
      this.setData({ loading: false });
    } catch (error) {
      console.error('加载健康数据失败:', error);
      this.setData({ loading: false });
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    }
  },

  // 刷新健康数据
  async refreshHealthData() {
    // 静默刷新，不显示loading
    try {
      await this.updateHealthData();
    } catch (error) {
      console.error('刷新健康数据失败:', error);
    }
  },

  // 更新健康数据
  async updateHealthData() {
    // 模拟从设备或API获取最新数据
    const healthData = {
      bloodPressure: {
        systolic: 118 + Math.floor(Math.random() * 10),
        diastolic: 78 + Math.floor(Math.random() * 8),
        status: 'normal',
        statusText: '正常'
      },
      heartRate: {
        value: 70 + Math.floor(Math.random() * 10),
        status: 'normal',
        statusText: '正常'
      },
      bloodSugar: {
        value: (5.2 + Math.random() * 1.0).toFixed(1),
        status: 'normal',
        statusText: '正常'
      },
      temperature: {
        value: (36.3 + Math.random() * 0.6).toFixed(1),
        status: 'normal',
        statusText: '正常'
      }
    };
    
    // 判断健康状态
    this.evaluateHealthStatus(healthData);
    
    this.setData({ healthData });
  },

  // 评估健康状态
  evaluateHealthStatus(healthData) {
    // 血压评估
    const { systolic, diastolic } = healthData.bloodPressure;
    if (systolic >= 140 || diastolic >= 90) {
      healthData.bloodPressure.status = 'danger';
      healthData.bloodPressure.statusText = '偏高';
    } else if (systolic >= 130 || diastolic >= 85) {
      healthData.bloodPressure.status = 'warning';
      healthData.bloodPressure.statusText = '偏高';
    }
    
    // 心率评估
    const heartRate = healthData.heartRate.value;
    if (heartRate > 100 || heartRate < 60) {
      healthData.heartRate.status = 'warning';
      healthData.heartRate.statusText = '异常';
    }
    
    // 血糖评估
    const bloodSugar = parseFloat(healthData.bloodSugar.value);
    if (bloodSugar >= 7.0) {
      healthData.bloodSugar.status = 'danger';
      healthData.bloodSugar.statusText = '偏高';
    } else if (bloodSugar >= 6.1) {
      healthData.bloodSugar.status = 'warning';
      healthData.bloodSugar.statusText = '偏高';
    }
    
    // 体温评估
    const temperature = parseFloat(healthData.temperature.value);
    if (temperature >= 37.5) {
      healthData.temperature.status = 'danger';
      healthData.temperature.statusText = '发热';
    } else if (temperature >= 37.0) {
      healthData.temperature.status = 'warning';
      healthData.temperature.statusText = '偏高';
    }
  },

  // 模拟API调用
  simulateApiCall() {
    return new Promise(resolve => {
      setTimeout(resolve, 1000);
    });
  },

  // 查看健康详情
  viewHealthDetail(e) {
    const type = e.currentTarget.dataset.type;
    wx.navigateTo({
      url: `/pages/health-detail/health-detail?type=${type}`
    });
  },

  // 切换图表
  switchChart(e) {
    const type = e.currentTarget.dataset.type;
    const chartSummaries = {
      bloodPressure: '您的血压在过去一周内保持稳定，建议继续保持良好的生活习惯。',
      heartRate: '您的心率变化正常，适度运动有助于心血管健康。',
      bloodSugar: '您的血糖控制良好，请继续保持健康饮食。',
      temperature: '您的体温正常，身体状况良好。'
    };
    
    this.setData({
      currentChart: type,
      chartSummary: chartSummaries[type]
    });
  },

  // 记录健康数据
  recordHealth() {
    wx.navigateTo({
      url: '/pages/health-record/health-record'
    });
  },

  // 设备连接
  deviceConnect() {
    wx.navigateTo({
      url: '/pages/device-connect/device-connect'
    });
  },

  // 健康报告
  healthReport() {
    wx.navigateTo({
      url: '/pages/health-report/health-report'
    });
  },

  // 用药提醒管理
  medicationReminder() {
    wx.navigateTo({
      url: '/pages/medication/medication'
    });
  },

  // 管理用药
  manageMedications() {
    wx.navigateTo({
      url: '/pages/medication/medication'
    });
  },

  // 切换用药状态
  toggleMedication(e) {
    const medicationId = e.currentTarget.dataset.id;
    const medications = this.data.medications.map(item => {
      if (item.id === medicationId) {
        return { ...item, taken: !item.taken };
      }
      return item;
    });
    
    this.setData({ medications });
    
    // 震动反馈
    wx.vibrateShort();
    
    // 显示提示
    const medication = medications.find(item => item.id === medicationId);
    wx.showToast({
      title: medication.taken ? '已标记为已服用' : '已取消标记',
      icon: 'success'
    });
  },

  // 查看所有记录
  viewAllRecords() {
    wx.navigateTo({
      url: '/pages/health-records/health-records'
    });
  },

  // 查看记录详情
  viewRecordDetail(e) {
    const recordId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/health-record-detail/health-record-detail?id=${recordId}`
    });
  },

  // 分享
  onShareAppMessage() {
    return {
      title: '云数银龄 - 健康管理',
      path: '/pages/health/health'
    };
  }
});