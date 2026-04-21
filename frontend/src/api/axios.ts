import axios from 'axios'
import { useAuthStore } from '@/store/authStore'

export const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true
      const { refreshToken, setAuth, clearAuth } = useAuthStore.getState()
      if (refreshToken) {
        try {
          const { data } = await axios.post('/api/auth/refresh', { refreshToken })
          setAuth(
            { accessToken: data.accessToken, refreshToken: data.refreshToken },
            { id: data.userId, username: data.username, email: data.email, role: data.role },
          )
          original.headers.Authorization = `Bearer ${data.accessToken}`
          return api(original)
        } catch {
          clearAuth()
        }
      }
    }
    return Promise.reject(error)
  },
)
