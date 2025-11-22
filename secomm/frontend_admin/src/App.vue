<template>
  <!-- Use simple div for blank layout pages (like login) -->
  <div v-if="isBlankLayout" class="bg-slate-900 min-h-screen">
    <RouterView />
  </div>
  <!-- For authenticated pages, let PageLayout handle everything -->
  <RouterView v-else />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const isBlankLayout = computed(() => {
  return route.meta.layout === 'blank'
})
</script>
