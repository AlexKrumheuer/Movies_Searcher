<script setup>
    import { defineProps, onMounted, defineEmits} from 'vue';
    import { useMidia } from '../util/useMidia';
    import Card from './Card.vue';
    const props = defineProps({
        sectionInfo: Object,
    })

    const emit = defineEmits(['conteudo-clicado-home']);
    const mudarPagina = (novaPagina, id) => {
    emit('conteudo-clicado-home', novaPagina, id, props.sectionInfo.tipo);
}

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
            <button @click="verTudo()">
                Ver tudo
            </button>
        </div>
        <div class="grid">
            <p v-if="loading">Carregando...</p>
            <Card @mudar-pagina="mudarPagina('specifc',item.id)" v-for="item in itens.slice(0,10)" :key="item.id" :cardInfo="item"/>
        </div>
    </div>
</template>
<style>
    .feed {
        width: 100%;
        display: flex;
        flex-direction: column;
        gap: 3rem;
    }
    .title {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .title h1 {
        color: #fff;
        font-size: 4rem;
    }

    .title button {
        padding: 1.5rem 2rem;
        cursor: pointer;
        background-color: #fff;
        height: fit-content;
        border-radius: 8px;
        font-size: 1.5rem;
        font-weight: bold;
        border: none;
        transition: 0.25s;

    }

    .title button:hover { 
        background-color: #39A0FF;
        color: #fff;
    }

    .grid {
        display: grid;
        grid-template-columns: 1fr 1fr 1fr 1fr 1fr;
        gap: 2rem;
    }
</style>