import React, { useEffect, useState } from 'react'
import {searchSongs} from '../../services/CassetteService'
import cassetteShell from '../../assets/Shellcolor_BlackSolid_MB0222_300W-optimized.webp'

const PlayerComponent = () => {
    const [cassette, setCassette] = useState(null);
    const [query, setQuery] = useState(null);

    useEffect(() => {
        setQuery("Britney spears - circus");
    }, [query])

    useEffect(() => {
        console.log("Trying...")
        if (!query) return;

        searchSongs(query).then((response) => {
            setCassette(response.data.data);
        }).catch(error => {
            console.error("Error fetching cassette:", error);
        })
    }, [query])

    return (
        <div className="cassette-container">
            {cassette ? (
                <>
                    <img className="cassette-shell" src={cassetteShell} alt="cassette"></img>
                    <div className="label-container"><img className="label" src={cassette.cover_image} alt="cassette"></img></div>
                </>
            ) : (
                <p>Loading cassette...</p>
            )}
        </div>
    )
}

export default PlayerComponent;