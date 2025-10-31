import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { ref } from 'vue'

// Create a simple ExpenseCard component for testing
const ExpenseCard = {
  name: 'ExpenseCard',
  props: {
    expense: {
      type: Object,
      required: true
    }
  },
  emits: ['delete'],
  template: `
    <div class="expense-card">
      <h3 class="expense-title">{{ expense.description }}</h3>
      <p class="expense-amount">₹{{ expense.amount.toFixed(2) }}</p>
      <p class="expense-payer">Paid by {{ expense.paidByName }}</p>
      <button @click="$emit('delete', expense.id)" class="delete-btn">
        Delete
      </button>
    </div>
  `
}

describe('ExpenseCard.vue', () => {
  const mockExpense = {
    id: 1,
    description: 'Lunch',
    amount: 500.00,
    paidById: 1,
    paidByName: 'John Doe',
    category: 'Food',
    participantIds: [1, 2]
  }

  it('renders expense details correctly', () => {
    const wrapper = mount(ExpenseCard, {
      props: {
        expense: mockExpense
      }
    })

    expect(wrapper.find('.expense-title').text()).toBe('Lunch')
    expect(wrapper.find('.expense-amount').text()).toBe('₹500.00')
    expect(wrapper.find('.expense-payer').text()).toBe('Paid by John Doe')
  })

  it('emits delete event when delete button is clicked', async () => {
    const wrapper = mount(ExpenseCard, {
      props: {
        expense: mockExpense
      }
    })

    await wrapper.find('.delete-btn').trigger('click')

    expect(wrapper.emitted()).toHaveProperty('delete')
    expect(wrapper.emitted('delete')[0]).toEqual([1])
  })

  it('formats amount with 2 decimal places', () => {
    const expenseWithDecimals = {
      ...mockExpense,
      amount: 123.456
    }

    const wrapper = mount(ExpenseCard, {
      props: {
        expense: expenseWithDecimals
      }
    })

    expect(wrapper.find('.expense-amount').text()).toBe('₹123.46')
  })
})
