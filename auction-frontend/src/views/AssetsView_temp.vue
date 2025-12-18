<template>
  <div class="assets-view">
    <el-card>
      <div slot="header" class="card-header">
        <span>资产列表</span>
        <div>
          <el-button type="primary" @click="showCreateDialog">添加资产</el-button>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索资产..."
            style="width: 300px; margin-left: 20px;"
            @keyup.enter.native="searchAssets"
          >
            <el-button slot="append" icon="el-icon-search" @click="searchAssets"></el-button>
          </el-input>
        </div>
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
        <!-- 添加资产属性列 -->
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
            <span v-if="scope.row.createdTime">{{ formatDate(scope.row.createdTime) }}</span>
            <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column label="相关拍卖" min-width="100">
          <template slot-scope="scope">
            <el-button 
              size="small" 
              @click="viewRelatedAuctions(scope.row)">
              查看
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
          <!-- 显示资产属性 -->
          <el-descriptions-item v-if="selectedAsset.properties" label="资产属性">
            <div v-for="(value, key) in parseProperties(selectedAsset.properties)" :key="key">
              <strong>{{ key }}:</strong> {{ value }}
            </div>
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
    
    <!-- 创建/编辑资产对话框 -->
    <el-dialog :visible.sync="createDialogVisible" :title="editingAsset ? '编辑资产' : '创建资产'" width="600px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="资产名称" prop="name">
          <el-input v-model="createForm.name" />
        </el-form-item>
        <el-form-item label="资产分类" prop="categoryId">
          <el-select v-model="createForm.categoryId" placeholder="请选择资产分类" style="width: 100%" @change="onCategoryChange">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id">
            </el-option>
          </el-select>
        </el-form-item>
        
        <!-- 动态属性字段 -->
        <div v-if="categoryProperties">
          <el-form-item 
            v-for="(config, key) in categoryProperties" 
            :key="key"
            :label="key">
            <!-- 如果是数组类型，则显示下拉选择框 -->
            <el-select 
              v-if="Array.isArray(config)" 
              v-model="dynamicProperties[key]" 
              placeholder="请选择"
              style="width: 100%">
              <el-option
                v-for="option in config"
                :key="option"
                :label="option"
                :value="option">
              </el-option>
            </el-select>
            
            <!-- 如果是对象类型，则显示下拉选择框 -->
            <el-select 
              v-else-if="typeof config === 'object' && config !== null" 
              v-model="dynamicProperties[key]" 
              placeholder="请选择"
              style="width: 100%">
              <el-option
                v-for="(option, optionKey) in config"
                :key="optionKey"
                :label="optionKey"
                :value="optionKey">
              </el-option>
            </el-select>
            
            <!-- 否则显示文本输入框 -->
            <el-input 
              v-else 
              v-model="dynamicProperties[key]" 
              placeholder="请输入内容">
            </el-input>
          </el-form-item>
        </div>
        
        <el-form-item label="描述" prop="description">
          <el-input v-model="createForm.description" type="textarea" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAsset">保存</el-button>
      </div>
    </el-dialog>
    
    <!-- 引用创建拍卖对话框组件 -->
    <CreateAuctionDialog 
      :visible.sync="auctionDialogVisible" 
      :asset="selectedAsset"
      @submit="saveAuction"
    />
  </div>
</template>

<script>
import { assetApi } from '../api/assetApi'
import { configApi } from '../api/configApi'
import { auctionApi } from '../api/auctionApi'
import CreateAuctionDialog from '../components/assets/CreateAuctionDialog.vue'

export default {
  name: 'AssetsView',
  components: {
    CreateAuctionDialog
  },
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
      // 添加资产相关
      createDialogVisible: false,
      editingAsset: false,
      categoryProperties: null,
      dynamicProperties: {},
      createForm: {
        name: '',
        description: '',
        categoryId: null,
        ownerId: 1 // 默认用户ID
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
          this.total = this.assets.length
          console.log('Loaded assets:', this.assets) // 调试日志
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
        console.log('Categories API Response:', response) // 调试日志
        if (response && response.code === 200) {
          this.categories = response.data || []
          console.log('Loaded categories:', this.categories) // 调试日志
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
      this.auctionDialogVisible = true
    },
    async saveAuction(form) {
      try {
        // 创建拍卖
        const response = await auctionApi.createAuction(form);
        
        if (response.code === 200) {
          this.$message.success('拍卖创建成功');
          this.auctionDialogVisible = false;
          this.loadAssets(); // 重新加载资产列表以更新状态
          
          // 添加拍卖资产关联
          if (this.selectedAsset && response.data && response.data.id) {
            const auctionAssetResponse = await auctionApi.addAssetToAuction(response.data.id, {
              assetId: this.selectedAsset.id,
              quantity: form.quantity,
              startPrice: form.startPrice,
              currentPrice: form.currentPrice,
              reservePrice: form.reservePrice
            });
            
            if (auctionAssetResponse.code !== 200) {
              this.$message.warning('拍卖资产关联创建失败: ' + (auctionAssetResponse.message || '未知错误'));
            }
          }
        } else {
          this.$message.error(response.message || '拍卖创建失败');
        }
      } catch (error) {
        this.$message.error('拍卖创建失败: ' + (error.message || '未知错误'));
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
    parseProperties(properties) {
      try {
        return JSON.parse(properties);
      } catch (e) {
        console.error('解析资产属性失败:', e);
        return {};
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
    // 添加资产相关方法
    showCreateDialog() {
      this.editingAsset = false;
      this.createForm = {
        name: '',
        description: '',
        categoryId: null,
        ownerId: 1 // 默认用户ID
      };
      this.categoryProperties = null;
      this.dynamicProperties = {};
      this.createDialogVisible = true;
    },
    onCategoryChange(categoryId) {
      // 当资产分类改变时，加载该分类的属性定义
      console.log('选择分类ID:', categoryId);
      if (categoryId) {
        const category = this.categories.find(c => c.id === categoryId);
        console.log('找到分类:', category);
        if (category && category.properties) {
          try {
            // 使用专门的分类属性解析函数
            const parsedProperties = this.parseCategoryProperties(category.properties);
            console.log('解析后的属性:', parsedProperties);
            this.categoryProperties = parsedProperties;
            this.dynamicProperties = {}; // 重置动态属性
          } catch (e) {
            console.error('处理分类属性时出错:', e);
            this.categoryProperties = null;
          }
        } else {
          this.categoryProperties = null;
        }
      } else {
        this.categoryProperties = null;
      }
    },
    async saveAsset() {
      try {
        // 将动态属性合并到资产属性中
        if (Object.keys(this.dynamicProperties).length > 0) {
          this.createForm.properties = JSON.stringify(this.dynamicProperties);
        }
        
        let response;
        if (this.editingAsset) {
          // 更新资产
          response = await assetApi.updateAsset(this.createForm.id, this.createForm);
        } else {
          // 创建资产
          // 设置所有者ID为当前用户（模拟）
          response = await assetApi.createAsset(this.createForm);
        }
        
        if (response.code === 200) {
          this.$message.success(this.editingAsset ? '更新成功' : '创建成功');
          this.createDialogVisible = false;
          this.loadAssets(); // 重新加载资产列表
        } else {
          this.$message.error(response.message || (this.editingAsset ? '更新失败' : '创建失败'));
        }
      } catch (error) {
        this.$message.error(this.editingAsset ? '更新失败: ' + (error.message || '未知错误') : '创建失败: ' + (error.message || '未知错误'));
      }
    },
    parseCategoryProperties(properties) {
      // 处理资产分类属性，支持多种格式
      if (!properties) return null;
      
      try {
        // 首先尝试标准JSON解析
        const parsed = JSON.parse(properties);
        return parsed;
      } catch (e) {
        console.warn('标准JSON解析失败，尝试其他格式:', e);
        
        // 如果标准JSON解析失败，尝试其他常见格式
        try {
          // 尝试直接作为对象使用（如果已经是对象）
          if (typeof properties === 'object') {
            return properties;
          }
          
          // 尝试使用eval解析（仅在必要时使用，注意安全性）
          // 注意：这里仅用于演示目的，在生产环境中应避免使用eval
          if (typeof properties === 'string' && properties.trim().startsWith('{')) {
            const evaluated = eval('(' + properties + ')');
            return evaluated;
          }
        } catch (evalError) {
          console.error('所有解析方法都失败:', evalError);
        }
      }
      
      return null;
    },
  },
  mounted() {
    console.log('AssetsView mounted')
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

.dialog-footer {
  text-align: right;
}
</style>