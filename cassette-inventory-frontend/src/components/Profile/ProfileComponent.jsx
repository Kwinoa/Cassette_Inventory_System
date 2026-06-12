import React, { useContext, useState, useEffect } from 'react';
import { AuthContext } from '../AuthContext/AuthContext';
import PieDataChart from '../DashboardData/PieDataChart';
import AreaDataChart from '../DashboardData/AreaDataChart';
import styles from './Profile.module.css';
import {getDashboardData, getSelf} from "../../services/CassetteService";

const monthlyData = [
    { name: 'Jan', cassettes: 2 },
    { name: 'Feb', cassettes: 4 },
    { name: 'Mar', cassettes: 3 },
    { name: 'Apr', cassettes: 6 },
    { name: 'May', cassettes: 5 },
    { name: 'Jun', cassettes: 8 },
    { name: 'Jul', cassettes: 7 },
    { name: 'Aug', cassettes: 5 },
    { name: 'Sep', cassettes: 4 },
    { name: 'Oct', cassettes: 6 },
    { name: 'Nov', cassettes: 9 },
    { name: 'Dec', cassettes: 7 }
];

const ProfileComponent = () => {
    const {firstName, lastName, officialEmail} = useContext(AuthContext);
    const [totalCassettes, setTotalCassettes] = useState(0);
    const [yearsSinceJoined, setYearsSinceJoined] = useState(0);
    const [favoriteArtist, setFavoriteArtist] = useState('');
    const [favoriteDecade, setFavoriteDecade] = useState('');
    const [largestCassette, setLargestCassette] = useState('');
    const [genreDist, setGenreDist] = useState();
    const [styleDist, setStyleDist] = useState();
    const [monthDist, setMonthDist] = useState();
    
    useEffect(() => {
        getDashboardData().then(response => {
            const data = response.data.data;
            setTotalCassettes(data.totalCassettes);
            setYearsSinceJoined(data.yearsSinceJoined.toFixed(2));
            setFavoriteArtist(data.favoriteArtist);
            setFavoriteDecade(data.favoriteDecade);
            setLargestCassette(data.largestCassette);
            
            const monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]
            const monthList = [];
            Object.entries(data.monthDistribution).forEach((arr) => {
                const monthMap = {num: arr[1].month, name: monthNames[arr[1].month - 1], cassettes: arr[1].cassette_count};
                monthList.push(monthMap);
            });
            monthList.sort((left, right) => left.num - right.num);
            const genreList = [];
            Object.entries(data.genreDistribution).map((arr) => {
                const genreMap = {name:arr[0], value:arr[1]};
                genreList.push(genreMap);
            })
            const styleList = [];
            Object.entries(data.styleDistribution).map((arr) => {
                const styleMap = {name:arr[0], value:arr[1]};
                styleList.push(styleMap);
            })
            // code to get monthly cassette purchase distribution for the year (backend not implemented yet)
            setMonthDist(monthList);
            setGenreDist(genreList);
            setStyleDist(styleList);
        });
    }, []);

    return (
        <section className={styles.profilePage}>
            <div className={styles.topCard}>
                <div className={styles.userRow}>
                    <div className={styles.userInfo}>
                        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" stroke="#243C4C"><g id="SVGRepo_bgCarrier" strokeWidth="0"></g><g id="SVGRepo_tracerCarrier" strokeLinecap="round" strokeLinejoin="round"></g><g id="SVGRepo_iconCarrier"> <path d="M14.5 8.5C14.5 9.88071 13.3807 11 12 11C10.6193 11 9.5 9.88071 9.5 8.5C9.5 7.11929 10.6193 6 12 6C13.3807 6 14.5 7.11929 14.5 8.5Z" fill="#243C4C"></path> <path d="M15.5812 16H8.50626C8.09309 16 7.87415 15.5411 8.15916 15.242C9.00598 14.3533 10.5593 13 12.1667 13C13.7899 13 15.2046 14.3801 15.947 15.2681C16.2011 15.5721 15.9774 16 15.5812 16Z" fill="#243C4C" stroke="#243C4C" strokeWidth="0.696" strokeLinecap="round" strokeLinejoin="round"></path> <circle cx="12" cy="12" r="10" stroke="#243C4C" strokeWidth="0.696"></circle> </g></svg>
                        <div>
                            <h1 className={styles.name}>{firstName} {lastName}</h1>
                            <p className={styles.email}>{officialEmail}</p>
                        </div>
                    </div>    
                    <div className={styles.statsRow}>
                        <div className={styles.statBlock}>
                            <p className={styles.statValue}>{totalCassettes}</p>
                            <p className={styles.statLabel}>Total cassettes</p>
                        </div>
                        <div className={styles.statBlock}>
                            <p className={styles.statValue}>{yearsSinceJoined}</p>
                            <p className={styles.statLabel}>Years since joined</p>
                        </div>
                    </div>
                </div>


                <div className={styles.areaCard}>
                    <h2 className={styles.cardTitle}>Monthly Collection Activity</h2>
                    <div className={styles.areaChartWrap}>
                        <AreaDataChart data={monthDist} />
                    </div>
                </div>
            </div>

            <div className={styles.bottomCard}>
                <div className={styles.favoriteItem}>
                    <p className={styles.favoriteLabel}>Favorite Artist</p>
                    <p className={styles.favoriteValue}>{favoriteArtist}</p>
                </div>

                <div className={styles.favoriteItem}>
                    <p className={styles.favoriteLabel}>Favorite Decade</p>
                    <p className={styles.favoriteValue}>{favoriteDecade}</p>
                </div>

                <div className={styles.favoriteItem}>
                    <p className={styles.favoriteLabel}>Largest Cassette (# tracks)</p>
                    <p className={styles.favoriteValue}>{largestCassette}</p>
                </div>
            </div>

            <div className={styles.pieGrid}>
                <div className={styles.pieCard}>
                    <h2 className={styles.cardTitle}>Genre Distribution</h2>
                    <div className={styles.pieChartWrap}>
                        <PieDataChart data={genreDist} />
                    </div>
                </div>

                <div className={styles.pieCard}>
                    <h2 className={styles.cardTitle}>Style Distribution</h2>
                    <div className={styles.pieChartWrap}>
                        <PieDataChart data={styleDist} />
                    </div>
                </div>
            </div>

        </section>
    );
};

export default ProfileComponent