<script setup>
import { onMounted, ref } from 'vue'
import api from '../../../services/api';
import { useRouter } from 'vue-router';

const props = defineProps({
    display: String
})

const list = ref([]);
const loading = ref(true);
const router = useRouter();

onMounted(async () => {
    try {
        const [listResponse, ratingResponse] = await Promise.all([
            api.get(`/api/${props.display}`), 
            api.get('/api/rating/user')    
        ])

        const rawList = listResponse.data
        const userRatings = ratingResponse.data

        list.value = rawList.map(item => {
            const foundRating = userRatings.find(r =>
                r.tmdbId === item.tmdbId && r.videoType === item.videoType
            );

            return {
                ...item,
                userRating: foundRating ? foundRating.rating : null
            };
        })
    } catch (error) {
        console.error("Error when searching for media:", error)
    } finally {
        loading.value = false
    }
})

const goToDetails = (tmdbId, videoType) => {
    router.push(`/details/${videoType}/${tmdbId}`);
}
</script>

<template>
    <div class="favorites-container">
        <div v-if="loading" class="loading-msg">
            <p>Loading your <span>{{ props.display == 'favorite' ? 'favorites' : props.display == 'watched media' ?
                'watched' : 'watch list' }}</span>...</p>
        </div>

        <div v-else-if="list.length === 0" class="empty-msg">
            <p>You dont have {{ props.display == 'favorite' ? 'favorites' : props.display == 'watched media' ? 'watched'
                : 'watch list' }}...</p>
        </div>

        <div v-else class="favorites-grid">
            <div v-for="fav in list" :key="fav.id" class="card-wrapper" @click="goToDetails(fav.tmdbId, fav.videoType)">
                <img class="card-img" :src="`https://image.tmdb.org/t/p/w342/${fav.posterPath}`" :alt="fav.title">
                <div class="card-overlay">
                    <p class="card-title">{{ fav.title }}</p>

                    <div class="rating-row" v-if="fav.userRating">
                        <fa v-for="n in 5" :key="n" icon="star" class="star-icon"
                            :class="{ 'active': fav.userRating >= n }" />
                        <span class="rating-value">{{ fav.userRating }}/5</span>
                    </div>

                    <div class="rating-row" v-else>
                        <span class="no-rating">No rating</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.favorites-container {
    padding: 3rem 0;
    min-height: 60vh;
}

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

.loading-msg,
.empty-msg {
    color: white;
    text-align: center;
    padding: 50px;
    font-size: 1.2rem;
}

.card-title {
    color: #fff;
    font-size: 1.7rem;
    flex-wrap: wrap;
}

.card-overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    background: linear-gradient(to top, rgba(0,0,0,0.9) 0%, rgba(0,0,0,0.6) 60%, transparent 100%);
    padding: 20px 10px 10px;
    display: flex;
    flex-direction: column;
    gap: 5px;
}

.card-title {
    color: #fff;
    font-size: 1.3rem; 
    font-weight: bold;
    margin: 0;
    white-space: nowrap; 
    overflow: hidden;
    text-overflow: ellipsis;
}

.rating-row {
    display: flex;
    align-items: center;
    gap: 2px;
}

.star-icon {
    font-size: 0.9rem;
    color: #555; 
}

.star-icon.active {
    color: #FFD700;
    filter: drop-shadow(0 0 2px rgba(0,0,0,0.5));
}

.rating-value {
    font-size: 1.3rem;
    color: #ddd;
    margin-left: 6px;
    font-weight: 300;
}

.no-rating {
    font-size: 1.3rem;
    color: #aaa;
    font-style: italic;
}
</style>