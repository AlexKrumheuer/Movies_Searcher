<script setup>
import { ref, onMounted, computed } from 'vue'
import api from '../../../services/api'

const favorites = ref([])
const memberSince = ref(new Date().getFullYear())
const loading = ref(true)

const movieCount = computed(() => {
    return favorites.value.filter(fav => fav.videoType === 'movie').length
});

const showCount = computed(() => {
    return favorites.value.filter(fav => fav.videoType === 'tv').length;
});

onMounted(async () => {
    try {
        loading.value = true;
                const favResponse = await api.get('/api/favorite');
        favorites.value = favResponse.data
    } catch (error) {
        console.error("Error loading statistics", error)
    } finally {
        loading.value = false
    }
});
</script>
<template>
    <div class="perfil-tracking">
        <div class="track">
            <fa icon="clapperboard" />
            {{ movieCount }} Favorited movies
        </div>
        <div class="track">
            <fa icon="video" />
            {{ showCount }} Favorited shows
        </div>
        <div class="track">
            <fa icon="user-group" />
            Member since: {{ memberSince }}
        </div>
    </div>
</template>