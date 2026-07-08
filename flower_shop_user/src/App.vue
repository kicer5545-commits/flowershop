<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
// test
import { onMounted } from 'vue'
import { useUserStore } from './store'

const userStore = useUserStore()

onMounted(() => {
  userStore.initFromStorage()
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Microsoft YaHei', Arial, sans-serif;
}

#app {
  min-height: 100vh;
}
</style>