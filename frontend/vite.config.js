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
    port: 3000, // 前端开发服务器端口
    proxy: {
      // 配置代理，解决跨域问题
      '/api': { // 将匹配到 /api 的请求代理到后端服务器
        target: 'http://localhost:8080', // 后端服务实际地址
        changeOrigin: true, // 是否改变源地址
        // rewrite: (path) => path.replace(/^\/api/, '') // 如果后端API不带/api前缀，则需要重写路径
      },
    },
  },
}); 