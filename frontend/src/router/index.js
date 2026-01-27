import {createRouter, createWebHistory} from 'vue-router'

const routes = [
    { 
        path: '/',
        name: 'Home', 
        component: () => import('../pages/Home.vue')
    },
    { 
        path: '/release', 
        component: () => import('../pages/Release.vue')
    },
    { 
        path: '/shows', 
        component: () => import('../pages/Show.vue')
    },
    { 
        path: '/movie', 
        component: () => import('../pages/Movie.vue')
    },
    { 
        path: '/details/:type/:id', 
        name: 'details', 
        props: true,
        component: () => import('../pages/SpecificPage.vue')
    },
    { 
        path: '/register', 
        name: 'register', 
        component: () => import('../pages/auth/Register.vue')
    },
    { 
        path: '/login', 
        name: "login", 
        component: () => import('../pages/auth/Login.vue')
    },
    { 
        path: '/perfil', 
        name: "perfil", 
        meta: {requiredAuth: true },
        component: () => import('../pages/personal/user/Perfil.vue')
    },
    { 
        path: '/favorite', 
        name: "favorite", 
        meta: { requiredAuth: true },
        component: () => import('../pages/personal/favorite/Favorite.vue')
    },
    { 
        path: '/watched', 
        name: "watched", 
        meta: { requiredAuth: true },
        component: () => import('../pages/personal/watched/Watched.vue')
    },
    { 
        path: '/watch_list', 
        name: "watch_list", 
        meta: { requiredAuth: true },
        component: () => import('../pages/personal/watchList/WatchList.vue')
    },
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