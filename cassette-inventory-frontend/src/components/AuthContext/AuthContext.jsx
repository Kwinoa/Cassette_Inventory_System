import { createContext, useState, useEffect } from "react";
import axios from 'axios'

export const AuthContext = createContext();

const apiClient = axios.create({baseURL:'http://localhost:8080'});
export function AuthProvider({ children }) {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [officialEmail, setOfficialEmail] = useState("");
    const [spotifyAuthorized, setSpotifyAuthorized] = useState(false);
    const [accessToken, setAccessToken] = useState('');
    const [cassettes, setCassettes] = useState([]);
    const [player, setPlayer] = useState(undefined);
    const [albumToPlay, setAlbumToPlay] = useState("");
    const [isPlaying, setIsPlaying] = useState(false);
    const [songPosition, setSongPosition] = useState(1);
    

    apiClient.interceptors.response.use((response) => response, (error) => {
        if(error.response && (error.response.status == 401 || error.response.status == 403)){
            console.warn("Unauthorized! Session probably expired");

            sessionStorage.clear();
            
            window.location.href = "/login";
        }
        return Promise.reject(error);
    })
    

    useEffect(() => {
        const checkLoggedIn = sessionStorage.getItem("isLoggedIn");
        if(checkLoggedIn != null){
            setIsLoggedIn(checkLoggedIn);
        }
    })
    return (
        <AuthContext.Provider value={{ accessToken, setAccessToken, isLoggedIn, setIsLoggedIn, firstName, setFirstName, lastName, setLastName, officialEmail, setOfficialEmail, spotifyAuthorized, setSpotifyAuthorized, cassettes, setCassettes,
            player, setPlayer, albumToPlay, setAlbumToPlay, isPlaying, setIsPlaying, songPosition, setSongPosition
        }}>
            {children}
        </AuthContext.Provider>
    );
}
    export default apiClient;