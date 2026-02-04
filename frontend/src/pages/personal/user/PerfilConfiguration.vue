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
const isLoading = ref(false) 

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
const emaiilIsEditing = ref(false)

const usernameEditClick = () => usernameIsEditing.value = !usernameIsEditing.value
const emailEditClick = () => emaiilIsEditing.value = !emaiilIsEditing.value

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
        toast.info("No changes detected.");
        return;
    }

    // Validações de frontend
    if (!Validator.validateText(newUsername.value)) {
        errorMessage.value = "Username invalid."
        return
    }
    if (!Validator.validateEmail(newEmail.value)) {
        errorMessage.value = "E-mail invalid."
        return
    }
    if (isSensitiveChange && !currentPassword.value) {
        toast.error("The current password is required to update e-mail.")
        return
    }

    const payload = {
        username: newUsername.value,
        email: newEmail.value,
        currentPassword: currentPassword.value || null,
        newPassword: newPassword.value || null
    };

    isLoading.value = true

    try {
        await api.put("/auth/me", payload)
        
        if (isEmailChanged) {
            toast.info("Verify your verification link sent to your e-mail to complete the update.")
        } else {
            toast.success("Perfil updated successfully!")
        }

        userData.value.username = newUsername.value
        currentPassword.value = ''
        newPassword.value = ''
        confirmNewPassword.value = ''
        
    } catch (error) {
        const apiMessage = error.response?.data

        if (apiMessage) {
            toast.error(apiMessage)
        } else {
            toast.error("Error when updating perfil. Try again later.")
        }
    } finally {
        isLoading.value = false
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
        <button class="save_change" :disabled="isLoading">
            <span v-if="isLoading">
                <fa icon="spinner" spin /> Saving...
            </span>
            <span v-else>Save Changes</span>
        </button>
        <p class="error-message" v-if="errorMessage">{{ errorMessage }}</p>
    </form>
</template>
