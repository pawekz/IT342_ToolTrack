import React, { useState } from "react";
import { Icon } from "@iconify/react";
import { Link, useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import axios from "axios";
import {useAuth} from "../components/AuthProvider.jsx";

const Register = () => {
  const {setJWTtoken} = useAuth();

  const [formData, setFormData] = useState({
    first_name: "",
    last_name: "",
    email: "",
    password_hash: "",
    confirmPassword: ""
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [showSuccessSnackbar, setShowSuccessSnackbar] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    // If already loading, prevent multiple submissions
    if (loading) return;

    setLoading(true);

    // Validate form
    if (!formData.first_name || !formData.last_name || !formData.email || !formData.password_hash || !formData.confirmPassword) {
      setError("All fields are required");
      setLoading(false);
      return;
    }

    if (formData.password_hash !== formData.confirmPassword) {
      setError("Passwords do not match");
      setLoading(false);
      return;
    }

    try {
      // Validate email first
      axios.get("https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/auth/checkUser?email=" + formData.email)
          .then( response => {
            if(response.data.msg === ("User does not exist")){
              //proceed to registration
              axios.post("https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/auth/register", {
                first_name: formData.first_name,
                last_name: formData.last_name,
                email: formData.email,
                password_hash: formData.password_hash,
              }).then( response => {
                if(response.status === 201){
                  setJWTtoken(response.data);
                  // Show success snackbar
                  setShowSuccessSnackbar(true);
                  // Navigate after a short delay to allow user to see success message
                  setTimeout(() => {
                    navigate('/dashboard');
                  }, 1500);
                } else {
                  setError(response.data); //Registration failed message
                  setLoading(false);
                }
              })
                  .catch(err => {
                    console.error("Registration error:", err);
                    setError(err.response?.data || "Registration failed. Please try again.");
                    setLoading(false);
                  });
            } else {
              setError("Email is already existed");
              setLoading(false);
            }
          })
          .catch(err => {
            console.error("Email validation error:", err);
            setError(err.response?.data || "Email validation failed. Please try again.");
            setLoading(false);
          });

    } catch (err) {
      console.error("Registration error:", err);

      // Log detailed error information
      console.log("Error details:", {
        status: err.response?.status,
        statusText: err.response?.statusText,
        data: err.response?.data,
        message: err.message
      });

      // Set a more informative error message
      if (err.response?.data) {
        setError(typeof err.response.data === 'string' ? err.response.data : JSON.stringify(err.response.data));
      } else if (err.message) {
        setError(`Registration failed: ${err.message}`);
      } else {
        setError("Registration failed. Please try again.");
      }
      setLoading(false);
    }
  };

  // Function to close snackbar
  const closeSnackbar = () => {
    setShowSuccessSnackbar(false);
  };

  return (
      <div className="flex flex-col items-center min-h-screen bg-gray-100 p-4 pt-20">
        <Navbar />
        <div className="bg-white p-10 rounded-xl shadow-md w-[28rem] mt-10">
          <h2 className="text-3xl font-bold text-center mb-3">Welcome</h2>
          <p className="text-gray-600 text-center mb-6">Register an account</p>

          {error && (
              <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                {error}
              </div>
          )}

          {showSuccessSnackbar && (
              <div className="fixed bottom-4 right-4 bg-green-500 text-white px-6 py-3 rounded-md shadow-lg flex items-center justify-between">
                <span>Successfully registered!</span>
                <button onClick={closeSnackbar} className="ml-4 text-white">
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                    <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
                  </svg>
                </button>
              </div>
          )}

          <form className="space-y-5" onSubmit={handleSubmit}>
            <div className="flex space-x-4">
              <input
                  type="text"
                  name="first_name"
                  placeholder="First Name"
                  value={formData.first_name}
                  onChange={handleChange}
                  className="w-1/2 border border-gray-300 px-4 py-3 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2EA69E]"
              />
              <input
                  type="text"
                  name="last_name"
                  placeholder="Last Name"
                  value={formData.last_name}
                  onChange={handleChange}
                  className="w-1/2 border border-gray-300 px-4 py-3 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2EA69E]"
              />
            </div>
            <input
                type="email"
                name="email"
                placeholder="Email"
                value={formData.email}
                onChange={handleChange}
                className="w-full border border-gray-300 px-4 py-3 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2EA69E]"
            />
            <input
                type="password"
                name="password_hash"
                placeholder="Password"
                value={formData.password_hash}
                onChange={handleChange}
                className="w-full border border-gray-300 px-4 py-3 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2EA69E]"
            />
            <input
                type="password"
                name="confirmPassword"
                placeholder="Confirm Password"
                value={formData.confirmPassword}
                onChange={handleChange}
                className="w-full border border-gray-300 px-4 py-3 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2EA69E]"
            />
            <button
                type="submit"
                disabled={loading}
                className="w-full bg-[#2EA69E] text-white py-3 rounded-md hover:bg-[#25897E] mb-2 cursor-pointer disabled:opacity-50"
            >
              {loading ? "Signing Up..." : "Sign Up"}
            </button>
          </form>

          <p className="text-center text-gray-600 mt-6">
            Already have an account? <Link to="/login" className="text-[#2EA69E] font-medium">Sign in</Link>
          </p>
        </div>
      </div>
  );
};

export default Register;