import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import SidebarLayout from "../components/SidebarLayout";
import { 
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer 
} from "recharts";

import axios from "axios"

const Dashboard = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [activeTab, setActiveTab] = useState("overview");
  const [loading, setLoading] = useState(false);

  const[totalUsers, setTotalUsers] = useState(0);
  const[totalTools, setTotalTools] = useState(0);
  const[totalBorrowed, setTotalBorrowed] = useState(0);
  const[sortRange, setSortRange] = useState("Last 6 months")
  const[dates, setDates] = useState([
    { month: "Jan", tools: 0 },
    { month: "Feb", tools: 0 },
    { month: "Mar", tools: 0 },
    { month: "Apr", tools: 0 },
    { month: "May", tools: 0 },
    { month: "Jun", tools: 0 },
    { month: "Jul", tools: 0 },
    { month: "Aug", tools: 0 },
    { month: "Sep", tools: 0 },
    { month: "Oct", tools: 0 },
    { month: "Nov", tools: 0 },
    { month: "Dec", tools: 0 }
  ]);


  const mockRecentActivities = [
    { id: 1, user: "John Doe", action: "Borrowed", tool: "Power Drill", date: "2023-09-15" },
    { id: 2, user: "Jane Smith", action: "Returned", tool: "Hammer Set", date: "2023-09-14" },
  ];

  const mockPopularTools = [
    { name: "Power Drill", borrowed: 12 },
    { name: "Hammer Set", borrowed: 10 },
    { name: "Screwdriver Set", borrowed: 8 },
    { name: "Lawn Mower", borrowed: 6 },
  ];

  useEffect(() => {
    console.log(sortRange)
    axios.get("https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/toolitem/getTotalTools",
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        })
        .then(result => {
          setTotalTools(result.data.total)
        })

    axios.get("https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/test/getTotalUsers",
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        })
        .then(result => {
          setTotalUsers(result.data)
        })
  }, []);

  useEffect(() => {
    const sortValue = sortRange.split(" ").join("");
    console.log("sortValue", sortValue);
    axios.get(`https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/transaction/getSortedDates/${sortValue}`,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        }).then(result => {
          if (result.status === 200) {
            updateMonthlyTools(result.data.timestamps)
          }
    })
  }, [sortRange]);


  function getMonthLabels(range) {
    const allMonths = ["Jan", "Feb", "Mar", "Apr", "May", "Jun",
      "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

    if (range === "Last 6 months") {
      const now = new Date();
      const months = [];
      for (let i = 5; i >= 0; i--) {
        const monthIndex = (now.getMonth() - i + 12) % 12;
        months.push(allMonths[monthIndex]);
      }
      return months;
    }

    // Return full year for Last year / All time
    return allMonths;
  }


  function updateMonthlyTools(backendData) {
    const shortMonth = (month) =>
        month.charAt(0).toUpperCase() + month.slice(1, 3).toLowerCase();

    // Format backend data into short month → count
    const formattedBackend = {};
    for (const [fullMonth, count] of Object.entries(backendData)) {
      const short = shortMonth(fullMonth);
      formattedBackend[short] = count;
    }

    // Get relevant months based on range
    const activeMonths = getMonthLabels(sortRange);

    // Set state with only relevant months
    const updated = activeMonths.map((month) => ({
      month,
      tools: formattedBackend[month] || 0,
    }));

    setDates(updated);
  }




  // For future implementation:
  // 1. Fetch dashboard data from API
  // 2. Add loading state management
  // 3. Implement error handling
  // 4. Add real-time updates if needed

  const handleTabChange = (tab) => {
    setActiveTab(tab);
    // Here you would fetch data based on the selected tab
    setLoading(true);
    // Simulate loading for UI demonstration



    setTimeout(() => {
      setLoading(false);
    }, 300);
  };

  return (
    <div className="min-h-screen flex bg-gray-100">
      <SidebarLayout />
      <div className="flex-1 p-4 md:p-6 overflow-y-auto h-screen">
        <div className="h-16 md:hidden" /> {/* Spacer for mobile view */}

        {/* Dashboard Header */}
        <div className="mb-6">
          <h1 className="text-2xl font-bold text-gray-800">Dashboard</h1>
          <p className="text-sm text-gray-600">Welcome back! Here's what's happening with your tool inventory.</p>
        </div>

        {/* Tab Navigation */}
        <div className="mb-6 border-b border-gray-200">
          <nav className="flex space-x-6">
            <button 
              onClick={() => handleTabChange("overview")}
              className={`pb-2 text-sm font-medium ${activeTab === "overview" ? "border-b-2 border-teal-500 text-teal-600" : "text-gray-500 hover:text-gray-700"}`}
            >
              Overview
            </button>

            <button 
              onClick={() => handleTabChange("users")}
              className={`pb-2 text-sm font-medium ${activeTab === "users" ? "border-b-2 border-teal-500 text-teal-600" : "text-gray-500 hover:text-gray-700"}`}
            >
              Users
            </button>
          </nav>
        </div>

        {loading ? (
          <div className="flex justify-center items-center h-40">
            <p className="text-gray-500">Loading data...</p>
          </div>
        ) : (
          <>
            {/* Stats Cards */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 md:gap-6 mb-6">
              <div className="bg-white rounded-xl p-6 shadow-sm hover:shadow transition-shadow">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="text-sm font-medium text-gray-600">Total Users</h3>
                    <p className="text-3xl font-bold mt-2">{totalUsers}</p> {/*<span className="text-green-500 text-sm">↑ 20%</span></p>
                    <p className="text-sm text-gray-500 mt-1">Increase compared to last week</p>*/}
                  </div>
                  <div className="p-2 bg-teal-50 rounded-md">
                    <svg className="w-6 h-6 text-teal-500" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path>
                    </svg>
                  </div>
                </div>
                <button className="text-sm text-teal-500 mt-4 inline-flex items-center">
                  User Report 
                  <svg className="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7"></path>
                  </svg>
                </button>
              </div>

              <div className="bg-white rounded-xl p-6 shadow-sm hover:shadow transition-shadow">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="text-sm font-medium text-gray-600">Total Number of Tools</h3>
                    <p className="text-3xl font-bold mt-2">{totalTools}</p>{/*<span className="text-green-500 text-sm">↑ 5%</span></p>
                    <p className="text-sm text-gray-500 mt-1">4 new tools added this month</p>*/}
                  </div>
                  <div className="p-2 bg-blue-50 rounded-md">
                    <svg className="w-6 h-6 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                    </svg>
                  </div>
                </div>
                <button className="text-sm text-blue-500 mt-4 inline-flex items-center">
                  All Tools 
                  <svg className="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7"></path>
                  </svg>
                </button>
              </div>

              <div className="bg-white rounded-xl p-6 shadow-sm hover:shadow transition-shadow">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="text-sm font-medium text-gray-600">Tools Currently Borrowed</h3>
                    <p className="text-3xl font-bold mt-2">{totalBorrowed}</p>{/*<span className="text-yellow-500 text-sm">↑ 12%</span></p>
                    <p className="text-sm text-gray-500 mt-1">8 due for return this week</p>*/}
                  </div>
                  <div className="p-2 bg-yellow-50 rounded-md">
                    <svg className="w-6 h-6 text-yellow-500" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"></path>
                    </svg>
                  </div>
                </div>
                <button className="text-sm text-yellow-500 mt-4 inline-flex items-center">
                  View Borrowed Tools 
                  <svg className="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7"></path>
                  </svg>
                </button>
              </div>
            </div>

            {/* Main Content */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-4 md:gap-6">
              {/* Graph Section */}
              <div className="bg-white rounded-xl p-6 shadow-sm lg:col-span-2">
                <div className="flex justify-between items-center mb-4">
                  <h3 className="text-sm font-medium text-gray-600">Monthly Tool Borrowing</h3>
                  <div className="flex space-x-2">
                    <select className="text-xs border border-gray-300 rounded-md px-2 py-1"
                    onChange={e => setSortRange(e.target.value)}>
                      <option>Last 6 months</option>
                      <option>Last year</option>
                      <option>All time</option>
                    </select>
                  </div>
                </div>
                <div className="h-64">
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={dates} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis dataKey="month" />
                      <YAxis />
                      <Tooltip />
                      <Legend />
                      <Bar dataKey="tools" fill="#14b8a6" />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
                <p className="text-xs text-gray-500 mt-4">
                  * Data shows the number of tools borrowed each month
                </p>
              </div>

              {/* Popular Tools */}
              <div className="bg-white rounded-xl p-6 shadow-sm">
                <h3 className="text-sm font-medium text-gray-600 mb-4">Most Popular Tools</h3>
                <div className="space-y-4">
                  {mockPopularTools.map((tool, index) => (
                    <div key={index} className="flex items-center justify-between">
                      <div className="flex items-center">
                        <div className={`w-8 h-8 rounded-md flex items-center justify-center ${
                          index === 0 ? 'bg-teal-100 text-teal-600' : 
                          index === 1 ? 'bg-blue-100 text-blue-600' : 
                          index === 2 ? 'bg-yellow-100 text-yellow-600' : 
                          'bg-gray-100 text-gray-600'
                        }`}>
                          {index + 1}
                        </div>
                        <span className="ml-3 text-sm font-medium">{tool.name}</span>
                      </div>
                      <span className="text-sm text-gray-500">Borrowed {tool.borrowed} times</span>
                    </div>
                  ))}
                </div>
                <button className="mt-4 text-sm text-teal-500 inline-flex items-center">
                  View All Tools 
                  <svg className="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7"></path>
                  </svg>
                </button>
              </div>
            </div>

            {/* Recent Activity Table */}
            <div className="mt-6 bg-white rounded-xl p-6 shadow-sm">
              <div className="flex justify-between items-center mb-4">
                <h3 className="text-sm font-medium text-gray-600">Recent Activities</h3>
                <button className="text-xs text-teal-500">View All</button>
              </div>
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead>
                    <tr className="text-xs font-medium text-gray-500 border-b">
                      <th className="pb-2 text-left">User</th>
                      <th className="pb-2 text-left">Action</th>
                      <th className="pb-2 text-left">Tool</th>
                      <th className="pb-2 text-left">Date</th>
                      <th className="pb-2 text-right">Status</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-100">
                    {mockRecentActivities.map((activity) => (
                      <tr key={activity.id} className="text-sm">
                        <td className="py-3 text-gray-800">{activity.user}</td>
                        <td className="py-3">
                          <span className={`py-1 px-2 rounded-full text-xs ${
                            activity.action === "Borrowed" ? "bg-blue-100 text-blue-700" : "bg-green-100 text-green-700"
                          }`}>
                            {activity.action}
                          </span>
                        </td>
                        <td className="py-3 text-gray-800">{activity.tool}</td>
                        <td className="py-3 text-gray-500">{new Date(activity.date).toLocaleDateString()}</td>
                        <td className="py-3 text-right">
                          <button className="text-teal-500 hover:text-teal-700">
                            View Details
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </>
        )}

        {/* For future implementation - notification center */}
        {/* 
        <div className="fixed bottom-4 right-4">
          <button className="bg-teal-500 text-white p-3 rounded-full shadow-lg">
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"></path>
            </svg>
          </button>
        </div>
        */}
      </div>
    </div>
  );
};

export default Dashboard;