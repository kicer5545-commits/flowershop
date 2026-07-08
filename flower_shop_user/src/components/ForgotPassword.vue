<template>
    <div class="forgot-container">
        <div class="forgot-card">
            <h2>找回密码</h2>
            <div v-if="!verified">
                <a-form :model="verifyForm" @finish="handleVerify">
                    <a-form-item label="用户名" name="userName" :rules="[{ required: true, message: '请输入用户名' }]">
                        <a-input v-model:value="verifyForm.userName" placeholder="请输入用户名" />
                    </a-form-item>
                    <a-form-item label="手机号" name="phone" :rules="[{ required: true, message: '请输入手机号' }]">
                        <a-input v-model:value="verifyForm.phone" placeholder="注册时使用的手机号" />
                    </a-form-item>
                    <a-form-item>
                        <a-button type="primary" html-type="submit" block>验证身份</a-button>
                    </a-form-item>
                </a-form>
            </div>
            <div v-else>
                <a-form :model="resetForm" @finish="handleReset">
                    <a-form-item label="新密码" name="newPassword" :rules="[{ required: true, message: '请输入新密码' }, { min: 6, message: '密码至少6位' }]">
                        <a-input-password v-model:value="resetForm.newPassword" placeholder="6-16位新密码" />
                    </a-form-item>
                    <a-form-item label="确认密码" name="confirmPassword" :rules="[{ required: true, message: '请确认密码' }]">
                        <a-input-password v-model:value="resetForm.confirmPassword" placeholder="再次输入新密码" />
                    </a-form-item>
                    <a-form-item>
                        <a-button type="primary" html-type="submit" block>重置密码</a-button>
                    </a-form-item>
                </a-form>
            </div>
            <div class="links">
                <router-link to="/login">返回登录</router-link>
            </div>
        </div>
    </div>
</template>

<script setup>
    import { ref } from 'vue'
    import { useRouter } from 'vue-router'
    import { message } from 'ant-design-vue'
    import axiosInstance from '../axios'

    const router = useRouter()
    const verified = ref(false)
    const verifyForm = ref({ userName: '', phone: '' })
    const resetForm = ref({ newPassword: '', confirmPassword: '' })
    const tempUser = ref('')

    const handleVerify = async () => {
        try {
            const res = await axiosInstance.get('/forgot/verify', {
                params: {
                    userName: verifyForm.value.userName,   // 注意是 userName 不是 username
                    phone: verifyForm.value.phone
                }
            })
            if (res === true) {
                verified.value = true
                tempUser.value = verifyForm.value.userName
                message.success('身份验证通过，请设置新密码')
            } else {
                message.error('用户名与手机号不匹配')
            }
        } catch (error) {
            console.error('验证失败', error)
            message.error('验证失败，请重试')
        }
    }

    const handleReset = async () => {
        if (resetForm.value.newPassword !== resetForm.value.confirmPassword) {
            message.error('两次输入的密码不一致')
            return
        }
        try {
            const res = await axiosInstance.post('/forgot/reset', {
                userName: tempUser.value,
                phone: verifyForm.value.phone,
                newPassword: resetForm.value.newPassword
            })
            if (res === '重置成功') {
                message.success('密码重置成功，请重新登录')
                router.push('/login')
            } else {
                message.error(res)
            }
        } catch (error) {
            console.error('重置失败', error)
            message.error('重置失败')
        }
    }
</script>

<style scoped>
    .forgot-container {
        min-height: 100vh;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        display: flex;
        justify-content: center;
        align-items: center;
    }
    .forgot-card {
        width: 450px;
        padding: 40px;
        background: white;
        border-radius: 8px;
        box-shadow: 0 2px 12px rgba(0,0,0,0.1);
    }
    .forgot-card h2 {
        text-align: center;
        color: #52c41a;
        margin-bottom: 30px;
    }
    .links {
        text-align: center;
        margin-top: 20px;
    }
    .links a {
        color: #52c41a;
        text-decoration: none;
    }
</style>