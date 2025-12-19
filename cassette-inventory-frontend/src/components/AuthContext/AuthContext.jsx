import { createContext, useState } from "react";

export const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [officialEmail, setOfficialEmail] = useState("");

    return (
        <AuthContext.Provider value={{ isLoggedIn, setIsLoggedIn, firstName, setFirstName, lastName, setLastName, officialEmail, setOfficialEmail }}>
            {children}
        </AuthContext.Provider>
    );
}