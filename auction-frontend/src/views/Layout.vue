<template>
  <el-container v-if="isLoggedIn" style="min-height: 100vh;">
    <el-header style="background-color: #409EFF; color: white; display: flex; align-items: center; justify-content: space-between;">
      <h1 style="margin: 0;">拍卖系统</h1>
      <div style="display: flex; align-items: center; gap: 20px;">
        <span>欢迎，{{ currentUser.fullName }} ({{ getUserTypeText(currentUser.userType) }})</span>
        <el-dropdown>
          <span class="el-dropdown-link" style="color: white; cursor: pointer;">
            <el-avatar :size="30" style="margin-right: 5px;">{{ currentUser.username.charAt(0).toUpperCase() }}</el-avatar>
            <i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </el-header>
    
    <el-container>
      <el-aside width="200px" style="background-color: #f5f5f5;">
        <el-menu :default-active="$route.path" router>
          <el-menu-item index="/assets">
            <i class="el-icon-goods"></i>
            <span>资产列表</span>
          </el-menu-item>
          <el-menu-item index="/auctions">
            <i class="el-icon-time"></i>
            <span>拍卖列表</span>
          </el-menu-item>
          <el-menu-item index="/my-bids">
            <i class="el-icon-document"></i>
            <span>我的出价</span>
          </el-menu-item>
          <el-menu-item index="/orders">
            <i class="el-icon-list"></i>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="/config">
            <i class="el-icon-setting"></i>
            <span>配置管理</span>
          </el-menu-item>
          <el-menu-item index="/scheduled-tasks">
            <i class="el-icon-alarm-clock"></i>
            <span>定时任务</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-main style="padding: 20px;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
  
  <router-view v-else />
</template>

<script>
export default {
  name: 'Layout',
  computed: {
    isLoggedIn() {
      return this.$store.state.user.isLoggedIn
    },
    currentUser() {
      return this.$store.state.user.currentUser
    },
    activeMenu() {
      return this.$store.state.app.activeMenu
    }
  },
  methods: {
    logout() {
      this.$store.dispatch('user/logout')
      this.$router.push('/login')
    },
    setActiveMenu(menu) {
      this.$store.dispatch('app/setActiveMenu', menu)
    },
    getUserTypeText(type) {
      const typeMap = {
        1: '管理员',
        2: '运营人员',
        3: '卖家',
        4: '买家'
      }
      return typeMap[type] || '未知'
    }
  },
  watch: {
    '$route'(to) {
      this.setActiveMenu(to.path)
    }
  },
  mounted() {
    // 初始化时设置当前菜单
    this.setActiveMenu(this.$route.path)
  }
}
</script>