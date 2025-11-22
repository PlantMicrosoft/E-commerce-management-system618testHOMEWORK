<template>
  <PageLayout title="Dashboard">
    <div class="mb-6">
      <h1 class="text-3xl font-bold text-gray-800">Dashboard</h1>
      <p class="text-gray-600 mt-1">Welcome to your admin dashboard</p>
    </div>

    <!-- Stats Cards -->
    <el-row :gutter="24" class="mb-8">
      <el-col :xs="24" :sm="12" :lg="6" v-for="(stat, index) in stats" :key="index">
        <el-card shadow="hover" class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm mb-1">{{ stat.label }}</p>
              <p class="text-2xl font-bold text-gray-800">{{ stat.value }}</p>
            </div>
            <div :class="['p-3 rounded-lg', stat.bgColor]">
              <component :is="stat.icon" :class="['w-8 h-8', stat.iconColor]" />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Recent Orders -->
    <el-card shadow="never">
      <template #header>
        <div class="flex items-center justify-between">
          <h2 class="text-xl font-semibold text-gray-800">Recent Orders</h2>
          <el-button type="primary" size="small" @click="$router.push('/orders')">
            View All Orders
          </el-button>
        </div>
      </template>

      <el-table :data="recentOrders" style="width: 100%" stripe>
        <el-table-column prop="id" label="Order ID" width="120">
          <template #default="{ row }">
            <span class="font-medium">#{{ row.id }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="customer" label="Customer" min-width="150" />

        <el-table-column prop="amount" label="Amount" width="120">
          <template #default="{ row }">
            <span class="font-semibold text-green-600">${{ row.amount }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="Status" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Actions" width="100">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewOrder(row)" circle :icon="View" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </PageLayout>
</template>

<script setup>
import PageLayout from '@/components/PageLayout.vue'
import { ref } from 'vue'
import { Package, ShoppingCart, Users, DollarSign, View } from 'lucide-vue-next'
import { ElMessage } from 'element-plus'

const stats = ref([
  {
    label: 'Total Products',
    value: '156',
    icon: Package,
    bgColor: 'bg-blue-100',
    iconColor: 'text-blue-600',
  },
  {
    label: 'Total Orders',
    value: '89',
    icon: ShoppingCart,
    bgColor: 'bg-green-100',
    iconColor: 'text-green-600',
  },
  {
    label: 'Total Users',
    value: '234',
    icon: Users,
    bgColor: 'bg-purple-100',
    iconColor: 'text-purple-600',
  },
  {
    label: 'Revenue',
    value: '$45,680',
    icon: DollarSign,
    bgColor: 'bg-yellow-100',
    iconColor: 'text-yellow-600',
  },
])

const recentOrders = ref([
  { id: 1001, customer: 'John Doe', amount: 299.99, status: 'Completed' },
  { id: 1002, customer: 'Jane Smith', amount: 159.5, status: 'Processing' },
  { id: 1003, customer: 'Bob Johnson', amount: 499.0, status: 'Pending' },
  { id: 1004, customer: 'Alice Brown', amount: 89.99, status: 'Completed' },
  { id: 1005, customer: 'Charlie Wilson', amount: 349.99, status: 'Shipped' },
])

const getStatusTagType = (status) => {
  const types = {
    Completed: 'success',
    Processing: 'primary',
    Pending: 'warning',
    Shipped: 'info',
  }
  return types[status] || 'default'
}

const viewOrder = (order) => {
  ElMessage.info(`Viewing order #${order.id}`)
}
</script>

<style scoped>
.stat-card {
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
}
</style>
