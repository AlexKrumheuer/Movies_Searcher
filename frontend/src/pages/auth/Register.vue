<script setup>
import { ref } from 'vue'
import '../../style/auth.css'
import Validator from '../../util/Validator'
import api from '../../services/api'
import {useRouter} from 'vue-router'
let username = ref('')
let email = ref('')
let password = ref('')
let confirmPassword = ref('')
let showPassword = ref(false)
let showConfirmPassword = ref(false)
let error = ref('')
const router = useRouter()

const validateInfo = () => {
    if(!Validator.validateText(username.value)) {
        error.value = "Username não pode estar em branco"
        return
    }

    if(!Validator.validateEmail(email.value)) {
        error.value = "E-mail inválido, exemplo: test@test.com"
        return
    }

    if(!Validator.validatePassword(password.value)) {
        error.value = "Senha inválida, exemplo: Test@@1234"
        return
    }

    if(!Validator.validateEqualsPassword(password.value, confirmPassword.value)) {
        error.value = "As senhas não coincidem"
        return
    }
    registrarUser()
}

const registrarUser = async () => {
    try {
        const response = await api.post("/auth/register",{
            username: username.value,
            email: email.value,
            password: password.value
        })
        router.push("/login")
    } catch(e) {
        if(e.response && e.response.status === 400) {
            error.value = "Esse e-mail já está em uso"
        } else {
            error.value = "Ocorreu um erro, tente novamente"
        }
    }
}
</script>
<template>
    <div class="auth-wrapper">
        <form @submit.prevent="validateInfo" class="auth-container">
            <h2>Crie sua conta</h2>
            <div class="auth-input">
                <label for="username">Username</label>
                <div class="auth-input__text">
                    <fa icon="circle-user" class="icon" />
                    <input type="text" name="username" placeholder="Digite seu username..." v-model="username">

                </div>
            </div>
            <div class="auth-input">
                <label for="email">Email</label>
                <div class="auth-input__text">
                    <fa icon="at" class="icon" />
                    <input type="email" name="email" placeholder="Digite seu e-mail..." v-model="email">

                </div>
            </div>
            <div class="auth-input">
                <label for="password">Senha</label>
                <div class="auth-input__password">
                    <fa icon="lock" class="icon" />
                    <input :type="showPassword ? 'text' : 'password'" name="password" placeholder="Digite sua senha..." v-model="password">
                    <fa v-if="showPassword" @click="showPassword = false" icon="eye" class="icon"/>
                    <fa v-else icon="eye-slash" @click="showPassword = true" class="icon"/>
                </div>
            </div>
            <div class="auth-input">
                <label for="confirmPassword">Confirme sua senha</label>
                <div class="auth-input__password">
                    <fa icon="lock" class="icon"/>
                    <input :type="showConfirmPassword ? 'text' : 'password'" name="confirmPassword" placeholder="Digite a sua senha..." v-model="confirmPassword">
                    <fa v-if="showConfirmPassword" @click="showConfirmPassword = false" icon="eye" class="icon"/>
                    <fa v-else icon="eye-slash" @click="showConfirmPassword = true" class="icon"/>
                </div>
            </div>
            <button class="auth-submit" type="submit">Registrar</button>
            <div>
                <router-link to="/login" class="auth-redirect__links">
                    <p>Já possui uma conta?</p>
                </router-link>
                <p v-if="error" class="auth-error">{{ error }}</p>
            </div>
        </form>
    </div>
</template>
