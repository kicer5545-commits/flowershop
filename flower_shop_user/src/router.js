import { createRouter, createWebHistory } from 'vue-router'
import Login from './components/Login.vue'
import Register from './components/Register.vue'
import Home from './components/Home.vue'
import Products from './components/Products.vue'
import ProductDetail from './components/ProductDetail.vue'
import Cart from './components/Cart.vue'
import Order from './components/Order.vue'
import Personal from './components/Personal.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/home' },
    { path: '/login', name: 'Login', component: Login },
    { path: '/register', name: 'Register', component: Register },
    { path: '/home', name: 'Home', component: Home, meta: { requiresAuth: true } },
    { path: '/products', name: 'Products', component: Products, meta: { requiresAuth: true } },
    { path: '/product/:id', name: 'ProductDetail', component: ProductDetail, meta: { requiresAuth: true } },
    { path: '/cart', name: 'Cart', component: Cart, meta: { requiresAuth: true } },
    { path: '/order', name: 'Order', component: Order, meta: { requiresAuth: true } },
    { path: '/personal', name: 'Personal', component: Personal, meta: { requiresAuth: true } },
    { path: '/forgot', name: 'ForgotPassword', component: () => import('./components/ForgotPassword.vue') },
    { path: '/favorites', name: 'Favorites', component: () => import('./components/Favorites.vue'), meta: { requiresAuth: true } }
  ]
})

router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth) {
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
    if (!isLoggedIn) {
      next('/login')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router