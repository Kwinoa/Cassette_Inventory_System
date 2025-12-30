import './App.css'
import {useState, useEffect, useContext} from 'react'
import CollectionComponent from './components/Collection/CollectionComponent'
import HeaderComponent from './components/Header/HeaderComponent'
import PlayerComponent from './components/Player/PlayerComponent'
import EditComponent from './components/Edit/EditComponent'
import SearchComponent from './components/Search/SearchComponent'
import LoginComponent from './components/Login/LoginComponent'
import RegisterComponent from './components/Register/RegisterComponent'
import {BrowserRouter, Routes, Route} from 'react-router-dom'
import {checkSpotifyRefreshToken} from "./services/CassetteService.js"
import {AuthContext} from "./components/AuthContext/AuthContext"
import axios from 'axios'
import ProfileComponent from './components/Profile/ProfileComponent.jsx'

function App() {
  const {isLoggedIn, setIsLoggedIn} = useContext(AuthContext);
  const {spotifyAuthorized, setSpotifyAuthorized} = useContext(AuthContext);
  
  useEffect(() => {
    const interceptor = axios.interceptors.response.use(
        (response) => response, // Do nothing if the request is successful
        (error) => {
            // Check for 401 (Unauthorized) or 403 (Forbidden)
            // Or 500 (if you want to force login after that 'Connection Reset' error)
            if (error.response && (error.response.status === 401 || error.response.status === 403)) {
                console.warn("API rejected request. Forcing re-authentication...");
                
                setIsLoggedIn(false);
                setSpotifyAuthorized(false);

                sessionStorage.clear();

                navigate("/logout"); 
              }
              return Promise.reject(error);
          }
      );

      // Cleanup: Remove the interceptor if the component unmounts
      return () => axios.interceptors.response.eject(interceptor);
  }, [setIsLoggedIn, setSpotifyAuthorized]);

  useEffect(() => {
      if(isLoggedIn){
        checkSpotifyRefreshToken().then((response) => {
          if(response.status == 200){
            setSpotifyAuthorized(response.data.data);
          }
        }).catch((error) => {console.log(error);});
      }
  }, [isLoggedIn]);

  return (
    <>
    <BrowserRouter>
      <HeaderComponent/>
        <Routes>
          <Route path='/' element = {<CollectionComponent />}></Route>
          <Route path='/cassettes' element = {<CollectionComponent/>}></Route>
          <Route path='/edit-cassette/:id' element={<EditComponent/>}></Route>
          <Route path='/add-cassette' element={<EditComponent/>}></Route>
          <Route path='/search' element={<SearchComponent smart={false}/>}></Route>
          <Route path='/smart-search' element={<SearchComponent smart={true}/>}></Route>
          <Route path='/login' element={<LoginComponent/>}></Route>
          <Route path='/register' element={<RegisterComponent/>}></Route>
          <Route path='/profile' element={<ProfileComponent/>}></Route>
        </Routes>
      {/* <CassettePlayerComponent/>
      <CassetteCardComponent/> */}
    </BrowserRouter>
    </>
  )
}

export default App
