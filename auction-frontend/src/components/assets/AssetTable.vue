<template>
  <el-table :data="assets" stripe style="width: 100%">
    <el-table-column prop="assetNo" label="资产编号" min-width="120"></el-table-column>
    <el-table-column prop="name" label="资产名称" min-width="150"></el-table-column>
    <el-table-column label="资产分类" min-width="120">
      <template slot-scope="scope">
        <span v-if="categories && categories.length > 0">{{ getCategoryName(scope.row.categoryId) }}</span>
        <span v-else>加载中...</span>
      </template>
    </el-table-column>
    <el-table-column prop="description" label="描述" min-width="200"></el-table-column>
    <el-table-column label="资产属性" min-width="200">
      <template slot-scope="scope">
        <div v-if="scope.row.properties">
          <div v-for="(value, key) in parseProperties(scope.row.properties)" :key="key">
            <el-tag size="mini" style="margin: 2px;">{{ key }}: {{ value }}</el-tag>
          </div>
        </div>
        <span v-else>无属性</span>
      </template>
    </el-table-column>
    <el-table-column label="创建时间" min-width="150">
      <template slot-scope="scope">
        {{ formatDate(scope.row.createdTime) }}
      </template>
    </el-table-column>
    <el-table-column label="相关拍卖" min-width="100">
      <template slot-scope="scope">
        <el-button 
          size="small" 
          @click="viewRelatedAuctions(scope.row)">
          相关拍卖
        </el-button>
      </template>
    </el-table-column>
    <el-table-column label="操作" min-width="200">
      <template slot-scope="scope">
        <el-button 
          size="small" 
          type="primary" 
          @click="createAuction(scope.row)">
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
</style>