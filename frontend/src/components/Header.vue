<script setup></script>

<template>
    <header>
        <img class="logo" src="../../public/img/logo.png" alt="">
        <div class="container-icons">
            <fa class="icons" icon="bars" />
            <fa class="icons" icon="user"/>
        </div>
    </header>
</template>

<style>
    header {
        background-color: #0A0A10;
        padding: 1rem 5rem;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .logo {
        width: 20%;
        cursor: pointer;
    }

    .container-icons {
        display: flex;
        gap: 3rem;
    }

    .icons {
        color: #fff;
        font-size: 2rem;
        cursor: pointer;
        transition: hover;
    }

    .icons:hover {
        color: #39A0FF;
    }
</style>