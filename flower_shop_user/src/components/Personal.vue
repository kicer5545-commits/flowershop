<template>
  <div class="personal-page">
    <Header />

    <div class="container">
      <h2>个人中心</h2>

      <div class="profile-card">
        <a-form :model="userForm" @finish="handleUpdate">
          <a-form-item label="用户名">
            <a-input v-model:value="userForm.userName" disabled />
          </a-form-item>

          <a-form-item label="手机号" name="phone">
            <a-input v-model:value="userForm.phone" placeholder="请输入手机号" />
          </a-form-item>

          <a-form-item label="地址" name="address">
            <a-textarea v-model:value="userForm.address" placeholder="请输入地址" :rows="3" />
          </a-form-item>

          <a-form-item>
            <a-button type="primary" html-type="submit">保存修改</a-button>
            <a-button style="margin-left: 10px" @click="showPwdModal = true">
              修改密码
            </a-button>
          </a-form-item>

          <div style="text-align: center; margin-top: 16px;">
            <router-link to="/favorites" class="favorites-link">❤️ 我的收藏</router-link>
          </div>
        </a-form>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <a-modal
        v-model:open="showPwdModal"
        title="修改密码"
        :mask-closable="false"
        :footer="null"
    >
      <a-form :model="pwdForm" @finish="handleChangePwd" :rules="pwdRules" layout="vertical">
        <a-form-item label="原密码" name="oldPassword">
          <a-input-password v-model:value="pwdForm.oldPassword" placeholder="请输入当前密码" />
        </a-form-item>

        <a-form-item label="新密码" name="newPassword">
          <a-input-password v-model:value="pwdForm.newPassword" placeholder="6-16位新密码" />
        </a-form-item>

        <a-form-item label="确认新密码" name="confirmPassword">
          <a-input-password v-model:value="pwdForm.confirmPassword" placeholder="再次输入新密码" />
        </a-form-item>

        <a-form-item style="margin-bottom: 0">
          <a-button type="primary" html-type="submit" block>确认修改</a-button>
        </a-form-item>
      </a-form>
    </a-modal>

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Header from './Header.vue'
import Footer from './Footer.vue'
import { message } from 'ant-design-vue'

const router = useRouter()

// 个人信息
const userForm = ref({
  id: 0,
  userName: '',
  phone: '',
  address: '',
  password: ''
})

// 密码弹窗
const showPwdModal = ref(false)

// 密码表单
const pwdForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 校验规则
const pwdRules = ref({
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度6-16位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_, value) => {
        if (value !== pwdForm.value.newPassword) return Promise.reject('两次密码不一致')
        return Promise.resolve()
      },
      trigger: 'blur'
    }
  ]
})

// 加载用户
const loadUserInfo = () => {
  const user = JSON.parse(localStorage.getItem('user'))
  if (!user) {
    router.push('/login')
    return
  }
  userForm.value = { ...user }
}

// 保存个人信息
const handleUpdate = () => {
  localStorage.setItem('user', JSON.stringify(userForm.value))
  message.success('保存成功')
}

// 纯前端修改密码
const handleChangePwd = () => {
  // 1. 验证原密码
  if (pwdForm.value.oldPassword !== userForm.value.password) {
    message.error('原密码错误')
    return
  }

  // 2. 更新密码
  userForm.value.password = pwdForm.value.newPassword
  localStorage.setItem('user', JSON.stringify(userForm.value))

  message.success('密码修改成功，请重新登录')
  showPwdModal.value = false

  // 3. 退出重新登录
  setTimeout(() => {
    localStorage.clear()
    router.push('/login')
  }, 1000)
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.personal-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.container {
  max-width: 800px;
  margin: 30px auto;
  padding: 0 20px;
}

.container h2 {
  margin-bottom: 30px;
  color: #333;
}

.profile-card {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.favorites-link {
  color: #52c41a;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.favorites-link:hover {
  color: #ff9494;
  text-decoration: underline;
}
</style>