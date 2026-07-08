<template>
  <div class="user-manage">
    <div class="search-bar">
      <a-input-search
        v-model:value="nameOrPhone"
        placeholder="请输入用户名或手机号"
        style="width: 300px"
        @search="searchUser"
      />
      <a-button type="primary" @click="showAddModal" style="margin-left: 10px;">
        <template #icon><PlusOutlined /></template>
        添加用户
      </a-button>
      <a-button @click="resetSearch" style="margin-left: 10px;">
        <template #icon><ReloadOutlined /></template>
        重置
      </a-button>
    </div>

    <a-table
      :columns="columns"
      :data-source="pagedData"
      :pagination="false"
      :loading="loading"
      row-key="id"
      bordered
      :scroll="{ y: 500 }"
      style="margin-top: 30px"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'serialNumber'">
          {{ record.serialNumber }}
        </template>

        <template v-else-if="column.key === 'role'">
          <a-tag :color="record.role === 1 ? 'red' : 'blue'">
            {{ record.role === 1 ? '管理员' : '普通用户' }}
          </a-tag>
        </template>

        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="showEditModal(record)">
              <template #icon><EditOutlined /></template>
              编辑
            </a-button>
            <a-popconfirm
              title="确定要注销该用户账号吗？"
              @confirm="handleDelete(record.id)"
              ok-text="确定"
              cancel-text="取消"
            >
              <a-button type="link" size="small" danger>
                <template #icon><DeleteOutlined /></template>
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
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

    <!-- 添加用户模态框 -->
    <a-modal
      v-model:open="addModalVisible"
      title="添加用户"
      @ok="handleAddUser"
      @cancel="closeAddModal"
      width="500px"
    >
      <a-form
        ref="addFormRef"
        :model="addFormState"
        :rules="addRules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="用户名" name="userName">
          <a-input v-model:value="addFormState.userName" placeholder="请输入用户名" />
        </a-form-item>

        <a-form-item label="密码" name="password">
          <a-input-password v-model:value="addFormState.password" placeholder="请输入密码" />
        </a-form-item>

        <a-form-item label="手机号" name="phone">
          <a-input v-model:value="addFormState.phone" placeholder="请输入手机号" />
        </a-form-item>

        <a-form-item label="地址" name="address">
          <a-input v-model:value="addFormState.address" placeholder="请输入地址" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 编辑用户模态框 -->
    <a-modal
      v-model:open="editModalVisible"
      title="编辑用户"
      @ok="handleEditUser"
      @cancel="closeEditModal"
      width="500px"
    >
      <a-form
        ref="editFormRef"
        :model="editFormState"
        :rules="editRules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="用户名" name="userName">
          <a-input v-model:value="editFormState.userName" disabled />
        </a-form-item>

        <a-form-item label="密码" name="password">
          <a-input-password v-model:value="editFormState.password" placeholder="请输入密码" />
        </a-form-item>

        <a-form-item label="手机号" name="phone">
          <a-input v-model:value="editFormState.phone" placeholder="请输入手机号" />
        </a-form-item>

        <a-form-item label="地址" name="address">
          <a-input v-model:value="editFormState.address" placeholder="请输入地址" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { message } from 'ant-design-vue'
import axiosInstance from '../axios'
import { PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined } from '@ant-design/icons-vue'

const users = ref([])
const nameOrPhone = ref('')
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(5)
const total = ref(0)

// 添加用户相关
const addModalVisible = ref(false)
const addFormRef = ref(null)
const addFormState = reactive({
  userName: '',
  password: '',
  phone: '',
  address: ''
})

// 编辑用户相关
const editModalVisible = ref(false)
const editFormRef = ref(null)
const editFormState = reactive({
  id: null,
  userName: '',
  password: '',
  phone: '',
  address: ''
})

const columns = [
  { title: '编号', key: 'serialNumber', width: 100, align: 'center' },
  { title: '用户名', dataIndex: 'userName', key: 'userName', width: 150, align: 'center' },
  { title: '密码', dataIndex: 'password', key: 'password', width: 150, align: 'center' },
  { title: '手机号', dataIndex: 'phone', key: 'phone', width: 150, align: 'center' },
  { title: '地址', dataIndex: 'address', key: 'address', align: 'center' },
  { title: '角色', key: 'role', width: 120, align: 'center' },
  { title: '操作', key: 'action', width: 150, align: 'center' }
]

// 添加表单验证规则
const addRules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 10, message: '用户名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度在 6 到 16 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入地址', trigger: 'blur' }
  ]
}

// 编辑表单验证规则
const editRules = {
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度在 6 到 16 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入地址', trigger: 'blur' }
  ]
}

// 分页后的数据（带连续编号）
const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return users.value.slice(start, end).map((user, index) => ({
    ...user,
    serialNumber: start + index + 1
  }))
})

// 处理页码变化
const handlePageChange = (page) => {
  currentPage.value = page
}

// 处理每页显示数量的变化
const handleSizeChange = (current, size) => {
  pageSize.value = size
  currentPage.value = 1
}

// 加载用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    const data = await axiosInstance.get('/user/all')
    users.value = data || []
    total.value = users.value.length
  } catch (error) {
    console.error('加载用户失败:', error)
    message.error('加载用户失败')
  } finally {
    loading.value = false
  }
}

// 搜索用户
const searchUser = async () => {
  if (!nameOrPhone.value) {
    await loadUsers()
    return
  }

  loading.value = true
  try {
    const data = await axiosInstance.get('/user/search', {
      params: { nameOrPhone: nameOrPhone.value }
    })
    users.value = data || []
    total.value = users.value.length
    currentPage.value = 1
  } catch (error) {
    console.error('搜索用户失败:', error)
    message.error('搜索用户失败')
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  nameOrPhone.value = ''
  loadUsers()
}

// 显示添加用户模态框
const showAddModal = () => {
  addModalVisible.value = true
  addFormState.userName = ''
  addFormState.password = ''
  addFormState.phone = ''
  addFormState.address = ''
}

// 关闭添加用户模态框
const closeAddModal = () => {
  addModalVisible.value = false
  addFormRef.value?.resetFields()
}

// 处理添加用户
const handleAddUser = async () => {
  try {
    await addFormRef.value.validate()

    const data = {
      userName: addFormState.userName,
      password: addFormState.password,
      phone: addFormState.phone,
      address: addFormState.address,
      role: 2 // 默认为普通用户
    }

    const result = await axiosInstance.post('/user/add', data)
    if (result === '添加成功') {
      message.success('添加成功')
      closeAddModal()
      await loadUsers()
    } else {
      message.error(result)
    }
  } catch (error) {
    if (error.errorFields) {
      return // 表单验证失败
    }
    console.error('添加用户失败:', error)
    message.error('添加用户失败：' + (error.message || '未知错误'))
  }
}

// 显示编辑用户模态框
const showEditModal = (record) => {
  editModalVisible.value = true
  editFormState.id = record.id
  editFormState.userName = record.userName
  editFormState.password = record.password || ''
  editFormState.phone = record.phone
  editFormState.address = record.address
}

// 关闭编辑用户模态框
const closeEditModal = () => {
  editModalVisible.value = false
  editFormRef.value?.resetFields()
}

// 处理编辑用户
const handleEditUser = async () => {
  try {
    await editFormRef.value.validate()

    const data = {
      id: editFormState.id,
      userName: editFormState.userName,
      password: editFormState.password,
      phone: editFormState.phone,
      address: editFormState.address
    }

    const result = await axiosInstance.post('/user/update', data)
    if (result === '更新成功') {
      message.success('更新成功')
      closeEditModal()
      await loadUsers()
    } else {
      message.error(result)
    }
  } catch (error) {
    if (error.errorFields) {
      return // 表单验证失败
    }
    console.error('更新用户失败:', error)
    message.error('更新用户失败：' + (error.message || '未知错误'))
  }
}

// 处理删除用户
const handleDelete = async (id) => {
  try {
    const result = await axiosInstance.get('/user/delete', { params: { id } })
    if (result === '删除成功') {
      message.success('删除成功')
      await loadUsers()
    } else {
      message.error(result)
    }
  } catch (error) {
    console.error('删除失败:', error)
    message.error('删除失败：' + (error.message || '未知错误'))
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-manage {
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
