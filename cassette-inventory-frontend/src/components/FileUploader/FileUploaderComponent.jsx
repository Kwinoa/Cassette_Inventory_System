import React, { useState } from 'react'
import styles from '../Edit/Edit.module.css'

const FileUploaderComponent = ({cover_image, setCoverImage, setCoverImageFile, errors, submitted}) => {

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        const previewURL = URL.createObjectURL(file);
        setCoverImage(previewURL);
        setCoverImageFile(file); // send file back to parent form
    };

    return (
        <div>
            {cover_image && (<img className={styles.image} src={cover_image} alt="album cover image"></img>)}
            <label htmlFor="cover_image" >Change Cover Image:</label>
            <input id="cover_image" className={`${styles.image} ${errors && styles.invalid_input}`} type='file' name='cover_image' accept="image/*" onChange={handleFileChange}></input>
            {!cover_image && submitted && <div className={styles.invalid_message}>Image is required</div>}
        </div>
    )
}

export default FileUploaderComponent