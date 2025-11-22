<template>
  <PageLayout title="Order Management">
    <div class="mb-6">
      <h1 class="text-3xl font-bold text-gray-800">订单管理</h1>
      <p class="text-gray-600 mt-1">管理所有客户订单</p>
    </div>

    <!-- Search and Filter Section -->
    <el-card class="mb-6" shadow="never">
      <div class="flex gap-4 items-center">
        <el-input
          v-model="searchQuery"
          placeholder="按ID，客户，数量，等关键词查询..."
          :prefix-icon="Search"
          style="width: 400px"
          clearable
        />
        <el-radio-group v-model="filterStatus" size="default">
          <el-radio-button label="All">所有订单</el-radio-button>
          <el-radio-button label="Pending">待处理</el-radio-button>
          <el-radio-button label="Processing">处理中</el-radio-button>
          <el-radio-button label="Shipped">已发货</el-radio-button>
          <el-radio-button label="Completed">已完成</el-radio-button>
        </el-radio-group>
      </div>
    </el-card>

    <!-- Orders Table -->
    <el-card shadow="never">
      <el-table :data="paginatedOrders" style="width: 100%" stripe>
        <el-table-column prop="id" label="订单 ID" width="120">
          <template #default="{ row }">
            <span class="font-medium">#{{ row.id }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="customer" label="客户" min-width="150" />

        <el-table-column prop="date" label="日期" width="120" />

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
            <el-button type="primary" size="small" @click="viewOrder(row)" circle :icon="Eye" />
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="flex justify-center mt-6">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="totalOrders"
          layout="total, sizes, prev, pager, next, jumper"
          background
        />
      </div>
    </el-card>

    <!-- Order Detail Dialog -->
    <el-dialog
      v-model="showDetailModal"
      title="Order Details"
      width="600px"
      :before-close="closeDetailModal"
    >
      <div v-if="selectedOrder">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单 ID">#{{ selectedOrder.id }}</el-descriptions-item>
          <el-descriptions-item label="客户">{{ selectedOrder.customer }}</el-descriptions-item>
          <el-descriptions-item label="日期">{{ selectedOrder.date }}</el-descriptions-item>
          <el-descriptions-item label="金额">
            <span class="font-semibold text-green-600">${{ selectedOrder.amount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="getStatusTagType(selectedOrder.status)">
              {{ selectedOrder.status }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <div class="mb-4">
          <label class="block text-gray-700 mb-2 font-medium">更新状态:</label>
          <el-select v-model="selectedOrder.status" style="width: 100%">
            <el-option label="待处理" value="Pending" />
            <el-option label="处理中" value="Processing" />
            <el-option label="已发货" value="Shipped" />
            <el-option label="已完成" value="Completed" />
          </el-select>
        </div>

        <div class="mb-4">
          <h3 class="font-semibold mb-2">订单商品:</h3>
          <el-tag v-for="item in selectedOrder.items" :key="item" class="mr-2 mb-2" type="info">
            {{ item }}
          </el-tag>
        </div>
      </div>

      <template #footer>
        <div class="flex gap-2 justify-end">
          <el-button @click="closeDetailModal">关闭</el-button>
          <el-button type="primary" @click="updateOrderStatus"> 更新状态 </el-button>
        </div>
      </template>
    </el-dialog>
  </PageLayout>
</template>

<script setup>
import PageLayout from '@/components/PageLayout.vue'
import { ref, computed, watch } from 'vue'
import { Eye, Search } from 'lucide-vue-next'
import { ElMessage, ElPagination } from 'element-plus'

const orders = ref([
  {
    id: 1001,
    customer: 'John Doe',
    date: '2024-01-15',
    amount: 299.99,
    status: 'Completed',
    items: ['Laptop Pro 15', 'Wireless Mouse'],
  },
  {
    id: 1002,
    customer: 'Jane Smith',
    date: '2024-01-16',
    amount: 159.5,
    status: 'Processing',
    items: ['Cotton T-Shirt (x3)', 'Programming Book'],
  },
  {
    id: 1003,
    customer: 'Bob Johnson',
    date: '2024-01-17',
    amount: 499.0,
    status: 'Pending',
    items: ['Coffee Maker', 'Wireless Mouse'],
  },
  {
    id: 1004,
    customer: 'Alice Brown',
    date: '2024-01-18',
    amount: 89.99,
    status: 'Completed',
    items: ['Programming Book'],
  },
  {
    id: 1005,
    customer: 'Charlie Wilson',
    date: '2024-01-19',
    amount: 349.99,
    status: 'Shipped',
    items: ['Laptop Pro 15'],
  },
  {
    id: 1006,
    customer: 'David Davis',
    date: '2024-01-20',
    amount: 129.99,
    status: 'Pending',
    items: ['Mechanical Keyboard'],
  },
  {
    id: 1007,
    customer: 'Eve Evans',
    date: '2024-01-21',
    amount: 249.99,
    status: 'Processing',
    items: ['Monitor 27"', 'HDMI Cable'],
  },
  {
    id: 1008,
    customer: 'Frank Miller',
    date: '2024-01-22',
    amount: 69.99,
    status: 'Completed',
    items: ['Wireless Headphones'],
  },
  {
    id: 1009,
    customer: 'Grace Lee',
    date: '2024-01-23',
    amount: 199.5,
    status: 'Shipped',
    items: ['Tablet', 'Stylus Pen'],
  },
  {
    id: 1010,
    customer: 'Henry Taylor',
    date: '2024-01-24',
    amount: 399.99,
    status: 'Pending',
    items: ['Gaming Console', 'Controller'],
  },
  {
    id: 1011,
    customer: 'Ivy Moore',
    date: '2024-01-25',
    amount: 49.99,
    status: 'Completed',
    items: ['Phone Case'],
  },
  {
    id: 1012,
    customer: 'Jack Wilson',
    date: '2024-01-26',
    amount: 299.0,
    status: 'Processing',
    items: ['Camera', 'Memory Card'],
  },
  {
    id: 1013,
    customer: 'Kate Anderson',
    date: '2024-01-27',
    amount: 179.99,
    status: 'Shipped',
    items: ['Wireless Speaker'],
  },
  {
    id: 1014,
    customer: 'Leo Thomas',
    date: '2024-01-28',
    amount: 89.5,
    status: 'Pending',
    items: ['External Hard Drive'],
  },
  {
    id: 1015,
    customer: 'Mia Garcia',
    date: '2024-01-29',
    amount: 149.99,
    status: 'Completed',
    items: ['Smart Watch'],
  },
])

const filterStatus = ref('All')
const showDetailModal = ref(false)
const selectedOrder = ref(null)
const searchQuery = ref('')

// Pagination setup
const currentPage = ref(1)
const pageSize = ref(10)
const totalOrders = ref(orders.value.length)

const filteredOrders = computed(() => {
  let filtered = orders.value

  // Filter by status
  if (filterStatus.value !== 'All') {
    filtered = filtered.filter((order) => order.status === filterStatus.value)
  }

  // Filter by search query
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(
      (order) =>
        order.id.toString().includes(query) ||
        order.customer.toLowerCase().includes(query) ||
        order.amount.toString().includes(query)
    )
  }

  return filtered
})

// Pagination computed property
const paginatedOrders = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredOrders.value.slice(start, end)
})

// Update total orders when filters or search change
watch(
  () => filteredOrders.value.length,
  (newLength) => {
    totalOrders.value = newLength
    currentPage.value = 1 // Reset to first page when filters change
  }
)

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
  selectedOrder.value = { ...order }
  showDetailModal.value = true
}

const updateOrderStatus = () => {
  const index = orders.value.findIndex((o) => o.id === selectedOrder.value.id)
  orders.value[index].status = selectedOrder.value.status
  ElMessage.success('Order status updated successfully!')
  closeDetailModal()
}

const closeDetailModal = () => {
  showDetailModal.value = false
  selectedOrder.value = null
}
</script>
