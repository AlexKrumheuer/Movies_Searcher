<script setup>
    // Navigation Menu component
    // It has all the logic of the page`s routes. And also it`s resposible for the input search


import { ref, watch } from 'vue';
import '../style/navigationMenu.css'
const emit = defineEmits(['mudar-valor-pesquisa', 'abrir-categoria'])
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
        <router-link to="/" class="element">
            <fa icon="home"/>
            <p>Home</p>
        </router-link>
        <div @click="categoriaClique" class="element">
            <fa icon="bars" />
            <p>Categories</p>
            <fa v-if="props.categoriaClicada" icon="angles-down" />
            <fa v-else="props.categoriaClicada" icon="angles-up" />
        </div>
        <router-link to="/release" class="element">
            <fa icon="calendar-days"/>
            <p>Lançamentos</p>
        </router-link>
       
        <router-link to="/movie" class="element">
            <fa icon="clapperboard" />
            <p>Filmes</p>
        </router-link>
        <router-link to="/shows" class="element">
            <fa icon="play" />
            <p>Séries</p>
        </router-link>
        <form  @submit.prevent="submitSearch()" class="search">
            <input type="text" v-model="search" placeholder="Search..."/>
            <fa type="submit" icon="magnifying-glass"/>
        </form>
    </nav>
</template>
<style>
    
</style>