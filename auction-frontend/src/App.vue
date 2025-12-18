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
    font-family: 'Helvetica Neue', Arial, sans-serif;
    font-size: 14px;
    -webkit-font-smoothing: antialiased;
    background-color: #f8f9fa;
    color: #303133;
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
    border-radius: 4px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.toolbar .el-form-item {
    margin-bottom: 10px;
}

.fade-enter-active,
.fade-leave-active {
    transition: all 0.2s ease;
}

.fade-enter,
.fade-leave-active {
    opacity: 0;
}

/* 自定义滚动条样式 */
::-webkit-scrollbar {
    width: 8px;
    height: 8px;
}

::-webkit-scrollbar-thumb {
    background-color: #c0c4cc;
    border-radius: 4px;
}

::-webkit-scrollbar-track {
    background-color: #f5f7fa;
}

/* Element UI 组件样式增强 */
.el-card {
    border: none;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.el-button {
    border-radius: 4px;
}

.el-button:focus,
.el-button:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    transition: all 0.2s ease;
}

.el-dialog {
    border-radius: 8px;
}

.el-dialog__header {
    background-color: #f5f7fa;
    border-bottom: 1px solid #ebeef5;
    padding: 15px 20px;
}

.el-dialog__body {
    padding: 20px;
}

.el-dialog__footer {
    border-top: 1px solid #ebeef5;
    padding: 15px 20px;
}

.el-tag {
    border-radius: 4px;
}

/* 表格样式优化 */
.el-table {
    background-color: #fff;
    border-radius: 4px;
    overflow: hidden;
}

.el-table th {
    background-color: #f5f7fa !important;
}

/* 输入框样式优化 */
.el-input__inner,
.el-textarea__inner {
    border-radius: 4px;
}

.el-input__inner:focus,
.el-textarea__inner:focus {
    border-color: #409eff;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}
</style>