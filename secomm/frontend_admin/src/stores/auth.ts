// Pinia store for managing authentication state
import { defineStore } from "pinia"
import { ref, computed } from "vue"
import type { User } from "@/types/auth"
import type { UserRole } from "@/config/roles"

export const useAuthStore = defineStore("auth", () => {
  const user = ref<User | null>(null)
  const isAuthenticated = computed(() => user.value !== null)
  const isLoading = ref(false)
  const lastActivityTime = ref<number>(Date.now())
  const sessionTimeout = 30 * 60 * 1000 // 30 minutes

  function login(name: string, email: string, role: UserRole) {
    isLoading.value = true
    try {
      user.value = {
        id: Math.random().toString(36).substr(2, 9),
        name,
        email,
        role,
        avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${name}`,
      }
      localStorage.setItem("auth_user", JSON.stringify(user.value))
      localStorage.setItem("auth_login_time", Date.now().toString())
      updateActivityTime()
    } finally {
      isLoading.value = false
    }
  }

  function logout() {
    user.value = null
    localStorage.removeItem("auth_user")
    localStorage.removeItem("auth_login_time")
    lastActivityTime.value = Date.now()
  }

  function loadUser() {
    const stored = localStorage.getItem("auth_user")
    const loginTime = localStorage.getItem("auth_login_time")

    if (stored && loginTime) {
      const sessionElapsed = Date.now() - Number.parseInt(loginTime)

      // Check if session has expired
      if (sessionElapsed > sessionTimeout) {
        logout()
        return
      }

      user.value = JSON.parse(stored)
      updateActivityTime()
    }
  }

  function updateActivityTime() {
    lastActivityTime.value = Date.now()
  }

  function isSessionValid(): boolean {
    if (!user.value) return false
    const loginTime = localStorage.getItem("auth_login_time")
    if (!loginTime) return false

    const sessionElapsed = Date.now() - Number.parseInt(loginTime)
    return sessionElapsed <= sessionTimeout
  }

  function getSessionTimeRemaining(): number {
    const loginTime = localStorage.getItem("auth_login_time")
    if (!loginTime) return 0

    const sessionElapsed = Date.now() - Number.parseInt(loginTime)
    const remaining = Math.max(0, sessionTimeout - sessionElapsed)
    return Math.round(remaining / 1000)
  }

  return {
    user,
    isAuthenticated,
    isLoading,
    lastActivityTime,
    login,
    logout,
    loadUser,
    updateActivityTime,
    isSessionValid,
    getSessionTimeRemaining,
  }
})
