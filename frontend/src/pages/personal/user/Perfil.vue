<script setup>
    import PerfilCard from './PerfilCard.vue';
    import PerfilTracking from './PerfilTracking.vue';
    import PerfilFavorites from './PerfilFavorites.vue';
    import '../../../style/perfil.css'
</script>
<template>
    <div class="perfil-container">
        <PerfilCard/>
        <PerfilTracking/>
        <div class="section-nav">
            <div class="sec">
                <fa icon="heart"/>
                <p>MY FAVORITES</p>
            </div>
            <div class="sec">
                <fa icon="gear"/>
                <p>CONFIGURATION</p>
            </div>
        </div>
        <nav class="grid-favorites">
            <PerfilFavorites/>
        </nav>
        <nav class="perfil-config">
        </nav>
    </div>
</template>
