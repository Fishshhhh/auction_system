<template>
  <div class="auction-detail-view">
    <el-card>
      <div slot="header">
        <el-page-header @back="goBack" content="拍卖详情">
        </el-page-header>
      </div>
      
      <div v-if="auction">
        <!-- 基本信息 -->
        <el-row :gutter="20">
          <el-col :span="16">
            <el-card class="box-card">
              <div slot="header" class="clearfix">
                <span>拍卖信息</span>
              </div>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="拍卖编号">{{ auction.id }}</el-descriptions-item>
                <el-descriptions-item label="资产名称">{{ auction.assetName }}</el-descriptions-item>
                <el-descriptions-item label="起拍价">¥{{ auction.startPrice }}</el-descriptions-item>
                <el-descriptions-item label="当前价">
                  <span class="current-price">¥{{ auction.currentPrice }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="保留价">¥{{ auction.reservePrice }}</el-descriptions-item>
                <el-descriptions-item label="一口价">¥{{ auction.buyItNowPrice }}</el-descriptions-item>
                <el-descriptions-item label="开始时间">{{ formatDate(auction.startTime) }}</el-descriptions-item>
                <el-descriptions-item label="结束时间">{{ formatDate(auction.endTime) }}</el-descriptions-item>
                <el-descriptions-item label="拍卖状态">
                  <el-tag :type="getAuctionStatusType(auction.auctionStatus)">
                    {{ getAuctionStatusText(auction.auctionStatus) }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="拍卖类型">
                  <el-tag :type="getAuctionTypeTagType(auction.auctionType)">
                    {{ getAuctionTypeText(auction.auctionType) }}
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>
            </el-card>
            
            <!-- 特定拍卖类型的信息 -->
            <el-card class="box-card" style="margin-top: 20px;">
              <div slot="header" class="clearfix">
                <span>{{ getAuctionTypeText(auction.auctionType) }}详情</span>
              </div>
              
              <!-- 公开竞价和无底价拍卖 -->
              <div v-if="auction.auctionType === 1 || auction.auctionType === 3">
                <el-descriptions :column="2">
                  <el-descriptions-item label="加价幅度">¥{{ auction.bidIncrement }}</el-descriptions-item>
                  <el-descriptions-item label="延时时间">{{ auction.extendTime }}分钟</el-descriptions-item>
                  <el-descriptions-item label="延时阈值">{{ auction.extendThreshold }}秒</el-descriptions-item>
                </el-descriptions>
              </div>
              
              <!-- 降价拍卖 -->
              <div v-if="auction.auctionType === 5">
                <el-descriptions :column="2">
                  <el-descriptions-item label="初始价">¥{{ auction.initialPrice }}</el-descriptions-item>
                  <el-descriptions-item label="降价阶梯">¥{{ auction.priceStep }}</el-descriptions-item>
                  <el-descriptions-item label="降价周期">{{ auction.priceDropInterval }}分钟</el-descriptions-item>
                  <el-descriptions-item label="最低价">¥{{ auction.minPrice }}</el-descriptions-item>
                  <el-descriptions-item label="下次降价时间">{{ formatDate(auction.nextPriceDropTime) }}</el-descriptions-item>
                </el-descriptions>
                <el-alert
                  title="降价拍卖说明：系统会按设定周期自动降价，您可以随时接受当前价格成交"
                  type="info"
                  show-icon>
                </el-alert>
              </div>
              
              <!-- 定向拍卖 -->
              <div v-if="auction.auctionType === 4">
                <el-descriptions :column="1">
                  <el-descriptions-item label="资格要求">
                    {{ auction.qualificationRequired ? '需要资格认证' : '无特殊要求' }}
                  </el-descriptions-item>
                </el-descriptions>
              </div>
              
              <!-- 暗拍 -->
              <div v-if="auction.auctionType === 2">
                <el-alert
                  title="暗拍说明：您的出价对其他竞买人不可见，拍卖结束后系统统一解密并排序"
                  type="info"
                  show-icon>
                </el-alert>
              </div>
            </el-card>
          </el-col>
          
          <!-- 右侧操作面板 -->
          <el-col :span="8">
            <el-card class="box-card">
              <div slot="header" class="clearfix">
                <span>操作面板</span>
              </div>
              
              <!-- 倒计时 -->
              <div v-if="auction.auctionStatus === 2" class="countdown">
                <div class="countdown-title">距结束还剩</div>
                <div class="countdown-display">{{ countdown }}</div>
              </div>
              
              <!-- 出价表单 -->
              <div v-if="canBid()">
                <el-form :model="bidForm" label-width="80px">
                  <el-form-item label="当前价格">
                    <span class="current-price-large">¥{{ auction.currentPrice }}</span>
                  </el-form-item>
                  <el-form-item label="您的出价" prop="bidPrice">
                    <el-input-number
                      v-model="bidForm.bidPrice"
                      :min="getMinBidPrice()"
                      :step="auction.bidIncrement || 100"
                      controls-position="right"
                      style="width: 100%">
                    </el-input-number>
                  </el-form-item>
                  <el-form-item>
                    <el-button 
                      type="primary" 
                      style="width: 100%" 
                      @click="submitBid"
                      :loading="bidLoading">
                      {{ auction.auctionType === 5 ? '接受当前价格' : '提交出价' }}
                    </el-button>
                  </el-form-item>
                </el-form>
                
                <el-alert 
                  v-if="auction.auctionType === 5" 
                  title="降价拍卖：出价必须等于当前价格" 
                  type="info" 
                  show-icon>
                </el-alert>
              </div>
              
              <!-- 无法出价提示 -->
              <div v-else>
                <el-alert 
                  :title="getCannotBidReason()" 
                  type="warning" 
                  show-icon>
                </el-alert>
              </div>
              
              <!-- 管理员操作 -->
              <div v-if="isAdmin()" style="margin-top: 20px;">
                <el-divider>管理员操作</el-divider>
                <el-button 
                  v-if="auction.auctionStatus === 1" 
                  type="warning" 
                  style="width: 100%; margin-bottom: 10px;"
                  @click="startAuction">
                  开始拍卖
                </el-button>
                <el-button 
                  v-if="auction.auctionStatus === 2" 
                  type="danger" 
                  style="width: 100%"
                  @click="endAuction">
                  结束拍卖
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 出价记录 -->
        <el-card class="box-card" style="margin-top: 20px;">
          <div slot="header" class="clearfix">
            <span>出价记录</span>
          </div>
          <el-table :data="bids" stripe style="width: 100%">
            <el-table-column prop="userId" label="出价人ID" min-width="100"></el-table-column>
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
      
      <div v-else>
        <el-skeleton :rows="10" animated />
      </div>
    </el-card>
  </div>
</template>

<script>
import { auctionApi } from '../api/auctionApi'

export default {
  name: 'AuctionDetail',
  data() {
    return {
      auction: null,
      bids: [],
      bidForm: {
        bidPrice: 0
      },
      bidLoading: false,
      countdown: '00:00:00'
    }
  },
  methods: {
    goBack() {
      this.$router.go(-1)
    },
    
    async loadAuction() {
      const auctionId = this.$route.params.id
      if (!auctionId) {
        this.$message.error('缺少拍卖ID参数')
        return
      }
      
      try {
        const response = await auctionApi.getAuctionById(auctionId)
        if (response.code === 200) {
          this.auction = response.data
          this.bidForm.bidPrice = this.getMinBidPrice()
          this.startCountdown()
        } else {
          this.$message.error(response.message || '加载拍卖详情失败')
        }
      } catch (error) {
        this.$message.error('加载拍卖详情失败: ' + (error.message || '未知错误'))
      }
    },
    
    async loadBids() {
      if (!this.auction) return
      
      try {
        const response = await auctionApi.getAuctionBids(this.auction.id)
        if (response.code === 200) {
          this.bids = response.data || []
        }
      } catch (error) {
        this.$message.error('加载出价记录失败: ' + (error.message || '未知错误'))
      }
    },
    
    startCountdown() {
      if (!this.auction || !this.auction.endTime) return
      
      const updateCountdown = () => {
        const now = new Date()
        const endTime = new Date(this.auction.endTime)
        const diff = endTime - now
        
        if (diff <= 0) {
          this.countdown = '已结束'
          return
        }
        
        const hours = Math.floor(diff / (1000 * 60 * 60))
        const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
        const seconds = Math.floor((diff % (1000 * 60)) / 1000)
        
        this.countdown = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
      }
      
      updateCountdown()
      setInterval(updateCountdown, 1000)
    },
    
    async submitBid() {
      if (!this.auction) return
      
      this.bidLoading = true
      try {
        const bidData = {
          auctionId: this.auction.id,
          userId: 1, // 模拟用户ID
          bidPrice: this.bidForm.bidPrice,
          bidStatus: 1
        }
        
        const response = await auctionApi.submitBid(this.auction.id, bidData)
        if (response.code === 200) {
          this.$message.success('出价成功!')
          this.loadAuction() // 重新加载拍卖信息
          this.loadBids() // 重新加载出价记录
        } else {
          this.$message.error(response.message || '出价失败')
        }
      } catch (error) {
        this.$message.error('出价失败: ' + (error.message || '未知错误'))
      } finally {
        this.bidLoading = false
      }
    },
    
    async startAuction() {
      if (!this.auction) return
      
      try {
        const response = await auctionApi.startAuction(this.auction.id)
        if (response.code === 200) {
          this.$message.success('拍卖已开始!')
          this.loadAuction()
        } else {
          this.$message.error(response.message || '操作失败')
        }
      } catch (error) {
        this.$message.error('操作失败: ' + (error.message || '未知错误'))
      }
    },
    
    async endAuction() {
      if (!this.auction) return
      
      try {
        const response = await auctionApi.endAuction(this.auction.id)
        if (response.code === 200) {
          this.$message.success('拍卖已结束!')
          this.loadAuction()
        } else {
          this.$message.error(response.message || '操作失败')
        }
      } catch (error) {
        this.$message.error('操作失败: ' + (error.message || '未知错误'))
      }
    },
    
    formatDate(dateString) {
      if (!dateString) return ''
      return new Date(dateString).toLocaleString('zh-CN')
    },
    
    getAuctionStatusText(status) {
      const statusMap = {
        1: '未开始',
        2: '进行中',
        3: '已结束',
        4: '已成交',
        5: '流拍'
      }
      return statusMap[status] || '未知'
    },
    
    getAuctionStatusType(status) {
      const typeMap = {
        1: '',        // 未开始
        2: 'success', // 进行中
        3: 'info',    // 已结束
        4: 'primary', // 已成交
        5: 'danger'   // 流拍
      }
      return typeMap[status] || ''
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
    
    getBidStatusText(status) {
      const statusMap = {
        1: '有效',
        2: '无效',
        3: '胜出',
        4: '失败'
      }
      return statusMap[status] || '未知'
    },
    
    getBidStatusType(status) {
      const typeMap = {
        1: 'success',
        2: 'danger',
        3: 'primary',
        4: 'info'
      }
      return typeMap[status] || ''
    },
    
    canBid() {
      if (!this.auction) return false
      // 只有进行中的拍卖才能出价
      if (this.auction.auctionStatus !== 2) return false
      return true
    },
    
    getCannotBidReason() {
      if (!this.auction) return '无法出价'
      
      switch (this.auction.auctionStatus) {
        case 1:
          return '拍卖尚未开始'
        case 3:
        case 4:
        case 5:
          return '拍卖已结束'
        default:
          return '暂无法出价'
      }
    },
    
    getMinBidPrice() {
      if (!this.auction) return 0
      
      // 降价拍卖必须等于当前价格
      if (this.auction.auctionType === 5) {
        return this.auction.currentPrice
      }
      
      // 其他拍卖类型至少要比当前价格高一个加价幅度
      if (this.auction.bidIncrement) {
        return this.auction.currentPrice + this.auction.bidIncrement
      }
      
      // 默认至少高100
      return this.auction.currentPrice + 100
    },
    
    isAdmin() {
      // 模拟管理员权限检查
      return true
    }
  },
  mounted() {
    this.loadAuction()
    this.loadBids()
  }
}
</script>

<style scoped>
.auction-detail-view {
  padding: 20px;
}

.current-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.current-price-large {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
}

.countdown {
  text-align: center;
  margin-bottom: 20px;
}

.countdown-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.countdown-display {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
}
</style>