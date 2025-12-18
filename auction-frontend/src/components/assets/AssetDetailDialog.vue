<template>
  <el-dialog :visible.sync="visible" title="资产详情" width="600px" @close="onClose">
    <div v-if="asset">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="资产名称">{{ asset.name }}</el-descriptions-item>
        <el-descriptions-item label="资产编号">{{ asset.assetNo }}</el-descriptions-item>
        <el-descriptions-item label="描述">{{ asset.description }}</el-descriptions-item>
        <el-descriptions-item label="起拍价">¥{{ asset.startingPrice }}</el-descriptions-item>
        <el-descriptions-item label="保留价">¥{{ asset.reservePrice }}</el-descriptions-item>
        <el-descriptions-item label="当前价">¥{{ asset.currentPrice }}</el-descriptions-item>
        <el-descriptions-item label="资产状态">
          <el-tag :type="getAssetStatusType(asset.status)">
            {{ getAssetStatusText(asset.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="asset.properties" label="资产属性">
          <div v-for="(value, key) in parseProperties(asset.properties)" :key="key">
            <strong>{{ key }}:</strong> {{ value }}
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(asset.createdTime) }}</el-descriptions-item>
      </el-descriptions>
      
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
      
      <div v-if="asset.images && asset.images.length > 0" class="image-gallery">
        <el-carousel height="300px">
          <el-carousel-item v-for="image in asset.images" :key="image.id">
            <img :src="image.imageUrl" alt="资产图片" class="carousel-image">
          </el-carousel-item>
        </el-carousel>
      </div>
    </div>
    
    <div slot="footer" class="dialog-footer">
      <el-button @click="onClose">关闭</el-button>
      <el-button 
        type="primary" 
        @click="handleCreateAuction">
        创建拍卖
      </el-button>
      <el-button 
        v-if="asset && asset.auctionStatus === 2" 
        type="warning" 
        @click="handleBidAsset">
        出价
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'AssetDetailDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    asset: {
      type: Object,
      default: () => null
    },
    relatedAuctions: {
      type: Array,
      default: () => []
    }
  },
  methods: {
    onClose() {
      this.$emit('update:visible', false)
    },
    handleCreateAuction() {
      this.$emit('create-auction', this.asset)
    },
    handleBidAsset() {
      this.$emit('bid-asset', this.asset)
    },
    viewAuctionDetail(auctionId) {
      this.$emit('view-auction-detail', auctionId)
    },
    formatDate(dateString) {
      if (!dateString) return ''
      return new Date(dateString).toLocaleString('zh-CN')
    },
    parseProperties(properties) {
      try {
        return JSON.parse(properties)
      } catch (e) {
        console.error('解析资产属性失败:', e)
        return {}
      }
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
    }
  }
}
</script>

<style scoped>
.image-gallery {
  margin-top: 20px;
}

.carousel-image {
  width: 100%;
  height: 300px;
  object-fit: cover;
}

.dialog-footer {
  text-align: right;
}
</style>