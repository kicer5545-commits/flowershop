import { createRouter, createWebHistory } from 'vue-router'
import Login from './components/Login.vue'
import Home from './components/Home.vue'
import ProductManage from './components/ProductManage.vue'
import OrderManage from './components/OrderManage.vue'
import UserManage from './components/UserManage.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', name: 'Login', component: Login },
    {
      path: '/home',
      name: 'Home',
      component: Home,
      meta: { requiresAuth: true, requiresAdmin: true },
      redirect: '/products',
      children: [
        { path: '/products', name: 'ProductManage', component: ProductManage, meta: { requiresAuth: true } },
        { path: '/orders', name: 'OrderManage', component: OrderManage, meta: { requiresAuth: true } },
        { path: '/users', name: 'UserManage', component: UserManage, meta: { requiresAuth: true } },
        { path: '/dashboard', name: 'Dashboard', component: () => import('./components/Dashboard.vue'), meta: { requiresAuth: true } }
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth) {
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
    if (!isLoggedIn) {
      next('/login')
      return
    }

    if (to.meta.requiresAdmin) {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      if (user.role !== 1) {
        alert('您没有权限访问')
        next('/login')
        return
      }
    }
  }
  next()
})

export default router
