<template>
  <PageLayout title="用户详细信息">
    <div class="mb-6">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>
          <router-link to="/users" class="hover:text-blue-600">用户管理</router-link>
        </el-breadcrumb-item>
        <el-breadcrumb-item>用户详细信息</el-breadcrumb-item>
      </el-breadcrumb>
      <h1 class="text-3xl font-bold text-gray-800 mt-4">用户详细信息</h1>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center h-64">
      <el-loading-spinner size="32" />
    </div>

    <div v-else-if="user" class="space-y-6">
      <!-- User Basic Information -->
      <el-card shadow="never">
        <template #header>
          <div class="flex items-center justify-between">
            <h2 class="text-xl font-semibold">用户基本信息</h2>
            <el-button type="primary" @click="editMode = !editMode">
              {{ editMode ? '取消' : '编辑' }}
            </el-button>
          </div>
        </template>

        <div v-if="!editMode">
          <el-row :gutter="24">
            <el-col :span="8">
              <div class="flex flex-col items-center text-center">
                <el-avatar :size="120" class="mb-4">
                  {{ (user.name || '').charAt(0).toUpperCase() }}
                </el-avatar>
                <h3 class="text-2xl font-bold text-gray-800">{{ user.name }}</h3>
                <p class="text-gray-600">{{ user.email }}</p>
              </div>
            </el-col>
            <el-col :span="16">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="用户ID">#{{ user.id }}</el-descriptions-item>
                <el-descriptions-item label="用户名">{{ user.username }}</el-descriptions-item>
                <el-descriptions-item label="手机号">{{ user.phone || 'N/A' }}</el-descriptions-item>
                <el-descriptions-item label="角色">
                  <el-tag :type="getRoleTagType(user.role)">{{ user.role }}</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="审批状态">
                  <el-tag :type="getRegistrationStatusTagType(user.approvalStatus)">
                    {{ user.approvalStatus }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="账户状态">
                  <el-tag :type="user.status === 'Active' ? 'success' : 'danger'">
                    {{ user.status }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="加入日期">{{ user.joinDate }}</el-descriptions-item>
                <el-descriptions-item label="最后登录时间">{{ user.lastLogin }}</el-descriptions-item>
              </el-descriptions>
            </el-col>
          </el-row>
        </div>

        <!-- Edit Form -->
        <div v-else>
          <el-form :model="editForm" label-width="140px" label-position="left">
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="姓名">
                  <el-input v-model="editForm.name" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="邮箱">
                  <el-input v-model="editForm.email" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="角色">
                  <el-select v-model="editForm.role" style="width: 100%">
                    <el-option label="客户" value="Customer" />
                    <el-option label="管理员" value="Admin" />
                    <el-option label="经理" value="Manager" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="Registration Status">
                  <el-select v-model="editForm.registrationStatus" style="width: 100%">
                    <el-option label="已注册" value="Registered" />
                    <el-option label="待验证" value="Pending" />
                    <el-option label="已验证" value="Verified" />
                    <el-option label="已暂停" value="Suspended" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="账户状态">
              <el-switch
                v-model="editForm.active"
                active-text="Active"
                inactive-text="Inactive"
                :active-value="true"
                :inactive-value="false"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveUser">Save Changes</el-button>
              <el-button @click="editMode = false">Cancel</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-card>

      <!-- User Orders (only show if user is active) -->
      <el-card v-if="user.status === 'Active'" shadow="never">
        <template #header>
          <div class="flex items-center justify-between">
            <h2 class="text-xl font-semibold">订单历史记录</h2>
            <el-tag type="info">{{ userOrders.length }} 订单</el-tag>
          </div>
        </template>

        <el-table :data="userOrders" style="width: 100%" stripe>
          <el-table-column prop="id" label="订单 ID" width="120">
            <template #default="{ row }">
              <span class="font-medium">#{{ row.id }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="date" label="订单日期" width="120" />

          <el-table-column prop="amount" label="订单金额" width="120">
            <template #default="{ row }">
              <span class="font-semibold text-green-600">${{ row.amount }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="status" label="订单状态" width="120">
            <template #default="{ row }">
              <el-tag :type="getOrderStatusTagType(row.status)">
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="items" label="订单商品" min-width="200">
            <template #default="{ row }">
              <span class="text-gray-600">{{ row.items }} items</span>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="viewOrder(row)" circle :icon="Eye" />
            </template>
          </el-table-column>
        </el-table>

        <div v-if="userOrders.length === 0" class="text-center py-8 text-gray-500">
          <el-icon size="48"><ShoppingCart /></el-icon>
          <p class="mt-2">No orders found for this user</p>
        </div>
      </el-card>

      <!-- Inactive User Message -->
      <el-card v-else shadow="never">
        <div class="text-center py-8 text-gray-500">
          <el-icon size="48"><UserX /></el-icon>
          <p class="mt-2">Order history is only available for active users</p>
        </div>
      </el-card>
    </div>

    <!-- Loading State -->
    <div v-else class="flex justify-center items-center h-64">
      <el-loading-spinner size="32" />
    </div>
  </PageLayout>
</template>

<script setup>
import PageLayout from '@/components/PageLayout.vue'
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Eye, ShoppingCart, UserX } from 'lucide-vue-next'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const user = ref(null)
const editMode = ref(false)
const editForm = ref({})
const loading = ref(false)

// 获取用户详情
const fetchUser = async (userId) => {
  loading.value = true
  try {
    const response = await fetch(`http://localhost:8080/api/admin/users/${userId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })

    const data = await response.json()
    
    if (data.status === 200) {
      const userData = data.data
      user.value = {
        id: userData.id,
        name: userData.realName || userData.username,
        email: userData.email || 'N/A',
        role: userData.roles?.[0]?.roleName || 'N/A',
        status: userData.active ? 'Active' : 'Inactive',
        registrationStatus: userData.approvalStatus || 'N/A',
        joinDate: userData.createdAt ? new Date(userData.createdAt).toLocaleDateString() : 'N/A',
        lastLogin: userData.lastLoginTime || 'N/A',
        username: userData.username,
        phone: userData.phone,
        approvalStatus: userData.approvalStatus
      }
      
      // Initialize edit form
      editForm.value = {
        ...user.value,
        active: user.value.status === 'Active',
      }
    } else {
      ElMessage.error(data.message || '获取用户详情失败')
      router.push('/users')
    }
  } catch (error) {
    console.error('获取用户详情失败:', error)
    ElMessage.error('获取用户详情失败，请稍后重试')
    router.push('/users')
  } finally {
    loading.value = false
  }
}

// Mock orders data - you can replace this with real API call later
const allOrders = [
  { id: 1001, userId: 1, date: '2024-01-08', amount: 299.99, status: 'Completed', items: 3 },
  { id: 1002, userId: 1, date: '2024-01-05', amount: 159.5, status: 'Shipped', items: 2 },
  { id: 1003, userId: 2, date: '2024-01-07', amount: 499.0, status: 'Processing', items: 1 },
  { id: 1004, userId: 2, date: '2024-01-03', amount: 89.99, status: 'Completed', items: 4 },
  { id: 1005, userId: 3, date: '2024-01-06', amount: 349.99, status: 'Pending', items: 2 },
  { id: 1006, userId: 5, date: '2024-01-04', amount: 199.99, status: 'Completed', items: 1 },
]

const userOrders = computed(() => {
  if (!user.value) return []
  return allOrders.filter((order) => order.userId === user.value.id)
})

onMounted(() => {
  const userId = parseInt(route.params.id)
  if (userId) {
    fetchUser(userId)
  } else {
    ElMessage.error('Invalid user ID')
    router.push('/users')
  }
})

const getRoleTagType = (role) => {
  const types = {
    ROLE_ADMIN: 'danger',
    Admin: 'danger',
    Manager: 'warning',
    Customer: 'primary',
    ADMIN: 'danger',
    MANAGER: 'warning', 
    CUSTOMER: 'primary'
  }
  return types[role] || 'info'
}

const getRegistrationStatusTagType = (status) => {
  const types = {
    APPROVED: 'success',
    Verified: 'success',
    Registered: 'primary',
    PENDING: 'warning',
    Pending: 'warning',
    REJECTED: 'danger',
    Suspended: 'danger',
  }
  return types[status] || 'info'
}

const getOrderStatusTagType = (status) => {
  const types = {
    Completed: 'success',
    Processing: 'primary',
    Pending: 'warning',
    Shipped: 'info',
    Cancelled: 'danger',
  }
  return types[status] || 'info'
}

const saveUser = async () => {
  try {
    const response = await fetch(`http://localhost:8080/api/admin/users/${user.value.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify({
        realName: editForm.value.name,
        email: editForm.value.email,
        active: editForm.value.active,
        // Add other fields as needed
      })
    })

    const data = await response.json()
    
    if (data.status === 200) {
      // Update user data
      Object.assign(user.value, {
        ...editForm.value,
        status: editForm.value.active ? 'Active' : 'Inactive',
      })
      
      editMode.value = false
      ElMessage.success('用户更新成功!')
    } else {
      ElMessage.error(data.message || '更新用户失败')
    }
  } catch (error) {
    console.error('更新用户失败:', error)
    ElMessage.error('更新用户失败，请稍后重试')
  }
}

const viewOrder = (order) => {
  ElMessage.info(`Viewing order #${order.id}`)
  // In real app, navigate to order detail page
}
</script>

<style scoped>
.el-descriptions {
  margin-top: 20px;
}
</style>
