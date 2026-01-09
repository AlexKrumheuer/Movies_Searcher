<script setup>
    // Content of home component
    // It searches in the API for the movies and tv shows most watched. Then it splices the list
    // with the 10 most viewed of each.


    import { defineProps, onMounted, defineEmits} from 'vue';
    import { useMidia } from '../util/useMidia';
    import Card from './Card.vue';
    const props = defineProps({
        sectionInfo: Object,
    })

    const emit = defineEmits(['conteudo-clicado-home']);

    const {itens, loading, buscarMidia} = useMidia()

    const carregar = () => {
        buscarMidia(props.sectionInfo.tipo, 1, "", "popular");
    }

    onMounted(() => {
        carregar();  
    })
</script>
<template>
    <div class="feed">
        <div class="title">
            <h1>{{props.sectionInfo.title}}</h1>
            <router-link 
                :to="props.sectionInfo.tipo === 'movie' ? '/movie' : '/shows'" 
                class="button-ver-tudo"
            >
            <button>
                Ver tudo
            </button>
            </router-link>
        </div>
        <div class="grid">
            <p v-if="loading">Carregando...</p>
            <router-link
                v-for="item in itens.slice(0,10)" 
                :key="item.id" 
                :to="{ name: 'details', params: { type: item.type, id: item.id  } }"
                style="text-decoration: none; color: inherit;">
            >
                <Card :cardInfo="item"/>
            </router-link>
        </div>
    </div>
</template>
