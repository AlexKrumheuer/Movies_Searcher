const baseUrl = "http://localhost:8080/api/tmdb";

const fetchFromBackend = async (endpoint, params = {}) => {
    const queryString = new URLSearchParams(params).toString();
    const url = `${baseUrl}${endpoint}?${queryString}`;
    
    console.log(`Chamando Backend: ${url}`);
    
    const res = await fetch(url);
    if(!res.ok) throw new Error(`Erro no backend: ${res.status}`);
    return await res.json();
}

export const tbmdService = {
    
    getContent: (type, page = 1, query = "", category = "popular", genreIds = []) => {
        if(query) {
            return fetchFromBackend(`/${type}/search`, {
                query: query,
                page: page
            });
        }
        const params = {
            page: page,
            category: category 
        };

        if(genreIds && genreIds.length > 0) {
            params.idsGenres = Array.isArray(genreIds) ? genreIds.join(',') : genreIds;
        }

        return fetchFromBackend(`/${type}/discover`, params);
    },
    getDetails: (type, id) => {
        return fetchFromBackend(`/${type}/${id}`);
    },
    
    getGenders: (type) => {
        return fetchFromBackend(`/genre/${type}/list`);
    }
}