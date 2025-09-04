// 设备详情页面
const { smartDeviceAPI } = require('../../utils/api');

Page({
  data: {
    loading: true,
    error: '',
    deviceId: '',
    deviceInfo: {},
    healthData: [],
    lastUpdateTime: ''
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ deviceId: options.id });
      this.loadDeviceDetail();
    } else {
      this.setData({ 
        error: '设备ID参数缺失',
        loading: false 
      });
    }
  },

  onShow() {
    // 页面显示时刷新数据
    if (this.data.deviceId) {
      this.loadDeviceDetail();
    }
  },

  // 加载设备详情
  async loadDeviceDetail() {
    try {
      this.setData({ loading: true, error: '' });
      
      const response = await smartDeviceAPI.getDeviceById(this.data.deviceId);
      
      if (response && response.success !== false) {
        const deviceInfo = response.data || response;
        
        // 标准化设备信息
        const normalizedDevice = {
          id: deviceInfo.id || deviceInfo.deviceId,
          deviceId: deviceInfo.deviceCode || deviceInfo.deviceId || deviceInfo.id,
          deviceName: deviceInfo.deviceName || deviceInfo.name || '未知设备',
          deviceType: deviceInfo.deviceType || deviceInfo.type || '未知类型',
          deviceStatus: deviceInfo.deviceStatus || deviceInfo.status || 'offline',
          installationLocation: deviceInfo.installationLocation || deviceInfo.location || '未设置',
          lastCommunicationTime: this.formatDateTime(deviceInfo.lastCommunicationTime || deviceInfo.lastUpdateTime),
          batteryLevel: deviceInfo.batteryLevel,
          signalStrength: deviceInfo.signalStrength,
          realTimeData: deviceInfo.realTimeData,
          deviceBrand: deviceInfo.deviceBrand,
          deviceModel: deviceInfo.deviceModel,
          purchaseDate: deviceInfo.purchaseDate,
          ipAddress: deviceInfo.ipAddress,
          macAddress: deviceInfo.macAddress
        };

        // 解析健康数据
        const healthData = this.parseHealthData(normalizedDevice);

        this.setData({
          deviceInfo: normalizedDevice,
          healthData,
          lastUpdateTime: this.getCurrentTime(),
          loading: false
        });
      } else {
        throw new Error(response.message || '获取设备详情失败');
      }
    } catch (error) {
      console.error('加载设备详情失败:', error);
      this.setData({
        error: error.message || '加载设备详情失败',
        loading: false
      });
    }
  },

  // 解析健康数据
  parseHealthData(deviceInfo) {
    const healthData = [];
    
    if (deviceInfo.realTimeData) {
      try {
        const data = JSON.parse(deviceInfo.realTimeData);
        
        // 根据设备类型解析不同的健康数据
        switch (deviceInfo.deviceType) {
          case '血压计':
            if (data.systolic && data.diastolic) {
              healthData.push({
                type: 'blood-pressure',
                icon: 'icon-blood-pressure',
                label: '血压',
                value: `${data.systolic}/${data.diastolic}`,
                unit: 'mmHg',
                status: this.getBloodPressureStatus(data.systolic, data.diastolic),
                statusText: this.getBloodPressureStatusText(data.systolic, data.diastolic)
              });
            }
            break;
            
          case '血糖仪':
            if (data.glucose) {
              healthData.push({
                type: 'blood-sugar',
                icon: 'icon-blood-sugar',
                label: '血糖',
                value: data.glucose,
                unit: 'mmol/L',
                status: this.getBloodSugarStatus(data.glucose),
                statusText: this.getBloodSugarStatusText(data.glucose)
              });
            }
            break;
            
          case '心率监测器':
            if (data.heartRate) {
              healthData.push({
                type: 'heart-rate',
                icon: 'icon-heart',
                label: '心率',
                value: data.heartRate,
                unit: 'bpm',
                status: this.getHeartRateStatus(data.heartRate),
                statusText: this.getHeartRateStatusText(data.heartRate)
              });
            }
            break;
            
          case '体温计':
            if (data.temperature) {
              healthData.push({
                type: 'temperature',
                icon: 'icon-temperature',
                label: '体温',
                value: data.temperature,
                unit: '°C',
                status: this.getTemperatureStatus(data.temperature),
                statusText: this.getTemperatureStatusText(data.temperature)
              });
            }
            break;
        }
      } catch (e) {
        console.error('解析实时数据失败:', e);
      }
    }
    
    return healthData;
  },

  // 健康数据状态判断方法
  getBloodPressureStatus(systolic, diastolic) {
    if (systolic >= 140 || diastolic >= 90) return 'high';
    if (systolic < 90 || diastolic < 60) return 'low';
    return 'normal';
  },

  getBloodPressureStatusText(systolic, diastolic) {
    if (systolic >= 140 || diastolic >= 90) return '偏高';
    if (systolic < 90 || diastolic < 60) return '偏低';
    return '正常';
  },

  getBloodSugarStatus(glucose) {
    if (glucose >= 7.0) return 'high';
    if (glucose < 3.9) return 'low';
    return 'normal';
  },

  getBloodSugarStatusText(glucose) {
    if (glucose >= 7.0) return '偏高';
    if (glucose < 3.9) return '偏低';
    return '正常';
  },

  getHeartRateStatus(heartRate) {
    if (heartRate > 100) return 'high';
    if (heartRate < 60) return 'low';
    return 'normal';
  },

  getHeartRateStatusText(heartRate) {
    if (heartRate > 100) return '偏快';
    if (heartRate < 60) return '偏慢';
    return '正常';
  },

  getTemperatureStatus(temperature) {
    if (temperature >= 37.3) return 'high';
    if (temperature < 36.0) return 'low';
    return 'normal';
  },

  getTemperatureStatusText(temperature) {
    if (temperature >= 37.3) return '发热';
    if (temperature < 36.0) return '偏低';
    return '正常';
  },

  // 连接设备
  async onConnectDevice() {
    try {
      wx.showLoading({ title: '连接中...' });
      
      // 调用连接设备API
      const response = await smartDeviceAPI.connectDevice(this.data.deviceId);
      
      if (response && response.success !== false) {
        wx.showToast({
          title: '连接成功',
          icon: 'success'
        });
        
        // 刷新设备状态
        this.loadDeviceDetail();
      } else {
        throw new Error(response.message || '连接失败');
      }
    } catch (error) {
      console.error('连接设备失败:', error);
      wx.showToast({
        title: error.message || '连接失败',
        icon: 'none'
      });
    } finally {
      wx.hideLoading();
    }
  },

  // 断开设备连接
  async onDisconnectDevice() {
    try {
      wx.showLoading({ title: '断开中...' });
      
      const response = await smartDeviceAPI.disconnectDevice(this.data.deviceId);
      
      if (response && response.success !== false) {
        wx.showToast({
          title: '已断开连接',
          icon: 'success'
        });
        
        // 刷新设备状态
        this.loadDeviceDetail();
      } else {
        throw new Error(response.message || '断开连接失败');
      }
    } catch (error) {
      console.error('断开设备连接失败:', error);
      wx.showToast({
        title: error.message || '断开连接失败',
        icon: 'none'
      });
    } finally {
      wx.hideLoading();
    }
  },

  // 刷新数据
  onRefreshData() {
    this.loadDeviceDetail();
  },

  // 重试
  onRetry() {
    this.loadDeviceDetail();
  },

  // 格式化日期时间
  formatDateTime(dateTime) {
    if (!dateTime) return '--';
    
    try {
      const date = new Date(dateTime);
      const now = new Date();
      const diff = now - date;
      
      if (diff < 60000) { // 1分钟内
        return '刚刚';
      } else if (diff < 3600000) { // 1小时内
        return `${Math.floor(diff / 60000)}分钟前`;
      } else if (diff < 86400000) { // 24小时内
        return `${Math.floor(diff / 3600000)}小时前`;
      } else {
        return date.toLocaleString('zh-CN', {
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        });
      }
    } catch (e) {
      return dateTime;
    }
  },

  // 获取当前时间
  getCurrentTime() {
    return new Date().toLocaleString('zh-CN', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  },

  // 分享
  onShareAppMessage() {
    return {
      title: `设备详情 - ${this.data.deviceInfo.deviceName}`,
      path: `/pages/device-detail/device-detail?id=${this.data.deviceId}`
    };
  }
});