import axios from 'axios'
import baseApiClient from './baseApi';
import { handleResponse, handleError } from '@/utils/apiHelper';

export const userApi = {
  // 用户登录
  async login(credentials) {
    try {
      const response = await baseApiClient.post('/auth/login', credentials);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 用户注册
  async register(userInfo) {
    try {
      const response = await baseApiClient.post('/auth/register', userInfo);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 获取当前用户信息
  async getCurrentUser() {
    try {
      const response = await baseApiClient.get('/users/me');
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 更新用户信息
  async updateUserInfo(userInfo) {
    try {
      const response = await baseApiClient.put('/users/me', userInfo);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 修改密码
  async changePassword(passwordInfo) {
    try {
      const response = await baseApiClient.put('/users/password', passwordInfo);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  }
}
