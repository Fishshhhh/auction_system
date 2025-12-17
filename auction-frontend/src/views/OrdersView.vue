<template>
  <div class="orders-view">
    <el-card>
      <div slot="header" class="card-header">
        <span>我的订单</span>
      </div>
      
      <el-table :data="myOrders" stripe style="width: 100%">
        <el-table-column prop="orderNo" label="订单编号" min-width="150"></el-table-column>
        <el-table-column prop="assetId" label="资产ID" min-width="150"></el-table-column>
        <el-table-column prop="orderAmount" label="订单金额" min-width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.orderAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="订单状态" min-width="100">
          <template slot-scope="scope">
            <el-tag :type="getOrderStatusType(scope.row.orderStatus)">
              {{ getOrderStatusText(scope.row.orderStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" min-width="150">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="200">
          <template slot-scope="scope">
            <el-button 
              size="small" 
              type="primary" 
              :disabled="scope.row.orderStatus !== 1" 
              @click="payOrder(scope.row)">
              支付
            </el-button>
            <el-button 
              size="small" 
              type="success" 
              :disabled="scope.row.orderStatus !== 2" 
              @click="completeOrder(scope.row)">
              完成订单
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 支付确认对话框 -->
    <el-dialog :visible.sync="payDialogVisible" title="确认支付" width="400px">
      <p>确认支付订单 {{ selectedOrder.orderNo }} 吗？</p>
      <p>支付金额：<strong>¥{{ selectedOrder.orderAmount }}</strong></p>
      <div slot="footer" class="dialog-footer">
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPay">确认支付</el-button>
      </div>
    </el-dialog>
    
    <!-- 完成订单确认对话框 -->
    <el-dialog :visible.sync="completeDialogVisible" title="确认完成订单" width="400px">
      <p>确认完成订单 {{ selectedOrder.orderNo }} 吗？</p>
      <div slot="footer" class="dialog-footer">
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmComplete">确认完成</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { orderApi } from '../api/orderApi'

export default {
  name: 'OrdersView',
  data() {
    return {
      myOrders: [],
      payDialogVisible: false,
      completeDialogVisible: false,
      selectedOrder: {}
    }
  },
  methods: {
    async loadMyOrders() {
      try {
        // 假设当前用户ID为1，同时作为买家和卖家查询订单
        const response = await orderApi.getOrdersByBuyer(1)
        if (response.code === 200) {
          // 确保响应数据是数组类型
          this.myOrders = Array.isArray(response.data) ? response.data : []
        }
      } catch (error) {
        console.error('加载我的订单失败:', error)
        // 发生错误时确保myOrders仍然是数组
        this.myOrders = []
        this.$message.error('加载订单失败: ' + error.message)
      }
    },
    payOrder(order) {
      this.selectedOrder = order
      this.payDialogVisible = true
    },
    completeOrder(order) {
      this.selectedOrder = order
      this.completeDialogVisible = true
    },
    async confirmPay() {
      try {
        const response = await orderApi.payOrder(this.selectedOrder.id)
        if (response.code === 200) {
          this.$message.success('支付成功!')
          this.payDialogVisible = false
          this.loadMyOrders() // 重新加载订单列表
        } else {
          this.$message.error(response.message || '支付失败')
        }
      } catch (error) {
        this.$message.error('支付失败: ' + error.message)
      }
    },
    async confirmComplete() {
      try {
        const response = await orderApi.completeOrder(this.selectedOrder.id)
        if (response.code === 200) {
          this.$message.success('订单已完成!')
          this.completeDialogVisible = false
          this.loadMyOrders() // 重新加载订单列表
        } else {
          this.$message.error(response.message || '操作失败')
        }
      } catch (error) {
        this.$message.error('操作失败: ' + error.message)
      }
    },
    formatDate(dateString) {
      if (!dateString) return ''
      return new Date(dateString).toLocaleString('zh-CN')
    },
    getOrderStatusText(status) {
      const statusMap = {
        1: '待付款',
        2: '已付款',
        3: '已完成',
        4: '已取消',
        5: '退款中',
        6: '已退款'
      }
      return statusMap[status] || '未知'
    },
    getOrderStatusType(status) {
      const typeMap = {
        1: '',
        2: 'success',
        3: 'primary',
        4: 'info',
        5: 'warning',
        6: 'danger'
      }
      return typeMap[status] || ''
    }
  },
  mounted() {
    this.loadMyOrders()
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>