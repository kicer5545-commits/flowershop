<template>
    <div class="favorites-page">
        <Header />
        <div class="container">
            <h2>我的收藏</h2>
            <div v-if="favorites.length === 0" class="empty">
                <p>暂无收藏商品</p>
                <router-link to="/products">去逛逛</router-link>
            </div>
            <div class="favorite-list">
                <div v-for="item in favorites" :key="item.id" class="favorite-item">
                    <img :src="item.productImage" :alt="item.productName" @click="viewDetail(item.productId)" />
                    <div class="info">
                        <h3>{{ item.productName }}</h3>
                        <p class="price">¥{{ item.productPrice }}</p>
                    </div>
                    <div class="actions">
                        <a-button type="primary" @click="addToCart(item.productId)">加入购物车</a-button>
                        <a-button danger @click="removeFavorite(item.productId)">取消收藏</a-button>
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
    import Header from './Header.vue'
    import Footer from './Footer.vue'
    import axiosInstance from '../axios'
    import { message } from 'ant-design-vue'

    const router = useRouter()
    const favorites = ref([])

    const loadFavorites = async () => {
        try {
            const data = await axiosInstance.get('/favorite/list')
            favorites.value = data
        } catch (error) {
            console.error('加载收藏列表失败', error)
            message.error('加载失败')
        }
    }

    const removeFavorite = async (productId) => {
        try {
            await axiosInstance.get('/favorite/remove', { params: { productId } })
            message.success('已取消收藏')
            await loadFavorites()
        } catch (error) {
            message.error('操作失败')
        }
    }

    const addToCart = async (productId) => {
        try {
            await axiosInstance.post('/cart/add', { productId, amount: 1 })
            message.success('已加入购物车')
        } catch (error) {
            message.error('加入购物车失败')
        }
    }

    const viewDetail = (productId) => {
        router.push(`/product/${productId}`)
    }

    onMounted(() => {
        loadFavorites()
    })
</script>

<style scoped>
    .favorites-page {
        min-height: 100vh;
        background: #f5f5f5;
    }
    .container {
        max-width: 1200px;
        margin: 30px auto;
        padding: 0 20px;
    }
    .empty {
        text-align: center;
        padding: 60px;
        background: white;
        border-radius: 8px;
    }
    .favorite-list {
        display: flex;
        flex-direction: column;
        gap: 16px;
    }
    .favorite-item {
        background: white;
        border-radius: 8px;
        padding: 16px;
        display: flex;
        align-items: center;
        gap: 20px;
    }
    .favorite-item img {
        width: 100px;
        height: 100px;
        object-fit: cover;
        border-radius: 8px;
        cursor: pointer;
    }
    .info {
        flex: 1;
    }
    .info h3 {
        margin-bottom: 8px;
    }
    .price {
        color: #ff6b6b;
        font-size: 18px;
        font-weight: bold;
    }
    .actions {
        display: flex;
        gap: 12px;
    }
</style>