<template>
  <div class="auctions-view">
    <el-card>
      <div slot="header" class="card-header">
        <span>拍卖列表</span>
      </div>
      
      <el-table :data="auctions" stripe style="width: 100%">
        <el-table-column prop="id" label="拍卖编号" min-width="100">
          <template slot-scope="scope">
            <el-button type="text" @click="viewAuctionDetail(scope.row.id)">{{ scope.row.id }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="assetId" label="资产ID" min-width="100"></el-table-column>
        <el-table-column prop="assetName" label="资产名称" min-width="150"></el-table-column>
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
        <el-table-column prop="startTime" label="开始时间" min-width="150">
          <template slot-scope="scope">
            {{ formatDate(scope.row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" min-width="150">
          <template slot-scope="scope">
            {{ formatDate(scope.row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="拍卖状态" min-width="100">
          <template slot-scope="scope">
            <el-tag :type="getAuctionStatusType(scope.row.auctionStatus)">
              {{ getAuctionStatusText(scope.row.auctionStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="拍卖类型" min-width="120">
          <template slot-scope="scope">
            <el-tag :type="getAuctionTypeTagType(scope.row.auctionType)">
              {{ getAuctionTypeText(scope.row.auctionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="150">
          <template slot-scope="scope">
            <el-button size="small" @click="viewAuction(scope.row)">查看详情</el-button>
            <el-button 
              v-if="canBid(scope.row)" 
              size="small" 
              type="primary" 
              @click="bidAuction(scope.row)">
              出价
            </el-button>
            <el-button 
              v-if="scope.row.auctionStatus === 1" 
              size="small" 
              type="warning" 
              @click="startAuction(scope.row)">
              开始拍卖
            </el-button>
            <el-button 
              v-if="scope.row.auctionStatus === 2" 
              size="small" 
              type="danger" 
              @click="endAuction(scope.row)">
              结束拍卖
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 拍卖详情对话框 -->
    <el-dialog :visible.sync="dialogVisible" title="拍卖详情" width="600px">
      <div v-if="selectedAuction">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="拍卖编号">{{ selectedAuction.id }}</el-descriptions-item>
          <el-descriptions-item label="资产ID">{{ selectedAuction.assetId }}</el-descriptions-item>
          <el-descriptions-item label="资产名称">{{ selectedAuction.assetName }}</el-descriptions-item>
          <el-descriptions-item label="起拍价">¥{{ selectedAuction.startPrice }}</el-descriptions-item>
          <el-descriptions-item label="当前价">¥{{ selectedAuction.currentPrice }}</el-descriptions-item>
          <el-descriptions-item label="保留价">¥{{ selectedAuction.reservePrice }}</el-descriptions-item>
          <el-descriptions-item label="一口价">¥{{ selectedAuction.buyItNowPrice }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ formatDate(selectedAuction.startTime) }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ formatDate(selectedAuction.endTime) }}</el-descriptions-item>
          <el-descriptions-item label="拍卖状态">
            <el-tag :type="getAuctionStatusType(selectedAuction.auctionStatus)">
              {{ getAuctionStatusText(selectedAuction.auctionStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="拍卖类型">
            <el-tag :type="getAuctionTypeTagType(selectedAuction.auctionType)">
              {{ getAuctionTypeText(selectedAuction.auctionType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(selectedAuction.createdTime) }}</el-descriptions-item>
          
          <!-- 特定拍卖类型的信息 -->
          <el-descriptions-item v-if="selectedAuction.auctionType === 5" label="初始价">
            ¥{{ selectedAuction.initialPrice }}
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedAuction.auctionType === 5" label="降价阶梯">
            ¥{{ selectedAuction.priceStep }}
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedAuction.auctionType === 5" label="降价周期">
            {{ selectedAuction.priceDropInterval }}分钟
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedAuction.auctionType === 5" label="最低价">
            ¥{{ selectedAuction.minPrice }}
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedAuction.auctionType === 5" label="下次降价时间">
            {{ formatDate(selectedAuction.nextPriceDropTime) }}
          </el-descriptions-item>
        </el-descriptions>
        
        <!-- 出价记录 -->
        <div style="margin-top: 20px;">
          <h4>出价记录</h4>
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
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button 
          v-if="canBid(selectedAuction)" 
          type="primary" 
          @click="bidAuction(selectedAuction)">
          出价
        </el-button>
      </div>
    </el-dialog>
    
    <!-- 出价对话框 -->
    <el-dialog :visible.sync="bidDialogVisible" title="出价" width="400px">
      <el-form v-if="selectedAuction" :model="bidForm" label-width="80px">
        <el-form-item label="资产名称">
          <span>{{ selectedAuction.assetName }}</span>
        </el-form-item>
        <el-form-item label="当前价格">
          <span>¥{{ selectedAuction.currentPrice }}</span>
        </el-form-item>
        <el-form-item label="您的出价" prop="bidPrice">
          <el-input-number
            v-model="bidForm.bidPrice"
            :min="getMinBidPrice()"
            :step="selectedAuction.bidIncrement || 100"
            controls-position="right"
          />
        </el-form-item>
        <el-alert 
          v-if="selectedAuction.auctionType === 5" 
          title="降价拍卖：出价必须等于当前价格" 
          type="info" 
          show-icon>
        </el-alert>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="bidDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBid">提交出价</el-button>
      </div>
    </el-dialog>
    
    <!-- 开始拍卖确认对话框 -->
    <el-dialog :visible.sync="startDialogVisible" title="确认开始拍卖" width="400px">
      <p v-if="selectedAuction">确认开始拍卖 {{ selectedAuction.assetName }} 吗？</p>
      <div slot="footer" class="dialog-footer">
        <el-button @click="startDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmStart">确认开始</el-button>
      </div>
    </el-dialog>
    
    <!-- 结束拍卖确认对话框 -->
    <el-dialog :visible.sync="endDialogVisible" title="确认结束拍卖" width="400px">
      <p v-if="selectedAuction">确认结束拍卖 {{ selectedAuction.assetName }} 吗？</p>
      <div slot="footer" class="dialog-footer">
        <el-button @click="endDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmEnd">确认结束</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { auctionApi } from '../api/auctionApi'

export default {
  name: 'AuctionsView',
  data() {
    return {
      auctions: [],
      bids: [],
      dialogVisible: false,
      bidDialogVisible: false,
      startDialogVisible: false,
      endDialogVisible: false,
      selectedAuction: null,
      bidForm: {
        bidPrice: 0
      }
    }
  },
  methods: {
    async loadAuctions() {
      try {
        const response = await auctionApi.getAuctions()
        console.log('Auctions API Response:', response) // 调试日志
        if (response && response.code === 200) {
          // 确保 response.data 是数组
          if (Array.isArray(response.data)) {
            // 显示所有拍卖
            console.log('Auctions loaded1123123:', response.data)
            this.auctions = response.data
            console.log('Auctions loaded:', this.auctions) // 添加调试日志
          } else {
            console.warn('Auctions data is not an array:', response.data)
            this.auctions = []
          }
        } else {
          this.$message.error((response && response.message) || '加载拍卖列表失败')
          this.auctions = []
        }
      } catch (error) {
        console.error('加载拍卖列表失败:', error)
        this.$message.error('加载拍卖列表失败: ' + (error.message || '未知错误'))
        this.auctions = []
      }
    },
    async viewAuction(auction) {
      this.selectedAuction = auction
      // 加载出价记录
      try {
        const response = await auctionApi.getAuctionBids(auction.id)
        if (response.code === 200) {
          this.bids = response.data || []
        }
      } catch (error) {
        console.error('加载出价记录失败:', error)
        this.$message.error('加载出价记录失败: ' + (error.message || '未知错误'))
        this.bids = []
      }
      this.dialogVisible = true
    },
    bidAuction(auction) {
      this.selectedAuction = auction
      this.bidForm.bidPrice = this.getMinBidPrice()
      this.bidDialogVisible = true
    },
    startAuction(auction) {
      this.selectedAuction = auction
      this.startDialogVisible = true
    },
    endAuction(auction) {
      this.selectedAuction = auction
      this.endDialogVisible = true
    },
    async confirmStart() {
      try {
        const response = await auctionApi.startAuction(this.selectedAuction.id)
        if (response.code === 200) {
          this.$message.success('拍卖已开始!')
          this.startDialogVisible = false
          this.loadAuctions() // 重新加载拍卖列表
        } else {
          this.$message.error(response.message || '操作失败')
        }
      } catch (error) {
        this.$message.error('操作失败: ' + (error.message || '未知错误'))
      }
    },
    async confirmEnd() {
      try {
        const response = await auctionApi.endAuction(this.selectedAuction.id)
        if (response.code === 200) {
          this.$message.success('拍卖已结束!')
          this.endDialogVisible = false
          this.loadAuctions() // 重新加载拍卖列表
        } else {
          this.$message.error(response.message || '操作失败')
        }
      } catch (error) {
        this.$message.error('操作失败: ' + (error.message || '未知错误'))
      }
    },
    async submitBid() {
      if (!this.selectedAuction) return
      
      try {
        const bidData = {
          auctionId: this.selectedAuction.id,
          userId: 1, // 模拟用户ID
          bidPrice: this.bidForm.bidPrice,
          bidStatus: 1
        }
        
        const response = await auctionApi.submitBid(this.selectedAuction.id, bidData)
        if (response.code === 200) {
          this.$message.success('出价成功!')
          this.bidDialogVisible = false
          this.loadAuctions() // 重新加载拍卖列表以更新价格
        } else {
          this.$message.error(response.message || '出价失败')
        }
      } catch (error) {
        this.$message.error('出价失败: ' + (error.message || '未知错误'))
      }
    },
    viewAuctionDetail(auctionId) {
      this.$router.push(`/auctions/${auctionId}`)
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
    canBid(auction) {
      // 检查是否可以出价
      if (!auction) return false
      // 只有进行中的拍卖才能出价
      if (auction.auctionStatus !== 2) return false
      // 降价拍卖也可以出价（接受当前价格）
      return true
    },
    getMinBidPrice() {
      if (!this.selectedAuction) return 0
      
      // 降价拍卖必须等于当前价格
      if (this.selectedAuction.auctionType === 5) {
        return this.selectedAuction.currentPrice
      }
      
      // 其他拍卖类型至少要比当前价格高一个加价幅度
      if (this.selectedAuction.bidIncrement) {
        return this.selectedAuction.currentPrice + this.selectedAuction.bidIncrement
      }
      
      // 默认至少高100
      return this.selectedAuction.currentPrice + 100
    }
  },
  mounted() {
    this.loadAuctions()
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