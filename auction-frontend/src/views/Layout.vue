<template>
  <el-container v-if="isLoggedIn" style="min-height: 100vh;" class="auction-layout">
    <el-header class="auction-header">
      <div class="header-content">
        <div class="logo-section">
          <i class="el-icon-s-shop logo-icon"></i>
          <h1 class="system-title">拍卖系统</h1>
        </div>
        <div class="user-section">
          <span class="welcome-text">欢迎，{{ currentUser.fullName }} ({{ getUserTypeText(currentUser.userType) }})</span>
          <el-dropdown>
            <span class="el-dropdown-link user-profile">
              <el-avatar :size="30" class="user-avatar">{{ currentUser.username.charAt(0).toUpperCase() }}</el-avatar>
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    
    <el-container class="main-container">
      <el-aside width="200px" class="side-navigation">
        <el-menu :default-active="$route.path" router class="navigation-menu">
          <el-menu-item index="/assets">
            <i class="el-icon el-icon-goods menu-icon"></i>
            <span>资产列表</span>
          </el-menu-item>
          <el-menu-item index="/auctions">
            <i class="el-icon el-icon-time menu-icon"></i>
            <span>拍卖列表</span>
          </el-menu-item>
          <el-menu-item index="/my-bids">
            <i class="el-icon el-icon-document menu-icon"></i>
            <span>出价列表</span>
          </el-menu-item>
          <el-menu-item index="/orders">
            <i class="el-icon el-icon-tickets menu-icon"></i>
            <span>订单列表</span>
          </el-menu-item>
          <el-menu-item index="/config">
            <i class="el-icon el-icon-setting menu-icon"></i>
            <span>配置管理</span>
          </el-menu-item>
          <el-menu-item index="/scheduled-tasks">
            <i class="el-icon el-icon-alarm-clock menu-icon"></i>
            <span>定时任务</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-main class="main-content">
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

<style scoped>
.auction-layout {
  background-color: #f8f9fa;
}

.auction-header {
  background: linear-gradient(135deg, #1e3c72, #2a5298);
  color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 0;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.logo-section {
  display: flex;
  align-items: center;
}

.logo-icon {
  font-size: 24px;
  margin-right: 10px;
}

.system-title {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.welcome-text {
  font-size: 14px;
}

.user-profile {
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.user-avatar {
  background-color: #409eff;
  margin-right: 5px;
}

.main-container {
  height: calc(100vh - 60px);
}

.side-navigation {
  background-color: #ffffff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  border-right: 1px solid #ebeef5;
}

.navigation-menu {
  border-right: none;
  height: 100%;
}

.navigation-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
  font-size: 14px;
}

.navigation-menu .el-menu-item:hover {
  background-color: #ecf5ff;
}

.navigation-menu .el-menu-item.is-active {
  background-color: #ecf5ff;
  color: #409eff;
  border-right: 3px solid #409eff;
}

.main-content {
  padding: 20px;
  background-color: #f8f9fa;
  overflow-y: auto;
}

/* 添加图标相关样式 */
.menu-icon {
  margin-right: 8px;
  width: 24px;
  height: 24px;
  font-size: 18px;
  vertical-align: middle;
  display: inline-block;
}
</style>