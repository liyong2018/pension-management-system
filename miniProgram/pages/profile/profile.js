// 个人中心页面
const { authAPI, userAPI, healthAPI } = require('../../utils/api');

Page({
  data: {
    isLoggedIn: false,
    userInfo: {
      avatar: '/images/default-avatar.png',
      name: '未登录',
      age: null,
      phone: '',
      idCard: '',
      email: '',
      organization: '',
      isAdmin: false
    },
    healthSummary: {
      bloodPressure: '--',
      heartRate: '--',
      bloodSugar: '--',
      lastCheckTime: '--'
    },
    stats: {
      totalOrders: 0,
      completedServices: 0,
      favoriteServices: 0,
      points: 0
    },
    loading: false
  },

  onLoad() {
    this.checkLoginStatus();
  },

  onShow() {
    this.checkLoginStatus();
    if (this.data.isLoggedIn) {
      this.loadUserData();
    }
  },

  // 检查登录状态
  checkLoginStatus() {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    
    if (token && userInfo) {
      this.setData({
        isLoggedIn: true,
        userInfo: {
          ...this.data.userInfo,
          ...userInfo,
          avatar: userInfo.avatar || '/images/default-avatar.png'
        }
      });
      this.loadUserData();
    } else {
      this.setData({
        isLoggedIn: false,
        userInfo: {
          avatar: '/images/default-avatar.png',
          name: '未登录',
          age: null,
          phone: '',
          idCard: '',
          email: '',
          organization: '',
          isAdmin: false
        }
      });
    }
  },

  // 加载用户数据
  async loadUserData() {
    if (!this.data.isLoggedIn) {
      return;
    }

    this.setData({ loading: true });
    
    try {
      // 并行加载用户详细信息和健康数据
      const [userDetails, healthData, statsData] = await Promise.all([
        this.loadUserDetails(),
        this.loadHealthData(),
        this.loadStatsData()
      ]);

      console.log('用户数据加载完成');
    } catch (error) {
      console.error('加载用户数据失败:', error);
      wx.showToast({
        title: '数据加载失败',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  // 加载用户详细信息
  async loadUserDetails() {
    try {
      const userDetails = await authAPI.getCurrentUser();
      this.setData({
        userInfo: {
          ...this.data.userInfo,
          ...userDetails
        }
      });
      return userDetails;
    } catch (error) {
      console.error('加载用户详细信息失败:', error);
      throw error;
    }
  },

  // 加载健康数据
  async loadHealthData() {
    try {
      const healthData = await healthAPI.getHealthSummary();
      this.setData({
        healthSummary: healthData
      });
      return healthData;
    } catch (error) {
      console.error('加载健康数据失败:', error);
      // 健康数据加载失败不影响其他功能
      return null;
    }
  },

  // 加载统计数据
  async loadStatsData() {
    try {
      const statsData = await userAPI.getUserStatistics();
      this.setData({
        stats: statsData
      });
      return statsData;
    } catch (error) {
      console.error('加载统计数据失败:', error);
      // 统计数据加载失败不影响其他功能
      return null;
    }
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.refreshData();
  },

  // 加载用户信息
  async loadUserInfo() {
    try {
      this.setData({ loading: true });
      
      // 从缓存获取用户信息
      const userInfo = wx.getStorageSync('userInfo') || {};
      
      // 模拟API调用获取最新用户信息
      const mockUserInfo = {
        avatar: userInfo.avatar || '/images/default-avatar.png',
        name: userInfo.name || '张老伯',
        phone: userInfo.phone || '138****8888',
        age: userInfo.age || '75',
        gender: userInfo.gender || '男',
        isVip: userInfo.isVip || false
      };
      
      this.setData({ 
        userInfo: mockUserInfo,
        loading: false 
      });
      
      // 更新缓存
      wx.setStorageSync('userInfo', mockUserInfo);
      
      // 加载健康概览
      await this.loadHealthSummary();
      
    } catch (error) {
      console.error('加载用户信息失败:', error);
      this.setData({ loading: false });
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    }
  },

  // 加载健康概览
  async loadHealthSummary() {
    try {
      // 模拟获取健康数据
      const healthSummary = {
        steps: '6,832',
        heartRate: '72',
        bloodPressure: '120/80',
        sleep: '7.5h'
      };
      
      this.setData({ healthSummary });
    } catch (error) {
      console.error('加载健康概览失败:', error);
    }
  },

  // 加载统计数据
  async loadStatistics() {
    try {
      // 模拟API调用
      const statistics = {
        familyCount: 3,
        orderCount: 2,
        unreadCount: 5,
        walletBalance: '268.50'
      };
      
      this.setData(statistics);
    } catch (error) {
      console.error('加载统计数据失败:', error);
    }
  },

  // 刷新数据
  async refreshData() {
    if (this.data.isLoggedIn) {
      await this.loadUserData();
    }
    
    // 停止下拉刷新
    wx.stopPullDownRefresh();
  },

  // 处理用户信息点击
  handleUserInfoTap() {
    if (!this.data.isLoggedIn) {
      // 未登录，跳转到登录页面
      wx.navigateTo({
        url: '/pages/login/login'
      });
    } else {
      // 已登录，显示用户信息或跳转到个人信息编辑页面
      this.showUserInfo();
    }
  },

  // 显示用户信息
  showUserInfo() {
    const { userInfo } = this.data;
    wx.showModal({
      title: '个人信息',
      content: `姓名：${userInfo.fullName || userInfo.name}\n用户名：${userInfo.username}\n邮箱：${userInfo.email || '未设置'}\n手机：${userInfo.phone || '未设置'}\n机构：${userInfo.organization || '无'}`,
      showCancel: true,
      cancelText: '关闭',
      confirmText: '编辑',
      success: (res) => {
        if (res.confirm) {
          // 跳转到个人信息编辑页面
          wx.showToast({
            title: '编辑功能开发中',
            icon: 'none'
          });
        }
      }
    });
  },

  // 更换头像
  changeAvatar() {
    if (!this.data.isLoggedIn) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }

    wx.showActionSheet({
      itemList: ['拍照', '从相册选择'],
      success: (res) => {
        const sourceType = res.tapIndex === 0 ? ['camera'] : ['album'];
        
        wx.chooseImage({
          count: 1,
          sourceType: sourceType,
          success: (res) => {
            const tempFilePath = res.tempFilePaths[0];
            this.uploadAvatar(tempFilePath);
          }
        });
      }
    });
  },

  // 上传头像
  async uploadAvatar(filePath) {
    try {
      wx.showLoading({ title: '上传中...' });
      
      // 模拟上传
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      // 更新头像
      const userInfo = { ...this.data.userInfo, avatar: filePath };
      this.setData({ userInfo });
      
      // 更新缓存
      wx.setStorageSync('userInfo', userInfo);
      
      wx.hideLoading();
      wx.showToast({
        title: '头像更新成功',
        icon: 'success'
      });
      
    } catch (error) {
      wx.hideLoading();
      wx.showToast({
        title: '上传失败',
        icon: 'none'
      });
    }
  },

  // 编辑个人资料
  editProfile() {
    wx.navigateTo({
      url: '/pages/edit-profile/edit-profile'
    });
  },

  // 跳转到健康页面
  goToHealth() {
    wx.switchTab({
      url: '/pages/health/health'
    });
  },

  // 页面导航
  navigateTo(e) {
    const url = e.currentTarget.dataset.url;
    wx.navigateTo({ url });
  },

  // 快捷服务
  quickService(e) {
    const type = e.currentTarget.dataset.type;
    
    switch (type) {
      case 'emergency':
        this.emergencyCall();
        break;
      case 'booking':
        wx.navigateTo({
          url: '/pages/booking/booking'
        });
        break;
      case 'consultation':
        wx.navigateTo({
          url: '/pages/consultation/consultation'
        });
        break;
      case 'feedback':
        wx.navigateTo({
          url: '/pages/feedback/feedback'
        });
        break;
    }
  },

  // 紧急呼叫
  emergencyCall() {
    wx.showModal({
      title: '紧急呼叫',
      content: '是否拨打紧急联系人电话？',
      confirmText: '拨打',
      cancelText: '取消',
      success: (res) => {
        if (res.confirm) {
          // 获取紧急联系人电话
          const emergencyPhone = '120'; // 或从用户设置中获取
          wx.makePhoneCall({
            phoneNumber: emergencyPhone,
            fail: () => {
              wx.showToast({
                title: '拨打失败',
                icon: 'none'
              });
            }
          });
        }
      }
    });
  },

  // 分享
  onShareAppMessage() {
    return {
      title: '云数银龄 - 智慧养老服务',
      path: '/pages/index/index'
    };
  },

  // 分享到朋友圈
  onShareTimeline() {
    return {
      title: '云数银龄 - 智慧养老服务',
      query: 'from=timeline'
    };
  },

  // 设置页面
  goToSettings() {
    if (!this.data.isLoggedIn) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }
    
    wx.navigateTo({
      url: '/pages/settings/settings'
    });
  },

  // 退出登录
  logout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      showCancel: true,
      cancelText: '取消',
      confirmText: '退出',
      success: (res) => {
        if (res.confirm) {
          // 清除本地存储的登录信息
          wx.removeStorageSync('token');
          wx.removeStorageSync('userInfo');
          
          // 重置页面数据
          this.setData({
            isLoggedIn: false,
            userInfo: {
              avatar: '/images/default-avatar.png',
              name: '未登录',
              age: null,
              phone: '',
              idCard: '',
              email: '',
              organization: '',
              isAdmin: false
            },
            healthSummary: {
              bloodPressure: '--',
              heartRate: '--',
              bloodSugar: '--',
              lastCheckTime: '--'
            },
            stats: {
              totalOrders: 0,
              completedServices: 0,
              favoriteServices: 0,
              points: 0
            }
          });
          
          wx.showToast({
            title: '已退出登录',
            icon: 'success'
          });
        }
      }
    });
  }
});