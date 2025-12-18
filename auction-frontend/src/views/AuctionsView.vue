<template>
  <div class="auctions-view">
    <el-card class="auctions-card">
      <div slot="header" class="card-header">
        <div class="header-content">
          <div class="header-title-section">
            <div>
              <h2 class="header-title">拍卖列表</h2>
              <p class="header-subtitle">管理系统中的所有拍卖活动</p>
            </div>
          </div>
          <div class="header-actions">
            <el-button type="success" icon="el-icon-refresh" @click="loadAuctions">
              刷新数据
            </el-button>
          </div>
        </div>
      </div>
      
      <div class="stats-section">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-icon bg-primary">
                <i class="el-icon-time"></i>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ totalAuctionsCount }}</div>
                <div class="stat-label">总拍卖数</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-icon bg-info">
                <i class="el-icon-video-play"></i>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ ongoingAuctionsCount }}</div>
                <div class="stat-label">进行中</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-icon bg-success">
                <i class="el-icon-circle-check"></i>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ completedAuctionsCount }}</div>
                <div class="stat-label">已成交</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-icon bg-warning">
                <i class="el-icon-timer"></i>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ endingSoonAuctionsCount }}</div>
                <div class="stat-label">即将结束</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
      
      <div class="filter-section">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-input 
              placeholder="搜索拍卖编号..." 
              v-model="searchKeyword"
              clearable
              @clear="loadAuctions"
              @keyup.enter.native="loadAuctions">
              <i slot="prefix" class="el-input__icon el-icon-search"></i>
            </el-input>
          </el-col>
          <el-col :span="6">
            <el-select v-model="statusFilter" placeholder="选择拍卖状态" clearable @change="loadAuctions">
              <el-option label="全部状态" value=""></el-option>
              <el-option label="未开始" value="1"></el-option>
              <el-option label="进行中" value="2"></el-option>
              <el-option label="已结束" value="3"></el-option>
              <el-option label="已成交" value="4"></el-option>
              <el-option label="流拍" value="5"></el-option>
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-select v-model="typeFilter" placeholder="选择拍卖类型" clearable @change="loadAuctions">
              <el-option label="全部类型" value=""></el-option>
              <el-option label="公开实时竞价" value="1"></el-option>
              <el-option label="暗拍" value="2"></el-option>
              <el-option label="无底价" value="3"></el-option>
              <el-option label="定向" value="4"></el-option>
              <el-option label="降价" value="5"></el-option>
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-button type="primary" icon="el-icon-search" @click="loadAuctions">搜索</el-button>
          </el-col>
        </el-row>
      </div>
      
      <el-table 
        :data="processedAuctions" 
        stripe 
        style="width: 100%" 
        class="professional-table auctions-table" 
        v-loading="loading"
        :header-cell-style="{background: 'linear-gradient(180deg, #f5f7fa, #ebedf0)', color: '#606266', fontWeight: '600'}"
        :row-class-name="tableRowClassName">
        <el-table-column prop="id" label="拍卖编号" min-width="110">
          <template slot-scope="scope">
            <div class="cell-content">
              <el-button type="text" @click="viewAuctionDetail(scope.row.id)" class="auction-id-link">
                {{ scope.row.id }}
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="资产信息" min-width="250">
          <template slot-scope="scope">
            <div class="asset-info-container">
              <div v-for="asset in scope.row.assets" :key="asset.id" class="asset-item">
                <div class="asset-basic-info">
                  <div class="asset-name">{{ asset.name }}</div>
                  <div class="asset-quantity">数量: {{ asset.quantity || 0 }}</div>
                </div>
                <div v-if="asset.properties && Object.keys(parseProperties(asset.properties)).length > 0" class="asset-properties">
                  <el-tag 
                    v-for="(value, key) in getFirstTwoProperties(asset.properties)" 
                    :key="key" 
                    size="mini" 
                    class="property-tag">
                    {{ key }}: {{ value }}
                  </el-tag>
                  <el-tag v-if="getPropertyCount(asset.properties) > 2" size="mini" type="info">
                    +{{ getPropertyCount(asset.properties) - 2 }}
                  </el-tag>
                </div>
                <div v-else class="no-properties">-</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="价格信息" min-width="150">
          <template slot-scope="scope">
            <div class="price-info-container">
              <div class="price-item">
                <span class="price-label">起拍价:</span>
                <span class="price-value">¥{{ scope.row.startPrice }}</span>
              </div>
              <div class="price-item">
                <span class="price-label">当前价:</span>
                <span class="price-value current-price">¥{{ scope.row.currentPrice }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="拍卖时间" min-width="180">
          <template slot-scope="scope">
            <div class="time-info-container">
              <div class="time-item">
                <span class="time-label">开始:</span>
                <span class="time-value">{{ formatDate(scope.row.startTime) }}</span>
              </div>
              <div class="time-item">
                <span class="time-label">结束:</span>
                <span class="time-value">{{ formatDate(scope.row.endTime) }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="拍卖状态" min-width="100" align="center">
          <template slot-scope="scope">
            <div class="status-info-container">
              <el-tag :type="getAuctionStatusType(scope.row.auctionStatus)" class="status-tag">
                {{ getAuctionStatusText(scope.row.auctionStatus) }}
              </el-tag>
              <el-tag v-if="scope.row.isPackageAuction" type="danger" class="package-tag">打包</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="拍卖类型" min-width="120" align="center">
          <template slot-scope="scope">
            <div class="cell-content center-content">
              <el-tag :type="getAuctionTypeTagType(scope.row.auctionType)" class="type-tag">
                {{ getAuctionTypeText(scope.row.auctionType) }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="倒计时" min-width="120" align="center">
          <template slot-scope="scope">
            <div class="cell-content center-content">
              <div v-if="scope.row.auctionStatus === 2 && countdownDisplay[scope.row.id]" class="countdown-container">
                <span class="countdown-text">{{ countdownDisplay[scope.row.id] }}</span>
              </div>
              <div v-else-if="scope.row.auctionStatus === 1" class="countdown-default pending-status">
                未开始
              </div>
              <div v-else class="countdown-default ended-status">
                已结束
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="180" fixed="right" align="center">
          <template slot-scope="scope">
            <div class="cell-content center-content">
              <el-button 
                v-if="canBid(scope.row)" 
                size="mini" 
                type="primary" 
                @click="bidAuction(scope.row)"
                class="action-button bid-button">
                出价
              </el-button>
              <el-button 
                v-if="scope.row.auctionStatus === 1" 
                size="mini" 
                type="warning" 
                @click="startAuction(scope.row)"
                class="action-button start-button">
                开始
              </el-button>
              <el-button 
                v-if="scope.row.auctionStatus === 2" 
                size="mini" 
                type="danger" 
                @click="endAuction(scope.row)"
                class="action-button end-button">
                结束
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-section">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalAuctions">
        </el-pagination>
      </div>
    </el-card>
    
    <!-- 拍卖详情对话框 -->
    <el-dialog :visible.sync="dialogVisible" title="拍卖详情" width="600px" class="auction-detail-dialog">
      <div v-if="selectedAuction">
        <el-descriptions :column="1" border class="auction-details">
          <el-descriptions-item label="拍卖编号">{{ selectedAuction.id }}</el-descriptions-item>
          <el-descriptions-item label="资产信息">
            <div v-for="asset in selectedAuction.assets" :key="asset.id" class="asset-info">
              <div class="asset-name">{{ asset.name }} (数量: {{ asset.quantity || 0 }})</div>
              <div v-if="asset.properties" class="asset-properties">
                属性:
                <el-tag 
                  v-for="(value, key) in parseProperties(asset.properties)" 
                  :key="key" 
                  size="mini" 
                  class="property-tag">
                  {{ key }}: {{ value }}
                </el-tag>
              </div>
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="起拍价(元/台)">
            <span class="price-text">¥{{ selectedAuction.startPrice }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="当前价(元/台)">
            <span class="price-text current-price">¥{{ selectedAuction.currentPrice }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="保留价(元/台)">
            <span class="price-text">¥{{ selectedAuction.reservePrice }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="一口价(元/台)">
            <span class="price-text">¥{{ selectedAuction.buyItNowPrice }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="开始时间">
            <span class="time-text">{{ formatDate(selectedAuction.startTime) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="结束时间">
            <span class="time-text">{{ formatDate(selectedAuction.endTime) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="拍卖状态">
            <el-tag :type="getAuctionStatusType(selectedAuction.auctionStatus)" class="status-tag">
              {{ getAuctionStatusText(selectedAuction.auctionStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="拍卖类型">
            <el-tag :type="getAuctionTypeTagType(selectedAuction.auctionType)" class="type-tag">
              {{ getAuctionTypeText(selectedAuction.auctionType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedAuction.isPackageAuction" label="打包拍卖">
            <el-tag type="danger" class="package-tag">是</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            <span class="time-text">{{ formatDate(selectedAuction.createdTime) }}</span>
          </el-descriptions-item>
          
          <!-- 特定拍卖类型的信息 -->
          <el-descriptions-item v-if="selectedAuction.auctionType === 5" label="初始价(元/台)">
            <span class="price-text">¥{{ selectedAuction.initialPrice }}</span>
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedAuction.auctionType === 5" label="降价阶梯(元/台)">
            <span class="price-text">¥{{ selectedAuction.priceStep }}</span>
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedAuction.auctionType === 5" label="降价周期">
            {{ selectedAuction.priceDropInterval }}分钟
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedAuction.auctionType === 5" label="最低价(元/台)">
            <span class="price-text">¥{{ selectedAuction.minPrice }}</span>
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedAuction.auctionType === 5" label="下次降价时间">
            <span class="time-text">{{ formatDate(selectedAuction.nextPriceDropTime) }}</span>
          </el-descriptions-item>
        </el-descriptions>
        
        <!-- 出价记录 -->
        <div class="bids-section">
          <h3 class="section-title">出价记录</h3>
          <el-table :data="bids" stripe style="width: 100%" class="bids-table">
            <el-table-column prop="userId" label="出价人ID" min-width="100"></el-table-column>
            <el-table-column prop="bidPrice" label="出价金额(元/台)" min-width="100">
              <template slot-scope="scope">
                <span class="price-text">¥{{ scope.row.bidPrice }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" min-width="80"></el-table-column>
            <el-table-column label="总金额" min-width="100">
              <template slot-scope="scope">
                <span class="price-text total-price">¥{{ (scope.row.bidPrice * scope.row.quantity).toFixed(2) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="createdTime" label="出价时间" min-width="150">
              <template slot-scope="scope">
                <span class="time-text">{{ formatDate(scope.row.createdTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="bidStatus" label="状态" min-width="100">
              <template slot-scope="scope">
                <el-tag :type="getBidStatusType(scope.row.bidStatus)" class="status-tag">
                  {{ getBidStatusText(scope.row.bidStatus) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" class="footer-button cancel-button">关闭</el-button>
        <el-button 
          v-if="canBid(selectedAuction)" 
          type="primary" 
          @click="bidAuction(selectedAuction)"
          class="footer-button bid-button">
          出价
        </el-button>
      </div>
    </el-dialog>
    
    <!-- 出价对话框 -->
    <el-dialog :visible.sync="bidDialogVisible" title="出价" width="400px" class="bid-dialog">
      <el-form v-if="selectedAuction" :model="bidForm" label-width="80px" class="bid-form">
        <el-form-item label="资产信息">
          <div v-for="asset in selectedAuction.assets" :key="asset.id" class="asset-info">
            <div class="asset-name">{{ asset.name }} (数量: {{ asset.quantity || 0 }})</div>
          </div>
        </el-form-item>
        <el-form-item label="当前价格(元/台)">
          <span class="price-text current-price">¥{{ selectedAuction.currentPrice }}</span>
        </el-form-item>
        <el-form-item label="出价数量" prop="quantity">
          <el-input-number
            v-model="bidForm.quantity"
            :min="1"
            :max="getMaxBidQuantity()"
            :disabled="isPackageAuction()"
            controls-position="right"
            class="quantity-input"
          />
          <div v-if="isPackageAuction()" class="el-form-item-message">
            打包拍卖，数量不可更改
          </div>
        </el-form-item>
        <el-form-item label="您的出价(元/台)" prop="bidPrice">
          <el-input-number
            v-model="bidForm.bidPrice"
            :min="getMinBidPrice()"
            :step="selectedAuction.bidIncrement || 100"
            controls-position="right"
            class="price-input"
          />
        </el-form-item>
        <el-form-item label="总金额">
          <span class="price-text total-price">¥{{ (bidForm.bidPrice * bidForm.quantity).toFixed(2) }}</span>
        </el-form-item>
        <el-alert 
          v-if="selectedAuction.auctionType === 5" 
          title="降价拍卖：出价必须等于当前价格" 
          type="info" 
          show-icon
          class="auction-alert">
        </el-alert>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="bidDialogVisible = false" class="footer-button cancel-button">取消</el-button>
        <el-button type="primary" @click="submitBid" class="footer-button submit-button">提交出价</el-button>
      </div>
    </el-dialog>
    
    <!-- 开始拍卖确认对话框 -->
    <el-dialog :visible.sync="startDialogVisible" title="确认开始拍卖" width="400px" class="confirmation-dialog">
      <p v-if="selectedAuction" class="confirmation-text">确认开始拍卖 {{ getAssetNames(selectedAuction) }} 吗？</p>
      <div slot="footer" class="dialog-footer">
        <el-button @click="startDialogVisible = false" class="footer-button cancel-button">取消</el-button>
        <el-button type="warning" @click="confirmStart" class="footer-button start-button">确认开始</el-button>
      </div>
    </el-dialog>
    
    <!-- 结束拍卖确认对话框 -->
    <el-dialog :visible.sync="endDialogVisible" title="确认结束拍卖" width="400px" class="confirmation-dialog">
      <p v-if="selectedAuction" class="confirmation-text">确认结束拍卖 {{ getAssetNames(selectedAuction) }} 吗？</p>
      <div slot="footer" class="dialog-footer">
        <el-button @click="endDialogVisible = false" class="footer-button cancel-button">取消</el-button>
        <el-button type="danger" @click="confirmEnd" class="footer-button end-button">确认结束</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { auctionApi } from '../api/auctionApi'
import { assetApi } from '../api/assetApi'

export default {
  name: 'AuctionsView',
  data() {
    return {
      auctions: [],
      auctionAssets: {},
      assets: [],
      bids: [],
      dialogVisible: false,
      bidDialogVisible: false,
      startDialogVisible: false,
      endDialogVisible: false,
      selectedAuction: null,
      bidForm: {
        bidPrice: 0,
        quantity: 1
      },
      // 新增一个用于存储处理后的拍卖数据的变量
      processedAuctions: [],
      // 倒计时相关
      countdownTimers: {},
      countdownDisplay: {},
      // 分页相关
      currentPage: 1,
      pageSize: 10,
      totalAuctions: 0,
      loading: false,
      // 过滤条件
      searchKeyword: '',
      statusFilter: '',
      typeFilter: ''
    }
  },
  computed: {
    auctionsWithAssets() {
      try {
        // 如果数据还未加载完成，返回空数组
        if (!Array.isArray(this.auctions) || !this.auctionAssets || !Array.isArray(this.assets)) {
          return [];
        }

        return this.auctions.map(auction => {
          // 获取与此拍卖关联的资产信息
          const auctionAssets = this.auctionAssets[auction.id] || [];
          const assetsWithDetails = auctionAssets.map(auctionAsset => {
            const asset = this.assets.find(a => a.id === auctionAsset.assetId);
            if (asset) {
              return Object.assign({}, asset, {
                quantity: auctionAsset.quantity
              });
            }
            // 如果找不到资产，创建一个默认资产对象
            return {
              id: auctionAsset.assetId,
              quantity: auctionAsset.quantity,
              name: '未知资产'
            };
          });

          return Object.assign({}, auction, {
            assets: assetsWithDetails,
            assetNames: assetsWithDetails.map(a => a.name).join(', ')
          });
        });
      } catch (error) {
        console.error('计算 auctionsWithAssets 时出错:', error);
        return [];
      }
    },
    totalAuctionsCount() {
      return this.auctions.length;
    },
    ongoingAuctionsCount() {
      return this.auctions.filter(auction => auction.auctionStatus === 2).length;
    },
    completedAuctionsCount() {
      return this.auctions.filter(auction => auction.auctionStatus === 4).length;
    },
    endingSoonAuctionsCount() {
      const now = new Date();
      return this.auctions.filter(auction => {
        return auction.auctionStatus === 2 && new Date(auction.endTime) - now < 3600000; // 1小时
      }).length;
    }
  },
  methods: {
    // 新增一个处理拍卖数据的方法
    async processAuctionData() {
      try {
        // 处理拍卖数据，将资产信息合并到拍卖对象中
        this.processedAuctions = await Promise.all(this.auctions.map(async (auction) => {
          // 获取拍卖关联的资产信息
          const auctionAssetList = this.auctionAssets[auction.id] || [];
          
          // 获取资产详细信息并加载图片
          const assetsWithDetails = await Promise.all(auctionAssetList.map(async (auctionAsset) => {
            const asset = this.assets.find(a => a.id === auctionAsset.assetId);
            if (asset) {
              // 加载资产图片
              try {
                const imageResponse = await assetApi.getAssetImages(asset.id);
                if (imageResponse.code === 200) {
                  asset.images = imageResponse.data || [];
                }
              } catch (error) {
                console.error(`加载资产 ${asset.id} 的图片失败:`, error);
                asset.images = []; // 确保即使加载失败也有 images 属性
              }
              
              // 合并资产信息和拍卖资产信息
              return Object.assign({}, asset, {
                quantity: auctionAsset.quantity,
                startPrice: auctionAsset.startPrice,
                currentPrice: auctionAsset.currentPrice,
                reservePrice: auctionAsset.reservePrice
              });
            }
            return auctionAsset;
          }));
          
          // 初始化倒计时
          if (auction.auctionStatus === 2) {
            this.$nextTick(() => {
              this.initCountdown(auction);
            });
          }
          
          return Object.assign({}, auction, {
            assets: assetsWithDetails,
            assetNames: assetsWithDetails.map(a => a.name).join(', ')
          });
        }));
      } catch (error) {
        console.error('处理拍卖数据时出错:', error);
        this.processedAuctions = [];
      }
    },
    
    async loadAuctions() {
      try {
        // 并行加载拍卖和资产数据
        const [auctionsResponse, assetsResponse] = await Promise.all([
          auctionApi.getAuctions({
            page: this.currentPage,
            size: this.pageSize,
            keyword: this.searchKeyword,
            status: this.statusFilter,
            type: this.typeFilter
          }),
          assetApi.getAssets()
        ]);

        if (auctionsResponse && auctionsResponse.code === 200) {
          // 确保 response.data 是数组
          if (Array.isArray(auctionsResponse.data)) {
            this.auctions = auctionsResponse.data;
            this.totalAuctions = auctionsResponse.total;
            // 加载每个拍卖关联的资产信息
            await this.loadAuctionAssets(auctionsResponse.data);
          } else {
            this.auctions = [];
            this.totalAuctions = 0;
          }
        } else {
          this.auctions = [];
          this.totalAuctions = 0;
        }

        if (assetsResponse && assetsResponse.code === 200) {
          if (Array.isArray(assetsResponse.data)) {
            this.assets = assetsResponse.data;
          } else {
            this.assets = [];
          }
        } else {
          this.assets = [];
        }
        
        // 数据加载完成后处理拍卖数据
        await this.processAuctionData();
      } catch (error) {
        console.error('加载拍卖列表失败:', error);
        this.auctions = [];
        this.assets = [];
        await this.processAuctionData();
      }
    },

    // 加载拍卖关联的资产信息
    async loadAuctionAssets(auctions) {
      try {
        // 并行加载所有拍卖的资产信息
        const promises = auctions.map(auction => 
          auctionApi.getAuctionAssets(auction.id)
        );

        const responses = await Promise.all(promises);

        // 将结果存储到 auctionAssets 对象中
        responses.forEach((response, index) => {
          const auctionId = auctions[index].id;
          if (response && response.code === 200 && Array.isArray(response.data)) {
            this.$set(this.auctionAssets, auctionId, response.data);
          } else {
            this.$set(this.auctionAssets, auctionId, []);
          }
        });
        
        // 资产信息加载完成后处理拍卖数据
        await this.processAuctionData();
      } catch (error) {
        console.error('加载拍卖资产信息失败:', error);
        this.auctionAssets = {};
        await this.processAuctionData();
      }
    },

    async viewAuction(auction) {
      this.selectedAuction = auction;
      // 加载出价记录
      try {
        const response = await auctionApi.getAuctionBids(auction.id);
        if (response.code === 200) {
          this.bids = response.data || [];
        }
      } catch (error) {
        console.error('加载出价记录失败:', error);
        this.bids = [];
      }
      this.dialogVisible = true;
    },

    bidAuction(auction) {
      this.selectedAuction = auction;
      this.bidForm.bidPrice = this.getMinBidPrice();
      
      // 设置默认购买数量
      if (auction.isPackageAuction) {
        // 打包拍卖默认购买数量为资产总数
        const totalQuantity = auction.assets.reduce((sum, asset) => sum + (asset.quantity || 0), 0);
        this.bidForm.quantity = totalQuantity;
      } else {
        this.bidForm.quantity = 1;
      }
      
      this.bidDialogVisible = true;
    },

    startAuction(auction) {
      this.selectedAuction = auction;
      this.startDialogVisible = true;
    },

    endAuction(auction) {
      this.selectedAuction = auction;
      this.endDialogVisible = true;
    },

    async confirmStart() {
      try {
        const response = await auctionApi.startAuction(this.selectedAuction.id);
        if (response.code === 200) {
          this.$message.success('拍卖已开始!');
          this.startDialogVisible = false;
          this.loadAuctions();
        } else {
          this.$message.error(response.message || '操作失败');
        }
      } catch (error) {
        this.$message.error('操作失败: ' + (error.message || '未知错误'));
      }
    },

    async confirmEnd() {
      try {
        const response = await auctionApi.endAuction(this.selectedAuction.id);
        if (response.code === 200) {
          this.$message.success('拍卖已结束!');
          this.endDialogVisible = false;
          this.loadAuctions();
        } else {
          this.$message.error(response.message || '操作失败');
        }
      } catch (error) {
        this.$message.error('操作失败: ' + (error.message || '未知错误'));
      }
    },

    async submitBid() {
      if (!this.selectedAuction) return;

      try {
        const bidData = {
          auctionId: this.selectedAuction.id,
          userId: this.$store.state.user.currentUser.id,  // 使用当前登录用户的ID
          bidPrice: this.bidForm.bidPrice,
          quantity: this.bidForm.quantity,
          bidStatus: 1
        };

        const response = await auctionApi.submitBid(this.selectedAuction.id, bidData);
        if (response.code === 200) {
          this.$message.success('出价成功!');
          this.bidDialogVisible = false;
          this.loadAuctions();
        } else {
          this.$message.error(response.message || '出价失败');
        }
      } catch (error) {
        this.$message.error('出价失败: ' + (error.message || '未知错误'));
      }
    },

    viewAuctionDetail(auctionId) {
      // 使用更明确的路由导航方式
      this.$router.push({
        name: 'AuctionDetail',
        params: { id: auctionId }
      });
    },

    formatDate(dateString) {
      if (!dateString) return '';
      return new Date(dateString).toLocaleString('zh-CN');
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
    },

    getAuctionTypeText(type) {
      const typeMap = {
        1: '公开实时竞价',
        2: '暗拍',
        3: '无底价',
        4: '定向',
        5: '降价'
      };
      return typeMap[type] || '未知';
    },

    getAuctionTypeTagType(type) {
      const typeMap = {
        1: 'primary',
        2: 'warning',
        3: 'success',
        4: 'info',
        5: 'danger'
      };
      return typeMap[type] || '';
    },

    getBidStatusText(status) {
      const statusMap = {
        1: '有效',
        2: '无效',
        3: '胜出',
        4: '失败'
      };
      return statusMap[status] || '未知';
    },

    getBidStatusType(status) {
      const typeMap = {
        1: 'success',
        2: 'danger',
        3: 'primary',
        4: 'info'
      };
      return typeMap[status] || '';
    },

    canBid(auction) {
      if (!auction) return false;
      if (auction.auctionStatus !== 2) return false;
      return true;
    },
    
    // 倒计时相关方法
    initCountdown(auction) {
      if (auction.auctionStatus !== 2 || !auction.endTime) return;
      
      const auctionId = auction.id;
      // 清除已有的定时器
      if (this.countdownTimers[auctionId]) {
        clearInterval(this.countdownTimers[auctionId]);
      }
      
      // 设置新的定时器
      this.countdownTimers[auctionId] = setInterval(() => {
        const now = new Date();
        const endTime = new Date(auction.endTime);
        const diff = endTime - now;
        
        if (diff <= 0) {
          // 时间到了，清除定时器
          clearInterval(this.countdownTimers[auctionId]);
          this.$set(this.countdownDisplay, auctionId, '已结束');
          // 重新加载数据以更新状态
          this.loadAuctions();
        } else {
          // 计算剩余时间
          const days = Math.floor(diff / (1000 * 60 * 60 * 24));
          const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
          const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
          const seconds = Math.floor((diff % (1000 * 60)) / 1000);
          
          let display = '';
          if (days > 0) {
            display = `${days}天${hours}小时`;
          } else if (hours > 0) {
            display = `${hours}小时${minutes}分钟`;
          } else {
            display = `${minutes}分${seconds}秒`;
          }
          
          this.$set(this.countdownDisplay, auctionId, display);
        }
      }, 1000);
    },

    getMinBidPrice() {
      if (!this.selectedAuction) return 0;

      if (this.selectedAuction.auctionType === 5) {
        return this.selectedAuction.currentPrice;
      }

      if (this.selectedAuction.bidIncrement) {
        return this.selectedAuction.currentPrice + this.selectedAuction.bidIncrement;
      }

      return this.selectedAuction.currentPrice + 100;
    },

    getMaxBidQuantity() {
      if (!this.selectedAuction) return 1;

      if (this.selectedAuction.assets && this.selectedAuction.assets.length > 0) {
        return Math.min(...this.selectedAuction.assets.map(asset => asset.quantity || 0));
      }

      return 1;
    },

    getAssetNames(auction) {
      if (auction && auction.assets) {
        return auction.assets.map(a => a.name).join(', ');
      }
      return '';
    },

    getTimeDiff(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      const now = new Date()
      const diffMs = date - now
      const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))
      const diffHours = Math.floor((diffMs % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
      
      if (diffMs < 0) {
        return '已过期'
      } else if (diffDays > 0) {
        return `${diffDays}天后`
      } else if (diffHours > 0) {
        return `${diffHours}小时后`
      } else {
        return '即将'
      }
    },

    parseProperties(properties) {
      try {
        if (!properties) return {};
        return JSON.parse(properties);
      } catch (e) {
        console.error('解析资产属性失败:', e);
        return {};
      }
    },
    
    isPackageAuction() {
      return this.selectedAuction && this.selectedAuction.isPackageAuction;
    },
    
    getFirstTwoProperties(properties) {
      const parsedProperties = this.parseProperties(properties);
      const keys = Object.keys(parsedProperties);
      return keys.slice(0, 2).reduce((acc, key) => {
        acc[key] = parsedProperties[key];
        return acc;
      }, {});
    },
    
    getPropertyCount(properties) {
      return Object.keys(this.parseProperties(properties)).length;
    },
    
    tableRowClassName({row, rowIndex}) {
      return 'professional-table-row'
    },
    
    handleSizeChange(newSize) {
      this.pageSize = newSize;
      this.loadAuctions();
    },
    
    handleCurrentChange(newPage) {
      this.currentPage = newPage;
      this.loadAuctions();
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
  },
  mounted() {
    this.loadAuctions();
  },
  
  beforeDestroy() {
    // 清除所有定时器
    Object.values(this.countdownTimers).forEach(timer => {
      if (timer) clearInterval(timer);
    });
  }
}
</script>

<style scoped>
.auctions-view {
  padding: 20px;
  background-color: #f8f9fa;
  min-height: 100%;
}

.auctions-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}

.auctions-card /deep/ .el-card__header {
  background-color: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
  padding: 15px 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.header-title-section {
  display: flex;
  align-items: center;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-subtitle {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.header-actions {
  display: flex;
  align-items: center;
}

.stats-section {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background-color: #fff;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  font-size: 24px;
  margin-right: 15px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
}

.stat-icon.bg-primary {
  color: #409eff;
  background-color: rgba(64, 158, 255, 0.1);
}

.stat-icon.bg-info {
  color: #67c23a;
  background-color: rgba(103, 194, 58, 0.1);
}

.stat-icon.bg-success {
  color: #e6a23c;
  background-color: rgba(230, 162, 60, 0.1);
}

.stat-icon.bg-warning {
  color: #f56c6c;
  background-color: rgba(245, 108, 108, 0.1);
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.filter-section {
  margin-bottom: 20px;
}

.auctions-table {
  margin-top: 20px;
}

.auctions-table ::v-deep .el-table__header th {
  background: linear-gradient(180deg, #f5f7fa, #ebedf0) !important;
  color: #606266;
  font-weight: 600;
  font-size: 14px;
}

.auctions-table ::v-deep .el-table__row:hover {
  background-color: #f0f8ff !important;
}

.auctions-table ::v-deep .el-table__row.professional-table-row {
  transition: all 0.3s ease;
}

.auctions-table ::v-deep .el-table__row.professional-table-row:hover {
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
  flex-direction: column;
  align-items: flex-start;
  gap: 5px;
}

.image-container {
  width: 50px;
  height: 50px;
  border-radius: 6px;
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

.asset-image-wrapper {
  width: 100%;
  height: 100%;
}

.no-image {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border: 1px dashed #dcdfe6;
  color: #c0c4cc;
  font-size: 16px;
}

.asset-name {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

.property-tag {
  margin: 2px;
}

.no-properties {
  color: #909399;
  font-style: italic;
}

.asset-quantity-container {
  display: flex;
  align-items: center;
  background-color: #f0f2f5;
  padding: 2px 8px;
  border-radius: 12px;
}

.asset-quantity {
  font-weight: 600;
  color: #303133;
}

.quantity-unit {
  color: #909399;
  margin-left: 2px;
  font-size: 12px;
}

.price-cell {
  display: flex;
  align-items: center;
}

.price-text {
  font-weight: 600;
  color: #606266;
}

.current-price {
  color: #e6a23c;
  font-size: 15px;
}

.package-tag {
  font-weight: 600;
}

.time-cell {
  display: flex;
  align-items: center;
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

.status-tag,
.type-tag {
  font-weight: 500;
}

.countdown-container {
  display: flex;
  align-items: center;
  color: #f56c6c;
  font-weight: 700;
}

.countdown-text {
  font-weight: 700;
  color: #f56c6c;
  font-size: 14px;
}

.pending-status,
.ended-status {
  display: flex;
  align-items: center;
  color: #909399;
  font-style: italic;
}

.action-button {
  margin: 2px;
}

.bid-button {
  background-color: #409eff;
  border-color: #409eff;
}

.start-button {
  background-color: #e6a23c;
  border-color: #e6a23c;
}

.end-button {
  background-color: #f56c6c;
  border-color: #f56c6c;
}

.auction-id-link {
  font-weight: 700;
  color: #409eff;
  font-size: 14px;
}

/* Dialog styles */
.auction-detail-dialog,
.bid-dialog,
.confirmation-dialog {
  border-radius: 8px;
  overflow: hidden;
}

.auction-details {
  margin-bottom: 20px;
}

.asset-info {
  margin-bottom: 10px;
}

.asset-info:last-child {
  margin-bottom: 0;
}

.asset-properties {
  margin-top: 5px;
}

.section-title {
  color: #303133;
  border-left: 4px solid #409eff;
  padding-left: 10px;
  margin: 20px 0 15px 0;
}

.bids-section {
  margin-top: 20px;
}

.bids-table {
  margin-top: 10px;
}

.bid-form .el-form-item {
  margin-bottom: 20px;
}

.quantity-input,
.price-input {
  width: 100%;
}

.auction-alert {
  margin-top: 15px;
}

.confirmation-text {
  font-size: 16px;
  color: #606266;
  line-height: 1.6;
}

.dialog-footer {
  text-align: right;
}

.footer-button {
  margin-left: 10px;
}

.cancel-button {
  border-color: #dcdfe6;
  color: #606266;
}

.submit-button {
  background-color: #409eff;
  border-color: #409eff;
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