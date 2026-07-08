<template>
  <div class="admin-layout">
    <!-- 顶部粉色导航栏 -->
    <div class="top-nav">
      <div class="nav-content">
        <div class="nav-left">
          <span class="nav-icon"></span>
          <span class="nav-title">🌷花点时间</span>
        </div>
        <div class="nav-right">
          <span class="admin-name">管理员: {{ adminName }}</span>
          <a-button type="link" @click="handleLogout" class="logout-link">
            退出
          </a-button>
        </div>
      </div>
    </div>

    <a-layout style="min-height: calc(100vh - 56px); margin-top: 56px;">
      <a-layout-sider :width="220" style="background: #fff; box-shadow: 2px 0 8px rgba(0,0,0,0.06)">
        <div style="padding: 20px 16px 12px; border-bottom: 1px solid #f0f0f0;">
          <h3 style="margin: 0; color: #1890ff; font-size: 16px;">🛠️ 管理功能</h3>
        </div>
        <a-menu
          v-model:selectedKeys="selectedMenu"
          mode="inline"
          :style="{ height: '100%', borderRight: 0 }"
          @click="handleMenuClick"
        >
          <a-menu-item key="products">
            <template #icon>
              <span>🌷</span>
            </template>
            产品管理
          </a-menu-item>

          <a-menu-item key="orders">
            <template #icon>
              <span>🛒</span>
            </template>
            订单管理
          </a-menu-item>

          <a-menu-item key="users">
            <template #icon>
              <span>👤</span>
            </template>
            用户管理
          </a-menu-item>

          <a-menu-item key="dashboard">
            <template #icon><span>📊</span></template>
            数据统计
          </a-menu-item>
        </a-menu>
      </a-layout-sider>

      <a-layout>
        <a-layout-header style="background: #fff; padding: 0 24px; box-shadow: 0 1px 4px rgba(0,0,0,0.06)">
          <div class="admin-header">
            <h2 style="margin: 0;">鲜花管理后台</h2>
          </div>
        </a-layout-header>
        <a-layout-content :style="{ margin: '24px', padding: '24px', background: '#fff', minHeight: '280px' }">
          <router-view :key="$route.path" @refresh-products="loadProducts" />
        </a-layout-content>
      </a-layout>
    </a-layout>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { logout } from '../auth'
import axiosInstance from '../axios'

const router = useRouter()
const route = useRoute()
const selectedMenu = ref([route.path.replace('/', '') || 'products'])
const adminName = ref('')

const handleMenuClick = ({ key }) => {
  selectedMenu.value = [key]
  router.push(`/${key}`)
}

const showAddProduct = () => {
  router.push('/products')
  setTimeout(() => {
    window.dispatchEvent(new CustomEvent('show-add-product'))
  }, 100)
}

const exportData = async () => {
  try {
    const products = await axiosInstance.get('/products')
    const orders = await axiosInstance.get('/orders')
    const users = await axiosInstance.get('/users')

    const data = {
      products,
      orders,
      users,
      exportTime: new Date().toISOString()
    }

    const dataStr = JSON.stringify(data, null, 2)
    const dataUri = 'data:application/json;charset=utf-8,' + encodeURIComponent(dataStr)
    const exportFileDefaultName = `flower-data-${new Date().getTime()}.json`

    const linkElement = document.createElement('a')
    linkElement.setAttribute('href', dataUri)
    linkElement.setAttribute('download', exportFileDefaultName)
    linkElement.click()

    message.success('数据导出成功！')
  } catch (error) {
    message.error('数据导出失败')
  }
}

const loadProducts = async () => {
  // 刷新数据
}

const handleLogout = () => {
  logout()
  router.push('/login')
}

onMounted(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  adminName.value = user.userName || '管理员'

  if (!route.path || route.path === '/home') {
    router.push('/products')
    selectedMenu.value = ['products']
  }
})
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background: #f5f5f5;
}

.top-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 56px;
  background: #ff9494;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 32px;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-icon {
  font-size: 24px;
}

.nav-title {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.admin-name {
  color: #fff;
  font-size: 14px;
}

.logout-link {
  color: #fff;
  font-size: 14px;
  padding: 0 8px;
}

.logout-link:hover {
  color: #fff;
  opacity: 0.8;
}

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
}



.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.table-actions {
  display: flex;
  gap: 10px;
}

.form-header {
  margin-bottom: 24px;
}

.form-header h3 {
  margin-bottom: 8px;
  color: #333;
}

.form-header p {
  color: #666;
  margin: 0;
}

.image-upload-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.category-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
}

.price-text {
  color: #ff4d4f;
  font-weight: bold;
  font-size: 15px;
}

.stock-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
}

.edit-btn {
  color: #1890ff;
  padding: 0;
}

.delete-btn {
  padding: 0;
}

:deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-weight: 600;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: #fafafa;
}

:deep(.ant-pagination) {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
