<script setup>
import { onMounted, ref } from 'vue'
import api from '../../../services/api';

const userData = ref({})

onMounted(async () => {
    try {
        const response = await api.get("/auth/me")
        userData.value = response.data
    } catch(error) {
        console.error("Error when getting user info")
    }
})

</script>
<template>
    <div class="perfil-info">
            <img class="perfil-image" src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQpj2elRrHLpPcNjxMfFdLA9zSoxiCbivEvHQ&s"
                alt="">
            <h2>{{ userData.username }}</h2>
        </div>
</template>