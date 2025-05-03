import axios from "axios";
import React, { createContext, useContext, useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [token, setToken] = useState(localStorage.getItem("token") || '');
    const [user, setUser] = useState(null);

    const isAuthenticated = !!token;

    useEffect(() => {
        if (token) {
            const decoded = jwtDecode(token);
            setUser(decoded);
        }
    }, [token]);

    const loginAction = async (data, navigate) => {
        try {
            if (!data.isGoogle) {
                const response = await axios.post(`https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/auth/admin/login`, data, {
                    headers: { "Content-Type": "application/json" }
                });
                if (response.status === 200 || response.status === 201) {
                    const { token, role } = response.data;
                    localStorage.setItem("token", token);
                    setToken(token);
                    setUser(jwtDecode(token));
                    if (role === "Staff") {
                        navigate("/dashboard");
                    }
                    return;
                }
            } else {
                const response = await axios.post(`https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/auth/admin/googleLogin`, data, {
                    headers: { "Content-Type": "application/json" }
                });
                if (response.status === 200 || response.status === 201) {
                    setToken(response.data.token);
                    localStorage.setItem("token", response.data.token);
                    setUser(jwtDecode(response.data.token));
                    if (jwtDecode(response.data.token).role === "Staff") {
                        navigate("/dashboard");
                    }
                    return;
                }
            }
            throw new Error(response.data.message);
        } catch (error) {
            console.error(error);
        }
    };

    const logout = () => {
        localStorage.removeItem("token");
        sessionStorage.clear();
        setToken('');
        setUser(null);
    };

    const setJWTtoken = (token) => {
        localStorage.setItem("token", token);
        setToken(token);
        setUser(jwtDecode(token));
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, loginAction, logout, setJWTtoken, user }}>
            {children}
        </AuthContext.Provider>
    );
};

export default AuthProvider;
export const useAuth = () => {
    return useContext(AuthContext);
};
