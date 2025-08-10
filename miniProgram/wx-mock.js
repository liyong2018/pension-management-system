// 微信小程序API模拟器 - 用于浏览器环境测试
(function() {
  'use strict';
  
  // 如果已经存在wx对象，则不重复创建
  if (typeof window !== 'undefined' && window.wx) {
    return;
  }

  // 模拟localStorage作为小程序存储
  const storage = {
    data: {},
    setItem: function(key, value) {
      this.data[key] = value;
      if (typeof localStorage !== 'undefined') {
        localStorage.setItem('wx_' + key, value);
      }
    },
    getItem: function(key) {
      if (typeof localStorage !== 'undefined') {
        const value = localStorage.getItem('wx_' + key);
        if (value !== null) {
          this.data[key] = value;
          return value;
        }
      }
      return this.data[key] || '';
    },
    removeItem: function(key) {
      delete this.data[key];
      if (typeof localStorage !== 'undefined') {
        localStorage.removeItem('wx_' + key);
      }
    }
  };

  // 创建wx对象模拟
  const wx = {
    // 存储相关API
    setStorageSync: function(key, data) {
      console.log('[wx.setStorageSync]', key, data);
      storage.setItem(key, typeof data === 'object' ? JSON.stringify(data) : data);
    },

    getStorageSync: function(key) {
      const value = storage.getItem(key);
      console.log('[wx.getStorageSync]', key, '→', value);
      try {
        return JSON.parse(value);
      } catch (e) {
        return value;
      }
    },

    removeStorageSync: function(key) {
      console.log('[wx.removeStorageSync]', key);
      storage.removeItem(key);
    },

    // 网络请求API
    request: function(options) {
      console.log('[wx.request]', options);
      
      const xhr = new XMLHttpRequest();
      xhr.open(options.method || 'GET', options.url);
      
      // 设置请求头
      if (options.header) {
        Object.keys(options.header).forEach(key => {
          xhr.setRequestHeader(key, options.header[key]);
        });
      }
      
      xhr.onload = function() {
        const response = {
          statusCode: xhr.status,
          data: xhr.responseText ? JSON.parse(xhr.responseText) : null,
          header: {}
        };
        
        if (xhr.status >= 200 && xhr.status < 300) {
          options.success && options.success(response);
        } else {
          options.fail && options.fail(response);
        }
        options.complete && options.complete(response);
      };
      
      xhr.onerror = function() {
        const error = { errMsg: 'request:fail' };
        options.fail && options.fail(error);
        options.complete && options.complete(error);
      };
      
      xhr.send(options.data ? JSON.stringify(options.data) : null);
    },

    // 导航相关API
    navigateTo: function(options) {
      console.log('[wx.navigateTo]', options);
      
      // 模拟页面跳转
      if (options.url) {
        // 提取页面路径和参数
        const [path, query] = options.url.split('?');
        const pageName = path.split('/').pop();
        
        // 显示跳转信息
        this.showToast({
          title: `跳转到: ${pageName}`,
          icon: 'success'
        });
        
        // 模拟跳转成功
        setTimeout(() => {
          options.success && options.success();
          options.complete && options.complete();
        }, 100);
      } else {
        const error = { errMsg: 'navigateTo:fail url is required' };
        options.fail && options.fail(error);
        options.complete && options.complete(error);
      }
    },

    navigateBack: function(options = {}) {
      console.log('[wx.navigateBack]', options);
      this.showToast({
        title: '返回上一页',
        icon: 'success'
      });
      setTimeout(() => {
        options.success && options.success();
        options.complete && options.complete();
      }, 100);
    },

    // 交互反馈API
    showToast: function(options) {
      console.log('[wx.showToast]', options);
      
      // 创建toast元素
      const toast = document.createElement('div');
      toast.style.cssText = `
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background: rgba(0, 0, 0, 0.8);
        color: white;
        padding: 12px 20px;
        border-radius: 6px;
        font-size: 14px;
        z-index: 10000;
        pointer-events: none;
      `;
      toast.textContent = options.title || '';
      
      document.body.appendChild(toast);
      
      // 自动移除
      setTimeout(() => {
        if (toast.parentNode) {
          toast.parentNode.removeChild(toast);
        }
        options.complete && options.complete();
      }, options.duration || 1500);
    },

    showModal: function(options) {
      console.log('[wx.showModal]', options);
      
      const result = confirm(options.content || '');
      const response = {
        confirm: result,
        cancel: !result
      };
      
      if (result) {
        options.success && options.success(response);
      } else {
        options.fail && options.fail(response);
      }
      options.complete && options.complete(response);
    },

    showLoading: function(options) {
      console.log('[wx.showLoading]', options);
      // 简单实现，实际项目中可以创建loading UI
      options.complete && options.complete();
    },

    hideLoading: function() {
      console.log('[wx.hideLoading]');
    },

    // 下拉刷新API
    stopPullDownRefresh: function() {
      console.log('[wx.stopPullDownRefresh]');
    },

    // 用户信息API
    getUserProfile: function(options) {
      console.log('[wx.getUserProfile]', options);
      
      // 模拟用户信息
      const userInfo = {
        nickName: '测试用户',
        avatarUrl: '/images/default-avatar.png',
        gender: 1,
        country: '中国',
        province: '北京',
        city: '北京'
      };
      
      setTimeout(() => {
        options.success && options.success({ userInfo });
        options.complete && options.complete({ userInfo });
      }, 100);
    }
  };

  // 将wx对象挂载到全局
  if (typeof window !== 'undefined') {
    window.wx = wx;
    console.log('[wx-mock] 微信小程序API模拟器已加载');
  }

  if (typeof global !== 'undefined') {
    global.wx = wx;
  }

})();