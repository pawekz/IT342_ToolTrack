import React, { useState } from "react";
import SidebarLayout from "../components/SidebarLayout";
import ToolModal from "../components/ToolModal";
import Hammer from "../assets/hammer.jpg";

const ToolManagement = () => {
  const [showModal, setShowModal] = useState(false);
  const [editingTool, setEditingTool] = useState(null);
  const [deleteConfirmation, setDeleteConfirmation] = useState(null);
  const [toolItems, setToolItems] = useState([
    {
      tool_id: 1,
      name: "Hammer",
      serial_number: "SN123456",
      tool_condition: "GOOD",
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

  const handleEditTool = (updatedTool) => {
    setToolItems(
        toolItems.map((tool) =>
            tool.tool_id === updatedTool.tool_id ? updatedTool : tool
        )
    );
    setEditingTool(null);
  };

  const handleDeleteTool = (toolId) => {
    // For a real backend, you would make an API call here
    setToolItems(toolItems.filter((tool) => tool.tool_id !== toolId));
    setDeleteConfirmation(null);
  };

  const openEditModal = (tool) => {
    setEditingTool(tool);
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setEditingTool(null);
  };

  // Function to determine condition badge styling
  const getConditionStyle = (condition) => {
    switch (condition) {
      case "NEW":
        return "bg-indigo-50 text-indigo-600 border border-indigo-200";
      case "GOOD":
        return "bg-blue-50 text-blue-600 border border-blue-200";
      case "FAIR":
        return "bg-purple-50 text-purple-600 border border-purple-200";
      case "POOR":
        return "bg-orange-50 text-orange-600 border border-orange-200";
      default:
        return "bg-gray-50 text-gray-600 border border-gray-200";
    }
  };

  return (
      <div className="min-h-screen flex bg-gray-50">
        <SidebarLayout />

        <div className="flex-1 p-6 h-screen overflow-y-auto">
          <div className="h-16 md:hidden" /> {/* Spacer for mobile view */}

          <div className="flex justify-between items-center mb-8">
            <h1 className="text-2xl font-semibold text-gray-800">Tool Management</h1>
            <button
                onClick={() => setShowModal(true)}
                className="cursor-pointer bg-teal-500 text-white px-4 py-2 rounded-lg hover:bg-teal-600 transition-all shadow-sm flex items-center gap-2"
            >
              <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clipRule="evenodd" />
              </svg>
              Add Tool
            </button>
          </div>

          <ToolModal
              show={showModal}
              onClose={closeModal}
              onSubmit={editingTool ? handleEditTool : handleAddTool}
              initialData={editingTool}
              isEditing={!!editingTool}
          />

          {/* Delete Confirmation Modal */}
          {deleteConfirmation && (
              <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                <div className="bg-white rounded-lg p-6 w-full max-w-md">
                  <h3 className="text-lg font-semibold mb-4">Confirm Delete</h3>
                  <p className="mb-6">Are you sure you want to delete "{deleteConfirmation.name}"? This action cannot be undone.</p>
                  <div className="flex justify-end gap-3">
                    <button
                        onClick={() => setDeleteConfirmation(null)}
                        className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
                    >
                      Cancel
                    </button>
                    <button
                        onClick={() => handleDeleteTool(deleteConfirmation.tool_id)}
                        className="px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600"
                    >
                      Delete
                    </button>
                  </div>
                </div>
              </div>
          )}

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {toolItems.map((tool) => (
                <div key={tool.tool_id} className="bg-white rounded-2xl shadow-sm hover:shadow-md transition-shadow overflow-hidden border border-gray-100">
                  <div className="relative">
                    <img
                        src={tool.image_url}
                        alt={tool.name}
                        className="w-full h-48 object-cover"
                    />
                    <div className="absolute top-3 right-3 flex gap-2">
                  <span className={`text-xs px-3 py-1 rounded-full font-medium ${getConditionStyle(tool.tool_condition)}`}>
                    {tool.tool_condition}
                  </span>
                    </div>
                  </div>
                  <div className="p-5">
                    <div className="flex justify-between items-start mb-3">
                      <h2 className="text-lg font-semibold text-gray-800">{tool.name}</h2>
                    </div>
                    <p className="text-sm text-gray-600 mb-4 line-clamp-2">{tool.description}</p>
                    <div className="space-y-2 text-sm text-gray-500">
                      <div className="flex items-center gap-2">
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
                        </svg>
                        <span>{tool.serial_number}</span>
                      </div>
                      <div className="flex items-center gap-2">
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                        </svg>
                        <span>{tool.location}</span>
                      </div>
                    </div>
                  </div>
                  <div className="bg-gray-50 px-5 py-3 flex items-center justify-between">
                    <div className="text-xs text-gray-500 flex items-center gap-1">
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                      </svg>
                      Acquired: {tool.date_acquired}
                    </div>
                    <div className="flex gap-2">
                      <button
                          onClick={() => openEditModal(tool)}
                          className="cursor-pointer text-blue-500 hover:text-blue-600 text-sm font-medium"
                      >
                        Edit
                      </button>
                      <button
                          onClick={() => setDeleteConfirmation(tool)}
                          className="cursor-pointer text-red-500 hover:text-red-600 text-sm font-medium"
                      >
                        Delete
                      </button>
                    </div>
                  </div>
                </div>
            ))}
          </div>
        </div>
      </div>
  );
};

export default ToolManagement;