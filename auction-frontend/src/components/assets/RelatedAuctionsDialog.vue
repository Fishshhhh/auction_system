<template>
  <el-dialog :visible.sync="visible" title="相关拍卖信息" width="800px" @close="onClose">
    <div v-if="asset">
      <h4>资产：{{ asset.name }}</h4>
      <el-table :data="auctions" stripe style="width: 100%">
        <el-table-column prop="id" label="拍卖编号" min-width="100">
          <template slot-scope="scope">
            <el-button type="text" @click="viewAuctionDetail(scope.row.id)">{{ scope.row.id }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="startPrice" label="起拍价" min-width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.startPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="currentPrice" label="当前价" min-width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.currentPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="auctionType" label="拍卖类型" min-width="120">
          <template slot-scope="scope">
            <el-tag :type="getAuctionTypeTagType(scope.row.auctionType)">
              {{ getAuctionTypeText(scope.row.auctionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="拍卖状态" min-width="100">
          <template slot-scope="scope">
            <el-tag :type="getAuctionStatusType(scope.row.auctionStatus)">
              {{ getAuctionStatusText(scope.row.auctionStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="100">
          <template slot-scope="scope">
            <el-button size="small" @click="viewAuctionDetail(scope.row.id)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <div slot="footer" class="dialog-footer">
      <el-button @click="onClose">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'RelatedAuctionsDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    asset: {
      type: Object,
      default: () => null
    },
    auctions: {
      type: Array,
      default: () => []
    }
  },
  methods: {
    onClose() {
      this.$emit('update:visible', false);
    },
    viewAuctionDetail(auctionId) {
      this.$emit('view-auction-detail', auctionId);
    },
    getAuctionTypeText(type) {
      const typeMap = {
        1: '公开实时竞价',
        2: '暗拍',
        3: '无底价',
        4: '定向',
        5: '降价'
      }
      return typeMap[type] || '未知'
    },
    getAuctionTypeTagType(type) {
      const typeMap = {
        1: 'primary', // 公开实时竞价
        2: 'warning', // 暗拍
        3: 'success', // 无底价
        4: 'info',    // 定向
        5: 'danger'   // 降价
      }
      return typeMap[type] || ''
    },
    getAuctionStatusText(status) {
      const statusMap = {
        1: '未开始',
        2: '进行中',
        3: '已结束',
        4: '流拍'
      }
      return statusMap[status] || '未知'
    },
    getAuctionStatusType(status) {
      const typeMap = {
        1: '',        // 未开始
        2: 'success', // 进行中
        3: 'info',    // 已结束
        4: 'danger'   // 流拍
      }
      return typeMap[status] || ''
    }
  }
}
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style>