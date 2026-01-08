<script setup>
    import GridContent from './GridContent.vue';
</script>
<template>
    <GridContent 
    @definir-id-especifico="definirIdEspecifico" 
    :valor-pesquisa="valorPesquisa" 
    :tipo="'lancamentos'" 
    :titulo="'recentes'"
  />
</template>