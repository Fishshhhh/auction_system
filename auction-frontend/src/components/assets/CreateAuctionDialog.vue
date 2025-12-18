<template>
  <el-dialog :visible="visible" title="创建拍卖" width="600px" @close="onClose">
    <el-form :model="form" label-width="120px">
      <el-form-item label="资产名称">
        <span>{{ asset ? asset.name : '' }}</span>
      </el-form-item>
      <el-form-item label="资产数量" prop="quantity">
        <el-input-number v-model="form.quantity" :min="1" :max="999" controls-position="right" style="width: 100%" />
      </el-form-item>
      <el-form-item label="是否打包拍卖" prop="isPackageAuction">
        <el-switch v-model="form.isPackageAuction"></el-switch>
      </el-form-item>
      <el-form-item label="起拍价" prop="startPrice">
        <div class="input-with-unit">
          <el-input-number v-model="form.startPrice" :min="0" controls-position="right" class="input-number" />
          <span class="unit">元/台</span>
        </div>
      </el-form-item>
      <el-form-item label="保留价" prop="reservePrice">
        <div class="input-with-unit">
          <el-input-number v-model="form.reservePrice" :min="0" controls-position="right" class="input-number" />
          <span class="unit">元/台</span>
        </div>
      </el-form-item>
      <el-form-item label="拍卖类型" prop="auctionType">
        <el-select v-model="form.auctionType" placeholder="请选择拍卖类型" style="width: 100%">
          <el-option label="公开实时竞价" :value="1"></el-option>
          <el-option label="暗拍" :value="2"></el-option>
          <el-option label="无底价拍卖" :value="3"></el-option>
          <el-option label="定向拍卖" :value="4"></el-option>
          <el-option label="降价拍卖" :value="5"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="拍卖开始时间" prop="startTime">
        <el-date-picker
          v-model="form.startTime"
          type="datetime"
          placeholder="选择拍卖开始时间"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="拍卖结束时间" prop="endTime">
        <el-date-picker
          v-model="form.endTime"
          type="datetime"
          placeholder="选择拍卖结束时间"
          style="width: 100%"
        />
      </el-form-item>
      
      <!-- 公开竞价和无底价拍卖参数 -->
      <template v-if="form.auctionType === 1 || form.auctionType === 3">
        <el-form-item label="加价幅度" prop="bidIncrement">
          <div class="input-with-unit">
            <el-input-number v-model="form.bidIncrement" :min="0" controls-position="right" class="input-number" />
            <span class="unit">元/台</span>
          </div>
        </el-form-item>
        <el-form-item label="延时时间(分钟)" prop="extendTime">
          <el-input-number v-model="form.extendTime" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="延时阈值(秒)" prop="extendThreshold">
          <el-input-number v-model="form.extendThreshold" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
      </template>
      
      <!-- 降价拍卖参数 -->
      <template v-if="form.auctionType === 5">
        <el-form-item label="初始价" prop="initialPrice">
          <div class="input-with-unit">
            <el-input-number v-model="form.initialPrice" :min="0" controls-position="right" class="input-number" />
            <span class="unit">元/台</span>
          </div>
        </el-form-item>
        <el-form-item label="降价阶梯" prop="priceStep">
          <div class="input-with-unit">
            <el-input-number v-model="form.priceStep" :min="0" controls-position="right" class="input-number" />
            <span class="unit">元/台</span>
          </div>
        </el-form-item>
        <el-form-item label="降价周期(分钟)" prop="priceDropInterval">
          <el-input-number v-model="form.priceDropInterval" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="最低价" prop="minPrice">
          <div class="input-with-unit">
            <el-input-number v-model="form.minPrice" :min="0" controls-position="right" class="input-number" />
            <span class="unit">元/台</span>
          </div>
        </el-form-item>
      </template>
      
      <!-- 定向拍卖参数 -->
      <template v-if="form.auctionType === 4">
        <el-form-item label="需要资格认证" prop="qualificationRequired">
          <el-switch v-model="form.qualificationRequired"></el-switch>
        </el-form-item>
      </template>
      
      <!-- 保证金参数 -->
      <el-form-item label="需要保证金" prop="depositRequired">
        <el-switch v-model="form.depositRequired"></el-switch>
      </el-form-item>
      <el-form-item v-if="form.depositRequired" label="保证金金额" prop="depositAmount">
        <el-input-number v-model="form.depositAmount" :min="0" controls-position="right" style="width: 100%" />
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="onClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">创建拍卖</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'CreateAuctionDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    asset: {
      type: Object,
      default: () => null
    }
  },
  model: {
    prop: 'visible',
    event: 'close'
  },
  data() {
    return {
      form: {
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
        quantity: 1,
        isPackageAuction: false // 添加打包拍卖选项
      }
    }
  },
  watch: {
    asset: {
      handler(newAsset) {
        if (newAsset) {
          this.form = {
            assetId: newAsset.id,
            templateId: null,
            startPrice: newAsset.startingPrice || 0,
            currentPrice: newAsset.startingPrice || 0,
            reservePrice: newAsset.reservePrice || 0,
            buyItNowPrice: null,
            startTime: newAsset.auctionStartTime || '',
            endTime: newAsset.auctionEndTime || '',
            auctionStatus: 1, // 未开始
            auctionType: newAsset.auctionType || 1,
            bidIncrement: 0,
            extendTime: 0,
            extendThreshold: 0,
            initialPrice: newAsset.startingPrice || 0,
            priceStep: 0,
            priceDropInterval: 1,
            minPrice: 0,
            nextPriceDropTime: null,
            qualificationRequired: false,
            winnerUserId: null,
            finalPrice: null,
            depositRequired: false,
            depositAmount: 0,
            quantity: 1,
            isPackageAuction: false // 添加打包拍卖选项
          }
        }
      },
      immediate: true
    }
  },
  methods: {
    onClose() {
      this.$emit('close')
    },
    async handleSubmit() {
      this.$emit('submit', this.form)
    }
  }
}
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}

.input-with-unit {
  display: flex;
  align-items: center;
}

.input-number {
  flex: 1;
}

.unit {
  margin-left: 10px;
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}
</style>