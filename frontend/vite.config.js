import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 3000, // 更新为当前使用的端口
    host: '0.0.0.0',
    proxy: {
      // 配置代理，解决跨域问题
      '/api': { // 将匹配到 /api 的请求代理到后端服务器
        target: 'http://localhost:8082', // 后端服务实际地址
        changeOrigin: true, // 是否改变源地址
        secure: false, // 禁用 SSL 证书验证
        ws: true, // 支持 websocket
        configure: (proxy, _options) => {
          proxy.on('error', (err, _req, _res) => {
            console.log('代理错误:', err);
          });
          proxy.on('proxyReq', (proxyReq, req, _res) => {
            console.log('发送请求:', req.method, req.url);
            console.log('请求头:', proxyReq.getHeaders());
          });
          proxy.on('proxyRes', (proxyRes, req, _res) => {
            console.log('收到响应:', req.url, proxyRes.statusCode);
            console.log('响应头:', proxyRes.headers);
          });
        }
      },
      // Add proxy for /auth path
      '/auth': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        secure: false,
        configure: (proxy, _options) => {
          proxy.on('error', (err, _req, _res) => {
            console.log('认证代理错误:', err);
          });
          proxy.on('proxyReq', (proxyReq, req, _res) => {
            console.log('发送认证请求:', req.method, req.url);
          });
          proxy.on('proxyRes', (proxyRes, req, _res) => {
            console.log('收到认证响应:', req.url, proxyRes.statusCode);
          });
        }
      }
    },
  },
});