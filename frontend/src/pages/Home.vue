<script setup>
import {ref} from 'vue'
import HomeFeedConteudo from '../components/HomeFeedConteudo.vue';

const emit = defineEmits(['mudar-pagina', 'definir-id-especifico']);
const conteudoClicadoHome = (novaPagina, id, tipo) => {
    emit('mudar-pagina', novaPagina);
    emit('definir-id-especifico', id, tipo);
}
const lancamento = ref({
    title: 'Filmes Populares',
    tipo: 'movie',
})

const ultimasSeries = ref({
    title: 'Séries Populares',
    tipo: 'tv',

})
</script>
<template>
    <section class="home">
        <HomeFeedConteudo @conteudo-clicado-home="conteudoClicadoHome" :sectionInfo="lancamento" />
        <HomeFeedConteudo @conteudo-clicado-home="conteudoClicadoHome" :sectionInfo="ultimasSeries" />
    </section>
    

</template> 
<style>
    .home {
        background-color: #0A0A10;
        width: 100%;
        padding: 2rem 10rem;
        display: flex;
        flex-direction: column;
        gap: 5rem;
    }
</style>