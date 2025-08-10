// 志愿者页面逻辑
const app = getApp()

Page({
  data: {
    loading: false,
    searchKeyword: '',
    activeTab: 'all',
    categories: [
      { id: 'all', name: '全部' },
      { id: 'care', name: '生活照料' },
      { id: 'health', name: '健康服务' },
      { id: 'companion', name: '陪伴聊天' },
      { id: 'education', name: '文化教育' },
      { id: 'emergency', name: '应急救助' }
    ],
    volunteerStats: {
      totalHours: 0,
      totalActivities: 0,
      helpedPeople: 0,
      ranking: 0
    },
    recommendedActivities: [],
    activities: [],
    page: 1,
    hasMore: true,
    showFilter: false,
    filterOptions: {
      category: 'all',
      status: 'all',
      timeRange: 'all'
    }
  },

  onLoad() {
    this.loadVolunteerStats()
    this.loadRecommendedActivities()
    this.loadActivities()
  },

  onShow() {
    // 页面显示时刷新数据
    this.refreshData()
  },

  onPullDownRefresh() {
    this.refreshData()
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadMoreActivities()
    }
  },

  // 刷新数据
  async refreshData() {
    this.setData({
      page: 1,
      hasMore: true,
      activities: []
    })
    
    await Promise.all([
      this.loadVolunteerStats(),
      this.loadRecommendedActivities(),
      this.loadActivities()
    ])
    
    wx.stopPullDownRefresh()
  },

  // 加载志愿者统计
  async loadVolunteerStats() {
    try {
      // 模拟API调用
      const stats = {
        totalHours: 156,
        totalActivities: 23,
        helpedPeople: 45,
        ranking: 8
      }

      this.setData({ volunteerStats: stats })
    } catch (error) {
      console.error('加载志愿者统计失败:', error)
    }
  },

  // 加载推荐活动
  async loadRecommendedActivities() {
    try {
      // 模拟API调用
      const recommended = [
        {
          id: 1,
          title: '陪伴独居老人',
          image: '/images/activity1.jpg',
          time: '今天 14:00-16:00',
          location: '阳光社区',
          category: '陪伴聊天',
          isUrgent: true
        },
        {
          id: 2,
          title: '健康体检志愿服务',
          image: '/images/activity2.jpg',
          time: '明天 09:00-12:00',
          location: '社区卫生服务中心',
          category: '健康服务',
          isUrgent: false
        },
        {
          id: 3,
          title: '老年人智能手机培训',
          image: '/images/activity3.jpg',
          time: '1月20日 15:00-17:00',
          location: '社区活动中心',
          category: '文化教育',
          isUrgent: false
        }
      ]

      this.setData({ recommendedActivities: recommended })
    } catch (error) {
      console.error('加载推荐活动失败:', error)
    }
  },

  // 加载志愿活动
  async loadActivities() {
    if (this.data.loading) return

    this.setData({ loading: true })

    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))

      const newActivities = [
        {
          id: 1,
          title: '陪伴独居老人聊天',
          description: '为社区独居老人提供陪伴服务，聊天谈心，关注心理健康',
          image: '/images/volunteer1.jpg',
          time: '今天 14:00-16:00',
          location: '阳光社区',
          participants: 8,
          maxParticipants: 10,
          status: 'recruiting',
          statusText: '招募中',
          organizer: {
            name: '阳光社区',
            avatar: '/images/org1.png'
          },
          isJoined: false,
          category: 'companion'
        },
        {
          id: 2,
          title: '健康体检志愿服务',
          description: '协助医护人员为老年人进行健康体检，测量血压、血糖等',
          image: '/images/volunteer2.jpg',
          time: '明天 09:00-12:00',
          location: '社区卫生服务中心',
          participants: 15,
          maxParticipants: 20,
          status: 'recruiting',
          statusText: '招募中',
          organizer: {
            name: '卫生服务中心',
            avatar: '/images/org2.png'
          },
          isJoined: true,
          category: 'health'
        },
        {
          id: 3,
          title: '老年人智能手机培训',
          description: '教授老年人使用智能手机，包括微信、支付宝等常用应用',
          image: '/images/volunteer3.jpg',
          time: '1月20日 15:00-17:00',
          location: '社区活动中心',
          participants: 12,
          maxParticipants: 15,
          status: 'ongoing',
          statusText: '进行中',
          organizer: {
            name: '志愿者协会',
            avatar: '/images/org3.png'
          },
          isJoined: false,
          category: 'education'
        },
        {
          id: 4,
          title: '老年人生活照料',
          description: '为行动不便的老年人提供日常生活照料，包括买菜、做饭等',
          image: '/images/volunteer4.jpg',
          time: '每周三 10:00-12:00',
          location: '各社区',
          participants: 25,
          maxParticipants: 30,
          status: 'recruiting',
          statusText: '招募中',
          organizer: {
            name: '爱心志愿团',
            avatar: '/images/org4.png'
          },
          isJoined: false,
          category: 'care'
        }
      ]

      const activities = this.data.page === 1 ? newActivities : [...this.data.activities, ...newActivities]
      const hasMore = this.data.page < 3 // 模拟只有3页数据

      this.setData({
        activities,
        hasMore,
        loading: false
      })
    } catch (error) {
      console.error('加载活动失败:', error)
      app.showError('加载活动失败')
      this.setData({ loading: false })
    }
  },

  // 加载更多活动
  async loadMoreActivities() {
    this.setData({
      page: this.data.page + 1
    })
    await this.loadActivities()
  },

  // 搜索输入
  onSearchInput(e) {
    this.setData({
      searchKeyword: e.detail.value
    })
  },

  // 搜索确认
  onSearchConfirm() {
    if (!this.data.searchKeyword.trim()) return
    
    // 执行搜索
    this.searchActivities()
  },

  // 搜索活动
  async searchActivities() {
    this.setData({
      loading: true,
      page: 1,
      activities: []
    })

    try {
      // 模拟搜索API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      // 这里应该调用实际的搜索API
      await this.loadActivities()
    } catch (error) {
      console.error('搜索失败:', error)
      app.showError('搜索失败')
    }
  },

  // 切换分类
  onTabChange(e) {
    const category = e.currentTarget.dataset.category
    if (category === this.data.activeTab) return

    this.setData({
      activeTab: category,
      page: 1,
      activities: []
    })

    this.loadActivities()
  },

  // 查看我的志愿服务
  onMyVolunteer() {
    wx.navigateTo({
      url: '/pages/my-volunteer/my-volunteer'
    })
  },

  // 志愿者注册
  onRegisterVolunteer() {
    wx.navigateTo({
      url: '/pages/volunteer-register/volunteer-register'
    })
  },

  // 我的活动
  onMyActivities() {
    wx.navigateTo({
      url: '/pages/my-activities/my-activities'
    })
  },

  // 志愿证书
  onVolunteerCertificate() {
    wx.navigateTo({
      url: '/pages/volunteer-certificate/volunteer-certificate'
    })
  },

  // 爱心排行
  onVolunteerRanking() {
    wx.navigateTo({
      url: '/pages/volunteer-ranking/volunteer-ranking'
    })
  },

  // 更多推荐
  onMoreRecommended() {
    wx.navigateTo({
      url: '/pages/recommended-activities/recommended-activities'
    })
  },

  // 查看活动详情
  onActivityDetail(e) {
    const activityId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/activity-detail/activity-detail?id=${activityId}`
    })
  },

  // 报名活动
  async onJoinActivity(e) {
    e.stopPropagation()
    const activityId = e.currentTarget.dataset.id
    const activity = this.data.activities.find(a => a.id === activityId)
    
    if (activity.isJoined) {
      // 取消报名
      wx.showModal({
        title: '确认取消',
        content: '确定要取消报名吗？',
        success: (res) => {
          if (res.confirm) {
            this.cancelJoinActivity(activityId)
          }
        }
      })
    } else {
      // 报名活动
      this.joinActivity(activityId)
    }
  },

  // 报名活动
  async joinActivity(activityId) {
    try {
      // 模拟API调用
      await app.request({
        url: `/api/activities/${activityId}/join`,
        method: 'POST'
      })

      // 更新本地状态
      const activities = this.data.activities.map(activity => {
        if (activity.id === activityId) {
          return {
            ...activity,
            isJoined: true,
            participants: activity.participants + 1
          }
        }
        return activity
      })

      this.setData({ activities })
      app.showSuccess('报名成功')
    } catch (error) {
      console.error('报名失败:', error)
      app.showError('报名失败')
    }
  },

  // 取消报名
  async cancelJoinActivity(activityId) {
    try {
      // 模拟API调用
      await app.request({
        url: `/api/activities/${activityId}/cancel`,
        method: 'POST'
      })

      // 更新本地状态
      const activities = this.data.activities.map(activity => {
        if (activity.id === activityId) {
          return {
            ...activity,
            isJoined: false,
            participants: activity.participants - 1
          }
        }
        return activity
      })

      this.setData({ activities })
      app.showSuccess('取消报名成功')
    } catch (error) {
      console.error('取消报名失败:', error)
      app.showError('取消报名失败')
    }
  },

  // 分享活动
  onShareActivity(e) {
    e.stopPropagation()
    const activityId = e.currentTarget.dataset.id
    const activity = this.data.activities.find(a => a.id === activityId)
    
    return {
      title: activity.title,
      path: `/pages/activity-detail/activity-detail?id=${activityId}`,
      imageUrl: activity.image
    }
  },

  // 显示筛选
  onShowFilter() {
    this.setData({ showFilter: true })
  },

  // 隐藏筛选
  onHideFilter() {
    this.setData({ showFilter: false })
  },

  // 筛选选择
  onFilterSelect(e) {
    const { type, value } = e.currentTarget.dataset
    this.setData({
      [`filterOptions.${type}`]: value
    })
  },

  // 重置筛选
  onFilterReset() {
    this.setData({
      filterOptions: {
        category: 'all',
        status: 'all',
        timeRange: 'all'
      }
    })
  },

  // 应用筛选
  onFilterApply() {
    this.setData({
      showFilter: false,
      page: 1,
      activities: []
    })

    this.loadActivities()
  },

  // 创建活动
  onCreateActivity() {
    wx.navigateTo({
      url: '/pages/create-activity/create-activity'
    })
  },

  // 刷新
  onRefresh() {
    this.refreshData()
  },

  // 页面分享
  onShareAppMessage() {
    return {
      title: '志愿服务 - 养老服务平台',
      path: '/pages/volunteer/volunteer',
      imageUrl: '/images/share-volunteer.png'
    }
  }
})