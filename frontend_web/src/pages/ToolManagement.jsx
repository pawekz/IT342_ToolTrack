import React, { useState } from "react";
import SidebarLayout from "../components/SidebarLayout";
import ToolModal from "../components/ToolModal";
import Hammer from "../assets/hammer.jpg";

const ToolManagement = () => {
  const [showModal, setShowModal] = useState(false);
  const [toolItems, setToolItems] = useState([
    {
      tool_id: 1,
      name: "Hammer",
      serial_number: "SN123456",
      tool_condition: "GOOD",
      status: "BORROWED",
      location: "Shelf A1",
      description: "A tool for hammering nails.",
      image_url: Hammer,
      date_acquired: "2023-06-15",
    },
    {
      tool_id: 2,
      name: "Welding Mask",
      serial_number: "SN654321",
      tool_condition: "NEW",
      status: "AVAILABLE",
      location: "Locker B2",
      description: "Protective welding mask with auto-darkening feature.",
      image_url: "https://via.placeholder.com/400x250",
      date_acquired: "2024-01-05",
    },
    {
      tool_id: 3,
      name: "Impact Wrench",
      serial_number: "SN789012",
      tool_condition: "FAIR",
      status: "MAINTENANCE",
      location: "Garage C",
      description: "Battery-powered impact wrench for automotive use.",
      image_url: "https://via.placeholder.com/400x250",
      date_acquired: "2022-10-20",
    },
  ]);

  const handleAddTool = (tool) => {
    const toolWithId = {
      ...tool,
      tool_id: toolItems.length + 1,
    };
    setToolItems([...toolItems, toolWithId]);
  };

  return (
    <div className="min-h-screen flex bg-gray-100">
      <SidebarLayout />
      
      <div className="flex-1 p-6">

        <div className="h-16 md:hidden" /> {/* Spacer for mobile view */}

        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-semibold text-gray-800">Tool Management</h1>
          <button
            onClick={() => setShowModal(true)}
            className="bg-teal-500 text-white px-4 py-2 rounded-md hover:bg-teal-600"
          >
            Add Tool
          </button>
        </div>

        <ToolModal
          show={showModal}
          onClose={() => setShowModal(false)}
          onSubmit={handleAddTool}
        />

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {toolItems.map((tool) => (
            <div key={tool.tool_id} className="bg-white rounded-xl shadow overflow-hidden">
              <img
                src={tool.image_url}
                alt={tool.name}
                className="w-full h-48 object-cover"
              />
              <div className="p-4">
                <div className="flex justify-between items-start mb-1">
                  <h2 className="text-lg font-bold text-gray-800">{tool.name}</h2>
                  <div className="flex gap-1">
                    <span className="text-xs bg-blue-100 text-blue-600 px-2 py-0.5 rounded-full uppercase font-semibold">
                      {tool.tool_condition}
                    </span>
                    <span
                      className={`text-xs px-2 py-0.5 rounded-full uppercase font-semibold ${
                        tool.status === "AVAILABLE"
                          ? "bg-green-100 text-green-600"
                          : tool.status === "BORROWED"
                          ? "bg-yellow-100 text-yellow-600"
                          : tool.status === "MAINTENANCE"
                          ? "bg-red-100 text-red-600"
                          : "bg-gray-200 text-gray-600"
                      }`}
                    >
                      {tool.status}
                    </span>
                  </div>
                </div>
                <p className="text-sm text-gray-600 mb-2">{tool.description}</p>
                <div className="text-xs text-gray-500 mb-1">
                  <span className="font-medium">Serial:</span> {tool.serial_number}
                </div>
                <div className="text-xs text-gray-500">
                  <span className="font-medium">Location:</span> {tool.location}
                </div>
              </div>
              <div className="bg-gray-50 px-4 py-2 text-xs text-gray-400">
                Acquired on: {tool.date_acquired}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default ToolManagement;
