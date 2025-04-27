import React, { useState } from 'react';
import { X, Info, ChevronRight } from 'lucide-react';

// Mock tool data - in a real implementation, this would be passed as props or fetched
const toolDetails = [
    {
        tool_id: 1,
        name: "Hammer",
        serial_number: "SN123456",
        tool_condition: "GOOD",
        location: "Shelf A1",
        description: "A tool for hammering nails.",
        image_url: "https://i.pravatar.cc/150?img=20", // Placeholder for demo
        date_acquired: "2023-06-15",
    },
    {
        tool_id: 2,
        name: "Ballpen",
        serial_number: "SN123457",
        tool_condition: "NEW",
        location: "Drawer D3",
        description: "High-quality ballpoint pen for writing.",
        image_url: "https://i.pravatar.cc/150?img=21", // Placeholder for demo
        date_acquired: "2023-08-10",
    },
    {
        tool_id: 3,
        name: "Wireless Drill",
        serial_number: "SN654321",
        tool_condition: "GOOD",
        location: "Cabinet E2",
        description: "Cordless power drill with rechargeable battery.",
        image_url: "https://i.pravatar.cc/150?img=22", // Placeholder for demo
        date_acquired: "2022-11-19",
    },
    {
        tool_id: 4,
        name: "Laptop",
        serial_number: "SN789012",
        tool_condition: "FAIR",
        location: "IT Room",
        description: "Business laptop for general office use.",
        image_url: "https://i.pravatar.cc/150?img=23", // Placeholder for demo
        date_acquired: "2022-05-08",
    },
];

const UserModal = ({ user, isOpen, onClose }) => {
    const [showToolDetails, setShowToolDetails] = useState(false);

    if (!isOpen || !user) return null;

    // Find the tool details that match the user's requested tool
    const toolDetail = toolDetails.find(tool => tool.name === user.tool);

    const toggleToolDetails = () => {
        setShowToolDetails(!showToolDetails);
    };

    return (
        <div className="fixed inset-0 bg-black/25 backdrop-blur-sm flex justify-center items-center z-50 overflow-y-auto">
            <div className="bg-white rounded-xl shadow-lg w-full max-w-md overflow-hidden border border-gray-100 animate-fade-in">
                {/* Header with close button */}
                <div className="flex justify-between items-center p-5 border-b border-gray-100">
                    <h3 className="text-lg font-semibold text-gray-800">User Details</h3>
                    <button
                        onClick={onClose}
                        className="p-1 rounded-full hover:bg-gray-100 transition-colors"
                    >
                        <X className="cursor-pointer w-5 h-5 text-gray-500" />
                    </button>
                </div>

                {/* User profile content */}
                <div className="p-6">
                    <div className="flex flex-col items-center mb-6">
                        <div className="w-24 h-24 rounded-full overflow-hidden border-4 border-teal-50 shadow-sm mb-4">
                            <img
                                src={user.avatar}
                                alt={`${user.name}'s profile`}
                                className="w-full h-full object-cover"
                            />
                        </div>
                        <h4 className="text-xl font-semibold text-gray-800">{user.name}</h4>
                    </div>

                    <div className="space-y-4">
                        <div className="bg-gray-50 p-3 rounded-lg">
                            <p className="text-sm text-gray-500">Email</p>
                            <p className="text-gray-800 font-medium">{user.email || 'email@example.com'}</p>
                        </div>

                        <div className="bg-gray-50 p-3 rounded-lg">
                            <div className="flex justify-between items-center">
                                <div>
                                    <p className="text-sm text-gray-500">Tool Requested</p>
                                    <div className="mt-1">
                    <span className="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-teal-50 text-teal-700 border border-teal-100">
                      {user.tool}
                    </span>
                                    </div>
                                </div>
                                {toolDetail && (
                                    <button
                                        onClick={toggleToolDetails}
                                        className="cursor-pointer flex items-center text-teal-600 text-xs font-medium hover:text-teal-700 transition-colors"
                                    >
                                        <Info className="w-4 h-4 mr-1" />
                                        Tool Details
                                        <ChevronRight className={`w-4 h-4 transition-transform ${showToolDetails ? 'rotate-90' : ''}`} />
                                    </button>
                                )}
                            </div>

                            {/* Tool details expandable section */}
                            {showToolDetails && toolDetail && (
                                <div className="mt-3 pt-3 border-t border-gray-200 animate-fade-in">
                                    <div className="flex gap-3">
                                        <div className="w-16 h-16 bg-gray-100 rounded-md overflow-hidden flex-shrink-0">
                                            <img
                                                src={toolDetail.image_url}
                                                alt={toolDetail.name}
                                                className="w-full h-full object-cover"
                                            />
                                        </div>
                                        <div className="flex-1">
                                            <h5 className="font-medium text-gray-800">{toolDetail.name}</h5>
                                            <p className="text-xs text-gray-500 mt-1">{toolDetail.description}</p>
                                        </div>
                                    </div>

                                    <div className="mt-3 grid grid-cols-2 gap-2 text-xs">
                                        <div>
                                            <p className="text-gray-500">Serial Number</p>
                                            <p className="font-medium text-gray-700">{toolDetail.serial_number}</p>
                                        </div>
                                        <div>
                                            <p className="text-gray-500">Condition</p>
                                            <p className="font-medium text-gray-700">{toolDetail.tool_condition}</p>
                                        </div>
                                        <div>
                                            <p className="text-gray-500">Location</p>
                                            <p className="font-medium text-gray-700">{toolDetail.location}</p>
                                        </div>
                                        <div>
                                            <p className="text-gray-500">Acquired</p>
                                            <p className="font-medium text-gray-700">{toolDetail.date_acquired}</p>
                                        </div>
                                    </div>
                                </div>
                            )}
                        </div>

                        <div className="bg-gray-50 p-3 rounded-lg">
                            <p className="text-sm text-gray-500">Request Status</p>
                            <p className="text-gray-800 font-medium">Pending Approval</p>
                        </div>
                    </div>
                </div>

                {/* Actions footer */}
                <div className="p-5 border-t border-gray-100 bg-gray-50 flex justify-end gap-3">
                    <button
                        onClick={onClose}
                        className="cursor-pointer px-4 py-2 border border-gray-200 text-gray-700 font-medium rounded-lg hover:bg-gray-100 transition-colors"
                    >
                        Close
                    </button>
                    <button className="cursor-pointer px-4 py-2 bg-teal-600 text-white font-medium rounded-lg hover:bg-teal-700 transition-colors">
                        View History
                    </button>
                </div>
            </div>
        </div>
    );
};

export default UserModal;