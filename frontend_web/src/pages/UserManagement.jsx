import React from 'react';
import Sidebar from '../components/Sidebar';
import { CheckCircle, XCircle, Pencil, MoreVertical } from 'lucide-react';

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
      <Sidebar />

      <div className="flex-1 p-6 overflow-auto">
        {/* Top header section */}
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-semibold text-gray-900">User Management</h2>
          <input
            type="text"
            placeholder="Search"
            className="border border-gray-300 px-4 py-2 rounded-md w-64 text-sm focus:outline-none focus:ring-1 focus:ring-gray-300"
          />
        </div>

        {/* Card container */}
        <div className="bg-white rounded-2xl shadow-sm p-6">
          {/* Column headers */}
          <div className="grid grid-cols-3 text-sm font-semibold text-gray-900 mb-2">
            <div>Borrow Requests</div>
            <div>Tool Request</div>
            <div className="text-right">Borrow Requests</div>
          </div>

          {/* User rows */}
          <div>
            {users.map((user) => (
              <div
                key={user.id}
                className={`
                  grid grid-cols-3 items-center px-2 py-3 rounded-xl
                  transition-colors duration-200
                  hover:bg-amber-50
                `}
              >
                <div className="flex items-center gap-3">
                  <img
                    src={user.avatar}
                    alt={user.name}
                    className="w-8 h-8 rounded-full object-cover"
                  />
                  <span className="text-gray-900 font-medium">{user.name}</span>
                </div>
                <div className="text-gray-800">{user.tool}</div>
                <div className="flex items-center justify-end gap-3 text-gray-600">
                  <button><CheckCircle className="w-5 h-5 text-green-500" /></button>
                  <button><XCircle className="w-5 h-5 text-red-500" /></button>
                  <button><Pencil className="w-4 h-4" /></button>
                  <button><MoreVertical className="w-4 h-4" /></button>
                </div>
              </div>
            ))}
          </div>

          {/* Footer */}
          <div className="pt-4 text-teal-600 text-sm hover:underline cursor-pointer">
            All Users →
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserManagement;
