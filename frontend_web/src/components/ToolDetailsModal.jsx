import React, { useState } from "react";

const ToolDetailsModal = ({ show, onClose, tool }) => {
    const [qrLoading, setQrLoading] = useState(true);

    if (!show || !tool) return null;

    return (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex justify-center items-center z-50">
            <div className="bg-white rounded-xl shadow-xl w-full max-w-2xl p-6 animate-fade-in">
                {/* Header */}
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-xl font-semibold text-gray-800">Tool Details</h2>
                    <button
                        onClick={onClose}
                        className="text-gray-400 hover:text-gray-600"
                    >
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                        </svg>
                    </button>
                </div>

                {/* Main Content */}
                <div className="flex flex-col md:flex-row gap-6">
                    {/* Left: Tool image */}
                    <div className="flex-shrink-0">
                        <img
                            src={tool.image_url}
                            alt={tool.name}
                            className="w-48 h-48 object-cover rounded-lg border"
                        />
                    </div>

                    {/* Right: Tool Info */}
                    <div className="flex-1 space-y-2 text-gray-700">
                        <div><span className="font-medium">Name:</span> {tool.name}</div>
                        <div><span className="font-medium">Location:</span> {tool.location}</div>
                        <div><span className="font-medium">Condition:</span> {tool.tool_condition}</div>
                        <div><span className="font-medium">Date Acquired:</span> {tool.date_acquired}</div>
                        <div><span className="font-medium">Description:</span> {tool.description}</div>
                        <div><span className="font-medium">Category:</span> {tool.category}</div>
                    </div>
                </div>

                {/* QR Code Section */}
                {tool.qr_code && (
                    <div className="mt-8 text-center">
                        <h3 className="text-md font-semibold mb-2">QR Code</h3>
                        <div className="flex justify-center">
                            <div className="relative w-40 h-40 border rounded-lg flex items-center justify-center bg-gray-50">
                                {qrLoading && (
                                    <svg className="animate-spin h-8 w-8 text-teal-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                    </svg>
                                )}
                                <img
                                    src={tool.qr_code}
                                    alt="Tool QR Code"
                                    className={`h-full w-full object-contain rounded-md ${qrLoading ? "hidden" : "block"}`}
                                    onLoad={() => setQrLoading(false)}
                                />
                            </div>
                        </div>

                        {/* Download button */}
                        {!qrLoading && (
                            <div className="mt-4">
                                <a
                                    href={tool.qr_code}
                                    download={`tool-${tool.tool_id}-qrcode.png`}
                                    className="inline-block bg-teal-500 text-white text-sm px-4 py-2 rounded-md hover:bg-teal-600 transition"
                                >
                                    Download QR Code
                                </a>
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default ToolDetailsModal;
