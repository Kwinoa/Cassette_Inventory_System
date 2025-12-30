import React, {useState, useEffect, useContext} from 'react'
import styles from './Header.module.css'
import {useNavigate} from 'react-router-dom'
import {getSelf, logout} from "../../services/CassetteService"
import {AuthContext} from "../AuthContext/AuthContext"

const HeaderComponent = () => {

  const {setIsLoggedIn, setFirstName, setLastName, setOfficialEmail } = useContext(AuthContext);
  const {isLoggedIn, firstName, lastName, player} = useContext(AuthContext);
  const [query, setQuery] = useState("");

  const navigator = useNavigate();

  useEffect(() => {
    if(isLoggedIn){
      getSelf().then((response) => {
        setIsLoggedIn(true);
        setFirstName(response.data.firstName);
        setLastName(response.data.lastName);
        setOfficialEmail(response.data.email);
      }).catch(() => {
        setIsLoggedIn(false);
        setFirstName('');
        setLastName('');
        setOfficialEmail('');
      })
    }
  }, [firstName])

  function loginOrLogoutButton() {
      if (isLoggedIn) {
        return <button onClick={handleLogout} className={styles.login}><a>Logout</a></button>
      } else {
        return <button onClick={goToLoginPage}><a>Login</a></button>
      }
  }

  async function handleLogout(e){
    e.preventDefault();
    logout().then((response) => {
          if(response.status == 200)
            sessionStorage.clear();
            player.disconnect();

            setIsLoggedIn(false);
            setFirstName('');
            setLastName('');
            setOfficialEmail('');

            navigator("/login");
      }).catch(error => {
        console.log(error);
      })
  }

  function goToHomePage() {
    navigator('/')
  }

  function goToSearchPage(e){
    e.preventDefault();
    console.log(`/search?query=${encodeURIComponent(query)}`);
    navigator(`/search?query=${encodeURIComponent(query)}`);
  }

  function gotToSmartSearchPage(e){
    e.preventDefault();
    navigator(`/smart-search`);
  }

  function goToLoginPage(e){
    e.preventDefault();
    navigator('/login');
  }

  function goToProfilePage(e){
    e.preventDefault();
    navigator('/profile');
  }

  return (
    <header>
      <nav>
        <button className={styles.logo} onClick={goToHomePage}>
          <svg viewBox="0 -11 64 64" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlnsXlink="http://www.w3.org/1999/xlink" xmlns:sketch="http://www.bohemiancoding.com/sketch/ns" fill="#000000"><g id="SVGRepo_bgCarrier" strokeWidth="0"></g><g id="SVGRepo_tracerCarrier" strokeLinecap="round" strokeLinejoin="round"></g><g id="SVGRepo_iconCarrier"> <title>Cassette</title> <desc>Created with Sketch.</desc> <defs> </defs> <g id="Page-1" stroke="none" strokeWidth="1" fill="none" fillRule="evenodd" sketch:type="MSPage"> <g id="Cassette" sketch:type="MSLayerGroup" transform="translate(1.000000, 1.000000)" stroke="#4b2e2e" strokeWidth="2"> <path d="M62,38 C62,39.1 61.1,40 60,40 L2,40 C0.9,40 0,39.1 0,38 L0,2 C0,0.9 0.9,0 2,0 L12,0 L19.2,7 L43.9,7 L51.2,0 L60,0 C61.1,0 62,0.9 62,2 L62,38 L62,38 Z" id="Shape" sketch:type="MSShapeGroup"> </path> <path d="M15,30 L47.7,30" id="Shape" sketch:type="MSShapeGroup"> </path> <path d="M18,1 L45,1" id="Shape" sketch:type="MSShapeGroup"> </path> <circle id="Oval" sketch:type="MSShapeGroup" cx="15" cy="23" r="7"> </circle> <circle id="Oval" sketch:type="MSShapeGroup" cx="47" cy="23" r="7"> </circle> </g> </g> </g></svg>
        </button>
        <form className={styles.search}>
          <label className={styles.label}>Search:</label>
          <input name={query} type="text" value={query} onChange={(e) => {setQuery(e.target.value)}}></input>
          <button className={styles.search_button}onClick={goToSearchPage}><svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><g id="SVGRepo_bgCarrier" strokeWidth="0"></g><g id="SVGRepo_tracerCarrier" strokeLinecap="round" strokeLinejoin="round" stroke="#CCCCCC" strokeWidth="0.096"></g><g id="SVGRepo_iconCarrier"> <path d="M11 6C13.7614 6 16 8.23858 16 11M16.6588 16.6549L21 21M19 11C19 15.4183 15.4183 19 11 19C6.58172 19 3 15.4183 3 11C3 6.58172 6.58172 3 11 3C15.4183 3 19 6.58172 19 11Z" stroke="#000000" strokeWidth="1.032" strokeLinecap="round" strokeLinejoin="round"></path> </g></svg></button>
          <button className={styles.smart_button} onClick={gotToSmartSearchPage}><svg viewBox="-3 0 32 32" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlnsXlink="http://www.w3.org/1999/xlink" fill="#000000" transform="matrix(-1, 0, 0, 1, 0, 0)" stroke="#000000" strokeWidth="0.00082"><g id="SVGRepo_bgCarrier" strokeWidth="0"></g><g id="SVGRepo_tracerCarrier" strokeLinecap="round" strokeLinejoin="round"></g><g id="SVGRepo_iconCarrier"> <g id="icomoon-ignore"> </g> <path d="M-0.007 28.236l13.916-13.916 0.754 0.754-13.916 13.916-0.754-0.754z" fill="#000000"> </path> <path d="M9.973 10.453h4.267v1.067h-4.267v-1.067z" fill="#000000"> </path> <path d="M21.707 10.453h4.267v1.067h-4.267v-1.067z" fill="#000000"> </path> <path d="M17.44 14.72h1.067v4.267h-1.067v-4.267z" fill="#000000"> </path> <path d="M17.44 2.987h1.067v4.267h-1.067v-4.267z" fill="#000000"> </path> <path d="M23.991 5.717l-3.017 3.017-0.754-0.754 3.017-3.017 0.754 0.754z" fill="#000000"> </path> <path d="M23.246 17.042l-3.017-3.017 0.754-0.754 3.017 3.017-0.754 0.754z" fill="#000000"> </path> <path d="M14.986 8.741l-3.017-3.017 0.754-0.754 3.017 3.017-0.754 0.754z" fill="#000000"> </path> </g></svg></button>
        </form>
        <div>
          {isLoggedIn && <button className={styles.profile} onClick={goToProfilePage}><svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" stroke="#243C4C"><g id="SVGRepo_bgCarrier" strokeWidth="0"></g><g id="SVGRepo_tracerCarrier" strokeLinecap="round" strokeLinejoin="round"></g><g id="SVGRepo_iconCarrier"> <path d="M14.5 8.5C14.5 9.88071 13.3807 11 12 11C10.6193 11 9.5 9.88071 9.5 8.5C9.5 7.11929 10.6193 6 12 6C13.3807 6 14.5 7.11929 14.5 8.5Z" fill="#243C4C"></path> <path d="M15.5812 16H8.50626C8.09309 16 7.87415 15.5411 8.15916 15.242C9.00598 14.3533 10.5593 13 12.1667 13C13.7899 13 15.2046 14.3801 15.947 15.2681C16.2011 15.5721 15.9774 16 15.5812 16Z" fill="#243C4C" stroke="#243C4C" strokeWidth="0.696" strokeLinecap="round" strokeLinejoin="round"></path> <circle cx="12" cy="12" r="10" stroke="#243C4C" strokeWidth="0.696"></circle> </g></svg></button>}
          {loginOrLogoutButton()}
        </div>
      </nav>
    </header>
  )
}

export default HeaderComponent