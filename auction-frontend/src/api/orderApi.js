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

export const orderApi = {
  // 获取所有订单
  async getOrders() {
    try {
      const response = await apiClient.get('/orders')
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 根据ID获取订单详情
  async getOrderById(id) {
    try {
      const response = await apiClient.get(`/orders/${id}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 根据订单号获取订单
  async getOrderByOrderNo(orderNo) {
    try {
      const response = await apiClient.get(`/orders/order-no/${orderNo}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 创建订单
  async createOrder(order) {
    try {
      const response = await apiClient.post('/orders', order)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 更新订单
  async updateOrder(id, order) {
    try {
      const response = await apiClient.put(`/orders/${id}`, order)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 删除订单
  async deleteOrder(id) {
    try {
      const response = await apiClient.delete(`/orders/${id}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 根据买家获取订单
  async getOrdersByBuyer(buyerId) {
    try {
      const response = await apiClient.get(`/orders/buyer/${buyerId}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 根据卖家获取订单
  async getOrdersBySeller(sellerId) {
    try {
      const response = await apiClient.get(`/orders/seller/${sellerId}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 根据状态获取订单
  async getOrdersByStatus(status) {
    try {
      const response = await apiClient.get(`/orders/status/${status}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 模拟支付订单
  async payOrder(id) {
    try {
      const response = await apiClient.put(`/orders/${id}/pay`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },
  
  // 完成订单
  async completeOrder(id) {
    try {
      const response = await apiClient.put(`/orders/${id}/complete`)
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