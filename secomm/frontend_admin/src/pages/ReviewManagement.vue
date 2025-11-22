<template>
  <PageLayout title="评价管理">
    <div class="mb-6">
      <h1 class="text-3xl font-bold text-gray-800">评价管理</h1>
      <p class="text-gray-600 mt-1">管理产品评价和反馈</p>
    </div>

    <!-- Search and Filter -->
    <el-card class="mb-6" shadow="never">
      <div class="flex flex-col md:flex-row gap-4 items-center justify-between">
        <el-input
          v-model="searchQuery"
          placeholder="按产品，用户，或评价内容查询"
          prefix-icon="Search"
          class="w-full md:w-64"
        />
        <el-radio-group v-model="filterStatus" size="default">
          <el-radio-button label="All">所有评价</el-radio-button>
          <el-radio-button label="Pending">待处理</el-radio-button>
          <el-radio-button label="Approved">已通过</el-radio-button>
          <el-radio-button label="Rejected">已拒绝</el-radio-button>
        </el-radio-group>
      </div>
    </el-card>

    <!-- Reviews List -->
    <div class="space-y-4">
      <el-card
        v-for="review in paginatedReviews"
        :key="review.id"
        shadow="hover"
        class="review-card"
      >
        <div class="flex justify-between items-start mb-3">
          <div>
            <h3 class="font-semibold text-lg">{{ review.productName }}</h3>
            <p class="text-gray-600 text-sm">
              by <span class="font-medium">{{ review.userName }}</span> on {{ review.date }}
            </p>
          </div>
          <div class="flex items-center gap-1">
            <el-rate
              v-model="review.rating"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}"
            />
          </div>
        </div>

        <p class="text-gray-700 mb-4">{{ review.comment }}</p>

        <div class="flex justify-between items-center">
          <el-tag :type="getStatusTagType(review.status)">
            {{ review.status }}
          </el-tag>

          <div class="flex gap-2">
            <el-button
              v-if="review.status === 'Pending'"
              type="success"
              size="small"
              @click="approveReview(review.id)"
              :icon="Check"
            >
              Approve
            </el-button>

            <el-button
              v-if="review.status === 'Pending'"
              type="warning"
              size="small"
              @click="rejectReview(review.id)"
              :icon="X"
            >
              Reject
            </el-button>

            <el-popconfirm
              v-if="review.status !== 'Pending'"
              title="Are you sure you want to delete this review?"
              @confirm="deleteReview(review.id)"
              confirm-button-text="Yes"
              cancel-button-text="No"
            >
              <template #reference>
                <el-button type="danger" size="small" :icon="Trash2"> Delete </el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>
      </el-card>
    </div>

    <!-- Pagination -->
    <el-pagination
      v-if="totalReviews > 0"
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[5, 10, 15]"
      :total="totalReviews"
      layout="total, sizes, prev, pager, next, jumper"
      class="mt-6 flex justify-center"
    />

    <!-- Empty State -->
    <el-empty v-else description="No reviews found" :image-size="200" />
  </PageLayout>
</template>

<script setup>
import PageLayout from '@/components/PageLayout.vue'
import { ref, computed, watch } from 'vue'
import { Check, X, Trash2, Search } from 'lucide-vue-next'
import { ElMessage, ElMessageBox } from 'element-plus'

const reviews = ref([
  {
    id: 1,
    productName: 'Laptop Pro 15',
    userName: 'John Doe',
    date: '2024-01-15',
    rating: 5,
    comment: 'Excellent laptop! Very fast and reliable. Perfect for programming and design work.',
    status: 'Approved',
  },
  {
    id: 2,
    productName: 'Wireless Mouse',
    userName: 'Jane Smith',
    date: '2024-01-16',
    rating: 4,
    comment: 'Good mouse, comfortable to use. Battery life could be better.',
    status: 'Pending',
  },
  {
    id: 3,
    productName: 'Cotton T-Shirt',
    userName: 'Bob Johnson',
    date: '2024-01-17',
    rating: 3,
    comment: 'Average quality. The size runs a bit small.',
    status: 'Pending',
  },
  {
    id: 4,
    productName: 'Programming Book',
    userName: 'Alice Brown',
    date: '2024-01-18',
    rating: 5,
    comment: 'Great book for learning Spring Framework! Clear explanations and good examples.',
    status: 'Approved',
  },
  {
    id: 5,
    productName: 'Coffee Maker',
    userName: 'Charlie Wilson',
    date: '2024-01-19',
    rating: 2,
    comment: 'Not satisfied. Stopped working after 2 weeks.',
    status: 'Rejected',
  },
  {
    id: 6,
    productName: 'Monitor 27"',
    userName: 'David Davis',
    date: '2024-01-20',
    rating: 4,
    comment: 'Great monitor with clear display. Perfect for work and gaming.',
    status: 'Approved',
  },
  {
    id: 7,
    productName: 'Mechanical Keyboard',
    userName: 'Eve Evans',
    date: '2024-01-21',
    rating: 5,
    comment: "Best keyboard I've ever used. Typing feels amazing.",
    status: 'Approved',
  },
  {
    id: 8,
    productName: 'Wireless Headphones',
    userName: 'Frank Miller',
    date: '2024-01-22',
    rating: 3,
    comment: 'Sound quality is good, but the fit could be better.',
    status: 'Pending',
  },
  {
    id: 9,
    productName: 'Tablet',
    userName: 'Grace Lee',
    date: '2024-01-23',
    rating: 4,
    comment: 'Great tablet for media consumption. Battery life is excellent.',
    status: 'Approved',
  },
  {
    id: 10,
    productName: 'Smart Watch',
    userName: 'Henry Taylor',
    date: '2024-01-24',
    rating: 2,
    comment: 'Disappointed with the battery life. Needs frequent charging.',
    status: 'Rejected',
  },
  {
    id: 11,
    productName: 'Gaming Console',
    userName: 'Ivy Moore',
    date: '2024-01-25',
    rating: 5,
    comment: 'Excellent gaming console with great graphics and performance.',
    status: 'Approved',
  },
  {
    id: 12,
    productName: 'Phone Case',
    userName: 'Jack Wilson',
    date: '2024-01-26',
    rating: 4,
    comment: 'Good phone case with nice design. Provides good protection.',
    status: 'Pending',
  },
  {
    id: 13,
    productName: 'Camera',
    userName: 'Kate Anderson',
    date: '2024-01-27',
    rating: 5,
    comment: 'Great camera with excellent image quality. Perfect for photography enthusiasts.',
    status: 'Approved',
  },
  {
    id: 14,
    productName: 'External Hard Drive',
    userName: 'Leo Thomas',
    date: '2024-01-28',
    rating: 3,
    comment: 'Average hard drive. Transfer speeds could be faster.',
    status: 'Pending',
  },
  {
    id: 15,
    productName: 'Wireless Speaker',
    userName: 'Mia Garcia',
    date: '2024-01-29',
    rating: 4,
    comment: 'Great wireless speaker with good sound quality. Easy to use.',
    status: 'Approved',
  },
])

const filterStatus = ref('All')
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(5)

const filteredReviews = computed(() => {
  let filtered = reviews.value

  // Status filter
  if (filterStatus.value !== 'All') {
    filtered = filtered.filter((review) => review.status === filterStatus.value)
  }

  // Search filter
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(
      (review) =>
        review.productName.toLowerCase().includes(query) ||
        review.userName.toLowerCase().includes(query) ||
        review.comment.toLowerCase().includes(query)
    )
  }

  return filtered
})

const totalReviews = computed(() => filteredReviews.value.length)

const paginatedReviews = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredReviews.value.slice(start, end)
})

// Reset page when filter or search changes
watch([filterStatus, searchQuery], () => {
  currentPage.value = 1
})

const getStatusTagType = (status) => {
  const types = {
    Approved: 'success',
    Pending: 'warning',
    Rejected: 'danger',
  }
  return types[status] || 'default'
}

const approveReview = (id) => {
  const review = reviews.value.find((r) => r.id === id)
  if (review) {
    review.status = 'Approved'
    ElMessage.success('Review approved successfully!')
  }
}

const rejectReview = (id) => {
  const review = reviews.value.find((r) => r.id === id)
  if (review) {
    review.status = 'Rejected'
    ElMessage.success('Review rejected successfully!')
  }
}

const deleteReview = (id) => {
  reviews.value = reviews.value.filter((r) => r.id !== id)
  ElMessage.success('Review deleted successfully!')
}
</script>

<style scoped>
.review-card {
  transition: transform 0.2s;
}

.review-card:hover {
  transform: translateY(-2px);
}
</style>
