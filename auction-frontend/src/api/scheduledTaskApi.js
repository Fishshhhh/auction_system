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

export const scheduledTaskApi = {
  // 获取所有定时任务
  async getScheduledTasks() {
    try {
      const response = await apiClient.get('/scheduled-tasks')
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },

  // 根据ID获取定时任务
  async getScheduledTaskById(id) {
    try {
      const response = await apiClient.get(`/scheduled-tasks/${id}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },

  // 创建定时任务
  async createScheduledTask(data) {
    try {
      const response = await apiClient.post('/scheduled-tasks', data)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },

  // 更新定时任务
  async updateScheduledTask(id, data) {
    try {
      const response = await apiClient.put(`/scheduled-tasks/${id}`, data)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },

  // 删除定时任务
  async deleteScheduledTask(id) {
    try {
      const response = await apiClient.delete(`/scheduled-tasks/${id}`)
      return response.data
    } catch (error) {
      return {
        code: (error && error.code) || 500,
        message: (error && error.message) || '未知错误',
        data: null
      }
    }
  },

  // 立即执行定时任务
  async executeScheduledTask(id) {
    try {
      const response = await apiClient.post(`/scheduled-tasks/${id}/execute`)
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