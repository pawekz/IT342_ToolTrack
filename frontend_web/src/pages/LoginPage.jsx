import React, { useState, useEffect } from "react";
import { Icon } from "@iconify/react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import axios from "axios";

import { useAuth } from "../components/AuthProvider";

const LoginPage = () => {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);

  const { loginAction, setJWTtoken } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [googleApiReady, setGoogleApiReady] = useState(false);
  const [googleApiLoading, setGoogleApiLoading] = useState(true);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [showSuccessSnackbar, setShowSuccessSnackbar] = useState(false);

  //test database connection - remove after testing
  const [testUser, setTestUser] = useState(null);
  const [testError, setTestError] = useState(null);
  //test database connection - remove after testing

  const handleFormSubmit = async (e) => {
    e.preventDefault();

    // Prevent multiple submissions
    if (loading) return;

    setLoading(true);
    setError(null);

    try {
      // Always use Azure backend for login
      const response = await fetch("https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/auth/admin/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
      });

      if (response.ok) {
        const data = await response.json();
        setJWTtoken(data.token);

        // Show success message
        setShowSuccessSnackbar(true);

        // Navigate after a short delay
        setTimeout(() => {
          navigate("/dashboard");
        }, 1500);
      } else {
        setError("Login failed. Please check your email and password.");
        setLoading(false);
      }
    } catch (error) {
      console.error("Login error:", error);
      setError("An error occurred during login. Please try again.");
      setLoading(false);
    }
  };

  useEffect(() => {
    const script = document.createElement('script');
    script.src = 'https://accounts.google.com/gsi/client';
    script.async = true;
    script.defer = true;
    script.onload = () => {
      setGoogleApiReady(true);
      setGoogleApiLoading(false);
    };
    script.onerror = () => {
      console.error('Google Identity Services script failed to load');
      setGoogleApiLoading(false);
      setError('Failed to load Google login service');
    };
    document.body.appendChild(script);

    return () => {
      document.body.removeChild(script);
    };
  }, []);

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

    // Set loading state for Google login too
    if (loading) return;
    setLoading(true);
    setError(null);

    try {
      //client id must not be exposed
      const client = google.accounts.oauth2.initTokenClient({
        client_id: import.meta.env.VITE_GOOGLE_CLIENT_ID,
        scope: 'profile email',
        callback: async (response) => {
          if (response.error) {
            console.error('Google login error:', response.error);
            setError('Google login failed');
            setLoading(false);
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
                // Show success message
                setShowSuccessSnackbar(true);

                // Navigate after a short delay
                setTimeout(() => {
                  navigate("/dashboard");
                }, 1500);
              } else if(response.status === 200 && response.data.msg === "User does not exist") {

                const userData = {
                  email: profile.email,
                  first_name: profile.given_name,
                  last_name: profile.family_name,
                  password_hash: null,
                  isGoogle: true
                };
                axios.post(`https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/auth/admin/register`, userData).then(response => {
                  setJWTtoken(response.data);
                  // Show success message
                  setShowSuccessSnackbar(true);

                  // Navigate after a short delay
                  setTimeout(() => {
                    navigate("/dashboard");
                  }, 1500);
                }).catch(err => {
                  console.error("Registration error:", err);
                  setError("Registration with Google failed. Please try again.");
                  setLoading(false);
                });
              }
            }).catch(err => {
              console.error("Check user error:", err);
              setError("Failed to check if user exists. Please try again.");
              setLoading(false);
            });
          } catch (err) {
            console.error('Error fetching Google profile:', err);
            setError("Failed to fetch profile data.");
            setLoading(false);
          }
        },
      });

      client.requestAccessToken();
    } catch (err) {
      console.error('Error initializing Google login:', err);
      setError("Google login failed to initialize.");
      setLoading(false);
    }
  };

  // Function to close snackbar
  const closeSnackbar = () => {
    setShowSuccessSnackbar(false);
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

        {/* Success snackbar */}
        {showSuccessSnackbar && (
            <div className="fixed bottom-4 right-4 bg-green-500 text-white px-6 py-3 rounded-md shadow-lg flex items-center justify-between">
              <span>Successfully logged in!</span>
              <button onClick={closeSnackbar} className="ml-4 text-white">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                  <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
                </svg>
              </button>
            </div>
        )}

        <div className="bg-white p-10 rounded-xl shadow-md w-[28rem] mt-10">
          <h2 className="text-3xl font-bold text-center mb-3">Welcome</h2>

          <p className="text-gray-600 text-center mb-6">Login to your account</p>

          {/* Error message display */}
          {error && (
              <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                {error}
              </div>
          )}

          {/* Google Login Button */}
          <button
              onClick={handleGoogleLogin}
              disabled={loading || !googleApiReady}
              className={`w-full bg-white border border-gray-300 text-gray-600 py-3 rounded-md shadow-md hover:bg-gray-100 flex items-center justify-center space-x-2 cursor-pointer mb-6 ${
                  (loading || !googleApiReady) ? 'opacity-50 cursor-not-allowed' : ''
              }`}
          >
            <Icon icon="flat-color-icons:google" width="22" height="22" />
            <span>{loading ? "Logging in..." : "Login with Google"}</span>
          </button>

          {/* Manual Login Form */}
          <form onSubmit={handleFormSubmit} className="space-y-5">
            <input
                type="email"
                placeholder="Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full border border-gray-300 px-4 py-3 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2EA69E]"
                disabled={loading}
            />
            <div className="relative">
            <input
                type={showPassword ? "text" : "password"}
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full border border-gray-300 px-4 py-3 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2EA69E]"
                disabled={loading}
            />
              <button
                type="button"
                onClick={() => setShowPassword(prev => !prev)}
                className="absolute inset-y-0 right-0 pr-4 flex items-center text-gray-400"
                tabIndex={-1}
                >
                  <Icon icon={showPassword ? "mdi:eye-off" : "mdi:eye"} width="24" height="24" />
              </button>
            </div>
            <button
                type="submit"
                disabled={loading}
                className={`w-full bg-[#2EA69E] text-white py-3 rounded-md hover:bg-[#25897E] cursor-pointer ${
                    loading ? 'opacity-50 cursor-not-allowed' : ''
                }`}
            >
              {loading ? "Logging in..." : "Login"}
            </button>

          </form>

          <p className="text-center text-gray-600 mt-6">
            Don't have an account? <a href="/register" className="text-[#2EA69E] font-medium">Sign up</a>
          </p>
        </div>
      </div>
  );
};

export default LoginPage;