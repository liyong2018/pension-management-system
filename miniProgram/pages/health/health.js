// 健康管理页面 - 设备列表
const { smartDeviceAPI } = require('../../utils/api');

Page({
  data: {
    loading: true,
    devices: [],
    filteredDevices: [],
    devicesByType: {}, // 按类型分组的设备
    searchKeyword: '',
    currentFilter: 'all',
    refreshing: false,
    
    // 设备类型过滤选项 - 动态生成
    filterOptions: [
      { type: 'all', name: '全部' }
    ]
  },

  onLoad(options) {
    this.loadDevices();
  },

  onShow() {
    this.loadDevices();
  },

  onPullDownRefresh() {
    this.refreshDevices();
  },

  // 加载设备列表
  async loadDevices() {
    try {
      this.setData({ loading: true });
      
      // 使用真实的搜索API接口
      const response = await smartDeviceAPI.searchDevices({
        page: 1,
        size: 100,
        deviceName: '',
        deviceType: '',
        deviceStatus: ''
      });
      
      console.log('API响应:', response);
      
      if (response && (response.success !== false)) {
        // 处理不同的响应数据结构
        let devices = [];
        if (response.data) {
          devices = response.data.list || response.data.records || response.data.content || [];
        } else if (Array.isArray(response)) {
          devices = response;
        } else if (response.list) {
          devices = response.list;
        } else if (response.records) {
          devices = response.records;
        }
        
        // 数据字段映射和标准化
        const normalizedDevices = devices.map(device => ({
          id: device.id || device.deviceId,
          deviceId: device.deviceCode || device.deviceId || device.id,
          deviceName: device.deviceName || device.name || '未知设备',
          deviceType: device.deviceType || device.type || '未知类型',
          deviceStatus: device.deviceStatus || device.status || 'offline',
          status: device.deviceStatus || device.status || 'offline',
          installationLocation: device.installationLocation || device.location || '未设置',
          location: device.installationLocation || device.location || '未设置',
          lastCommunicationTime: device.lastCommunicationTime || device.lastUpdateTime || device.updateTime,
          lastUpdateTime: device.lastCommunicationTime || device.lastUpdateTime || device.updateTime,
          batteryLevel: device.batteryLevel,
          signalStrength: device.signalStrength
        }));
        
        // 按设备类型分组
        const devicesByType = this.groupDevicesByType(normalizedDevices);
        
        // 动态生成过滤选项
        const filterOptions = this.generateFilterOptions(normalizedDevices);
        
        this.setData({ 
          devices: normalizedDevices,
          filteredDevices: normalizedDevices,
          devicesByType,
          filterOptions
        });
        
        this.filterDevices();
        
        console.log('设备数据处理完成:', {
          total: normalizedDevices.length,
          byType: devicesByType,
          filterOptions
        });
        
      } else {
        // API调用失败，使用模拟数据
        console.warn('API调用失败，使用模拟数据');
        this.loadMockDevices();
      }
    } catch (error) {
      console.error('加载设备列表失败:', error);
      // 网络错误，使用模拟数据
      this.loadMockDevices();
    } finally {
      this.setData({ loading: false });
    }
  },

  // 按设备类型分组
  groupDevicesByType(devices) {
    const grouped = {};
    devices.forEach(device => {
      const type = device.deviceType;
      if (!grouped[type]) {
        grouped[type] = [];
      }
      grouped[type].push(device);
    });
    return grouped;
  },

  // 动态生成过滤选项
  generateFilterOptions(devices) {
    // 定义设备大类别，确保标签始终显示
    const deviceCategories = [
      '定位',
      '安全', 
      '智能家居',
      '健康检测'
    ];
    
    // 从当前设备中获取实际存在的类型
    const existingTypes = new Set();
    devices.forEach(device => {
      if (device.deviceType && device.deviceType !== '未知类型') {
        existingTypes.add(device.deviceType);
      }
    });
    
    // 合并所有类型和实际存在的类型
    const allTypes = new Set([...deviceCategories, ...Array.from(existingTypes)]);
    
    const filterOptions = [{ type: 'all', name: '全部' }];
    Array.from(allTypes).sort().forEach(type => {
      filterOptions.push({ type, name: type });
    });
    
    return filterOptions;
  },

  // 加载模拟设备数据
  loadMockDevices() {
    const mockDevices = [
      {
        id: 'DEV001',
        deviceId: 'DEV001',
        deviceName: '智能血压计',
        deviceType: '健康检测',
        deviceStatus: 'online',
        status: 'online',
        installationLocation: '客厅',
        location: '客厅',
        lastCommunicationTime: '2024-01-15 10:30:00',
        lastUpdateTime: '2024-01-15 10:30:00',
        batteryLevel: 85
      },
      {
        id: 'DEV002',
        deviceId: 'DEV002',
        deviceName: 'GPS定位器',
        deviceType: '定位',
        deviceStatus: 'online',
        status: 'online',
        installationLocation: '随身携带',
        location: '随身携带',
        lastCommunicationTime: '2024-01-15 10:25:00',
        lastUpdateTime: '2024-01-15 10:25:00',
        batteryLevel: 92
      },
      {
        id: 'DEV003',
        deviceId: 'DEV003',
        deviceName: '智能门锁',
        deviceType: '安全',
        deviceStatus: 'online',
        status: 'online',
        installationLocation: '大门',
        location: '大门',
        lastCommunicationTime: '2024-01-15 09:15:00',
        lastUpdateTime: '2024-01-15 09:15:00',
        batteryLevel: 78
      },
      {
        id: 'DEV004',
        deviceId: 'DEV004',
        deviceName: '智能灯泡',
        deviceType: '智能家居',
        deviceStatus: 'offline',
        status: 'offline',
        installationLocation: '客厅',
        location: '客厅',
        lastCommunicationTime: '2024-01-14 08:15:00',
        lastUpdateTime: '2024-01-14 08:15:00',
        batteryLevel: 0
      }
    ];
    
    const devicesByType = this.groupDevicesByType(mockDevices);
    
    // 动态生成过滤选项
    const filterOptions = this.generateFilterOptions(mockDevices);
    
    this.setData({ 
      devices: mockDevices,
      filteredDevices: mockDevices,
      devicesByType,
      filterOptions
    });
    
    this.filterDevices();
    
    wx.showToast({
      title: '使用模拟数据',
      icon: 'none',
      duration: 2000
    });
  },

  // 刷新设备列表
  async refreshDevices() {
    try {
      this.setData({ refreshing: true });
      await this.loadDevices();
    } catch (error) {
      console.error('刷新设备列表失败:', error);
    } finally {
      this.setData({ refreshing: false });
      wx.stopPullDownRefresh();
    }
  },

  // 搜索输入
  onSearchInput(e) {
    const searchKeyword = e.detail.value;
    this.setData({ searchKeyword });
    this.filterDevices();
  },

  // 搜索设备
  async onSearch() {
    const { searchKeyword, currentFilter } = this.data;
    
    // 如果有搜索关键词，调用API进行搜索
    if (searchKeyword.trim()) {
      try {
        this.setData({ loading: true });
        
        const response = await smartDeviceAPI.searchDevices({
          page: 1,
          size: 100,
          deviceName: searchKeyword,
          deviceType: currentFilter !== 'all' ? currentFilter : '',
          deviceStatus: ''
        });
        
        if (response && (response.success !== false)) {
          // 处理搜索结果
          let devices = [];
          if (response.data) {
            devices = response.data.list || response.data.records || response.data.content || [];
          } else if (Array.isArray(response)) {
            devices = response;
          } else if (response.list) {
            devices = response.list;
          } else if (response.records) {
            devices = response.records;
          }
          
          // 数据标准化
          const normalizedDevices = devices.map(device => ({
            id: device.id || device.deviceId,
            deviceId: device.deviceCode || device.deviceId || device.id,
            deviceName: device.deviceName || device.name || '未知设备',
            deviceType: device.deviceType || device.type || '未知类型',
            deviceStatus: device.deviceStatus || device.status || 'offline',
            status: device.deviceStatus || device.status || 'offline',
            installationLocation: device.installationLocation || device.location || '未设置',
            location: device.installationLocation || device.location || '未设置',
            lastCommunicationTime: device.lastCommunicationTime || device.lastUpdateTime || device.updateTime,
            lastUpdateTime: device.lastCommunicationTime || device.lastUpdateTime || device.updateTime,
            batteryLevel: device.batteryLevel,
            signalStrength: device.signalStrength
          }));
          
          const devicesByType = this.groupDevicesByType(normalizedDevices);
          
          // 动态生成过滤选项
          const filterOptions = this.generateFilterOptions(normalizedDevices);
          
          this.setData({
            devices: normalizedDevices,
            filteredDevices: normalizedDevices,
            devicesByType,
            filterOptions
          });
          
        } else {
          // API搜索失败，使用本地过滤
          this.filterDevices();
        }
        
      } catch (error) {
        console.error('搜索设备失败:', error);
        // 搜索失败，使用本地过滤
        this.filterDevices();
      } finally {
        this.setData({ loading: false });
      }
    } else {
      // 没有搜索关键词，重新加载所有设备
      await this.loadDevices();
    }
  },

  // 过滤器点击
  async onFilterTap(e) {
    const filterType = e.currentTarget.dataset.type;
    
    this.setData({ currentFilter: filterType });
    
    // 优先使用本地筛选
    this.filterDevices();
  },

  // 过滤设备
  filterDevices() {
    const { devices, currentFilter, searchKeyword } = this.data;
    let filtered = devices;
    
    // 按类型过滤
    if (currentFilter !== 'all') {
      filtered = filtered.filter(device => device.deviceType === currentFilter);
    }
    
    // 按关键词搜索
    if (searchKeyword) {
      const keyword = searchKeyword.toLowerCase();
      filtered = filtered.filter(device => 
        (device.deviceName && device.deviceName.toLowerCase().includes(keyword)) ||
        (device.deviceType && device.deviceType.toLowerCase().includes(keyword)) ||
        (device.deviceId && device.deviceId.toLowerCase().includes(keyword)) ||
        (device.location && device.location.toLowerCase().includes(keyword))
      );
    }
    
    // 重新按类型分组过滤后的设备
    const filteredDevicesByType = this.groupDevicesByType(filtered);
    
    this.setData({ 
      filteredDevices: filtered,
      devicesByType: filteredDevicesByType
    });
    
    console.log('过滤结果:', {
      filter: currentFilter,
      keyword: searchKeyword,
      total: filtered.length,
      byType: filteredDevicesByType
    });
  },

  // 设备点击
  onDeviceTap(e) {
    const deviceId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/device-detail/device-detail?id=${deviceId}`
    });
  },

  // 连接设备
  async onConnectTap(e) {
    e.stopPropagation();
    const deviceId = e.currentTarget.dataset.id;
    
    try {
      wx.showLoading({ title: '连接中...' });
      
      const response = await smartDeviceAPI.updateDeviceStatus(deviceId, {
        status: 'online'
      });
      
      if (response.success) {
        wx.showToast({
          title: '连接成功',
          icon: 'success'
        });
        this.loadDevices();
      } else {
        wx.showToast({
          title: '连接失败',
          icon: 'none'
        });
      }
    } catch (error) {
      console.error('连接设备失败:', error);
      wx.showToast({
        title: '连接失败',
        icon: 'none'
      });
    } finally {
      wx.hideLoading();
    }
  },

  // 断开设备
  async onDisconnectTap(e) {
    e.stopPropagation();
    const deviceId = e.currentTarget.dataset.id;
    
    try {
      wx.showLoading({ title: '断开中...' });
      
      const response = await smartDeviceAPI.updateDeviceStatus(deviceId, {
        status: 'offline'
      });
      
      if (response.success) {
        wx.showToast({
          title: '已断开',
          icon: 'success'
        });
        this.loadDevices();
      } else {
        wx.showToast({
          title: '断开失败',
          icon: 'none'
        });
      }
    } catch (error) {
      console.error('断开设备失败:', error);
      wx.showToast({
        title: '断开失败',
        icon: 'none'
      });
    } finally {
      wx.hideLoading();
    }
  },

  // 查看详情
  onDetailTap(e) {
    e.stopPropagation();
    const deviceId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/device-detail/device-detail?id=${deviceId}`
    });
  },

  // 分享
  onShareAppMessage() {
    return {
      title: '云数银龄 - 设备管理',
      path: '/pages/health/health'
    };
  }
});