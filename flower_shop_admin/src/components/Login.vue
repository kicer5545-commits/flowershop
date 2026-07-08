<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="title">管理员登录</h2>
      <a-form :model="loginForm" @finish="handleLogin" layout="vertical">
        <a-form-item label="用户名:" name="username" :rules="[{ required: true, message: '请输入用户名' }]">
          <a-input v-model:value="loginForm.username" placeholder="请输入用户名" />
        </a-form-item>

        <a-form-item label="密码:" name="password" :rules="[{ required: true, message: '请输入密码' }]">
          <a-input-password v-model:value="loginForm.password" placeholder="请输入密码" />
        </a-form-item>

        <a-form-item>
          <div class="button-group">
            <a-button type="primary" html-type="submit" class="login-btn">登录</a-button>
            <a-button @click="resetForm" class="reset-btn">重置</a-button>
          </div>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axiosInstance from '../axios'
import { login } from '../auth'

const router = useRouter()
const loginForm = ref({
  username: '',
  password: ''
})

const handleLogin = async () => {
  try {
    const response = await axiosInstance.get('/login', {
      params: {
        userInfo: loginForm.value.username,
        password: loginForm.value.password,
        isAdminLogin: true
      }
    })

    if (response && response.id && response.role === 1) {
      login(response)
      router.push('/home')
    } else if (response === '您没有权限！') {
      alert('您没有管理员权限')
    } else {
      alert('用户名或密码错误')
    }
  } catch (error) {
    console.error('登录失败:', error)
    alert('登录失败，请检查网络连接')
  }
}

const resetForm = () => {
  loginForm.value.username = ''
  loginForm.value.password = ''
}

const goToUserLogin = () => {
  window.location.href = 'http://localhost:8080/login'
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: url('/images/background/登录背景.jpg') no-repeat;
  background-size: cover;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.logo {
  font-size: 20px;
  color: white;
  font-weight: bold;
}

.nav {
  display: flex;
  gap: 25px;
}

.nav-item {
  color: white;
  text-decoration: none;
  font-size: 16px;
}

.nav-item:hover {
  opacity: 0.8;
}

.login-card {
  width: 500px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-top: 80px;
}

.title {
  text-align: center;
  color: #7cb342;
  margin: 0 0 30px 0;
  font-size: 28px;
  font-weight: 500;
}

.button-group {
  display: flex;
  gap: 15px;
  margin-top: 20px;
}

.login-btn {
  flex: 1;
  height: 40px;
  background-color: #7cb342;
  border: none;
  font-size: 16px;
}

.login-btn:hover {
  background-color: #689f38;
}

.reset-btn {
  flex: 1;
  height: 40px;
  background-color: #e8e8e8;
  border: none;
  color: #666;
  font-size: 16px;
}

.reset-btn:hover {
  background-color: #d9d9d9;
}

.back-link {
  text-align: center;
  color: #7cb342;
  cursor: pointer;
  margin-top: 20px;
  font-size: 16px;
}

.back-link:hover {
  color: #f09a9a;
}
</style>
