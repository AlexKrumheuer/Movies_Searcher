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
    const nameChanged = userData.value.username !== newUsername.value
    const emailChanged = userData.value.email !== newEmail.value
    const passwordChanging = newPassword.value !== ''

    if (!nameChanged && !emailChanged && !passwordChanging) {
        toast.info("Nenhuma alteração foi detectada.")
        return
    }
    if(!Validator.validateText(userData.value.username)) {
        console.error("Invalid username")
        errorMessage.value = "Invalid username"
        return
    }
    if(!Validator.validateEmail(newEmail.value)) {
        console.error("Invalid email")
        errorMessage.value = "Invalid email"

        return
    }
    if(newPassword.value || confirmNewPassword.value) {
        if(!Validator.validateEqualsPassword(newPassword.value, confirmNewPassword.value)) {
            console.error("New passwords do not match")
            errorMessage.value = "New passwords do not match"

            return
        }
        if(!Validator.validatePassword(newPassword.value)) {
            console.error("Invalid new password")
            errorMessage.value = "Invalid new password"
            return
        }
    }

    const isTryingToChangePassword = newPassword.value !== '' || confirmNewPassword.value !== ''
    
    if (isTryingToChangePassword && currentPassword.value === '') {
        toast.error("Para definir uma nova senha, você precisa informar a senha atual.")
        return
    }

    if (isTryingToChangePassword && !Validator.validateEqualsPassword(newPassword.value, confirmNewPassword.value)) {
        toast.error("As novas senhas não coincidem.")
        return
    }

    const shouldSendPassword = isTryingToChangePassword && currentPassword.value


    let passwordTypeForEmailChange = false
    if(userData.value.email !== newEmail.value) {
        if(currentPassword.value === '') {
            console.error("Current password is required to change email")
            errorMessage.value = "Current password is required to change email"
            return
        } else {
            passwordTypeForEmailChange = true
        }
    }
    let passwordAltered = confirmNewPassword.value !== '' && newPassword.value !== '' && currentPassword.value !== ''
    try {
        const payload = {
            username: newUsername.value,
            email: newEmail.value,
            currentPassword:(userData.value.email !== newEmail.value || shouldSendPassword) 
                             ? currentPassword.value 
                             : null,
            newPassword: passwordAltered ? newPassword.value : null,
            confirmNewPassword: passwordAltered ? confirmNewPassword.value : null
        }
        console.log(payload)
        const response = await api.put("/auth/me", payload)
        errorMessage.value = ''
        if(userData.value.email !== newEmail.value) {
            toast.info("Please confirm your new email address through the link sent to your email.")
        }
        toast.success("User info updated successfully")
        currentPassword.value = ''
        newPassword.value = ''
        confirmNewPassword.value = ''
    } catch(error) {
        const status = error.response?.status
        const backendMessage = error.response?.data?.message

        if (status === 401 || status === 403) {
            toast.error("A senha atual informada está incorreta.")
        } else if (status === 409) {
            toast.error(backendMessage || "Este nome de usuário ou e-mail já está em uso.")
        } else if (status === 400) {
            toast.warning("Verifique os dados informados: " + (backendMessage || "Dados inválidos."))
        } else {
            toast.error("Ops! Ocorreu um erro no servidor. Tente mais tarde.")
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
