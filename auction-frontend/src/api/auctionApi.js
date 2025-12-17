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

export const auctionApi = {
  // 获取所有拍卖
  async getAuctions() {
    try {
      const response = await apiClient.get('/auctions')
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 根据ID获取拍卖详情
  async getAuctionById(id) {
    try {
      const response = await apiClient.get(`/auctions/${id}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 创建拍卖
  async createAuction(auction) {
    try {
      const response = await apiClient.post('/auctions', auction)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 更新拍卖
  async updateAuction(id, auction) {
    try {
      const response = await apiClient.put(`/auctions/${id}`, auction)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 删除拍卖
  async deleteAuction(id) {
    try {
      const response = await apiClient.delete(`/auctions/${id}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 根据状态获取拍卖
  async getAuctionsByStatus(status) {
    try {
      const response = await apiClient.get(`/auctions/status/${status}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 获取拍卖出价记录
  async getAuctionBids(id) {
    try {
      const response = await apiClient.get(`/auctions/${id}/bids`)
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
  async submitBid(auctionId, bid) {
    try {
      const response = await apiClient.post(`/auctions/${auctionId}/bids`, bid)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 获取用户出价记录
  async getUserBids(userId) {
    try {
      const response = await apiClient.get(`/auctions/user/${userId}/bids`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 手动开始拍卖
  async startAuction(id) {
    try {
      const response = await apiClient.post(`/auctions/${id}/start`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 手动结束拍卖
  async endAuction(id) {
    try {
      const response = await apiClient.post(`/auctions/${id}/end`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 为拍卖添加资产
  async addAssetToAuction(auctionId, auctionAsset) {
    try {
      const response = await apiClient.post(`/auctions/${auctionId}/assets`, auctionAsset)
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