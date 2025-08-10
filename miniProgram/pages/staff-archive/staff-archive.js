// pages/staff-archive/staff-archive.js
const { organizationAPI } = require('../../utils/api');

Page({
  /**
   * 页面的初始数据
   */
  data: {
    organizationId: '',
    organizationName: '',
    staffList: [],
    loading: true,
    searchKeyword: '',
    filteredStaff: []
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    const { organizationId } = options;
    if (organizationId) {
      this.setData({ organizationId });
      this.loadStaffData();
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
   * 加载人员档案数据
   */
  async loadStaffData() {
    try {
      this.setData({ loading: true });
      
      // 检查登录状态
      const token = wx.getStorageSync('token');
      if (!token) {
        wx.showModal({
          title: '需要登录',
          content: '请先登录后查看人员档案',
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

      // 获取当前机构ID
      const currentOrgId = this.data.organizationId;
      console.log('当前机构ID:', currentOrgId);

      // 模拟获取人员档案数据
      // 实际项目中应该调用真实的API: organizationAPI.getStaffByOrgId(currentOrgId)
      const allStaffData = this.generateMockStaffData();
      
      // 根据机构ID过滤人员数据
      const filteredStaffData = allStaffData.filter(staff => staff.organizationId === currentOrgId);
      
      // 获取机构名称
      const organizationName = this.getOrganizationName(currentOrgId);
      
      console.log('过滤后的人员数据:', filteredStaffData);
      
      this.setData({
        staffList: filteredStaffData,
        filteredStaff: filteredStaffData,
        organizationName: organizationName,
        loading: false
      });
      
    } catch (error) {
      console.error('加载人员档案失败:', error);
      this.setData({ loading: false });
      wx.showToast({
        title: '加载失败，请重试',
        icon: 'none'
      });
    }
  },

  /**
   * 生成模拟人员档案数据
   */
  generateMockStaffData() {
    return [
      // 机构1的人员
      {
        id: 1,
        organizationId: '1',
        name: '王建国',
        gender: '男',
        age: 87,
        phone: '13601234567',
        careType: '机构养老',
        careLevel: '二级护理',
        ability: '能力完好',
        health: '血压血糖控制良好，行动自如，精神状态佳',
        entryTime: '2023-03-15'
      },
      {
        id: 2,
        organizationId: '1',
        name: '李秀英',
        gender: '女',
        age: 82,
        phone: '13701234567',
        careType: '机构养老',
        careLevel: '一级护理',
        ability: '轻度失能',
        health: '患有轻度认知障碍，需要日常生活协助',
        entryTime: '2023-05-20'
      },
      {
        id: 3,
        organizationId: '1',
        name: '张明华',
        gender: '男',
        age: 75,
        phone: '13801234567',
        careType: '机构养老',
        careLevel: '三级护理',
        ability: '中度失能',
        health: '行动不便，需要轮椅辅助，定期康复训练',
        entryTime: '2023-07-10'
      },
      // 机构2的人员
      {
        id: 4,
        organizationId: '2',
        name: '陈美丽',
        gender: '女',
        age: 78,
        phone: '13901234567',
        careType: '社区养老',
        careLevel: '一级护理',
        ability: '能力完好',
        health: '身体健康，定期体检正常',
        entryTime: '2023-04-20'
      },
      {
        id: 5,
        organizationId: '2',
        name: '刘德华',
        gender: '男',
        age: 85,
        phone: '13501234567',
        careType: '社区养老',
        careLevel: '二级护理',
        ability: '轻度失能',
        health: '患有高血压，需要定期服药',
        entryTime: '2023-06-15'
      },
      // 机构3的人员
      {
        id: 6,
        organizationId: '3',
        name: '赵小芳',
        gender: '女',
        age: 73,
        phone: '13701234568',
        careType: '居家养老',
        careLevel: '一级护理',
        ability: '能力完好',
        health: '身体状况良好，生活自理',
        entryTime: '2023-08-10'
      },
      {
        id: 7,
        organizationId: '3',
        name: '孙志强',
        gender: '男',
        age: 80,
        phone: '13801234568',
        careType: '居家养老',
        careLevel: '二级护理',
        ability: '轻度失能',
        health: '腿脚不便，需要助行器辅助',
        entryTime: '2023-09-05'
      }
    ];
  },

  /**
   * 根据机构ID获取机构名称
   */
  getOrganizationName(organizationId) {
    const organizationMap = {
      '1': '测试中文机构名称',
      '2': '和谐养老院',
      '3': '阳光社区服务中心'
    };
    return organizationMap[organizationId] || '未知机构';
  },

  /**
   * 搜索输入处理
   */
  onSearchInput(e) {
    const keyword = e.detail.value;
    this.setData({ searchKeyword: keyword });
    this.filterStaff(keyword);
  },

  /**
   * 筛选人员数据
   */
  filterStaff(keyword) {
    let filtered = this.data.staffList;
    
    if (keyword) {
      const lowerKeyword = keyword.toLowerCase();
      filtered = filtered.filter(staff => 
        staff.name.toLowerCase().includes(lowerKeyword) ||
        staff.careType.toLowerCase().includes(lowerKeyword) ||
        staff.careLevel.toLowerCase().includes(lowerKeyword)
      );
    }
    
    this.setData({ filteredStaff: filtered });
  },

  /**
   * 人员卡片点击
   */
  onStaffTap(e) {
    const staffId = e.currentTarget.dataset.id;
    const staff = this.data.staffList.find(s => s.id === staffId);
    
    if (staff) {
      wx.showModal({
        title: `${staff.name} 详细信息`,
        content: `年龄：${staff.age}岁\n联系电话：${staff.phone}\n护理类型：${staff.careType}\n护理等级：${staff.careLevel}\n能力评估：${staff.ability}\n健康状况：${staff.health}\n入住时间：${staff.entryTime}`,
        showCancel: false,
        confirmText: '知道了'
      });
    }
  },

  /**
   * 拨打电话
   */
  onCallTap(e) {
    const phone = e.currentTarget.dataset.phone;
    
    wx.showModal({
      title: '联系家属',
      content: `是否拨打电话：${phone}`,
      confirmText: '拨打',
      cancelText: '取消',
      success: (res) => {
        if (res.confirm) {
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
      }
    });
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    this.loadStaffData().then(() => {
      wx.stopPullDownRefresh();
    });
  }
});