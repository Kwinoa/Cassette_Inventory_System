import ReactDOM from 'react-dom/client'
import React from 'react';
import App from './App'
import './index.css'
import {AuthProvider} from "./components/AuthContext/AuthContext"

ReactDOM.createRoot(document.getElementById('root')).render(
    <AuthProvider>
        <App />
    </AuthProvider>
)
