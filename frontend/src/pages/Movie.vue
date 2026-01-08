<script setup>
    import GridContent from './GridContent.vue';
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