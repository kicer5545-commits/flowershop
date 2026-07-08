<template>
  <div class="product-manage">
    <div class="table-header">
      <h2>产品列表</h2>
      <div class="table-actions">
        <a-input-search
          v-model:value="searchText"
          placeholder="搜索产品名称"
          style="width: 200px"
          @search="handleSearch"
        />
        <a-select
          v-model:value="selectedCategory"
          placeholder="筛选分类"
          style="width: 150px; margin-left: 10px"
          @change="filterByCategory"
        >
          <a-select-option value="all">全部分类</a-select-option>
          <a-select-option value="rose">玫瑰系列</a-select-option>
          <a-select-option value="lily">百合系列</a-select-option>
          <a-select-option value="ChineseRose">月季系列</a-select-option>
          <a-select-option value="chrysanthemum">菊花系列</a-select-option>
          <a-select-option value="carnation">康乃馨</a-select-option>
          <a-select-option value="orchid">兰花系列</a-select-option>
          <a-select-option value="birthday">生日花束</a-select-option>
          <a-select-option value="valentine">情人节</a-select-option>
          <a-select-option value="wedding">婚礼花艺</a-select-option>
          <a-select-option value="indoor">绿植盆栽</a-select-option>
        </a-select>
        <a-button type="primary" @click="showAddModal" style="margin-left: 10px;">
          <template #icon><PlusOutlined /></template>
          添加鲜花
        </a-button>
        <a-button @click="resetSearch" style="margin-left: 10px;">
          <template #icon><ReloadOutlined /></template>
          重置
        </a-button>
        <a-button @click="showImportModal" style="margin-left: 10px;">
          <template #icon><UploadOutlined /></template>
          批量导入
        </a-button>
      </div>
    </div>

    <a-table
      :columns="columns"
      :data-source="displayProducts"
      :pagination="{ pageSize: 8, showTotal: (total) => `共 ${total} 条记录` }"
      :loading="loading"
      row-key="id"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'serialNumber'">
          {{ record.serialNumber }}
        </template>

        <template v-else-if="column.key === 'image'">
          <a-image
            :width="60"
            :src="record.image"
            :preview="false"
            style="border-radius: 4px;"
          />
        </template>

        <template v-else-if="column.key === 'category'">
          <a-tag :color="getCategoryColor(record.category)">
            {{ getCategoryLabel(record.category) }}
          </a-tag>
        </template>

        <template v-else-if="column.key === 'price'">
          <span style="color: #ff4d4f; font-weight: bold;">
            ¥{{ record.price }}
          </span>
        </template>

        <template v-else-if="column.key === 'stock'">
          <a-tag :color="record.stock > 10 ? 'green' : 'orange'">
            {{ record.stock > 0 ? `库存 ${record.stock}` : '缺货' }}
          </a-tag>
        </template>

        <template v-else-if="column.key === 'rating'">
          <a-rate :value="record.rating" disabled allow-half />
        </template>

        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="showEditModal(record)">
              <template #icon><EditOutlined /></template>
              编辑
            </a-button>
            <a-popconfirm title="确定删除吗？" @confirm="handleDelete(record.id)">
              <a-button type="link" size="small" danger>
                <template #icon><DeleteOutlined /></template>
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal v-model:open="modalVisible" :title="modalTitle" @ok="handleSave" width="600px">
      <a-form :model="productForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="商品名称">
          <a-input v-model:value="productForm.name" placeholder="请输入鲜花名称" />
        </a-form-item>

        <a-form-item label="分类">
          <a-select v-model:value="productForm.category" placeholder="选择分类">
            <a-select-option value="rose">玫瑰系列</a-select-option>
            <a-select-option value="lily">百合系列</a-select-option>
            <a-select-option value="ChineseRose">月季系列</a-select-option>
            <a-select-option value="chrysanthemum">菊花系列</a-select-option>
            <a-select-option value="carnation">康乃馨</a-select-option>
            <a-select-option value="orchid">兰花系列</a-select-option>
            <a-select-option value="birthday">生日花束</a-select-option>
            <a-select-option value="valentine">情人节</a-select-option>
            <a-select-option value="wedding">婚礼花艺</a-select-option>
            <a-select-option value="indoor">绿植盆栽</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="价格">
          <a-input-number v-model:value="productForm.price" :min="0" :max="9999" :precision="2" style="width: 100%" addon-before="¥" placeholder="请输入价格" />
        </a-form-item>

        <a-form-item label="库存">
          <a-input-number v-model:value="productForm.stock" :min="0" :max="999" style="width: 100%" placeholder="请输入库存数量" />
        </a-form-item>

        <a-form-item label="评分">
          <a-input-number v-model:value="productForm.rating" :min="0" :max="5" :step="0.1" :precision="1" style="width: 100%" placeholder="请输入评分（0-5）" />
        </a-form-item>

        <a-form-item label="产品图片">
          <div class="image-upload-container">
            <a-upload
              list-type="picture-card"
              :file-list="uploadFileList"
              :before-upload="beforeUpload"
              @change="handleImageChange"
              :max-count="1"
            >
              <div v-if="uploadFileList.length === 0">
                <PlusOutlined />
                <div style="margin-top: 8px">上传图片</div>
              </div>
            </a-upload>
          </div>
          <p style="color: #999; font-size: 12px; margin-top: 4px;">
            支持 JPG、PNG 格式，建议尺寸 500x500px
          </p>
        </a-form-item>

        <a-form-item label="描述">
          <a-textarea v-model:value="productForm.description" :rows="4" show-count :maxlength="200" placeholder="请输入产品描述" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="importModalVisible"
      title="批量导入商品"
      @ok="handleImport"
      @cancel="handleImportCancel"
      :confirm-loading="importLoading"
    >
      <a-alert
        message="导入说明"
        description="请按照模板格式准备Excel文件，支持.xlsx和.xls格式。第一行为表头，从第二行开始填写数据。"
        type="info"
        show-icon
        style="margin-bottom: 16px"
      />

      <a-upload
        v-model:file-list="importFileList"
        :before-upload="beforeUploadExcel"
        :custom-request="uploadFile"
        accept=".xlsx,.xls"
        :max-count="1"
        :disabled="importLoading"
      >
        <a-button :disabled="importLoading">
          <UploadOutlined /> 选择Excel文件
        </a-button>
      </a-upload>

      <div style="margin-top: 16px;">
        <a href="#" @click.prevent="downloadTemplate">下载导入模板</a>
      </div>

      <div v-if="importResult" style="margin-top: 16px;">
        <a-alert
          :message="importResult.success ? '导入成功' : '导入失败'"
          :description="importResult.message"
          :type="importResult.success ? 'success' : 'error'"
          show-icon
        />
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import axiosInstance from '../axios'
import { PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined, UploadOutlined } from '@ant-design/icons-vue'

const products = ref([])
const searchText = ref('')
const selectedCategory = ref('all')
const loading = ref(false)
const modalVisible = ref(false)
const modalTitle = ref('添加商品')
const isEdit = ref(false)
const importModalVisible = ref(false)
const importFileList = ref([])
const uploadFileList = ref([])
const selectedFile = ref(null)
const importLoading = ref(false)
const importResult = ref(null)

const productForm = ref({
  id: 0,
  name: '',
  category: '',
  price: 0,
  stock: 0,
  rating: 0,
  description: '',
  image: ''
})

const categoryConfig = {
  rose: { label: '玫瑰系列', color: 'red' },
  lily: { label: '百合系列', color: 'pink' },
  ChineseRose: { label: '月季系列', color: 'purple' },
  chrysanthemum: { label: '菊花系列', color: 'orange' },
  carnation: { label: '康乃馨', color: 'cyan' },
  orchid: { label: '兰花系列', color: 'blue' },
  birthday: { label: '生日花束', color: 'volcano' },
  valentine: { label: '情人节', color: 'magenta' },
  wedding: { label: '婚礼花艺', color: 'gold' },
  indoor: { label: '绿植盆栽', color: 'green' }
}

const columns = [
  { title: '编号', key: 'serialNumber', width: 80, align: 'center' },
  { title: '图片', key: 'image', width: 100 },
  { title: '名称', dataIndex: 'name', key: 'name', width: 200 },
  { title: '分类', key: 'category', width: 120 },
  { title: '价格', key: 'price', width: 100, sorter: (a, b) => a.price - b.price },
  { title: '库存', key: 'stock', width: 100, sorter: (a, b) => a.stock - b.stock },
  { title: '评分', key: 'rating', width: 120, sorter: (a, b) => a.rating - b.rating },
  { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
  { title: '操作', key: 'action', width: 200 }
]

const filteredProducts = computed(() => {
  let filtered = [...products.value]

  if (selectedCategory.value !== 'all') {
    filtered = filtered.filter(product => product.category === selectedCategory.value)
  }

  if (searchText.value) {
    const keyword = searchText.value.toLowerCase()
    filtered = filtered.filter(product =>
      product.name.toLowerCase().includes(keyword) ||
      (product.description && product.description.toLowerCase().includes(keyword))
    )
  }

  return filtered
})

const displayProducts = computed(() => {
  return filteredProducts.value.map((product, index) => ({
    ...product,
    serialNumber: index + 1
  }))
})

const getCategoryLabel = (value) => {
  return categoryConfig[value] ? categoryConfig[value].label : value
}

const getCategoryColor = (value) => {
  return categoryConfig[value] ? categoryConfig[value].color : 'default'
}

const filterByCategory = () => {
  console.log('筛选分类:', selectedCategory.value)
}

const resetSearch = () => {
  searchText.value = ''
  selectedCategory.value = 'all'
  message.success('已重置搜索条件')
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    message.error('只能上传图片文件！')
    return false
  }

  if (!isLt5M) {
    message.error('图片大小不能超过5MB！')
    return false
  }

  selectedFile.value = file

  const reader = new FileReader()
  reader.onload = (e) => {
    productForm.value.image = e.target.result
  }
  reader.readAsDataURL(file)

  return false
}

const handleImageChange = (info) => {
  const { file } = info
  if (file.status === 'removed') {
    productForm.value.image = ''
    selectedFile.value = null
  }
  uploadFileList.value = info.fileList
}

const loadProducts = async () => {
  loading.value = true
  try {
    const data = await axiosInstance.get('/products')
    products.value = data
  } catch (error) {
    console.error('加载商品失败:', error)
  } finally {
    loading.value = false
  }
}

const showAddModal = () => {
  isEdit.value = false
  modalTitle.value = '添加商品'
  productForm.value = {
    id: 0,
    name: '',
    category: '',
    price: 0,
    stock: 0,
    rating: 0,
    description: '',
    image: ''
  }
  uploadFileList.value = []
  selectedFile.value = null
  modalVisible.value = true
}

const showEditModal = (record) => {
  isEdit.value = true
  modalTitle.value = '编辑商品'
  productForm.value = { ...record }
  uploadFileList.value = []
  selectedFile.value = null

  if (productForm.value.image) {
    let imageUrl = productForm.value.image

    if (!imageUrl.startsWith('http') && !imageUrl.startsWith('/api')) {
      imageUrl = imageUrl
    }

    uploadFileList.value = [{
      uid: '-1',
      name: imageUrl.substring(imageUrl.lastIndexOf('/') + 1),
      status: 'done',
      url: imageUrl,
      thumbUrl: imageUrl
    }]
  }

  modalVisible.value = true
}

const uploadImage = async () => {
  if (!selectedFile.value) {
    return productForm.value.image
  }

  const formData = new FormData()
  formData.append('file', selectedFile.value)

  try {
    const res = await axiosInstance.post('/upload/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    if (res.success) {
      const imageUrl = res.data.url
      console.log('上传成功，图片路径:', imageUrl)
      return imageUrl
    } else {
      throw new Error(res.message || '图片上传失败')
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    message.error('图片上传失败：' + (error.message || '未知错误'))
    throw error
  }
}

const handleSave = async () => {
  try {
    if (selectedFile.value) {
      productForm.value.image = await uploadImage()
    }

    if (isEdit.value) {
      await axiosInstance.post('/product/update', productForm.value)
      message.success('更新成功')
    } else {
      await axiosInstance.post('/product/add', productForm.value)
      message.success('添加成功')
    }
    modalVisible.value = false
    await loadProducts()
  } catch (error) {
    console.error('保存失败:', error)
    message.error('保存失败：' + (error.message || '未知错误'))
  }
}

const handleDelete = async (id) => {
  try {
    await axiosInstance.get('/product/delete', { params: { id } })
    message.success('删除成功')
    await loadProducts()
  } catch (error) {
    console.error('删除失败:', error)
    message.error('删除失败')
  }
}

const handleSearch = () => {
  console.log('搜索关键词:', searchText.value)
}

onMounted(() => {
  loadProducts()
})

const showImportModal = () => {
  importModalVisible.value = true
  importFileList.value = []
  importResult.value = null
  importLoading.value = false
}

const handleImportCancel = () => {
  importModalVisible.value = false
  importFileList.value = []
  importResult.value = null
  importLoading.value = false
}

const beforeUploadExcel = (file) => {
  const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
                  file.type === 'application/vnd.ms-excel' ||
                  file.name.endsWith('.xlsx') ||
                  file.name.endsWith('.xls')

  if (!isExcel) {
    message.error('只能上传Excel文件(.xlsx, .xls)!')
    return false
  }

  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    message.error('文件大小不能超过10MB!')
    return false
  }

  return false
}

const uploadFile = async ({ file, onSuccess, onError }) => {
  importLoading.value = true
  importResult.value = null

  const formData = new FormData()
  formData.append('file', file)

  try {
    const res = await axiosInstance.post('/admin/importProducts', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    importResult.value = {
      success: res.success,
      message: res.message
    }

    if (res.success) {
      message.success(res.message)
      onSuccess(res)
      await loadProducts()
    } else {
      message.error(res.message)
      onError(new Error(res.message))
    }
  } catch (error) {
    console.error('导入失败:', error)
    const errorMsg = error.response?.data?.message || error.message || '导入失败'
    importResult.value = {
      success: false,
      message: errorMsg
    }
    message.error(errorMsg)
    onError(error)
  } finally {
    importLoading.value = false
  }
}

const handleImport = () => {
  if (importFileList.value.length === 0) {
    message.warning('请先选择要导入的Excel文件')
    return
  }

  if (importLoading.value) {
    return
  }

  const file = importFileList.value[0].originFileObj || importFileList.value[0]
  uploadFile({ file, onSuccess: () => {}, onError: () => {} })
}

const downloadTemplate = () => {
  const template = `名称,分类,价格,描述,图片URL,库存,评分
艾莎玫瑰,rose,299.00,白粉撞色显雅致，寓意守护与偏爱,/images/roses/艾莎玫瑰.jpg,50,4.8
白雪山,lily,399.00,优选鲜切白雪山玫瑰，洁白无瑕质感佳,/images/lilys/白雪山.jpg,30,4.9`

  const blob = new Blob(["\uFEFF" + template], { type: "text/csv;charset=utf-8;" })
  const link = document.createElement("a")
  const url = URL.createObjectURL(blob)
  link.href = url
  link.setAttribute("download", "商品导入模板.csv")
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
  message.success('模板下载成功')
}
</script>

<style scoped>
.product-manage {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.table-header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-bottom: 16px;
  gap: 20px;
}

.table-header h2 {
  margin: 0;
  color: #333;
  white-space: nowrap;
}

.table-actions {
  display: flex;
  gap: 10px;
  align-items: center;
  flex: 1;
}

.image-upload-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

@media (max-width: 768px) {
  .table-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .table-actions {
    width: 100%;
    flex-direction: column;
  }
}
</style>
