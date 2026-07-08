<template>
  <div class="order-manage">
    <div class="search-bar">
      <a-input
          v-model:value="searchUserName"
          placeholder="请输入用户姓名"
          style="width: 200px"
      />
      &nbsp;
      <a-select
          v-model:value="orderStatus"
          placeholder="请选择订单状态"
          style="width: 150px"
      >
        <a-select-option value="">全部订单状态</a-select-option>
        <a-select-option value="0">未支付</a-select-option>
        <a-select-option value="1">已支付</a-select-option>
        <a-select-option value="2">已发货</a-select-option>
        <a-select-option value="3">已完成</a-select-option>
      </a-select>
      &nbsp;
      <a-button type="primary" @click="getOrdersByParams">
        <template #icon>
          <SearchOutlined/>
        </template>
        搜索
      </a-button>
      <a-button @click="reset">重置</a-button>
    </div>

    <a-table
        :data-source="currentData"
        :columns="columns"
        :pagination="false"
        bordered
        :scroll="{ y: 500 }"
        row-key="id"
        style="margin-top: 30px"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="getStatusColor(record.status)">
            {{ getStatusText(record.status) }}
          </a-tag>
        </template>

        <template v-else-if="column.key === 'action'">
          <a-popconfirm
              title="确认发货？"
              ok-text="确认"
              cancel-text="取消"
              @confirm="delivery(record)"
              v-if="record.status === '1'"
          >
            <a-button type="primary" size="small">发货</a-button>
          </a-popconfirm>
          <span v-else-if="record.status === '0'" style="color: #faad14;">待支付</span>
          <span v-else-if="record.status === '2'" style="color: #52c41a;">已发货</span>
          <span v-else style="color: #999;">已完成</span>
        </template>
      </template>
    </a-table>

    <!-- 分页 -->
    <a-pagination
        v-model:current="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-size-options="['5', '10', '20']"
        show-size-changer
        show-quick-jumper
        :show-total="(total) => `共 ${total} 条`"
        style="margin-top: 20px"
        @change="handlePageChange"
        @showSizeChange="handleSizeChange"
    />
  </div>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue'
import {message} from 'ant-design-vue'
import axiosInstance from '../axios'
import {SearchOutlined} from '@ant-design/icons-vue'

const orders = ref([])
const searchUserName = ref('')
const orderStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(5)
const total = ref(0)

const columns = [
  {title: '编号', dataIndex: 'id', key: 'id', width: 100, align: 'center'},
  {title: '商品详情', dataIndex: 'productDetails', key: 'productDetails', width: 380, align: 'center'},
  {
    title: '价格',
    key: 'price',
    width: 120,
    align: 'center',
    customRender: ({record}) => {
      let priceValue = record.price
      if (priceValue && typeof priceValue === 'object') {
        if (priceValue.scale !== undefined && priceValue.unscaledValue !== undefined) {
          const unscaled = BigInt(priceValue.unscaledValue)
          const scale = priceValue.scale
          priceValue = (Number(unscaled) / Math.pow(10, scale)).toFixed(2)
        } else if (priceValue.toString) {
          priceValue = priceValue.toString()
        } else {
          priceValue = '0.00'
        }
      }
      return `¥${priceValue}`
    }
  },
  {title: '收货信息', key: 'address', width: 250, align: 'center', dataIndex: 'address'},
  {title: '订单状态', key: 'status', width: 150, align: 'center'},
  {title: '下单用户', key: 'userName', width: 150, align: 'center', dataIndex: 'userName'},
  {title: '下单时间', dataIndex: 'time', key: 'time', width: 200, align: 'center'},
  {title: '操作', key: 'action', width: 150, align: 'center'}
]

const currentData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return orders.value.slice(start, end)
})

const handlePageChange = (page) => {
  currentPage.value = page
}

const handleSizeChange = (current, size) => {
  pageSize.value = size
  currentPage.value = 1
}

const getStatusText = (status) => {
  const statusMap = {
    '0': '未支付',
    '1': '已支付',
    '2': '已发货',
    '3': '已完成'
  }
  return statusMap[status] || '未知状态'
}

const getStatusColor = (status) => {
  const colorMap = {
    '0': 'orange',
    '1': 'blue',
    '2': 'green',
    '3': 'default'
  }
  return colorMap[status] || 'default'
}

const loadOrders = async () => {
  try {
    const data = await axiosInstance.get('/order/all')
    orders.value = data || []
    total.value = orders.value.length
  } catch (error) {
    console.error('加载订单失败:', error)
    message.error('加载订单失败')
  }
}

const getOrdersByParams = async () => {
  try {
    const params = {}
    if (searchUserName.value) {
      params.userName = searchUserName.value
    }
    if (orderStatus.value !== '') {
      params.status = orderStatus.value
    }

    const data = await axiosInstance.get('/order/search', {params})
    orders.value = data || []
    total.value = orders.value.length
    currentPage.value = 1
  } catch (error) {
    console.error('搜索订单失败:', error)
    message.error('搜索订单失败')
  }
}

const reset = () => {
  searchUserName.value = ''
  orderStatus.value = ''
  loadOrders()
}

const delivery = async (order) => {
  try {
    const result = await axiosInstance.post('/order/delivery', {id: order.id})
    if (result === '更新成功') {
      message.success('发货成功')
      await loadOrders()
    } else {
      message.error(result || '发货失败')
    }
  } catch (error) {
    console.error('发货失败:', error)
    message.error('发货失败')
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-manage {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.search-bar {
  margin-bottom: 20px;
}

:deep(.ant-table) {
  font-size: 14px;
}
</style>
