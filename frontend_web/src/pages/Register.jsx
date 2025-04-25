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
      // const isEmailAvailable = await validateEmail(formData.email);
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
                    setJWTtoken(response.data)
                    navigate('/dashboard')
                  }else{
                    setError(response.data) //Registraton failed message
                  }
              })
            }else{
              setError("Email is already existed")
            }
          })

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
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col items-center min-h-screen bg-gray-100 p-4 pt-20">
      <Navbar />
      <div className="bg-white p-10 rounded-xl shadow-md w-[28rem] mt-10">
        <h2 className="text-3xl font-bold text-center mb-3">Welcome</h2>
        <p className="text-gray-600 text-center mb-6">Register an account</p>

        <a href="/googlelogin" className="w-full bg-white border border-gray-300 text-gray-600 py-3 rounded-md shadow-md hover:bg-gray-100 mb-6 flex items-center justify-center space-x-2 cursor-pointer">
          <Icon icon="flat-color-icons:google" width="22" height="22" />
          <span>Continue with Google</span>
        </a>

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
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
