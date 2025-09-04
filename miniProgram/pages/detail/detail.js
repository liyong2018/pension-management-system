// pages/detail/detail.js
const { organizationAPI } = require('../../utils/api');

Page({
  /**
   * 页面的初始数据
   */
  data: {
    detailInfo: null,
    loading: true,
    id: '',
    type: '' // 详情类型：elderly（老人详情）、staff（员工详情）、organization（机构详情）
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log('详情页面参数:', options);
    
    const { id, type } = options;
    if (id && type) {
      this.setData({ 
        id: id,
        type: type 
      });
      this.loadDetailInfo();
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
   * 加载详情信息
   */
  async loadDetailInfo() {
    try {
      this.setData({ loading: true });
      
      // 检查登录状态
      const token = wx.getStorageSync('token');
      if (!token) {
        wx.showModal({
          title: '需要登录',
          content: '请先登录后查看详情信息',
          confirmText: '去登录',
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

      let detailInfo;
      const { id, type } = this.data;
      
      // 根据类型加载不同的详情数据
      switch (type) {
        case 'elderly':
          detailInfo = await this.loadElderlyDetail(id);
          break;
        case 'staff':
          detailInfo = await this.loadStaffDetail(id);
          break;
        case 'organization':
          detailInfo = await this.loadOrganizationDetail(id);
          break;
        default:
          throw new Error('未知的详情类型');
      }
      
      this.setData({
        detailInfo: detailInfo,
        loading: false
      });
      
    } catch (error) {
      console.error('加载详情信息失败:', error);
      this.setData({ loading: false });
      wx.showToast({
        title: '加载失败，请重试',
        icon: 'none'
      });
    }
  },

  /**
   * 加载老人详情
   */
  async loadElderlyDetail(id) {
    // 模拟老人详情数据
    return {
      // 基本信息
      name: '张三',
      gender: '男',
      age: 75,
      idCard: '110101194801011234',
      phone: '13812345678',
      
      // 联系信息
      emergencyContact: '李四',
      emergencyPhone: '13987654321',
      relationship: '儿子',
      address: '北京市朝阳区某某街道123号',
      
      // 健康概况
      healthStatus: '良好',
      bloodPressure: '120/80',
      heartRate: '72',
      temperature: '36.5',
      bloodSugar: '5.6',
      
      // 护理信息
      careLevel: '三级护理',
      careType: '生活照料',
      nursingPlan: '日常生活协助，定期健康监测',
      
      // 其他信息
      entryDate: '2023-01-15',
      roomNumber: 'A-201',
      medicalHistory: '高血压，糖尿病',
      allergies: '青霉素过敏',
      notes: '性格温和，喜欢下棋'
    };
  },

  /**
   * 加载员工详情
   */
  async loadStaffDetail(id) {
    // 模拟员工详情数据
    return {
      // 基本信息
      name: '王护士',
      gender: '女',
      age: 28,
      employeeId: 'EMP001',
      phone: '13611112222',
      
      // 职业信息
      position: '护理员',
      department: '护理部',
      level: '主管护师',
      workYears: '5年',
      
      // 资质信息
      certificates: ['护士执业证书', '养老护理员证书', '急救证书'],
      education: '护理专业本科',
      skills: ['基础护理', '康复护理', '心理疏导'],
      
      // 工作信息
      entryDate: '2019-03-01',
      workSchedule: '白班',
      responsibleArea: 'A区1-3楼',
      performance: '优秀',
      
      // 联系信息
      email: 'wangnurse@example.com',
      address: '北京市海淀区某某小区',
      emergencyContact: '王某某',
      emergencyPhone: '13699998888'
    };
  },

  /**
   * 加载机构详情
   */
  async loadOrganizationDetail(id) {
    // 模拟机构详情数据
    return {
      // 基本信息
      name: '阳光养老院',
      type: '养老院',
      level: '三星级',
      establishDate: '2015-06-01',
      
      // 联系信息
      phone: '010-12345678',
      email: 'info@sunshine-care.com',
      address: '北京市朝阳区阳光街88号',
      website: 'www.sunshine-care.com',
      
      // 规模信息
      totalBeds: 200,
      occupiedBeds: 180,
      occupancyRate: '90%',
      area: '5000平方米',
      
      // 服务信息
      services: ['生活照料', '医疗护理', '康复训练', '文娱活动'],
      facilities: ['医务室', '康复室', '活动室', '餐厅', '花园'],
      careTypes: ['自理', '半自理', '不能自理', '特护'],
      
      // 人员信息
      totalStaff: 50,
      nurseCount: 20,
      doctorCount: 5,
      caregiverCount: 25,
      
      // 资质信息
      license: '京民政养老许可证001',
      certifications: ['ISO9001质量管理体系', '消防安全合格证'],
      
      // 收费信息
      priceRange: '3000-8000元/月',
      paymentMethods: ['月付', '季付', '年付'],
      
      // 其他信息
      description: '专业的养老服务机构，致力于为老年人提供优质的生活照料和医疗护理服务。',
      features: ['24小时护理', '专业医疗团队', '营养配餐', '文娱活动丰富']
    };
  },

  /**
   * 拨打电话
   */
  onCallPhone(e) {
    const phone = e.currentTarget.dataset.phone;
    if (phone) {
      wx.makePhoneCall({
        phoneNumber: phone,
        fail: (err) => {
          console.error('拨打电话失败:', err);
          wx.showToast({
            title: '拨打失败',
            icon: 'none'
          });
        }
      });
    }
  },

  /**
   * 编辑信息
   */
  onEdit() {
    const { id, type } = this.data;
    wx.navigateTo({
      url: `/pages/edit-detail/edit-detail?id=${id}&type=${type}`
    });
  },

  /**
   * 分享信息
   */
  onShare() {
    wx.showShareMenu({
      withShareTicket: true,
      menus: ['shareAppMessage', 'shareTimeline']
    });
  },

  /**
   * 页面分享
   */
  onShareAppMessage() {
    const { detailInfo, type } = this.data;
    let title = '详情信息';
    
    if (detailInfo) {
      switch (type) {
        case 'elderly':
          title = `${detailInfo.name} - 老人详情`;
          break;
        case 'staff':
          title = `${detailInfo.name} - 员工详情`;
          break;
        case 'organization':
          title = `${detailInfo.name} - 机构详情`;
          break;
      }
    }
    
    return {
      title: title,
      path: `/pages/detail/detail?id=${this.data.id}&type=${this.data.type}`
    };
  }
});