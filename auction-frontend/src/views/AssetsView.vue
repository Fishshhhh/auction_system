<template>
  <div class="assets-view">
    <el-card>
      <div slot="header" class="card-header">
        <span>资产列表</span>
        <el-button v-if="isAdmin()" type="primary" @click="showCreateDialog">新增资产</el-button>
      </div>
      
      <asset-table 
        :assets="assets" 
        :categories="categories"
        :loading="loading"
        @view-detail="viewAssetDetail"
        @edit="editAsset"
        @delete="deleteAsset"
        @publish="publishAsset"
        @unpublish="unpublishAsset"
        @view-auctions="viewRelatedAuctions"
        @create-auction="createAuction"
        @bid-asset="bidAsset"
      />
    </el-card>
    
    <!-- 创建/编辑资产对话框 -->
    <create-asset-dialog
      :visible="createDialogVisible"
      :asset="editingAsset"
      :categories="categories"
      @close="createDialogVisible = false"
      @save="saveAsset"
    />
    
    <!-- 资产详情对话框 -->
    <asset-detail-dialog
      :visible="detailDialogVisible"
      :asset="selectedAsset"
      @close="detailDialogVisible = false"
    />
    
    <!-- 相关拍卖对话框 -->
    <related-auctions-dialog
      :visible="auctionsDialogVisible"
      :asset="selectedAsset"
      :auctions="relatedAuctions"
      @close="auctionsDialogVisible = false"
    />
    
    <!-- 创建拍卖对话框 -->
    <create-auction-dialog
      :visible="createAuctionDialogVisible"
      :asset="selectedAsset"
      :categories="categories"
      @close="createAuctionDialogVisible = false"
      @success="onCreateAuctionSuccess"
      @submit="handleCreateAuction"
    />
  </div>
</template>

<script>
import { assetApi } from '@/api/assetApi';
import { configApi } from '@/api/configApi';
import { auctionApi } from '@/api/auctionApi';
import AssetTable from '@/components/assets/AssetTable.vue';
import CreateAssetDialog from '@/components/assets/CreateAssetDialog.vue';
import AssetDetailDialog from '@/components/assets/AssetDetailDialog.vue';
import RelatedAuctionsDialog from '@/components/assets/RelatedAuctionsDialog.vue';
import CreateAuctionDialog from '@/components/assets/CreateAuctionDialog.vue';

export default {
  name: 'AssetsView',
  components: {
    AssetTable,
    CreateAssetDialog,
    AssetDetailDialog,
    RelatedAuctionsDialog,
    CreateAuctionDialog
  },
  data() {
    return {
      assets: [],
      categories: [],
      loading: false,
      createDialogVisible: false,
      detailDialogVisible: false,
      auctionsDialogVisible: false,
      createAuctionDialogVisible: false,
      editingAsset: null,
      selectedAsset: null,
      relatedAuctions: []
    };
  },
  computed: {
    currentUser() {
      return this.$store.state.user.currentUser;
    }
  },
  methods: {
    async loadAssets() {
      this.loading = true;
      try {
        const response = await assetApi.getAssets();
        if (response.code === 200) {
          this.assets = response.data || [];
        }
      } catch (error) {
        this.$message.error('加载资产列表失败: ' + (error.message || '未知错误'));
      } finally {
        this.loading = false;
      }
    },
    
    async loadCategories() {
      try {
        const response = await configApi.getCategories();
        if (response.code === 200) {
          this.categories = response.data || [];
        }
      } catch (error) {
        this.$message.error('加载资产分类失败: ' + (error.message || '未知错误'));
      }
    },
    
    showCreateDialog() {
      // 关闭其他所有对话框
      this.closeAllDialogs();
      this.editingAsset = null;
      this.createDialogVisible = true;
    },
    
    editAsset(asset) {
      // 使用Object.assign替代对象展开运算符，以兼容旧版本浏览器
      // 关闭其他所有对话框
      this.closeAllDialogs();
      this.editingAsset = Object.assign({}, asset);
      this.createDialogVisible = true;
    },
    
    async saveAsset(asset) {
      try {
        let response;
        if (asset.id) {
          // 更新资产
          response = await assetApi.updateAsset(asset.id, asset);
        } else {
          // 创建资产
          response = await assetApi.createAsset(asset);
        }
        
        if (response.code === 200) {
          this.$message.success(asset.id ? '更新成功' : '创建成功');
          this.createDialogVisible = false;
          this.loadAssets();
        } else {
          this.$message.error(response.message || (asset.id ? '更新失败' : '创建失败'));
        }
      } catch (error) {
        this.$message.error((asset.id ? '更新失败' : '创建失败') + ': ' + (error.message || '未知错误'));
      }
    },
    
    async deleteAsset(asset) {
      try {
        await this.$confirm('确认删除资产 "' + asset.name + '"?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        
        const response = await assetApi.deleteAsset(asset.id);
        if (response.code === 200) {
          this.$message.success('删除成功');
          this.loadAssets();
        } else {
          this.$message.error(response.message || '删除失败');
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败: ' + (error.message || '未知错误'));
        }
      }
    },
    
    async publishAsset(asset) {
      try {
        const response = await assetApi.publishAsset(asset.id);
        if (response.code === 200) {
          this.$message.success('发布成功');
          this.loadAssets();
        } else {
          this.$message.error(response.message || '发布失败');
        }
      } catch (error) {
        this.$message.error('发布失败: ' + (error.message || '未知错误'));
      }
    },
    
    async unpublishAsset(asset) {
      try {
        const response = await assetApi.unpublishAsset(asset.id);
        if (response.code === 200) {
          this.$message.success('取消发布成功');
          this.loadAssets();
        } else {
          this.$message.error(response.message || '取消发布失败');
        }
      } catch (error) {
        this.$message.error('取消发布失败: ' + (error.message || '未知错误'));
      }
    },
    
    viewAssetDetail(asset) {
      // 关闭其他所有对话框
      this.closeAllDialogs();
      this.selectedAsset = asset;
      this.detailDialogVisible = true;
    },
    
    async viewRelatedAuctions(asset) {
      // 关闭其他所有对话框
      this.closeAllDialogs();
      this.selectedAsset = asset;
      
      // 获取相关拍卖数据
      try {
        // 先获取所有拍卖
        const auctionsResponse = await auctionApi.getAuctions();
        if (auctionsResponse.code === 200) {
          const allAuctions = auctionsResponse.data || [];
          
          // 获取所有拍卖与资产的关联关系
          const auctionAssetsPromises = allAuctions.map(auction => 
            auctionApi.getAuctionAssets(auction.id)
          );
          
          const auctionAssetsResponses = await Promise.all(auctionAssetsPromises);
          
          // 过滤出包含当前资产的拍卖
          this.relatedAuctions = allAuctions.filter((auction, index) => {
            const auctionAssetsResponse = auctionAssetsResponses[index];
            if (auctionAssetsResponse.code === 200) {
              const auctionAssets = auctionAssetsResponse.data || [];
              return auctionAssets.some(auctionAsset => auctionAsset.assetId === asset.id);
            }
            return false;
          });
        } else {
          this.relatedAuctions = [];
        }
      } catch (error) {
        console.error('获取相关拍卖数据失败:', error);
        this.relatedAuctions = [];
        this.$message.error('获取相关拍卖数据失败: ' + (error.message || '未知错误'));
      }
      
      this.auctionsDialogVisible = true;
    },
    
    createAuction(asset) {
      // 关闭其他所有对话框
      this.closeAllDialogs();
      this.selectedAsset = asset;
      this.createAuctionDialogVisible = true;
    },
    
    async handleCreateAuction(form) {
      try {
        const response = await auctionApi.createAuction(form);
        if (response.code === 200) {
          this.$message.success('创建拍卖成功');
          this.createAuctionDialogVisible = false;
          this.loadAssets();
        } else {
          this.$message.error(response.message || '创建拍卖失败');
        }
      } catch (error) {
        this.$message.error('创建拍卖失败: ' + (error.message || '未知错误'));
      }
    },
    
    bidAsset(asset) {
      // 关闭其他所有对话框
      this.closeAllDialogs();
      // 实现出价功能
      this.$message.info('出价功能待实现');
    },
    
    closeAllDialogs() {
      this.createDialogVisible = false;
      this.detailDialogVisible = false;
      this.auctionsDialogVisible = false;
      this.createAuctionDialogVisible = false;
    },
    
    onCreateAuctionSuccess() {
      this.createAuctionDialogVisible = false;
      this.loadAssets();
    },
    
    isAdmin() {
      return this.currentUser && this.currentUser.userType === 1;
    }
  },
  mounted() {
    this.loadAssets();
    this.loadCategories();
  }
};
</script>

<style scoped>
.assets-view {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>