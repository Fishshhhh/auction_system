import axios from 'axios'

// 创建axios实例
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 响应拦截器
apiClient.interceptors.response.use(
  response => {
    // 直接返回整个响应对象，而不是response.data
    return response
  },
  error => {
    console.error('API Error:', error)
    return Promise.reject({
      code: (error.response && error.response.status) || 500,
      message: error.message || '网络错误',
      data: null
    })
  }
)

export const configApi = {
  // 获取资产分类
  async getCategories() {
    try {
      const response = await apiClient.get('/config/categories')
      // 直接返回响应，axios拦截器会处理
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 创建资产分类
  async createCategory(category) {
    try {
      const response = await apiClient.post('/config/categories', category)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 更新资产分类
  async updateCategory(id, category) {
    try {
      const response = await apiClient.put(`/config/categories/${id}`, category)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 删除资产分类
  async deleteCategory(id) {
    try {
      const response = await apiClient.delete(`/config/categories/${id}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 搜索资产分类
  async searchCategories(keyword) {
    try {
      const response = await apiClient.get(`/config/categories/search?keyword=${keyword}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 获取拍卖模板
  async getTemplates() {
    try {
      const response = await apiClient.get('/config/templates')
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 创建拍卖模板
  async createTemplate(template) {
    try {
      const response = await apiClient.post('/config/templates', template)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 更新拍卖模板
  async updateTemplate(id, template) {
    try {
      const response = await apiClient.put(`/config/templates/${id}`, template)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 删除拍卖模板
  async deleteTemplate(id) {
    try {
      const response = await apiClient.delete(`/config/templates/${id}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 根据类型获取拍卖模板
  async getTemplatesByType(type) {
    try {
      const response = await apiClient.get(`/config/templates/type/${type}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 获取系统配置
  async getSystemConfigs() {
    try {
      const response = await apiClient.get('/config/system')
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 根据配置组获取系统配置
  async getSystemConfigsByGroup(group) {
    try {
      const response = await apiClient.get(`/config/system/group/${group}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 根据配置键获取系统配置
  async getSystemConfigByKey(key) {
    try {
      const response = await apiClient.get(`/config/system/key/${key}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 创建系统配置
  async createSystemConfig(config) {
    try {
      const response = await apiClient.post('/config/system', config)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 更新系统配置
  async updateSystemConfig(id, config) {
    try {
      const response = await apiClient.put(`/config/system/${id}`, config)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 删除系统配置
  async deleteSystemConfig(id) {
    try {
      const response = await apiClient.delete(`/config/system/${id}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  }
}