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
          <!-- Username input -->
          <div>
            <label class="block text-sm font-medium text-slate-300 mb-2">用户名</label>
            <input
              v-model="formData.username"
              type="email"
              placeholder="请输入邮箱地址"
              class="w-full px-4 py-2 bg-slate-700/50 border border-slate-600 rounded-lg text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
              required
            />
          </div>

          <!-- Password input -->
          <div>
            <label class="block text-sm font-medium text-slate-300 mb-2">密码</label>
            <input
              v-model="formData.password"
              type="password"
              placeholder="请输入密码"
              class="w-full px-4 py-2 bg-slate-700/50 border border-slate-600 rounded-lg text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
              required
            />
          </div>

          <!-- Submit button -->
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

const formData = ref({
  username: '',
  password: ''
})

const loading = ref(false)

async function handleLogin() {
  if (!formData.value.username || !formData.value.password) {
    ElMessage.error('请输入用户名和密码')
    return
  }

  loading.value = true
  
  try {
    const response = await fetch('http://localhost:8080/api/auth/signin', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: formData.value.username,
        password: formData.value.password
      })
    })

    const data = await response.json()

    if (data.status === 200) {
      // 保存JWT token和用户信息
      localStorage.setItem('token', data.data.token)
      localStorage.setItem('user', JSON.stringify(data.data))
      
      // 更新store状态
      authStore.login(
        data.data.realName || data.data.username,
        data.data.username,
        'admin' // 根据实际角色设置
      )
      
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } else {
      ElMessage.error(data.message || '登录失败')
    }
  } catch (error) {
    console.error('登录错误:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>
