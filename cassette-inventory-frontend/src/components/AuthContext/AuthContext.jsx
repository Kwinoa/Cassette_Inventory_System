import { createContext, useState, useEffect } from "react";
import { getSelf } from "../../services/CassetteService";

export const AuthContext = createContext();

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
    

    // On every page load (including Spotify redirects), ask the server if the
    // session is still valid. The session cookie is sent automatically via
    // withCredentials, so no client-side storage is needed.
    useEffect(() => {
        getSelf().then((response) => {
            setIsLoggedIn(true);
            setFirstName(response.data.firstName);
            setLastName(response.data.lastName);
            setOfficialEmail(response.data.email);
        }).catch((error) => {
            setIsLoggedIn(false);
            sessionStorage.clear();
        });
    }, [])
    
    return (
        <AuthContext.Provider value={{ accessToken, setAccessToken, isLoggedIn, setIsLoggedIn, firstName, setFirstName, lastName, setLastName, officialEmail, setOfficialEmail, spotifyAuthorized, setSpotifyAuthorized, cassettes, setCassettes,
            player, setPlayer, albumToPlay, setAlbumToPlay, isPlaying, setIsPlaying, songPosition, setSongPosition
        }}>
            {children}
        </AuthContext.Provider>
    );
}
