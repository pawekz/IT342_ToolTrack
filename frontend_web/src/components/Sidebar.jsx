import { Link } from "react-router-dom";
import { Icon } from "@iconify/react";
import { X } from "lucide-react";

const Sidebar = ({ isOpen, onClose }) => {
  return (
    <div
      className={`
        fixed md:static top-0 left-0 z-50 h-screen w-[250px] bg-white shadow-lg p-4 flex flex-col rounded-r-xl
        transform transition-transform duration-300 ease-in-out
        ${isOpen ? "translate-x-0" : "-translate-x-full md:translate-x-0"}
      `}
    >
      {/* Close Button for Mobile */}
      <div className="md:hidden flex justify-end mb-2">
        <button onClick={onClose}>
          <X className="w-6 h-6 text-gray-600" />
        </button>
      </div>

      {/* Top Section with flex-grow */}
      <div className="flex-grow">
        <div className="mb-6 text-2xl font-bold text-center text-gray-800">ToolTrack</div>

        <input
          type="text"
          placeholder="Search"
          className="w-full px-3 py-2 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-300 mb-6"
        />

        <ul className="space-y-3 text-gray-700 text-sm">
          <li>
            <Link to="/dashboard" className="flex items-center gap-3 px-3 py-2 rounded-md hover:bg-gray-100">
              <Icon icon="mdi:view-dashboard" className="text-xl" /> Dashboard
            </Link>
          </li>
          <li>
            <Link to="/toolmanagement" className="flex items-center gap-3 px-3 py-2 rounded-md hover:bg-gray-100">
              <Icon icon="mdi:tools" className="text-xl" /> Tool Management
            </Link>
          </li>
          <li>
            <Link to="/user-management" className="flex items-center gap-3 px-3 py-2 rounded-md hover:bg-gray-100">
              <Icon icon="mdi:account-group" className="text-xl" /> User Management
            </Link>
          </li>
          <li>
            <Link to="/tools" className="flex items-center gap-3 px-3 py-2 rounded-md hover:bg-gray-100">
              <Icon icon="mdi:toolbox-outline" className="text-xl" /> Tools
            </Link>
          </li>
        </ul>
      </div>

      {/* Bottom Section */}
      <div className="space-y-3 mt-6">
        <div className="flex items-center gap-3 px-3 py-2">
          <img src="https://via.placeholder.com/40" alt="Profile" className="w-10 h-10 rounded-full" />
          <div>
            <p className="font-semibold text-sm">Aeron Carabuena</p>
            <span className="text-xs bg-green-100 text-green-700 px-2 py-0.5 rounded-full">Admin</span>
          </div>
        </div>

        <ul className="space-y-2 text-gray-700 text-sm">
          <li>
            <Link to="/settings" className="flex items-center gap-3 px-3 py-2 rounded-md hover:bg-gray-100">
              <Icon icon="mdi:cog-outline" className="text-xl" /> Settings
            </Link>
          </li>
          <li>
            <Link to="/login" className="flex items-center gap-3 px-3 py-2 text-red-500 rounded-md hover:bg-red-50">
              <Icon icon="mdi:logout" className="text-xl" /> Log out
            </Link>
          </li>
        </ul>
      </div>
    </div>
  );
};

export default Sidebar;
