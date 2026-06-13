<script setup>
</script>

<template>
  <v-container  class="about-page">
    <div class="center-content">

    <h1>App Created By Dino Longo</h1>
    <p>Open for work! Feel free to contact me at longodino@gmail.com or 314-681-8702</p>
    <a href="mailto:longodino@gmail.com">
      <v-btn color="primary" variant="outlined">Email Me</v-btn>
    </a>
    <br />
    <a href="tel:314-681-8702">
      <v-btn color="primary" variant="outlined">Call Me</v-btn>
    </a>
    </div>
  </v-container>
</template>

<style scoped>
.about-page {
  background: #1a1a1a;
}
.center-content {
  min-height: 100vh;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

h1 {
  color: white;
  font-size: 32px;
  font-weight: 700;
  text-align: center;
  margin: 0 0 16px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}
h2 {
  color: white;
  font-size: 24px;
  font-weight: 600;
  text-align: center;
  margin: 0 0 16px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

p {
  color: white;
  font-size: 18px;
  text-align: center;
  margin: 0;
}
</style>
