const TOKEN_KEY = 'admin_token'
const USER_KEY = 'admin_user'

// 获取 token
export const getToken = () => {
  return localStorage.getItem(TOKEN_KEY)
}

// 设置 token
export const setToken = (token) => {
  localStorage.setItem(TOKEN_KEY, token)
}

// 移除 token
export const removeToken = () => {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}

// 获取用户信息
export const getUser = () => {
  const userStr = localStorage.getItem(USER_KEY)
  return userStr ? JSON.parse(userStr) : null
}

// 设置用户信息
export const setUser = (user) => {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

// 检查是否已登录
export const isAuthenticated = () => {
  return !!getToken()
}

// 检查是否有管理员权限
export const isAdmin = () => {
  const user = getUser()
  return user && user.roles && user.roles.some(role => 
    role.roleCode === 'ROLE_ADMIN' || role.roleCode === 'ROLE_SUPER_ADMIN'
  )
}