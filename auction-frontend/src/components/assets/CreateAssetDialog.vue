<template>
  <el-dialog :visible.sync="visible" :title="editingAsset ? '编辑资产' : '创建资产'" width="600px" @close="onClose">
    <el-form :model="form" label-width="100px">
      <el-form-item label="资产名称" prop="name">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="资产分类" prop="categoryId">
        <el-select v-model="form.categoryId" placeholder="请选择资产分类" style="width: 100%" @change="onCategoryChange">
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
        <el-input v-model="form.description" type="textarea" />
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="onClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'CreateAssetDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    editingAsset: {
      type: Boolean,
      default: false
    },
    asset: {
      type: Object,
      default: () => null
    },
    categories: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      form: {
        name: '',
        description: '',
        categoryId: null,
        ownerId: 1 // 默认用户ID
      },
      categoryProperties: null,
      dynamicProperties: {}
    }
  },
  watch: {
    asset: {
      handler(newAsset) {
        if (newAsset) {
          this.form = {
            id: newAsset.id,
            name: newAsset.name,
            description: newAsset.description,
            categoryId: newAsset.categoryId,
            ownerId: newAsset.ownerId || 1
          };
          
          // 处理资产属性
          if (newAsset.properties) {
            try {
              this.dynamicProperties = JSON.parse(newAsset.properties);
            } catch (e) {
              console.error('解析资产属性失败:', e);
              this.dynamicProperties = {};
            }
          }
        }
      },
      immediate: true
    },
    categories: {
      handler(newCategories) {
        if (this.form.categoryId) {
          this.onCategoryChange(this.form.categoryId);
        }
      },
      immediate: true
    }
  },
  methods: {
    onCategoryChange(categoryId) {
      // 当资产分类改变时，加载该分类的属性定义
      if (categoryId) {
        const category = this.categories.find(c => c.id === categoryId);
        if (category && category.properties) {
          try {
            // 使用专门的分类属性解析函数
            const parsedProperties = this.parseCategoryProperties(category.properties);
            this.categoryProperties = parsedProperties;
            // 重置动态属性
            this.dynamicProperties = {};
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
    onClose() {
      this.$emit('update:visible', false);
    },
    handleSubmit() {
      // 将动态属性合并到资产属性中
      if (Object.keys(this.dynamicProperties).length > 0) {
        this.form.properties = JSON.stringify(this.dynamicProperties);
      }
      
      this.$emit('submit', this.form);
    }
  }
}
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style>