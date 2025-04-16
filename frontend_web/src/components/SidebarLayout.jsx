// components/SidebarLayout.jsx
import React, { useState, useEffect } from "react";
import Sidebar from "./Sidebar";
import { Menu } from "lucide-react";

const SidebarLayout = ({ children }) => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  useEffect(() => {
    document.body.style.overflow = isSidebarOpen ? "hidden" : "auto";
  }, [isSidebarOpen]);

  return (
    <div className="min-h-screen flex bg-gray-100">
      <div className="fixed top-4 left-4 z-50 md:hidden">
        <button
          onClick={() => setIsSidebarOpen(true)}
          className="text-gray-600 bg-white p-2 rounded-md shadow-md focus:outline-none"
        >
          <Menu className="w-6 h-6" />
        </button>
      </div>

      <Sidebar isOpen={isSidebarOpen} onClose={() => setIsSidebarOpen(false)} />
      <div className="flex-1">
        <div className="h-16 md:hidden" />
        {children}
      </div>
    </div>
  );
};

export default SidebarLayout;
