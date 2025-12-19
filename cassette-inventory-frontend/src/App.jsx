import './App.css'
import {useState} from 'react'
import CollectionComponent from './components/Collection/CollectionComponent'
import HeaderComponent from './components/Header/HeaderComponent'
import PlayerComponent from './components/Player/PlayerComponent'
import EditComponent from './components/Edit/EditComponent'
import SearchComponent from './components/Search/SearchComponent'
import LoginComponent from './components/Login/LoginComponent'
import RegisterComponent from './components/Register/RegisterComponent'
import {BrowserRouter, Routes, Route} from 'react-router-dom'

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  

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
        </Routes>
      {/* <CassettePlayerComponent/>
      <CassetteCardComponent/> */}
    </BrowserRouter>
    </>
  )
}

export default App
