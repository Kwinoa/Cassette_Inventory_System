import axios from 'axios';

const REST_API_BASE_URL = 'http://localhost:8080';

const api = axios.create({
    baseURL: "http://localhost:8080",
    withCredentials: true
});

export const getUserCassettes = () => api.get("/getUserCassettes");

export const saveCassette = (cassette) => api.post("/saveCassette", cassette);

export const updateCassette = (cassette, id) => api.put("/cassette/" + id, cassette)

export const getCassetteById = (id) => api.get("/cassette/" + id);

export const deleteCassetteById = (id) => api.delete("/delete/" + id);

export const searchSongs = (querySearch) => api.get("/search", {
    params: {
        query: querySearch
    }
});

export const searchSmart = () => api.get("/smartSearch");

export const login = (formData) => api.post("/api/auth/login", formData);

export const logout = () => api.post("/api/auth/logout");

export const register = (formData) => api.post("/api/auth/register", formData);

export const getSelf = () => api.get("/api/auth/getSelf");

export const uploadImage = (formData) => api.post(
        "/uploadImage", formData, { headers: { "Content-Type": "multipart/form-data" },  withCredentials: true});

