<template>
  <div class="product-card">
    <div class="card-image" @click="$emit('imageClick', product)">
      <img :src="product.image" :alt="product.name" />
      <div v-if="product.stock === 0" class="sold-out">已售罄</div>
    </div>

    <div class="card-info">
      <h3 class="product-name">{{ product.name }}</h3>
      <p class="product-description">{{ product.description }}</p>

      <div class="product-price">
        <span class="current-price">¥{{ product.price }}</span>
      </div>

      <div class="product-meta">
        <span class="stock">库存: {{ product.stock }}</span>
        <span class="rating">★ {{ product.rating }}</span>
      </div>

      <div class="product-actions">
        <a-button
                type="text"
                size="small"
                @click.stop="$emit('toggleFavorite', product)"
                :style="{ color: product.favorited ? '#ff4d4f' : '#999' }"
        >
          {{ product.favorited ? '❤️ 已收藏' : '🤍 收藏' }}
        </a-button>
        <a-button
                type="primary"
                danger
                :disabled="product.stock === 0"
                @click="$emit('addToCart', product)"
        >
          加入购物车
        </a-button>
      </div>
      </div>
    </div>
</template>

<script setup>
defineProps({
  product: {
    type: Object,
    required: true
  }
})

defineEmits(['addToCart', 'imageClick','toggleFavorite'])
</script>

<style scoped>
.product-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.card-image {
  position: relative;
  height: 200px;
  overflow: hidden;
  cursor: pointer;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-card:hover .card-image img {
  transform: scale(1.05);
}

.sold-out {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
}

.card-info {
  padding: 15px;
}

.product-name {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
  height: 44px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-description {
  margin: 0 0 10px 0;
  font-size: 12px;
  color: #999;
  height: 36px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-price {
  margin-bottom: 10px;
}

.current-price {
  font-size: 20px;
  color: #ff6b6b;
  font-weight: bold;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  font-size: 12px;
  color: #666;
}

.stock {
  color: #52c41a;
}

.rating {
  color: #ffc107;
}

.product-actions {
  display: flex;
}

.product-actions button {
  width: 100%;
}
</style>