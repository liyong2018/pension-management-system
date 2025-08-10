// 登录页面
const { authAPI } = require('../../utils/api');

Page({
  data: {
    username: '',
    password: '',
    showPassword: false,
    rememberPassword: false,
    loading: false,
    canLogin: false
  },

  onLoad(options) {
    // 检查是否已登录
    this.checkLoginStatus();
    
    // 加载记住的用户名和密码
    this.loadRememberedCredentials();
  },

  onShow() {
    // 每次显示页面时检查登录状态
    this.checkLoginStatus();
  },

  // 检查登录状态
  checkLoginStatus() {
    const token = wx.getStorageSync('token');
    if (token) {
      // 已登录，跳转到首页
      wx.switchTab({
        url: '/pages/index/index'
      });
    }
  },

  // 加载记住的凭据
  loadRememberedCredentials() {
    const rememberedCredentials = wx.getStorageSync('rememberedCredentials');
    if (rememberedCredentials) {
      this.setData({
        username: rememberedCredentials.username || '',
        password: rememberedCredentials.password || '',
        rememberPassword: true
      });
      this.checkCanLogin();
    }
  },

  // 用户名输入
  onUsernameInput(e) {
    this.setData({
      username: e.detail.value
    });
    this.checkCanLogin();
  },

  // 密码输入
  onPasswordInput(e) {
    this.setData({
      password: e.detail.value
    });
    this.checkCanLogin();
  },

  // 检查是否可以登录
  checkCanLogin() {
    const { username, password } = this.data;
    this.setData({
      canLogin: username.trim().length > 0 && password.trim().length > 0
    });
  },

  // 切换密码显示
  togglePassword() {
    this.setData({
      showPassword: !this.data.showPassword
    });
  },

  // 切换记住密码
  toggleRemember() {
    this.setData({
      rememberPassword: !this.data.rememberPassword
    });
  },

  // 处理登录
  async handleLogin() {
    if (!this.data.canLogin || this.data.loading) {
      return;
    }

    const { username, password, rememberPassword } = this.data;

    try {
      this.setData({ loading: true });

      // 调用登录API
      const response = await authAPI.login({
        username: username.trim(),
        password: password.trim()
      });

      console.log('登录成功:', response);

      // 保存token和用户信息
      wx.setStorageSync('token', response.token);
      wx.setStorageSync('userInfo', response.user);

      // 处理记住密码
      if (rememberPassword) {
        wx.setStorageSync('rememberedCredentials', {
          username: username.trim(),
          password: password.trim()
        });
      } else {
        wx.removeStorageSync('rememberedCredentials');
      }

      // 显示成功提示
      wx.showToast({
        title: '登录成功',
        icon: 'success',
        duration: 1500
      });

      // 延迟跳转到首页
      setTimeout(() => {
        wx.switchTab({
          url: '/pages/index/index'
        });
      }, 1500);

    } catch (error) {
      console.error('登录失败:', error);
      
      // 显示错误提示
      wx.showToast({
        title: error.message || '登录失败，请重试',
        icon: 'none',
        duration: 2000
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  // 微信登录
  async wechatLogin() {
    try {
      // 获取微信登录凭证
      const loginRes = await wx.login();
      console.log('微信登录凭证:', loginRes.code);

      // 获取用户信息
      const userInfoRes = await wx.getUserProfile({
        desc: '用于完善用户资料'
      });
      console.log('微信用户信息:', userInfoRes.userInfo);

      // 这里应该调用后端API进行微信登录
      // 暂时模拟登录成功
      wx.showToast({
        title: '微信登录功能开发中',
        icon: 'none'
      });

    } catch (error) {
      console.error('微信登录失败:', error);
      wx.showToast({
        title: '微信登录失败',
        icon: 'none'
      });
    }
  },

  // 忘记密码
  forgotPassword() {
    wx.showModal({
      title: '忘记密码',
      content: '请联系管理员重置密码，或通过注册手机号找回密码',
      showCancel: true,
      cancelText: '取消',
      confirmText: '联系管理员',
      success: (res) => {
        if (res.confirm) {
          // 跳转到联系页面或拨打电话
          wx.makePhoneCall({
            phoneNumber: '400-123-4567'
          });
        }
      }
    });
  },

  // 跳转到注册页面
  goToRegister() {
    wx.showToast({
      title: '注册功能开发中',
      icon: 'none'
    });
  },

  // 显示隐私政策
  showPrivacy() {
    wx.showModal({
      title: '隐私政策',
      content: '我们重视您的隐私保护，详细的隐私政策请访问官网查看。',
      showCancel: false,
      confirmText: '知道了'
    });
  },

  // 显示服务条款
  showTerms() {
    wx.showModal({
      title: '服务条款',
      content: '使用本应用即表示您同意我们的服务条款，详细条款请访问官网查看。',
      showCancel: false,
      confirmText: '知道了'
    });
  }
});