<template>
  <div class="auctions-view">
    <el-card>
      <div slot="header" class="card-header">
        <span>拍卖列表</span>
      </div>
      
      <el-table :data="processedAuctions" stripe style="width: 100%">
        <el-table-column prop="id" label="拍卖编号" min-width="100">
          <template slot-scope="scope">
            <el-button type="text" @click="viewAuctionDetail(scope.row.id)">{{ scope.row.id }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="assetNames" label="资产名称" min-width="150">
          <template slot-scope="scope">
            <div v-for="asset in scope.row.assets" :key="asset.id">
              {{ asset.name }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="资产属性" min-width="200">
          <template slot-scope="scope">
            <div v-for="asset in scope.row.assets" :key="asset.id">
              <div v-if="asset.properties">
                <el-tag 
                  v-for="(value, key) in parseProperties(asset.properties)" 
                  :key="key" 
                  size="mini" 
                  style="margin: 2px;">
                  {{ key }}: {{ value }}
                </el-tag>
              </div>
              <div v-else>无属性</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="资产数量" min-width="100">
          <template slot-scope="scope">
            <div v-for="asset in scope.row.assets" :key="asset.id">
              {{ asset.quantity || 0 }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="startPrice" label="起拍价(元/台)" min-width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.startPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="currentPrice" label="当前最优价(单台)" min-width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.currentPrice }}
          </template>
        </el-table-column>
        <el-table-column label="打包拍卖" min-width="80">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isPackageAuction" type="danger">打包</el-tag>
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
        <el-table-column label="倒计时" min-width="120">
          <template slot-scope="scope">
            <div v-if="scope.row.auctionStatus === 2 && countdownDisplay[scope.row.id]">
              {{ countdownDisplay[scope.row.id] }}
            </div>
            <div v-else>
              --
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="150">
          <template slot-scope="scope">
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
          <el-descriptions-item label="资产信息">
            <div v-for="asset in selectedAuction.assets" :key="asset.id">
              <div>{{ asset.name }} (数量: {{ asset.quantity || 0 }})</div>
              <div v-if="asset.properties">
                属性:
                <el-tag 
                  v-for="(value, key) in parseProperties(asset.properties)" 
                  :key="key" 
                  size="mini" 
                  style="margin: 2px;">
                  {{ key }}: {{ value }}
                </el-tag>
              </div>
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="起拍价(元/台)">¥{{ selectedAuction.startPrice }}</el-descriptions-item>
          <el-descriptions-item label="当前价(元/台)">¥{{ selectedAuction.currentPrice }}</el-descriptions-item>
          <el-descriptions-item label="保留价(元/台)">¥{{ selectedAuction.reservePrice }}</el-descriptions-item>
          <el-descriptions-item label="一口价(元/台)">¥{{ selectedAuction.buyItNowPrice }}</el-descriptions-item>
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
          <el-descriptions-item v-if="selectedAuction.isPackageAuction" label="打包拍卖">
            <el-tag type="danger">是</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(selectedAuction.createdTime) }}</el-descriptions-item>
          
          <!-- 特定拍卖类型的信息 -->
          <el-descriptions-item v-if="selectedAuction.auctionType === 5" label="初始价(元/台)">
            ¥{{ selectedAuction.initialPrice }}
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedAuction.auctionType === 5" label="降价阶梯(元/台)">
            ¥{{ selectedAuction.priceStep }}
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedAuction.auctionType === 5" label="降价周期">
            {{ selectedAuction.priceDropInterval }}分钟
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedAuction.auctionType === 5" label="最低价(元/台)">
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
            <el-table-column prop="bidPrice" label="出价金额(元/台)" min-width="100">
              <template slot-scope="scope">
                ¥{{ scope.row.bidPrice }}
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" min-width="80"></el-table-column>
            <el-table-column label="总金额" min-width="100">
              <template slot-scope="scope">
                ¥{{ (scope.row.bidPrice * scope.row.quantity).toFixed(2) }}
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
        <el-form-item label="资产信息">
          <div v-for="asset in selectedAuction.assets" :key="asset.id">
            <div>{{ asset.name }} (数量: {{ asset.quantity || 0 }})</div>
          </div>
        </el-form-item>
        <el-form-item label="当前价格(元/台)">
          <span>¥{{ selectedAuction.currentPrice }}</span>
        </el-form-item>
        <el-form-item label="出价数量" prop="quantity">
          <el-input-number
            v-model="bidForm.quantity"
            :min="1"
            :max="getMaxBidQuantity()"
            :disabled="selectedAuction.isPackageAuction"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="您的出价(元/台)" prop="bidPrice">
          <el-input-number
            v-model="bidForm.bidPrice"
            :min="getMinBidPrice()"
            :step="selectedAuction.bidIncrement || 100"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="总金额">
          <span>¥{{ (bidForm.bidPrice * bidForm.quantity).toFixed(2) }}</span>
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
      <p v-if="selectedAuction">确认开始拍卖 {{ getAssetNames(selectedAuction) }} 吗？</p>
      <div slot="footer" class="dialog-footer">
        <el-button @click="startDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmStart">确认开始</el-button>
      </div>
    </el-dialog>
    
    <!-- 结束拍卖确认对话框 -->
    <el-dialog :visible.sync="endDialogVisible" title="确认结束拍卖" width="400px">
      <p v-if="selectedAuction">确认结束拍卖 {{ getAssetNames(selectedAuction) }} 吗？</p>
      <div slot="footer" class="dialog-footer">
        <el-button @click="endDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmEnd">确认结束</el-button>
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
      countdownDisplay: {}
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
    }
  },
  methods: {
    // 新增一个处理拍卖数据的方法
    processAuctionData() {
      try {
        // 处理拍卖数据，将资产信息合并到拍卖对象中
        this.processedAuctions = this.auctions.map(auction => {
          // 获取拍卖关联的资产信息
          const auctionAssetList = this.auctionAssets[auction.id] || [];
          
          // 获取资产详细信息
          const assetsWithDetails = auctionAssetList.map(auctionAsset => {
            const asset = this.assets.find(a => a.id === auctionAsset.assetId);
            if (asset) {
              // 合并资产信息和拍卖资产信息
              return Object.assign({}, asset, {
                quantity: auctionAsset.quantity,
                startPrice: auctionAsset.startPrice,
                currentPrice: auctionAsset.currentPrice,
                reservePrice: auctionAsset.reservePrice
              });
            }
            return auctionAsset;
          });
          
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
        });
      } catch (error) {
        console.error('处理拍卖数据时出错:', error);
        this.processedAuctions = [];
      }
    },
    
    async loadAuctions() {
      try {
        // 并行加载拍卖和资产数据
        const [auctionsResponse, assetsResponse] = await Promise.all([
          auctionApi.getAuctions(),
          assetApi.getAssets()
        ]);

        if (auctionsResponse && auctionsResponse.code === 200) {
          // 确保 response.data 是数组
          if (Array.isArray(auctionsResponse.data)) {
            this.auctions = auctionsResponse.data;
            // 加载每个拍卖关联的资产信息
            await this.loadAuctionAssets(auctionsResponse.data);
          } else {
            this.auctions = [];
          }
        } else {
          this.auctions = [];
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
        this.processAuctionData();
      } catch (error) {
        console.error('加载拍卖列表失败:', error);
        this.auctions = [];
        this.assets = [];
        this.processAuctionData();
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
        this.processAuctionData();
      } catch (error) {
        console.error('加载拍卖资产信息失败:', error);
        this.auctionAssets = {};
        this.processAuctionData();
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
      this.bidForm.quantity = 1;
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
          userId: 1,
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

    parseProperties(properties) {
      try {
        if (!properties) return {};
        return JSON.parse(properties);
      } catch (e) {
        console.error('解析资产属性失败:', e);
        return {};
      }
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
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
