<template>
  <div class="detail-page">
    <Header />

    <div class="container" v-if="product">
      <div class="product-detail">
        <div class="product-image">
          <img :src="product.image" :alt="product.name" />
        </div>

        <div class="product-info">
          <h1>{{ product.name }}</h1>
          <p class="description">{{ product.description }}</p>

          <div class="price-section">
            <span class="price">¥{{ product.price }}</span>
            <span class="stock">库存: {{ product.stock }}</span>
          </div>

          <div class="rating">
            <span>评分: ★ {{ product.rating }}</span>
          </div>

          <div class="actions">
            <a-button
                :type="favorited ? 'default' : 'primary'"
                danger
                size="large"
                @click="toggleFavorite"
            >
              {{ favorited ? '❤️ 已收藏' : '🤍 收藏' }}
            </a-button>
            <a-button type="primary" danger size="large" @click="addToCart">
              加入购物车
            </a-button>
            <a-button size="large" @click="goBack">返回列表</a-button>
          </div>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Header from './Header.vue'
import Footer from './Footer.vue'
import axiosInstance from '../axios'
import { message } from 'ant-design-vue'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const favorited = ref(false)

const loadProduct = async () => {
  try {
    const data = await axiosInstance.get('/product/detail', {
      params: { id: route.params.id }
    })
    product.value = data

    const user = JSON.parse(localStorage.getItem('user'))
    if (user) {
      try {
        const res = await axiosInstance.get('/favorite/check', {
          params: { productId: data.id }
        })
        favorited.value = res === true
      } catch (e) {
        favorited.value = false
      }
    }
  } catch (error) {
    console.error('加载商品详情失败:', error)
  }
}

const toggleFavorite = async () => {
  const user = JSON.parse(localStorage.getItem('user'))
  if (!user) {
    message.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    if (favorited.value) {
      await axiosInstance.get('/favorite/remove', { params: { productId: product.value.id } })
      favorited.value = false
      message.success('已取消收藏')
    } else {
      await axiosInstance.get('/favorite/add', { params: { productId: product.value.id } })
      favorited.value = true
      message.success('收藏成功')
    }
  } catch (error) {
    console.error('操作收藏失败', error)
    message.error('操作失败')
  }
}

const addToCart = async () => {
  const user = JSON.parse(localStorage.getItem('user'))
  if (!user) {
    message.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    const response = await axiosInstance.post('/cart/add', {
      productId: product.value.id,
      amount: 1
    })

    if (response === '添加成功') {
      message.success('已添加到购物车')
    } else {
      message.warning(response)
    }
  } catch (error) {
    console.error('添加购物车失败:', error)
    message.error('添加失败，请重试')
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadProduct()
})
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.container {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
}

.product-detail {
  background: white;
  border-radius: 8px;
  padding: 40px;
  display: flex;
  gap: 40px;
}

.product-image {
  flex: 1;
}

.product-image img {
  width: 100%;
  border-radius: 8px;
}

.product-info {
  flex: 1;
}

.product-info h1 {
  margin-bottom: 20px;
  color: #333;
}

.description {
  color: #666;
  line-height: 1.8;
  margin-bottom: 30px;
}

.price-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 8px;
}

.price {
  font-size: 32px;
  color: #ff6b6b;
  font-weight: bold;
}

.stock {
  color: #52c41a;
  font-size: 16px;
}

.rating {
  margin-bottom: 30px;
  color: #ffc107;
  font-size: 18px;
}

.actions {
  display: flex;
  gap: 15px;
}

.actions button {
  flex: 1;
}
</style>
