<script setup>
import Header from './components/Header.vue'
</script>

<template>
  <div id="app">
    <Header />
    <router-view />
  </div>
</template>

<style>
#app {
  min-height: 100vh;
}
</style>
