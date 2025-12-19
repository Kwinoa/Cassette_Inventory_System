import React, { useEffect, useState, useContext } from 'react'
import { getUserCassettes } from '../../services/CassetteService';
import styles from './Collection.module.css'
import CardComponent from '../Card/CardComponent';
import { useNavigate, useLocation } from 'react-router-dom'
import {AuthContext} from "../AuthContext/AuthContext"

const CollectionComponent = () => {

    // Javscript function that passes initial value of useState to create
    // a state variable array and function to set state variable
    const {isLoggedIn, firstName, lastName} = useContext(AuthContext);
    const [cassettes, setCassettes] = useState([]);
    const [originalCassettes, setOriginalCassettes] = useState([]);
    const [inventoryQuery, setInventoryQuery] = useState("");
    const [updateCollection, setUpdateCollection] = useState(false);

    const navigator = useNavigate();

    // Get the CassetteService API response and store it into cassettes list
    useEffect(() => {
        if(isLoggedIn){
            getUserCassettes().then((response) => {
                setCassettes(response.data.data);
                setOriginalCassettes(response.data.data);
            }).catch(error => {
                console.error(error);
            })
        }
    }, [])

    function goToAddPage() {
        navigator('/add-cassette/');
    }

    function filterInventory(e) {
        e.preventDefault();
        const query = e.target.value.trim();
        setInventoryQuery(query);
        if (query !== "" && query.length > 0 ) {
            console.log("searching...", query);
            setCassettes(originalCassettes.filter(cassette => cassette.title.trim().toLowerCase().includes(query.trim().toLowerCase())));
            console.log(cassettes);
        }else{
            setCassettes(originalCassettes);
        }
    }

    return (
        <div className={styles.collection_container}>
            {isLoggedIn ? <h1>{firstName}'s Cassette Collection</h1> : <h1>Cassette Collection</h1>}
            <div>
                <form>
                    <label className={styles.hidden_label}>Search Collection:</label>
                    <input name="search" className={styles.collection_input} onChange={(e) => {filterInventory(e);}}></input>
                    {/* <button className={styles.search_button} onClick={filterInventory}><svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><g id="SVGRepo_bgCarrier" strokeWidth="0"></g><g id="SVGRepo_tracerCarrier" strokeLinecap="round" strokeLinejoin="round" stroke="#CCCCCC" strokeWidth="0.096"></g><g id="SVGRepo_iconCarrier"> <path d="M11 6C13.7614 6 16 8.23858 16 11M16.6588 16.6549L21 21M19 11C19 15.4183 15.4183 19 11 19C6.58172 19 3 15.4183 3 11C3 6.58172 6.58172 3 11 3C15.4183 3 19 6.58172 19 11Z" stroke="#000000" strokeWidth="1.032" strokeLinecap="round" strokeLinejoin="round"></path> </g></svg></button> */}
                </form>
                <button className={styles.add_button} onClick={goToAddPage}>
                    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" stroke="#000000" strokeWidth="0.0001000000000000003"><g id="SVGRepo_bgCarrier" strokeWidth="0"></g><g id="SVGRepo_tracerCarrier" strokeLinecap="round" strokeLinejoin="round" stroke="#CCCCCC" strokeWidth="0.6890000000000001"> <path d="M3.5 11C3.5 9.10025 3.50106 7.72573 3.64199 6.67754C3.78098 5.64373 4.04772 5.00253 4.52513 4.52513C5.00253 4.04772 5.64373 3.78098 6.67754 3.64199C7.72573 3.50106 9.10025 3.5 11 3.5H13C14.8998 3.5 16.2743 3.50106 17.3225 3.64199C18.3563 3.78098 18.9975 4.04772 19.4749 4.52513C19.9523 5.00253 20.219 5.64373 20.358 6.67754C20.4989 7.72573 20.5 9.10025 20.5 11V13C20.5 14.8998 20.4989 16.2743 20.358 17.3225C20.219 18.3563 19.9523 18.9975 19.4749 19.4749C18.9975 19.9523 18.3563 20.219 17.3225 20.358C16.2743 20.4989 14.8998 20.5 13 20.5H11C9.10025 20.5 7.72573 20.4989 6.67754 20.358C5.64373 20.219 5.00253 19.9523 4.52513 19.4749C4.04772 18.9975 3.78098 18.3563 3.64199 17.3225C3.50106 16.2743 3.5 14.8998 3.5 13V11Z" stroke="#222222"></path> <path d="M12 8L12 16" stroke="#222222" strokeLinejoin="round"></path> <path d="M16 12L8 12" stroke="#222222" strokeLinejoin="round"></path> </g><g id="SVGRepo_iconCarrier"> <path d="M3.5 11C3.5 9.10025 3.50106 7.72573 3.64199 6.67754C3.78098 5.64373 4.04772 5.00253 4.52513 4.52513C5.00253 4.04772 5.64373 3.78098 6.67754 3.64199C7.72573 3.50106 9.10025 3.5 11 3.5H13C14.8998 3.5 16.2743 3.50106 17.3225 3.64199C18.3563 3.78098 18.9975 4.04772 19.4749 4.52513C19.9523 5.00253 20.219 5.64373 20.358 6.67754C20.4989 7.72573 20.5 9.10025 20.5 11V13C20.5 14.8998 20.4989 16.2743 20.358 17.3225C20.219 18.3563 19.9523 18.9975 19.4749 19.4749C18.9975 19.9523 18.3563 20.219 17.3225 20.358C16.2743 20.4989 14.8998 20.5 13 20.5H11C9.10025 20.5 7.72573 20.4989 6.67754 20.358C5.64373 20.219 5.00253 19.9523 4.52513 19.4749C4.04772 18.9975 3.78098 18.3563 3.64199 17.3225C3.50106 16.2743 3.5 14.8998 3.5 13V11Z" stroke="#222222"></path> <path d="M12 8L12 16" stroke="#222222" strokeLinejoin="round"></path> <path d="M16 12L8 12" stroke="#222222" strokeLinejoin="round"></path> </g></svg>
                </button>
            </div>
            <ul className={styles.card_container}>
                {cassettes && cassettes.map(cassette =>
                    <CardComponent cassette={cassette} key={cassette.id} />
                )}
            </ul>
        </div>
    )
}

export default CollectionComponent;