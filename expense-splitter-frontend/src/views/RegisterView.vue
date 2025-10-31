<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
          Create your account
        </h2>
        <p class="mt-2 text-center text-sm text-gray-600">
          Or
          <router-link to="/login" class="font-medium text-blue-600 hover:text-blue-500">
            sign in to existing account
          </router-link>
        </p>
      </div>

      <form @submit.prevent="handleRegister" class="mt-8 space-y-6">
        <!-- Error Message -->
        <div v-if="error" class="bg-red-50 border border-red-400 text-red-700 px-4 py-3 rounded">
          {{ error }}
        </div>

        <div class="rounded-md shadow-sm space-y-4">
          <!-- Name Field -->
          <div>
            <label for="name" class="block text-sm font-medium text-gray-700">Full Name</label>
            <input
              id="name"
              v-model="formData.name"
              type="text"
              required
              class="input-field"
              :class="{ 'border-red-500': errors.name }"
              placeholder="John Doe"
              @blur="validateName"
            />
            <p v-if="errors.name" class="error-text">{{ errors.name }}</p>
          </div>

          <!-- Email Field -->
          <div>
            <label for="email" class="block text-sm font-medium text-gray-700">Email address</label>
            <input
              id="email"
              v-model="formData.email"
              type="email"
              required
              class="input-field"
              :class="{ 'border-red-500': errors.email }"
              placeholder="john@example.com"
              @blur="validateEmail"
            />
            <p v-if="errors.email" class="error-text">{{ errors.email }}</p>
          </div>

          <!-- Phone Field -->
          <div>
            <label for="phone" class="block text-sm font-medium text-gray-700">Phone (Optional)</label>
            <input
              id="phone"
              v-model="formData.phone"
              type="tel"
              class="input-field"
              placeholder="9876543210"
            />
          </div>

          <!-- Password Field -->
          <div>
            <label for="password" class="block text-sm font-medium text-gray-700">Password</label>
            <input
              id="password"
              v-model="formData.password"
              type="password"
              required
              class="input-field"
              :class="{ 'border-red-500': errors.password }"
              placeholder="••••••••"
              @blur="validatePassword"
            />
            <p v-if="errors.password" class="error-text">{{ errors.password }}</p>
          </div>
        </div>

        <!-- Submit Button -->
        <div>
          <button
            type="submit"
            :disabled="!isFormValid || loading"
            class="w-full btn btn-primary"
          >
            <span v-if="loading">Creating account...</span>
            <span v-else>Create account</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useAuth } from '@/composables/useAuth'

const { register, loading, error } = useAuth()

const formData = ref({
  name: '',
  email: '',
  phone: '',
  password: ''
})

const errors = ref({})

const validateName = () => {
  if (!formData.value.name) {
    errors.value.name = 'Name is required'
  } else if (formData.value.name.length < 2) {
    errors.value.name = 'Name must be at least 2 characters'
  } else {
    delete errors.value.name
  }
}

const validateEmail = () => {
  if (!formData.value.email) {
    errors.value.email = 'Email is required'
  } else if (!/\S+@\S+\.\S+/.test(formData.value.email)) {
    errors.value.email = 'Email must be valid'
  } else {
    delete errors.value.email
  }
}

const validatePassword = () => {
  if (!formData.value.password) {
    errors.value.password = 'Password is required'
  } else if (formData.value.password.length < 6) {
    errors.value.password = 'Password must be at least 6 characters'
  } else {
    delete errors.value.password
  }
}

const isFormValid = computed(() => {
  return formData.value.name &&
         formData.value.email &&
         formData.value.password &&
         Object.keys(errors.value).length === 0
})

const handleRegister = async () => {
  validateName()
  validateEmail()
  validatePassword()

  if (!isFormValid.value) return

  try {
    await register(formData.value)
  } catch (err) {
    console.error('Registration failed:', err)
  }
}
</script>
