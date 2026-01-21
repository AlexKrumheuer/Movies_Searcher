<script setup>
    import PerfilCard from './PerfilCard.vue';
    import PerfilTracking from './PerfilTracking.vue';
    import PerfilFavorites from './PerfilFavorites.vue';
    import '../../../style/perfil.css'
    import {ref} from 'vue'
    import PerfilConfiguration from './PerfilConfiguration.vue';

    const display = ref('favorite')

    const changeDisplay = () => {
        if(display.value == 'favorite') {
            display.value = 'configuration'
        } else {
            display.value = 'favorite'
        }
    }
</script>
<template>
    <div class="perfil-container">
        <PerfilCard/>
        <PerfilTracking/>
        <div class="section-nav">
            <div :style="{ color: display === 'favorite' ? '#39A0FF' : '' }" class="sec" @click="changeDisplay">
                <fa icon="heart"/>
                <p>MY FAVORITES</p>
            </div>
            <div :style="{ color: display === 'configuration' ? '#39A0FF' : '' }" class="sec" @click="changeDisplay">
                <fa icon="gear"/>
                <p>CONFIGURATION</p>
            </div>
        </div>
        <nav v-if="display == 'favorite'" class="grid-favorites">
            <PerfilFavorites/>
        </nav>
        <nav v-else class="perfil-config">
            <PerfilConfiguration/>
        </nav>
    </div>
</template>
