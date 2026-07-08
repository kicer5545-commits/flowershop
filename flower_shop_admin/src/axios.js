import axios from 'axios'

const axiosInstance = axios.create({
  baseURL: '/api',
  timeout: 5000
})

axiosInstance.interceptors.request.use(config => {
  const user = localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : null
  if (user && user.token) {
    config.headers.token = user.token
  }
  if (config.headers['Content-Type'] !== 'multipart/form-data') {
    config.headers['Content-Type'] = 'application/json;charset=utf-8'
  }
  return config
}, error => {
  return Promise.reject(error)
})

axiosInstance.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

export default axiosInstance
