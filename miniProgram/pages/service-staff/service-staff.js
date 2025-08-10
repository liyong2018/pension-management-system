// pages/service-staff/service-staff.js
const API = require('../../utils/api');

Page({
  /**
   * 页面的初始数据
   */
  data: {
    serviceStaff: [], // 服务人员列表
    loading: true, // 加载状态
    organizationId: '', // 机构ID
    organizationName: '', // 机构名称
    pageNum: 1, // 当前页码
    pageSize: 10, // 每页大小
    total: 0, // 总数
    hasMore: true, // 是否还有更多数据
    searchKeyword: '', // 搜索关键词
    refreshing: false // 下拉刷新状态
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log('服务人员页面加载参数:', options);
    
    // 检查登录状态
    const token = wx.getStorageSync('token');
    if (!token) {
      wx.showModal({
        title: '需要登录',
        content: '请先登录后查看服务人员信息',
        showCancel: false,
        confirmText: '去登录',
        success: () => {
          wx.navigateTo({
            url: '/pages/login/login'
          });
        }
      });
      return;
    }
    
    if (options.organizationId) {
      this.setData({
        organizationId: options.organizationId,
        organizationName: decodeURIComponent(options.organizationName || '机构服务人员')
      });
      this.loadServiceStaff();
    } else {
      wx.showToast({
        title: '参数错误',
        icon: 'none'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }
  },

  /**
   * 加载服务人员数据
   */
  async loadServiceStaff(isRefresh = false) {
    try {
      if (isRefresh) {
        this.setData({ 
          refreshing: true,
          pageNum: 1,
          serviceStaff: []
        });
      } else {
        this.setData({ loading: true });
      }

      // 检查登录状态
      const token = wx.getStorageSync('token');
      if (!token) {
        this.setData({ loading: false, refreshing: false });
        wx.showModal({
          title: '需要登录',
          content: '请先登录后查看服务人员信息',
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

      const params = {
        page: this.data.pageNum,
        size: this.data.pageSize,
        organizationId: this.data.organizationId
      };

      // 如果有搜索关键词，添加到参数中
      if (this.data.searchKeyword) {
        params.name = this.data.searchKeyword; // 后端使用name参数进行搜索
      }

      console.log('调用服务人员API，参数:', params);
      const response = await API.serviceStaffAPI.getServiceStaff(params);
      console.log('服务人员数据加载成功:', response);

      if (response && (response.list || response.data)) {
        const list = response.list || response.data || [];
        const currentStaff = isRefresh ? [] : this.data.serviceStaff;
        
        this.setData({
          serviceStaff: [...currentStaff, ...list],
          total: response.total || list.length,
          hasMore: response.hasMore || (list.length >= this.data.pageSize),
          loading: false,
          refreshing: false
        });
        
        console.log('成功加载服务人员数据:', list.length, '条');
      } else {
        // 如果后端API还没有实现，使用模拟数据
        console.log('使用模拟服务人员数据');
        const mockData = this.generateMockServiceStaff();
        this.setData({
          serviceStaff: mockData,
          total: mockData.length,
          hasMore: false,
          loading: false,
          refreshing: false
        });
      }
    } catch (error) {
      console.error('加载服务人员数据失败:', error);
      
      // 如果API调用失败，使用模拟数据
      console.log('API调用失败，使用模拟数据');
      const mockData = this.generateMockServiceStaff();
      this.setData({
        serviceStaff: mockData,
        total: mockData.length,
        hasMore: false,
        loading: false,
        refreshing: false
      });

      wx.showToast({
        title: 'API暂不可用，显示模拟数据',
        icon: 'none',
        duration: 2000
      });

      if (error.message && error.message.includes('401')) {
        wx.removeStorageSync('token');
        wx.removeStorageSync('userInfo');
        wx.showModal({
          title: '登录已过期',
          content: '请重新登录后查看服务人员信息',
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
      }
    }
  },

  /**
   * 生成模拟服务人员数据
   */
  generateMockServiceStaff() {
    const positions = ['护理员', '医生', '营养师', '康复师', '心理咨询师', '社工', '护士', '保洁员'];
    const specialties = ['老年护理', '慢病管理', '康复训练', '心理疏导', '营养配餐', '生活照料', '医疗护理', '环境维护'];
    const levels = ['初级', '中级', '高级', '专家'];
    
    const mockStaff = [];
    const orgId = this.data.organizationId;
    
    // 根据机构ID生成不同的服务人员
    const staffCount = 8 + Math.floor(Math.random() * 5); // 8-12个服务人员
    
    for (let i = 0; i < staffCount; i++) {
      const position = positions[Math.floor(Math.random() * positions.length)];
      const specialty = specialties[Math.floor(Math.random() * specialties.length)];
      const level = levels[Math.floor(Math.random() * levels.length)];
      
      mockStaff.push({
        id: `SS${orgId}${String(i + 1).padStart(3, '0')}`,
        name: this.generateRandomName(),
        position: position,
        specialty: specialty,
        level: level,
        phone: `138****${String(Math.floor(Math.random() * 10000)).padStart(4, '0')}`,
        experience: Math.floor(Math.random() * 15) + 1, // 1-15年经验
        rating: (4.0 + Math.random() * 1).toFixed(1), // 4.0-5.0评分
        serviceCount: Math.floor(Math.random() * 500) + 50, // 50-550次服务
        avatar: '/images/default-avatar.png',
        organizationId: orgId,
        status: Math.random() > 0.1 ? '在职' : '休假', // 90%在职
        joinDate: this.generateRandomDate(),
        description: `专业的${position}，擅长${specialty}，具有${Math.floor(Math.random() * 15) + 1}年工作经验。`
      });
    }
    
    return mockStaff;
  },

  /**
   * 生成随机姓名
   */
  generateRandomName() {
    const surnames = ['王', '李', '张', '刘', '陈', '杨', '赵', '黄', '周', '吴', '徐', '孙', '胡', '朱', '高', '林'];
    const names = ['伟', '芳', '娜', '秀英', '敏', '静', '丽', '强', '磊', '军', '洋', '勇', '艳', '杰', '娟', '涛', '明', '超', '秀兰', '霞'];
    
    const surname = surnames[Math.floor(Math.random() * surnames.length)];
    const name = names[Math.floor(Math.random() * names.length)];
    
    return surname + name;
  },

  /**
   * 生成随机日期
   */
  generateRandomDate() {
    const start = new Date(2020, 0, 1);
    const end = new Date();
    const date = new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()));
    return date.toISOString().split('T')[0];
  },

  /**
   * 搜索输入处理
   */
  onSearchInput(e) {
    const keyword = e.detail.value;
    this.setData({ searchKeyword: keyword });
  },

  /**
   * 搜索按钮点击
   */
  onSearch() {
    this.setData({ pageNum: 1 });
    this.loadServiceStaff(true);
  },

  /**
   * 服务人员卡片点击
   */
  onStaffTap(e) {
    const staffId = e.currentTarget.dataset.id;
    console.log('点击服务人员:', staffId);
    
    // 跳转到服务人员详情页面
    wx.navigateTo({
      url: `/pages/service-staff-detail/service-staff-detail?id=${staffId}&organizationId=${this.data.organizationId}`
    });
  },

  /**
   * 拨打电话
   */
  onCallTap(e) {
    const phone = e.currentTarget.dataset.phone;
    wx.makePhoneCall({
      phoneNumber: phone,
      success: () => {
        console.log('拨打电话成功');
      },
      fail: (err) => {
        console.error('拨打电话失败:', err);
        wx.showToast({
          title: '拨打失败',
          icon: 'none'
        });
      }
    });
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    this.loadServiceStaff(true).then(() => {
      wx.stopPullDownRefresh();
    });
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({ 
        pageNum: this.data.pageNum + 1 
      });
      this.loadServiceStaff();
    }
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {
    return {
      title: `${this.data.organizationName} - 服务人员`,
      path: `/pages/service-staff/service-staff?organizationId=${this.data.organizationId}&organizationName=${encodeURIComponent(this.data.organizationName)}`,
      imageUrl: '/images/service.png'
    };
  }
})