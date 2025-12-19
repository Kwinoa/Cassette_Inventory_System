import React, { useState, useEffect } from 'react'
import styles from '../Collection/Collection.module.css'
import { searchSongs, searchSmart } from '../../services/CassetteService'
import CardComponent from '../Card/CardComponent'
import { useSearchParams } from 'react-router-dom'

const SearchComponent = ({ smart }) => {
    const [cassettes, setCassettes] = useState([]);
    const [searchParams] = useSearchParams();
    const query = searchParams.get("query");
    const [loading, setLoading] = useState(true);

    // Get the CassetteService API response and store it into cassettes list
    useEffect(() => {
        console.log("Effect running", query, smart);
        setLoading(true);
        const getResults = async () => {
            try {
                let response;
                if(query !== ""){
                    if (!smart) {
                        response = await searchSongs(query);
                    } else {
                        response = await searchSmart();
                    }
                    console.log(response.data.data);
                    setCassettes(response.data.data);
                }else{
                    setCassettes([]);
                }
            } catch (e) {
                console.log(e);
                setCassettes([]);
            } finally {
                setLoading(false);
            }
        }
        getResults();
    }, [query, smart])

    function loadingMessage() {
        if (loading) {
            return <p>Loading results...</p>
        } else if (!loading && cassettes.length === 0) {
            return <p>No results found</p>
        }
    }
    return (
        <div className={styles.collection_container}>
            <h1>Search Results</h1>
            <ul className={styles.card_container}>
                {cassettes.map((cassette, index) =>
                    <CardComponent cassette={cassette} key={index} />
                )}
            </ul>
            {loadingMessage()}
        </div>
    )
}

export default SearchComponent