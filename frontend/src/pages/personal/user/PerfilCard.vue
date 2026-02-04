<script setup>
import { onMounted, ref } from 'vue'
import api from '../../../services/api';
import { useToast } from 'vue-toastification';

const userData = ref({})

onMounted(async () => {
    try {
        const response = await api.get("/auth/me")
        userData.value = response.data
    } catch(error) {
        console.error("Error when getting user info")
    }
})

const toast = useToast();

const isUploading = ref(false)

const handleProfileUpload = async (event) => {
    const file = event.target.files[0]
    if (!file) return

    isUploading.value = true;

    if (file.size > 2 * 1024 * 1024) {
        toast.error("The file size exceeds the 2MB limit.");
        return;
    }

    const formData = new FormData();
    formData.append('file', file)

    try {
        const response = await api.post('/auth/me/profile-image', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });

        userData.value.profileImageUrl = response.data.profileImageUrl
        toast.success("Perfil Image updated successfully!");
    } catch (error) {
        const msg = error.response?.data?.message || "Error in cloudinary upload."
        toast.error(msg)
    } finally {
        isUploading.value = false;
    }
}

const bannerInput = ref(null)
const isUploadingBanner = ref(false);
const defaultProfile = '/img/user-default.png'

const handleBannerUpload = async (event) => {
    const file = event.target.files[0]
    if (!file) return

    if (file.size > 5 * 1024 * 1024) { 
        toast.error("The banner size exceeds the 5MB limit.")
        return;
    }
    isUploadingBanner.value = true;
    const formData = new FormData()
    formData.append('file', file)

    try {
        const response = await api.post('/auth/me/banner-image', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        })

        userData.value.bannerUrl = response.data.bannerUrl
        toast.success("Banner updated successfully!")
    } catch (error) {
        const msg = error.response?.data?.message || "Error in banner upload."
        toast.error(msg)
    } finally {
        isUploadingBanner.value = false;
    }
}

</script>
<template>
    <div class="perfil-info" :style="{ backgroundImage: `url(${userData.bannerUrl || bannerPadrao})` }">
        <div v-if="isUploadingBanner" class="banner-loading-bar"></div>
    
        <div v-if="isUploadingBanner" class="banner-overlay">
            <fa icon="sync" spin /> <span>Uploading your banner...</span>
        </div>
        <div class="edit-banner" @click="$refs.bannerInput.click()">
            <fa icon="pencil-alt" /> <span>Edit Banner</span>
        </div>
        <input 
            type="file" 
            ref="bannerInput" 
            @change="handleBannerUpload" 
            style="display: none" 
            accept="image/*"
        />

        <div class="perfil-image-container">
            <div v-if="isUploading" class="loading-overlay">
                <fa icon="spinner" spin />
            </div>
            <img 
                class="perfil-image" 
                :src="userData.profileImageUrl == 'no_image' ? defaultProfile : userData.profileImageUrl"
                @click="$refs.fileInput.click()"
                alt="User profile"
            >
            <input 
                type="file" 
                ref="fileInput" 
                @change="handleProfileUpload" 
                style="display: none" 
                accept="image/*"
            />
        </div>
        
        <h2>{{ userData.username }}</h2>
    </div>
</template>
