<script setup>
import { onMounted, ref } from 'vue'
import api from '../../../services/api';
import { useRouter } from 'vue-router';

const favoriteList = ref([]);
const loading = ref(true);
const router = useRouter();

onMounted(async () => {
    try {
        const response = await api.get("/api/favorite");
        favoriteList.value = response.data;
    } catch (error) {
        console.error("Erro ao buscar favoritos:", error);
    } finally {
        loading.value = false;
    }
});

const goToDetails = (tmdbId, videoType) => {
    router.push(`/details/${videoType}/${tmdbId}`);
}
</script>

<template>
    <div class="favorites-container">
        <div v-if="loading" class="loading-msg">
            <p>Loading your favorites...</p>
        </div>

        <div v-else-if="favoriteList.length === 0" class="empty-msg">
            <p>You dont have favorites...</p>
        </div>

        <div v-else class="favorites-grid">
            <div 
                v-for="fav in favoriteList" 
                :key="fav.id" 
                class="card-wrapper"
                @click="goToDetails(fav.tmdbId, fav.videoType)"
            >
                <img 
                    class="card-img" 
                    :src="`https://image.tmdb.org/t/p/w342/${fav.posterPath}`" 
                    :alt="fav.title"
                >
                <div class="card-overlay">
                    <p class="card-title">{{ fav.title }}</p>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.favorites-grid {
    display: grid;
    width: 100vw;
    grid-template-columns: 1fr 1fr 1fr 1fr 1fr;
    gap: 5rem;
    padding: 0 10rem;
}

.card-wrapper {
    width: 200px;
    cursor: pointer;
    transition: transform 0.2s;
    position: relative;
    border-radius: 8px;
    overflow: hidden;
}

.card-wrapper:hover {
    transform: scale(1.05);
}

.card-img {
    width: 100%;
    height: auto;
    border-radius: 8px;
    display: block;
}

.loading-msg, .empty-msg {
    color: white;
    text-align: center;
    padding: 50px;
    font-size: 1.2rem;
}

.card-title {
    color: #fff;
}
</style>