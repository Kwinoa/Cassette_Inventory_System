import React, { useState, useEffect, useContext } from 'react'
import styles from './Edit.module.css'
import { saveCassette, updateCassette, getCassetteById, deleteCassetteById, uploadImage } from '../../services/CassetteService'
import { genreSet, styleSet } from './GenreAndStyleSet'
import Select from 'react-select'
import { useNavigate, useParams } from 'react-router-dom'
import 'bootstrap/dist/css/bootstrap.min.css'
import FileUploaderComponent from '../FileUploader/FileUploaderComponent'
import axios from "axios";
import {AuthContext} from "../AuthContext/AuthContext"

const EditComponent = () => {

    const {loggedIn} = useContext(AuthContext);
    const { id } = useParams();
    const [cover_image, setCoverImage] = useState('');
    const [coverImageFile, setCoverImageFile] = useState(null);
    const BASE_URL = "http://127.0.0.1:8080";
    const [title, setTitle] = useState('');
    const [year, setYear] = useState('');
    const [genre, setGenre] = useState([]);
    const [style, setStyle] = useState([]);
    const [date, setDate] = useState('');
    const format = "Cassette";
    const track_list = ["track1", "track2", "track3"]

    const [submitted, setSubmitted] = useState(false);

    const [errors, setErrors] = useState({
        loggedIn: '',
        cover_image: '',
        title: '',
        year: '',
        genre: '',
        style: '',
        date: ''
    })

    function validateForm() {
        let valid = true;

        const errorsCopy = { ...errors };

        if(!loggedIn){
            errorsCopy.loggedIn = 'Login is required to modify cassettes';
        }
        if (cover_image) {
            errorsCopy.cover_image = '';
        }
        if (title.trim()) {
            errorsCopy.title = '';
        } else {
            errorsCopy.title = 'Title is required';
            valid = false;
        }
        if (year) {
            errorsCopy.year = '';
        } else {
            errorsCopy.year = 'Year is required';
            valid = false;
        }
        if (genre) {
            errorsCopy.genre = '';
        } else {
            errorsCopy.genre = 'Genre is required';
            valid = false;
        }
        if (style) {
            errorsCopy.style = '';
        } else {
            errorsCopy.style = 'Year is required';
            valid = false;
        }
        if (date.trim()) {
            errorsCopy.date = '';
        } else {
            errorsCopy.date = 'Date is required';
            valid = false;
        }

        setErrors(errorsCopy);

        return valid;
    }

    const navigator = useNavigate();

    async function uploadCoverImage(file) {
        const formData = new FormData();
        formData.append("file", file);
        try{
            const res = await uploadImage(formData);
            return res.data.data;
        } catch(error){
            return null;
        }
    }

    useEffect(() => {
        if (id) {
            getCassetteById(id).then((response) => {
                const data = response.data.data;
                setCoverImage(data.cover_image);
                setTitle(data.title);
                setYear(data.year);
                setDate(data.date);
                setGenre(data.genre)
                setStyle(data.style);
            }).catch(error => {
                console.error(error);
            })
        }
    }, [id])

    async function addCassette(e) {
        e.preventDefault();
        setSubmitted(true);

        if (validateForm()) {
            let finalPath = cover_image;
            if (coverImageFile instanceof File) {
                const uploadedPath = await uploadCoverImage(coverImageFile)
                if(uploadedPath){
                    finalPath = uploadedPath;
                }
            }

            if (id) {
                const cassette = {cover_image: BASE_URL + finalPath, title, year, genre, style, date, format, track_list};
                updateCassette(cassette, id).then((response) => {
                console.log(response.data);
                navigator('/');
                }).catch(error => {
                    console.log(error);
                })
            } else {
                const cassette = {cover_image: finalPath, title, year, genre, style, date, format, track_list};
                saveCassette(cassette).then((response) => {
                console.log(response.data);
                navigator('/');
                }).catch(error => {
                    console.log(error);
                })
            }
        }
    }

    function deleteCassette(e) {
        e.preventDefault();
        deleteCassetteById(id).then((response) => {
            console.log(response.data.data);
            navigator('/');
        }).catch(error => {
            console.log(error);
        })
    }

    function pageTitle() {
        if (id) {
            return <h1>Edit Cassette</h1>
        } else {
            return <h1>Add Cassette</h1>
        }
    }

    return (
        <div className={styles.cassette_edit_container}>
            {
                pageTitle()
            }
            <form className={styles.edit_form}>
                <FileUploaderComponent key={id} cover_image={cover_image} setCoverImage={setCoverImage} setCoverImageFile={setCoverImageFile} submitted={submitted} />
                <div>
                    <div>
                        <label className={styles.edit_label} htmlFor="title">Author & Title<em className={styles.required}>*</em></label>
                        <input className={`${styles.edit_input} form-control ${errors.date ? `is-invalid` : ``}`} id={title} type='text' placeholder='Artist - Title' name='title' value={title} onChange={(e) => {
                            setTitle(e.target.value);
                        }}></input>
                        {errors.title && <div className={styles.invalid_message}>{errors.title}</div>}
                    </div>
                    <div>
                        <label className={styles.edit_label} htmlFor="year">Year<em className={styles.required}>*</em></label>
                        <input className={`${styles.edit_input} form-control ${errors.date ? `is-invalid` : ``}`} id={year} type='number' placeholder="YYYY" name='year' value={year} min="1900" max="2025" onChange={(e) => {
                            setYear(e.target.value);
                        }}></input>
                        {errors.year && <div className={styles.invalid_message}>{errors.year}</div>}
                    </div>
                    <div>
                        <label className={styles.edit_label} htmlFor="genres">Genres<em className={styles.required}>*</em></label>
                        {/* <CassetteCardLabelComponent labels={genres} labelName="Genres:" /> */}
                        <Select
                            id="genre"
                            className={`${styles.select} form-control ${errors.date ? `is-invalid` : ``}`}
                            classNamePrefix="select_valid_input"
                            value={
                                (genre ?? []).map(g => ({
                                    label: g,
                                    value: g
                                }))
                            }
                            options={genreSet}
                            onChange={(selectedGenres) =>
                                setGenre(selectedGenres ? selectedGenres.map(g => g.value) : [])
                            }
                            placeholder="Select Genres"
                            isMulti
                        />
                        {(!genre || genre.length == 0) && submitted && <div className={styles.invalid_message}>Genre is required</div>}
                    </div>
                    <div>
                        <label className={styles.edit_label} htmlFor="style">Styles<em className={styles.required}>*</em></label>
                        {/* <CassetteCardLabelComponent labels={style} labelName="Styles:" /> */}
                        <Select
                            id={style}
                            className={`${styles.select} form-control ${errors.date ? `is-invalid` : ``}`}
                            classNamePrefix="select_valid_input"
                            value={
                                (style ?? []).map(g => ({
                                    label: g,
                                    value: g
                                }))
                            }
                            options={styleSet}
                            onChange={(selectedStyles) => {
                                setStyle(selectedStyles ? selectedStyles.map(s => s.value) : [])
                            }} placeholder='Select Styles' isMulti />
                        {(!style || style.length == 0) && submitted && <div className={styles.invalid_message}>Style is required</div>}
                    </div>
                    <div>
                        <label className={styles.edit_label} htmlFor="date">Date Added<em className={styles.required}>*</em></label>
                        <input className={`${styles.edit_input} form-control ${errors.date ? `is-invalid` : ``}`} id={date} type='date' name='date' value={date} onChange={(e) => {
                            setDate(e.target.value);
                        }}></input>
                        {errors.date && <div className={styles.invalid_message}>{errors.date}</div>}
                    </div>
                    <div className={styles.buttons_container}>
                        <button className={styles.submit_button} onClick={addCassette}>Submit</button>
                        <button className={styles.delete_button} onClick={deleteCassette}>Delete</button>
                    </div>
                    {errors.loggedIn && <div className={styles.invalid_message}>{errors.loggedIn}</div>}
                </div>
            </form>
        </div>
    )
}

export default EditComponent