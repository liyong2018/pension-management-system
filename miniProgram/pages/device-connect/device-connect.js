// 设备连接页面
const { smartDeviceAPI, deviceAlarmAPI } = require('../../utils/api');

Page({
  data: {
    loading: true,
    devices: [],
    searchKeyword: '',
    currentPage: 1,
    pageSize: 10,
    hasMore: true,
    refreshing: false,
    
    // 设备状态筛选
    statusFilter: 'all', // all, online, offline, alarm
    statusOptions: [
      { value: 'all', label: '全部' },
      { value: 'online', label: '在线' },
      { value: 'offline', label: '离线' },
      { value: 'alarm', label: '告警' }
    ],
    
    // 设备类型筛选
    typeFilter: 'all',
    typeOptions: [
      { value: 'all', label: '全部类型' },
      { value: '血压计', label: '血压计' },
      { value: '血糖仪', label: '血糖仪' },
      { value: '心率监测器', label: '心率监测器' },
      { value: '体温计', label: '体温计' },
      { value: '智能手环', label: '智能手环' }
    ]
  },

  onLoad(options) {
    this.loadDevices();
  },

  onShow() {
    // 页面显示时刷新数据
    this.refreshDevices();
  },

  onPullDownRefresh() {
    this.refreshDevices().then(() => {
      wx.stopPullDownRefresh();
    });
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadMoreDevices();
    }
  },

  // 加载设备列表
  async loadDevices(reset = true) {
    try {
      if (reset) {
        this.setData({ 
          loading: true, 
          currentPage: 1,
          devices: []
        });
      }

      const params = {
        pageNum: this.data.currentPage,
        pageSize: this.data.pageSize
      };

      // 添加搜索关键词
      if (this.data.searchKeyword) {
        params.keyword = this.data.searchKeyword;
      }

      // 添加状态筛选
      if (this.data.statusFilter !== 'all') {
        params.status = this.data.statusFilter;
      }

      // 添加类型筛选
      if (this.data.typeFilter !== 'all') {
        params.deviceType = this.data.typeFilter;
      }

      const response = await smartDeviceAPI.getDeviceList(params);
      
      if (response.success) {
        const newDevices = response.data.records || [];
        const devices = reset ? newDevices : [...this.data.devices, ...newDevices];
        
        this.setData({
          devices,
          hasMore: newDevices.length === this.data.pageSize,
          loading: false
        });
      } else {
        throw new Error(response.message || '获取设备列表失败');
      }
    } catch (error) {
      console.error('加载设备列表失败:', error);
      this.setData({ loading: false });
      wx.showToast({
        title: error.message || '加载失败',
        icon: 'none'
      });
    }
  },

  // 刷新设备列表
  async refreshDevices() {
    this.setData({ refreshing: true });
    await this.loadDevices(true);
    this.setData({ refreshing: false });
  },

  // 加载更多设备
  async loadMoreDevices() {
    this.setData({ 
      currentPage: this.data.currentPage + 1 
    });
    await this.loadDevices(false);
  },

  // 搜索输入
  onSearchInput(e) {
    this.setData({
      searchKeyword: e.detail.value
    });
  },

  // 执行搜索
  onSearch() {
    this.loadDevices(true);
  },

  // 清除搜索
  onClearSearch() {
    this.setData({ searchKeyword: '' });
    this.loadDevices(true);
  },

  // 状态筛选
  onStatusFilterChange(e) {
    this.setData({
      statusFilter: e.detail.value
    });
    this.loadDevices(true);
  },

  // 类型筛选
  onTypeFilterChange(e) {
    this.setData({
      typeFilter: e.detail.value
    });
    this.loadDevices(true);
  },

  // 查看设备详情
  viewDeviceDetail(e) {
    const deviceId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/device-detail/device-detail?id=${deviceId}`
    });
  },

  // 连接设备
  async connectDevice(e) {
    const deviceId = e.currentTarget.dataset.id;
    const device = this.data.devices.find(d => d.id === deviceId);
    
    if (!device) return;

    try {
      wx.showLoading({ title: '连接中...' });
      
      // 更新设备状态为在线
      await smartDeviceAPI.updateDeviceStatus(deviceId, 'online');
      
      // 更新本地设备状态
      const devices = this.data.devices.map(d => {
        if (d.id === deviceId) {
          return { ...d, status: 'online', statusText: '在线' };
        }
        return d;
      });
      
      this.setData({ devices });
      
      wx.hideLoading();
      wx.showToast({
        title: '连接成功',
        icon: 'success'
      });
    } catch (error) {
      wx.hideLoading();
      wx.showToast({
        title: '连接失败',
        icon: 'none'
      });
    }
  },

  // 断开设备连接
  async disconnectDevice(e) {
    const deviceId = e.currentTarget.dataset.id;
    
    try {
      wx.showLoading({ title: '断开中...' });
      
      await smartDeviceAPI.updateDeviceStatus(deviceId, 'offline');
      
      // 更新本地设备状态
      const devices = this.data.devices.map(d => {
        if (d.id === deviceId) {
          return { ...d, status: 'offline', statusText: '离线' };
        }
        return d;
      });
      
      this.setData({ devices });
      
      wx.hideLoading();
      wx.showToast({
        title: '已断开连接',
        icon: 'success'
      });
    } catch (error) {
      wx.hideLoading();
      wx.showToast({
        title: '操作失败',
        icon: 'none'
      });
    }
  }
});