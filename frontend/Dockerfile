# 使用nginx作为基础镜像
FROM nginx:alpine

# 复制构建产物到nginx目录
COPY dist /usr/share/nginx/html

# 复制nginx配置文件
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 3000

# 启动nginx
CMD ["nginx", "-g", "daemon off;"] 