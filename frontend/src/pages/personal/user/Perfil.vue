<script setup>
    import PerfilCard from './PerfilCard.vue';
    import PerfilTracking from './PerfilTracking.vue';
    import '../../../style/perfil.css'
    import {ref} from 'vue'
    import PerfilConfiguration from './PerfilConfiguration.vue';
    import PerfilSaved from './PerfilSaved.vue';

    const display = ref('favorite')

    const changeDisplay = (newDisplayValue) => {
        display.value = newDisplayValue
    }
</script>
<template>
    <div class="perfil-container">
        <PerfilCard/>
        <PerfilTracking/>
        <div class="section-nav">
            <div :style="{ color: display === 'favorite' ? '#39A0FF' : '' }" class="sec" @click="changeDisplay('favorite')">
                <fa icon="heart"/>
                <p>MY FAVORITES</p>
            </div>
            <div :style="{ color: display === 'watched' ? '#39A0FF' : '' }" class="sec" @click="changeDisplay('watched')">
                <fa icon="circle-check"/>
                <p>WATCHED</p>
            </div>
            <div :style="{ color: display === 'watchlist' ? '#39A0FF' : '' }" class="sec" @click="changeDisplay('watchlist')">
                <fa icon="list"/>
                <p>WATCH LIST</p>
            </div>
            <div :style="{ color: display === 'configuration' ? '#39A0FF' : '' }" class="sec" @click="changeDisplay('configuration')">
                <fa icon="gear"/>
                <p>CONFIGURATION</p>
            </div>
        </div>
        <nav v-if="display == 'favorite'" class="grid-favorites">
            <PerfilSaved :display="'favorite'"/>
        </nav>
        <nav v-else-if="display == 'watched'" class="grid-favorites">
            <PerfilSaved :display="'watched'"/>
        </nav>
        <nav v-else-if="display == 'watchlist'" class="grid-favorites">
            <PerfilSaved :display="'watchlist'"/>
        </nav>
        <nav v-else class="perfil-config">
            <PerfilConfiguration/>
        </nav>
    </div>
</template>
