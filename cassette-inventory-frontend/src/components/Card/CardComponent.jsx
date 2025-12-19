import React from 'react'
import styles from './Card.module.css'
import LabelComponent from '../Label/LabelComponent';
import { useNavigate } from 'react-router-dom'
import { saveCassette } from '../../services/CassetteService'

const CardComponent = ({ cassette }) => {

    const navigator = useNavigate();

    function goToEditPage() {
        navigator('/edit-cassette/' + cassette.id);
    }

    function addCassette(e) {
        e.preventDefault();
        console.log(cassette);
        saveCassette(cassette).then((response) => {
            console.log(response.data.data);
            navigator('/');
        }).catch(error => {
            console.log(error);
        })
    }

    function editOrAddButton() {
        if (cassette && cassette.id == 0) {
            return <button className={styles.add_button} onClick={addCassette}>
                <svg viewBox="0 0 1024 1024" fill="#000000" version="1.1" xmlns="http://www.w3.org/2000/svg"><g id="SVGRepo_bgCarrier" strokeWidth="0"></g><g id="SVGRepo_tracerCarrier" strokeLinecap="round" strokeLinejoin="round"></g><g id="SVGRepo_iconCarrier"><path d="M512 980c-12.8 0-24-11.2-24-24V536H67.2c-12.8 0-24-11.2-24-24s10.4-24 24-24H488V67.2c0-12.8 11.2-24 24-24s24 10.4 24 24V488h420c12.8 0 24 11.2 24 24s-10.4 24-24 24H536v420c0 12.8-11.2 24-24 24z" fill=""></path></g></svg>            </button>
        } else if (cassette && cassette.id != 0) {
            return <button className={styles.edit_button} onClick={goToEditPage}><svg viewBox="0 0 24.00 24.00" fill="none" xmlns="http://www.w3.org/2000/svg" transform="rotate(0)matrix(1, 0, 0, 1, 0, 0)" stroke="#243C4C"><g id="SVGRepo_bgCarrier" strokeWidth="0"></g><g id="SVGRepo_tracerCarrier" strokeLinecap="round" strokeLinejoin="round"></g><g id="SVGRepo_iconCarrier"> <path d="M21.2799 6.40005L11.7399 15.94C10.7899 16.89 7.96987 17.33 7.33987 16.7C6.70987 16.07 7.13987 13.25 8.08987 12.3L17.6399 2.75002C17.8754 2.49308 18.1605 2.28654 18.4781 2.14284C18.7956 1.99914 19.139 1.92124 19.4875 1.9139C19.8359 1.90657 20.1823 1.96991 20.5056 2.10012C20.8289 2.23033 21.1225 2.42473 21.3686 2.67153C21.6147 2.91833 21.8083 3.21243 21.9376 3.53609C22.0669 3.85976 22.1294 4.20626 22.1211 4.55471C22.1128 4.90316 22.0339 5.24635 21.8894 5.5635C21.7448 5.88065 21.5375 6.16524 21.2799 6.40005V6.40005Z" stroke="#243C4C" strokeWidth="1.152" strokeLinecap="round" strokeLinejoin="round"></path> <path d="M11 4H6C4.93913 4 3.92178 4.42142 3.17163 5.17157C2.42149 5.92172 2 6.93913 2 8V18C2 19.0609 2.42149 20.0783 3.17163 20.8284C3.92178 21.5786 4.93913 22 6 22H17C19.21 22 20 20.2 20 18V13" stroke="#243C4C" strokeWidth="1.152" strokeLinecap="round" strokeLinejoin="round"></path> </g></svg></button>
        } else {
            return <></>
        }
    }

    return (
        <li key={cassette.id} className={styles.card}>
            <img src={cassette.cover_image} alt="cassette album thumbnail" />
            <div className={styles.card_info}>
                <div>
                    <h3 className={styles.title}>{cassette.title}</h3>
                    {editOrAddButton()}
                </div>
                <p className={styles.year}>{cassette.year}</p>
                <LabelComponent labels={cassette.genre} labelName="Genres:" />
                <LabelComponent labels={cassette.style} labelName="Styles:" />
                <p className={styles.date_added}>Added {cassette.date}</p>
            </div>
        </li>
    )
}

export default CardComponent