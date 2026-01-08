import { ref } from "vue";
import { tbmdService } from "../services/tmbd";

export function useMidia() {
  const itens = ref([]);
  const loading = ref(false);
  const error = ref(null);
  const totalPages = ref(1);
  const totalResults = ref(0);

  const buscarMidia = async (tipo, page, query, category, genreIds) => {
    loading.value = true;
    error.value = null;
    try {
      const data = await tbmdService.getContent(tipo, page, query, category, genreIds);
      totalResults.value = data.total_results;
      totalPages.value = data.total_pages;
      itens.value = data.results.map((item) => ({
        id: item.id,
        title: item.title || item.name,
        type: item.media_type || tipo,
        year: (item.release_date || item.first_air_date || "").split("-")[0],
        img: item.poster_path
          ? `https://image.tmdb.org/t/p/w500${item.poster_path}`
          : null,
      }));
    } catch (err) {
      error.value = "Não foi possível carregar os dados.";
      console.error(err);
    } finally {
      loading.value = false;
    }
  };
  const generos = ref([]);
  const listarGeneros = async (tipo) => {
    try {
      const data = await tbmdService.getGenders(tipo);
      generos.value = data.genres;
    } catch (err) {
      console.error(err);
    }
  };
  return {
    itens,
    loading,
    error,
    totalPages,
    totalResults,
    buscarMidia,
    listarGeneros,
    generos,
  };
}
