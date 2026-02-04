<script setup>
import { onMounted, ref } from 'vue'
import PasswordInput from '../../auth/PasswordInput.vue';
import api from '../../../services/api';
import '../../../style/user/perfilconfiguration.css'
import Validator from '../../../util/Validator.js'
import { useToast } from 'vue-toastification';

const userData = ref({})
const newUsername = ref('')
const newEmail = ref('')  
const toast = useToast() 

onMounted(async () => {
    try {
        const response = await api.get("/auth/me")
        userData.value = response.data
        newUsername.value = userData.value.username
        newEmail.value = userData.value.email
    } catch(error) {
        console.error("Error when getting user info")
    }
})

const errorMessage = ref('')

const usernameIsEditing = ref(false)
const usernameEditClick = () => {
    usernameIsEditing.value = !usernameIsEditing.value
}

const emaiilIsEditing = ref(false)
const emailEditClick = () => {
    emaiilIsEditing.value = !emaiilIsEditing.value
}


const currentPassword = ref('')
const newPassword = ref('')
const confirmNewPassword = ref('')

const submitForm = async () => {
    errorMessage.value = '';

    const isNameChanged = newUsername.value !== userData.value.username;
    const isEmailChanged = newEmail.value !== userData.value.email;
    const isTryingPasswordChange = newPassword.value !== '' || confirmNewPassword.value !== '';
    
    const isSensitiveChange = isEmailChanged || isTryingPasswordChange;

    if (!isNameChanged && !isEmailChanged && !isTryingPasswordChange) {
        toast.info("Nenhuma alteração detectada.");
        return;
    }

    if (!Validator.validateText(newUsername.value)) {
        errorMessage.value = "Username inválido.";
        return;
    }
    if (!Validator.validateEmail(newEmail.value)) {
        errorMessage.value = "E-mail inválido.";
        return;
    }

    if (isSensitiveChange && !currentPassword.value) {
        toast.error("A senha atual é obrigatória para alterar e-mail ou senha.");
        return;
    }

    if (isTryingPasswordChange) {
        if (!Validator.validateEqualsPassword(newPassword.value, confirmNewPassword.value)) {
            toast.error("As novas senhas não coincidem.");
            return;
        }
        if (!Validator.validatePassword(newPassword.value)) {
            toast.error("A nova senha não atende aos requisitos de segurança.");
            return;
        }
    }

    const payload = {
        username: newUsername.value,
        email: newEmail.value,
        currentPassword: isSensitiveChange ? currentPassword.value : null,
        newPassword: isTryingPasswordChange ? newPassword.value : null,
        confirmNewPassword: isTryingPasswordChange ? confirmNewPassword.value : null
    };

    try {
        const response = await api.put("/auth/me", payload);
        
        if (isEmailChanged) {
            toast.info("Verifique o link de confirmação enviado para o seu novo e-mail.");
        } else {
            toast.success("Perfil atualizado com sucesso!");
        }

        userData.value.username = newUsername.value;
        
        currentPassword.value = '';
        newPassword.value = '';
        confirmNewPassword.value = '';
        
    } catch (error) {
        const status = error.response?.status;
        if (status === 401 || status === 403) {
            toast.error("A senha atual informada está incorreta.");
        } else if (status === 409) {
            toast.error("Este nome de utilizador ou e-mail já está em uso.");
        } else {
            toast.error("Erro ao atualizar perfil. Tente novamente mais tarde.");
        }
    }
}
</script>
<template>
    <form @submit.prevent="submitForm" class="form-perfil-configuration">
        <h2>Your Configuration</h2>
        <div class="perfil-config-wrapper">
            <div class="config-input">
                <h2>Perfil Config</h2>
                <div class="username-config-wrapper">
                    <label for="username">Change your username</label>

                    <div class="username-config">
                        <input 
                            v-if="usernameIsEditing"
                            type="text" 
                            name="username" 
                            v-model="newUsername"
                            class="username-info"
                        >

                        <span 
                            class="username-info username-info__span" 
                            v-else
                        >
                            {{ newUsername }}
                        </span>

                        <fa icon="edit" @click="usernameEditClick" />
                        
                    </div>
                </div>


            </div>
            <div>
                <h2>Account Config</h2>
                <div class="wrapper-account__config">
                    <div class="config-input">
                        <div class="email-config">
                            <label for="email">E-mail</label>
                            <div class="email">
                                <input 
                                    type="email" name="email" 
                                    v-model="newEmail" 
                                    v-if="emaiilIsEditing"
                                    class="username-info" 
                                >
                                <span v-else class="username-info username-info__span" >{{ newEmail }}</span>
                                <fa icon="edit" @click="emailEditClick"/>
                            </div>
                        </div>
                    </div>
                    <div class="password-wrapper">
                        <PasswordInput :id="'cPassword'" :password="currentPassword" v-model="currentPassword"
                            placeholder="Type your current Password..." :labelText="'Current Password'" />

                        <PasswordInput :id="'nPassword'" :password="newPassword" v-model="newPassword"
                            placeholder="Type your new Password..." :labelText="'New Password'" />

                        <PasswordInput :id="'confirmNewPassword'" :password="confirmNewPassword"
                            v-model="confirmNewPassword" placeholder="Confirm your new Password..."
                            :labelText="'Confirm your new Password'" />
                    </div>
                </div>
            </div>
        </div>  
        <button class="save_change">Save Changes</button>
        <p class="error-message" v-if="errorMessage">{{ errorMessage }}</p>
    </form>
</template>
