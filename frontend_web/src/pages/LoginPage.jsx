import React, { useState, useEffect } from "react";
import { Icon } from "@iconify/react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import axios from "axios";

import { useAuth } from "../components/AuthProvider";


const LoginPage = () => {

  const navigate = useNavigate();
  
  const { loginAction, setJWTtoken } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [googleApiReady, setGoogleApiReady] = useState(false)
  const [googleApiLoading, setGoogleApiLoading] = useState(true)
  const [error, setError] = useState(null)

  //test database connection - remove after testing
  const [testUser, setTestUser] = useState(null);
  const [testError, setTestError] = useState(null);
  //test database connection - remove after testing

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    try {
      // Always use Azure backend for login
      const response = await fetch("https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/auth/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
      });

      if (response.ok) {
        const data = await response.json();
        setJWTtoken(data.token)
        navigate("/dashboard")
      } else {
        setError("Login failed");
      }
    } catch (error) {
      console.error("Login error:", error);
    }
  };

  useEffect(() => {
    const script = document.createElement('script')
    script.src = 'https://accounts.google.com/gsi/client'
    script.async = true
    script.defer = true
    script.onload = () => {
      setGoogleApiReady(true)
      setGoogleApiLoading(false)
    }
    script.onerror = () => {
      console.error('Google Identity Services script failed to load')
      setGoogleApiLoading(false)
      setError('Failed to load Google login service')
    }
    document.body.appendChild(script)

    return () => {
      document.body.removeChild(script)
    }
  }, [])

  //function to test database connection - remove after testing
  useEffect(() => {

    // Function to test database connection
    const testDbConnection = async () => {
      try {
        // Always use Azure backend for DB test
        const apiUrl = 'https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/test/dbconnect';
        const response = await axios.get(apiUrl);
        setTestUser(response.data);
      } catch (err) {
        console.error("Database connection test error:", err);
        setTestError(err.response?.data?.error || "Failed to connect to database");
      }
    };

    // Call the test function
    testDbConnection();
  }, []);
  //test database connection - remove after testing

  const handleGoogleLogin = async () => {
    if (!googleApiReady) {
      console.error('Google API is not ready');
      setError('Google login service is not available');
      return;
    }

    try {
      //client id must not be exposed
      const client = google.accounts.oauth2.initTokenClient({
        client_id: import.meta.env.VITE_GOOGLE_CLIENT_ID,
        scope: 'profile email',
        callback: async (response) => {
          if (response.error) {
            console.error('Google login error:', response.error);
            setError('Google login failed');
            return;
          }
  
          try {
            const profileResponse = await fetch('https://www.googleapis.com/oauth2/v3/userinfo', {
              headers: {
                Authorization: `Bearer ${response.access_token}`,
              },
            });
  
            const profile = await profileResponse.json();
            console.log('Google Profile:', profile);

            const checkUserResponse = await axios.get(`https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/auth/checkUser`,
              {params: { email: profile.email }},
            ).then(response => {
              console.log("Check User Response:", response.data);
              if(response.status === 200 && response.data.msg === "User exists"){
                //go to login auth provider
                const credentials = {
                  email: profile.email,
                  isGoogle: true
                };
                loginAction(credentials, navigate);
              }else if(response.status === 200 && response.data.msg === "User does not exist") {
                
                const userData = {
                  email: profile.email,
                  first_name: profile.given_name,
                  last_name: profile.family_name,
                  password_hash: null,
                  isGoogle: true
                };
                axios.post(`https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/auth/register`, userData).then(response => {
                  setJWTtoken(response.data);
                })
              }
            });
          } catch (err) {
            console.error('Error fetching Google profile:', err);
            setError("Failed to fetch profile data.");
          }
        },
      });
  
      client.requestAccessToken();
    } catch (err) {
      console.error('Error initializing Google login:', err);
      setError("Google login failed to initialize.");
    }
  };
  

  return (
    <div className="flex flex-col items-center min-h-screen bg-gray-100 p-4 pt-20 relative">
      <Navbar />
      {/* Database connection status indicator */}
      <div
        className={`fixed bottom-2 right-2 w-[12px] h-[12px] rounded-full ${
          testUser ? 'bg-green-500' : testError ? 'bg-red-500' : 'bg-gray-300'
        }`}
        title={testUser ? "Database connected" : testError ? "Database connection failed" : "Checking connection..."}
      ></div>
      <div className="bg-white p-10 rounded-xl shadow-md w-[28rem] mt-10">
        <h2 className="text-3xl font-bold text-center mb-3">Welcome</h2>


        <p className="text-gray-600 text-center mb-6">Login to your account</p>

        {/* Google Login Button */}
        <button
          onClick={handleGoogleLogin}
          className="w-full bg-white border border-gray-300 text-gray-600 py-3 rounded-md shadow-md hover:bg-gray-100 flex items-center justify-center space-x-2 cursor-pointer mb-15"
        >
          <Icon icon="flat-color-icons:google" width="22" height="22" />
          <span>Login with Google</span>
        </button>

        {/* Manual Login Form */}
        <form onSubmit={handleFormSubmit} className="space-y-5 mt-4">
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full border border-gray-300 px-4 py-3 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2EA69E]"
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full border border-gray-300 px-4 py-3 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2EA69E]"
          />
          <button
            type="submit"
            className="w-full bg-[#2EA69E] text-white py-3 rounded-md hover:bg-[#25897E] cursor-pointer"
          >
            Login
          </button>
        </form>

        <p className="text-center text-gray-600 mt-6">
          Don’t have an account? <a href="/register" className="text-[#2EA69E] font-medium">Sign up</a>
        </p>
      </div>
    </div>
  );
};

export default LoginPage;
