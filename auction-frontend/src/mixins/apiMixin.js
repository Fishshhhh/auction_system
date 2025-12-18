// 通用API混入，提供基本的API调用方法和错误处理
export default {
  methods: {
    // 通用错误处理方法
    handleApiError(error, defaultMessage = '操作失败') {
      console.error('API Error:', error);
      const message = (error && error.message) || defaultMessage;
      this.$message.error(message);
      return Promise.reject(error);
    },
    
    // 通用成功处理方法
    handleApiSuccess(response, defaultMessage = '操作成功') {
      if (response && response.code === 200) {
        const message = (response.message) || defaultMessage;
        this.$message.success(message);
        return Promise.resolve(response);
      } else {
        const message = (response && response.message) || defaultMessage;
        this.$message.error(message);
        return Promise.reject(new Error(message));
      }
    }
  }
};