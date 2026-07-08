<template>
  <div class="register-container">
    <div class="register-card">
      <h2 class="title">用户注册</h2>
      <a-form :model="registerForm" @finish="handleRegister">
        <a-form-item label="用户名" name="username" :rules="[{ required: true, message: '请输入用户名' }]">
          <a-input v-model:value="registerForm.username" placeholder="请输入用户名" />
        </a-form-item>

        <a-form-item label="密码" name="password" :rules="[{ required: true, message: '请输入密码' }, { min: 6, message: '密码至少6位' }]">
          <a-input-password v-model:value="registerForm.password" placeholder="请输入密码" />
        </a-form-item>

        <a-form-item label="确认密码" name="confirmPassword" :rules="[{ required: true, message: '请确认密码' }]">
          <a-input-password v-model:value="registerForm.confirmPassword" placeholder="请再次输入密码" />
        </a-form-item>

        <a-form-item label="手机号" name="phone" :rules="[{ required: true, message: '请输入手机号' }]">
          <a-input v-model:value="registerForm.phone" placeholder="请输入手机号" />
        </a-form-item>

        <a-form-item label="地址" name="address">
          <a-textarea v-model:value="registerForm.address" placeholder="请输入地址" :rows="3" />
        </a-form-item>

        <a-form-item>
          <a-button type="primary" html-type="submit" block>注册</a-button>
        </a-form-item>

        <div class="links">
          <router-link to="/login">已有账号？立即登录</router-link>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup>
  import { ref } from 'vue'
  import { useRouter } from 'vue-router'
  import axiosInstance from '../axios'

  const router = useRouter()
  const registerForm = ref({
    username: '',
    password: '',
    confirmPassword: '',
    phone: '',
    address: ''
  })

  const handleRegister = async () => {
    if (registerForm.value.password !== registerForm.value.confirmPassword) {
      alert('两次输入的密码不一致')
      return
    }

    const userData = {
      userName: registerForm.value.username,
      password: registerForm.value.password,
      phone: registerForm.value.phone,
      address: registerForm.value.address,
      role: 2
    }

    try {
      const response = await axiosInstance.post('/register', userData)
      if (response === '注册成功') {
        alert('注册成功，请登录')
        router.push('/login')
      } else {
        alert(response || '注册失败')
      }
    } catch (error) {
      console.error('注册失败:', error)
      alert('注册失败，请检查网络连接')
    }
  }
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.register-card {
  width: 450px;
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
  text-align: center;
  margin-top: 20px;
}

.links a {
  color: #52c41a;
  text-decoration: none;
}

.links a:hover {
  color: #ff9494;
}
</style>