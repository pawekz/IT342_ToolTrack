import React, { useState } from "react";
import { Link } from "react-router-dom";
import { Menu, X } from "lucide-react";
import ToolTrack from "../assets/ToolTrack_logo.png";

const Navbar = () => {
  const [menuOpen, setMenuOpen] = useState(false);

  const toggleMenu = () => setMenuOpen(!menuOpen);

  return (
    <nav className="fixed top-0 left-0 w-full bg-white shadow-sm py-3 px-6 md:px-8 flex justify-between items-center z-11 border-b border-gray-100">
      <div className="flex items-center gap-3 cursor-pointer">
        <img 
          src={ToolTrack} 
          alt="ToolTrack Logo" 
          className="h-8 w-8 object-contain" 
          onClick={() => window.location.href = "/"}
        />
        <span className="text-xl font-semibold bg-gradient-to-r from-teal-600 to-cyan-500 bg-clip-text text-transparent" onClick={() => window.location.href = "/"}>ToolTrack</span>
      </div>

      {/* Desktop Links */}
      <div className="hidden md:flex space-x-8">
        <a href="#" className="text-gray-600 hover:text-teal-500 transition-colors text-sm font-medium">Home</a>
        <a href="#" className="text-gray-600 hover:text-teal-500 transition-colors text-sm font-medium">Feature</a>
        <a href="#" className="text-gray-600 hover:text-teal-500 transition-colors text-sm font-medium">Developer</a>
      </div>

      <div className="hidden md:flex space-x-4 items-center">
        <Link
          to="/register"
          className="text-gray-600 px-5 py-2 rounded-full transition-colors hover:bg-gray-50 hover:text-teal-500 text-sm font-medium"
        >
          Sign Up
        </Link>
        <Link
          to="/login"
          className="bg-gradient-to-r from-teal-500 to-cyan-500 text-white px-6 py-2 rounded-full transition-all hover:shadow-md hover:scale-105 text-sm font-medium"
        >
          Login
        </Link>
      </div>

      {/* Mobile Burger */}
      <div className="md:hidden">
        <button 
          onClick={toggleMenu} 
          className="text-gray-600 hover:text-teal-500 transition-colors p-1 rounded-md hover:bg-gray-50"
        >
          {menuOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </div>

      {/* Mobile Menu */}
      {menuOpen && (
        <div className="absolute top-full left-0 w-full bg-white/95 backdrop-blur-sm shadow-md px-6 py-6 flex flex-col space-y-4 md:hidden rounded-b-lg border-t border-gray-50 animate-fadeIn">
          <a href="#" className="text-gray-600 hover:text-teal-500 transition-colors px-2 py-1 rounded-md hover:bg-gray-50">Home</a>
          <a href="#" className="text-gray-600 hover:text-teal-500 transition-colors px-2 py-1 rounded-md hover:bg-gray-50">Feature</a>
          <a href="#" className="text-gray-600 hover:text-teal-500 transition-colors px-2 py-1 rounded-md hover:bg-gray-50">Developer</a>
          <div className="border-t border-gray-100 pt-4 flex flex-col space-y-3 mt-2">
            <Link
              to="/register"
              className="text-gray-600 px-4 py-2 rounded-full hover:bg-gray-50 hover:text-teal-500 transition-colors text-center font-medium"
            >
              Sign Up
            </Link>
            <Link
              to="/login"
              className="bg-gradient-to-r from-teal-500 to-cyan-500 text-white px-4 py-2 rounded-full hover:shadow-md transition-all text-center font-medium"
            >
              Login
            </Link>
          </div>
        </div>
      )}
    </nav>
  );
};

export default Navbar;