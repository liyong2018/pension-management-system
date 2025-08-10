// 首页逻辑
const app = getApp()

Page({
  data: {
    userInfo: {},
    loading: true,
    weather: null,
    reminders: [],
    healthData: null,
    communityNews: [],
    emergencyContacts: []
  },

  onLoad() {
    this.initPage()
  },

  onShow() {
    this.refreshData()
  },

  // 初始化页面
  async initPage() {
    try {
      // 获取用户信息
      const userInfo = wx.getStorageSync('userInfo') || {}
      this.setData({ userInfo })

      // 并行加载数据
      await Promise.all([
        this.loadWeatherData(),
        this.loadReminders(),
        this.loadHealthData(),
        this.loadCommunityNews(),
        this.loadEmergencyContacts()
      ])
    } catch (error) {
      console.error('初始化页面失败:', error)
      app.showError('加载数据失败')
    } finally {
      this.setData({ loading: false })
    }
  },

  // 刷新数据
  async refreshData() {
    try {
      await Promise.all([
        this.loadReminders(),
        this.loadHealthData()
      ])
    } catch (error) {
      console.error('刷新数据失败:', error)
    }
  },

  // 加载天气数据
  async loadWeatherData() {
    try {
      // 模拟天气数据
      const weather = {
        temperature: 22,
        description: '晴朗'
      }
      this.setData({ weather })
    } catch (error) {
      console.error('加载天气数据失败:', error)
    }
  },

  // 加载提醒数据
  async loadReminders() {
    try {
      // 模拟提醒数据
      const reminders = [
        {
          id: 1,
          time: '09:00',
          title: '服药提醒',
          description: '记得服用降压药',
          status: 'pending',
          statusText: '待完成'
        },
        {
          id: 2,
          time: '14:00',
          title: '体检预约',
          description: '社区医院常规体检',
          status: 'scheduled',
          statusText: '已预约'
        }
      ]
      this.setData({ reminders })
    } catch (error) {
      console.error('加载提醒数据失败:', error)
    }
  },

  // 加载健康数据
  async loadHealthData() {
    try {
      // 模拟健康数据
      const healthData = {
        bloodPressure: '120/80',
        heartRate: '72',
        bloodSugar: '5.6',
        temperature: '36.5°C'
      }
      this.setData({ healthData })
    } catch (error) {
      console.error('加载健康数据失败:', error)
    }
  },

  // 加载社区动态
  async loadCommunityNews() {
    try {
      // 模拟社区动态数据
      const communityNews = [
        {
          id: 1,
          title: '社区健康讲座通知',
          summary: '本周六上午9点，邀请专家讲解老年人健康保健知识',
          image: '/images/news1.jpg',
          publishTime: '2小时前'
        },
        {
          id: 2,
          title: '志愿者招募活动',
          summary: '诚邀爱心人士加入我们的志愿者团队，为老年人提供贴心服务',
          image: '/images/news2.jpg',
          publishTime: '1天前'
        }
      ]
      this.setData({ communityNews })
    } catch (error) {
      console.error('加载社区动态失败:', error)
    }
  },

  // 加载紧急联系人
  async loadEmergencyContacts() {
    try {
      // 模拟紧急联系人数据
      const emergencyContacts = [
        {
          id: 1,
          name: '张医生',
          relation: '家庭医生',
          phone: '138****1234',
          avatar: '/images/doctor.png'
        },
        {
          id: 2,
          name: '李护士',
          relation: '社区护士',
          phone: '139****5678',
          avatar: '/images/nurse.png'
        }
      ]
      this.setData({ emergencyContacts })
    } catch (error) {
      console.error('加载紧急联系人失败:', error)
    }
  },

  // 导航到紧急求助
  navigateToEmergency() {
    wx.navigateTo({
      url: '/pages/emergency/emergency'
    })
  },

  // 导航到健康监测
  navigateToHealth() {
    wx.switchTab({
      url: '/pages/health/health'
    })
  },

  // 导航到服务预约
  navigateToServices() {
    wx.switchTab({
      url: '/pages/services/services'
    })
  },

  // 导航到志愿服务
  navigateToVolunteer() {
    wx.navigateTo({
      url: '/pages/volunteer/volunteer'
    })
  },

  // 导航到社区
  navigateToCommunity() {
    wx.switchTab({
      url: '/pages/community/community'
    })
  },

  // 查看新闻详情
  viewNewsDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/news-detail/news-detail?id=${id}`
    })
  },

  // 拨打电话
  makeCall(e) {
    const phone = e.currentTarget.dataset.phone
    wx.makePhoneCall({
      phoneNumber: phone,
      fail: (error) => {
        console.error('拨打电话失败:', error)
        app.showError('拨打电话失败')
      }
    })
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.refreshData().finally(() => {
      wx.stopPullDownRefresh()
    })
  },

  // 分享
  onShareAppMessage() {
    return {
      title: '云数银龄 - 智慧养老服务平台',
      path: '/pages/index/index'
    }
  }
})