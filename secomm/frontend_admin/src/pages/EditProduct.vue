<template>
  <PageLayout title="产品编辑页面">
    <div class="mb-6">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>
          <router-link to="/products" class="hover:text-blue-600">Products</router-link>
        </el-breadcrumb-item>
        <el-breadcrumb-item>Edit Product</el-breadcrumb-item>
      </el-breadcrumb>
      <h1 class="text-3xl font-bold text-gray-800 mt-4">产品编辑页面</h1>
    </div>

    <el-form
      ref="productFormRef"
      :model="product"
      :rules="rules"
      @submit.prevent="saveProduct"
      class="max-w-6xl"
      label-width="140px"
      label-position="left"
    >
      <el-row :gutter="24">
        <!-- Left Column - Product Details -->
        <el-col :lg="16" :md="24">
          <!-- Basic Information -->
          <el-card class="mb-6" shadow="never">
            <template #header>
              <div class="flex items-center gap-2">
                <el-icon><InfoFilled /></el-icon>
                <span class="text-lg font-semibold">基本信息</span>
              </div>
            </template>

            <el-row :gutter="16">
              <el-col :md="12" :sm="24">
                <el-form-item label="产品名称" prop="name" required>
                  <el-input v-model="product.name" placeholder="输入产品名称" clearable />
                </el-form-item>
              </el-col>

              <el-col :md="12" :sm="24">
                <el-form-item label="SKU" prop="sku" required>
                  <el-input v-model="product.sku" placeholder="输入 SKU" clearable />
                </el-form-item>
              </el-col>

              <el-col :md="12" :sm="24">
                <el-form-item label="单价" prop="unit_price" required>
                  <el-input-number
                    v-model="product.unit_price"
                    :min="0"
                    :precision="2"
                    :step="0.01"
                    placeholder="0.00"
                    style="width: 100%"
                  >
                    <template #prefix>$</template>
                  </el-input-number>
                </el-form-item>
              </el-col>

              <el-col :md="12" :sm="24">
                <el-form-item label="库存" prop="units_in_stock" required>
                  <el-input-number
                    v-model="product.units_in_stock"
                    :min="0"
                    placeholder="0"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>

              <el-col :span="24">
                <el-form-item label="分类" prop="category_id" required>
                  <el-select
                    v-model="product.category_id"
                    placeholder="选择产品分类"
                    style="width: 100%"
                    clearable
                  >
                    <el-option
                      v-for="category in categories"
                      :key="category.id"
                      :label="category.categoryName"
                      :value="category.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>

              <el-col :span="24">
                <el-form-item label="描述" prop="description">
                  <el-input
                    v-model="product.description"
                    type="textarea"
                    :rows="4"
                    placeholder="Enter product description..."
                    show-word-limit
                    maxlength="500"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>

          <!-- Status -->
          <el-card shadow="never">
            <template #header>
              <div class="flex items-center gap-2">
                <el-icon><Setting /></el-icon>
                <span class="text-lg font-semibold">Status</span>
              </div>
            </template>

            <el-form-item label="产品状态">
              <el-switch
                v-model="product.active"
                active-text="活动"
                inactive-text="非活动"
                active-color="#13ce66"
                inactive-color="#ff4949"
              />
              <div class="text-sm text-gray-500 mt-1">
                活动产品对消费者可见
              </div>
            </el-form-item>
          </el-card>
        </el-col>

        <!-- Right Column - Product Image -->
        <el-col :lg="8" :md="24">
          <el-card shadow="never" class="mt-6 lg:mt-0">
            <template #header>
              <div class="flex items-center gap-2">
                <el-icon><Picture /></el-icon>
                <span class="text-lg font-semibold">产品图片</span>
              </div>
            </template>

            <!-- Image Upload Area -->
            <el-upload
              ref="uploadRef"
              class="upload-demo"
              drag
              :auto-upload="false"
              :show-file-list="false"
              accept="image/*"
              :on-change="handleFileChange"
            >
              <div v-if="imagePreview" class="image-preview">
                <el-image
                  :src="imagePreview"
                  alt="Product preview"
                  style="width: 200px; height: 200px"
                  fit="cover"
                  class="rounded-lg"
                  :preview-src-list="[imagePreview]"
                />
                <div class="mt-3">
                  <el-button type="danger" size="small" @click.stop="removeImage" :icon="Delete">
                    Remove Image
                  </el-button>
                </div>
              </div>

              <div v-else class="upload-area">
                <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
                <div class="el-upload__text">拖动图片至此 或 <em>点击上传</em></div>
                <div class="el-upload__tip">PNG, JPG, GIF up to 10MB</div>
              </div>
            </el-upload>

            <!-- Image URL Input -->
            <el-divider>或</el-divider>
            <el-form-item label="图片URL" prop="image_url">
              <el-input
                v-model="product.image_url"
                placeholder="https://example.com/image.jpg"
                clearable
              >
                <template #prefix>
                  <el-icon><Link /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-card>
        </el-col>
      </el-row>

      <!-- Action Buttons -->
      <el-card class="mt-6" shadow="never">
        <div class="flex gap-4 justify-end">
          <el-button size="large" @click="cancelEdit">
            <el-icon><Close /></el-icon>
            取消
          </el-button>
          <el-button type="primary" size="large" @click="saveProduct" :loading="saving">
            <el-icon><Check /></el-icon>
            保存
          </el-button>
        </div>
      </el-card>
    </el-form>
  </PageLayout>
</template>

<script setup>
import PageLayout from '@/components/PageLayout.vue'
import { ref, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  InfoFilled,
  Setting,
  Picture,
  UploadFilled,
  Link,
  Delete,
  Check,
  Close,
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getProduct, updateProduct, getCategories } from '../services/productService.js'

const router = useRouter()
const route = useRoute()
const productFormRef = ref()
const uploadRef = ref()
const saving = ref(false)
const loading = ref(false)
const categories = ref([])

const product = ref({
  id: null,
  sku: '',
  name: '',
  description: '',
  unit_price: 0,
  image_url: '',
  active: true,
  units_in_stock: 0,
  category_id: '',
  imageFile: null
})

const imagePreview = ref('')

// Form validation rules
const rules = {
  name: [
    { required: true, message: '请输入产品名称', trigger: 'blur' },
    { min: 2, max: 255, message: '长度应为2到255个字符', trigger: 'blur' },
  ],
  sku: [
    { required: true, message: '请输入SKU', trigger: 'blur' },
    { min: 2, max: 255, message: '长度应为2到255个字符', trigger: 'blur' },
  ],
  unit_price: [
    { required: true, message: '请输入单价', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格必须大于0', trigger: 'blur' },
  ],
  units_in_stock: [
    { required: true, message: '请输入库存数量', trigger: 'blur' },
    { type: 'number', min: 0, message: '库存必须大于等于0', trigger: 'blur' },
  ],
  category_id: [{ required: true, message: '请选择分类', trigger: 'change' }],
}

// Watch for image changes to update preview
watch(
  () => product.value.image_url,
  (newUrl) => {
    if (newUrl && (newUrl.startsWith('http') || newUrl.startsWith('data:'))) {
      imagePreview.value = newUrl
    }
  }
)

const handleFileChange = (file) => {
  if (file.raw && file.raw.type.startsWith('image/')) {
    // Check file size (10MB limit)
    if (file.raw.size > 10 * 1024 * 1024) {
      ElMessage.error('File size cannot exceed 10MB')
      return
    }

    const reader = new FileReader()
    reader.onload = (e) => {
      imagePreview.value = e.target.result
      product.value.image_url = e.target.result
      product.value.imageFile = file.raw
    }
    reader.readAsDataURL(file.raw)
  } else {
    ElMessage.error('Please select a valid image file')
  }
}

const removeImage = () => {
  imagePreview.value = ''
  product.value.image_url = ''
  product.value.imageFile = null
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await getCategories()
    categories.value = response.data || []
  } catch (error) {
    console.error('获取分类失败:', error)
    ElMessage.error('获取分类失败')
  }
}

// 获取产品详情
const fetchProduct = async () => {
  const productId = route.params.id
  if (!productId) return

  loading.value = true
  try {
    const response = await getProduct(productId)
    const productData = response.data
    
    console.log('获取到的产品数据:', productData) // Debug log
    
    product.value = {
      id: productData.id,
      sku: productData.sku,
      name: productData.name,
      description: productData.description || '',
      unit_price: productData.unitPrice,
      units_in_stock: productData.unitsInStock,
      category_id: productData.categoryId,
      active: productData.active,
      image_url: productData.imageUrl || '',
      imageFile: null
    }
    
    if (productData.imageUrl) {
      const fullImageUrl = productData.imageUrl.startsWith('http')
        ? productData.imageUrl
        : `http://localhost:8080${productData.imageUrl.startsWith('/') ? '' : '/'}${productData.imageUrl}`
      imagePreview.value = fullImageUrl
      console.log("产品图片 URL:", fullImageUrl)
    }
  } catch (error) {
    console.error('获取产品详情失败:', error)
    ElMessage.error('Product not found')
    router.push('/products')
  } finally {
    loading.value = false
  }
}

const saveProduct = async () => {
  if (!productFormRef.value) return

  try {
    const valid = await productFormRef.value.validate()
    if (!valid) return

    saving.value = true

    await updateProduct(product.value.id, product.value)
    
    ElMessage.success('Product updated successfully!')
    router.push('/products')
  } catch (error) {
    console.error('更新产品失败:', error)
    ElMessage.error(error.message || '更新产品失败')
  } finally {
    saving.value = false
  }
}

const cancelEdit = () => {
  router.push('/products')
}

onMounted(() => {
  fetchCategories()
  fetchProduct()
})
</script>

<style scoped>
.upload-area {
  text-align: center;
  padding: 40px 0;
}

.image-preview {
  text-align: center;
}

.el-icon--upload {
  font-size: 67px;
  color: #c0c4cc;
  margin-bottom: 16px;
}

.el-upload__text {
  color: #606266;
  font-size: 14px;
}

.el-upload__tip {
  font-size: 12px;
  color: #909399;
  margin-top: 7px;
}
</style>