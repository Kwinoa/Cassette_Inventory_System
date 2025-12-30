import React, {useEffect, useRef, useState, useContext} from 'react'
import { useNavigate } from 'react-router-dom'
import { getAccessToken, getAccessTokenUsingRefresh, updateSpotifyRefreshToken, transferPlayback, changePlaybackAlbum, setSpotifyShuffleOff } from '../../services/CassetteService'
import {AuthContext} from "../AuthContext/AuthContext"
import styles from "./SpotifyPlayer.module.css"
import axios from 'axios'
import Select from 'react-select'

const SpotifyPlayerComponent = () => {

    const {isLoggedIn, spotifyAuthorized, setSpotifyAuthorized, accessToken, setAccessToken, player, setPlayer} = useContext(AuthContext)
    const {cassettes, setCassettes, albumToPlay, setAlbumToPlay, isPlaying, setIsPlaying, songPosition, setSongPosition} = useContext(AuthContext);
    const [cassetteSet, setCassetteSet] = useState([]);
    const [deviceId, setDeviceId] = useState('');

    const [trackList, setTrackList] = useState([]);
    const [playedSongs, setPlayedSongs] = useState([]);
    const [foundCassette, setFoundCassette] = useState({cover_image: ""});
    const [coverImage, setCoverImage] = useState("");
    const playerRef = useRef(null);
    const prevTrackUri = useRef(null);
    const tokenRef = useRef(accessToken);

    const navigate = useNavigate();
    const hasCalled = useRef(false); // Prevents React StrictMode from running this twice

    useEffect(() => { 
        tokenRef.current = accessToken; 
    }, [accessToken]);

    useEffect(() => {
        console.log(deviceId);

        const token = sessionStorage.getItem("access_token");
        if(token && !accessToken){
            setAccessToken(token);
        }

        const code = retrieveCode();
        if(code && !hasCalled.current && !spotifyAuthorized) {
            hasCalled.current = true;
            requestAccessToken(code);
        }

        const cassetteCache = JSON.parse(sessionStorage.getItem("cassette_cache"));
        if(cassetteCache){
            setCassettes(cassetteCache.data);
        }

        if(albumToPlay){
            if(cassettes){
                const found = cassettes?.find(c => c.title == albumToPlay.label);
                console.log("Found Cassette:", found.cover_image);
                if(found){
                    setFoundCassette(cassettes?.find(c => c.title == albumToPlay.label));
                    const trackList = found.track_List || [];
                    setTrackList(trackList);
                }
            }
        }

        if(isPlaying){
            play();
        }
    }, [])

    useEffect(() => {
        const cassetteData = JSON.parse(sessionStorage.getItem("cassette_cache"));
        if(cassetteData){
            try{
                const cassetteCache = cassetteData.data;

                if(Array.isArray(cassetteCache)){
                    const titles = cassetteCache.filter(c => (c.albumUri !== "0" && c.albumUri !== null)).map(c => ({
                        value: c.albumUri,
                        label: c.title
                    }));
                    setCassetteSet(titles);
                }
            }catch(error){
                console.log("cassette parse error: ", error);
            }
        }
    }, [isLoggedIn, spotifyAuthorized, cassettes, accessToken])

    useEffect(() => {  
        const token = sessionStorage.getItem("access_token");
        if(token){
            setAccessToken(token);
        }
        
        if(!token || !isLoggedIn || !spotifyAuthorized || playerRef.current) return;

        if(playerRef.current){
            playerRef.current.disconnect();
        }

        window.onSpotifyWebPlaybackSDKReady = () => {
            const player = new window.Spotify.Player({
                name: 'Web Playback SDK',
                getOAuthToken: cb => { cb(tokenRef.current) },
                volume: 0.3
            });

            playerRef.current = player;
            setPlayer(player);

            player.addListener('ready', ({ device_id }) => {
                console.log('Ready with Device ID', device_id);
                sessionStorage.setItem("device_id", device_id);
                setDeviceId(device_id);

                setIsPlaying(false);
                setSpotifyShuffleOff(token, device_id).then((response) => {
                    console.log(response.data);
                });
                transferPlayback(device_id, token);
            });

            player.addListener('not_ready', ({ device_id }) => {
                console.log('Device ID has gone offline', device_id);
            });

            player.addListener('player_state_changed', state => {                
                if (!state) {
                    console.error('User is not playing music through the Web Playback SDK');
                    return;
                }

                setIsPlaying(!state.paused);
                console.log("Paused?:", state.paused)

                const currentTrack = state.track_window.current_track;
                const currentUri = currentTrack.uri;
                    
                if (prevTrackUri.current !== currentUri) {                    
                    const isBackward = state.track_window.next_tracks.some(t => t.uri === prevTrackUri.current);
                    const isForward = state.track_window.previous_tracks.some(t => t.uri === prevTrackUri.current);

                    if (isForward) {
                        console.log("Song position:", songPosition);
                        setSongPosition(prev => prev + 1);
                    } else if (isBackward) {
                        console.log("Song position:", songPosition)
                        setSongPosition(prev => prev - 1);
                    }

                    prevTrackUri.current = currentUri;
                }

                setIsPlaying(!state.paused);
            });

            player.setName("Casssette Inventory Player").then(() => {
                console.log('Player name updated!');
            });

            player.on('playback_error', ({ message }) => {
                console.error('Failed to perform playback', message);
            });

            player.on('account_error', ({ message }) => {
                console.error('Failed to validate Spotify account', message);
            });

            player.on('authentication_error', ({ message }) => {
                console.error('Failed to authenticate', message);
            });

            player.on('initialization_error', ({ message }) => {
                console.error('Failed to initialize', message);
            });

            player.connect();
            playerRef.current = player;
        };

        if(window.Spotify){
            window.onSpotifyWebPlaybackSDKReady();
        } else {
            if(!document.getElementById("spotify_sdk")){
                const script = document.createElement("script");
                script.id ="spotify_sdk"
                script.src = "https://sdk.scdn.co/spotify-player.js";
                script.async = true;
                document.body.appendChild(script);
            }
        }
    }, [isLoggedIn, spotifyAuthorized, accessToken]);

    useEffect(() => {
        if(isLoggedIn && spotifyAuthorized){
            const device = sessionStorage.getItem("device_id");
            setDeviceId(device);

            const existingToken = sessionStorage.getItem("access_token");

            if(!existingToken){
                console.log("refreshing access token...");
                refreshAccessToken();
            } else {
                console.log("Token already exists, skipping refresh");
            }
        }
    }, [isLoggedIn, spotifyAuthorized]);

    const CLIENT_ID = "9d3227f170f3420ca40575eedd592d29";
    const REDIRECT_URI = "http://127.0.0.1:3000";
    const SCOPE = 'streaming user-modify-playback-state user-read-private user-read-email';
    const AUTH_URL = new URL("https://accounts.spotify.com/authorize");

    const generateRandomString = (length) => {
        const possible = 'LKKER93KHG092309484JKSDAFLERW332KDIE985K2F92E';
        const values = crypto.getRandomValues(new Uint8Array(length));
        return values.reduce((acc, x) => acc + possible[x % possible.length], "");
    }

    const hashCode = async (plain) => {
        const encoder = new TextEncoder()
        const data = encoder.encode(plain)
        return window.crypto.subtle.digest('SHA-256', data)
    }
    
    const base64encode = (input) => {
        return btoa(String.fromCharCode(...new Uint8Array(input)))
            .replace(/=/g, '')
            .replace(/\+/g, '-')
            .replace(/\//g, '_');
    }

    const requestAuthorization = async () => {
        console.log("requesting authorization...");

        const codeVerifier  = generateRandomString(45);
        window.sessionStorage.setItem('code_verifier', codeVerifier);

        const hashed = await hashCode(codeVerifier);

        const codeChallenge = base64encode(hashed);

        const params = new URLSearchParams({
            code_challenge: codeChallenge,
            code_challenge_method: 'S256',
            scope: SCOPE,
            redirect_uri: REDIRECT_URI, 
            client_id: CLIENT_ID,
            response_type: 'code',
        });

        AUTH_URL.search = new URLSearchParams(params).toString();
        console.log(AUTH_URL.toString());
        window.location.href = AUTH_URL.toString();
    }

    const retrieveCode = () => {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('code');
    };

    const requestAccessToken = async (code) => {
        console.log("retrieving access token...");

        window.history.replaceState({}, document.title, "/");
        const codeVerifier = sessionStorage.getItem('code_verifier');

        const expiresAt = sessionStorage.getItem('token_expiration');
        const now = new Date().getTime();

        if(!expiresAt || now > (expiresAt - 60000)){
            try {
                const response = await getAccessToken(code, codeVerifier);
                const data = response.data.data;
                console.log(response.data);
    
                sessionStorage.setItem('access_token', data.access_token);
                setAccessToken(data.access_token);

                const expiration_time = new Date().getTime() + (data.expires_in * 1000);
                sessionStorage.setItem('token_expiration', expiration_time);
    
                updateSpotifyRefreshToken(response.data.data.refresh_token).then((response) => {console.log(response);});
                setSpotifyAuthorized(true);
            } catch (error) {
                console.error("Token exchange failed:", error);
            }
        }else{
            if(spotifyAuthorized){
                refreshAccessToken();
            }
        }
    }

    const refreshAccessToken = async () => {
        const expiresAt = sessionStorage.getItem('token_expiration');
        const now = new Date().getTime();

        if(!expiresAt || now > (expiresAt - 60000)){
            try{
                const response = await getAccessTokenUsingRefresh()
                console.log(response);
                const access_token = response.data.data.access_token;
                
                if(access_token){
                    const expiration_time = new Date().getTime() + (response.data.data.expires_in * 1000);
                    sessionStorage.setItem('token_expiration', expiration_time);
                    
                    setAccessToken(response.data.data.access_token);
                    sessionStorage.setItem('access_token', response.data.data.access_token);

                    setSpotifyAuthorized(true);
                }
                
            } catch(error) {
                console.log("refresh token error: ", error);
                setSpotifyAuthorized(false);
            }
        }
    }
    
    function handleAlbumChange(song){
        console.log("album has changed");
        const option = {value: song.value, label: song.label}
        setAlbumToPlay(option);

        if(cassettes){
            const found = cassettes?.find(c => c.title == song.label);
            console.log("Found Cassette:", found.cover_image);
            if(found){
                setFoundCassette(found);
                setCoverImage(found.cover_image);
                const trackList = found.track_List || [];
                setTrackList(trackList);
            }
        }

        if(deviceId){
            player.activateElement()
            setIsPlaying(true);
            setSongPosition(1);                        
            changePlaybackAlbum(song.value, accessToken, deviceId);
        }
    }
    
    const pause = () => {
        if(player){
            setIsPlaying(false);
            player.pause().then(() => {
                console.log('Paused!');
            });
        }
    }

    const play = () => {
        if(player){
            setIsPlaying(true);
            player.activateElement();
            player.resume().then(() => {
                console.log('Resumed!');
            });
        }
    }

    function handlePlayPause(e){
        e.preventDefault();
        if(accessToken){
            console.log("Playing", albumToPlay.label, "...");
            if(isPlaying){
                pause();
            } else if(!isPlaying){
                play();
            }
        }
    }

    function handleRewind(e){
        e.preventDefault(); 
        player.previousTrack().then(() => {
            console.log('Set to previous track!');
        })
    }

    function handleForward(e){
        e.preventDefault();
        player.nextTrack().then(() => {
            console.log('Skipped to next track!');
        })
    }

    async function loginWithSpotify(){
        await requestAuthorization();
    }

    const customSelectStyles = {
    control: (provided, state) => ({
        ...provided,
        backgroundColor: 'transparent', // Makes the main box transparent
        border: 'none',       // Optional: keep a border so it's visible
        color: 'black',
        height: '4rem',
    }),
    singleValue: (provided) => ({
        ...provided,
        color: 'rgba(67, 66, 66, 0.75)',                 // The color of the text when an item is selected
    }),
    placeholder: (provided) => ({
        ...provided,
        color: 'rgba(67, 66, 66, 0.75)', // Faded color for placeholder
    }),
    menu: (provided) => ({
        ...provided,
        border: 'none',
        backgroundColor: '#243c4cae',
    }),
    option: (provided, state) => ({
        ...provided,
        backgroundColor: state.isFocused ? 'rgba(255, 255, 255, 0.56)' : 'transparent',
        color: 'white',
        cursor: 'pointer',
    }),
};

    return (
        <div className={styles.player_container}>
            <div className={styles.cassette_container}>
                <div>
                    <div className={styles.cassette_cover}>
                        <div className={styles.cassette_label}>
                            {/* <h2>A</h2> */}
                            <ul>
                                {trackList.map((track, i) => (
                                    <li key={i} className={`${songPosition-1 === i ? styles.highlight : ''}`}>{track}</li>
                                ))}
                            </ul>
                        </div>
                        <div className={styles.cassette_wheels}>
                            <button className={` ${isPlaying ? styles.spinning : styles.wheel}`}></button>
                            {spotifyAuthorized ? <Select
                                styles={customSelectStyles}
                                value={albumToPlay}
                                className={styles.song_select}  
                                options={cassetteSet}
                                onChange={(song) => handleAlbumChange(song)}
                                placeholder='Select Album' /> : <button className={styles.spotify_login} onClick={loginWithSpotify}>Login To Spotify</button>}
                            <button className={`${isPlaying ? styles.spinning : styles.wheel}`}></button>
                        </div>
                    </div>
                    <div className={styles.cassette_controls}>
                        <button className={styles.rewind} onClick={handleRewind}></button>
                        <button className={`${isPlaying ? styles.pause : styles.play}`} onClick={handlePlayPause}></button>
                        <button className={styles.forward} onClick={handleForward}></button>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default SpotifyPlayerComponent