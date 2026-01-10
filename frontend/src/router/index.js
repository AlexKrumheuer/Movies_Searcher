import {createRouter, createWebHistory} from 'vue-router'

import Home from '../pages/Home.vue'
import Release from '../pages/Release.vue'
import Show from '../pages/Show.vue'
import Movie from '../pages/Movie.vue'
import SpecificPage from '../pages/SpecificPage.vue'
import Register from '../pages/auth/Register.vue'
import Login from '../pages/auth/Login.vue'
import Perfil from '../pages/personal/user/Perfil.vue'
import Favorite from '../pages/personal/favorite/Favorite.vue'

const routes = [
    { path: '/', name: 'Home', component: Home},
    { path: '/release', component: Release},
    { path: '/shows', component: Show},
    { path: '/movie', component: Movie},
    { path: '/details/:type/:id', name: 'details', component: SpecificPage, props: true},
    { path: '/register', name: 'register', component: Register},
    { path: '/login', name: "login", component: Login},
    { path: '/perfil', name: "perfil", component: Perfil},
    { path: '/favorite', name: "favorite", component: Favorite}
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

router.beforeEach((to, from, next) => {
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

    const token = localStorage.getItem('token')

    if(requiresAuth && !token) {
        next("/login")
    } else if((to.path === '/login' || to.path === '/register') && token) {
        next('/')
    } else {
        next()
    }
})

export default router