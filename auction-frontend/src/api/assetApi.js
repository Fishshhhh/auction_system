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

export const assetApi = {
  // 获取所有资产
  async getAssets() {
    try {
      const response = await apiClient.get('/assets')
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 根据ID获取资产详情
  async getAssetById(id) {
    try {
      const response = await apiClient.get(`/assets/${id}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 创建资产
  async createAsset(asset) {
    try {
      const response = await apiClient.post('/assets', asset)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 更新资产
  async updateAsset(id, asset) {
    try {
      const response = await apiClient.put(`/assets/${id}`, asset)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 删除资产
  async deleteAsset(id) {
    try {
      const response = await apiClient.delete(`/assets/${id}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 根据状态获取资产
  async getAssetsByStatus(status) {
    try {
      const response = await apiClient.get(`/assets/status/${status}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 根据分类获取资产
  async getAssetsByCategory(categoryId) {
    try {
      const response = await apiClient.get(`/assets/category/${categoryId}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 根据所有者获取资产
  async getAssetsByOwner(ownerId) {
    try {
      const response = await apiClient.get(`/assets/owner/${ownerId}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 搜索资产
  async searchAssets(keyword) {
    try {
      const response = await apiClient.get(`/assets/search?keyword=${keyword}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 发布资产
  async publishAsset(id) {
    try {
      const response = await apiClient.put(`/assets/${id}/publish`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 取消发布资产
  async unpublishAsset(id) {
    try {
      const response = await apiClient.put(`/assets/${id}/unpublish`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 批量发布资产
  async batchPublishAssets(assetIds) {
    try {
      const response = await apiClient.put('/assets/batch-publish', assetIds)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 获取资产图片
  async getAssetImages(id) {
    try {
      const response = await apiClient.get(`/assets/${id}/images`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 获取资产封面图片
  async getAssetCoverImage(id) {
    try {
      const response = await apiClient.get(`/assets/${id}/cover-image`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 添加资产图片
  async addAssetImage(id, image) {
    try {
      const response = await apiClient.post(`/assets/${id}/images`, image)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 删除资产图片
  async deleteAssetImage(imageId) {
    try {
      const response = await apiClient.delete(`/assets/images/${imageId}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 设置资产封面图片
  async setCoverImage(assetId, imageId) {
    try {
      const response = await apiClient.put(`/assets/${assetId}/cover-image/${imageId}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 获取资产相关的拍卖
  async getAssetAuctions(id) {
    try {
      const response = await apiClient.get(`/assets/${id}/auctions`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 提交出价
  async submitBid(assetId, bid) {
    try {
      const response = await apiClient.post(`/assets/${assetId}/bids`, bid)
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