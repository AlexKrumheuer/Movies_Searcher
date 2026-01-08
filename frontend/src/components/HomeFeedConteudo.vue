<script setup>
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

    const verTudo = () => {
        if(props.sectionInfo.tipo === 'movie') {
            emit('conteudo-clicado-home', 'filmes', null, null);
        } else if(props.sectionInfo.tipo === 'tv') {
            emit('conteudo-clicado-home', 'series', null, null);
        }
    }

    onMounted(() => {
        carregar();  
    })
</script>
<template>
    <div class="feed">
        <div class="title">
            <h1>{{props.sectionInfo.title}}</h1>
            <route-link @click="verTudo()">
                Ver tudo
            </route-link>
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
