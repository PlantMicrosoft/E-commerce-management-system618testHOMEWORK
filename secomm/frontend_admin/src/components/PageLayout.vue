<template>
  <div class="flex h-screen bg-gray-100 gap-6">
    <!-- Sidebar -->
    <Sidebar v-if="showSidebar" />

    <!-- Main content -->
    <main class="flex-1 flex flex-col overflow-hidden bg-white">
      <!-- Top bar -->

      <!-- Page content -->
      <div class="flex-1 overflow-auto bg-gray-50 pl-12 pr-8 py-8">
        <div class="max-w-7xl mx-auto">
          <slot></slot>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import Sidebar from './Sidebar.vue'

interface Props {
  title?: string
  showSidebar?: boolean
}

withDefaults(defineProps<Props>(), {
  title: 'Dashboard',
  showSidebar: true,
})
</script>
