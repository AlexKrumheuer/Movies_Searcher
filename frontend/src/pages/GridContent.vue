<script setup>
import { computed, onMounted, ref, watch, defineEmits } from 'vue';
import { useMidia } from '../util/useMidia';
import Card from '../components/Card.vue';

const emit = defineEmits(['mudar-pagina', 'definir-id-especifico', 'abrir-categoria']);
const mudarPagina = (novaPagina, id) => {
    emit('mudar-pagina', novaPagina);
    emit('definir-id-especifico', id, tipoApi);
}

const props = defineProps({
    tipo: {type: String, required: true},
    titulo: String,
    valorPesquisa: String,
    listaGeneros: Array,
})

const pagAtual = ref(1)
const {itens, loading, totalPages, totalResults, buscarMidia} =  useMidia();

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
}, {deep: true})


onMounted(() => {
    carregar()
    window.scrollTo(0,0)
});

watch(() => tipoApi.value, () => {
    if(props.tipo === 'lancamentos') {
        emit('abrir-categoria', tipoApi.value)
    }
});

const pagVisiveis = computed(() => {
    let inicio = pagAtual.value - 3;
    let fim = pagAtual.value + 3;
    if(inicio < 1) {
        inicio = 1;
        fim = 7;
    }
    if(fim > totalPages.value) {
        fim = totalPages.value;
        inicio = totalPages.value - 6;
        if(inicio < 1) inicio = 1
    }
    let paginas = [];
    for(let i = inicio; i <= fim; i++) {    
        if(i < 1 || i > totalPages.value) {
            continue;
        }
        paginas.push(i);
    }
    return paginas;
})

const botaoLancamento = () => {
    tipoApi.value = tipoApi.value === 'movie' ? 'tv' : 'movie';
    condicaoApi.value =  condicaoApi.value === 'upcoming' ?  'on_the_air' : 'upcoming'
    pagAtual.value = 1;
    carregar();
}

function mudarPag(type) {
    if(type === 'next' && pagAtual.value < totalPages.value) {
        pagAtual.value++;
    } else if(type === 'prev' && pagAtual.value > 1) {
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
            <p>Pag {{pagAtual}}/{{ totalPages }}. Total de resultados ({{ totalResults }})</p>
            <button @click="botaoLancamento" v-if="props.tipo === 'lancamentos'" class="button-lancamento"><span>
                {{ tipoApi === 'movie' ? 'Ver Séries' : 'Ver Filmes' }}
            </span></button>
        </div>
        <div class="grid-filme">
            <p v-if="loading">Carregando...</p>
            <Card @mudar-pagina="mudarPagina('specifc',item.id)" v-for="item in itens" :key="item.id" :cardInfo="item"/>
        </div>
        <div class="paginacao">
            <button @click="mudarPag('prev')" :disabled="pagAtual === 0">Anterior</button>
            <p 
                :style="pagAtual === n ? {backgroundColor: '#39A0FF', color: '#fff', border: '1px solid #39A0FF'} : {}" 
                @click="mudarPagDireto(n)" 
                v-for="n in pagVisiveis" 
                :key="n"
            >
                {{ n }}
            </p>
            <button @click="mudarPag('next')" :disabled="pagAtual === totalPages">Próximo</button>
        </div>
    </section>
    
</template>
<style>
    .title-filme {
        color: #fff;
        text-align: center;
        width: 100%;
        font-size: 1.5rem;
    }
    .filme {
        background-color: #0A0A10;
        width: 100%;
        padding: 2rem 10rem;
        display: flex;
        flex-direction: column;
        gap: 5rem;
        min-height: 75vh;
    }

    .button-lancamento {
        padding: 0.8rem 1.5rem;
        cursor: pointer;
        background-color: #39A0FF;
        border-radius: 8px;
        font-size: 1.5rem;
        font-weight: bold;
        border: none;
        transition: 0.25s;
        color: #fff;
        margin-top: 1rem;
    }

    .button-lancamento:hover {
        background-color: #fff;
        color: #39A0FF;
    }

    .grid-filme {
        display: grid;
        grid-template-columns: 1fr 1fr 1fr 1fr 1fr;
        gap: 2rem;
    }

    .paginacao {
        color: #fff;
        display: flex;
        gap: 1rem;
        justify-content: center;
        align-items: center;
    }

    .paginacao button {
        padding: 0.8rem 1.5rem;
        cursor: pointer;
        background-color: #fff;
        height: fit-content;
        border-radius: 8px;
        font-size: 1.5rem;
        font-weight: bold;
        border: none;
        transition: 0.25s;
    }

    .paginacao button:hover { 
        background-color: #39A0FF;
        color: #fff;
    }

    .paginacao p {
        font-size: 1.5rem;
        background-color: #0A0A10;
        border: 1px solid #fff;
        border-radius: 8px;
        padding: 0.5rem 1rem;
        cursor: pointer;
    }

    .paginacao p:hover {
        background-color: #39A0FF;
        border: 1px solid #39A0FF;
        color: #fff;
    }
</style>