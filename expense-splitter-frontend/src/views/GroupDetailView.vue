<template>
  <div class="min-h-screen bg-gray-100">
    <!-- Navbar -->
    <nav class="bg-white shadow-lg">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center space-x-4">
            <button @click="goBack" class="text-gray-600 hover:text-gray-900">
              ← Back
            </button>
            <h1 class="text-xl font-bold text-gray-900">{{ group?.name || 'Loading...' }}</h1>
          </div>
          <div class="flex items-center space-x-4">
            <span class="text-gray-700">{{ user?.name }}</span>
            <button @click="logout" class="btn btn-secondary">
              Logout
            </button>
          </div>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="px-4 py-6 sm:px-0">
        
        <!-- Group Info Card -->
        <div class="card mb-6">
          <div class="flex justify-between items-start">
            <div>
              <h2 class="text-2xl font-bold text-gray-900">{{ group?.name }}</h2>
              <p class="text-gray-600 mt-1">{{ group?.description }}</p>
              <p class="text-sm text-gray-500 mt-2">
                {{ group?.memberIds?.length || 0 }} members
              </p>
            </div>
            <button @click="showAddExpenseModal = true" class="btn btn-primary">
              + Add Expense
            </button>
          </div>
        </div>

        <!-- Tabs -->
        <div class="border-b border-gray-200 mb-6">
          <nav class="-mb-px flex space-x-8">
            <button
              @click="activeTab = 'expenses'"
              :class="[
                'py-4 px-1 border-b-2 font-medium text-sm',
                activeTab === 'expenses'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              ]"
            >
              Expenses
            </button>
            <button
              @click="activeTab = 'settlements'"
              :class="[
                'py-4 px-1 border-b-2 font-medium text-sm',
                activeTab === 'settlements'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              ]"
            >
              Settlements
            </button>
          </nav>
        </div>

        <!-- Expenses Tab -->
        <div v-if="activeTab === 'expenses'">
          <div v-if="loadingExpenses" class="text-center py-8">
            <p class="text-gray-600">Loading expenses...</p>
          </div>

          <div v-else-if="expenses.length === 0" class="text-center py-8 card">
            <p class="text-gray-600 mb-4">No expenses yet</p>
            <button @click="showAddExpenseModal = true" class="btn btn-primary">
              Add First Expense
            </button>
          </div>

          <div v-else class="space-y-4">
            <!-- Expense Cards -->
            <div
              v-for="expense in expenses"
              :key="expense.id"
              class="card flex justify-between items-start"
            >
              <div class="flex-1">
                <div class="flex items-start justify-between">
                  <div>
                    <h3 class="text-lg font-semibold text-gray-900">
                      {{ expense.description }}
                    </h3>
                    <p class="text-sm text-gray-600 mt-1">
                      Paid by <span class="font-medium">{{ expense.paidByName }}</span>
                    </p>
                    <p class="text-sm text-gray-500 mt-1">
                      Category: {{ expense.category || 'Uncategorized' }}
                    </p>
                    <p class="text-sm text-gray-500">
                      {{ expense.participantIds?.length || 0 }} participants
                    </p>
                  </div>
                  <div class="text-right">
                    <p class="text-2xl font-bold text-gray-900">
                      ₹{{ expense.amount.toFixed(2) }}
                    </p>
                    <button
                      @click="confirmDeleteExpense(expense.id)"
                      class="mt-2 text-sm text-red-600 hover:text-red-800"
                    >
                      Delete
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- Total -->
            <div class="card bg-blue-50 border border-blue-200">
              <div class="flex justify-between items-center">
                <span class="text-lg font-semibold text-gray-900">Total Expenses</span>
                <span class="text-2xl font-bold text-blue-600">
                  ₹{{ totalExpenses.toFixed(2) }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Settlements Tab -->
        <div v-if="activeTab === 'settlements'">
          <div class="mb-4">
            <button
              @click="loadSettlements"
              :disabled="loadingSettlements"
              class="btn btn-primary"
            >
              <span v-if="loadingSettlements">Calculating...</span>
              <span v-else>Calculate Settlements</span>
            </button>
          </div>

          <div v-if="settlements.length === 0" class="card text-center py-8">
            <p class="text-gray-600">No settlements to show</p>
            <p class="text-sm text-gray-500 mt-2">
              Click "Calculate Settlements" to see who owes whom
            </p>
          </div>

          <div v-else class="space-y-4">
            <div
              v-for="(settlement, index) in settlements"
              :key="index"
              class="card"
            >
              <div class="flex items-center justify-between">
                <div class="flex-1">
                  <p class="text-lg text-gray-900">
                    <span class="font-semibold">{{ settlement.fromUserName }}</span>
                    <span class="text-gray-600 mx-2">pays</span>
                    <span class="font-semibold">{{ settlement.toUserName }}</span>
                  </p>
                </div>
                <div class="text-right">
                  <p class="text-2xl font-bold text-green-600">
                    ₹{{ settlement.amount.toFixed(2) }}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>

    <!-- Add Expense Modal -->
    <div
      v-if="showAddExpenseModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      @click.self="showAddExpenseModal = false"
    >
      <div class="bg-white rounded-lg p-6 max-w-md w-full mx-4 max-h-[90vh] overflow-y-auto">
        <h3 class="text-lg font-semibold mb-4">Add New Expense</h3>
        
        <form @submit.prevent="handleCreateExpense">
          <div class="space-y-4">
            <!-- Description -->
            <div>
              <label class="block text-sm font-medium text-gray-700">Description *</label>
              <input
                v-model="newExpense.description"
                type="text"
                required
                class="input-field"
                :class="{ 'border-red-500': expenseErrors.description }"
                placeholder="Lunch, Hotel, etc."
                @blur="validateDescription"
              />
              <p v-if="expenseErrors.description" class="error-text">
                {{ expenseErrors.description }}
              </p>
            </div>

            <!-- Amount -->
            <div>
              <label class="block text-sm font-medium text-gray-700">Amount (₹) *</label>
              <input
                v-model.number="newExpense.amount"
                type="number"
                step="0.01"
                min="0.01"
                required
                class="input-field"
                :class="{ 'border-red-500': expenseErrors.amount }"
                placeholder="500.00"
                @blur="validateAmount"
              />
              <p v-if="expenseErrors.amount" class="error-text">
                {{ expenseErrors.amount }}
              </p>
            </div>

            <!-- Category -->
            <div>
              <label class="block text-sm font-medium text-gray-700">Category</label>
              <select v-model="newExpense.category" class="input-field">
                <option value="">Select category</option>
                <option value="Food">Food</option>
                <option value="Transport">Transport</option>
                <option value="Accommodation">Accommodation</option>
                <option value="Entertainment">Entertainment</option>
                <option value="Other">Other</option>
              </select>
            </div>

            <!-- Paid By -->
            <div>
              <label class="block text-sm font-medium text-gray-700">Paid By *</label>
              <select
                v-model.number="newExpense.paidById"
                required
                class="input-field"
              >
                <option value="">Select member</option>
                <option
                  v-for="memberId in group?.memberIds"
                  :key="memberId"
                  :value="memberId"
                >
                  {{ getUserName(memberId) }}
                </option>
              </select>
            </div>

            <!-- Participants (Multi-select) -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Split Among (Select participants)
              </label>
              <div class="space-y-2 max-h-40 overflow-y-auto border border-gray-300 rounded-lg p-3">
                <label
                  v-for="memberId in group?.memberIds"
                  :key="memberId"
                  class="flex items-center space-x-2"
                >
                  <input
                    type="checkbox"
                    :value="memberId"
                    v-model="newExpense.participantIds"
                    class="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                  />
                  <span class="text-sm text-gray-700">{{ getUserName(memberId) }}</span>
                </label>
              </div>
              <p class="text-xs text-gray-500 mt-1">
                Leave empty to split among all group members
              </p>
            </div>
          </div>

          <!-- Error Message -->
          <div v-if="expenseError" class="mt-4 bg-red-50 border border-red-400 text-red-700 px-4 py-3 rounded">
            {{ expenseError }}
          </div>

          <!-- Buttons -->
          <div class="mt-6 flex justify-end space-x-3">
            <button
              type="button"
              @click="showAddExpenseModal = false"
              class="btn btn-secondary"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="!isExpenseFormValid || loadingExpenses"
              class="btn btn-primary"
            >
              <span v-if="loadingExpenses">Adding...</span>
              <span v-else>Add Expense</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuth } from '@/composables/useAuth'
import { useExpenses } from '@/composables/useExpenses'
import { groupAPI, userAPI } from '@/services/api'

const route = useRoute()
const router = useRouter()
const { user, logout } = useAuth()
const {
  expenses,
  settlements,
  loading: loadingExpenses,
  error: expenseError,
  fetchGroupExpenses,
  createExpense,
  deleteExpense,
  calculateSettlements
} = useExpenses()

const group = ref(null)
const activeTab = ref('expenses')
const showAddExpenseModal = ref(false)
const loadingSettlements = ref(false)
const allUsers = ref([])

const newExpense = ref({
  description: '',
  amount: 0,
  category: '',
  paidById: null,
  participantIds: []
})

const expenseErrors = ref({})

const totalExpenses = computed(() => {
  return expenses.value.reduce((sum, expense) => sum + expense.amount, 0)
})

const isExpenseFormValid = computed(() => {
  return newExpense.value.description.length >= 3 &&
         newExpense.value.amount > 0 &&
         newExpense.value.paidById &&
         Object.keys(expenseErrors.value).length === 0
})

const validateDescription = () => {
  if (!newExpense.value.description) {
    expenseErrors.value.description = 'Description is required'
  } else if (newExpense.value.description.length < 3) {
    expenseErrors.value.description = 'Description must be at least 3 characters'
  } else {
    delete expenseErrors.value.description
  }
}

const validateAmount = () => {
  if (!newExpense.value.amount || newExpense.value.amount <= 0) {
    expenseErrors.value.amount = 'Amount must be greater than 0'
  } else {
    delete expenseErrors.value.amount
  }
}

const fetchGroupDetails = async () => {
  try {
    const response = await groupAPI.getById(route.params.id)
    group.value = response.data
  } catch (error) {
    console.error('Failed to fetch group:', error)
  }
}

const fetchAllUsers = async () => {
  try {
    const response = await userAPI.getAll()
    allUsers.value = response.data
  } catch (error) {
    console.error('Failed to fetch users:', error)
  }
}

const getUserName = (userId) => {
  const foundUser = allUsers.value.find(u => u.id === userId)
  return foundUser ? foundUser.name : `User ${userId}`
}

const handleCreateExpense = async () => {
  validateDescription()
  validateAmount()

  if (!isExpenseFormValid.value) return

  try {
    await createExpense({
      description: newExpense.value.description,
      amount: newExpense.value.amount,
      category: newExpense.value.category,
      paidById: newExpense.value.paidById,
      groupId: parseInt(route.params.id),
      participantIds: newExpense.value.participantIds.length > 0 
        ? newExpense.value.participantIds 
        : null
    })

    // Reset form
    newExpense.value = {
      description: '',
      amount: 0,
      category: '',
      paidById: null,
      participantIds: []
    }
    expenseErrors.value = {}
    showAddExpenseModal.value = false

    // Refresh settlements if on that tab
    if (activeTab.value === 'settlements' && settlements.value.length > 0) {
      await loadSettlements()
    }
  } catch (error) {
    console.error('Failed to create expense:', error)
  }
}

const confirmDeleteExpense = async (expenseId) => {
  if (confirm('Are you sure you want to delete this expense?')) {
    try {
      await deleteExpense(expenseId)
      
      // Refresh settlements if on that tab
      if (activeTab.value === 'settlements' && settlements.value.length > 0) {
        await loadSettlements()
      }
    } catch (error) {
      console.error('Failed to delete expense:', error)
    }
  }
}

const loadSettlements = async () => {
  loadingSettlements.value = true
  try {
    await calculateSettlements(route.params.id)
  } catch (error) {
    console.error('Failed to calculate settlements:', error)
  } finally {
    loadingSettlements.value = false
  }
}

const goBack = () => {
  router.push('/dashboard')
}

onMounted(async () => {
  await fetchGroupDetails()
  await fetchAllUsers()
  await fetchGroupExpenses(route.params.id)
})


</script>
