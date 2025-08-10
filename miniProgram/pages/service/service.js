// pages/service/service.js
const { organizationAPI } = require('../../utils/api');

Page({
  /**
   * 页面的初始数据
   */
  data: {
    organizations: [], // 所有机构数据
    filteredOrganizations: [], // 筛选后的机构数据
    loading: true, // 加载状态
    searchKeyword: '', // 搜索关键词
    currentFilter: '', // 当前筛选类型
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.loadOrganizations();
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    // 每次显示页面时刷新数据
    this.loadOrganizations();
  },

  /**
   * 加载机构数据
   */
  async loadOrganizations() {
    try {
      this.setData({ loading: true });
      
      // 检查登录状态
      const token = wx.getStorageSync('token');
      if (!token) {
        this.setData({ loading: false });
        wx.showModal({
          title: '需要登录',
          content: '请先登录后查看机构信息',
          confirmText: '去登录',
          cancelText: '取消',
          success: (res) => {
            if (res.confirm) {
              wx.navigateTo({
                url: '/pages/login/login'
              });
            }
          }
        });
        return;
      }
      
      const response = await organizationAPI.getOrganizations();
      console.log('机构数据加载成功:', response);
      
      if (response && response.list) {
        // 为每个机构添加默认服务标签
        const organizations = response.list.map(org => ({
          ...org,
          services: this.getServicesByType(org.type),
          rating: org.rating || (4.0 + Math.random()).toFixed(1) // 如果没有评分，生成随机评分
        }));
        
        this.setData({
          organizations: organizations,
          filteredOrganizations: organizations,
          loading: false
        });
      } else {
        throw new Error('数据格式错误');
      }
    } catch (error) {
      console.error('加载机构数据失败:', error);
      this.setData({ loading: false });
      
      // 如果是401错误，说明token过期
      if (error.message && error.message.includes('401')) {
        wx.removeStorageSync('token');
        wx.removeStorageSync('userInfo');
        wx.showModal({
          title: '登录已过期',
          content: '请重新登录后查看机构信息',
          confirmText: '去登录',
          cancelText: '取消',
          success: (res) => {
            if (res.confirm) {
              wx.navigateTo({
                url: '/pages/login/login'
              });
            }
          }
        });
      } else {
        wx.showToast({
          title: '加载失败，请重试',
          icon: 'none',
          duration: 2000
        });
      }
    }
  },

  /**
   * 根据机构类型获取服务标签（已移除，不再需要）
   */
  getServicesByType(type) {
    // 不再需要服务标签
    return [];
  },

  /**
   * 搜索输入处理
   */
  onSearchInput(e) {
    const keyword = e.detail.value;
    this.setData({ searchKeyword: keyword });
    this.filterOrganizations(keyword, this.data.currentFilter);
  },

  /**
   * 搜索按钮点击
   */
  onSearch() {
    this.filterOrganizations(this.data.searchKeyword, this.data.currentFilter);
  },

  /**
   * 筛选标签点击
   */
  onFilterTap(e) {
    const filter = e.currentTarget.dataset.filter;
    this.setData({ currentFilter: filter });
    this.filterOrganizations(this.data.searchKeyword, filter);
  },

  /**
   * 筛选机构数据
   */
  filterOrganizations(keyword, filter) {
    let filtered = this.data.organizations;

    // 按类型筛选
    if (filter) {
      filtered = filtered.filter(org => org.type === filter);
    }

    // 按关键词搜索
    if (keyword) {
      const lowerKeyword = keyword.toLowerCase();
      filtered = filtered.filter(org => 
        org.name.toLowerCase().includes(lowerKeyword) ||
        org.address.toLowerCase().includes(lowerKeyword) ||
        org.type.toLowerCase().includes(lowerKeyword)
      );
    }

    this.setData({ filteredOrganizations: filtered });
  },

  /**
   * 机构卡片点击
   */
  onOrganizationTap(e) {
    const id = e.currentTarget.dataset.id;
    console.log('点击机构:', id);
    
    // 跳转到机构详情页面
    wx.navigateTo({
      url: `/pages/organization-detail/organization-detail?id=${id}`
    });
  },

  /**
   * 人员档案按钮点击
   */
  onArchiveTap(e) {
    console.log('onArchiveTap 被调用了!', e);
    
    const id = e.currentTarget.dataset.id;
    console.log('查看人员档案, 机构ID:', id);
    
    // 显示点击反馈
    wx.showToast({
      title: '正在跳转...',
      icon: 'loading',
      duration: 1000
    });
    
    // 检查用户登录状态
    const token = wx.getStorageSync('token');
    console.log('当前token状态:', token ? '已登录' : '未登录');
    
    if (!token) {
      wx.showModal({
        title: '提示',
        content: '请先登录后查看人员档案',
        confirmText: '去登录',
        cancelText: '取消',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({
              url: '/pages/login/login'
            });
          }
        }
      });
      return;
    }

    // 跳转到人员档案页面
    console.log('准备跳转到人员档案页面...');
    wx.navigateTo({
      url: `/pages/staff-archive/staff-archive?organizationId=${id}`,
      success: () => {
        console.log('跳转成功');
      },
      fail: (err) => {
        console.error('跳转失败:', err);
        wx.showToast({
          title: '跳转失败',
          icon: 'none'
        });
      }
    });
  },

  /**
   * 服务人员按钮点击
   */
  onServiceStaffTap(e) {
    console.log('onServiceStaffTap 被调用了!', e);
    
    const id = e.currentTarget.dataset.id;
    const name = e.currentTarget.dataset.name;
    console.log('查看服务人员, 机构ID:', id, '机构名称:', name);
    
    // 显示点击反馈
    wx.showToast({
      title: '正在跳转...',
      icon: 'loading',
      duration: 1000
    });
    
    // 检查用户登录状态
    const token = wx.getStorageSync('token');
    console.log('当前token状态:', token ? '已登录' : '未登录');
    
    if (!token) {
      wx.showModal({
        title: '提示',
        content: '请先登录后查看服务人员',
        confirmText: '去登录',
        cancelText: '取消',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({
              url: '/pages/login/login'
            });
          }
        }
      });
      return;
    }

    // 跳转到服务人员页面
    console.log('准备跳转到服务人员页面...');
    wx.navigateTo({
      url: `/pages/service-staff/service-staff?organizationId=${id}&organizationName=${encodeURIComponent(name)}`,
      success: () => {
        console.log('跳转成功');
      },
      fail: (err) => {
        console.error('跳转失败:', err);
        wx.showToast({
          title: '跳转失败',
          icon: 'none'
        });
      }
    });
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    this.loadOrganizations().then(() => {
      wx.stopPullDownRefresh();
    });
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {
    // 可以在这里实现分页加载
    console.log('触底加载更多');
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {
    return {
      title: '银龄智慧养老服务',
      path: '/pages/service/service',
      imageUrl: '/images/share-service.png'
    };
  }
})