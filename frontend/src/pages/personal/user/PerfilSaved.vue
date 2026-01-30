<script setup>
import { onMounted, ref } from 'vue'
import api from '../../../services/api';
import { useRouter } from 'vue-router';
import '../../../style/user/perfilsaved.css'

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

