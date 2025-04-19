import React from 'react';
import SidebarLayout from "../components/SidebarLayout";
import { CheckCircle, XCircle, Pencil, MoreVertical } from 'lucide-react';

// Mock data for testing – to be replaced with dynamic data from the backend
const users = [
  {
    id: 1,
    name: 'Nathaniel Salvoro',
    tool: 'Hammer',
    avatar: 'https://i.pravatar.cc/150?img=1',
  },
  {
    id: 2,
    name: 'Maggie Johnson',
    tool: 'Ballpen',
    avatar: 'https://i.pravatar.cc/150?img=2',
  },
  {
    id: 3,
    name: 'Gael Harry',
    tool: 'Wireless Drill',
    avatar: 'https://i.pravatar.cc/150?img=3',
  },
  {
    id: 4,
    name: 'Jenna Sullivan',
    tool: 'Laptop',
    avatar: 'https://i.pravatar.cc/150?img=4',
  },
];

const UserManagement = () => {
  return (
    <div className="flex h-screen bg-gray-50">
      <SidebarLayout />

      <div className="flex-1 p-6 overflow-auto">
        <div className="h-16 md:hidden" /> {/* Spacer for mobile view */}
        
        {/* Top header section */}
        <div className="flex flex-col md:flex-row md:items-center md:justify-between mb-8 gap-4">
          <div>
            <h2 className="text-2xl font-semibold text-gray-800">User Management</h2>
            <p className="text-gray-500 text-sm mt-1">Manage tool borrowing requests</p>
          </div>
          
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <svg className="h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clipRule="evenodd" />
              </svg>
            </div>
            <input
              type="text"
              placeholder="Search users or tools"
              className="border border-gray-200 pl-10 px-4 py-2 rounded-lg w-full md:w-64 text-sm focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent shadow-sm"
            />
          </div>
        </div>

        {/* Card container */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
          {/* Header with subtle background */}
          <div className="bg-gray-50 px-6 py-4 border-b border-gray-100">
            <div className="grid grid-cols-3 text-sm font-medium text-gray-700">
              <div>User</div>
              <div>Tool Requested</div>
              <div className="text-right">Actions</div>
            </div>
          </div>

          {/* User rows*/}
          <div className="divide-y divide-gray-100">
            {users.map((user) => (
              <div
                key={user.id}
                className="grid grid-cols-3 items-center px-6 py-4 hover:bg-teal-50 transition-colors duration-200"
              >
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-full overflow-hidden shadow-sm border border-gray-200">
                    <img
                      src={user.avatar}
                      alt={user.name}
                      className="w-full h-full object-cover"
                    />
                  </div>
                  <span className="text-gray-800 font-medium">{user.name}</span>
                </div>
                
                <div className="text-gray-700 flex items-center">
                  <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
                    {user.tool}
                  </span>
                </div>
                
                <div className="flex items-center justify-end gap-1">
                  <button className="flex items-center justify-center rounded-md bg-green-50 hover:bg-green-100 text-green-600 px-3 py-1.5 text-xs font-medium transition-colors">
                    <CheckCircle className="w-4 h-4 mr-1" />
                    Approve
                  </button>
                  <button className="flex items-center justify-center rounded-md bg-red-50 hover:bg-red-100 text-red-600 px-3 py-1.5 text-xs font-medium transition-colors">
                    <XCircle className="w-4 h-4 mr-1" />
                    Decline
                  </button>
                  <button className="ml-2 p-1.5 rounded-md hover:bg-gray-100 transition-colors">
                    <MoreVertical className="w-4 h-4 text-gray-500" />
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* Footer */}
          <div className="px-6 py-4 bg-gray-50 border-t border-gray-100">
            <div className="flex justify-between items-center">
              <span className="text-sm text-gray-500">Showing 4 of 24 requests</span>
              <a href="#" className="text-sm font-medium text-teal-600 hover:text-teal-700 flex items-center gap-1 transition-colors">
                View all requests
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                </svg>
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserManagement;