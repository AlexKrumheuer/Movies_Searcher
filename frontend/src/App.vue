<script setup>
import Header from './components/Header.vue';
import NavigationMenu from './components/NavigationMenu.vue';
import Generos from './components/Generos.vue';
import Footer from './components/Footer.vue';
import { ref, computed } from 'vue';
import { useRoute} from 'vue-router'

const route = useRoute()
const valorPesquisa = ref('')

const tipoAtual = computed(() => {
  if (route.path.includes('movie')) return 'movie';
  if (route.path.includes('shows')) return 'tv';
  return '';
});


// navigation menuconst valorPesquisa = ref('')
function mudarValorPesquisa(novoValor) {
  valorPesquisa.value = novoValor;
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
  />
  <Generos 
    @zerar-generos="zerarGeneros"
    @filtrar-genero="filtrarGenero"
    :lista-generos="listaGeneros"
    v-if="pagina !== 'home' && pagina !== 'specific' && categoriaClicada && pagina !== 'lancamentos'" 
    :tipo="tipoAtual" 
  />
  <router-view v-slot="{ Component }">
    <component
      :is="Component"
      :valor-pesquisa="valorPesquisa"
      :lista-generos="listaGeneros"
    />
  </router-view>

  <Footer/>
</template>

<style>

</style>
