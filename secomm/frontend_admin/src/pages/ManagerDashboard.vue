<template>
  <PageLayout title="经理驾驶舱">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900">经理驾驶舱</h1>
      <p class="text-gray-600 mt-1">实时查看运营状态和管理概览</p>
    </div>

    <!-- Date Filter -->
    <el-card class="mb-6" shadow="never">
      <el-radio-group v-model="selectedPeriod" size="default">
        <el-radio-button label="Today">今日</el-radio-button>
        <el-radio-button label="7 Days">7天</el-radio-button>
        <el-radio-button label="30 Days">30天</el-radio-button>
        <el-radio-button label="90 Days">90天</el-radio-button>
      </el-radio-group>
    </el-card>

    <!-- Key Metrics Cards -->
    <el-row :gutter="24" class="mb-8">
      <el-col :xs="24" :sm="12" :lg="6" v-for="metric in metrics" :key="metric.label">
        <el-card shadow="hover" class="metric-card">
          <div class="flex items-center justify-between mb-4">
            <div :class="['p-3 rounded-lg', metric.bgColor]">
              <component :is="metric.icon" :class="['w-6 h-6', metric.iconColor]" />
            </div>
            <el-tag :type="metric.trend >= 0 ? 'success' : 'danger'" size="small">
              {{ metric.trend >= 0 ? '+' : '' }}{{ metric.trend }}%
            </el-tag>
          </div>
          <h3 class="text-gray-600 text-sm font-medium mb-1">{{ metric.label }}</h3>
          <p class="text-3xl font-bold text-gray-900">{{ metric.value }}</p>
        </el-card>
      </el-col>
    </el-row>

    <!-- Charts Row 1 -->
    <el-row :gutter="24" class="mb-6">
      <!-- Sales Trend Chart -->
      <el-col :lg="12" :md="24">
        <el-card shadow="never">
          <template #header>
            <h2 class="text-lg font-bold text-gray-900">销售趋势</h2>
          </template>
          <div class="h-80 flex items-center justify-center">
            <div class="text-center text-gray-500">
              <el-icon size="48"><TrendingUp /></el-icon>
              <p class="mt-2">Sales Chart Placeholder</p>
              <p class="text-sm">Chart component would go here</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- Order Status Distribution -->
      <el-col :lg="12" :md="24">
        <el-card shadow="never">
          <template #header>
            <h2 class="text-lg font-bold text-gray-900">订单状态分布</h2>
          </template>
          <div class="h-80">
            <div class="flex items-center justify-center h-full">
              <div class="text-center">
                <div class="text-4xl font-bold text-gray-900 mb-2">{{ totalOrders }}</div>
                <div class="text-sm text-gray-600 mb-4">总订单数</div>
                <div class="space-y-2">
                  <div
                    v-for="status in orderStatusData"
                    :key="status.status"
                    class="flex items-center justify-between"
                  >
                    <div class="flex items-center gap-2">
                      <div :class="['w-3 h-3 rounded', getStatusColor(status.status)]"></div>
                      <span class="text-sm">{{ status.status }}</span>
                    </div>
                    <span class="text-sm font-medium">{{ status.count }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Recent Orders Section -->
    <el-row :gutter="24" class="mb-6">
      <el-col :span="24">
        <el-card shadow="never">
          <template #header>
            <div class="flex items-center justify-between">
              <h2 class="text-lg font-bold text-gray-900">最近订单</h2>
              <el-button type="primary" size="small" @click="$router.push('/orders')">
                查看所有订单
              </el-button>
            </div>
          </template>

          <el-table :data="recentOrders" style="width: 100%" stripe>
            <el-table-column prop="id" label="订单 ID" width="120">
              <template #default="{ row }">
                <span class="font-medium">#{{ row.id }}</span>
              </template>
            </el-table-column>

            <el-table-column prop="customer" label="客户" min-width="150" />

            <el-table-column prop="amount" label="金额" width="120">
              <template #default="{ row }">
                <span class="font-semibold text-green-600">${{ row.amount }}</span>
              </template>
            </el-table-column>

            <el-table-column prop="status" label="状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getStatusTagType(row.status)">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button
                  type="primary"
                  size="small"
                  @click="viewOrder(row)"
                  circle
                  :icon="View"
                />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- Charts Row 2 -->
    <el-row :gutter="24" class="mb-6">
      <!-- Top Products -->
      <el-col :lg="16" :md="24">
        <el-card shadow="never">
          <template #header>
            <h2 class="text-lg font-bold text-gray-900">畅销产品</h2>
          </template>
          <div class="space-y-4">
            <div v-for="product in topProducts" :key="product.id" class="product-item">
              <div
                class="flex items-center gap-4 p-3 rounded-lg hover:bg-gray-50 transition-colors"
              >
                <el-avatar :size="48" shape="square">
                  <el-icon><Package /></el-icon>
                </el-avatar>
                <div class="flex-1">
                  <h3 class="font-semibold text-gray-900">{{ product.name }}</h3>
                  <p class="text-sm text-gray-600">{{ product.category }}</p>
                </div>
                <div class="text-right">
                  <p class="font-semibold text-gray-900">{{ product.sales }} sales</p>
                  <p class="text-sm text-green-600">${{ product.revenue.toLocaleString() }}</p>
                </div>
                <div class="w-16">
                  <el-progress
                    :percentage="product.percentage"
                    :show-text="false"
                    :stroke-width="6"
                  />
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- Quick Stats -->
      <el-col :lg="8" :md="24">
        <el-card shadow="never">
          <template #header>
            <h2 class="text-lg font-bold text-gray-900">快速统计</h2>
          </template>
          <div class="space-y-4">
            <el-statistic title="今日新用户" :value="42" suffix="人">
              <template #prefix>
                <el-icon><UserPlus /></el-icon>
              </template>
            </el-statistic>
            <el-divider />
            <el-statistic title="待处理订单" :value="23" suffix="个">
              <template #prefix>
                <el-icon><ShoppingBag /></el-icon>
              </template>
            </el-statistic>
            <el-divider />
            <el-statistic title="客户反馈" :value="8" suffix="条">
              <template #prefix>
                <el-icon><MessageSquare /></el-icon>
              </template>
            </el-statistic>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </PageLayout>
</template>

<script setup>
import PageLayout from '@/components/PageLayout.vue'
import { ref, computed } from 'vue'
import {
  TrendingUp,
  ShoppingCart,
  Users,
  Star,
  Package,
  ShoppingBag,
  UserPlus,
  MessageSquare,
  View,
} from 'lucide-vue-next'
import { ElMessage } from 'element-plus'

const selectedPeriod = ref('7 Days')

// Key Metrics (merged from both dashboards)
const metrics = ref([
  {
    label: '总销售',
    value: '$45,231',
    trend: 12.5,
    icon: TrendingUp,
    bgColor: 'bg-blue-100',
    iconColor: 'text-blue-600',
  },
  {
    label: '总订单',
    value: '1,234',
    trend: 8.2,
    icon: ShoppingCart,
    bgColor: 'bg-green-100',
    iconColor: 'text-green-600',
  },
  {
    label: '活跃用户',
    value: '8,549',
    trend: -2.4,
    icon: Users,
    bgColor: 'bg-purple-100',
    iconColor: 'text-purple-600',
  },
  {
    label: '平均评分',
    value: '4.8',
    trend: 5.1,
    icon: Star,
    bgColor: 'bg-yellow-100',
    iconColor: 'text-yellow-600',
  },
])

// Order Status Data
const orderStatusData = ref([
  { status: 'Completed', count: 856 },
  { status: 'Processing', count: 234 },
  { status: 'Pending', count: 98 },
  { status: 'Cancelled', count: 46 },
])

// Recent Orders (from Dashboard.vue)
const recentOrders = ref([
  { id: 1001, customer: 'John Doe', amount: 299.99, status: 'Completed' },
  { id: 1002, customer: 'Jane Smith', amount: 159.5, status: 'Processing' },
  { id: 1003, customer: 'Bob Johnson', amount: 499.0, status: 'Pending' },
  { id: 1004, customer: 'Alice Brown', amount: 89.99, status: 'Completed' },
  { id: 1005, customer: 'Charlie Wilson', amount: 349.99, status: 'Shipped' },
])

const totalOrders = computed(() => {
  return orderStatusData.value.reduce((sum, status) => sum + status.count, 0)
})

const getStatusColor = (status) => {
  const colors = {
    Completed: 'bg-green-500',
    Processing: 'bg-blue-500',
    Pending: 'bg-yellow-500',
    Cancelled: 'bg-red-500',
  }
  return colors[status] || 'bg-gray-500'
}

const getStatusTagType = (status) => {
  const types = {
    Completed: 'success',
    Processing: 'primary',
    Pending: 'warning',
    Shipped: 'info',
  }
  return types[status] || 'default'
}

// Top Products
const topProducts = ref([
  {
    id: 1,
    name: 'Wireless Headphones',
    category: 'Electronics',
    sales: 342,
    revenue: 34200,
    percentage: 95,
  },
  {
    id: 2,
    name: 'Smart Watch',
    category: 'Electronics',
    sales: 298,
    revenue: 29800,
    percentage: 82,
  },
  {
    id: 3,
    name: 'Running Shoes',
    category: 'Sports',
    sales: 256,
    revenue: 25600,
    percentage: 71,
  },
  {
    id: 4,
    name: 'Coffee Maker',
    category: 'Home',
    sales: 189,
    revenue: 18900,
    percentage: 52,
  },
  {
    id: 5,
    name: 'Yoga Mat',
    category: 'Sports',
    sales: 167,
    revenue: 16700,
    percentage: 46,
  },
])

// Functions from Dashboard.vue
const viewOrder = (order) => {
  ElMessage.info(`Viewing order #${order.id}`)
}
</script>

<style scoped>
.metric-card {
  transition: transform 0.2s;
}

.metric-card:hover {
  transform: translateY(-2px);
}

.product-item {
  border-radius: 8px;
}
</style>
