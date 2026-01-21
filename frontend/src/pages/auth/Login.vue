<script setup>
import { ref } from 'vue'
import '../../style/auth.css'
import Validator from '../../util/Validator'
import api from '../../services/api'
import { useRouter } from 'vue-router'
import PasswordInput from './PasswordInput.vue'
let email = ref('')
let password = ref('')
let error = ref('')
const router = useRouter()

const validateInfo = () => {
    if (!Validator.validateText(email.value)) {
        error.value = "E-mail não pode estar em branco"
        return
    }

    if (!Validator.validateText(password.value)) {
        error.value = "Senha não pode estar em branco"
        return
    }

    loginUser()
}

const loginUser = async () => {
    try {
        const response = await api.post("/auth/login", {
            email: email.value,
            password: password.value
        })
        const token = response.data.token
        localStorage.setItem("token", token)
        window.dispatchEvent(new Event('auth-change'))
        router.push("/")
    } catch (e) {
        if (e.response && e.response.status === 400) {
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
            <h2>Logue na sua conta</h2>
            <div class="auth-input">
                <label for="email">Email</label>
                <div class="auth-input__text">
                    <fa icon="at" class="icon" />
                    <input type="email" name="email" placeholder="Digite seu e-mail..." v-model="email">

                </div>
            </div>
            <PasswordInput :id="'password'" :password="confirmPassword" v-model="password"
                placeholder="Type your password..." :labelText="Password" />
            <button class="auth-submit" type="submit">Logar</button>
            <div>
                <div class="container-redirect">
                    <router-link to="/register" class="auth-redirect__links">
                        <p>Não possui uma conta?</p>
                    </router-link>
                    <router-link to="/forget_password" class="auth-redirect__links">
                        <p>Esqueceu sua senha?</p>
                    </router-link>
                </div>
                <p v-if="error" class="auth-error">{{ error }}</p>
            </div>
        </form>
    </div>
</template>
