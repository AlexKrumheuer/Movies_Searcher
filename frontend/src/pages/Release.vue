<script setup>
    import GridContent from './GridContent.vue';
    defineProps({
    valorPesquisa: String,
    listaGeneros: Array
});
</script>
<template>
    <GridContent 
    @definir-id-especifico="definirIdEspecifico" 
    :valor-pesquisa="valorPesquisa" 
    :tipo="'lancamentos'" 
    :titulo="'recentes'"
  />
</template>