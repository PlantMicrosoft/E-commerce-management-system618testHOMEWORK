import { get, post, postFormData, putFormData, put, del } from '../utils/api.js'

// 获取所有产品（支持搜索和过滤）
export const getProducts = async (params = {}) => {
  const queryParams = new URLSearchParams()
  
  // 添加分页参数
  if (params.page !== undefined) queryParams.append('page', params.page)
  if (params.size !== undefined) queryParams.append('size', params.size)
  if (params.sortBy) queryParams.append('sortBy', params.sortBy)
  if (params.sortDir) queryParams.append('sortDir', params.sortDir)
  
  // 添加搜索和过滤参数
  if (params.keyword) queryParams.append('keyword', params.keyword)
  if (params.categoryId) queryParams.append('categoryId', params.categoryId)
  if (params.status) queryParams.append('status', params.status)
  
  const url = queryParams.toString() ? `/products?${queryParams.toString()}` : '/products'
  return await get(url)
}

// 搜索产品
export const searchProducts = async (keyword, page = 0, size = 10) => {
  const q = new URLSearchParams()
  q.append('keyword', keyword)
  q.append('page', page)
  q.append('size', size)
  return await get(`/products/search?${q.toString()}`)
}

// 获取单个产品详情
export const getProduct = async (id) => {
  return await get(`/products/${id}`)
}

// 创建产品
export const createProduct = async (productData) => {
  const formData = new FormData()
  
  // 添加产品基本信息
  formData.append('sku', productData.sku)
  formData.append('name', productData.name)
  formData.append('description', productData.description || '')
  formData.append('unitPrice', productData.unit_price)
  formData.append('unitsInStock', productData.units_in_stock)
  formData.append('categoryId', productData.category_id)
  formData.append('active', productData.active)
  
  // 添加图片文件
  if (productData.imageFile) {
    formData.append('image', productData.imageFile)
  }
  
  return await postFormData('/products', formData)
}

// 更新产品
export const updateProduct = async (id, productData) => {
  if (productData.imageFile) {
    const formData = new FormData()
    formData.append('sku', productData.sku)
    formData.append('name', productData.name)
    formData.append('description', productData.description || '')
    formData.append('unitPrice', productData.unit_price)
    formData.append('unitsInStock', productData.units_in_stock)
    formData.append('categoryId', productData.category_id)
    formData.append('active', productData.active)
    formData.append('image', productData.imageFile)
    
    // 后端要求PUT + multipart
    return await putFormData(`/products/${id}`, formData)
  } else {
    // 没有新图片，使用JSON
    const updateData = {
      sku: productData.sku,
      name: productData.name,
      description: productData.description || '',
      unitPrice: productData.unit_price,
      unitsInStock: productData.units_in_stock,
      categoryId: productData.category_id,
      active: productData.active
    }
    return await put(`/products/${id}`, updateData)
  }
}

// 删除产品
export const deleteProduct = async (id) => {
  return await del(`/products/${id}`)
}

// 获取产品分类
export const getCategories = async () => {
  return await get('/categories')
}