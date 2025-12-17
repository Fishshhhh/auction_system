<template>
  <div class="config-management-view">
    <el-tabs v-model="activeTab">
      <!-- 资产分类管理 -->
      <el-tab-pane label="资产分类" name="categories">
        <el-card>
          <div slot="header" class="card-header">
            <span>资产分类管理</span>
            <el-button type="primary" @click="showCategoryDialog()">添加分类</el-button>
          </div>
          
          <el-table :data="categories" stripe style="width: 100%">
            <el-table-column prop="name" label="分类名称" min-width="150"></el-table-column>
            <el-table-column prop="code" label="分类编码" min-width="120"></el-table-column>
            <el-table-column prop="properties" label="属性" min-width="200"></el-table-column>
            <el-table-column prop="description" label="描述" min-width="200"></el-table-column>
            <el-table-column prop="status" label="状态" min-width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
                  {{ scope.row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" min-width="150">
              <template slot-scope="scope">
                <el-button size="small" @click="editCategory(scope.row)">编辑</el-button>
                <el-button 
                  size="small" 
                  :type="scope.row.status === 1 ? 'warning' : 'success'"
                  @click="toggleCategoryStatus(scope.row)">
                  {{ scope.row.status === 1 ? '禁用' : '启用' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
      
      <!-- 拍卖模板管理 -->
      <el-tab-pane label="拍卖模板" name="templates">
        <el-card>
          <div slot="header" class="card-header">
            <span>拍卖模板管理</span>
            <el-button type="primary" @click="showTemplateDialog()">添加模板</el-button>
          </div>
          
          <el-table :data="templates" stripe style="width: 100%">
            <el-table-column prop="name" label="模板名称" min-width="150"></el-table-column>
            <el-table-column label="拍卖类型" min-width="120">
              <template slot-scope="scope">
                <el-tag :type="getAuctionTypeTagType(scope.row.auctionType)">
                  {{ getAuctionTypeText(scope.row.auctionType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" min-width="200"></el-table-column>
            <el-table-column prop="isDefault" label="默认模板" min-width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.isDefault === 1 ? 'success' : 'info'">
                  {{ scope.row.isDefault === 1 ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" min-width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
                  {{ scope.row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" min-width="150">
              <template slot-scope="scope">
                <el-button size="small" @click="editTemplate(scope.row)">编辑</el-button>
                <el-button 
                  size="small" 
                  :type="scope.row.status === 1 ? 'warning' : 'success'"
                  @click="toggleTemplateStatus(scope.row)">
                  {{ scope.row.status === 1 ? '禁用' : '启用' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
      
      <!-- 系统配置 -->
      <el-tab-pane label="系统配置" name="system">
        <el-card>
          <div slot="header" class="card-header">
            <span>系统配置</span>
            <el-button type="primary" @click="showConfigDialog()">添加配置</el-button>
          </div>
          
          <el-table :data="systemConfigs" stripe style="width: 100%">
            <el-table-column prop="displayName" label="配置名称" min-width="150"></el-table-column>
            <el-table-column prop="configKey" label="配置键" min-width="150"></el-table-column>
            <el-table-column prop="configValue" label="配置值" min-width="150"></el-table-column>
            <el-table-column prop="configGroup" label="配置组" min-width="120"></el-table-column>
            <el-table-column prop="description" label="描述" min-width="200"></el-table-column>
            <el-table-column prop="status" label="状态" min-width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
                  {{ scope.row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" min-width="150">
              <template slot-scope="scope">
                <el-button size="small" @click="editConfig(scope.row)">编辑</el-button>
                <el-button 
                  size="small" 
                  :type="scope.row.status === 1 ? 'warning' : 'success'"
                  @click="toggleConfigStatus(scope.row)">
                  {{ scope.row.status === 1 ? '禁用' : '启用' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>
    
    <!-- 分类编辑对话框 -->
    <el-dialog :visible.sync="categoryDialogVisible" :title="editingCategory ? '编辑分类' : '添加分类'" width="500px">
      <el-form :model="categoryForm" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" />
        </el-form-item>
        <el-form-item label="分类编码" prop="code">
          <el-input v-model="categoryForm.code" />
        </el-form-item>
        <el-form-item label="属性" prop="properties">
          <el-input v-model="categoryForm.properties" type="textarea" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="categoryForm.description" type="textarea" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="categoryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCategory">保存</el-button>
      </div>
    </el-dialog>
    
    <!-- 模板编辑对话框 -->
    <el-dialog :visible.sync="templateDialogVisible" :title="editingTemplate ? '编辑模板' : '添加模板'" width="600px">
      <el-form :model="templateForm" label-width="120px">
        <el-form-item label="模板名称" prop="name">
          <el-input v-model="templateForm.name" />
        </el-form-item>
        <el-form-item label="拍卖类型" prop="auctionType">
          <el-select v-model="templateForm.auctionType" placeholder="请选择拍卖类型" style="width: 100%" @change="onAuctionTypeChange">
            <el-option label="公开实时竞价" :value="1"></el-option>
            <el-option label="暗拍" :value="2"></el-option>
            <el-option label="无底价拍卖" :value="3"></el-option>
            <el-option label="定向拍卖" :value="4"></el-option>
            <el-option label="降价拍卖" :value="5"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="templateForm.description" type="textarea" />
        </el-form-item>
        <el-form-item label="默认模板" prop="isDefault">
          <el-switch
            v-model="templateForm.isDefault"
            :active-value="1"
            :inactive-value="0">
          </el-switch>
        </el-form-item>
        
        <!-- 公开竞价和无底价拍卖参数 -->
        <div v-if="templateForm.auctionType === 1 || templateForm.auctionType === 3">
          <el-form-item label="加价幅度" prop="bidIncrement">
            <el-input-number v-model="templateForm.bidIncrement" :min="0" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="延时时间(分钟)" prop="extendTime">
            <el-input-number v-model="templateForm.extendTime" :min="0" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="延时阈值(秒)" prop="extendThreshold">
            <el-input-number v-model="templateForm.extendThreshold" :min="0" controls-position="right" style="width: 100%" />
          </el-form-item>
        </div>
        
        <!-- 降价拍卖参数 -->
        <div v-if="templateForm.auctionType === 5">
          <el-form-item label="初始价" prop="initialPrice">
            <el-input-number v-model="templateForm.initialPrice" :min="0" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="降价阶梯" prop="priceStep">
            <el-input-number v-model="templateForm.priceStep" :min="0" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="降价周期(分钟)" prop="priceDropInterval">
            <el-input-number v-model="templateForm.priceDropInterval" :min="1" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="最低成交价" prop="minPrice">
            <el-input-number v-model="templateForm.minPrice" :min="0" controls-position="right" style="width: 100%" />
          </el-form-item>
        </div>
        
        <!-- 定向拍卖参数 -->
        <div v-if="templateForm.auctionType === 4">
          <el-form-item label="需要资格认证" prop="qualificationRequired">
            <el-switch
              v-model="templateForm.qualificationRequired"
              :active-value="true"
              :inactive-value="false">
            </el-switch>
          </el-form-item>
        </div>
        
        <!-- 保证金参数 -->
        <el-form-item label="保证金比例(%)" prop="depositRate">
          <el-input-number v-model="templateForm.depositRate" :min="0" :max="30" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="templateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTemplate">保存</el-button>
      </div>
    </el-dialog>
    
    <!-- 系统配置编辑对话框 -->
    <el-dialog :visible.sync="configDialogVisible" :title="editingConfig ? '编辑配置' : '添加配置'" width="500px">
      <el-form :model="configForm" label-width="100px">
        <el-form-item label="配置名称" prop="displayName">
          <el-input v-model="configForm.displayName" />
        </el-form-item>
        <el-form-item label="配置键" prop="configKey">
          <el-input v-model="configForm.configKey" />
        </el-form-item>
        <el-form-item label="配置值" prop="configValue">
          <el-input v-model="configForm.configValue" />
        </el-form-item>
        <el-form-item label="配置组" prop="configGroup">
          <el-input v-model="configForm.configGroup" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="configForm.description" type="textarea" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveConfig">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { configApi } from '../api/configApi'

export default {
  name: 'ConfigManagement',
  data() {
    return {
      activeTab: 'categories',
      categories: [],
      templates: [],
      systemConfigs: [],
      
      // 分类相关
      categoryDialogVisible: false,
      editingCategory: false,
      categoryForm: {
        name: '',
        code: '',
        properties: '',
        description: ''
      },
      
      // 模板相关
      templateDialogVisible: false,
      editingTemplate: false,
      templateForm: {
        name: '',
        auctionType: 1,
        description: '',
        isDefault: 0,
        bidIncrement: 0,
        extendTime: 0,
        extendThreshold: 0,
        initialPrice: 0,
        priceStep: 0,
        priceDropInterval: 1,
        minPrice: 0,
        qualificationRequired: false,
        depositRate: 0
      },
      
      // 系统配置相关
      configDialogVisible: false,
      editingConfig: false,
      configForm: {
        displayName: '',
        configKey: '',
        configValue: '',
        configGroup: '',
        description: ''
      }
    }
  },
  methods: {
    // 加载所有配置数据
    async loadAllConfigData() {
      try {
        await this.loadCategories()
        await this.loadTemplates()
        await this.loadSystemConfigs()
      } catch (error) {
        console.error('加载配置数据失败:', error)
        this.$message.error('加载配置数据失败: ' + (error.message || '未知错误'))
      }
    },
    
    // 资产分类相关方法
    async loadCategories() {
      try {
        const response = await configApi.getCategories()
        console.log('Categories API Response:', response)
        if (response.code === 200) {
          // 确保响应数据是数组类型
          console.log('Raw response data:', response.data)
          this.categories = Array.isArray(response.data) ? response.data : []
          console.log('Categories assigned to this.categories:', this.categories)
          console.log('Categories length:', this.categories.length)
        } else {
          this.$message.error(response.message || '加载资产分类失败')
        }
      } catch (error) {
        console.error('Load categories error:', error)
        // 发生错误时确保categories仍然是数组
        this.categories = []
        this.$message.error('加载资产分类失败: ' + (error.message || '未知错误'))
      }
    },
    
    showCategoryDialog() {
      this.editingCategory = false
      this.categoryForm = {
        name: '',
        code: '',
        properties: '',
        description: ''
      }
      this.categoryDialogVisible = true
    },
    
    editCategory(category) {
      this.editingCategory = true
      // 使用 Object.assign 替代对象展开运算符
      this.categoryForm = Object.assign({}, category)
      this.categoryDialogVisible = true
    },
    
    async saveCategory() {
      try {
        let response
        if (this.editingCategory) {
          response = await configApi.updateCategory(this.categoryForm.id, this.categoryForm)
        } else {
          response = await configApi.createCategory(this.categoryForm)
        }
        
        if (response.code === 200) {
          this.$message.success(this.editingCategory ? '更新成功' : '创建成功')
          this.categoryDialogVisible = false
          this.loadCategories()
        } else {
          this.$message.error(response.message || (this.editingCategory ? '更新失败' : '创建失败'))
        }
      } catch (error) {
        this.$message.error(this.editingCategory ? '更新失败: ' + (error.message || '未知错误') : '创建失败: ' + (error.message || '未知错误'))
      }
    },
    
    async toggleCategoryStatus(category) {
      try {
        const updatedCategory = Object.assign({}, category, { status: category.status === 1 ? 0 : 1 })
        const response = await configApi.updateCategory(category.id, updatedCategory)
        if (response.code === 200) {
          this.$message.success('状态更新成功')
          this.loadCategories()
        } else {
          this.$message.error(response.message || '状态更新失败')
        }
      } catch (error) {
        this.$message.error('状态更新失败: ' + (error.message || '未知错误'))
      }
    },
    
    // 拍卖模板相关方法
    async loadTemplates() {
      try {
        const response = await configApi.getTemplates()
        if (response.code === 200) {
          // 确保响应数据是数组类型
          this.templates = Array.isArray(response.data) ? response.data : []
        } else {
          this.$message.error(response.message || '加载拍卖模板失败')
        }
      } catch (error) {
        // 发生错误时确保templates仍然是数组
        this.templates = []
        this.$message.error('加载拍卖模板失败: ' + (error.message || '未知错误'))
      }
    },
    
    showTemplateDialog() {
      this.editingTemplate = false
      this.templateForm = {
        name: '',
        auctionType: 1,
        description: '',
        isDefault: 0,
        bidIncrement: 0,
        extendTime: 0,
        extendThreshold: 0,
        initialPrice: 0,
        priceStep: 0,
        priceDropInterval: 1,
        minPrice: 0,
        qualificationRequired: false,
        depositRate: 0
      }
      this.templateDialogVisible = true
    },
    
    editTemplate(template) {
      this.editingTemplate = true
      // 使用 Object.assign 替代对象展开运算符
      this.templateForm = Object.assign({}, template)
      this.templateDialogVisible = true
    },
    
    onAuctionTypeChange(value) {
      // 当拍卖类型改变时，重置相关参数
      this.templateForm.bidIncrement = 0
      this.templateForm.extendTime = 0
      this.templateForm.extendThreshold = 0
      this.templateForm.initialPrice = 0
      this.templateForm.priceStep = 0
      this.templateForm.priceDropInterval = 1
      this.templateForm.minPrice = 0
      this.templateForm.qualificationRequired = false
    },
    
    async saveTemplate() {
      try {
        let response
        if (this.editingTemplate) {
          response = await configApi.updateTemplate(this.templateForm.id, this.templateForm)
        } else {
          response = await configApi.createTemplate(this.templateForm)
        }
        
        if (response.code === 200) {
          this.$message.success(this.editingTemplate ? '更新成功' : '创建成功')
          this.templateDialogVisible = false
          this.loadTemplates()
        } else {
          this.$message.error(response.message || (this.editingTemplate ? '更新失败' : '创建失败'))
        }
      } catch (error) {
        this.$message.error(this.editingTemplate ? '更新失败: ' + (error.message || '未知错误') : '创建失败: ' + (error.message || '未知错误'))
      }
    },
    
    async toggleTemplateStatus(template) {
      try {
        const updatedTemplate = Object.assign({}, template, { status: template.status === 1 ? 0 : 1 })
        const response = await configApi.updateTemplate(template.id, updatedTemplate)
        if (response.code === 200) {
          this.$message.success('状态更新成功')
          this.loadTemplates()
        } else {
          this.$message.error(response.message || '状态更新失败')
        }
      } catch (error) {
        this.$message.error('状态更新失败: ' + (error.message || '未知错误'))
      }
    },
    
    // 系统配置相关方法
    async loadSystemConfigs() {
      try {
        const response = await configApi.getSystemConfigs()
        if (response.code === 200) {
          // 确保响应数据是数组类型
          this.systemConfigs = Array.isArray(response.data) ? response.data : []
        } else {
          this.$message.error(response.message || '加载系统配置失败')
        }
      } catch (error) {
        // 发生错误时确保systemConfigs仍然是数组
        this.systemConfigs = []
        this.$message.error('加载系统配置失败: ' + (error.message || '未知错误'))
      }
    },
    
    showConfigDialog() {
      this.editingConfig = false
      this.configForm = {
        displayName: '',
        configKey: '',
        configValue: '',
        configGroup: '',
        description: ''
      }
      this.configDialogVisible = true
    },
    
    editConfig(config) {
      this.editingConfig = true
      // 使用 Object.assign 替代对象展开运算符
      this.configForm = Object.assign({}, config)
      this.configDialogVisible = true
    },
    
    async saveConfig() {
      try {
        let response
        if (this.editingConfig) {
          response = await configApi.updateSystemConfig(this.configForm.id, this.configForm)
        } else {
          response = await configApi.createSystemConfig(this.configForm)
        }
        
        if (response.code === 200) {
          this.$message.success(this.editingConfig ? '更新成功' : '创建成功')
          this.configDialogVisible = false
          this.loadSystemConfigs()
        } else {
          this.$message.error(response.message || (this.editingConfig ? '更新失败' : '创建失败'))
        }
      } catch (error) {
        this.$message.error(this.editingConfig ? '更新失败: ' + (error.message || '未知错误') : '创建失败: ' + (error.message || '未知错误'))
      }
    },
    
    async toggleConfigStatus(config) {
      try {
        const updatedConfig = Object.assign({}, config, { status: config.status === 1 ? 0 : 1 })
        const response = await configApi.updateSystemConfig(config.id, updatedConfig)
        if (response.code === 200) {
          this.$message.success('状态更新成功')
          this.loadSystemConfigs()
        } else {
          this.$message.error(response.message || '状态更新失败')
        }
      } catch (error) {
        this.$message.error('状态更新失败: ' + (error.message || '未知错误'))
      }
    },
    
    // 工具方法
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
    }
  },
  mounted() {
    this.loadAllConfigData()
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  text-align: right;
}
</style>