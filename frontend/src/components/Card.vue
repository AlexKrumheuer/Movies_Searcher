<script setup>
import '../style/card.css'
const props = defineProps({
    cardInfo: Object,
})
</script>
<template>
    <div class="card">
        <img :src="props.cardInfo.img" :alt="props.cardInfo.title">
        <p>{{ props.cardInfo.title }} {{ props.cardInfo.year }}</p>
    </div>
</template>
