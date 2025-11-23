import { getToken, removeToken } from './auth.js'

const API_BASE_URL = 'http://localhost:8080/api'

// 创建请求配置
const createRequestConfig = (options = {}) => {
  const token = getToken()
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers
    },
    ...options
  }

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
}

// 处理响应
const handleResponse = async (response) => {
  if (response.status === 401) {
    removeToken()
    window.location.href = '/login'
    throw new Error('未授权访问')
  }

  const data = await response.json()
  
  if (data.status !== 200) {
    throw new Error(data.message || '请求失败')
  }

  return data
}

// GET 请求
export const get = async (url, options = {}) => {
  const config = createRequestConfig(options)
  const response = await fetch(`${API_BASE_URL}${url}`, {
    method: 'GET',
    ...config
  })
  return handleResponse(response)
}

// POST 请求
export const post = async (url, data, options = {}) => {
  const config = createRequestConfig(options)
  const response = await fetch(`${API_BASE_URL}${url}`, {
    method: 'POST',
    body: JSON.stringify(data),
    ...config
  })
  return handleResponse(response)
}

// POST 请求 (FormData)
export const postFormData = async (url, formData, options = {}) => {
  const token = getToken()
  const config = {
    headers: {},
    ...options
  }

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  const response = await fetch(`${API_BASE_URL}${url}`, {
    method: 'POST',
    body: formData,
    ...config
  })
  return handleResponse(response)
}

// PUT 请求 (FormData)
export const putFormData = async (url, formData, options = {}) => {
  const token = getToken()
  const config = {
    headers: {},
    ...options
  }

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  const response = await fetch(`${API_BASE_URL}${url}`, {
    method: 'PUT',
    body: formData,
    ...config
  })
  return handleResponse(response)
}

// PUT 请求
export const put = async (url, data, options = {}) => {
  const config = createRequestConfig(options)
  const response = await fetch(`${API_BASE_URL}${url}`, {
    method: 'PUT',
    body: JSON.stringify(data),
    ...config
  })
  return handleResponse(response)
}

// DELETE 请求
export const del = async (url, options = {}) => {
  const config = createRequestConfig(options)
  const response = await fetch(`${API_BASE_URL}${url}`, {
    method: 'DELETE',
    ...config
  })
  return handleResponse(response)
}