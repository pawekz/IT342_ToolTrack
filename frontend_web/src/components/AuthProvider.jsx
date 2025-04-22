import axios from "axios";
import React, { createContext, useContext, useState } from "react";
import { jwtDecode } from "jwt-decode";


export const AuthContext = createContext();

export const AuthProvider = ({children}) => {

    const [token, setToken] = useState(localStorage.getItem("token")||'');

    const isAuthenticated = !!token; 

    const loginAction = async (data, navigate) => {
        try {
            console.log(data)
            if(!data.isGoogle){ //NormalLogin
                const response = await axios.post(`http://localhost:8080/auth/login`, data, {
                    headers: { "Content-Type": "application/json" }
                });
                if (response.status === 200 || response.status === 201) {
                    const { token, role } = response.data; 
                    localStorage.setItem("token", token);
                    setToken(token);
        
                    // Redirect user based on role
                    if (role === "Staff") {
                        navigate("/dashboard"); 
                    }
                    return;
                }
            } else{ //googleLogin
                const response = await axios.post(`http://localhost:8080/auth/googleLogin`, data, {
                    headers: { "Content-Type": "application/json" }
                });
                if (response.status === 200 || response.status === 201) {
                    const decode = jwtDecode(response.data);
                    
                    console.log(decode)
                    // const { token, role } = response.data; 
                    // localStorage.setItem("token", token);
                    // setToken(token);
        
                    // // Redirect user based on role
                    // if (role === "Staff") {
                    //     navigate("/dashboard"); 
                    // }
                    // return;
                }
            }
            throw new Error(response.data.message)
        } catch (error) {
            console.log(error)
        }
        
    }

    const logout = () => {
        setIsAuthenticated(false)
        localStorage.removeItem('isAuthenticated')
    }

    const setJWTtoken = (token) => {
        localStorage.setItem("token", token);
        setToken(token);
        return;
    }

    return (
        <AuthContext.Provider value={{isAuthenticated, loginAction, logout, setJWTtoken}}>
            {children}
        </AuthContext.Provider>
    )
}
export default AuthProvider;
export const useAuth = () => {
    return useContext(AuthContext);
};