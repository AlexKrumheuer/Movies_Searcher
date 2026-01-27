import { ref } from "vue"
import api from "../services/api"

export function useRating(tmdbId, videoType) {
  const currentRating = ref(0)
  const isLoading = ref(false)

  const getRating = async () => {
    try {
      const response = await api.get(`/api/rating/${tmdbId}`)
      
      if (response.status === 204) {
          currentRating.value = 0
      } else if (response.data) {
          currentRating.value = response.data.rating || 0
      }
    } catch (error) {
      if (error.response && error.response.status !== 404) {
        console.error("Error searching for rating:", error)
      }
    }
  };

  const setRating = async (newValue) => {
    if (isLoading.value) return
    isLoading.value = true

    try {
      if (currentRating.value === newValue) {
          await api.delete(`/api/rating/${tmdbId}`)
          currentRating.value = 0
      } 
      else {
          const payload = {
            rating: newValue,
            tmdbId: Number(tmdbId),
            videoType: videoType
          };
          await api.post("/api/rating", payload)
          currentRating.value = newValue
      }
    } catch (e) {
      console.error("Error when rating:", e)
    } finally {
      isLoading.value = false
    }
  };


  return {
    currentRating,
    getRating,
    setRating,
  };
}