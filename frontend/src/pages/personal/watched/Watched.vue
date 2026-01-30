<script setup>
import PerfilSaved from '../user/PerfilSaved.vue';
import '../../../style/personal/watched.css'


</script>
<template>
    <div class="container-user__content">
        <h2 class="user-content--title">Your Watched Media</h2>
        <PerfilSaved :display="'watched'"/>
    </div>
</template>
<style>

</style>