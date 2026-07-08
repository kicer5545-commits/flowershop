<template>
  <header class="header">
    <div class="header-content">
      <div class="logo" @click="goHome">
        <h1>🌷花点时间</h1>
      </div>

      <nav class="nav">
        <router-link to="/home">首页</router-link>
        <router-link to="/products">全部商品</router-link>
        <router-link to="/cart">购物车</router-link>
        <router-link to="/order">我的订单</router-link>
        <router-link to="/personal">个人中心</router-link>
      </nav>

      <div class="user-info">
        <span v-if="userName">欢迎，{{ userName }}</span>
        <a-button type="link" @click="handleLogout">退出</a-button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { logout } from '../auth'

const router = useRouter()
const userName = ref('')

const goHome = () => {
  router.push('/home')
}

const handleLogout = () => {
  logout()
  router.push('/login')
}

onMounted(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  userName.value = user.userName || ''
})
</script>

<style scoped>
.header {
  background: linear-gradient(135deg, #ff9494);
  color: white;
  padding: 15px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  cursor: pointer;
}

.logo h1 {
  margin: 0;
  font-size: 24px;
}

.nav {
  display: flex;
  gap: 30px;
}

.nav a {
  color: white;
  text-decoration: none;
  font-size: 16px;
  transition: opacity 0.3s;
}

.nav a:hover {
  opacity: 0.8;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-info span {
  color: white;
}
</style>