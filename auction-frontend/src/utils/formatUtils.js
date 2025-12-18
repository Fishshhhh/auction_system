// 通用格式化工具函数
export const formatDate = (dateString) => {
  if (!dateString) return '';
  return new Date(dateString).toLocaleString('zh-CN');
};

export const formatCurrency = (amount) => {
  return `¥${amount}`;
};

export const getStatusText = (status, statusMap) => {
  return statusMap[status] || '未知';
};

export const getStatusType = (status, typeMap) => {
  return typeMap[status] || '';
};