import axios from 'axios'
import baseApiClient from './baseApi';
import { handleResponse, handleError } from '@/utils/apiHelper';

export const assetApi = {
  // 获取所有资产
  async getAssets() {
    try {
      const response = await baseApiClient.get('/assets');
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 根据ID获取资产详情
  async getAssetById(id) {
    try {
      const response = await baseApiClient.get(`/assets/${id}`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 创建资产
  async createAsset(asset) {
    try {
      const response = await baseApiClient.post('/assets', asset);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 更新资产
  async updateAsset(id, asset) {
    try {
      const response = await baseApiClient.put(`/assets/${id}`, asset);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 删除资产
  async deleteAsset(id) {
    try {
      const response = await baseApiClient.delete(`/assets/${id}`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 根据状态获取资产
  async getAssetsByStatus(status) {
    try {
      const response = await baseApiClient.get(`/assets/status/${status}`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 根据分类获取资产
  async getAssetsByCategory(categoryId) {
    try {
      const response = await baseApiClient.get(`/assets/category/${categoryId}`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 根据所有者获取资产
  async getAssetsByOwner(ownerId) {
    try {
      const response = await baseApiClient.get(`/assets/owner/${ownerId}`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 搜索资产
  async searchAssets(keyword) {
    try {
      const response = await baseApiClient.get(`/assets/search?keyword=${keyword}`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 发布资产
  async publishAsset(id) {
    try {
      const response = await baseApiClient.put(`/assets/${id}/publish`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 取消发布资产
  async unpublishAsset(id) {
    try {
      const response = await baseApiClient.put(`/assets/${id}/unpublish`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 批量发布资产
  async batchPublishAssets(assetIds) {
    try {
      const response = await baseApiClient.put('/assets/batch-publish', assetIds);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 获取资产图片
  async getAssetImages(id) {
    try {
      const response = await baseApiClient.get(`/assets/${id}/images`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 获取资产封面图片
  async getAssetCoverImage(id) {
    try {
      const response = await baseApiClient.get(`/assets/${id}/cover-image`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 添加资产图片
  async addAssetImage(id, image) {
    try {
      const response = await baseApiClient.post(`/assets/${id}/images`, image);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 删除资产图片
  async deleteAssetImage(imageId) {
    try {
      const response = await baseApiClient.delete(`/assets/images/${imageId}`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 设置资产封面图片
  async setCoverImage(assetId, imageId) {
    try {
      const response = await baseApiClient.put(`/assets/${assetId}/cover-image/${imageId}`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  }
}
