<template>
  <div class="assets-view">
    <el-card>
      <div slot="header" class="card-header">
        <span>资产列表</span>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索资产..."
          style="width: 300px;"
          @keyup.enter.native="searchAssets"
        >
          <el-button slot="append" icon="el-icon-search" @click="searchAssets"></el-button>
        </el-input>
      </div>
      
      <el-table :data="assets" stripe style="width: 100%">
        <el-table-column prop="assetNo" label="资产编号" min-width="120"></el-table-column>
        <el-table-column prop="name" label="资产名称" min-width="150"></el-table-column>
        <el-table-column label="资产分类" min-width="120">
          <template slot-scope="scope">
            {{ getCategoryName(scope.row.categoryId) }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200"></el-table-column>
        <el-table-column label="创建时间" min-width="150">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column label="相关拍卖" min-width="100">
          <template slot-scope="scope">
            <el-button 
              v-if="scope.row.auctionStatus" 
              size="small" 
              @click="viewRelatedAuctions(scope.row)">
              查看
            </el-button>
            <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="200">
          <template slot-scope="scope">
            <el-button 
              v-if="scope.row.status === 4" 
              size="small" 
              type="primary" 
              @click="createAuction(scope.row)">
              创建拍卖
            </el-button>
            <el-button 
              v-if="scope.row.auctionStatus === 2" 
              size="small" 
              type="warning" 
              @click="bidAsset(scope.row)">
              出价
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
    
    <!-- 资产详情对话框 -->
    <el-dialog :visible.sync="dialogVisible" title="资产详情" width="600px">
      <div v-if="selectedAsset">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="资产名称">{{ selectedAsset.name }}</el-descriptions-item>
          <el-descriptions-item label="资产编号">{{ selectedAsset.assetNo }}</el-descriptions-item>
          <el-descriptions-item label="描述">{{ selectedAsset.description }}</el-descriptions-item>
          <el-descriptions-item label="起拍价">¥{{ selectedAsset.startingPrice }}</el-descriptions-item>
          <el-descriptions-item label="保留价">¥{{ selectedAsset.reservePrice }}</el-descriptions-item>
          <el-descriptions-item label="当前价">¥{{ selectedAsset.currentPrice }}</el-descriptions-item>
          <el-descriptions-item label="资产状态">
            <el-tag :type="getAssetStatusType(selectedAsset.status)">
              {{ getAssetStatusText(selectedAsset.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(selectedAsset.createdTime) }}</el-descriptions-item>
        </el-descriptions>
        
        <!-- 相关拍卖信息 -->
        <div v-if="relatedAuctions && relatedAuctions.length > 0" style="margin-top: 20px;">
          <h4>相关拍卖</h4>
          <el-table :data="relatedAuctions" stripe style="width: 100%">
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
        
        <div v-if="selectedAsset.images && selectedAsset.images.length > 0" class="image-gallery">
          <el-carousel height="300px">
            <el-carousel-item v-for="image in selectedAsset.images" :key="image.id">
              <img :src="image.imageUrl" alt="资产图片" class="carousel-image">
            </el-carousel-item>
          </el-carousel>
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button 
          v-if="selectedAsset && selectedAsset.status === 4" 
          type="primary" 
          @click="createAuction(selectedAsset)">
          创建拍卖
        </el-button>
        <el-button 
          v-if="selectedAsset && selectedAsset.auctionStatus === 2" 
          type="warning" 
          @click="bidAsset(selectedAsset)">
          出价
        </el-button>
      </div>
    </el-dialog>
    
    <!-- 相关拍卖对话框 -->
    <el-dialog :visible.sync="relatedAuctionsDialogVisible" title="相关拍卖信息" width="800px">
      <div v-if="selectedAsset">
        <h4>资产：{{ selectedAsset.name }}</h4>
        <el-table :data="relatedAuctions" stripe style="width: 100%">
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
        <el-button @click="relatedAuctionsDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
    
    <!-- 创建拍卖对话框 -->
    <el-dialog :visible.sync="auctionDialogVisible" title="创建拍卖" width="600px">
      <el-form :model="auctionForm" label-width="120px">
        <el-form-item label="资产名称">
          <span>{{ selectedAsset ? selectedAsset.name : '' }}</span>
        </el-form-item>
        <el-form-item label="资产数量" prop="quantity">
          <el-input-number v-model="auctionForm.quantity" :min="1" :max="999" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="起拍价" prop="startPrice">
          <el-input-number v-model="auctionForm.startPrice" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="保留价" prop="reservePrice">
          <el-input-number v-model="auctionForm.reservePrice" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="拍卖类型" prop="auctionType">
          <el-select v-model="auctionForm.auctionType" placeholder="请选择拍卖类型" style="width: 100%">
            <el-option label="公开实时竞价" :value="1"></el-option>
            <el-option label="暗拍" :value="2"></el-option>
            <el-option label="无底价拍卖" :value="3"></el-option>
            <el-option label="定向拍卖" :value="4"></el-option>
            <el-option label="降价拍卖" :value="5"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="拍卖开始时间" prop="startTime">
          <el-date-picker
            v-model="auctionForm.startTime"
            type="datetime"
            placeholder="选择拍卖开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="拍卖结束时间" prop="endTime">
          <el-date-picker
            v-model="auctionForm.endTime"
            type="datetime"
            placeholder="选择拍卖结束时间"
            style="width: 100%"
          />
        </el-form-item>
        
        <!-- 公开竞价和无底价拍卖参数 -->
        <template v-if="auctionForm.auctionType === 1 || auctionForm.auctionType === 3">
          <el-form-item label="加价幅度" prop="bidIncrement">
            <el-input-number v-model="auctionForm.bidIncrement" :min="0" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="延时时间(分钟)" prop="extendTime">
            <el-input-number v-model="auctionForm.extendTime" :min="0" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="延时阈值(秒)" prop="extendThreshold">
            <el-input-number v-model="auctionForm.extendThreshold" :min="0" controls-position="right" style="width: 100%" />
          </el-form-item>
        </template>
        
        <!-- 降价拍卖参数 -->
        <template v-if="auctionForm.auctionType === 5">
          <el-form-item label="初始价" prop="initialPrice">
            <el-input-number v-model="auctionForm.initialPrice" :min="0" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="降价阶梯" prop="priceStep">
            <el-input-number v-model="auctionForm.priceStep" :min="0" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="降价周期(分钟)" prop="priceDropInterval">
            <el-input-number v-model="auctionForm.priceDropInterval" :min="1" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="最低价" prop="minPrice">
            <el-input-number v-model="auctionForm.minPrice" :min="0" controls-position="right" style="width: 100%" />
          </el-form-item>
        </template>
        
        <!-- 定向拍卖参数 -->
        <template v-if="auctionForm.auctionType === 4">
          <el-form-item label="需要资格认证" prop="qualificationRequired">
            <el-switch v-model="auctionForm.qualificationRequired"></el-switch>
          </el-form-item>
        </template>
        
        <!-- 保证金参数 -->
        <el-form-item label="需要保证金" prop="depositRequired">
          <el-switch v-model="auctionForm.depositRequired"></el-switch>
        </el-form-item>
        <el-form-item v-if="auctionForm.depositRequired" label="保证金金额" prop="depositAmount">
          <el-input-number v-model="auctionForm.depositAmount" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="auctionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAuction">创建拍卖</el-button>
      </div>
    </el-dialog>
    
    <!-- 出价对话框 -->
    <el-dialog :visible.sync="bidDialogVisible" title="出价" width="400px">
      <el-form v-if="selectedAsset" :model="bidForm" label-width="80px">
        <el-form-item label="资产名称">
          <span>{{ selectedAsset.name }}</span>
        </el-form-item>
        <el-form-item label="当前价格">
          <span>¥{{ selectedAsset.currentPrice }}</span>
        </el-form-item>
        <el-form-item label="您的出价" prop="bidPrice">
          <el-input-number
            v-model="bidForm.bidPrice"
            :min="selectedAsset.currentPrice + 100"
            :step="100"
            controls-position="right"
          />
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="bidDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBid">提交出价</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { assetApi } from '../api/assetApi'
import { configApi } from '../api/configApi'
import { auctionApi } from '../api/auctionApi'

export default {
  name: 'AssetsView',
  data() {
    return {
      assets: [],
      categories: [],
      total: 0,
      pageSize: 10,
      currentPage: 1,
      searchKeyword: '',
      dialogVisible: false,
      relatedAuctionsDialogVisible: false,
      auctionDialogVisible: false,
      bidDialogVisible: false,
      selectedAsset: null,
      relatedAuctions: [],
      bidForm: {
        bidPrice: 0
      },
      auctionForm: {
        assetId: null,
        templateId: null,
        startPrice: 0,
        currentPrice: 0,
        reservePrice: 0,
        buyItNowPrice: null,
        startTime: '',
        endTime: '',
        auctionStatus: 1,
        auctionType: 1,
        bidIncrement: 0,
        extendTime: 0,
        extendThreshold: 0,
        initialPrice: 0,
        priceStep: 0,
        priceDropInterval: 1,
        minPrice: 0,
        nextPriceDropTime: null,
        qualificationRequired: false,
        winnerUserId: null,
        finalPrice: null,
        depositRequired: false,
        depositAmount: 0,
        quantity: 1
      }
    }
  },
  methods: {
    async loadAssets() {
      try {
        const response = await assetApi.getAssets()
        console.log('API Response:', response) // 调试日志
        if (response && response.code === 200) {
          this.assets = response.data || []
          this.total = response.data ? response.data.length : 0
        } else {
          this.$message.error((response && response.message) || '加载资产列表失败')
          this.assets = []
          this.total = 0
        }
      } catch (error) {
        console.error('加载资产列表失败:', error)
        this.$message.error('加载资产列表失败: ' + (error.message || '未知错误'))
        this.assets = []
        this.total = 0
      }
    },
    async loadCategories() {
      try {
        const response = await configApi.getCategories()
        if (response && response.code === 200) {
          this.categories = response.data || []
        } else {
          this.$message.error((response && response.message) || '加载资产分类失败')
        }
      } catch (error) {
        console.error('加载资产分类失败:', error)
        this.$message.error('加载资产分类失败: ' + (error.message || '未知错误'))
      }
    },
    getCategoryName(categoryId) {
      if (!categoryId) return '未分类'
      const category = this.categories.find(c => c.id === categoryId)
      return category ? category.name : '未知分类'
    },
    searchAssets() {
      // 搜索功能可以在这里实现
      this.loadAssets()
    },
    handlePageChange(page) {
      this.currentPage = page
      this.loadAssets()
    },
    async viewAsset(asset) {
      this.selectedAsset = asset
      // 加载资产图片
      try {
        const response = await assetApi.getAssetImages(asset.id)
        if (response.code === 200) {
          this.$set(this.selectedAsset, 'images', response.data)
        }
      } catch (error) {
        console.error('加载资产图片失败:', error)
      }
      
      // 加载相关拍卖
      try {
        const response = await assetApi.getAssetAuctions(asset.id)
        if (response.code === 200) {
          this.relatedAuctions = response.data || []
        }
      } catch (error) {
        console.error('加载相关拍卖失败:', error)
      }
      
      this.dialogVisible = true
    },
    async viewRelatedAuctions(asset) {
      this.selectedAsset = asset
      // 加载相关拍卖
      try {
        const response = await assetApi.getAssetAuctions(asset.id)
        if (response.code === 200) {
          this.relatedAuctions = response.data || []
        }
      } catch (error) {
        console.error('加载相关拍卖失败:', error)
        this.$message.error('加载相关拍卖失败: ' + (error.message || '未知错误'))
        this.relatedAuctions = []
      }
      
      this.relatedAuctionsDialogVisible = true
    },
    async publishAsset(asset) {
      try {
        const response = await assetApi.publishAsset(asset.id)
        if (response.code === 200) {
          this.$message.success('资产上架成功')
          this.loadAssets() // 重新加载资产列表
        } else {
          this.$message.error(response.message || '资产上架失败')
        }
      } catch (error) {
        this.$message.error('资产上架失败: ' + (error.message || '未知错误'))
      }
    },
    createAuction(asset) {
      this.selectedAsset = asset
      // 初始化拍卖表单
      this.auctionForm = {
        assetId: asset.id,
        templateId: null,
        startPrice: asset.startingPrice || 0,
        currentPrice: asset.startingPrice || 0,
        reservePrice: asset.reservePrice || 0,
        buyItNowPrice: null,
        startTime: asset.auctionStartTime || '',
        endTime: asset.auctionEndTime || '',
        auctionStatus: 1, // 未开始
        auctionType: asset.auctionType || 1,
        bidIncrement: 0,
        extendTime: 0,
        extendThreshold: 0,
        initialPrice: asset.startingPrice || 0,
        priceStep: 0,
        priceDropInterval: 1,
        minPrice: 0,
        nextPriceDropTime: null,
        qualificationRequired: false,
        winnerUserId: null,
        finalPrice: null,
        depositRequired: false,
        depositAmount: 0,
        quantity: 1
      }
      this.auctionDialogVisible = true
    },
    async saveAuction() {
      try {
        // 创建拍卖
        const response = await auctionApi.createAuction(this.auctionForm)
        
        if (response.code === 200) {
          this.$message.success('拍卖创建成功')
          this.auctionDialogVisible = false
          this.loadAssets() // 重新加载资产列表以更新状态
          
          // 添加拍卖资产关联
          if (this.selectedAsset && response.data && response.data.id) {
            const auctionAssetResponse = await auctionApi.addAssetToAuction(response.data.id, {
              assetId: this.selectedAsset.id,
              quantity: this.auctionForm.quantity,
              startPrice: this.auctionForm.startPrice,
              currentPrice: this.auctionForm.currentPrice,
              reservePrice: this.auctionForm.reservePrice
            })
            
            if (auctionAssetResponse.code !== 200) {
              this.$message.warning('拍卖资产关联创建失败: ' + (auctionAssetResponse.message || '未知错误'))
            }
          }
        } else {
          this.$message.error(response.message || '拍卖创建失败')
        }
      } catch (error) {
        this.$message.error('拍卖创建失败: ' + (error.message || '未知错误'))
      }
    },
    bidAsset(asset) {
      this.selectedAsset = asset
      this.bidForm.bidPrice = asset.currentPrice + 100
      this.bidDialogVisible = true
    },
    async submitBid() {
      if (!this.selectedAsset) return
      
      try {
        const bidData = {
          auctionId: this.selectedAsset.id,
          userId: 1, // 模拟用户ID
          bidPrice: this.bidForm.bidPrice,
          bidStatus: 1
        }
        
        const response = await assetApi.submitBid(this.selectedAsset.id, bidData)
        if (response.code === 200) {
          this.$message.success('出价成功!')
          this.bidDialogVisible = false
          this.loadAssets() // 重新加载资产列表以更新价格
        } else {
          this.$message.error(response.message || '出价失败')
        }
      } catch (error) {
        this.$message.error('出价失败: ' + (error.message || '未知错误'))
      }
    },
    viewAuctionDetail(auctionId) {
      this.dialogVisible = false
      this.relatedAuctionsDialogVisible = false
      this.$router.push(`/auctions/${auctionId}`)
    },
    formatDate(dateString) {
      if (!dateString) return ''
      return new Date(dateString).toLocaleString('zh-CN')
    },
    getAssetStatusText(status) {
      const statusMap = {
        1: '待审核',
        2: '审核通过',
        3: '审核拒绝',
        4: '已上架',
        5: '已下架',
        6: '已售出'
      }
      return statusMap[status] || '未知'
    },
    getAssetStatusType(status) {
      const typeMap = {
        1: '',        // 待审核
        2: 'success', // 审核通过
        3: 'danger',  // 审核拒绝
        4: 'primary', // 已上架
        5: 'info',    // 已下架
        6: 'warning'  // 已售出
      }
      return typeMap[status] || ''
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
    }
  },
  mounted() {
    this.loadAssets()
    this.loadCategories()
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}

.image-gallery {
  margin-top: 20px;
}

.carousel-image {
  width: 100%;
  height: 300px;
  object-fit: cover;
}
</style>