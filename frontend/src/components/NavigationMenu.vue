<script setup>
import {    ref, watch } from 'vue';
const emit = defineEmits(['mudar-valor-pesquisa', 'mudar-pagina', 'abrir-categoria'])
const props = defineProps({
    paginaAtual: String,
    categoriaClicada: Boolean,
})


let search = ref('')

const submitSearch = () => {
    emit('mudar-valor-pesquisa', search.value)
}

let categoriaCliqueVariavel = ref(false)
watch(() => props.categoriaClicada, () => {
    categoriaCliqueVariavel.value = props.categoriaClicada
})
const categoriaClique = () => {
    emit('abrir-categoria')
    categoriaCliqueVariavel.value = !categoriaCliqueVariavel.value}
</script>

<template>
    <nav>
        <div :style="paginaAtual === 'home' ? { color: '#39A0FF', borderBottom: '1px solid #39A0FF' } : {}" v-on:click="emit('mudar-pagina', 'home')" class="element">
            <fa icon="home"/>
            <p>Home</p>
        </div>
        <div @click="categoriaClique" class="element">
            <fa icon="bars" />
            <p>Categories</p>
            <fa v-if="props.categoriaClicada" icon="angles-down" />
            <fa v-else="props.categoriaClicada" icon="angles-up" />
        </div>
        <div :style="paginaAtual === 'lancamentos' ? { color: '#39A0FF', borderBottom: '1px solid #39A0FF' } : {}" v-on:click="emit('mudar-pagina', 'lancamentos')" class="element">
            <fa icon="calendar-days"/>
            <p>Lançamentos</p>
        </div>
       
        <div :style="paginaAtual === 'filmes' ? { color: '#39A0FF', borderBottom: '1px solid #39A0FF' } : {}" v-on:click="emit('mudar-pagina', 'filmes')" class="element">
            <fa icon="clapperboard" />
            <p>Filmes</p>
        </div>
        <div :style="paginaAtual === 'series' ? { color: '#39A0FF', borderBottom: '1px solid #39A0FF' } : {}" v-on:click="emit('mudar-pagina', 'series')" class="element">
            <fa icon="play" />
            <p>Séries</p>
        </div>
        <form  @submit.prevent="submitSearch()" class="search">
            <input type="text" v-model="search" placeholder="Search..."/>
            <fa type="submit" icon="magnifying-glass"/>
        </form>
    </nav>
</template>

<style>
    nav {
        background-color: #1C1C23;
        display: flex;
        justify-content: space-between;
        padding: 1rem 5rem;
        font-size: 1.5rem;
    }

    .element {
        display: flex;
        align-items: center;
        color: #fff;
        transition: 0.25s;
        cursor: pointer;
        gap: 1rem;
    }

    .element:hover {
         color: #39A0FF;
    }

    .search {
        background-color: #fff;
        border-radius: 8px;
        padding: 0.5rem;
    }

    .search input {
        padding: 0.5rem;
        border: none;
    }

    .search input:focus {
        outline: none;
    }
</style>