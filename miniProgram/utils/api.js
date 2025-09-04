// API配置
const BASE_URL = 'http://8.137.85.158:8080/api';

// 请求拦截器
const request = (options) => {
  return new Promise((resolve, reject) => {
    // 获取token
    const token = wx.getStorageSync('token');
    
    // 设置请求头
    const header = {
      'Content-Type': 'application/json',
      ...options.header
    };
    
    // 如果有token，添加到请求头
    if (token) {
      header['Authorization'] = `Bearer ${token}`;
    }

    wx.request({
      url: `${BASE_URL}${options.url}`,
      method: options.method || 'GET',
      data: options.data,
      header: header,
      success: (res) => {
        console.log('API请求成功:', options.url, res);
        
        // 处理HTTP状态码
        if (res.statusCode >= 200 && res.statusCode < 300) {
          resolve(res.data);
        } else if (res.statusCode === 401) {
          // token过期或无效，清除本地存储并跳转到登录页
          wx.removeStorageSync('token');
          wx.removeStorageSync('userInfo');
          wx.showToast({
            title: '登录已过期，请重新登录',
            icon: 'none'
          });
          setTimeout(() => {
            wx.navigateTo({
              url: '/pages/login/login'
            });
          }, 1500);
          reject(new Error('登录已过期'));
        } else {
          reject(new Error(res.data.message || `请求失败: ${res.statusCode}`));
        }
      },
      fail: (err) => {
        console.error('API请求失败:', options.url, err);
        reject(new Error('网络请求失败，请检查网络连接'));
      }
    });
  });
};

// 用户认证相关API
const authAPI = {
  // 用户登录
  login: (loginData) => {
    return request({
      url: '/auth/login',
      method: 'POST',
      data: loginData
    });
  },

  // 获取当前用户信息
  getCurrentUser: () => {
    return request({
      url: '/system-users/me',
      method: 'GET'
    });
  },

  // 更新用户资料
  updateProfile: (profileData) => {
    return request({
      url: '/system-users/me/profile',
      method: 'PUT',
      data: profileData
    });
  }
};

// 用户管理相关API
const userAPI = {
  // 获取用户详情
  getUserById: (userId) => {
    return request({
      url: `/system-users/${userId}`,
      method: 'GET'
    });
  },

  // 获取用户统计信息
  getUserStatistics: () => {
    return request({
      url: '/system-users/statistics',
      method: 'GET'
    });
  }
};

// 健康数据相关API（模拟）
const healthAPI = {
  // 获取健康概览
  getHealthSummary: () => {
    // 模拟API调用
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          steps: Math.floor(Math.random() * 10000) + 5000,
          heartRate: Math.floor(Math.random() * 20) + 70,
          bloodPressure: `${Math.floor(Math.random() * 20) + 110}/${Math.floor(Math.random() * 15) + 70}`,
          sleep: `${(Math.random() * 2 + 6).toFixed(1)}h`
        });
      }, 500);
    });
  }
};

// 机构相关API
const organizationAPI = {
  // 获取机构列表
  getOrganizations: (params = {}) => {
    return request({
      url: '/organizations',
      method: 'GET',
      data: params
    });
  },

  // 获取机构详情
  getOrganizationById: (id) => {
    return request({
      url: `/organizations/${id}`,
      method: 'GET'
    });
  }
};

// 服务相关API（模拟）
const serviceAPI = {
  // 获取服务统计
  getServiceStatistics: () => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          familyCount: Math.floor(Math.random() * 5) + 1,
          orderCount: Math.floor(Math.random() * 10),
          unreadCount: Math.floor(Math.random() * 20),
          walletBalance: (Math.random() * 1000).toFixed(2)
        });
      }, 300);
    });
  }
};

// 服务人员相关API
const serviceStaffAPI = {
  // 获取服务人员列表
  getServiceStaff: (params = {}) => {
    // 手动构建查询字符串，避免使用URLSearchParams（微信小程序不支持）
    let queryString = '';
    if (params && Object.keys(params).length > 0) {
      const queryPairs = [];
      for (const key in params) {
        if (params.hasOwnProperty(key) && params[key] !== undefined && params[key] !== null) {
          queryPairs.push(`${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`);
        }
      }
      queryString = queryPairs.join('&');
    }
    
    // 使用后端API地址
    const apiUrl = '/service-staff';
    const fullUrl = queryString ? `${apiUrl}?${queryString}` : apiUrl;
    
    return request({
      url: fullUrl,
      method: 'GET'
    });
  },

  // 根据ID获取服务人员详情
  getServiceStaffById: (id) => {
    return request({
      url: `/service-staff/${id}`,
      method: 'GET'
    });
  }
};

// 服务订单相关API
const serviceRecordsAPI = {
  // 获取服务订单列表
  getServiceRecords: (params = {}) => {
    // 手动构建查询字符串
    let queryString = '';
    if (params && Object.keys(params).length > 0) {
      const queryPairs = [];
      for (const key in params) {
        if (params.hasOwnProperty(key) && params[key] !== undefined && params[key] !== null && params[key] !== '') {
          queryPairs.push(`${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`);
        }
      }
      queryString = queryPairs.join('&');
    }
    
    // 使用统一的request方法
    const apiUrl = queryString ? `/service-records?${queryString}` : '/service-records';
    
    return request({
      url: apiUrl,
      method: 'GET'
    });
  },

  // 根据ID获取服务订单详情
  getServiceRecordById: (id) => {
    return request({
      url: `/service-records/${id}`,
      method: 'GET'
    });
  }
};

// 智能设备相关API
const smartDeviceAPI = {
  // 获取设备列表（分页）
  getDeviceList: (params = {}) => {
    // 手动构建查询字符串
    let queryString = '';
    if (params && Object.keys(params).length > 0) {
      const queryPairs = [];
      for (const key in params) {
        if (params.hasOwnProperty(key) && params[key] !== undefined && params[key] !== null && params[key] !== '') {
          queryPairs.push(`${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`);
        }
      }
      queryString = queryPairs.join('&');
    }
    
    const apiUrl = queryString ? `/smart-devices?${queryString}` : '/smart-devices';
    
    return request({
      url: apiUrl,
      method: 'GET'
    });
  },

  // 搜索设备（使用真实API接口）
  searchDevices: (params = {}) => {
    // 手动构建查询字符串
    let queryString = '';
    if (params && Object.keys(params).length > 0) {
      const queryPairs = [];
      for (const key in params) {
        if (params.hasOwnProperty(key) && params[key] !== undefined && params[key] !== null && params[key] !== '') {
          queryPairs.push(`${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`);
        }
      }
      queryString = queryPairs.join('&');
    }
    
    const apiUrl = queryString ? `/smart-devices/search?${queryString}` : '/smart-devices/search';
    
    return request({
      url: apiUrl,
      method: 'GET'
    });
  },

  // 搜索设备（使用真实API接口）
  searchDevices: (params = {}) => {
    // 手动构建查询字符串
    let queryString = '';
    if (params && Object.keys(params).length > 0) {
      const queryPairs = [];
      for (const key in params) {
        if (params.hasOwnProperty(key) && params[key] !== undefined && params[key] !== null && params[key] !== '') {
          queryPairs.push(`${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`);
        }
      }
      queryString = queryPairs.join('&');
    }
    
    const apiUrl = queryString ? `/smart-devices/search?${queryString}` : '/smart-devices/search';
    
    return request({
      url: apiUrl,
      method: 'GET'
    });
  },

  // 根据老人ID获取设备列表
  getDevicesByElderlyId: (elderlyId) => {
    return request({
      url: `/smart-devices/elderly/${elderlyId}`,
      method: 'GET'
    });
  },

  // 根据机构ID获取设备列表
  getDevicesByOrganizationId: (organizationId) => {
    return request({
      url: `/smart-devices/organization/${organizationId}`,
      method: 'GET'
    });
  },

  // 获取设备详情
  getDeviceById: (deviceId) => {
    return request({
      url: `/smart-devices/${deviceId}`,
      method: 'GET'
    });
  },

  // 更新设备状态
  updateDeviceStatus: (deviceId, status) => {
    return request({
      url: `/smart-devices/${deviceId}/status`,
      method: 'PUT',
      data: { status }
    });
  },

  // 获取设备实时数据
  getDeviceData: (deviceId) => {
    return request({
      url: `/smart-devices/${deviceId}/data`,
      method: 'GET'
    });
  }
};

// 设备告警相关API
const deviceAlarmAPI = {
  // 获取设备告警列表
  getAlarmList: (params = {}) => {
    let queryString = '';
    if (params && Object.keys(params).length > 0) {
      const queryPairs = [];
      for (const key in params) {
        if (params.hasOwnProperty(key) && params[key] !== undefined && params[key] !== null && params[key] !== '') {
          queryPairs.push(`${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`);
        }
      }
      queryString = queryPairs.join('&');
    }
    
    const apiUrl = queryString ? `/device-alarms?${queryString}` : '/device-alarms';
    
    return request({
      url: apiUrl,
      method: 'GET'
    });
  },

  // 处理告警
  handleAlarm: (alarmId, handleData) => {
    return request({
      url: `/device-alarms/${alarmId}/handle`,
      method: 'PUT',
      data: handleData
    });
  }
};

// 导出API模块
module.exports = {
  request,
  authAPI,
  userAPI,
  healthAPI,
  serviceAPI,
  serviceStaffAPI,
  serviceRecordsAPI,
  organizationAPI,
  smartDeviceAPI,
  deviceAlarmAPI
};