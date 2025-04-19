import { Link, useLocation } from "react-router-dom";
import { Icon } from "@iconify/react";
import { X } from "lucide-react";

const Sidebar = ({ isOpen, onClose }) => {
  const location = useLocation();
  
  const isActive = (path) => {
    return location.pathname === path;
  };

  const navItems = [
    { path: "/dashboard", icon: "mdi:view-dashboard", label: "Dashboard" },
    { path: "/toolmanagement", icon: "mdi:tools", label: "Tool Management" },
    { path: "/user-management", icon: "mdi:account-group", label: "User Management" },
    { path: "/tools", icon: "mdi:toolbox-outline", label: "Tools" }
  ];

  const bottomNavItems = [
    { path: "/settings", icon: "mdi:cog-outline", label: "Settings" },
    { path: "/login", icon: "mdi:logout", label: "Log out", className: "text-red-500 hover:bg-red-50" }
  ];

  // Brand color
  const brandColor = "#2EA69E";

  return (
    <div
      className={`
        fixed md:static top-0 left-0 z-50 h-screen w-64 bg-white shadow-xl p-5 flex flex-col rounded-r-2xl
        transform transition-transform duration-300 ease-in-out border-r border-gray-100
        ${isOpen ? "translate-x-0" : "-translate-x-full md:translate-x-0"}
      `}
      style={{ "--brand-color": brandColor }}
    >
      {/* Close Button for Mobile */}
      <div className="md:hidden flex justify-end mb-4">
        <button 
          onClick={onClose}
          className="p-1 rounded-full hover:bg-gray-100 transition-colors"
        >
          <X className="w-5 h-5 text-gray-600" />
        </button>
      </div>

      {/* Logo */}
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-center" style={{ color: brandColor }}>ToolTrack</h1>
      </div>

      {/* Search Bar */}
      <div className="relative mb-8">
        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
          <Icon icon="mdi:magnify" className="w-4 h-4 text-gray-400" />
        </div>
        <input
          type="text"
          placeholder="Search"
          className="w-full pl-10 pr-4 py-2 text-sm bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:border-transparent transition-colors"
          style={{ "--tw-ring-color": brandColor }}
        />
      </div>

      {/* Top Section with flex-grow */}
      <div className="flex-grow">
        <nav>
          <ul className="space-y-1">
            {navItems.map((item) => (
              <li key={item.path}>
                <Link 
                  to={item.path} 
                  className={`
                    flex items-center gap-3 px-4 py-3 rounded-lg transition-colors
                    ${isActive(item.path) 
                      ? "font-medium" 
                      : "text-gray-600 hover:bg-gray-50"}
                  `}
                  style={{ 
                    backgroundColor: isActive(item.path) ? `${brandColor}15` : '',
                    color: isActive(item.path) ? brandColor : ''
                  }}
                >
                  <Icon icon={item.icon} className="text-xl" />
                  {item.label}
                </Link>
              </li>
            ))}
          </ul>
        </nav>
      </div>

      {/* Divider */}
      <div className="border-t border-gray-200 my-4"></div>

      {/* Bottom Section */}
      <div>
        {/* User Profile */}
        <div className="flex items-center gap-3 p-3 mb-4 bg-gray-50 rounded-lg">
          <div className="w-10 h-10 rounded-full flex items-center justify-center text-white font-bold"
               style={{ background: brandColor }}>
            AC
          </div>
          <div>
            <p className="font-medium text-gray-800">Aeron Carabuena</p>
            <span className="text-xs px-2 py-0.5 rounded-full" 
                  style={{ backgroundColor: `${brandColor}15`, color: brandColor }}>
              Admin
            </span>
          </div>
        </div>

        {/* Bottom Navigation */}
        <ul className="space-y-1">
          {bottomNavItems.map((item) => (
            <li key={item.path}>
              <Link 
                to={item.path} 
                className={`
                  flex items-center gap-3 px-4 py-3 rounded-lg transition-colors
                  ${item.className || "text-gray-600 hover:bg-gray-50"}
                  ${isActive(item.path) && !item.className ? "bg-gray-50 text-gray-800 font-medium" : ""}
                `}
                style={{ 
                  backgroundColor: isActive(item.path) && !item.className ? `${brandColor}10` : '',
                  color: isActive(item.path) && !item.className ? brandColor : ''
                }}
              >
                <Icon icon={item.icon} className="text-xl" />
                {item.label}
              </Link>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default Sidebar;