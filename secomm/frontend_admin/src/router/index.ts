import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { canAccessPage } from '@/config/roles'
import type { RouteRecordRaw } from 'vue-router'

// Lazy load pages
import Login from '../pages/Login.vue'
// import Dashboard from '../pages/Dashboard.vue'
import ProductManagement from '../pages/ProductManagement.vue'
import OrderManagement from '../pages/OrderManagement.vue'
import UserManagement from '../pages/UserManagement.vue'
import ReviewManagement from '../pages/ReviewManagement.vue'
import ManagerDashboard from '../pages/ManagerDashboard.vue'
import AddProduct from '../pages/AddProduct.vue'
import EditProduct from '../pages/EditProduct.vue'
import UserDetail from '../pages/UserDetail.vue'

declare module 'vue-router' {
  interface RouteMeta {
    requiresAuth?: boolean
    layout?: string
    title?: string
    description?: string
    breadcrumb?: string
  }
}

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    component: Login,
    meta: {
      layout: 'blank',
      title: 'Sign In',
      breadcrumb: 'Login',
    },
  },
  {
    path: '/manager-dashboard',
    component: ManagerDashboard,
    meta: {
      requiresAuth: true,
      title: 'Manager Dashboard',
      description: 'Comprehensive dashboard with analytics and recent activity',
      breadcrumb: 'Dashboard',
    },
  },
  // Put /products/add and /products/edit BEFORE /products
  {
    path: '/products/add',
    component: AddProduct,
    meta: {
      requiresAuth: false,
      title: 'Add Product',
      description: 'Add a new product',
      breadcrumb: 'Add Product',
    },
  },
  {
    path: '/products/edit/:id',
    component: EditProduct,
    meta: {
      requiresAuth: false,
      title: 'Edit Product',
      description: 'Edit an existing product',
      breadcrumb: 'Edit Product',
    },
  },
  {
    path: '/products',
    component: ProductManagement,
    meta: {
      requiresAuth: true,
      title: 'Product Management',
      description: 'Manage all products',
      breadcrumb: 'Products',
    },
  },
  {
    path: '/orders',
    component: OrderManagement,
    meta: {
      requiresAuth: true,
      title: 'Order Management',
      description: 'Manage customer orders',
      breadcrumb: 'Orders',
    },
  },
  {
    path: '/users/:id',
    component: UserDetail,
    meta: {
      requiresAuth: false,
      title: 'User Details',
      description: 'View and edit user details',
      breadcrumb: 'User Details',
    },
  },
  {
    path: '/users',
    component: UserManagement,
    meta: {
      requiresAuth: true,
      title: 'User Management',
      description: 'Manage system users',
      breadcrumb: 'Users',
    },
  },
  {
    path: '/reviews',
    component: ReviewManagement,
    meta: {
      requiresAuth: true,
      title: 'Review Management',
      description: 'Manage product reviews',
      breadcrumb: 'Reviews',
    },
  },
  {
    path: '/',
    redirect: '/manager-dashboard',
  },
  {
    path: '/dashboard',
    redirect: '/manager-dashboard',
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/manager-dashboard',
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  // Load user from localStorage on first navigation
  if (!authStore.isAuthenticated) {
    authStore.loadUser()
  }

  // Check if route requires authentication
  if (to.meta.requiresAuth) {
    if (!authStore.isAuthenticated) {
      // User not authenticated, redirect to login
      next({
        path: '/login',
        query: { redirect: to.fullPath },
      })
      return
    }

    if (!authStore.user) {
      next('/login')
      return
    }

    if (!canAccessPage(authStore.user.role, to.path)) {
      // User doesn't have permission for this page
      console.warn(`Access denied: User role '${authStore.user.role}' cannot access '${to.path}'`)
      next('/manager-dashboard')
      return
    }
  }

  // Redirect authenticated users away from login
  if (to.path === '/login' && authStore.isAuthenticated) {
    // Check if there's a redirect query parameter
    const redirect = to.query.redirect as string
    if (redirect && redirect.startsWith('/')) {
      next(redirect)
    } else {
      next('/manager-dashboard')
    }
    return
  }

  const title = to.meta.title || 'EShop Admin'
  document.title = title ? `${title} | EShop Admin` : 'EShop Admin'

  next()
})

router.afterEach((to) => {
  // Update document title
  const title = to.meta.title || 'EShop Admin'
  document.title = title ? `${title} | EShop Admin` : 'EShop Admin'
})

export default router
