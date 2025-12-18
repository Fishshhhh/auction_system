import axios from 'axios';

// 创建基础axios实例
const baseApiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

export default baseApiClient;