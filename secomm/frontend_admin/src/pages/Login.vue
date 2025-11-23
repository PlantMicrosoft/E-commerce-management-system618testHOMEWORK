<template>

  <div
    class="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 flex items-center justify-center p-4"
  >
    <!-- Background decoration -->
    <div class="absolute inset-0 overflow-hidden">
      <div class="absolute top-20 left-10 w-72 h-72 bg-blue-500/10 rounded-full blur-3xl"></div>
      <div
        class="absolute bottom-20 right-10 w-72 h-72 bg-purple-500/10 rounded-full blur-3xl"
      ></div>
    </div>

    <!-- Login container -->
    <div class="relative w-full max-w-md">
      <!-- Logo and title -->
      <div class="text-center mb-8">
        <h1 class="text-4xl font-bold text-white mb-2">Secomm后台管理系统</h1>
        <p class="text-slate-400">教学用小电商后台</p>
      </div>

      <!-- Login card -->
      <div
        class="bg-slate-800/50 backdrop-blur-xl border border-slate-700 rounded-2xl p-8 shadow-2xl"
      >
        <div class="mb-8">
          <h2 class="text-2xl font-bold text-white mb-2">登 录</h2>
          <p class="text-slate-400">请选择登录的角色</p>
        </div>

        <form @submit.prevent="handleLogin" class="space-y-6">
          <div>
            <label class="block text-sm font-medium text-slate-300 mb-2">姓名</label>
            <input
              v-model="formData.name"
              type="text"
              placeholder="John Doe"
              class="w-full px-4 py-2 bg-slate-700/50 border border-slate-600 rounded-lg text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
              required
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-300 mb-2">邮箱地址</label>
            <input
              v-model="formData.email"
              type="email"
              placeholder="john@example.com"
              class="w-full px-4 py-2 bg-slate-700/50 border border-slate-600 rounded-lg text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
              required
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-300 mb-2">角色</label>
            <div class="space-y-3 bg-slate-700/30 border border-slate-600 rounded-lg p-4">
              <label v-for="role in roles" :key="role.name" class="flex items-start gap-3 cursor-pointer">
                <input type="radio" class="mt-1" :value="role.name" v-model="formData.role" />
                <div>
                  <p class="text-slate-200 font-medium">{{ role.displayName }}</p>
                  <p class="text-slate-400 text-xs">{{ role.description }}</p>
                </div>
              </label>
            </div>
          </div>

          <button
            type="submit"
            :disabled="loading"
            class="w-full py-2.5 bg-gradient-to-r from-blue-600 to-blue-500 text-white font-semibold rounded-lg hover:from-blue-700 hover:to-blue-600 transition duration-200 shadow-lg hover:shadow-blue-500/50 disabled:opacity-50"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </button>
        </form>

        <!-- Demo info -->
        <div class="mt-6 p-3 bg-slate-700/30 border border-slate-600 rounded-lg">
          <p class="text-xs text-slate-400">
            <span class="font-semibold text-slate-300">Demo Mode:</span> Enter any name and email,
            select a role, and click Sign In to explore the admin panel.
          </p>
        </div>
      </div>

      <!-- Footer -->
      <p class="text-center text-slate-500 text-sm mt-6">架构 Spring Framework + Vue 3</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { roleConfigurations } from '@/config/roles'
import type { UserRole } from '@/config/roles'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

const roles = Object.values(roleConfigurations)

const formData = ref<{ name: string; email: string; role: UserRole | '' }>({
  name: '',
  email: '',
  role: ''
})

const loading = ref(false)

async function handleLogin() {
  if (!formData.value.name || !formData.value.email || !formData.value.role) {
    ElMessage.error('请输入姓名、邮箱并选择角色')
    return
  }

  loading.value = true
  try {
    authStore.login(formData.value.name, formData.value.email, formData.value.role as UserRole)
    ElMessage.success('登录成功')
    router.push('/manager-dashboard')
  } finally {
    loading.value = false
  }
}
</script>
