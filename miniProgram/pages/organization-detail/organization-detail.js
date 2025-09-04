// pages/organization-detail/organization-detail.js
const api = require('../../utils/api.js');

Page({
  /**
   * 页面的初始数据
   */
  data: {
    loading: true,
    error: '',
    organizationId: '',
    organizationInfo: {
      id: '',
      name: '',
      type: '',
      phone: '',
      email: '',
      address: '',
      description: '',
      services: [],
      workingHours: '',
      rating: 0,
      status: 'active'
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log('机构详情页面加载，参数：', options);
    
    if (options.id) {
      this.setData({
        organizationId: options.id
      });
      this.loadOrganizationDetail(options.id);
    } else {
      this.setData({
        loading: false,
        error: '缺少机构ID参数'
      });
    }
  },

  /**
   * 加载机构详情
   */
  async loadOrganizationDetail(id) {
    try {
      this.setData({ loading: true, error: '' });
      
      // 模拟API调用，实际项目中应该调用真实API
      const mockData = {
        id: id,
        name: '阳光养老院',
        type: '机构养老单位',
        phone: '400-123-4567',
        email: 'contact@sunshine-care.com',
        address: '北京市朝阳区阳光街123号',
        description: '阳光养老院是一家专业的养老服务机构，提供全方位的养老护理服务，拥有专业的医护团队和完善的设施设备。',
        services: ['日常护理', '医疗服务', '康复训练', '营养配餐', '文娱活动'],
        workingHours: '24小时服务',
        rating: 4.8,
        status: 'active'
      };
      
      // 模拟网络延迟
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      this.setData({
        organizationInfo: mockData,
        loading: false
      });
      
      // 设置页面标题
      wx.setNavigationBarTitle({
        title: mockData.name
      });
      
    } catch (error) {
      console.error('加载机构详情失败：', error);
      this.setData({
        loading: false,
        error: '加载机构详情失败，请重试'
      });
    }
  },

  /**
   * 重试加载
   */
  onRetry() {
    if (this.data.organizationId) {
      this.loadOrganizationDetail(this.data.organizationId);
    }
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
          console.error('拨打电话失败：', err);
          wx.showToast({
            title: '拨打电话失败',
            icon: 'none'
          });
        }
      });
    }
  },

  /**
   * 查看地图
   */
  onViewMap() {
    const { organizationInfo } = this.data;
    if (organizationInfo.address) {
      wx.openLocation({
        latitude: 39.9042, // 示例坐标，实际应该从API获取
        longitude: 116.4074,
        name: organizationInfo.name,
        address: organizationInfo.address,
        fail: (err) => {
          console.error('打开地图失败：', err);
          wx.showToast({
            title: '打开地图失败',
            icon: 'none'
          });
        }
      });
    }
  },

  /**
   * 联系机构
   */
  onContact() {
    const { organizationInfo } = this.data;
    wx.showActionSheet({
      itemList: ['拨打电话', '查看地图'],
      success: (res) => {
        if (res.tapIndex === 0) {
          this.onCallPhone({ currentTarget: { dataset: { phone: organizationInfo.phone } } });
        } else if (res.tapIndex === 1) {
          this.onViewMap();
        }
      }
    });
  },

  /**
   * 预约服务
   */
  onBookService() {
    wx.showToast({
      title: '预约功能开发中',
      icon: 'none'
    });
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    if (this.data.organizationId) {
      this.loadOrganizationDetail(this.data.organizationId).finally(() => {
        wx.stopPullDownRefresh();
      });
    } else {
      wx.stopPullDownRefresh();
    }
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {
    const { organizationInfo } = this.data;
    return {
      title: `${organizationInfo.name} - 养老服务机构`,
      path: `/pages/organization-detail/organization-detail?id=${organizationInfo.id}`,
      imageUrl: '/images/service.png'
    };
  }
})