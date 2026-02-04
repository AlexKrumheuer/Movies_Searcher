<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../../services/api'
import { useToast } from 'vue-toastification'

const route = useRoute()
const router = useRouter()
const toast = useToast()

const status = ref('loading')

onMounted(async () => {
    const token = route.query.token

    if (!token) {
        status.value = 'error'
        toast.error("Token not found.")
        return
    }

    try {
        await api.get(`/auth/confirm-email?token=${token}`)
        
        status.value = 'success'
        toast.success("E-mail was successfully updated! Redirecting to login...")
        
        setTimeout(() => {
            router.push('/login')
        }, 3500)
        
    } catch (error) {
        status.value = 'error'
        const msg = error.response?.data || "Invalid link or expired token."
        toast.error(msg)
    }
});
</script>

<template>
    <div class="confirm-container">
        <div v-if="status === 'loading'" class="card">
            <fa icon="spinner" spin size="3x" />
            <h2>Verifying your new e-mail...</h2>
        </div>

        <div v-if="status === 'success'" class="card success">
            <fa icon="check-circle" size="3x" />
            <h2>Everything is set!</h2>
            <p>Your e-mail was updated! Redirecting to login...</p>
        </div>

        <div v-if="status === 'error'" class="card error">
            <fa icon="times-circle" size="3x" />
            <h2>Ops! Something went wrong.</h2>
            <p>The link may have expired or already been used.</p>
            <router-link to="/perfil" class="btn">Back to profile</router-link>
        </div>
    </div>
</template>

<style scoped>
.confirm-container {
    height: 80vh;
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    background: #1a1a1a;
}
.card { padding: 2rem; background: #1a1a1a; border-radius: 8px; color: white; }
.success { color: #4BB543; }
.error { color: #ff3333; }
.btn { margin-top: 1rem; display: inline-block; color: #39A0FF; text-decoration: none; }
</style>