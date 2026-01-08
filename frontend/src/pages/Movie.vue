<script setup>
    import GridContent from './GridContent.vue';

    defineProps({
      valorPesquisa: String,
      listaGeneros: Array
  });

</script>
<template>
    <GridContent 
    :lista-generos="listaGeneros"
    @definir-id-especifico="definirIdEspecifico" 
    :valor-pesquisa="valorPesquisa" 
    :tipo="'movie'" 
    :titulo="'Filmes'"
  />
</template>