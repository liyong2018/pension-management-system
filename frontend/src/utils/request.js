import axios from 'axios';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api', // 使用相对路径，让 nginx 处理代理
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  },
  withCredentials: false // 在主机网络模式下不需要跨域凭证
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 统一从 localStorage 获取 token，优先使用 'authToken'
    const token = localStorage.getItem('authToken') || localStorage.getItem('token');
    console.log('🔑 当前token:', token ? `${token.substring(0, 20)}...` : 'null');
    console.log('🌐 请求URL:', config.baseURL + config.url);
    console.log('📝 请求方法:', config.method);
    
    if (token) {
      // 移除可能存在的 Bearer 前缀
      const tokenValue = token.startsWith('Bearer ') ? token.slice(7) : token;
      config.headers['Authorization'] = `Bearer ${tokenValue}`;
      console.log('🔒 已添加认证头');
    } else {
      console.log('⚠️ 未找到token');
    }
    
    // 添加请求时间戳，用于日志记录
    config.metadata = { startTime: new Date() };
    
    return config;
  },
  error => {
    console.error('❌ 请求错误：', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 计算请求耗时
    const endTime = new Date();
    const startTime = response.config.metadata.startTime;
    const duration = endTime - startTime;
    
    console.log(`✅ 响应成功 [${duration}ms]：`, {
      url: response.config.url,
      method: response.config.method,
      status: response.status,
      data: response.data
    });
    
    // 处理不同的响应格式
    if (response.data && (response.data.code === 0 || response.data.code === 200)) {
      return response.data.data || response.data;
    }
    
    return response.data;
  },
  error => {
    console.error('❌ 响应错误：', {
      url: error.config?.url,
      method: error.config?.method,
      status: error.response?.status,
      data: error.response?.data,
      error: error.message
    });
    
    if (error.response) {
      // 服务器返回错误状态码
      const errorMessage = error.response.data?.message || error.response.data?.msg || error.response.data?.error;
      
      switch (error.response.status) {
        case 400:
          ElMessage.error('请求参数错误：' + (errorMessage || '请检查输入'));
          break;
        case 401:
          // 未授权，清除token并跳转到登录页
          localStorage.removeItem('token');
          localStorage.removeItem('authToken');
          localStorage.removeItem('userInfo');
          const router = useRouter();
          router.push('/login');
          ElMessage.error('登录已过期，请重新登录');
          break;
        case 403:
          ElMessage.error('没有权限访问该资源：' + (errorMessage || '请联系管理员'));
          break;
        case 404:
          ElMessage.error('请求的资源不存在：' + (errorMessage || '请检查URL'));
          break;
        case 500:
          ElMessage.error('服务器内部错误：' + (errorMessage || '请稍后重试'));
          break;
        default:
          ElMessage.error(errorMessage || '请求失败，请稍后重试');
      }
    } else if (error.request) {
      // 请求发出但没有收到响应
      ElMessage.error('无法连接到服务器，请检查网络连接');
      console.error('请求超时或网络错误：', error.request);
    } else {
      // 请求配置出错
      ElMessage.error('请求配置错误：' + error.message);
      console.error('请求配置错误：', error.message);
    }
    
    return Promise.reject(error);
  }
);

export default service; 