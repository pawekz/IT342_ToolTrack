import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import SidebarLayout from "../components/SidebarLayout";

const Dashboard = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const navigate = useNavigate();
  const [user, setUser] = useState(null);

  // Uncomment this when authentication is in place
  /*
  useEffect(() => {
    const storedUser = localStorage.getItem("user");
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    } else {
      navigate("/login");
    }
  }, [navigate]);
  */

  return (
    <div className="min-h-screen flex bg-gray-100">
      <SidebarLayout />
      <div className="flex-1 p-6">

      <div className="h-16 md:hidden" /> {/* Spacer for mobile view */}

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">

          <div className="bg-white rounded-xl p-6 shadow">
            <h3 className="text-sm font-medium text-gray-600">Total Users</h3>
            <p className="text-3xl font-bold mt-2">10 <span className="text-green-500 text-sm">↑</span></p>
            <p className="text-sm text-gray-500">Increase compared to last week</p>
            <a href="#" className="text-sm text-teal-500 mt-2 inline-block">User Report →</a>
          </div>

          <div className="bg-white rounded-xl p-6 shadow">
            <h3 className="text-sm font-medium text-gray-600">Total Number of Tools</h3>
            <p className="text-3xl font-bold mt-2">56</p>
            <a href="#" className="text-sm text-teal-500 mt-4 inline-block">All Tools →</a>
          </div>

          <div className="bg-white rounded-xl p-6 shadow">
            <h3 className="text-sm font-medium text-gray-600">Number of Tools borrowed</h3>
            <p className="text-3xl font-bold mt-2">27</p>
            <a href="#" className="text-sm text-teal-500 mt-4 inline-block">All Tools →</a>
          </div>
        </div>

        {/* Graph Container (Empty for now) */}
        <div className="bg-white rounded-xl p-6 shadow h-[300px]">
          <h3 className="text-sm font-medium text-gray-600 mb-2">Average Borrowing</h3>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
