<template>
  <div class="cart-page">
    <Header />

    <div class="container">
      <h2>我的购物车</h2>

      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>

      <div v-else-if="cartItems.length === 0" class="empty-cart">
        <p>购物车是空的</p>
        <router-link to="/products">去购物</router-link>
      </div>

      <div v-else>
        <div class="cart-list">
          <div v-for="(item, index) in cartItems" :key="item.id" class="cart-item">
            <img :src="item.productImage" :alt="item.productName" />
            <div class="item-info">
              <h3>{{ item.productName }}</h3>
              <p class="price">¥{{ item.productPrice }}</p>
            </div>
            <div class="quantity">
              <a-input-number v-model:value="item.amount" :min="1" @change="updateQuantity(item)" />
            </div>
            <div class="subtotal">
              <p>小计: ¥{{ (item.productPrice * item.amount).toFixed(2) }}</p>
            </div>
            <div class="actions">
              <a-button type="danger" @click="removeItem(item.id)">删除</a-button>
            </div>
          </div>
        </div>

        <div class="cart-summary">
          <div class="total">
            <span>总计:</span>
            <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
          </div>
          <div class="checkout-actions">
            <a-button @click="clearCart">清空购物车</a-button>
            <a-button type="primary" @click="showCheckoutModal">去结算</a-button>
          </div>
        </div>
      </div>
    </div>

    <a-modal
        v-model:visible="checkoutVisible"
        title="确认订单"
        @ok="handleCheckout"
        @cancel="handleCancel"
        ok-text="提交订单"
        cancel-text="取消"
    >
      <div class="checkout-form">
        <a-form :model="checkoutForm" layout="vertical">
          <a-form-item label="收货地址" required>
            <a-textarea
                v-model:value="checkoutForm.address"
                placeholder="请输入详细的收货地址"
                :rows="3"
            />
          </a-form-item>

          <div class="order-summary">
            <h4>订单摘要</h4>
            <p>商品数量: {{ totalItems }} 件</p>
            <p class="total-amount">应付总额: ¥{{ totalPrice.toFixed(2) }}</p>
          </div>
        </a-form>
      </div>
    </a-modal>

    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import Header from './Header.vue'
import Footer from './Footer.vue'
import axiosInstance from '../axios'

const router = useRouter()
const cartItems = ref([])
const checkoutVisible = ref(false)
const checkoutForm = ref({
  address: ''
})
const loading = ref(false)

const loadCart = async () => {
  const user = JSON.parse(localStorage.getItem('user'))
  if (!user) {
    router.push('/login')
    return
  }

  loading.value = true
  try {
    const carts = await axiosInstance.get('/cart/list', {
      params: { userId: user.id }
    })
    cartItems.value = carts || []
  } catch (error) {
    console.error('加载购物车失败:', error)
    message.error('加载购物车失败')
  } finally {
    loading.value = false
  }
}

const updateQuantity = async (item) => {
  try {
    const response = await axiosInstance.post('/cart/update', {
      id: item.id,
      amount: item.amount
    })

    if (response !== '更新成功') {
      message.warning(response)
    } else {
      message.success('更新成功')
    }
  } catch (error) {
    console.error('更新数量失败:', error)
    message.error('更新失败')
  }
}

const removeItem = async (id) => {
  try {
    const response = await axiosInstance.get('/cart/delete', { params: { id } })

    if (response === '删除成功') {
      await loadCart()
      message.success('删除成功')
    } else {
      message.warning(response)
    }
  } catch (error) {
    console.error('删除失败:', error)
    message.error('删除失败')
  }
}

const clearCart = async () => {
  const user = JSON.parse(localStorage.getItem('user'))
  try {
    const response = await axiosInstance.get('/cart/clear', { params: { userId: user.id } })

    if (response === '清空成功') {
      await loadCart()
      message.success('清空成功')
    } else {
      message.warning(response)
    }
  } catch (error) {
    console.error('清空失败:', error)
    message.error('清空失败')
  }
}

const showCheckoutModal = () => {
  const user = JSON.parse(localStorage.getItem('user'))
  if (!user) {
    router.push('/login')
    return
  }

  checkoutForm.value.address = user.address || ''
  checkoutVisible.value = true
}

const handleCheckout = async () => {
  if (!checkoutForm.value.address || checkoutForm.value.address.trim() === '') {
    message.warning('请填写收货地址')
    return
  }

  const user = JSON.parse(localStorage.getItem('user'))

  try {
    console.log('开始创建订单...')
    const response = await axiosInstance.post('/order/createFromCart', {
      address: checkoutForm.value.address
    })

    console.log('订单创建响应:', response)

    if (response === '下单成功') {
      message.success('下单成功')
      checkoutVisible.value = false
      await loadCart()
      router.push('/order')
    } else {
      message.error(response || '下单失败')
    }
  } catch (error) {
    console.error('创建订单失败:', error)
    if (error.response && error.response.data) {
      message.error('创建订单失败: ' + error.response.data.error)
    } else {
      message.error('创建订单失败: ' + error.message)
    }
  }
}

const handleCancel = () => {
  checkoutVisible.value = false
  checkoutForm.value.address = ''
}

const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    return sum + (item.productPrice * item.amount)
  }, 0)
})

const totalItems = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    return sum + item.amount
  }, 0)
})

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
.cart-page {
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

.empty-cart {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 8px;
}

.empty-cart p {
  font-size: 18px;
  color: #999;
  margin-bottom: 20px;
}

.empty-cart a {
  color: #52c41a;
  text-decoration: none;
}

.cart-list {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
  gap: 20px;
}

.cart-item:last-child {
  border-bottom: none;
}

.cart-item img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
}

.item-info {
  flex: 1;
}

.item-info h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.price {
  color: #ff6b6b;
  font-size: 18px;
  font-weight: bold;
}

.quantity {
  width: 120px;
}

.subtotal {
  width: 150px;
  text-align: right;
}

.subtotal p {
  margin: 0;
  color: #ff6b6b;
  font-weight: bold;
}

.actions {
  width: 100px;
}

.cart-summary {
  margin-top: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total {
  font-size: 20px;
}

.total-price {
  color: #ff6b6b;
  font-size: 28px;
  font-weight: bold;
  margin-left: 10px;
}

.checkout-actions {
  display: flex;
  gap: 15px;
}

.checkout-form {
  padding: 20px 0;
}

.order-summary {
  margin-top: 20px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 4px;
}

.order-summary h4 {
  margin: 0 0 10px 0;
  color: #333;
}

.order-summary p {
  margin: 5px 0;
  color: #666;
}

.total-amount {
  color: #ff6b6b;
  font-size: 18px;
  font-weight: bold;
  margin-top: 10px !important;
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
