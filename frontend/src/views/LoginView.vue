<template>
  <div class="login-container">
    <div class="login-box">
      <h1 class="system-title">银龄智慧养老服务系统</h1>
      <h2 class="login-title">用户登录</h2>
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="username">用户名</label>
          <input type="text" id="username" v-model="username" required placeholder="请输入用户名">
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input type="password" id="password" v-model="password" required placeholder="请输入密码">
        </div>
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>
        <button type="submit" class="login-button" :disabled="loading">
          {{ loading ? '登录中...' : '登 录' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const username = ref('');
const password = ref('');
const errorMessage = ref('');
const loading = ref(false);
const router = useRouter();

const handleLogin = async () => {
  loading.value = true;
  errorMessage.value = '';

  try {
    console.log('🔄 正在尝试登录...', { 
      username: username.value, 
      password: password.value,
      usernameLength: username.value.length,
      passwordLength: password.value.length 
    });
    
    const requestBody = {
      username: username.value,
      password: password.value,
    };
    
    console.log('📤 发送的请求体:', requestBody);
    
    const response = await fetch('/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      mode: 'cors',
      credentials: 'omit',
      body: JSON.stringify(requestBody),
    });

    console.log('📡 登录响应状态:', response.status);
    
    // 尝试解析响应内容
    let data;
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      data = await response.json();
      console.log('📄 登录响应数据:', data);
    } else {
      // 如果不是 JSON，则读取为文本
      const text = await response.text();
      console.log('📄 登录响应文本:', text);
      data = { message: text };
    }

    if (response.ok) {
      if (data.token) {
        console.log('✅ 登录成功，保存 token');
        localStorage.setItem('authToken', data.token);
        localStorage.removeItem('token');
        if (data.user) {
          localStorage.setItem('userInfo', JSON.stringify(data.user));
        }
        
        // 跳转到首页
        await router.push('/');
        
        // 延迟一小段时间后刷新菜单，确保App组件已经挂载完成
        setTimeout(() => {
          if (window.refreshTopMenu) {
            console.log('🔄 登录成功后刷新菜单');
            window.refreshTopMenu();
          }
        }, 100);
      } else {
        errorMessage.value = '登录成功但未收到 token，请联系管理员。';
      }
    } else {
      // 改进错误处理，显示验证错误的详细信息
      if (response.status === 400 && data && typeof data === 'object') {
        // 处理验证错误
        const validationErrors = [];
        if (data.username) validationErrors.push(`用户名: ${data.username}`);
        if (data.password) validationErrors.push(`密码: ${data.password}`);
        
        if (validationErrors.length > 0) {
          errorMessage.value = validationErrors.join(', ');
        } else {
          errorMessage.value = '请求格式错误';
        }
      } else {
        errorMessage.value = data.message || data.error || '用户名或密码错误';
      }
    }
  } catch (error) {
    console.error('❌ 登录错误:', error);
    errorMessage.value = '登录失败，请检查网络连接或稍后重试。';
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-image: radial-gradient(circle at 20% 80%, rgba(0, 212, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 107, 53, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(123, 104, 238, 0.1) 0%, transparent 50%);
  background-color: #0c1426; /* Dark blue background */
  font-family: 'Microsoft YaHei', sans-serif;
  color: #ffffff;
}

.login-box {
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(10px);
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3), 0 0 0 1px rgba(0, 212, 255, 0.3); /* Subtle glow border */
  width: 100%;
  max-width: 400px;
  text-align: center;
}

.system-title {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(45deg, #00d4ff, #ff6b35);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 10px;
  text-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
}

.login-title {
  font-size: 20px;
  color: #ffffff;
  margin-bottom: 30px;
  font-weight: 500;
}

.login-form .form-group {
  margin-bottom: 20px;
  text-align: left;
}

.login-form label {
  display: block;
  margin-bottom: 8px;
  color: #00d4ff; /* Accent color for labels */
  font-size: 14px;
  font-weight: 500;
}

.login-form input[type="text"],
.login-form input[type="password"] {
  width: calc(100% - 24px); /* Account for padding */
  padding: 12px;
  border: 1px solid rgba(0, 212, 255, 0.3);
  border-radius: 8px;
  background-color: rgba(255, 255, 255, 0.05);
  color: #ffffff;
  font-size: 14px;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.login-form input[type="text"]::placeholder,
.login-form input[type="password"]::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.login-form input[type="text"]:focus,
.login-form input[type="password"]:focus {
  outline: none;
  border-color: #00d4ff;
  box-shadow: 0 0 10px rgba(0, 212, 255, 0.3);
}

.error-message {
  color: #ff4757; /* Red for error messages */
  background-color: rgba(255, 71, 87, 0.1);
  border: 1px solid rgba(255, 71, 87, 0.3);
  padding: 10px;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 13px;
  text-align: center;
}

.login-button {
  width: 100%;
  padding: 12px;
  background: linear-gradient(45deg, #00d4ff, #00aaff);
  border: none;
  border-radius: 8px;
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s, transform 0.2s;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.login-button:hover {
  background: linear-gradient(45deg, #00aaff, #00d4ff);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 212, 255, 0.3);
}

.login-button:disabled {
  background: #555;
  cursor: not-allowed;
  opacity: 0.7;
}
</style> 