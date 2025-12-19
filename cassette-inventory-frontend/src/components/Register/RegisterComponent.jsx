import {useState, useEffect} from 'react'
import styles from '../Register/Register.module.css'
import { useNavigate } from "react-router-dom";
import { register } from '../../services/CassetteService'

const RegisterComponent = () => {

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    
    const [isChecked, setIsChecked] = useState(false);
    const [showPassConditions, setShowPassConditions] = useState(false);
    const validations = {
        length: password.length >= 8,
        digit: /\d/.test(password),
        uppercase: /[A-Z]/.test(password),
        lowercase: /[a-z]/.test(password),
        special: /[!@#$%^*(\)\_+=[\]{}":\.]/.test(password),
    };
    const ConditionItem = ({ isMet, label }) => (
        <div className={styles.condition}>
            <div>{isMet ? "✓" : "✗"}</div>
            <p>{label}</p>
        </div>
    );

    const [errors, setErrors] = useState({
        email: '',
        password: '',
        firstName: '',
        lastName: ''
    });

    useEffect(()=> {
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
        if(password && (!validations.length || !validations.digit || !validations.lowercase || !validations.uppercase || !validations.special)){
            errorsCopy.password = "Password conditions not met"
        }
        if (firstName) {
            errorsCopy.firstName = '';
        } else {
            errorsCopy.firstName = 'First name is required';
        }
        if (lastName) {
            errorsCopy.lastName = '';
        } else {
            errorsCopy.lastName = 'Last name is required';
        }

        setErrors(errorsCopy);
    }, [firstName, lastName, email, password])
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
        if(password && (!validations.length || !validations.digit || !validations.lowercase || !validations.uppercase || !validations.special)){
            errorsCopy.password = "Password conditions not met"
            valid = false;
        }
        if (firstName) {
            errorsCopy.firstName = '';
        } else {
            errorsCopy.firstName = 'firstName is required';
            valid = false;
        }
        if (lastName) {
            errorsCopy.lastName = '';
        } else {
            errorsCopy.lastName = 'lastName is required';
            valid = false;
        }

        setErrors(errorsCopy);

        return valid;
    }
    
    const navigator = useNavigate();
    
    async function handleSubmit(e){
        e.preventDefault();
        if (validateForm()) {
            const formData = { email, password, firstName, lastName};
            console.log(formData);
            register(formData).then((response) => {
                console.log(response.data);
                if(response.status == 200)
                    navigator("/login");
            }).catch(error => {
                if(error.response && error.response.data){
                    const message = error.response.data
                    console.log(message);
                    if(message.includes("Email")){
                        let errorsCopy = {...errors};
                        errorsCopy.email = message;
                        setErrors(errorsCopy);
                    }
                }
            })
        }
    }

    function handleCheckedToggle(){
        setIsChecked(!isChecked);
    }

    function goToLogin(){
        navigator("/login");
    }
    return (
        <div className={styles.cassette_register_container}>
            <h1>Register</h1>
            <form id="loginForm" onSubmit={handleSubmit} className={styles.register_form}>
                <div>
                    <label className={styles.register_label} htmlFor="email">Email<em className={styles.required}>*</em></label>
                    <input className={`${styles.register_input} form-control ${errors.email ? `is-invalid` : ``}`} id="email" type='email' name='email' onChange={(e) => {
                        setEmail(e.target.value);
                    }}></input>
                    {errors.email && <div className={styles.invalid_message}>{errors.email}</div>}
                </div>
                <div>
                    <label className={styles.register_label} htmlFor="password">Password<em className={styles.required}>*</em></label>
                    <input className={`${styles.register_input} form-control ${errors.password ? `is-invalid` : ``}`} id="password" type={isChecked ? 'text' : 'password'} name='password' onFocus={(e) => {setShowPassConditions(true)}} onBlur={(e) => {setShowPassConditions(false)}}onChange={(e) => {
                        setPassword(e.target.value);
                    }}></input>
                    {errors.password && <div className={styles.invalid_message}>{errors.password}</div>}
                    <label>
                        <input type="checkbox" className={styles.checkbox} checked={isChecked} onChange={handleCheckedToggle}></input>
                        Show Password
                    </label>
                    {showPassConditions && 
                        <div className={styles.condition_container}>
                            <ConditionItem isMet={validations.length} label="At least 8 characters"/>
                            <ConditionItem isMet={validations.digit} label="At least 1 digit"/>
                            <ConditionItem isMet={validations.uppercase} label="At least 1 uppercase"/>
                            <ConditionItem isMet={validations.lowercase} label="At least 1 lowercase"/>
                            <ConditionItem isMet={validations.special} label='At least 1 special character: !@#$%^*.()_+=[]{}":'/>
                        </div>}
                </div>
                <div>
                    <label className={styles.register_label} htmlFor="firstName">First Name<em className={styles.required}>*</em></label>
                    <input className={`${styles.register_input} form-control ${errors.firstName ? `is-invalid` : ``}`} id="firstName" type='text' p name='firstName' onChange={(e) => {
                        setFirstName(e.target.value);
                    }}></input>
                    {errors.firstName && <div className={styles.invalid_message}>{errors.firstName}</div>}
                </div>
                <div>
                    <label className={styles.register_label} htmlFor="lastName">Last Name<em className={styles.required}>*</em></label>
                    <input className={`${styles.register_input} form-control ${errors.lastName ? `is-invalid` : ``}`} id="lastName" type='text'  name='lastName' onChange={(e) => {
                        setLastName(e.target.value);
                    }}></input>
                    {errors.lastName && <div className={styles.invalid_message}>{errors.lastName}</div>}
                </div>
                <div className={styles.register_button_container}>
                    <button type="submit" className={styles.register_button}>Register</button>
                    <p>Already have an account? <a onClick={goToLogin} className={styles.register_link}>Login</a></p>
                </div>
            </form>
        </div>
    )
}

export default RegisterComponent