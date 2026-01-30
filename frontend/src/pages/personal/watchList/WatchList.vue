<script setup>
import PerfilSaved from '../user/PerfilSaved.vue';
import '../../../style/personal/watchlist.css'


</script>
<template>
    <div class="container-user__content">
        <h2 class="user-content--title">Your Watch List</h2>
        <PerfilSaved :display="'watchlist'"/>
    </div>
</template>
