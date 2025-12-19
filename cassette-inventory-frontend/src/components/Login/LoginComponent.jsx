import {useState, useContext, useEffect} from 'react'
import styles from '../Login/Login.module.css'
import { useNavigate } from "react-router-dom";
import { login } from '../../services/CassetteService'
import {AuthContext} from "../AuthContext/AuthContext"

const LoginComponent = () => {

    const {setIsLoggedIn, setFirstName, setLastName, setOfficialEmail} = useContext(AuthContext);
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [isChecked, setIsChecked] = useState(false);

    const [errors, setErrors] = useState({
        email: '',
        password: '',
    });

    useEffect(() => {
        const errorsCopy = { ...errors };

        if (email.trim()) {
            errorsCopy.email = '';
        } else{
            errorsCopy.email = "Email is required";
        }
        if (password) {
            errorsCopy.password = '';
        } else {
            errorsCopy.password = 'Password is required';
        }

        setErrors(errorsCopy);
    }, [email, password])

    function validateForm(){
        let valid = true;

        const errorsCopy = { ...errors };

        if (email.trim()) {
            errorsCopy.email = '';
        } else{
            errorsCopy.email = "Email is required";
            valid = false;
        }
        if (password) {
            errorsCopy.password = '';
        } else {
            errorsCopy.password = 'Password is required';
            valid = false;
        }

        setErrors(errorsCopy);

        return valid;
    }
    
    const navigator = useNavigate();
    
    async function handleSubmit(e){
        e.preventDefault();

        if (validateForm()) {
            const formData = { email, password};
            login(formData).then((response) => {
                if(response.status == 200)
                    setIsLoggedIn(true);
                    setFirstName(response.data.firstName);
                    setLastName(response.data.lastName);
                    setOfficialEmail(response.data.email);

                    navigator("/");
            }).catch(error => {
                console.log(error);
            })
        }
    }

    function handleCheckedToggle(){
        setIsChecked(!isChecked);
    }

    function goToRegister(e){
        e.preventDefault();
        navigator("/register")
    }

    return (
        <div className={styles.cassette_login_container}>
            <h1>Login</h1>
            <form id="loginForm" onSubmit={handleSubmit} className={styles.login_form}>
                <div>
                    <label className={styles.login_label} htmlFor="email">Email</label>
                    <input className={`${styles.login_input} form-control ${errors.email ? `is-invalid` : ``}`} id="email" type='text' name='email' onChange={(e) => {
                        setEmail(e.target.value);
                    }}></input>
                    {errors.email && <div className={styles.invalid_message}>{errors.email}</div>}
                </div>
                <div>
                    <label className={styles.login_label} htmlFor="password">Password</label>
                    <input className={`${styles.login_input} form-control ${errors.password ? `is-invalid` : ``}`} id="password" type={isChecked ? 'text' : 'password'} name='password' onChange={(e) => {
                        setPassword(e.target.value);
                    }}></input>
                    {errors.password && <div className={styles.invalid_message}>{errors.password}</div>}
                    <label>
                        <input type="checkbox" className={styles.checkbox} checked={isChecked} onChange={handleCheckedToggle}></input>
                        Show Password
                    </label>
                </div>
                <div className={styles.login_button_container}>
                    <button type="submit" className={styles.login_button}>Login</button>
                    <p>New User? <a onClick={goToRegister} className={styles.register_link}>Register</a></p>
                </div>
            </form>
        </div>
    )
}

export default LoginComponent