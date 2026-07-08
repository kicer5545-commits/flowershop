<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="title">花店系统登录</h2>
      <!-- 给表单设置 labelCol 和 wrapperCol 控制标签和输入框宽度，保证对齐 -->
      <a-form
          :model="loginForm"
          @finish="handleLogin"
          :label-col="{ span: 6 }"
          :wrapper-col="{ span: 18 }"
      >
        <a-form-item label="用户名" name="username" :rules="[{ required: true, message: '请输入用户名' }]">
          <a-input v-model:value="loginForm.username" placeholder="请输入用户名" />
        </a-form-item>

        <a-form-item label="密码" name="password" :rules="[{ required: true, message: '请输入密码' }]">
          <a-input-password v-model:value="loginForm.password" placeholder="请输入密码" />
        </a-form-item>

        <a-form-item :wrapper-col="{ span: 24 }">
          <a-button type="primary" html-type="submit" block>登录</a-button>
        </a-form-item>
        <a-form-item :wrapper-col="{ span: 24 }">
          <a-button @click="resetForm" block>重置</a-button>
        </a-form-item>

        <div class="links">
          <router-link to="/register">前往注册</router-link>
          <router-link to="/forgot" style="margin-left: 16px;">忘记密码？</router-link>
        </div>
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

const resetForm = () => {
  loginForm.value = {
    username: '',
    password: ''
  }
}

const handleLogin = async () => {
  try {
    const response = await axiosInstance.get('/login', {
      params: {
        userInfo: loginForm.value.username,
        password: loginForm.value.password,
        isAdminLogin: false
      }
    })

    if (response && response.id) {
      login(response)
      router.push('/home')
    } else {
      alert('用户名或密码错误')
    }
  } catch (error) {
    console.error('登录失败:', error)
    alert('登录失败，请检查网络连接')
  }
}

const goToAdminLogin = () => {
  window.location.href = 'http://localhost:8082/login'
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: url('/images/background/登录背景.jpg') no-repeat center center;
  background-size: cover;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-card {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  color: #52c41a;
  margin-bottom: 30px;
  font-size: 24px;
}

.links {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  color: #666;
}

.links a {
  color: #52c41a;
  text-decoration: none;
}

.links a:hover {
  color: #ff9494;
}

/* 关键：让表单标签右对齐，保证视觉对齐 */
:deep(.ant-form-item-label) {
  text-align: right;
}
</style>