import React, { useState, useEffect } from "react";
import { Icon } from "@iconify/react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";

const LoginPage = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [googleApiReady, setGoogleApiReady] = useState(false)
  const [googleApiLoading, setGoogleApiLoading] = useState(true)
  const [error, setError] = useState(null)

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch(`http://${import.meta.env.VITE_BackendHostname}:8080/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      if (response.ok) {
        const userData = await response.json();
        localStorage.setItem("user", JSON.stringify(userData)); // Store session
        navigate("/dashboard"); // Redirect to dashboard
      } else {
        alert("Invalid login credentials");
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

  const handleGoogleLogin = async () => {
    if (!googleApiReady) {
      console.error('Google API is not ready');
      setError('Google login service is not available');
      return;
    }
  
    try {
      const client = google.accounts.oauth2.initCodeClient({
        client_id: import.meta.env.VITE_CLIENT_GOOGLE_ID, // Ensure this is correctly set in your .env file
        scope: 'profile email',
        ux_mode: 'popup', // This ensures a popup is used
        callback: async (response) => {
          if (response.error) {
            console.error('Google login error:', response.error);
            setError('Google login failed');
            return;
          }
  
          try {
            console.log('Authorization code:', response.code);
  
            // Optionally, send the authorization code to your backend for further processing
            const profileResponse = await fetch('https://www.googleapis.com/oauth2/v3/userinfo', {
              headers: {
                Authorization: `Bearer ${response.code}`, // Replace with your backend token exchange logic
              },
            });
  
            const profile = await profileResponse.json();
            console.log('Google Profile:', profile);
  
            // Handle the profile data (e.g., store it in localStorage or send it to your backend)
          } catch (err) {
            console.error('Error fetching Google profile:', err);
          }
        },
      });
  
      // Open the popup
      client.requestCode();
    } catch (err) {
      console.error('Error initializing Google login:', err);
    }
  };

  return (
    <div className="flex flex-col items-center min-h-screen bg-gray-100 p-4 pt-20">
      <Navbar />
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
