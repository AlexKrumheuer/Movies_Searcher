<script setup>
import PerfilSaved from '../user/PerfilSaved.vue';


</script>
<template>
    <div class="container-user__content">
        <h2 class="user-content--title">Your Watched Media</h2>
        <PerfilSaved :display="'watched'"/>
    </div>
</template>
<style>
    .container-user__content {
        padding: 4rem;
        min-height: 80vh;
        background-color: #0A0A10;
    }

    .user-content--title {
        color: #fff;
        font-size: 2.5rem;
        text-align: center;
        margin-bottom: 3rem;
    }
</style>