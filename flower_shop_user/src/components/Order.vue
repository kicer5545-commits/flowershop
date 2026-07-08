<template>
  <div class="order-page">
    <Header />

    <div class="container">
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <h2>我的订单</h2>
        <a-button v-if="!loading" type="default" @click="exportToExcel">📎 导出 Excel</a-button>
      </div>

      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>

      <div v-else-if="orders.length === 0" class="empty-orders">
        <p>暂无订单</p>
        <router-link to="/products">去购物</router-link>
      </div>

      <div v-else class="order-list">
        <div v-for="order in orders" :key="order.id" class="order-item">
          <div class="order-header">
            <div class="header-left">
              <span class="order-id">订单号: {{ order.id }}</span>
              <span class="order-time">{{ order.time }}</span>
            </div>
            <a-tag :color="getStatusColor(order.status)" class="order-status">
              {{ getStatusText(order.status) }}
            </a-tag>
          </div>

          <div class="order-body">
            <p class="product-details">{{ order.productDetails }}</p>
            <div class="order-footer">
              <div class="footer-left">
                <span class="order-address">收货地址: {{ order.address }}</span>
              </div>
              <div class="footer-right">
                <span class="order-price">¥{{ order.price }}</span>
                <a-button
                    v-if="order.status === '0'"
                    type="primary"
                    size="small"
                    @click="payOrder(order.id)"
                    style="margin-left: 15px"
                >
                  立即支付
                </a-button>
                <a-button
                    v-if="order.status === '2'"
                    type="default"
                    size="small"
                    @click="confirmReceive(order.id)"
                    style="margin-left: 15px"
                >
                  确认收货
                </a-button>
                <a-button
                    type="danger"
                    size="small"
                    @click="deleteOrder(order.id)"
                    style="margin-left: 15px"
                >
                  删除订单
                </a-button>
                <a-button size="small" @click="exportOrderPDF(order)">📄 导出PDF</a-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import Header from './Header.vue'
import Footer from './Footer.vue'
import axiosInstance from '../axios'
import * as XLSX from 'xlsx'
import jsPDF from 'jspdf'
import html2canvas from 'html2canvas'

const router = useRouter()
const orders = ref([])
const loading = ref(false)

const exportToExcel = () => {
  if (orders.value.length === 0) {
    message.warning('暂无订单可导出')
    return
  }
  const exportData = orders.value.map(order => ({
    '订单号': order.id,
    '商品详情': order.productDetails,
    '总金额': order.price,
    '订单状态': getStatusText(order.status),
    '收货地址': order.address,
    '下单时间': order.time
  }))
  const ws = XLSX.utils.json_to_sheet(exportData)
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, '我的订单')
  XLSX.writeFile(wb, `我的订单_${new Date().toISOString().slice(0,19).replace(/:/g, '-')}.xlsx`)
  message.success('导出成功')
}

const loadOrders = async () => {
  const user = JSON.parse(localStorage.getItem('user'))
  if (!user) {
    router.push('/login')
    return
  }

  loading.value = true
  try {
    const data = await axiosInstance.get('/order/list', {
      params: { userId: user.id }
    })
    orders.value = data || []
  } catch (error) {
    console.error('加载订单失败:', error)
    message.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

const getStatusText = (status) => {
  const statusMap = {
    '0': '未支付',
    '1': '已支付',
    '2': '已发货',
    '3': '已完成'
  }
  return statusMap[status] || '未知状态'
}

const getStatusColor = (status) => {
  const colorMap = {
    '0': 'orange',
    '1': 'blue',
    '2': 'green',
    '3': 'default'
  }
  return colorMap[status] || 'default'
}

const payOrder = async (orderId) => {
  Modal.confirm({
    title: '确认支付',
    content: '确定要支付此订单吗？',
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        const response = await axiosInstance.post('/order/update', {
          id: orderId,
          status: '1'
        })

        if (response === '更新成功') {
          message.success('支付成功，商品库存已更新')
          await loadOrders()
        } else {
          message.error(response)
        }
      } catch (error) {
        console.error('支付失败:', error)
        message.error('支付失败')
      }
    }
  })
}

const confirmReceive = async (orderId) => {
  Modal.confirm({
    title: '确认收货',
    content: '确定已收到商品吗？',
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        const response = await axiosInstance.post('/order/update', {
          id: orderId,
          status: '3'
        })

        if (response === '更新成功') {
          message.success('确认收货成功')
          await loadOrders()
        } else {
          message.error(response)
        }
      } catch (error) {
        console.error('确认收货失败:', error)
        message.error('操作失败')
      }
    }
  })
}

const deleteOrder = async (orderId) => {
  Modal.confirm({
    title: '确认删除',
    content: '确定要删除此订单吗？删除后无法恢复。',
    okText: '确认',
    cancelText: '取消',
    okType: 'danger',
    onOk: async () => {
      try {
        const response = await axiosInstance.get('/order/delete', {
          params: { id: orderId }
        })

        if (response === '删除成功') {
          message.success('订单删除成功')
          await loadOrders()
        } else {
          message.error(response)
        }
      } catch (error) {
        console.error('删除订单失败:', error)
        message.error('删除失败')
      }
    }
  })
}

const exportOrderPDF = async (order) => {
  const element = document.createElement('div')
  element.style.width = '600px'
  element.style.padding = '20px'
  element.style.backgroundColor = 'white'
  element.style.fontFamily = 'Microsoft YaHei, Arial'
  element.innerHTML = `
    <div style="text-align:center;">
      <h2 style="color:#52c41a;">🌷 花店订单详情</h2>
    </div>
    <hr />
    <p><strong>订单号：</strong>${order.id}</p>
    <p><strong>下单时间：</strong>${order.time}</p>
    <p><strong>商品详情：</strong>${order.productDetails}</p>
    <p><strong>总金额：</strong>¥${order.price}</p>
    <p><strong>收货地址：</strong>${order.address}</p>
    <p><strong>订单状态：</strong>${getStatusText(order.status)}</p>
    <hr />
    <p style="text-align:center; color:#999;">感谢您的购买！</p>
  `
  document.body.appendChild(element)

  try {
    const canvas = await html2canvas(element, { scale: 2 })
    const imgData = canvas.toDataURL('image/png')
    const pdf = new jsPDF('p', 'mm', 'a4')
    const imgWidth = 190
    const imgHeight = (canvas.height * imgWidth) / canvas.width
    let position = 0
    pdf.addImage(imgData, 'PNG', 10, position, imgWidth, imgHeight)
    pdf.save(`订单_${order.id}.pdf`)
  } catch (error) {
    console.error('生成PDF失败', error)
    message.error('生成PDF失败，请重试')
  } finally {
    document.body.removeChild(element)
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.container {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
}

.container h2 {
  margin-bottom: 30px;
  color: #333;
}

.empty-orders {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 8px;
}

.empty-orders p {
  font-size: 18px;
  color: #999;
  margin-bottom: 20px;
}

.empty-orders a {
  color: #52c41a;
  text-decoration: none;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-item {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.order-header {
  padding: 15px 20px;
  background: #f9f9f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
}

.header-left {
  display: flex;
  gap: 20px;
  align-items: center;
}

.order-id {
  color: #666;
  font-weight: 500;
}

.order-time {
  color: #999;
  font-size: 14px;
}

.order-status {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 14px;
}

.order-body {
  padding: 20px;
}

.product-details {
  margin: 0 0 15px 0;
  color: #333;
  line-height: 1.6;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.footer-left {
  flex: 1;
}

.order-address {
  color: #666;
  font-size: 14px;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.order-price {
  font-size: 20px;
  color: #ff6b6b;
  font-weight: bold;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  color: #999;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e0e0e0;
  border-top: 4px solid #52c41a;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
