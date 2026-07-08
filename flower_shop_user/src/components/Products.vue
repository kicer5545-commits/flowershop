<template>
  <div class="products-page">
    <Header />

    <div class="container">
      <div class="sidebar">
        <h3>商品分类</h3>
        <ul>
          <li :class="{ active: currentCategory === 'all' }" @click="filterCategory('all')">全部商品</li>
          <li :class="{ active: currentCategory === 'rose' }" @click="filterCategory('rose')">玫瑰系列</li>
          <li :class="{ active: currentCategory === 'lily' }" @click="filterCategory('lily')">百合系列</li>
          <li :class="{ active: currentCategory === 'ChineseRose' }" @click="filterCategory('ChineseRose')">月季系列</li>
          <li :class="{ active: currentCategory === 'chrysanthemum' }" @click="filterCategory('chrysanthemum')">菊花系列</li>
          <li :class="{ active: currentCategory === 'carnation' }" @click="filterCategory('carnation')">康乃馨</li>
          <li :class="{ active: currentCategory === 'orchid' }" @click="filterCategory('orchid')">兰花系列</li>
          <li :class="{ active: currentCategory === 'birthday' }" @click="filterCategory('birthday')">生日花束</li>
          <li :class="{ active: currentCategory === 'valentine' }" @click="filterCategory('valentine')">情人节</li>
          <li :class="{ active: currentCategory === 'wedding' }" @click="filterCategory('wedding')">婚礼花艺</li>
          <li :class="{ active: currentCategory === 'indoor' }" @click="filterCategory('indoor')">绿植盆栽</li>
        </ul>
      </div>

      <div class="main-content">
        <h2>{{ getCategoryName(currentCategory) }}</h2>
        <div v-if="loading" class="loading-container">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>
        <div v-else class="product-grid">
          <ProductCard
              v-for="product in products"
              :key="product.id"
              :product="product"
              @addToCart="addToCart"
              @imageClick="viewDetail"
              @toggleFavorite="toggleFavorite"
          />
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
import ProductCard from './ProductCard.vue'
import axiosInstance from '../axios'

const route = useRoute()
const router = useRouter()
const currentCategory = ref(route.query.category || 'all')
const products = ref([])
const loading = ref(false)

const loadProducts = async () => {
  loading.value = true
  try {
    const params = currentCategory.value === 'all' ? {} : { category: currentCategory.value }
    const data = await axiosInstance.get('/products', { params })
    const user = JSON.parse(localStorage.getItem('user'))
    if (user) {
      for (let p of data) {
        try {
          const res = await axiosInstance.get('/favorite/check', { params: { productId: p.id } })
          p.favorited = res === true
        } catch (e) {
          p.favorited = false
        }
      }
    }
    products.value = data
  } catch (error) {
    console.error('加载商品失败:', error)
  } finally {
    loading.value = false
  }
}

const toggleFavorite = async (product) => {
  const user = JSON.parse(localStorage.getItem('user'))
  if (!user) {
    router.push('/login')
    return
  }
  try {
    if (product.favorited) {
      await axiosInstance.get('/favorite/remove', { params: { productId: product.id } })
      product.favorited = false
      alert('已取消收藏')
    } else {
      await axiosInstance.get('/favorite/add', { params: { productId: product.id } })
      product.favorited = true
      alert('收藏成功')
    }
  } catch (error) {
    console.error('操作收藏失败', error)
    alert('操作失败')
  }
}

const filterCategory = (category) => {
  currentCategory.value = category
  router.push({ path: '/products', query: { category } })
  loadProducts()
}

const getCategoryName = (category) => {
  const names = {
    'all': '全部商品',
    'rose': '玫瑰系列',
    'lily': '百合系列',
    'ChineseRose': '月季系列',
    'chrysanthemum': '菊花系列',
    'carnation': '康乃馨系列',
    'orchid': '兰花系列',
    'others': '其他花卉',
    'birthday': '生日花束',
    'anniversary': '周年纪念',
    'valentine': '情人节特供',
    'mothers-day': '母亲节花束',
    'wedding': '婚礼花艺',
    'flower-box': '鲜花礼盒',
    'gift-set': '礼品套装',
    'indoor': '绿植盆栽',
    'succulent': '多肉植物',
    'table': '餐桌花艺',
    'wall': '墙面装饰'
  }
  return names[category] || '商品列表'
}

const addToCart = async (product) => {
  const user = JSON.parse(localStorage.getItem('user'))
  if (!user) {
    alert('请先登录')
    router.push('/login')
    return
  }

  try {
    const response = await axiosInstance.post('/cart/add', {
      productId: product.id,
      amount: 1
    })

    if (response === '添加成功') {
      alert('已添加到购物车')
    } else {
      alert(response)
    }
  } catch (error) {
    console.error('添加购物车失败:', error)
    alert('添加失败，请重试')
  }
}

const viewDetail = (product) => {
  router.push(`/product/${product.id}`)
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.products-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.container {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
  display: flex;
  gap: 30px;
}

.sidebar {
  width: 200px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  height: fit-content;
}

.sidebar h3 {
  margin-bottom: 15px;
  color: #333;
}

.sidebar ul {
  list-style: none;
  padding: 0;
}

.sidebar li {
  padding: 10px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
}

.sidebar li:hover {
  background: #f0f0f0;
}

.sidebar li.active {
  background: #52c41a;
  color: white;
}

.main-content {
  flex: 1;
}

.main-content h2 {
  margin-bottom: 20px;
  color: #333;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
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
