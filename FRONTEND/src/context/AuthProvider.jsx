import { createContext, useContext } from 'react';
import useLocalStorage from "../hooks/useLocalStorage.jsx";
import {useNavigate} from "react-router-dom";

const USERS = [
    {
        username: "jramos",
        password: "jramos",
        name: "Jon Ramos",
    },
];

const AuthContext = createContext(undefined);

export const AuthProvider = ({children}) => {

    const [user, setUser] = useLocalStorage("user", null);
    const [users, setUsers] = useLocalStorage("users", USERS);
    const navigate = useNavigate();

    // LoginPage logic
    const login = async (user) => {

        const { username, password } = user;

        // try {
        //
        //     const res = await fetch('http://localhost:8080/auth/login', {
        //         method: 'POST',
        //         headers: {
        //             'Content-Type': 'application/json',
        //             'Authorization': 'Basic ' + btoa(`${username}:${password}`),
        //         },
        //     });
        //
        //     if (!res.ok) return false;
        //
        //     const data = await res.text();
        //
        //     setUser({ ...user, role: data });
        //
        //     return true;
        //
        // } catch (error) {
        //     console.error('Error during login:', error);
        //     return false;
        // }

        // Replace this with actual authentication logic
        const foundUser = users.find(u => u.username === username && u.password === password);
        if (foundUser) {
            setUser(foundUser);
            navigate('/bilatzailea'); // Redirect to the search page after login
            return true;
        }

        return false;

    };

    const logout = () => {
        setUser(null);
        navigate('/login');
    }

    return (
        <AuthContext.Provider value={{user, setUsers, login, logout}}>
            {children}
        </AuthContext.Provider>
    );

};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};