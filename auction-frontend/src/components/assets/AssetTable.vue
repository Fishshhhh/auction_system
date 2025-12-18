<template>
  <el-table 
    :data="assets" 
    stripe 
    style="width: 100%" 
    class="professional-table assets-table" 
    v-loading="loading"
    :header-cell-style="{background: 'linear-gradient(180deg, #f5f7fa, #ebedf0)', color: '#606266', fontWeight: '600'}"
    :row-class-name="tableRowClassName">
    <el-table-column prop="imageUrl" label="资产图片" min-width="100">
      <template slot-scope="scope">
        <div class="cell-content image-cell">
          <div v-if="scope.row.images && scope.row.images.length > 0" class="image-container">
            <img 
              :src="getCoverImage(scope.row.images)" 
              :alt="scope.row.name" 
              class="asset-image"
              @error="handleImageError"
            >
          </div>
          <div v-else class="no-image">
            <i class="el-icon-picture-outline"></i>
          </div>
        </div>
      </template>
    </el-table-column>
    <el-table-column prop="assetNo" label="资产编号" min-width="130">
      <template slot-scope="scope">
        <div class="cell-content">
          <el-tag type="primary" size="mini" class="id-tag">{{ scope.row.assetNo }}</el-tag>
        </div>
      </template>
    </el-table-column>
    <el-table-column prop="name" label="资产名称" min-width="160">
      <template slot-scope="scope">
        <div class="cell-content asset-name-cell">
          <span class="asset-name">{{ scope.row.name }}</span>
        </div>
      </template>
    </el-table-column>
    <el-table-column label="资产分类" min-width="120">
      <template slot-scope="scope">
        <div class="cell-content">
          <el-tag v-if="categories && categories.length > 0" :type="getCategoryTagType(scope.row.categoryId)" class="category-tag">
            {{ getCategoryName(scope.row.categoryId) }}
          </el-tag>
          <span v-else class="loading-text">未分类</span>
        </div>
      </template>
    </el-table-column>
    <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip>
      <template slot-scope="scope">
        <div class="cell-content">
          <span class="asset-description">{{ scope.row.description || '-' }}</span>
        </div>
      </template>
    </el-table-column>
    <el-table-column label="资产属性" min-width="200">
      <template slot-scope="scope">
        <div class="cell-content">
          <div v-if="scope.row.properties && Object.keys(parseProperties(scope.row.properties)).length > 0">
            <el-popover
              placement="right"
              trigger="hover"
              width="300">
              <div class="properties-detail">
                <div class="properties-header">
                  资产属性详情
                </div>
                <div 
                  v-for="(value, key) in parseProperties(scope.row.properties)" 
                  :key="key" 
                  class="property-item">
                  <span class="property-key">{{ key }}:</span>
                  <span class="property-value">{{ value }}</span>
                </div>
              </div>
              <div slot="reference" class="properties-summary">
                <el-tag 
                  v-for="(value, key) in getFirstTwoProperties(scope.row.properties)" 
                  :key="key" 
                  size="mini" 
                  class="property-tag">
                  {{ key }}: {{ value }}
                </el-tag>
                <el-tag v-if="getPropertyCount(scope.row.properties) > 2" size="mini" type="info">
                  +{{ getPropertyCount(scope.row.properties) - 2 }}
                </el-tag>
              </div>
            </el-popover>
          </div>
          <span v-else class="no-properties">-</span>
        </div>
      </template>
    </el-table-column>
    <el-table-column label="创建时间" min-width="160">
      <template slot-scope="scope">
        <div class="cell-content">
          <div class="time-content">
            <div class="time-text">{{ formatDate(scope.row.createdTime) }}</div>
            <div class="time-diff">{{ getTimeDiff(scope.row.createdTime) }}</div>
          </div>
        </div>
      </template>
    </el-table-column>
    <el-table-column label="相关拍卖" min-width="110" align="center">
      <template slot-scope="scope">
        <div class="cell-content center-content">
          <el-button 
            size="mini" 
            type="text"
            @click="viewRelatedAuctions(scope.row)"
            class="related-auctions-button">
            相关拍卖
          </el-button>
        </div>
      </template>
    </el-table-column>
    <el-table-column label="操作" min-width="180" fixed="right" align="center">
      <template slot-scope="scope">
        <div class="cell-content center-content action-buttons">
          <el-button 
            size="mini" 
            type="primary" 
            @click="createAuction(scope.row)"
            class="create-auction-button">
            创建拍卖
          </el-button>
        </div>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
export default {
  name: 'AssetTable',
  props: {
    assets: {
      type: Array,
      default: () => []
    },
    categories: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    getCategoryName(categoryId) {
      if (!categoryId) return '未分类'
      const category = this.categories.find(c => c.id === categoryId)
      return category ? category.name : '未知分类'
    },
    getCategoryTagType(categoryId) {
      const category = this.categories.find(c => c.id === categoryId)
      if (!category) return ''
      
      // 根据分类名称生成不同的标签类型
      const categoryName = category.name
      if (categoryName.includes('设备')) return 'success'
      if (categoryName.includes('房产')) return 'warning'
      if (categoryName.includes('车辆')) return 'danger'
      if (categoryName.includes('股权')) return 'info'
      return 'primary'
    },
    formatDate(dateString) {
      if (!dateString) return '-'
      const date = new Date(dateString)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    },
    getTimeDiff(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      const now = new Date()
      const diffMs = now - date
      const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))
      const diffHours = Math.floor((diffMs % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
      
      if (diffDays > 0) {
        return `${diffDays}天前`
      } else if (diffHours > 0) {
        return `${diffHours}小时前`
      } else {
        return '刚刚'
      }
    },
    parseProperties(properties) {
      try {
        return JSON.parse(properties)
      } catch (e) {
        console.error('解析资产属性失败:', e)
        return {}
      }
    },
    getFirstTwoProperties(properties) {
      try {
        const parsed = JSON.parse(properties)
        const keys = Object.keys(parsed)
        const result = {}
        keys.slice(0, 2).forEach(key => {
          result[key] = parsed[key]
        })
        return result
      } catch (e) {
        return {}
      }
    },
    getPropertyCount(properties) {
      try {
        return Object.keys(JSON.parse(properties)).length
      } catch (e) {
        return 0
      }
    },
    viewRelatedAuctions(asset) {
      this.$emit('view-auctions', asset)
    },
    createAuction(asset) {
      this.$emit('create-auction', asset)
    },
    tableRowClassName({row, rowIndex}) {
      return 'professional-table-row'
    },
    getCoverImage(images) {
      // 获取封面图片或第一张图片
      const coverImage = images.find(img => img.isCover === 1)
      return coverImage ? coverImage.imageUrl : images[0].imageUrl
    },
    handleImageError(event) {
      // 图片加载失败时显示默认图标
      event.target.src = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIiB2aWV3Qm94PSIwIDAgMjQgMjQiIGZpbGw9IiNmZmYiPjxwYXRoIGQ9Ik04LjUgMTMuNWwyLjUgMyAzLjUtNC41IDQuNSA2SDVtMTYgMS41YTkgOSAwIDAgMS0xOCAwIDkgOSAwIDAgMSAxOCAweiIvPjwvc3ZnPg=='
    }
  }
}
</script>

<style scoped>
.assets-table ::v-deep .el-table__header th {
  background: linear-gradient(180deg, #f5f7fa, #ebedf0) !important;
  color: #606266;
  font-weight: 600;
  font-size: 14px;
}

.assets-table ::v-deep .el-table__row:hover {
  background-color: #f0f8ff !important;
}

.assets-table ::v-deep .el-table__row.professional-table-row {
  transition: all 0.3s ease;
}

.assets-table ::v-deep .el-table__row.professional-table-row:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.cell-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 8px 0;
}

.center-content {
  justify-content: center;
}

.image-cell {
  justify-content: center;
}

.image-container {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border: 1px solid #ebeef5;
}

.asset-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border: 1px dashed #dcdfe6;
  color: #c0c4cc;
  font-size: 20px;
}

.asset-name-cell {
  display: flex;
  align-items: center;
}

.id-tag {
  font-weight: 600;
  background: linear-gradient(135deg, #409eff, #74b9ff);
  border: none;
  color: white;
}

.asset-name {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

.category-tag {
  font-weight: 500;
}

.asset-description {
  color: #606266;
  font-size: 13px;
}

.property-tag {
  margin: 2px;
}

.loading-text,
.no-properties {
  color: #909399;
  font-style: italic;
}

.time-content {
  display: flex;
  flex-direction: column;
}

.time-text {
  color: #606266;
  font-size: 13px;
  font-weight: 500;
}

.time-diff {
  color: #909399;
  font-size: 12px;
  margin-top: 2px;
}

.related-auctions-button,
.create-auction-button {
  font-weight: 500;
  padding: 7px 12px;
}

.related-auctions-button:hover {
  color: #409eff;
}

.action-buttons {
  gap: 5px;
}

.properties-summary {
  display: flex;
  flex-wrap: wrap;
  gap: 3px;
}

.properties-detail {
  max-width: 300px;
}

.properties-header {
  font-weight: 600;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
  color: #409eff;
}

.property-item {
  display: flex;
  margin-bottom: 8px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.property-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.property-key {
  font-weight: 600;
  margin-right: 10px;
  color: #303133;
  min-width: 80px;
}

.property-value {
  color: #606266;
  flex: 1;
}

</style>