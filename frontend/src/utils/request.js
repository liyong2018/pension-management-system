import axios from 'axios';
import { ElMessage } from 'element-plus';

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api', // 修改为 /api，确保所有请求都会经过代理
  timeout: 15000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    // 确保 URL 格式正确
    if (!config.url.startsWith('/')) {
      config.url = '/' + config.url;
    }
    return config;
  },
  error => {
    console.error('请求错误：', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 处理 204 No Content 响应
    if (response.status === 204) {
      return response;
    }
    
    // 如果后端返回的不是 20x 状态码
    if (response.status !== 200 && response.status !== 201) {
      ElMessage({
        message: response.data?.message || '错误',
        type: 'error',
        duration: 5 * 1000
      });
      return Promise.reject(new Error(response.data?.message || '错误'));
    }
    return response;
  },
  error => {
    console.error('响应错误：', error);
    const message = error.response?.data?.message || '请求失败';
    ElMessage({
      message: message,
      type: 'error',
      duration: 5 * 1000
    });
    
    // 处理 401 未授权的情况
    if (error.response?.status === 401) {
      // 清除 token 并跳转到登录页
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    
    return Promise.reject(error);
  }
);

export default service; 