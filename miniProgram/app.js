App({
  globalData: {
    userInfo: null,
    token: null,
    baseUrl: 'http://8.137.85.158:8080/api',
    isLoggedIn: false
  },

  onLaunch() {
    // 检查登录状态
    const token = wx.getStorageSync('token')
    if (token) {
      this.globalData.token = token
      this.globalData.isLoggedIn = true
    }

    // 获取用户信息
    this.getUserProfile()
  },

  getUserProfile() {
    wx.getUserProfile({
      desc: '用于完善会员资料',
      success: (res) => {
        this.globalData.userInfo = res.userInfo
        wx.setStorageSync('userInfo', res.userInfo)
      }
    })
  },

  // 网络请求封装
  request(options) {
    return new Promise((resolve, reject) => {
      wx.request({
        url: this.globalData.baseUrl + options.url,
        method: options.method || 'GET',
        data: options.data || {},
        header: {
          'Content-Type': 'application/json',
          'Authorization': this.globalData.token ? `Bearer ${this.globalData.token}` : '',
          ...options.header
        },
        success: (res) => {
          if (res.statusCode === 200) {
            resolve(res.data)
          } else {
            reject(res)
          }
        },
        fail: reject
      })
    })
  },

  // 显示错误信息
  showError(message) {
    wx.showToast({
      title: message || '操作失败',
      icon: 'none',
      duration: 2000
    })
  },

  // 显示成功信息
  showSuccess(message) {
    wx.showToast({
      title: message || '操作成功',
      icon: 'success',
      duration: 2000
    })
  }
})