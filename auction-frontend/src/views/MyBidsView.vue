<template>
  <div class="my-bids-view">
    <el-card>
      <div slot="header" class="card-header">
        <span>出价列表</span>
      </div>
      
      <el-table :data="myBids" stripe style="width: 100%">
        <el-table-column prop="id" label="出价ID" min-width="100"></el-table-column>
        <el-table-column prop="auctionId" label="拍卖ID" min-width="100">
          <template slot-scope="scope">
            <el-button type="text" @click="viewAuctionDetail(scope.row.auctionId)">
              {{ scope.row.auctionId }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="拍卖资产" min-width="150">
          <template slot-scope="scope">
            <div v-if="scope.row.auction && scope.row.auction.assets">
              <div v-for="asset in scope.row.auction.assets" :key="asset.id">
                {{ asset.name }}
              </div>
            </div>
            <div v-else>
              未知资产
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="userId" label="出价人" min-width="100"></el-table-column>
        <el-table-column prop="bidPrice" label="出价金额(元/台)" min-width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.bidPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" min-width="80"></el-table-column>
        <el-table-column label="总金额" min-width="100">
          <template slot-scope="scope">
            ¥{{ (scope.row.bidPrice * scope.row.quantity).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="出价时间" min-width="150">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column label="拍卖状态" min-width="100">
          <template slot-scope="scope">
            <div v-if="scope.row.auction">
              <el-tag :type="getAuctionStatusType(scope.row.auction.auctionStatus)">
                {{ getAuctionStatusText(scope.row.auction.auctionStatus) }}
              </el-tag>
            </div>
            <div v-else>
              未知
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="bidStatus" label="出价状态" min-width="100">
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
import { assetApi } from '../api/assetApi'
import { userApi } from '../api/userApi'

export default {
  name: 'MyBidsView',
  data() {
    return {
      myBids: [],
      assets: []
    }
  },
  methods: {
    async loadMyBids() {
      try {
        // 获取所有出价记录
        const response = await auctionApi.getAllBids()
        if (response.code === 200) {
          // 确保响应数据是数组类型
          let bids = Array.isArray(response.data) ? response.data : []
          
          // 为每个出价加载拍卖和资产信息
          for (let bid of bids) {
            // 加载拍卖信息
            const auctionResponse = await auctionApi.getAuctionById(bid.auctionId);
            if (auctionResponse.code === 200) {
              bid.auction = auctionResponse.data;
              
              // 加载拍卖关联的资产信息
              const assetsResponse = await auctionApi.getAuctionAssets(bid.auctionId);
              if (assetsResponse.code === 200 && Array.isArray(assetsResponse.data)) {
                // 获取资产详细信息
                const assetDetails = [];
                for (let auctionAsset of assetsResponse.data) {
                  const assetResponse = await assetApi.getAssetById(auctionAsset.assetId);
                  if (assetResponse.code === 200) {
                    assetDetails.push(Object.assign({}, assetResponse.data, {
                      quantity: auctionAsset.quantity
                    }));
                  }
                }
                bid.auction.assets = assetDetails;
              }
            }
          }
          
          this.myBids = bids;
        }
      } catch (error) {
        console.error('加载我的出价失败:', error)
        // 发生错误时确保myBids仍然是数组
        this.myBids = []
      }
    },
    
    viewAuctionDetail(auctionId) {
      this.$router.push(`/auctions/${auctionId}`);
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
    },
    
    getAuctionStatusText(status) {
      const statusMap = {
        1: '未开始',
        2: '进行中',
        3: '已结束',
        4: '已成交',
        5: '流拍'
      };
      return statusMap[status] || '未知';
    },
    
    getAuctionStatusType(status) {
      const typeMap = {
        1: '',
        2: 'success',
        3: 'info',
        4: 'primary',
        5: 'danger'
      };
      return typeMap[status] || '';
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