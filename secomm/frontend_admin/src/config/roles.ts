// Role configuration system defining permissions and accessible pages for each role

export type UserRole = 'admin' | 'manager' | 'staff'

export interface MenuItem {
  name: string
  path: string
  icon: string
}

export interface RoleConfig {
  name: string
  displayName: string
  description: string
  menuItems: MenuItem[]
}

export const roleConfigurations: Record<UserRole, RoleConfig> = {
  admin: {
    name: 'admin',
    displayName: '管理员',
    description: '系统管理员，拥有所有管理权限',
    menuItems: [
      { name: '产品管理', path: '/products', icon: '📦' },
      { name: '订单管理', path: '/orders', icon: '🛒' },
      { name: '用户管理', path: '/users', icon: '👥' },
      { name: '评论管理', path: '/reviews', icon: '⭐' },
      { name: '经理驾驶舱', path: '/manager-dashboard', icon: '📈' },
    ],
  },
  manager: {
    name: 'manager',
    displayName: '经理',
    description: '经理，拥有仪表板、订单和评论的访问权限',
    menuItems: [
      { name: 'Dashboard', path: '/dashboard', icon: '📊' },
      { name: 'Orders', path: '/orders', icon: '🛒' },
      { name: 'Reviews', path: '/reviews', icon: '⭐' },
      { name: 'Manager Dashboard', path: '/manager-dashboard', icon: '📈' },
    ],
  },
  staff: {
    name: 'staff',
    displayName: '员工',
    description: '员工，仅限访问订单和评论',
    menuItems: [
      { name: 'Orders', path: '/orders', icon: '🛒' },
      { name: 'Reviews', path: '/reviews', icon: '⭐' },
    ],
  },
}

export function getRoleConfig(role: UserRole): RoleConfig {
  return roleConfigurations[role]
}

export function canAccessPage(userRole: UserRole, pagePath: string): boolean {
  const config = roleConfigurations[userRole]
  return config.menuItems.some((item) => item.path === pagePath)
}
