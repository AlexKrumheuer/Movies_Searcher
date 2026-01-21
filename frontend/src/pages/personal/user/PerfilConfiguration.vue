<script setup>
import { onMounted, ref } from 'vue'
import PasswordInput from '../../auth/PasswordInput.vue';
import api from '../../../services/api';

const userData = ref({})

onMounted(async () => {
    try {
        const response = await api.get("/auth/me")
        userData.value = response.data
        console.log(userData.value)
    } catch(error) {
        console.error("Error when getting user info")
    }
})

const currentPassword = ref('')
const newPassword = ref('')
const confirmNewPassword = ref('')
</script>
<template>
    <form class="form-perfil-configuration">
        <h2>Your Configuration</h2>
        <div class="perfil-config-wrapper">
            <div class="config-input">
                <h2>Perfil Config</h2>
                <div class="username-config-wrapper">
                    <label for="username">Change your username</label>

                    <div class="username-config">
                        <input type="text" name="username">
                        <fa icon="edit" />

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
                                <input type="email" name="email">
                                <fa icon="edit" />
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

    </form>
</template>
<style>
.form-perfil-configuration {
    color: #fff;
    width: 100vw;
    padding: 3rem;
    display: flex;
    align-items: center;
    flex-direction: column;
    gap: 2rem;
}

.wrapper-account__config {
    display: flex;
    flex-direction: column;
    gap: 4rem;
}

.perfil-config-wrapper {
    display: grid;
    width: 50%;
    grid-template-columns: 1fr 1fr;
    gap: 3rem;
}

.username-config {
    display: grid;
    grid-template-columns: 90% 10%;
    align-items: center;
    gap: 1rem;
}


.config-input {
    display: flex;
    flex-direction: column;
}

.config-input input {
    padding: 0.7rem;
    border-radius: 8px;
}

.config-input input:focus {
    outline: none;
}

.email {
    display: grid;
    grid-template-columns: 90% 10%;
    align-items: center;
    gap: 1rem;
}

.password-wrapper {
    display: flex;
    flex-direction: column;
    gap: 2rem;
}

.save_change {
    width: 30%;
    padding: 1.2rem;
    border-radius: 8px;
    color: #fff;
    background-color: #39A0FF;
    border: 1px solid transparent;
    font-weight: bold;
    cursor: pointer;
    transition: 0.25s;
}

.save_change:hover {
    background-color: #fff;
    color: #39A0FF;
    border: 1px solid #39A0FF;
}
</style>