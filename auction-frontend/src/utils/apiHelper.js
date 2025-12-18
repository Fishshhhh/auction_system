// API工具函数，用于统一处理响应和错误
export const handleResponse = (response) => {
  return response.data;
};

export const handleError = (error) => {
  console.error('API Error:', error);
  return {
    code: (error.response && error.response.status) || 500,
    message: error.message || '网络错误',
    data: null
  };
};