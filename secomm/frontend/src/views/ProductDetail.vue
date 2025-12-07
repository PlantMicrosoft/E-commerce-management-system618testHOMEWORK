<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
    <AppHeader :showSearch="false" />

    <div class="flex flex-1 relative">
      <AppSidebar
        :categories="categories"
        :selectedCategoryId="null"
        @selectCategory="handleSelectCategory"
        @showAllProducts="handleShowAllProducts"
      />

      <main class="flex-1 p-8">
        <div v-if="loading" class="text-center py-8">
          <p>加载中...</p>
        </div>

        <div v-else-if="product" class="max-w-4xl">
          <div class="bg-white border border-gray-200 rounded p-6 mb-6">
            <div class="flex gap-8">
              <div class="w-64 h-64 bg-gray-100 flex-shrink-0 flex items-center justify-center overflow-hidden">
                <img
                  v-if="product.imageUrl"
                  :src="getImageUrl(product.imageUrl)"
                  :alt="product.name"
                  class="w-full h-full object-cover"
                  @error="handleImageError"
                />
                <svg
                  v-else
                  class="w-32 h-32 text-gray-300"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
                  />
                </svg>
              </div>
              <div class="flex-1">
                <h2 class="text-2xl font-bold mb-2">{{ product.name }}</h2>
                <p class="text-gray-600 mb-4">{{ product.description }}</p>
                <p class="text-3xl font-bold mb-6">￥{{ product.unitPrice }}</p>
                <div class="mb-4">
                  <p class="text-sm text-gray-500">SKU: {{ product.sku }}</p>
                  <p class="text-sm text-gray-500">库存: {{ product.unitsInStock }}</p>
                </div>
                <button
                  @click="addToCart"
                  :disabled="product.unitsInStock === 0"
                  :class="[
                    'px-8 py-3 rounded font-semibold transition-colors',
                    product.unitsInStock > 0
                      ? 'border border-gray-900 hover:bg-gray-100'
                      : 'bg-gray-300 text-gray-500 cursor-not-allowed'
                  ]"
                >
                  {{ product.unitsInStock > 0 ? '加入购物车' : '缺货' }}
                </button>
              </div>
            </div>
          </div>

          <div class="bg-white border border-gray-200 rounded p-6">
            <h3 class="text-xl font-semibold mb-4">产品描述</h3>
            <div class="space-y-4 text-gray-700 leading-relaxed">
              <p>{{ product.description }}</p>
            </div>
          </div>
        </div>

        <div v-else class="text-center py-8">
          <p class="text-red-600">产品未找到</p>
          <router-link to="/" class="text-blue-600 hover:underline mt-4 inline-block">
            返回首页
          </router-link>
        </div>
      </main>
    </div>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCart } from '../composables/useCart.js'
import AppHeader from '../components/AppHeader.vue'
import AppSidebar from '../components/AppSidebar.vue'
import AppFooter from '../components/AppFooter.vue'

const router = useRouter()
const route = useRoute()
const { addToCart: addToCartFunction } = useCart()
const categories = ref([])
const product = ref(null)
const loading = ref(false)

// 获取分类数据
const fetchCategories = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/categories')
    const data = await response.json()
    if (data.status === 200) {
      categories.value = data.data
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取产品详情
const fetchProduct = async (productId) => {
  loading.value = true
  try {
    const response = await fetch(`http://localhost:8080/api/products/${productId}`)
    const data = await response.json()
    console.log('产品详情响应:', data)
    if (data.status === 200) {
      product.value = data.data
    } else {
      console.error('获取产品详情失败:', data.message)
      product.value = null
    }
  } catch (error) {
    console.error('获取产品详情失败:', error)
    product.value = null
  } finally {
    loading.value = false
  }
}

// 添加到购物车
const addToCart = async () => {
  console.log('Adding to cart:', product.value.id) // Debug log
  const success = await addToCartFunction(product.value.id, 1)
  if (success) {
    alert('商品已添加到购物车')
  }
}

// 处理分类选择
const handleSelectCategory = (categoryId) => {
  router.push({ name: 'home', query: { category: categoryId } })
}

// 处理显示所有产品
const handleShowAllProducts = () => {
  router.push({ name: 'home' })
}

// 处理图片加载错误
const handleImageError = (event) => {
  console.log('图片加载失败:', event.target.src)
  event.target.style.display = 'none'
}
const getImageUrl = (imageUrl) => {
  if (!imageUrl) return ''
  if (imageUrl.startsWith('http')) return imageUrl
  const processed = imageUrl.startsWith('/uploads') ? imageUrl : `/uploads${imageUrl}`
  return `http://localhost:8080${processed}`
}
// 处理图片URL，统一走后端静态资源↑
onMounted(() => {
  fetchCategories()
  const productId = route.params.id
  if (productId) {
    fetchProduct(productId)
  }
})
</script>
