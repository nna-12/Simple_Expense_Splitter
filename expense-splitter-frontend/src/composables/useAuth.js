import { ref, computed } from 'vue'
import { authAPI } from '@/services/api'
import { useRouter } from 'vue-router'

export function useAuth() {
  const router = useRouter()
  const loading = ref(false)
  const error = ref(null)

  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const token = ref(localStorage.getItem('jwt_token') || null)

  const isAuthenticated = computed(() => !!token.value && !!user.value)

  const register = async (userData) => {
    loading.value = true
    error.value = null

    try {
      const response = await authAPI.register(userData)
      const { token: newToken, userId, email, name } = response.data

      localStorage.setItem('jwt_token', newToken)
      localStorage.setItem('user', JSON.stringify({ userId, email, name }))

      token.value = newToken
      user.value = { userId, email, name }

      router.push('/dashboard')
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Registration failed'
      throw err
    } finally {
      loading.value = false
    }
  }

  const login = async (credentials) => {
    loading.value = true
    error.value = null

    try {
      const response = await authAPI.login(credentials)
      const { token: newToken, userId, email, name } = response.data

      localStorage.setItem('jwt_token', newToken)
      localStorage.setItem('user', JSON.stringify({ userId, email, name }))

      token.value = newToken
      user.value = { userId, email, name }

      router.push('/dashboard')
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Login failed'
      throw err
    } finally {
      loading.value = false
    }
  }

  const logout = () => {
    localStorage.removeItem('jwt_token')
    localStorage.removeItem('user')
    token.value = null
    user.value = null
    router.push('/login')
  }

  return {
    user,
    token,
    isAuthenticated,
    loading,
    error,
    register,
    login,
    logout,
  }
}
