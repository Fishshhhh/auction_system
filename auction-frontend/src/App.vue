<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style scoped>
/deep/ .el-table th.gutter{
    /*
    * 解决element-ui 表格篡位的问题 
    */
    display: table-cell!important;
}
</style>

<style>
body {
    margin: 0;
    padding: 0;
    font-family: Helvetica Neue, Helvetica, PingFang SC, Hiragino Sans GB, Microsoft YaHei, SimSun, sans-serif;
    font-size: 14px;
    -webkit-font-smoothing: antialiased;
}

#app {
    position: absolute;
    top: 0;
    bottom: 0;
    width: 100%;
}

.el-submenu [class^=fa] {
    vertical-align: baseline;
    margin-right: 10px;
}

.el-menu-item [class^=fa] {
    vertical-align: baseline;
    margin-right: 10px;
}

.el-menu--collapse {
    width: 55px;
}

.el-main {
    padding: 0;
}

.toolbar {
    background: #f2f2f2;
    padding: 10px;
    margin: 10px 0;
    .el-form-item {
        margin-bottom: 10px;
    }
}

.fade-enter-active,
.fade-leave-active {
    transition: all 0.2s ease;
}

.fade-enter,
.fade-leave-active {
    opacity: 0;
}
</style>