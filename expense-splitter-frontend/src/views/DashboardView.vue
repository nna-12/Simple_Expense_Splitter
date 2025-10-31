<template>
  <div class="min-h-screen bg-gray-100">
    <!-- Navbar -->
    <nav class="bg-white shadow-lg">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center">
            <h1 class="text-xl font-bold text-gray-900">Expense Splitter</h1>
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
        <h2 class="text-2xl font-bold text-gray-900 mb-6">My Groups</h2>
        
        <!-- Loading State -->
        <div v-if="loading" class="text-center py-8">
          <p class="text-gray-600">Loading groups...</p>
        </div>

        <!-- Empty State -->
        <div v-else-if="groups.length === 0" class="text-center py-8">
          <p class="text-gray-600 mb-4">You don't have any groups yet</p>
          <button @click="showCreateModal = true" class="btn btn-primary">
            Create Your First Group
          </button>
        </div>

        <!-- Groups Grid -->
        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            v-for="group in groups"
            :key="group.id"
            @click="goToGroup(group.id)"
            class="card hover:shadow-lg cursor-pointer transition-shadow"
          >
            <h3 class="text-lg font-semibold text-gray-900 mb-2">
              {{ group.name }}
            </h3>
            <p class="text-gray-600 text-sm mb-4">
              {{ group.description || 'No description' }}
            </p>
            <div class="flex items-center justify-between">
              <span class="text-sm text-gray-500">
                {{ group.memberIds?.length || 0 }} members
              </span>
            </div>
          </div>
        </div>

        <!-- Create Button -->
        <div v-if="groups.length > 0" class="mt-6">
          <button @click="showCreateModal = true" class="btn btn-primary">
            + Create New Group
          </button>
        </div>
      </div>
    </div>

    <!-- Simple Create Group Modal -->
    <div v-if="showCreateModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 max-w-md w-full mx-4">
        <h3 class="text-lg font-semibold mb-4">Create New Group</h3>
        <form @submit.prevent="handleCreateGroup">
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700">Group Name</label>
              <input
                v-model="newGroup.name"
                type="text"
                required
                class="input-field"
                placeholder="Weekend Trip"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Description</label>
              <textarea
                v-model="newGroup.description"
                class="input-field"
                rows="3"
                placeholder="Friends weekend getaway"
              ></textarea>
            </div>
          </div>
          <div class="mt-6 flex justify-end space-x-3">
            <button type="button" @click="showCreateModal = false" class="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" class="btn btn-primary">
              Create Group
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useAuth'
import { groupAPI } from '@/services/api'

const router = useRouter()
const { user, logout } = useAuth()

const groups = ref([])
const loading = ref(false)
const showCreateModal = ref(false)
const newGroup = ref({
  name: '',
  description: ''
})

const fetchGroups = async () => {
  loading.value = true
  try {
    const response = await groupAPI.getByUserId(user.value.userId)
    groups.value = response.data
  } catch (error) {
    console.error('Failed to fetch groups:', error)
  } finally {
    loading.value = false
  }
}

const handleCreateGroup = async () => {
  try {
    await groupAPI.create({
      name: newGroup.value.name,
      description: newGroup.value.description,
      createdById: user.value.userId,
      memberIds: [user.value.userId]
    })
    showCreateModal.value = false
    newGroup.value = { name: '', description: '' }
    await fetchGroups()
  } catch (error) {
    console.error('Failed to create group:', error)
  }
}

const goToGroup = (groupId) => {
  router.push(`/groups/${groupId}`)
}

onMounted(() => {
  fetchGroups()
})
</script>
