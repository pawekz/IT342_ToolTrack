import React, { useState } from "react";
import { Link } from "react-router-dom";
import { Icon } from "@iconify/react";

const Navbar = () => {
  const [menuOpen, setMenuOpen] = useState(false);

  const toggleMenu = () => setMenuOpen(!menuOpen);

  return (
    <nav className="fixed top-0 left-0 w-full bg-white shadow-md py-4 px-8 flex justify-between items-center z-10">
      <div className="text-xl font-bold cursor-pointer">ToolTrack</div>

      {/* Desktop Links */}
      <div className="hidden md:flex space-x-6">
        <a href="#" className="text-gray-600 hover:text-gray-900">Home</a>
        <a href="#" className="text-gray-600 hover:text-gray-900">Feature</a>
        <a href="#" className="text-gray-600 hover:text-gray-900">Developer</a>
      </div>

      <div className="hidden md:flex space-x-4">
        <Link
          to="/register"
          className="text-gray-600 px-4 py-2 rounded-md cursor-pointer hover:text-gray-900"
        >
          Sign Up
        </Link>
        <Link
          to="/login"
          className="bg-[#2EA69E] text-white px-4 py-2 rounded-md cursor-pointer hover:bg-[#25897E]"
        >
          Login
        </Link>
      </div>

      {/* Mobile Burger */}
      <div className="md:hidden">
        <button onClick={toggleMenu} className="text-2xl text-gray-600">
          <Icon icon={menuOpen ? "mdi:close" : "mdi:menu"} />
        </button>
      </div>

      {/* Mobile Menu */}
      {menuOpen && (
        <div className="absolute top-[70px] left-0 w-full bg-white shadow-md px-8 py-4 flex flex-col space-y-3 md:hidden">
          <a href="#" className="text-gray-600 hover:text-gray-900">Home</a>
          <a href="#" className="text-gray-600 hover:text-gray-900">Feature</a>
          <a href="#" className="text-gray-600 hover:text-gray-900">Developer</a>
          <Link
            to="/register"
            className="text-gray-600 px-4 py-2 rounded-md hover:text-gray-900"
          >
            Sign Up
          </Link>
          <Link
            to="/login"
            className="bg-[#2EA69E] text-white px-4 py-2 rounded-md hover:bg-[#25897E]"
          >
            Login
          </Link>
        </div>
      )}
    </nav>
  );
};

export default Navbar;
