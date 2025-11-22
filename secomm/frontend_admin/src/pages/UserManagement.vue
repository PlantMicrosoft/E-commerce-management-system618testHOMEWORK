<template>
  <PageLayout title="用户管理">
    <div class="mb-6">
      <h1 class="text-3xl font-bold text-gray-800">用户管理</h1>
      <p class="text-gray-600 mt-1">管理系统用户</p>
    </div>

    <!-- Search and Filter Section -->
    <el-card class="mb-6" shadow="never">
      <div class="flex gap-4 items-center">
        <el-input
          v-model="searchQuery"
          placeholder="搜索用户姓名或邮箱..."
          :prefix-icon="Search"
          style="width: 400px"
          clearable
        />
        <el-select
          v-model="selectedRegistrationStatus"
          placeholder="根据注册状态筛选..."
          style="width: 200px"
          clearable
        >
          <el-option label="所有状态" value="" />
          <el-option label="已注册" value="Registered" />
          <el-option label="待验证" value="Pending" />
          <el-option label="已验证" value="Verified" />
          <el-option label="已暂停" value="Suspended" />
        </el-select>
      </div>
    </el-card>

    <!-- Users Table -->
    <el-card shadow="never">
      <el-table :data="paginatedUsers" style="width: 100%" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />

        <el-table-column prop="realName" label="姓名" min-width="150">
          <template #default="{ row }">
            <div class="flex items-center gap-3">
              <el-avatar :size="32">
                {{ (row.realName || row.username || '').charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="font-medium">{{ row.realName || row.username }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="username" label="用户名" min-width="150" />

        <el-table-column prop="phone" label="手机号" min-width="120" />

        <el-table-column label="角色" width="120">
          <template #default="{ row }">
            <el-tag v-for="role in row.roles" :key="role.id" :type="getRoleTagType(role.roleCode)">
              {{ role.roleName }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="approvalStatus" label="审批状态" width="140">
          <template #default="{ row }">
            <el-tag 
              v-if="row.approvalStatus"
              :type="getApprovalStatusTagType(row.approvalStatus)"
            >
              {{ row.approvalStatus }}
            </el-tag>
            <span v-else class="text-gray-400">N/A</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <div class="flex gap-2">
              <el-button type="primary" size="small" @click="editUser(row)" :icon="Edit" circle />
              <el-button
                v-if="row.approvalStatus === 'PENDING'"
                type="success"
                size="small"
                @click="approveUser(row)"
                title="审批用户"
                circle
              >
                ✓
              </el-button>
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
          :total="totalUsers"
          layout="total, sizes, prev, pager, next, jumper"
          background
        />
      </div>
    </el-card>

    <!-- Edit User Dialog -->
    <el-dialog v-model="showModal" title="Edit User" width="500px" :before-close="closeModal">
      <el-form v-if="currentUser" :model="currentUser" label-width="140px" label-position="left">
        <el-form-item label="Name">
          <el-input v-model="currentUser.name" />
        </el-form-item>

        <el-form-item label="Email">
          <el-input v-model="currentUser.email" />
        </el-form-item>

        <el-form-item label="Role">
          <el-select v-model="currentUser.role" style="width: 100%">
            <el-option label="Customer" value="Customer" />
            <el-option label="Admin" value="Admin" />
            <el-option label="Manager" value="Manager" />
          </el-select>
        </el-form-item>

        <el-form-item label="Registration Status">
          <el-select v-model="currentUser.registrationStatus" style="width: 100%">
            <el-option label="Registered" value="Registered" />
            <el-option label="Pending" value="Pending" />
            <el-option label="Verified" value="Verified" />
            <el-option label="Suspended" value="Suspended" />
          </el-select>
        </el-form-item>

        <el-form-item label="Status">
          <el-switch
            v-model="currentUser.active"
            active-text="Active"
            inactive-text="Inactive"
            :active-value="true"
            :inactive-value="false"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="flex gap-2 justify-end">
          <el-button @click="closeModal">Cancel</el-button>
          <el-button type="primary" @click="saveUser">Save Changes</el-button>
        </div>
      </template>
    </el-dialog>
  </PageLayout>
</template>

<script setup>
import PageLayout from '@/components/PageLayout.vue'
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Edit, Ban } from 'lucide-vue-next'
import { ElMessage, ElPagination } from 'element-plus'

const router = useRouter()

const users = ref([])
const loading = ref(false)
const searchQuery = ref('')
const selectedRegistrationStatus = ref('')
const showModal = ref(false)
const currentUser = ref(null)

// Pagination setup
const currentPage = ref(1)
const pageSize = ref(10)
const totalUsers = ref(0)

// 获取所有用户数据
const fetchUsers = async () => {
  loading.value = true
  try {
    const response = await fetch('http://localhost:8080/api/admin/users', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        // 'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })

    const data = await response.json()
    
    if (data.status === 200) {
      users.value = data.data || []
      totalUsers.value = users.value.length
      console.log('获取用户列表:', users.value)
    } else {
      ElMessage.error(data.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 页面加载时获取用户数据
onMounted(() => {
  fetchUsers()
})

const filteredUsers = computed(() => {
  let filtered = users.value

  // Filter by search query
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(
      (user) => user.realName?.toLowerCase().includes(query) || 
               user.username?.toLowerCase().includes(query)
    )
  }

  // Filter by approval status
  if (selectedRegistrationStatus.value) {
    filtered = filtered.filter(
      (user) => user.approvalStatus === selectedRegistrationStatus.value
    )
  }

  return filtered
})

// Pagination computed property
const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredUsers.value.slice(start, end)
})

// Update total users when filters change
watch(
  () => filteredUsers.value.length,
  (newLength) => {
    totalUsers.value = newLength
    currentPage.value = 1 // Reset to first page when filters change
  }
)

const getRoleTagType = (role) => {
  const types = {
    ROLE_ADMIN: 'danger',
    Manager: 'warning',
    Customer: 'primary',
    ADMIN: 'danger',
    MANAGER: 'warning', 
    CUSTOMER: 'primary'
  }
  return types[role] || 'info' // 改为 'info' 而不是 'default'
}

const getRegistrationStatusTagType = (status) => {
  const types = {
    Verified: 'success',
    Registered: 'primary',
    Pending: 'warning',
    Suspended: 'danger',
  }
  return types[status] || 'default'
}

const getApprovalStatusTagType = (status) => {
  const types = {
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger'
  }
  return types[status] || 'info' // 改为 'info' 而不是 'default'
}

const editUser = (user) => {
  // Navigate to user detail page instead of opening modal
  console.log(user.id)
  router.push(`/users/${user.id}`)
}

const saveUser = () => {
  const index = users.value.findIndex((u) => u.id === currentUser.value.id)
  users.value[index] = {
    ...currentUser.value,
    status: currentUser.value.active ? 'Active' : 'Inactive',
  }
  ElMessage.success('User updated successfully!')
  closeModal()
}

const toggleUserStatus = (user) => {
  user.status = user.status === 'Active' ? 'Inactive' : 'Active'
  const action = user.status === 'Active' ? 'activated' : 'deactivated'
  ElMessage.success(`User ${action} successfully!`)
}

const approveUser = async (user) => {
  try {
    const response = await fetch(`http://localhost:8080/api/admin/users/${user.id}/approve`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify({
        approvalStatus: 'APPROVED',
        approvalNotes: '管理员审批通过'
      })
    })

    const data = await response.json()
    
    if (data.status === 200) {
      user.approvalStatus = 'APPROVED'
      ElMessage.success(data.message || '用户审批成功！')
    } else {
      ElMessage.error(data.message || '审批失败')
    }
  } catch (error) {
    console.error('审批用户失败:', error)
    ElMessage.error('审批失败，请稍后重试')
  }
}

const closeModal = () => {
  showModal.value = false
  currentUser.value = null
}
</script>
