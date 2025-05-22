import axios from 'axios';
import { ElMessage } from 'element-plus';

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api', // 使用相对路径
  timeout: 15000,
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
      return null;
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
    
    // 直接返回响应数据
    return response.data;
  },
  error => {
    console.error('响应错误：', error);
    
    // 处理网络错误
    if (!error.response) {
      ElMessage({
        message: '网络错误，请检查您的网络连接',
        type: 'error',
        duration: 5 * 1000
      });
      return Promise.reject(new Error('网络错误'));
    }
    
    // 处理 401 未授权的情况
    if (error.response?.status === 401) {
      // 清除 token 并跳转到登录页
      localStorage.removeItem('token');
      window.location.href = '/login';
      return Promise.reject(new Error('未授权，请重新登录'));
    }
    
    // 处理其他错误
    const message = error.response?.data?.message || '请求失败';
    ElMessage({
      message: message,
      type: 'error',
      duration: 5 * 1000
    });
    
    return Promise.reject(error);
  }
);

export default service; 