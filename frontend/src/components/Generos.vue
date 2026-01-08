<script setup>
// Component for genders
// It receives a Prop with the type (tv-show or movie) and a list with all the genders to be displayed
// It has also a logic to deal with removing and adding genders


import { onMounted, ref, watch, defineEmits } from 'vue';
import {useMidia} from '../util/useMidia';
import '../style/genders.css'

const props = defineProps({
    tipo: {type: String, required: true},
    listaGeneros: {type: Array}
}
)

const emit = defineEmits(['filtrar-genero', 'zerar-generos'])

const {listarGeneros, generos} = useMidia();
const carregar = () => {
    listarGeneros(props.tipo);
}

watch(() => props.tipo, () => {
    emit('zerar-generos')
    carregar();
});

onMounted(() => {
    carregar();
});

const filtrarPorGenero = (id) => {
    emit('filtrar-genero', id)
}
</script>
<template>
    <section class="generos">
        <h2>Lista de Gêneros para <span v-if="tipo === 'movie'">Filmes</span><span v-else-if="tipo === 'tv'">Séries</span></h2>
        <div class="lista-generos">
            <button
                :style="props.listaGeneros.includes(genero.id) ? {backgroundColor: '#000'}:{} "
                class="genero-button"
                v-for="genero in generos"
                :key="genero.id"
                @click="filtrarPorGenero(genero.id)"
            >
                {{ genero.name }}
            </button>
        </div>
    </section>
</template>
