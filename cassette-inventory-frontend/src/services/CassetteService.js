import axios from 'axios';

const api = axios.create({
    baseURL: "http://127.0.0.1:8080",
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

export const login = (formData) => api.post("/api/login", formData);

export const logout = () => api.post("/api/logout");

export const register = (formData) => api.post("/api/register", formData);

export const getSelf = () => api.get("/api/getSelf");

export const uploadImage = (formData) => api.post(
        "/uploadImage", formData, { headers: { "Content-Type": "multipart/form-data" },  withCredentials: true});

export const getAccessToken = (code, codeVerifier) => api.post("/spotify/callback", {code: code, codeVerifier: codeVerifier})

export const getAccessTokenUsingRefresh = () => api.post("/spotify/callback/refresh")

export const updateSpotifyRefreshToken = (refreshToken) => api.patch("/updateRefreshToken", {refreshToken: refreshToken});

export const checkSpotifyRefreshToken = () => api.get("/checkRefreshToken");

export const transferPlayback = (device_id, accessToken) => api.put("/spotify/transfer", {device_ids: [device_id], play: false}, {headers: {'Authorization': 'Bearer ' + accessToken}})

export const changePlaybackAlbum = (albumUri, accessToken, deviceId) => api.put("/spotify/changeAlbum/" + deviceId , {albumUri: albumUri, accessToken: accessToken})

export const setSpotifyAlbumUri = (id, accessToken, artist, album) => api.post("/spotify/setUri", {cassetteId: id, accessToken: accessToken, artist: artist, album: album})

export const setSpotifyShuffleOff = (accessToken, deviceId) => api.post("/spotify/shuffleOff/" + deviceId, {accessToken: accessToken})