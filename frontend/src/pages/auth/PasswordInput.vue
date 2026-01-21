<script setup>
import { ref } from 'vue'
const props = defineProps({
    modelValue: { type: String, default: '' },
    placeholder: { type: String, default: '' },
    id: { type: String, required: false },
    labelText: { type: String, default: 'Password' }
})

const emit = defineEmits(['update:modelValue'])

const showPassword = ref(false)

</script>
<template>
    <div class="auth-input">
                    <label :for="id">{{ labelText }}</label>

        <div class="auth-input__password">
            <fa icon="lock" class="icon" />
            <input :id="id" :type="showPassword ? 'text' : 'password'" name="password" :placeholder="placeholder"
                :value="modelValue" v-model="props.password" @input="$emit('update:modelValue', $event.target.value)" />
            <fa v-if="showPassword" @click="showPassword = false" icon="eye" class="icon click-icon" />
            <fa v-else icon="eye-slash" @click="showPassword = true" class="icon click-icon" />
        </div>
    </div>

</template>
<style>
.auth-input {
    display: flex;
    flex-direction: column;
    gap: 0.8rem;
    font-size: 1.4rem;
    width: 100%;
}


.auth-input input {
    padding: 0.8rem;
    border-radius: 8px;
    width: 100%;
}

.auth-input__password {
    color: #000;
    display: grid;
    align-items: center;
    justify-content: center;
    grid-template-columns: 10% 80% 10%;
    background-color: #fff;
    width: 100%;
    padding: 0.5rem;
    border-radius: 8px;
    font-size: 1.8rem;
}

.auth-input__text input,
.auth-input__password input {
    width: 100%;
    border: none;
}

.icon {
    margin: 0 auto;
}

.click-icon {
    cursor: pointer;
}
</style>