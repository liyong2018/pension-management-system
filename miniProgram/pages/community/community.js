// 社区页面逻辑 - 显示服务订单数据
const app = getApp()
const { serviceRecordsAPI } = require('../../utils/api')

Page({
  data: {
    loading: false,
    keyword: '',
    status: '',
    currentCategory: 'all',
    categories: [
      { id: 'all', name: '全部' },
      { id: 'pending', name: '进行中' },
      { id: 'completed', name: '已完成' },
      { id: 'cancelled', name: '已取消' }
    ],
    serviceRecords: [],
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    total: 0
  },

  onLoad() {
    this.loadServiceRecords()
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
      this.loadMoreServiceRecords()
    }
  },

  // 刷新数据
  async refreshData() {
    this.setData({
      pageNum: 1,
      hasMore: true,
      serviceRecords: []
    })
    
    await this.loadServiceRecords()
    wx.stopPullDownRefresh()
  },

  // 加载服务订单数据
  async loadServiceRecords() {
    if (this.data.loading) return

    this.setData({ loading: true })

    try {
      const params = {
        pageNum: this.data.pageNum,
        pageSize: this.data.pageSize,
        keyword: this.data.keyword,
        status: this.data.currentCategory === 'all' ? '' : this.data.currentCategory
      }

      console.log('请求服务订单参数:', params)
      const response = await serviceRecordsAPI.getServiceRecords(params)
      console.log('服务订单响应:', response)

      // 处理PageInfo格式的响应数据
      let newRecords = []
      let total = 0
      
      if (response && response.list) {
        // PageInfo格式：{ list: [...], total: number, ... }
        newRecords = response.list
        total = response.total || response.list.length
      } else if (response && response.data) {
        // 其他格式：{ data: [...], total: number }
        newRecords = response.data
        total = response.total || response.data.length
      } else if (Array.isArray(response)) {
        // 直接数组格式
        newRecords = response
        total = response.length
      } else {
        newRecords = []
        total = 0
      }
      
      // 处理状态文本映射和数据格式化
      newRecords = newRecords.map(record => {
        return {
          ...record,
          statusText: this.getStatusText(record.status),
          statusClass: this.getStatusClass(record.status),
          createTime: this.formatDateTime(record.createTime || record.createdAt),
          serviceTime: this.formatDateTime(record.serviceTime),
          price: record.price ? parseFloat(record.price).toFixed(2) : null
        }
      })
      
      const serviceRecords = this.data.pageNum === 1 ? newRecords : [...this.data.serviceRecords, ...newRecords]
      const hasMore = serviceRecords.length < total

      this.setData({
        serviceRecords,
        total,
        hasMore,
        loading: false
      })
    } catch (error) {
      console.error('加载服务订单失败:', error)
      wx.showToast({
        title: '加载服务订单失败',
        icon: 'none'
      })
      this.setData({ loading: false })
    }
  },

  // 加载更多服务订单
  async loadMoreServiceRecords() {
    this.setData({
      pageNum: this.data.pageNum + 1
    })
    await this.loadServiceRecords()
  },



  // 分类切换
  onCategoryChange(e) {
    const category = e.currentTarget.dataset.category
    this.setData({
      currentCategory: category,
      pageNum: 1,
      serviceRecords: []
    })
    this.loadServiceRecords()
  },

  // 搜索输入
  onSearchInput(e) {
    const { field } = e.currentTarget.dataset
    const value = e.detail.value
    this.setData({
      [field]: value
    })
  },

  // 执行搜索
  onSearch() {
    this.setData({
      pageNum: 1,
      serviceRecords: []
    })
    this.loadServiceRecords()
  },



  // 查看订单详情
  onViewDetail(e) {
    const { id } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/service-detail/service-detail?id=${id}`
    })
  },

  // 页面分享
  onShareAppMessage() {
    return {
      title: '服务订单 - 养老服务平台',
      path: '/pages/community/community',
      imageUrl: '/images/share-service.png'
    }
  },

  // 获取状态文本
  getStatusText(status) {
    const statusMap = {
      'pending': '待处理',
      'processing': '进行中',
      'completed': '已完成',
      'cancelled': '已取消',
      '待处理': '待处理',
      '进行中': '进行中',
      '已完成': '已完成',
      '已取消': '已取消'
    }
    return statusMap[status] || status || '未知状态'
  },

  // 获取状态样式类
  getStatusClass(status) {
    const classMap = {
      'pending': 'pending',
      'processing': 'processing',
      'completed': 'completed',
      'cancelled': 'cancelled',
      '待处理': 'pending',
      '进行中': 'processing',
      '已完成': 'completed',
      '已取消': 'cancelled'
    }
    return classMap[status] || 'pending'
  },

  // 格式化日期时间
  formatDateTime(dateTime) {
    if (!dateTime) return ''
    
    try {
      const date = new Date(dateTime)
      if (isNaN(date.getTime())) return dateTime
      
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      
      return `${year}-${month}-${day} ${hours}:${minutes}`
    } catch (error) {
      console.error('日期格式化错误:', error)
      return dateTime
    }
  }
})