<template>
  <el-container v-if="isLoggedIn" class="auction-layout">
    <el-header class="auction-header">
      <div class="header-content">
        <div class="logo-section">
          <div class="logo-placeholder">
            <i class="el-icon-s-shop"></i>
          </div>
          <h1 class="system-title">拍卖系统</h1>
        </div>
        <div class="header-controls">
          <div class="user-info">
            <el-badge :value="3" class="notification-badge">
              <el-button type="text" class="notification-button">
                <i class="el-icon-bell"></i>
              </el-button>
            </el-badge>
            <el-dropdown>
              <div class="user-profile">
                <el-avatar :size="36" class="user-avatar">
                  {{ currentUser.username.charAt(0).toUpperCase() }}
                </el-avatar>
                <div class="user-details">
                  <div class="user-name">{{ currentUser.fullName }}</div>
                  <div class="user-role">{{ getUserTypeText(currentUser.userType) }}</div>
                </div>
                <i class="el-icon-arrow-down"></i>
              </div>
              <el-dropdown-menu slot="dropdown" class="user-dropdown">
                <el-dropdown-item>
                  <i class="el-icon-user"></i>个人中心
                </el-dropdown-item>
                <el-dropdown-item>
                  <i class="el-icon-setting"></i>系统设置
                </el-dropdown-item>
                <el-dropdown-item divided @click.native="logout">
                  <i class="el-icon-switch-button"></i>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
      </div>
    </el-header>
    
    <el-container class="main-container">
      <el-aside width="240px" class="side-navigation">
        <div class="sidebar-header">
          <h2 class="sidebar-title">导航菜单</h2>
        </div>
        <el-menu :default-active="$route.path" router class="navigation-menu" :unique-opened="true">
          <el-menu-item index="/assets" class="menu-item">
            <i class="el-icon-goods menu-icon"></i>
            <span class="menu-text">资产列表</span>
          </el-menu-item>
          <el-menu-item index="/auctions" class="menu-item">
            <i class="el-icon-time menu-icon"></i>
            <span class="menu-text">拍卖列表</span>
          </el-menu-item>
          <el-menu-item index="/my-bids" class="menu-item">
            <i class="el-icon-document menu-icon"></i>
            <span class="menu-text">出价列表</span>
          </el-menu-item>
          <el-menu-item index="/orders" class="menu-item">
            <i class="el-icon-tickets menu-icon"></i>
            <span class="menu-text">订单列表</span>
          </el-menu-item>
          <el-menu-item index="/config" class="menu-item">
            <i class="el-icon-setting menu-icon"></i>
            <span class="menu-text">配置管理</span>
          </el-menu-item>
          <el-menu-item index="/scheduled-tasks" class="menu-item">
            <i class="el-icon-alarm-clock menu-icon"></i>
            <span class="menu-text">定时任务</span>
          </el-menu-item>
        </el-menu>
        
        <div class="sidebar-footer">
          <div class="system-info">
            <div class="info-item">
              <i class="el-icon-monitor"></i>
              <span>在线用户: 24</span>
            </div>
            <div class="info-item">
              <i class="el-icon-date"></i>
              <span>{{ currentTime }}</span>
            </div>
          </div>
        </div>
      </el-aside>
      
      <el-main class="main-content">
        <div class="breadcrumb-container">
          <el-breadcrumb separator="/" class="custom-breadcrumb">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
  
  <router-view v-else />
</template>

<script>
export default {
  name: 'Layout',
  data() {
    return {
      currentTime: new Date().toLocaleString('zh-CN')
    }
  },
  computed: {
    isLoggedIn() {
      return this.$store.state.user.isLoggedIn
    },
    currentUser() {
      return this.$store.state.user.currentUser
    },
    activeMenu() {
      return this.$store.state.app.activeMenu
    },
    currentPageTitle() {
      const routeMap = {
        '/assets': '资产列表',
        '/auctions': '拍卖列表',
        '/my-bids': '出价列表',
        '/orders': '订单列表',
        '/config': '配置管理',
        '/scheduled-tasks': '定时任务'
      }
      return routeMap[this.$route.path] || '未知页面'
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
        1: '系统管理员',
        2: '运营专员',
        3: '资产方',
        4: '竞拍方'
      }
      return typeMap[type] || '未知角色'
    },
    updateTime() {
      this.currentTime = new Date().toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
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
    // 每秒更新时间
    setInterval(this.updateTime, 1000)
  }
}
</script>

<style scoped>
.auction-layout {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.auction-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 70px;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 30px;
}

.logo-section {
  display: flex;
  align-items: center;
}

.logo-placeholder {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #409eff, #64b5f6);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.logo-placeholder i {
  font-size: 24px;
  color: white;
}

.system-title {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #409eff, #64b5f6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-controls {
  display: flex;
  align-items: center;
  gap: 30px;
}

.notification-badge ::v-deep .el-badge__content {
  background-color: #f56c6c;
  border: none;
}

.notification-button {
  font-size: 20px;
  color: #606266;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-profile {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.user-profile:hover {
  background-color: #f0f2f5;
}

.user-avatar {
  background: linear-gradient(135deg, #409eff, #64b5f6);
  margin-right: 12px;
  font-weight: 600;
}

.user-details {
  margin-right: 10px;
  text-align: left;
}

.user-name {
  font-weight: 600;
  font-size: 14px;
  color: #303133;
}

.user-role {
  font-size: 12px;
  color: #909399;
}

.user-dropdown ::v-deep .el-dropdown-menu__item {
  padding: 10px 20px;
  font-size: 14px;
}

.user-dropdown ::v-deep .el-dropdown-menu__item i {
  margin-right: 8px;
  width: 16px;
  text-align: center;
}

.main-container {
  height: calc(100vh - 70px);
}

.side-navigation {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  box-shadow: 2px 0 20px rgba(0, 0, 0, 0.08);
  border-right: none;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 20px 20px 10px;
  border-bottom: 1px solid #ebeef5;
}

.sidebar-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.navigation-menu {
  border-right: none;
  flex: 1;
  overflow-y: auto;
}

.navigation-menu ::v-deep .el-menu-item {
  height: 50px;
  line-height: 50px;
  font-size: 15px;
  margin: 5px 10px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.navigation-menu ::v-deep .el-menu-item:hover {
  background: linear-gradient(90deg, #ecf5ff, #f0f8ff);
  color: #409eff;
}

.navigation-menu ::v-deep .el-menu-item.is-active {
  background: linear-gradient(90deg, #409eff, #64b5f6);
  color: white;
}

.navigation-menu ::v-deep .el-menu-item.is-active .menu-icon {
  color: white;
}

.menu-icon {
  margin-right: 12px;
  width: 20px;
  height: 20px;
  font-size: 18px;
  vertical-align: middle;
  display: inline-block;
  transition: all 0.3s ease;
}

.sidebar-footer {
  padding: 20px;
  border-top: 1px solid #ebeef5;
}

.system-info {
  font-size: 12px;
  color: #909399;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-item i {
  margin-right: 8px;
  font-size: 14px;
}

.main-content {
  padding: 25px;
  background: #f5f7fa;
  overflow-y: auto;
}

.breadcrumb-container {
  background: white;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

.custom-breadcrumb ::v-deep .el-breadcrumb__item:last-child .el-breadcrumb__inner {
  font-weight: 600;
  color: #303133;
}
</style>