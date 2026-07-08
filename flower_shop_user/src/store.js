import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const isLoggedIn = ref(false)

  function setUser(userData) {
    user.value = userData
    isLoggedIn.value = true
    localStorage.setItem('user', JSON.stringify(userData))
    localStorage.setItem('isLoggedIn', 'true')
  }

  function clearUser() {
    user.value = null
    isLoggedIn.value = false
    localStorage.removeItem('user')
    localStorage.removeItem('isLoggedIn')
  }

  function initFromStorage() {
    const storedUser = localStorage.getItem('user')
    const loggedIn = localStorage.getItem('isLoggedIn')
    if (storedUser && loggedIn === 'true') {
      user.value = JSON.parse(storedUser)
      isLoggedIn.value = true
    }
  }

  return { user, isLoggedIn, setUser, clearUser, initFromStorage }
})

export const useCartStore = defineStore('cart', () => {
  const cartItems = ref([])

  function addToCart(item) {
    cartItems.value.push(item)
  }

  function removeFromCart(index) {
    cartItems.value.splice(index, 1)
  }

  function clearCart() {
    cartItems.value = []
  }

  return { cartItems, addToCart, removeFromCart, clearCart }
})