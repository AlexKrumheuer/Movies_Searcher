import { ref } from 'vue'
import api from '../services/api'

export function useMediaList(endpoint, tmdbId) {
    const isAdded = ref(false)
    const isLoading = ref(false)

    const checkStatus = async () => {
        try {
            const response = await api.get(endpoint)
            const list = response.data
            isAdded.value = list.some(item => item.tmdbId === Number(tmdbId))
        } catch (error) {
            console.error(`Error when creating ${endpoint}:`, error)
        }
    };

    const toggle = async (mediaData) => {
        if (isLoading.value) return;
        isLoading.value = true

        if (isAdded.value) {
            try {
                await api.delete(`${endpoint}/${tmdbId}`)
                isAdded.value = false
            } catch (e) {
                console.error("Error when removing:", e)
            } finally {
                isLoading.value = false;
            }
            return;
        }
        try {
            await api.post(endpoint, mediaData);
            isAdded.value = true;
        } catch (e) {
            if (e.response && e.response.status === 409) {
                isAdded.value = true; 
            } else if (e.response && e.response.status === 403) {
                alert("Expired Session. Sign in again.");
            } else {
                alert("Error when saving: " + (e.response?.data || "Unkown error"));
            }
        } finally {
            isLoading.value = false;
        }
    };

    return {
        isAdded,
        isLoading,
        checkStatus,
        toggle
    };


}