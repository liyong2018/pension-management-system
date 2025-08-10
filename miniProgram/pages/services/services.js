// 引入API工具
const { organizationAPI } = require('../../utils/api');

// 服务页面
Page({
  data: {
    loading: true,
    searchKeyword: '',
    currentCategory: 'all',
    serviceList: [],
    recommendedServices: [],
    categories: [
      { id: 'all', name: '全部', icon: '/images/category-all.png' },
      { id: 'medical', name: '医疗护理', icon: '/images/category-medical.png' },
      { id: 'daily', name: '生活照料', icon: '/images/category-daily.png' },
      { id: 'cleaning', name: '家政清洁', icon: '/images/category-cleaning.png' },
      { id: 'companion', name: '陪伴聊天', icon: '/images/category-companion.png' },
      { id: 'rehabilitation', name: '康复理疗', icon: '/images/category-rehabilitation.png' },
      { id: 'emergency', name: '紧急救助', icon: '/images/category-emergency.png' }
    ],
    
    // 筛选相关
    showFilter: false,
    priceRange: '',
    serviceTime: '',
    minRating: '',
    priceOptions: [
      { label: '不限', value: '' },
      { label: '50元以下', value: '0-50' },
      { label: '50-100元', value: '50-100' },
      { label: '100-200元', value: '100-200' },
      { label: '200元以上', value: '200+' }
    ],
    timeOptions: [
      { label: '不限', value: '' },
      { label: '上午', value: 'morning' },
      { label: '下午', value: 'afternoon' },
      { label: '晚上', value: 'evening' },
      { label: '全天', value: 'allday' }
    ],
    ratingOptions: [
      { label: '不限', value: '' },
      { label: '4.5分以上', value: '4.5' },
      { label: '4.0分以上', value: '4.0' },
      { label: '3.5分以上', value: '3.5' }
    ],
    
    // 分页相关
    page: 1,
    pageSize: 10,
    hasMore: true,
    loadingMore: false
  },

  onLoad(options) {
    this.loadRecommendedServices();
    this.loadServiceList();
  },

  onShow() {
    // 页面显示时刷新数据
  },

  onPullDownRefresh() {
    this.setData({
      page: 1,
      serviceList: [],
      hasMore: true
    });
    this.loadServiceList().then(() => {
      wx.stopPullDownRefresh();
    });
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loadingMore) {
      this.loadMoreServices();
    }
  },

  // 加载推荐服务
  async loadRecommendedServices() {
    try {
      // 调用机构API获取推荐的服务机构
      const response = await organizationAPI.getOrganizations({
        pageNum: 1,
        pageSize: 3
      });

      if (response && response.list) {
        const recommendedServices = response.list.map(org => ({
          id: org.id,
          title: org.name,
          price: this.getServicePrice(org.type), // 根据机构类型设置价格
          image: this.getServiceImage(org.type), // 根据机构类型设置图片
          description: org.description || '专业的养老服务机构',
          organizationId: org.id,
          organizationName: org.name,
          address: org.address,
          phone: org.phone,
          type: org.type
        }));
        
        this.setData({ recommendedServices });
      }
    } catch (error) {
      console.error('加载推荐服务失败:', error);
      // 如果API调用失败，使用默认数据
      this.setData({
        recommendedServices: [
          {
            id: 1,
            title: '专业护理服务',
            price: 120,
            image: '/images/service-nursing.jpg'
          }
        ]
      });
    }
  },

  // 加载服务列表
  async loadServiceList() {
    try {
      this.setData({ loading: true });
      
      // 调用机构API获取服务机构列表
      const response = await organizationAPI.getOrganizations({
        pageNum: this.data.page,
        pageSize: this.data.pageSize,
        name: this.data.searchKeyword
      });

      if (response && response.list) {
        // 将机构数据转换为服务数据格式
        const serviceList = response.list.map(org => ({
          id: org.id,
          title: org.name,
          description: org.description || '专业的养老服务机构，提供优质的养老服务',
          price: this.getServicePrice(org.type),
          rating: this.getRandomRating(),
          image: this.getServiceImage(org.type),
          tags: this.getServiceTags(org.type),
          provider: {
            name: org.name,
            avatar: '/images/default-avatar.png',
            id: org.id,
            address: org.address,
            phone: org.phone,
            type: org.type
          },
          organizationId: org.id,
          organizationName: org.name,
          address: org.address,
          phone: org.phone,
          type: org.type
        }));

        const currentList = this.data.page === 1 ? serviceList : [...this.data.serviceList, ...serviceList];
        const hasMore = response.hasNextPage || false;

        this.setData({
          serviceList: currentList,
          hasMore,
          loading: false
        });
      } else {
        // 如果API调用失败，使用模拟数据
        this.loadMockServices();
      }
    } catch (error) {
      console.error('加载服务列表失败:', error);
      // 如果API调用失败，使用模拟数据
      this.loadMockServices();
    }
  },

  // 加载模拟服务数据（作为备用）
  loadMockServices() {
    const mockServices = [
        {
          id: 1,
          title: '专业护理服务',
          description: '提供专业的医疗护理服务，包括测量血压、血糖监测、用药提醒等',
          price: 120,
          rating: 4.8,
          image: '/images/service-nursing.jpg',
          tags: ['专业护士', '24小时', '医疗设备'],
          provider: {
            name: '爱心护理中心',
            avatar: '/images/provider-1.jpg'
          }
        },
        {
          id: 2,
          title: '生活照料服务',
          description: '协助老人日常生活起居，包括洗漱、穿衣、用餐等基础照护',
          price: 80,
          rating: 4.6,
          image: '/images/service-daily.jpg',
          tags: ['生活照料', '贴心服务', '经验丰富'],
          provider: {
            name: '温馨家政',
            avatar: '/images/provider-2.jpg'
          }
        },
        {
          id: 3,
          title: '家政清洁服务',
          description: '专业的家庭清洁服务，保持居住环境整洁卫生',
          price: 60,
          rating: 4.5,
          image: '/images/service-cleaning.jpg',
          tags: ['深度清洁', '环保用品', '定期服务'],
          provider: {
            name: '洁净家政',
            avatar: '/images/provider-3.jpg'
          }
        },
        {
          id: 4,
          title: '陪伴聊天服务',
          description: '专业陪护人员提供心理陪伴，缓解老人孤独感',
          price: 50,
          rating: 4.7,
          image: '/images/service-companion.jpg',
          tags: ['心理陪护', '专业培训', '温暖陪伴'],
          provider: {
            name: '暖心陪护',
            avatar: '/images/provider-4.jpg'
          }
        },
        {
          id: 5,
          title: '康复理疗服务',
          description: '专业康复师提供物理治疗和康复训练服务',
          price: 150,
          rating: 4.9,
          image: '/images/service-therapy.jpg',
          tags: ['专业康复师', '个性化方案', '设备齐全'],
          provider: {
            name: '康复中心',
            avatar: '/images/provider-5.jpg'
          }
        }
      ];
      
      // 模拟分页
      const startIndex = (this.data.page - 1) * this.data.pageSize;
      const endIndex = startIndex + this.data.pageSize;
      const pageData = mockServices.slice(startIndex, endIndex);
      
      const serviceList = this.data.page === 1 ? pageData : [...this.data.serviceList, ...pageData];
      const hasMore = endIndex < mockServices.length;
      
      this.setData({
        serviceList,
        hasMore,
        loading: false
      });
  },

  // 根据机构类型获取服务价格
  getServicePrice(type) {
    const priceMap = {
      '机构养老单位': 150,
      '社区养老单位': 100,
      '居家养老单位': 80,
      '医疗机构': 200,
      '护理机构': 180,
      '康复机构': 160
    };
    return priceMap[type] || 120;
  },

  // 根据机构类型获取服务图片
  getServiceImage(type) {
    const imageMap = {
      '机构养老单位': '/images/service-nursing.jpg',
      '社区养老单位': '/images/service-daily.jpg',
      '居家养老单位': '/images/service-companion.jpg',
      '医疗机构': '/images/service-medical.jpg',
      '护理机构': '/images/service-nursing.jpg',
      '康复机构': '/images/service-therapy.jpg'
    };
    return imageMap[type] || '/images/service-nursing.jpg';
  },

  // 根据机构类型获取服务标签
  getServiceTags(type) {
    const tagMap = {
      '机构养老单位': ['专业护理', '24小时服务', '医疗保障'],
      '社区养老单位': ['就近服务', '社区支持', '便民服务'],
      '居家养老单位': ['上门服务', '个性化护理', '家庭式关怀'],
      '医疗机构': ['专业医疗', '设备齐全', '医保定点'],
      '护理机构': ['专业护士', '康复护理', '长期照护'],
      '康复机构': ['康复训练', '理疗设备', '专业康复师']
    };
    return tagMap[type] || ['专业服务', '贴心照护', '安全可靠'];
  },

  // 生成随机评分
  getRandomRating() {
    return Math.round((Math.random() * 1 + 4) * 10) / 10; // 4.0-5.0之间的评分
  },

  // 加载更多服务
  async loadMoreServices() {
    if (this.data.loadingMore || !this.data.hasMore) return;
    
    this.setData({
      loadingMore: true,
      page: this.data.page + 1
    });
    
    await this.loadServiceList();
    this.setData({ loadingMore: false });
  },

  // 搜索输入
  onSearchInput(e) {
    const searchKeyword = e.detail.value;
    this.setData({ searchKeyword });
    
    // 防抖搜索
    clearTimeout(this.searchTimer);
    this.searchTimer = setTimeout(() => {
      this.resetAndSearch();
    }, 500);
  },

  // 选择分类
  selectCategory(e) {
    const categoryId = e.currentTarget.dataset.id;
    this.setData({ currentCategory: categoryId });
    this.resetAndSearch();
  },

  // 重置并搜索
  resetAndSearch() {
    this.setData({
      page: 1,
      serviceList: [],
      hasMore: true
    });
    this.loadServiceList();
  },

  // 显示筛选弹窗
  showFilterModal() {
    this.setData({ showFilter: true });
  },

  // 隐藏筛选弹窗
  hideFilterModal() {
    this.setData({ showFilter: false });
  },

  // 选择价格范围
  selectPriceRange(e) {
    const value = e.currentTarget.dataset.value;
    this.setData({ priceRange: value });
  },

  // 选择服务时间
  selectServiceTime(e) {
    const value = e.currentTarget.dataset.value;
    this.setData({ serviceTime: value });
  },

  // 选择最低评分
  selectMinRating(e) {
    const value = e.currentTarget.dataset.value;
    this.setData({ minRating: value });
  },

  // 重置筛选条件
  resetFilter() {
    this.setData({
      priceRange: '',
      serviceTime: '',
      minRating: ''
    });
  },

  // 应用筛选条件
  applyFilter() {
    this.hideFilterModal();
    this.resetAndSearch();
  },

  // 查看服务详情
  viewServiceDetail(e) {
    const serviceId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/service-detail/service-detail?id=${serviceId}`
    });
  },

  // 快速预约
  quickBooking() {
    wx.navigateTo({
      url: '/pages/booking/booking'
    });
  },

  // 分享
  onShareAppMessage() {
    return {
      title: '云数银龄 - 专业养老服务',
      path: '/pages/services/services'
    };
  }
});