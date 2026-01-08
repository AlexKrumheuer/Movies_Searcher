<script setup>
import { computed, onMounted, ref, watch, defineEmits } from 'vue';
import { useMidia } from '../util/useMidia';
import Card from '../components/Card.vue';
import '../style/gridContent.css'

const emit = defineEmits(['definir-id-especifico', 'abrir-categoria']);


const props = defineProps({
    tipo: { type: String, required: true },
    titulo: String,
    valorPesquisa: String,
    listaGeneros: Array,
})

const pagAtual = ref(1)
const { itens, loading, totalPages, totalResults, buscarMidia } = useMidia();

let condicaoApi = ref(props.tipo === 'lancamentos' ? 'upcoming' : 'popular')
let tipoApi = ref(props.tipo === 'movie' ? 'movie' : props.tipo === 'tv' ? 'tv' : 'movie')



const carregar = () => {
    buscarMidia(tipoApi.value, pagAtual.value, props.valorPesquisa, condicaoApi.value, props.listaGeneros ? props.listaGeneros : []);
}


let debounce
watch(() => props.valorPesquisa, () => {
    clearTimeout(debounce);
    debounce = setTimeout(() => {
        pagAtual.value = 1;
        carregar();
    }, 500);
});

watch(() => props.listaGeneros, () => {
    pagAtual.value = 1
    carregar()
}, { deep: true })


onMounted(() => {
    carregar()
    window.scrollTo(0, 0)
});

watch(() => tipoApi.value, () => {
    if (props.tipo === 'lancamentos') {
        emit('abrir-categoria', tipoApi.value)
    }
});

const pagVisiveis = computed(() => {
    let inicio = pagAtual.value - 3;
    let fim = pagAtual.value + 3;
    if (inicio < 1) {
        inicio = 1;
        fim = 7;
    }
    if (fim > totalPages.value) {
        fim = totalPages.value;
        inicio = totalPages.value - 6;
        if (inicio < 1) inicio = 1
    }
    let paginas = [];
    for (let i = inicio; i <= fim; i++) {
        if (i < 1 || i > totalPages.value) {
            continue;
        }
        paginas.push(i);
    }
    return paginas;
})

const botaoLancamento = () => {
    tipoApi.value = tipoApi.value === 'movie' ? 'tv' : 'movie';
    condicaoApi.value = condicaoApi.value === 'upcoming' ? 'on_the_air' : 'upcoming'
    pagAtual.value = 1;
    carregar();
}

function mudarPag(type) {
    if (type === 'next' && pagAtual.value < totalPages.value) {
        pagAtual.value++;
    } else if (type === 'prev' && pagAtual.value > 1) {
        pagAtual.value--;
    }
    carregar();
}

function mudarPagDireto(n) {
    pagAtual.value = n;
    carregar();
}
</script>
<template>
    <section class="filme">
        <div class="title-filme">
            <h1>
                <span v-if="titulo === 'recentes'">
                    {{ tipoApi === 'movie' ? 'Filmes Recentes' : 'Séries Recentes' }}
                </span>
                <span v-else>{{ titulo }}</span>
            </h1>
            <p>Pag {{ pagAtual }}/{{ totalPages }}. Total de resultados ({{ totalResults }})</p>
            <button @click="botaoLancamento" v-if="props.tipo === 'lancamentos'" class="button-lancamento"><span>
                    {{ tipoApi === 'movie' ? 'Ver Séries' : 'Ver Filmes' }}
                </span></button>
        </div>
        <div class="grid-filme">
            <p v-if="loading">Carregando...</p>
            <router-link v-for="item in itens" :key="item.id"
                :to="{ name: 'details', params: { type: tipoApi, id: item.id } }"
                style="text-decoration: none; color: inherit;">
                <Card :cardInfo="item" />
            </router-link>
        </div>
        <div class="paginacao">
            <button @click="mudarPag('prev')" :disabled="pagAtual === 0">Anterior</button>
            <p :style="pagAtual === n ? { backgroundColor: '#39A0FF', color: '#fff', border: '1px solid #39A0FF' } : {}"
                @click="mudarPagDireto(n)" v-for="n in pagVisiveis" :key="n">
                {{ n }}
            </p>
            <button @click="mudarPag('next')" :disabled="pagAtual === totalPages">Próximo</button>
        </div>
    </section>

</template>
