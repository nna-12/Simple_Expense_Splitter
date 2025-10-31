import { ref } from 'vue'
import { expenseAPI } from '@/services/api'

export function useExpenses() {
  const expenses = ref([])
  const settlements = ref([])
  const loading = ref(false)
  const error = ref(null)

  const fetchGroupExpenses = async (groupId) => {
    loading.value = true
    error.value = null

    try {
      const response = await expenseAPI.getByGroupId(groupId)
      expenses.value = response.data
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Failed to fetch expenses'
      throw err
    } finally {
      loading.value = false
    }
  }

  const createExpense = async (expenseData) => {
    loading.value = true
    error.value = null

    try {
      const response = await expenseAPI.create(expenseData)
      expenses.value.push(response.data)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Failed to create expense'
      throw err
    } finally {
      loading.value = false
    }
  }

  const deleteExpense = async (expenseId) => {
    loading.value = true
    error.value = null

    try {
      await expenseAPI.delete(expenseId)
      expenses.value = expenses.value.filter(e => e.id !== expenseId)
    } catch (err) {
      error.value = err.response?.data?.message || 'Failed to delete expense'
      throw err
    } finally {
      loading.value = false
    }
  }

  const calculateSettlements = async (groupId) => {
    loading.value = true
    error.value = null

    try {
      const response = await expenseAPI.getSettlements(groupId)
      settlements.value = response.data
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Failed to calculate settlements'
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    expenses,
    settlements,
    loading,
    error,
    fetchGroupExpenses,
    createExpense,
    deleteExpense,
    calculateSettlements,
  }
}
