<template>
  <div class="my-bids-view">
    <el-card>
      <div slot="header" class="card-header">
        <span>我的出价</span>
      </div>
      
      <el-table :data="myBids" stripe style="width: 100%">
        <el-table-column prop="assetId" label="资产ID" min-width="150"></el-table-column>
        <el-table-column prop="bidPrice" label="出价金额" min-width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.bidPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="出价时间" min-width="150">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="bidStatus" label="状态" min-width="100">
          <template slot-scope="scope">
            <el-tag :type="getBidStatusType(scope.row.bidStatus)">
              {{ getBidStatusText(scope.row.bidStatus) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { auctionApi } from '../api/auctionApi'

export default {
  name: 'MyBidsView',
  data() {
    return {
      myBids: []
    }
  },
  methods: {
    async loadMyBids() {
      try {
        // 假设当前用户ID为1
        const response = await auctionApi.getUserBids(1)
        if (response.code === 200) {
          // 确保响应数据是数组类型
          this.myBids = Array.isArray(response.data) ? response.data : []
        }
      } catch (error) {
        console.error('加载我的出价失败:', error)
        // 发生错误时确保myBids仍然是数组
        this.myBids = []
      }
    },
    formatDate(dateString) {
      if (!dateString) return ''
      return new Date(dateString).toLocaleString('zh-CN')
    },
    getBidStatusText(status) {
      const statusMap = {
        1: '有效',
        2: '无效',
        3: '胜出'
      }
      return statusMap[status] || '未知'
    },
    getBidStatusType(status) {
      const typeMap = {
        1: 'success',
        2: 'danger',
        3: 'primary'
      }
      return typeMap[status] || ''
    }
  },
  mounted() {
    this.loadMyBids()
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