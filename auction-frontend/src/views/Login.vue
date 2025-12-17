<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <h1>拍卖系统</h1>
        <p>欢迎使用拍卖系统，请登录</p>
      </div>
      <el-form :model="loginForm" label-width="0px" @submit.native.prevent="handleLogin">
        <el-form-item>
          <el-input 
            v-model="loginForm.username" 
            placeholder="请输入用户名" 
            size="large"
            prefix-icon="el-icon-user"
          />
        </el-form-item>
        <el-form-item>
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码" 
            size="large"
            prefix-icon="el-icon-lock"
          />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            size="large" 
            style="width: 100%" 
            @click="handleLogin"
            :loading="loginLoading"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { userApi } from '@/api/userApi'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: 'admin',
        password: 'admin',
        rememberMe: true
      },
      loginLoading: false
    }
  },
  methods: {
    async handleLogin() {
      try {
        this.loginLoading = true
        // 注意：这里应该调用认证API，但为了简化我们模拟登录
        // const response = await userApi.login(this.loginForm)
        
        // 模拟登录成功
        const user = {
          id: 1,
          username: this.loginForm.username,
          fullName: '系统管理员',
          userType: 1
        }
        
        // 使用 Vuex 存储用户信息并跳转到主布局页面
        this.$store.dispatch('user/login', user)
        this.$router.push('/layout')
        this.$message.success('登录成功')
      } catch (error) {
        this.$message.error('登录失败: ' + (error.message || '未知错误'))
      } finally {
        this.loginLoading = false
      }
    }
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.login-container {
  width: 400px;
  padding: 30px;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  color: #409EFF;
  margin-bottom: 10px;
}

.login-header p {
  color: #909399;
}
</style>