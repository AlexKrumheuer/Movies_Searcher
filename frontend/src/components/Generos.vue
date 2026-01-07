<script setup>
import { onMounted, ref, watch, defineEmits } from 'vue';
import {useMidia} from '../util/useMidia';

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
<style>
.generos {
    background-color: #2C2C34;
    padding: 1rem 5rem;
    color: white;
}

.lista-generos {
    width: 50vw;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
}

.genero-button {
    background-color: #39A0FF;
    border: none;
    border-radius: 5px;
    color: white;
    padding: 0.5rem 1rem;
    margin: 0.5rem;
    cursor: pointer;
    transition: background-color 0.3s;
}

.genero-button:hover {
    background-color: #1E90FF;
}
</style>