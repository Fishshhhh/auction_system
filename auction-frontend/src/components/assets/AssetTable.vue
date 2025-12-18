<template>
  <el-table :data="assets" stripe style="width: 100%" class="assets-table">
    <el-table-column prop="assetNo" label="资产编号" min-width="120">
      <template slot-scope="scope">
        <span class="asset-no">{{ scope.row.assetNo }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="name" label="资产名称" min-width="150">
      <template slot-scope="scope">
        <span class="asset-name">{{ scope.row.name }}</span>
      </template>
    </el-table-column>
    <el-table-column label="资产分类" min-width="120">
      <template slot-scope="scope">
        <el-tag v-if="categories && categories.length > 0" type="info" class="category-tag">
          {{ getCategoryName(scope.row.categoryId) }}
        </el-tag>
        <span v-else class="loading-text">加载中...</span>
      </template>
    </el-table-column>
    <el-table-column prop="description" label="描述" min-width="200">
      <template slot-scope="scope">
        <span class="asset-description">{{ scope.row.description }}</span>
      </template>
    </el-table-column>
    <el-table-column label="资产属性" min-width="200">
      <template slot-scope="scope">
        <div v-if="scope.row.properties">
          <el-tag 
            v-for="(value, key) in parseProperties(scope.row.properties)" 
            :key="key" 
            size="mini" 
            class="property-tag">
            {{ key }}: {{ value }}
          </el-tag>
        </div>
        <span v-else class="no-properties">无属性</span>
      </template>
    </el-table-column>
    <el-table-column label="创建时间" min-width="150">
      <template slot-scope="scope">
        <span class="time-text">{{ formatDate(scope.row.createdTime) }}</span>
      </template>
    </el-table-column>
    <el-table-column label="相关拍卖" min-width="100">
      <template slot-scope="scope">
        <el-button 
          size="small" 
          type="text"
          @click="viewRelatedAuctions(scope.row)"
          class="related-auctions-button">
          相关拍卖
        </el-button>
      </template>
    </el-table-column>
    <el-table-column label="操作" min-width="200">
      <template slot-scope="scope">
        <el-button 
          size="small" 
          type="primary" 
          @click="createAuction(scope.row)"
          class="create-auction-button">
          创建拍卖
        </el-button>
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
    }
  },
  methods: {
    getCategoryName(categoryId) {
      if (!categoryId) return '未分类'
      const category = this.categories.find(c => c.id === categoryId)
      return category ? category.name : '未知分类'
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
    viewRelatedAuctions(asset) {
      this.$emit('view-auctions', asset)
    },
    createAuction(asset) {
      this.$emit('create-auction', asset)
    },
    bidAsset(asset) {
      this.$emit('bid-asset', asset)
    }
  }
}
</script>

<style scoped>
.assets-table {
  margin-top: 20px;
}

.assets-table /deep/ .el-table__header th {
  background-color: #fafafa;
  color: #606266;
  font-weight: 600;
}

.asset-no {
  font-weight: 500;
  color: #409eff;
}

.asset-name {
  font-weight: 600;
  color: #303133;
}

.category-tag {
  font-weight: 500;
}

.asset-description {
  color: #606266;
}

.property-tag {
  margin: 2px;
}

.loading-text,
.no-properties {
  color: #909399;
  font-style: italic;
}

.time-text {
  color: #909399;
}

.related-auctions-button,
.create-auction-button {
  font-weight: 500;
}

.related-auctions-button:hover {
  color: #409eff;
}
</style>