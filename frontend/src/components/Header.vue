<script setup>
    // Header component



    
    import "../style/index.css"
    import { ref, onMounted, onUnmounted } from 'vue'
    import api from '../services/api.js'
    import {useRouter} from 'vue-router'
    import { onClickOutside } from '@vueuse/core'


    const userProfileContainer = ref(null); 
    const router = useRouter()
    let showUser = ref(false)
    const open = () => {
        showUser.value = !showUser.value
    }
    onClickOutside(userProfileContainer, () => {
        showUser.value = false;
    });


    const isLoggedIn = ref(!!localStorage.getItem('token'))

    const updateLoginStatus = () => {
        isLoggedIn.value = !!localStorage.getItem('token')
    }

    onMounted(() => {
        window.addEventListener("auth-change", updateLoginStatus)
    })

    onUnmounted(() => {
        window.removeEventListener('auth-change', updateLoginStatus)
    })

    const logout = async () => {
        try {
            await api.post("/auth/logout")
        } catch(e) {
            console.error("Erro ao validar token no servidor", error)
        } 
        finally {
            localStorage.removeItem('token')
            window.dispatchEvent(new Event('auth-change'))
            showUser.value = false
            router.push("/login")
        }
    }
</script>

<template>
    <header>
        <img class="logo" src="../../public/img/logo.png" alt="">
        <div class="container-icons">
            <div class="user-menu-wrapper">
                <fa ref="userProfileContainer" class="icons" icon="user" @click="open" />
                
                <nav v-if="showUser" class="user-dropdown">
                    <ul>
                        <template v-if="!isLoggedIn">
                            <li><router-link @click="showUser = false" to="/login">Entrar</router-link></li>
                            <li><router-link @click="showUser = false" to="/register">Criar Conta</router-link></li>
                        </template>
                        <template v-else>
                            <li><router-link @click="showUser = false"  to="/perfil">Meu Perfil</router-link></li>
                            <li><router-link @click="showUser = false" to="/favorite">Favoritos</router-link></li>
                            <li @click="logout" class="logout-btn">Sair</li>
                        </template>
                    </ul>
                </nav>
            </div>

        </div>
    </header>
</template>
<style>
.user-menu-wrapper {
    position: relative;
}

.user-dropdown {
    position: absolute;
    right: 0;
    top: 100%;
    background-color: #1C1C23;
    border: 1px solid #39A0FF;
    border-radius: 8px;
    padding: 1rem;
    z-index: 1000;
    min-width: 150px;
}

.user-dropdown ul {
    list-style: none;
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.user-dropdown a {
    color: #FFFFFF;
    text-decoration: none;
    font-size: 1.4rem;
}

.user-dropdown a:hover {
    color: #39A0FF;
}

.logout-btn {
    color: #FFFFFF;
    cursor: pointer;
    font-size: 1.4rem;
    transition: color 0.3s;
}

.logout-btn:hover {
    color: #ff4d4d; 
}

@media (max-width: 768px) {
    .user-dropdown {
        position: fixed;
        top: 0;
        right: 0;
        height: 100vh;
        width: 70%; 
        border-radius: 0;
        box-shadow: -5px 0 15px rgba(0,0,0,0.5);
    }
}
</style>
