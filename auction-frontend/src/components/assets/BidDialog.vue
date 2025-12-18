<template>
  <el-dialog :visible.sync="visible" title="出价" width="400px" @close="onClose">
    <el-form v-if="asset" :model="form" label-width="80px">
      <el-form-item label="资产名称">
        <span>{{ asset.name }}</span>
      </el-form-item>
      <el-form-item label="当前价格">
        <span>¥{{ asset.currentPrice }}</span>
      </el-form-item>
      <el-form-item label="您的出价" prop="bidPrice">
        <el-input-number
          v-model="form.bidPrice"
          :min="asset.currentPrice + 100"
          :step="100"
          controls-position="right"
        />
      </el-form-item>
    </el-form>
    
    <div slot="footer" class="dialog-footer">
      <el-button @click="onClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">提交出价</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'BidDialog',
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
  data() {
    return {
      form: {
        bidPrice: 0
      }
    }
  },
  watch: {
    asset: {
      handler(newAsset) {
        if (newAsset) {
          this.form.bidPrice = newAsset.currentPrice + 100
        }
      },
      immediate: true
    }
  },
  methods: {
    onClose() {
      this.$emit('update:visible', false)
    },
    handleSubmit() {
      this.$emit('submit', this.form)
    }
  }
}
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style>