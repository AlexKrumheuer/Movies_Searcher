<script setup>
import Header from './components/Header.vue';
import NavigationMenu from './components/NavigationMenu.vue';
import Generos from './components/Generos.vue';
import GridContent from './pages/GridContent.vue';
import Home from './pages/Home.vue';
import SpecificPage from './pages/SpecificPage.vue';
import Footer from './components/Footer.vue';
import { ref } from 'vue';

let pagina = ref('home')
let infoSpecific = ref({
  tipo: '',
  id: null,
});
const mudarPagina = (novaPagina) => {
  pagina.value = novaPagina;
}

const valorPesquisa = ref('')
function mudarValorPesquisa(novoValor) {
  valorPesquisa.value = novoValor;
}

function definirIdEspecifico(id, tipo) {
  infoSpecific.value = { id, tipo };
}

let categoriaClicada = ref(false)
const abrirCategoria = () => {
  categoriaClicada.value = !categoriaClicada.value
}

let listaGeneros = ref([])
const filtrarGenero = (id) => {
  if(listaGeneros.value.includes(id)){
    listaGeneros.value = listaGeneros.value.filter(genero => genero != id)
  } else {
    listaGeneros.value.push(id)
  }
}

const zerarGeneros = () => {
  listaGeneros.value = []
}

</script>

<template>
  <Header />
  <NavigationMenu 
    @abrir-categoria="abrirCategoria"
    :categoria-clicada="categoriaClicada" 
    @mudar-valor-pesquisa="mudarValorPesquisa" 
    :pagina-atual="pagina" 
    @mudar-pagina="mudarPagina" 
  />
  <Generos 
    @zerar-generos="zerarGeneros"
    @filtrar-genero="filtrarGenero"
    :lista-generos="listaGeneros"
    v-if="pagina !== 'home' && pagina !== 'specific' && categoriaClicada && pagina !== 'lancamentos'" 
    :tipo="pagina === 'filmes' ? 'movie' : pagina === 'series' ? 'tv' : ''" 
  />
  <Home v-if="pagina === 'home'" @mudar-pagina="mudarPagina" @definir-id-especifico="definirIdEspecifico" />
  <GridContent 
    @definir-id-especifico="definirIdEspecifico" 
    @mudar-pagina="mudarPagina" 
    :valor-pesquisa="valorPesquisa" 
    v-else-if="pagina === 'lancamentos'" 
    :tipo="'lancamentos'" 
    :titulo="'recentes'"
  />
  <GridContent 
    :lista-generos="listaGeneros"
    @definir-id-especifico="definirIdEspecifico" 
    @mudar-pagina="mudarPagina" 
    :valor-pesquisa="valorPesquisa" 
    v-else-if="pagina === 'filmes'" 
    :tipo="'movie'" 
    :titulo="'Filmes'"
  />
  <GridContent 
    :lista-generos="listaGeneros"
    @definir-id-especifico="definirIdEspecifico" 
    @mudar-pagina="mudarPagina" 
    :valor-pesquisa="valorPesquisa" 
    v-else-if="pagina === 'series'" 
    :tipo="'tv'" :titulo="'Séries'" 
  />
  <SpecificPage v-else :info-especifico="infoSpecific" />
  <Footer/>
</template>

<style>

</style>
