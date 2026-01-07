<script setup>
import { onMounted, ref, computed } from 'vue';
import { tbmdService } from '../services/tmbd';

const props = defineProps({
    infoEspecifico: Object
})
let detalhes = ref(null);
onMounted(() => {
    const carregarDetalhes = async () => {
        detalhes.value = await tbmdService.getDetails(props.infoEspecifico.tipo, props.infoEspecifico.id); 
    }
    carregarDetalhes();
})

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
                        <p><span v-if="detalhes.created_by">Criado por: </span><span v-else>Dirigido por: </span>{{  textoDiretores  }}</p>
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
                                <div v-if="detalhes.credits.cast.length > 30" @click="mostrarMais = !mostrarMais" class="people">
                                    <p v-if="mostrarMais">Mostrar menos</p>
                                    <p v-else>Mostrar mais</p>
                                </div>
                            </div>
                            <div class="grid-info-select" v-else-if="infoSelected === 'crew'">
                                <div class="people" v-for="member in listas()" :key="member.id">
                                    <p>{{ member.name }} - {{ member.job }}</p>
                                </div>
                                <div  v-if="detalhes.credits.crew.length > 30" @click="mostrarMais = !mostrarMais" class="people">
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
                                <p v-if="detalhes.first_air_date">Data de estreia do primeiro episódio: {{ new Date(detalhes.first_air_date).toLocaleDateString('pt-BR') }}</p>
                                <div class="language-spoken" v-if="detalhes.last_episode_to_air">
                                    Último episódio a ser exibido:
                                    <div class="people">
                                        Data de exibição: {{ new Date(detalhes.last_episode_to_air.air_date).toLocaleDateString('pt-BR') }}
                                    </div>
                                    <div class="people">
                                        Número do Episódio: {{ detalhes.last_episode_to_air.episode_number }}
                                    </div>
                                    <div class="people">
                                        Nome do Episódio: {{ detalhes.last_episode_to_air.name }}
                                    </div>
                                </div>
                                <p v-if="detalhes.in_production">Em produção? {{ detalhes.in_production ? 'Sim' : 'Não' }}</p>
                                <div class="language-spoken">
                                    Idiomas Falados: 
                                    <div class="people" v-for="language in detalhes.spoken_languages" :key="language.id">
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
                    <div>
                        <h2>Avaliações</h2>
                        <p>TMDB: {{ detalhes.vote_average }} / 10 ({{ detalhes.vote_count }} votos)</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<style>
.banner-wrapper {
    position: relative;
    width: 100vw;
    height: 85vh;
    display: flex;
    justify-content: center;
    background-color: #0A0A10;
    padding: 2rem;
}
.background-image {
    mask-image: radial-gradient(ellipse at center, black 40%, transparent 100%);
    width: 60%;
    height: 80%;
    object-fit: cover;
    opacity: 0.9;
}

.fade-bottom {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(to bottom, transparent 40%, #0A0A10 100%);
}

.specific-container {
    margin-top: -150px;
    position: relative;
    z-index: 2;
    background: transparent;
    width: 100%;
    padding: 0 10rem;
    background-color: #0A0A10;
    padding-top: 5rem;
    color: #fff;
}

.card-content {
    display: grid;
    grid-template-columns: 1fr 4fr 1fr;
    gap: 5rem;
}

.card-info-title {
    display: flex;
    gap: 2rem;
    align-items: center;
}

.info-options {
    display: flex;
    gap: 2rem;
    margin-bottom: 1rem;
    cursor: pointer;
}

.info-selection {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.grid-info-select {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr;
    gap: 1rem;
}

.language-spoken {
    display: flex;
    gap: 0.5rem;
    align-items: center;
    flex-wrap: wrap;
}

.people {
    background-color: #283038;
    color: #9ab;
    border-radius: 8px;
    padding: 0.5rem;
    width: fit-content;
    cursor: pointer;
}

.people:hover {
    color: #fff;
}

.middle-content {
    font-size: 1.3rem;
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.grid-genre-select {
    display: grid;
    grid-template-columns: 1fr 1fr;
    font-size: 1.5rem;
    width: 50%;
    gap: 1rem;
}

</style>