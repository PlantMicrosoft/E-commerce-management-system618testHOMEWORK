<template>
  <PageLayout title="产品管理">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-3xl font-bold text-gray-800">产品管理</h1>
      <el-button type="primary" @click="$router.push('/products/add')" :icon="Plus" size="large">
        增加新产品
      </el-button>
    </div>

    <!-- Search and Filter Section -->
    <el-card class="mb-6" shadow="never">
      <div class="flex gap-4 items-center">
        <el-input
          v-model="searchQuery"
          placeholder="输入产品查询关键词..."
          :prefix-icon="Search"
          style="width: 300px"
          clearable
        />
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-select
          v-model="selectedCategory"
          placeholder="按种类过滤。。。"
          style="width: 200px"
          clearable
        >
          <el-option label="All Categories" value="" />
          <el-option label="Electronics" value="Electronics" />
          <el-option label="Clothing" value="Clothing" />
          <el-option label="Books" value="Books" />
          <el-option label="Home" value="Home" />
        </el-select>
        <el-select
          v-model="selectedStatus"
          placeholder="按状态过滤..."
          style="width: 150px"
          clearable
        >
          <el-option label="All Status" value="" />
          <el-option label="Active" value="active" />
          <el-option label="Inactive" value="inactive" />
        </el-select>
      </div>
    </el-card>

    <!-- Products Table -->
    <el-card shadow="never">
      <el-table
        :data="products"
        style="width: 100%"
        stripe
        :header-cell-style="{ backgroundColor: '#f5f7fa' }"
        v-loading="loading"
      >
        <el-table-column prop="id" label="产品ID" width="80" />

        <el-table-column prop="name" label="产品名称" min-width="200">
          <template #default="{ row }">
            <div class="flex items-center gap-3">
              <el-avatar
                :size="40"
                shape="square"
                :src="getImageUrl(row.imageUrl)"
                fit="cover"
                @error="() => true"
              >
                <img src="https://via.placeholder.com/40" />
              </el-avatar>
              <div>
                <div class="font-medium">{{ row.name }}</div>
                <div class="text-sm text-gray-500">SKU: {{ row.sku }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="categoryName" label="分类" width="120">
          <template #default="{ row }">
            <el-tag :type="getCategoryTagType(row.categoryName) || undefined">
              {{ row.categoryName }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="unitPrice" label="价格" width="100">
          <template #default="{ row }">
            <span class="font-semibold text-green-600">${{ row.unitPrice }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="unitsInStock" label="库存" width="100">
          <template #default="{ row }">
            <el-tag :type="row.unitsInStock > 50 ? 'success' : row.unitsInStock > 10 ? 'warning' : 'danger'">
              {{ row.unitsInStock }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.active"
              @change="toggleProductStatus(row)"
              active-text="Active"
              inactive-text="Inactive"
            />
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <div class="flex gap-2">
              <el-button
                type="primary"
                :icon="Edit"
                size="small"
                title="编辑产品"
                @click="editProduct(row)"
                circle
              />
              <!-- <el-button type="info" :icon="View" size="small" @click="viewProduct(row)" circle /> -->
              <el-popconfirm
                title="确认删除吗?"
                @confirm="deleteProduct(row.id)"
                confirm-button-text="Yes"
                cancel-button-text="No"
              >
                <template #reference>
                  <el-button type="danger" :icon="Delete" size="small" circle />
                </template>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="flex justify-center mt-6">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <!-- Product Detail Dialog -->
    <el-dialog
      v-model="showDetailDialog"
      title="Product Details"
      width="600px"
      :before-close="closeDetailDialog"
    >
      <div v-if="selectedProduct" class="space-y-4">
        <div class="flex gap-4">
          <el-image
            :src="getImageUrl(selectedProduct.imageUrl)"
            style="width: 150px; height: 150px"
            fit="cover"
            class="rounded-lg"
          />
          <div class="flex-1">
            <h3 class="text-xl font-bold mb-2">{{ selectedProduct.name }}</h3>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="SKU">{{ selectedProduct.sku }}</el-descriptions-item>
              <el-descriptions-item label="Category">
                <el-tag :type="getCategoryTagType(selectedProduct.categoryName)">
                  {{ selectedProduct.categoryName }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="Price">
                <span class="text-green-600 font-semibold">${{ selectedProduct.unitPrice }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="Stock">{{ selectedProduct.unitsInStock }}</el-descriptions-item>
              <el-descriptions-item label="Status">
                <el-tag :type="selectedProduct.active ? 'success' : 'danger'">
                  {{ selectedProduct.active ? 'Active' : 'Inactive' }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </div>
        <div v-if="selectedProduct.description">
          <h4 class="font-semibold mb-2">Description</h4>
          <p class="text-gray-600">{{ selectedProduct.description }}</p>
        </div>
      </div>

      <template #footer>
        <el-button @click="closeDetailDialog">Close</el-button>
        <el-button type="primary" @click="editProduct(selectedProduct)"> Edit Product </el-button>
      </template>
    </el-dialog>
  </PageLayout>
</template>

<script setup>
import PageLayout from '@/components/PageLayout.vue'
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, Edit, Delete, Search, View } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getProducts, searchProducts, deleteProduct as deleteProductAPI } from '../services/productService.js'

const router = useRouter()

const products = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取产品列表
const fetchProducts = async (page = 1, size = pageSize.value) => {
  loading.value = true
  try {
    const keyword = searchQuery.value?.trim()
    let response
    if (keyword) {
      response = await searchProducts(keyword, page - 1, size)
    } else {
      const params = { page: page - 1, size, sortBy: 'id', sortDir: 'desc' }
      response = await getProducts(params)
    }
    const data = response.data
    
    products.value = data.content || []
    total.value = data.totalElements || 0
    currentPage.value = page
    
    console.log('获取产品列表:', products.value)
  } catch (error) {
    console.error('获取产品失败:', error)
    ElMessage.error('获取产品失败')
  } finally {
    loading.value = false
  }
}

const searchQuery = ref('')
const selectedCategory = ref('')
const selectedStatus = ref('')

// 移除客户端过滤，改为服务端过滤
const handleSearch = () => {
  currentPage.value = 1
  fetchProducts(1, pageSize.value)
}

// 分页处理函数
const handlePageChange = (page) => {
  fetchProducts(page, pageSize.value)
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchProducts(1, size)
}

const showDetailDialog = ref(false)
const selectedProduct = ref(null)

const filteredProducts = computed(() => {
  let filtered = products.value

  // Filter by search query
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(
      (product) =>
        product.name.toLowerCase().includes(query) ||
        product.sku.toLowerCase().includes(query) ||
        product.categoryName.toLowerCase().includes(query)
    )
  }

  // Filter by category
  if (selectedCategory.value) {
    filtered = filtered.filter((product) => product.categoryName === selectedCategory.value)
  }

  // Filter by status
  if (selectedStatus.value) {
    const isActive = selectedStatus.value === 'active'
    filtered = filtered.filter((product) => product.active === isActive)
  }

  return filtered
})

// 处理图片URL
const getImageUrl = (imageUrl) => {
  if (!imageUrl) return 'https://via.placeholder.com/40'

  if (imageUrl.startsWith('http')) return imageUrl

  const normalized = imageUrl.startsWith('/') ? imageUrl : `/${imageUrl}`
  return `http://localhost:8080${normalized}`
}

const getCategoryTagType = (categoryName) => {
  const types = {
    'Electronics': 'primary',
    'Clothing': 'success', 
    'Books': 'warning',
    'Home': 'info',
    '电子产品': 'primary',
    '服装': 'success',
    '图书': 'warning',
    '家居': 'info'
  }
  return types[categoryName] || ''
}

const editProduct = (product) => {
  // 跳转到编辑页面
  router.push(`/products/edit/${product.id}`)
}

const viewProduct = (product) => {
  selectedProduct.value = product
  showDetailDialog.value = true
}

const deleteProduct = async (id) => {
  try {
    await deleteProductAPI(id)
    // 删除成功后重新获取当前页数据
    await fetchProducts(currentPage.value, pageSize.value)
    ElMessage.success('产品删除成功!')
  } catch (error) {
    console.error('删除产品失败:', error)
    ElMessage.error('删除产品失败')
  }
}

const toggleProductStatus = (product) => {
  const status = product.active ? 'activated' : 'deactivated'
  ElMessage.success(`Product ${status} successfully!`)
}

const closeDetailDialog = () => {
  showDetailDialog.value = false
  selectedProduct.value = null
}

// 页面加载时获取产品列表
onMounted(() => {
  fetchProducts(1, pageSize.value)
})
</script>
