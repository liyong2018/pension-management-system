// 网络请求工具
const baseURL = 'http://8.137.85.158:8080/api'

function request(options) {
  return new Promise((resolve, reject) => {
    // 获取存储的token
    const token = wx.getStorageSync('token');
    
    // 构建请求头
    const headers = {
      'Content-Type': 'application/json',
      ...options.header
    };
    
    // 如果有token，添加到Authorization头
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    wx.request({
      url: baseURL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: headers,
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res.data)
        } else {
          reject(new Error(`请求失败: ${res.statusCode}`))
        }
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

module.exports = request