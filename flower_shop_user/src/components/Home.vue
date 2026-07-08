Home.vue
<template>
  <div class="home">
    <Header />

    <!-- 搜索栏区域 -->
    <div class="search-section">
      <div class="search-container">
        <div class="search-box">
          <i class="search-icon">🔍</i>
          <input
              type="text"
              v-model="searchKeyword"
              placeholder="搜索鲜花、花束、绿植..."
              @keyup.enter="handleSearch"
              class="search-input"
          />
          <button @click="handleSearch" class="search-btn">搜索</button>
        </div>
        <!-- 热门搜索推荐 -->
        <div class="hot-search" v-if="hotSearches.length">
          <span class="hot-label">热门：</span>
          <span
              v-for="keyword in hotSearches"
              :key="keyword"
              class="hot-keyword"
              @click="searchKeyword = keyword; handleSearch()"
          >
            {{ keyword }}
          </span>
        </div>
      </div>
    </div>

    <div class="banner">
      <div class="carousel">
        <img
            v-for="(img, i) in imgs"
            :key="i"
            :src="img"
            alt="Banner"
            :class="{ active: curr === i }"
        />
      </div>
    </div>

    <div class="categories">
      <h2>鲜花分类</h2>
      <div class="category-grid">
        <div v-for="cat in categories" :key="cat.key" class="category-item" @click="goToCategory(cat.key)">
          <img :src="cat.image" :alt="cat.name" />
          <p>{{ cat.name }}</p>
        </div>
      </div>
    </div>

    <div class="featured-products">
      <h2>精选商品</h2>
      <div class="product-grid">
        <ProductCard
            v-for="product in featuredProducts"
            :key="product.id"
            :product="product"
            @addToCart="addToCart"
            @imageClick="viewDetail"
        />
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Header from './Header.vue'
import Footer from './Footer.vue'
import ProductCard from './ProductCard.vue'
import axiosInstance from '../axios'

const router = useRouter()

// 搜索相关
const searchKeyword = ref('')
// 热门搜索关键词
const hotSearches = ref(['玫瑰', '百合', '生日花束', '情人节', '绿植盆栽', '婚礼花艺'])

const categories = ref([
  { key: 'rose', name: '玫瑰系列', image: '/images/roses/卡罗拉.jpg' },
  { key: 'lily', name: '百合系列', image: '/images/lilys/香水百合.jpg' },
  { key: 'birthday', name: '生日花束', image: '/images/birthday/初心不负.jpg' },
  { key: 'valentine', name: '情人节特供', image: '/images/valentine/冰雪之恋.jpg' },
  { key: 'wedding', name: '婚礼花艺', image: '/images/wedding/白玫瑰配蝴蝶兰瀑布手捧花.jpg' },
  { key: 'indoor', name: '绿植盆栽', image: '/images/indoor/龟背竹.jpg' }
])

const featuredProducts = ref([])

const loadProducts = async () => {
  try {
    const products = await axiosInstance.get('/products')
    featuredProducts.value = products.slice(0, 8)
  } catch (error) {
    console.error('加载商品失败:', error)
  }
}

const goToCategory = (category) => {
  router.push({ path: '/products', query: { category } })
}

// ==============================================
// 【精准搜索：所有系列都已配对】
// ==============================================
const handleSearch = () => {
  if (!searchKeyword.value.trim()) {
    alert('请输入搜索关键词')
    return
  }

  const kw = searchKeyword.value.trim().toLowerCase()

  // 完整映射表，和你商品列表页的系列一一对应
  const map = {
    '玫瑰': 'rose',
    '玫瑰系列': 'rose',
    'rose': 'rose',

    '百合': 'lily',
    '百合系列': 'lily',
    'lily': 'lily',

    '月季': 'ChineseRose',
    '月季系列': 'ChineseRose',

    '菊花': 'chrysanthemum',
    '菊花系列': 'chrysanthemum',

    '康乃馨': 'carnation',

    '兰花': 'orchid',
    '兰花系列': 'orchid',

    '生日花束': 'birthday',

    '情人节': 'valentine',

    '婚礼花艺': 'wedding',

    '绿植盆栽': 'indoor'
  }

  // 匹配到就跳对应系列，没匹配到就跳全部商品
  const targetCategory = map[kw] || 'all'

  router.push({
    path: '/products',
    query: {
      category: targetCategory
    }
  })
}

const addToCart = async (product) => {
  const user = JSON.parse(localStorage.getItem('user'))
  if (!user) {
    router.push('/login')
    return
  }

  try {
    const cartData = {
      productId: product.id,
      amount: 1,
      userId: user.id,
      date: new Date().toISOString()
    }
    await axiosInstance.post('/cart/add', cartData)
    alert('已添加到购物车')
  } catch (error) {
    console.error('添加购物车失败:', error)
    alert('添加失败')
  }
}

const viewDetail = (product) => {
  router.push(`/product/${product.id}`)
}

onMounted(() => {
  loadProducts()
})

// 轮播逻辑
const imgs = ref([
  '/images/home/轮播图1.jpg',
  '/images/home/轮播图2.jpg',
  '/images/home/轮播图3.jpg'
])
const curr = ref(0)

onMounted(() => {
  setInterval(() => {
    curr.value = (curr.value + 1) % imgs.value.length
  }, 3000)
})
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 搜索栏样式 */
.search-section {
  max-width: 1200px;
  margin: 80px auto 20px;
  padding: 0 20px;
}

.search-container {
  background: white;
  border-radius: 16px;
  padding: 20px 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.search-box {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #f8f8f8;
  border-radius: 50px;
  padding: 4px 4px 4px 20px;
  transition: all 0.3s ease;
  border: 1px solid #e8e8e8;
}

.search-box:focus-within {
  background: white;
  border-color: #ff6b6b;
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.1);
}

.search-icon {
  font-size: 18px;
  color: #999;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 16px;
  padding: 14px 0;
  color: #333;
}

.search-input::placeholder {
  color: #bbb;
}

.search-btn {
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 50px;
  padding: 10px 28px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-btn:hover {
  background: #ff5252;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

.hot-search {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 12px;
  font-size: 13px;
}

.hot-label {
  color: #999;
  font-size: 13px;
}

.hot-keyword {
  color: #666;
  cursor: pointer;
  transition: color 0.2s;
  padding: 4px 0;
}

.hot-keyword:hover {
  color: #ff6b6b;
}

/* 轮播图样式 */
.banner {
  max-width: 1200px;
  margin: 0 auto 32px;
  padding: 0 20px;
}

.carousel {
  position: relative;
  width: 100%;
  min-height: 300px;
  border-radius: 12px;
  overflow: hidden;
  background: #f8f2eb;
  display: flex;
  align-items: center;
  justify-content: center;
}

.carousel img {
  position: relative;
  width: 100%;
  height: auto;
  display: none;
  opacity: 0;
  transition: opacity 0.6s ease;
  border-radius: 12px;
}

.carousel img.active {
  display: block;
  opacity: 1;
}

/* 鲜花分类 */
.categories {
  max-width: 1200px;
  margin: 0 auto 48px;
  padding: 0 20px;
}
.categories h2 {
  text-align: center;
  margin-bottom: 28px;
  color: #333;
  font-size: 24px;
}
.category-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}
.category-item {
  background: white;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: 0.3s;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.category-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
}
.category-item img {
  width: 100%;
  height: 180px;
  object-fit: cover;
}
.category-item p {
  padding: 12px;
  margin: 0;
  color: #333;
  font-size: 15px;
}

/* 精选商品 */
.featured-products {
  max-width: 1200px;
  margin: 0 auto 60px;
  padding: 0 20px;
}
.featured-products h2 {
  text-align: center;
  margin-bottom: 28px;
  color: #333;
  font-size: 24px;
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 22px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .search-box {
    padding: 4px 4px 4px 16px;
  }
  .search-input {
    font-size: 14px;
    padding: 10px 0;
  }
  .search-btn {
    padding: 8px 20px;
    font-size: 13px;
  }
  .hot-search {
    font-size: 12px;
    gap: 8px;
  }
  .category-grid {
    gap: 16px;
  }
  .product-grid {
    gap: 16px;
  }
}
</style>