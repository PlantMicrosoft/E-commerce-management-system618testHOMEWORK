<template>
  <aside class="w-64 bg-slate-800 flex flex-col overflow-hidden shadow-lg flex-shrink-0">
    <!-- Logo -->
    <div class="px-6 py-4 border-b border-slate-700">
      <h1 class="text-xl font-bold text-white flex items-center gap-2">
        <span
          class="w-8 h-8 bg-gradient-to-br from-blue-600 to-blue-500 rounded-lg flex items-center justify-center"
          >⚙️</span
        >
        Secomm系统后台管理
      </h1>
      <p class="text-xs text-slate-400 mt-1">管理台</p>
    </div>

    <!-- Navigation menu -->
    <nav class="flex-1 overflow-y-auto px-3 py-4">
      <div v-for="item in navigationItems" :key="item.path" class="mb-2">
        <RouterLink
          :to="item.path"
          class="flex items-center gap-3 px-4 py-2.5 rounded-lg text-slate-300 hover:bg-slate-700/50 hover:text-white transition-colors"
          :class="{
            'bg-blue-600/20 text-blue-400 border-l-2 border-blue-500': isActive(item.path),
          }"
        >
          <span class="text-lg">{{ item.icon }}</span>
          <span class="font-medium">{{ item.name }}</span>
        </RouterLink>
      </div>
    </nav>

    <!-- User section -->
    <div class="px-3 py-4 border-t border-slate-700">
      <div class="flex items-center gap-3 px-4 py-3 rounded-lg bg-slate-700/30">
        <img
          :src="authStore.user?.avatar"
          :alt="authStore.user?.name"
          class="w-10 h-10 rounded-full"
        />
        <div class="flex-1 min-w-0">
          <p class="text-sm font-medium text-white truncate">{{ authStore.user?.name }}</p>
          <p class="text-xs text-slate-400 truncate">
            {{ getRoleDisplay(authStore.user?.role) }}
          </p>
        </div>
      </div>
      <button
        @click="handleLogout"
        class="w-full mt-3 px-4 py-2 bg-slate-700/50 hover:bg-red-600/20 text-slate-300 hover:text-red-400 rounded-lg font-medium transition-colors"
      >
        Logout
      </button>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getRoleConfig } from '@/config/roles'
import type { MenuItem } from '@/config/roles'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const navigationItems = computed<MenuItem[]>(() => {
  const roleConfig = getRoleConfig(authStore.user?.role || '')
  return roleConfig.menuItems
})

function isActive(path: string): boolean {
  return route.path === path
}

function getRoleDisplay(role: string | undefined): string {
  // Add your role display logic here
  return role || 'User'
}

function handleLogout(): void {
  authStore.logout()
  router.push('/login')
}
</script>
