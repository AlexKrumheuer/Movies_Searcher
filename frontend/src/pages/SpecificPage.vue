<script setup>
import { onMounted, ref, computed } from 'vue';
import { useRoute } from 'vue-router'
import { tbmdService } from '../services/tmbd';
import '../style/specificPage.css'
import { useMediaList } from '../util/useMediaList';
import { useRating } from '../util/useRating';
const route = useRoute()

const type = route.params.type
const id = route.params.id

const favoritesManager = useMediaList('/api/favorite', id);
const watchedManager = useMediaList('/api/watched', id);
const watchlistManager = useMediaList('/api/watchlist', id);
const ratingManager = useRating(id, type)

let detalhes = ref(null);

onMounted(async () => {
    if (type && id) {
        detalhes.value = await tbmdService.getDetails(type, id);

        await Promise.all([
            favoritesManager.checkStatus(),
            watchedManager.checkStatus(),
            watchlistManager.checkStatus(),
            ratingManager.getRating()
        ]);
    }
});

const handleToggle = (manager) => {
    if (!detalhes.value) return;

    const mediaData = {
        tmdbId: detalhes.value.id,
        videoType: type,
        posterPath: detalhes.value.poster_path,
        title: detalhes.value.title || detalhes.value.name
    };

    manager.toggle(mediaData);
};

let infoSelected = ref("cast");
let mostrarMais = ref(false)

const textoDiretores = computed(() => {
    // se não tiver carregado os detalhes, retorna string vazia
    if (!detalhes.value) return '';

    // filtra pelos membros que tem o `job` de director e junta os nomes numa string colocando virgula
    return detalhes.value.credits.crew
        .filter(member => member.job === 'Director')
        .map(director => director.name)
        .join(', ') || detalhes.value.created_by.map(creator => creator.name).join(', ');
});
function listas() {
    if (mostrarMais.value && infoSelected.value === 'cast') return detalhes.value.credits.cast;
    if (mostrarMais.value && infoSelected.value === 'crew') return detalhes.value.credits.crew;
    let lista = ref([])
    if (infoSelected.value === 'cast') {
        lista.value = detalhes.value.credits.cast.slice(0, detalhes.value.credits.cast.length > 30 ? 30 : detalhes.value.credits.cast.length);
    } else if (infoSelected.value === 'crew') {
        lista.value = detalhes.value.credits.crew.slice(0, detalhes.value.credits.crew.length > 30 ? 30 : detalhes.value.credits.crew.length);
    }
    return lista.value;
}
function selectInfo(type) {
    mostrarMais.value = false
    infoSelected.value = type;
}

const rating = (n) => {
    if(!detalhes.value) return
    console.log(n)
    ratingManager.setRating(n);
}


</script>
<template>
    <div class="card-info" v-if="detalhes">
        <div class="banner-wrapper">
            <img class="background-image" :src="`https://image.tmdb.org/t/p/w1280/${detalhes.backdrop_path}`"
                :alt="detalhes.title ? detalhes.title : detalhes.name">
            <div class="fade-bottom"></div>
        </div>

        <div class="specific-container">
            <div class="card-content">
                <div>
                    <img :src="`https://image.tmdb.org/t/p/w342/${detalhes.poster_path}`"
                        :alt="detalhes.title ? detalhes.title : detalhes.name" />
                </div>
                <div class="middle-content">
                    <div class="card-info-title">
                        <h1>{{ detalhes.title ? detalhes.title : detalhes.name }}</h1>
                        <p>{{ detalhes.release_date?.split('-')[0] }}</p>
                        <p><span v-if="detalhes.created_by">Criado por: </span><span v-else>Dirigido por: </span>{{
                            textoDiretores }}</p>
                        <p v-if="detalhes.status">Status: {{ detalhes.status }}</p>
                    </div>
                    <p v-if="detalhes.tagline">{{ detalhes.tagline }}</p>
                    <p v-if="detalhes.overview">{{ detalhes.overview }}</p>
                    <div class="info-selection">
                        <div class="info-options">
                            <p :style="infoSelected === 'cast' ? { color: '#39A0FF', textDecoration: 'underline' } : {}"
                                @click="selectInfo('cast')">ELENCO
                            </p>
                            <p :style="infoSelected === 'crew' ? { color: '#39A0FF', textDecoration: 'underline' } : {}"
                                @click="selectInfo('crew')">EQUIPE
                            </p>
                            <p :style="infoSelected === 'details' ? { color: '#39A0FF', textDecoration: 'underline' } : {}"
                                @click="selectInfo('details')">DETALHES
                            </p>
                            <p :style="infoSelected === 'genres' ? { color: '#39A0FF', textDecoration: 'underline' } : {}"
                                @click="selectInfo('genres')">GÊNEROS
                            </p>
                            <p></p>
                        </div>
                        <div>
                            <div class="grid-info-select" v-if="infoSelected === 'cast'">
                                <div class="people" v-for="actor in listas()" :key="actor.id">
                                    <p>{{ actor.name }} como {{ actor.character }}</p>
                                </div>
                                <div v-if="detalhes.credits.cast.length > 30" @click="mostrarMais = !mostrarMais"
                                    class="people">
                                    <p v-if="mostrarMais">Mostrar menos</p>
                                    <p v-else>Mostrar mais</p>
                                </div>
                            </div>
                            <div class="grid-info-select" v-else-if="infoSelected === 'crew'">
                                <div class="people" v-for="member in listas()" :key="member.id">
                                    <p>{{ member.name }} - {{ member.job }}</p>
                                </div>
                                <div v-if="detalhes.credits.crew.length > 30" @click="mostrarMais = !mostrarMais"
                                    class="people">
                                    <p v-if="mostrarMais">Mostrar menos</p>
                                    <p v-else>Mostrar mais</p>
                                </div>
                            </div>
                            <div class="detalhes" v-else-if="infoSelected === 'details'">
                                <p v-if="detalhes.runtime">Duração: {{ detalhes.runtime }} minutos</p>
                                <p>Idioma Original: {{ detalhes.original_language }}</p>
                                <p></p>
                                <p v-if="detalhes.number_of_seasons">{{ detalhes.number_of_seasons }} temporadas</p>
                                <p v-if="detalhes.number_of_episodes">{{ detalhes.number_of_episodes }} episódios</p>
                                <p v-if="detalhes.first_air_date">Data de estreia do primeiro episódio: {{ new
                                    Date(detalhes.first_air_date).toLocaleDateString('pt-BR') }}</p>
                                <div class="language-spoken" v-if="detalhes.last_episode_to_air">
                                    Último episódio a ser exibido:
                                    <div class="people">
                                        Data de exibição: {{ new
                                            Date(detalhes.last_episode_to_air.air_date).toLocaleDateString('pt-BR') }}
                                    </div>
                                    <div class="people">
                                        Número do Episódio: {{ detalhes.last_episode_to_air.episode_number }}
                                    </div>
                                    <div class="people">
                                        Nome do Episódio: {{ detalhes.last_episode_to_air.name }}
                                    </div>
                                </div>
                                <p v-if="detalhes.in_production">Em produção? {{ detalhes.in_production ? 'Sim' : 'Não'
                                    }}</p>
                                <div class="language-spoken">
                                    Idiomas Falados:
                                    <div class="people" v-for="language in detalhes.spoken_languages"
                                        :key="language.id">
                                        <p>{{ language.english_name }}</p>
                                    </div>
                                </div>
                            </div>
                            <div class="grid-genre-select" v-else="infoSelected === 'genres'">
                                <div class="people" v-for="genre in detalhes.genres" :key="genre.id">
                                    <p>{{ genre.name }}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div>
                    <div class="rating-container">
                        <h2>Avaliações</h2>
                        <p>TMDB: {{ detalhes.vote_average }} / 10 ({{ detalhes.vote_count }} votos)</p>
                        <div class="favorite" @click="handleToggle(favoritesManager)">
                            <fa 
                                class="favorite-icon" 
                                icon="heart"
                                :style="{ color: favoritesManager.isAdded.value ? 'red' : 'inherit' }"
                            />
                            <p>{{ favoritesManager.isAdded.value ? "Remove Favorite" : "Add Favorite" }}</p>
                        </div>
                        <div class="favorite" @click="handleToggle(watchedManager)">
                            <fa 
                                class="favorite-icon" 
                                icon="eye"
                                :style="{ color: watchedManager.isAdded.value ? '#39A0FF' : 'inherit' }"
                                />
                            <p>{{ watchedManager.isAdded.value ? "Unwatch" : "Watched" }}</p>
                        </div>
                        <div class="favorite" @click="handleToggle(watchlistManager)"> 
                            <fa 
                                class="favorite-icon" 
                                icon="list"
                                :style="{ color: watchlistManager.isAdded.value ? '#39A0FF' : 'inherit' }"
                            />
                            <p>{{ watchlistManager.isAdded.value ? "Remove from Watchlist" : "Watchlist" }}</p>
                        </div>
                        <div class="rating">
                            <p>Rating</p>
                            <div class="rating-value">
                                <div>
                                        <fa
                                class="rating-star"
                                icon="star"
                                :key="n"
                                v-for="n in 5"
                                @click="rating(n)"
                                :style="{ color: ratingManager.currentRating.value >= n ? '#FFD700' : 'inherit'}"
                              />
                                </div>
                                
                              <p>{{ ratingManager.currentRating.value }}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
