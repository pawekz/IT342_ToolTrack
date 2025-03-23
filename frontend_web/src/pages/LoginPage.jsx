import React from "react";
import {Icon} from "@iconify/react";
import Navbar from "../components/Navbar";

const LoginPage = () => {
  return (
    <div className="flex flex-col items-center  min-h-screen bg-gray-100 p-4 pt-20">
      <Navbar />
      <div className="bg-white p-10 rounded-xl shadow-md w-[28rem] mt-10">
        <h2 className="text-3xl font-bold text-center mb-3">Welcome</h2>
        <p className="text-gray-600 text-center mb-6">Login to your account</p>

        <button className="w-full bg-white border border-gray-300 text-gray-600 py-3 rounded-md shadow-md hover:bg-gray-100 mb-15 flex items-center justify-center space-x-2 cursor-pointer">
          <Icon icon="flat-color-icons:google" width="22" height="22" /> 
          <span>Continue with Google</span>
        </button>

        <form className="space-y-5">
          <input 
            type="email" 
            placeholder="Email" 
            className="w-full border border-gray-300 px-4 py-3 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2EA69E]" 
          />
          <input 
            type="password"
            placeholder="Password" 
            className="w-full border border-gray-300 px-4 py-3 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2EA69E]" 
          />
          <button 
            type="submit" 
            className="w-full bg-[#2EA69E] text-white py-3 rounded-md hover:bg-[#25897E] mb-2 cursor-pointer">
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
