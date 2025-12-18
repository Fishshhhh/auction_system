import axios from 'axios'
import baseApiClient from './baseApi';
import { handleResponse, handleError } from '@/utils/apiHelper';

export const auctionApi = {
  // 根据资产ID获取相关拍卖
  async getAuctionsByAssetId(assetId) {
    try {
      const response = await baseApiClient.get(`/auctions/assets/${assetId}`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 获取所有拍卖
  async getAuctions() {
    try {
      const response = await baseApiClient.get('/auctions');
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 根据ID获取拍卖详情
  async getAuctionById(id) {
    try {
      const response = await baseApiClient.get(`/auctions/${id}`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 创建拍卖
  async createAuction(auction) {
    try {
      const response = await baseApiClient.post('/auctions', auction);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 更新拍卖
  async updateAuction(id, auction) {
    try {
      const response = await baseApiClient.put(`/auctions/${id}`, auction);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 删除拍卖
  async deleteAuction(id) {
    try {
      const response = await baseApiClient.delete(`/auctions/${id}`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 根据状态获取拍卖
  async getAuctionsByStatus(status) {
    try {
      const response = await baseApiClient.get(`/auctions/status/${status}`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 获取拍卖出价记录
  async getAuctionBids(id) {
    try {
      const response = await baseApiClient.get(`/auctions/${id}/bids`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 提交出价
  async submitBid(auctionId, bid) {
    try {
      const response = await baseApiClient.post(`/auctions/${auctionId}/bids`, bid);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 获取拍卖关联的资产
  async getAuctionAssets(auctionId) {
    try {
      const response = await baseApiClient.get(`/auctions/${auctionId}/assets`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 获取用户出价记录
  async getUserBids(userId) {
    try {
      const response = await baseApiClient.get(`/auctions/user/${userId}/bids`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 获取所有出价记录
  async getAllBids() {
    try {
      const response = await baseApiClient.get('/auctions/bids');
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 手动开始拍卖
  async startAuction(id) {
    try {
      const response = await baseApiClient.post(`/auctions/${id}/start`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 手动结束拍卖
  async endAuction(id) {
    try {
      const response = await baseApiClient.post(`/auctions/${id}/end`);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  },
  
  // 为拍卖添加资产
  async addAssetToAuction(auctionId, auctionAsset) {
    try {
      const response = await baseApiClient.post(`/auctions/${auctionId}/assets`, auctionAsset);
      return handleResponse(response);
    } catch (error) {
      return handleError(error);
    }
  }
}
