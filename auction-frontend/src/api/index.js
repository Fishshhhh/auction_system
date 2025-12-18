import { assetApi } from './assetApi';
import { auctionApi } from './auctionApi';
import { userApi } from './userApi';
import { configApi } from './configApi';
import { orderApi } from './orderApi';
import { scheduledTaskApi } from './scheduledTaskApi';

// 统一导出所有API模块
export {
  assetApi,
  auctionApi,
  userApi,
  configApi,
  orderApi,
  scheduledTaskApi
};

// 或者创建一个包含所有API的对象
export const api = {
  asset: assetApi,
  auction: auctionApi,
  user: userApi,
  config: configApi,
  order: orderApi,
  scheduledTask: scheduledTaskApi
};