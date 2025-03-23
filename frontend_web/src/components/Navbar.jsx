import React from "react";

const Navbar = () => {
  return (
    <nav className="fixed top-0 left-0 w-full bg-white shadow-md py-4 px-8 flex justify-between items-center z-10">
      <div className="text-xl font-bold cursor-pointer">ToolTrack</div>
      <div className="space-x-6">
        <a href="#" className="text-gray-600 hover:text-gray-900">Home</a>
        <a href="#" className="text-gray-600 hover:text-gray-900">Feature</a>
        <a href="#" className="text-gray-600 hover:text-gray-900">Developer</a>
      </div>
      <div className="space-x-4">
        <button className="text-gray-600 px-4 py-2 rounded-md cursor-pointer hover:text-gray-900">Sign Up</button>
        <button className="bg-[#2EA69E] text-white px-4 py-2 rounded-md cursor-pointer hover:bg-[#25897E]">Login</button>
      </div>
    </nav>
  );
};

export default Navbar;
