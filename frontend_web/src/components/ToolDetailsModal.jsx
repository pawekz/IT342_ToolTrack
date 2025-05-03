import React, { useState, useEffect } from "react";

// Create a function to format the date string
const formatDate = (dateString) => {
    if (!dateString) return 'N/A';

    try {
        // Parse the ISO date string
        const date = new Date(dateString);

        // Check if date is valid
        if (isNaN(date.getTime())) return 'Invalid Date';

        // Adjust for UTC+8
        const utcPlus8Date = new Date(date.getTime() + (8 * 60 * 60 * 1000));

        // Format the date as "Month DD YYYY h:mm AM/PM"
        const options = {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: 'numeric',
            minute: '2-digit',
            hour12: true
        };

        return utcPlus8Date.toLocaleString('en-US', options);
    } catch (error) {
        console.error("Error formatting date:", error);
        return dateString; // Return original string if there's an error
    }
};

const ToolDetailsModal = ({ show, onClose, tool }) => {
    const [qrLoading, setQrLoading] = useState(true);
    const [localQrUrl, setLocalQrUrl] = useState(null);

    // Reset state when modal closes
    useEffect(() => {
        if (!show) {
            setLocalQrUrl(null);
            setQrLoading(true);
        }
    }, [show]);

    // Download QR code once when modal opens
    useEffect(() => {
        if (show && tool && tool.qr_code && !localQrUrl) {
            try {
                // Create a new Image object to preload
                const img = new Image();
                img.crossOrigin = "anonymous"; // Try with this first

                img.onload = () => {
                    try {
                        // Create a canvas to draw the image
                        const canvas = document.createElement('canvas');
                        canvas.width = img.width;
                        canvas.height = img.height;
                        const ctx = canvas.getContext('2d');
                        ctx.drawImage(img, 0, 0);

                        // Convert to data URL
                        const dataUrl = canvas.toDataURL('image/png');
                        setLocalQrUrl(dataUrl);
                        setQrLoading(false);
                    } catch (err) {
                        console.error("Canvas processing error:", err);
                        setQrLoading(false);
                    }
                };

                img.onerror = () => {
                    console.warn("Failed to load image with crossOrigin");
                    setQrLoading(false);
                };

                img.src = tool.qr_code;
            } catch (err) {
                console.error("Error in QR loading effect:", err);
                setQrLoading(false);
            }
        } else if (show && tool && !tool.qr_code) {
            // No QR code available, so we're not loading one
            setQrLoading(false);
        }
    }, [show, tool, localQrUrl]);

    // Customizable printing function with text and positioning
    const printQRCode = () => {
        // Use the local URL if available, otherwise fall back to the original
        const imageSrc = localQrUrl || tool.qr_code;
        const toolName = tool.name || 'Tool';
        // const toolId = tool.tool_id || 'Unknown ID';

        // Create a new window
        const printWindow = window.open('', '_blank');
        if (!printWindow) {
            alert('Please allow pop-ups for this website to print QR codes');
            return;
        }

        // Write the image and print-specific HTML/CSS to the new window
        // Updated for 30mm height x 50mm width paper orientation
        printWindow.document.write(`
            <!DOCTYPE html>
            <html>
            <head>
                <title>Print QR Code</title>
                <style>
                    @page {
                        size: 30mm 50mm; /* Height x Width */
                        margin: 0;
                    }
                    body {
                        margin: 0;
                        padding: 0;
                        width: 30mm;
                        height: 50mm;
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        justify-content: center;
                        font-family: Arial, sans-serif;
                    }
                    .qr-container {
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        justify-content: center;
                        padding: 2mm;
                        box-sizing: border-box;
                        height: 100%;
                    }
                    .qr-image {
                        width: 22mm;
                        height: 22mm;
                        object-fit: contain;
                    }
                    .qr-text {
                        margin-top: 1.5mm;
                        font-size: 6pt;
                        line-height: 1.2;
                        text-align: center;
                        max-width: 26mm;
                        overflow: hidden;
                        white-space: nowrap;
                        text-overflow: ellipsis;
                    }
                    .qr-id {
                        font-size: 5pt;
                        margin-top: 0.5mm;
                    }
                </style>
            </head>
            <body>
                <div class="qr-container">
                    <img src="${imageSrc}" class="qr-image" alt="QR Code">
                    <div class="qr-text">${toolName}</div>
                    <!-- temporarily add some tool ID -->
                </div>
                <script>
                    // Wait for image to load before printing
                    document.querySelector('.qr-image').onload = function() {
                        setTimeout(function() {
                            window.print();
                            setTimeout(function() {
                                window.close();
                            }, 300);
                        }, 200);
                    };
                </script>
            </body>
            </html>
        `);

        printWindow.document.close();
    };

    if (!show || !tool) return null;

    // Format the date when rendering
    const formattedDate = formatDate(tool.date_acquired);

    return (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex justify-center items-center z-50">
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-3xl overflow-hidden animate-fade-in">
                {/* Header */}
                <div className="bg-gradient-to-r from-teal-500 to-blue-500 px-6 py-4 flex justify-between items-center">
                    <h2 className="text-xl font-bold text-white flex items-center">
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z" />
                        </svg>
                        Tool Details
                    </h2>
                    <button
                        onClick={onClose}
                        className="text-white hover:bg-white/20 rounded-full p-1 transition-all"
                    >
                        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                        </svg>
                    </button>
                </div>

                {/* Main Content */}
                <div className="p-6">
                    <div className="flex flex-col md:flex-row gap-6">
                        {/* Left: Tool image */}
                        <div className="flex-shrink-0">
                            <div className="relative rounded-xl overflow-hidden shadow-lg border border-gray-200">
                                <img
                                    src={tool.image_url}
                                    alt={tool.name}
                                    className="w-56 h-56 object-cover"
                                />
                                <div className="absolute top-2 right-2 bg-blue-500 text-white text-xs font-bold px-2 py-1 rounded-full">
                                    {tool.category}
                                </div>
                            </div>
                        </div>

                        {/* Right: Tool Info */}
                        <div className="flex-1 space-y-4">
                            <h3 className="text-2xl font-bold text-gray-800">{tool.name}</h3>

                            <div className="grid grid-cols-2 gap-x-4 gap-y-3">
                                <div className="flex items-center">
                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-teal-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                                    </svg>
                                    <div>
                                        <span className="text-sm text-gray-500">Location</span>
                                        <p className="font-medium text-gray-800">{tool.location}</p>
                                    </div>
                                </div>

                                <div className="flex items-center">
                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-teal-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                                    </svg>
                                    <div>
                                        <span className="text-sm text-gray-500">Condition</span>
                                        <p className="font-medium text-gray-800">{tool.tool_condition}</p>
                                    </div>
                                </div>

                                <div className="flex items-center">
                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-teal-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                    </svg>
                                    <div>
                                        <span className="text-sm text-gray-500">Date Acquired</span>
                                        <p className="font-medium text-gray-800">{formattedDate}</p>
                                    </div>
                                </div>

                                <div className="flex items-center">
                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-teal-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
                                    </svg>
                                    <div>
                                        <span className="text-sm text-gray-500">ID</span>
                                        <p className="font-medium text-gray-800">{tool.tool_id}</p>
                                    </div>
                                </div>
                            </div>

                            <div className="pt-2">
                                <span className="text-sm text-gray-500">Description</span>
                                <p className="mt-1 text-gray-700">{tool.description}</p>
                            </div>
                        </div>
                    </div>

                    {/* QR Code Section */}
                    {tool.qr_code && (
                        <div className="mt-8 border-t border-gray-200 pt-6">
                            <h3 className="text-lg font-semibold mb-4 text-gray-800 flex items-center">
                                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 20h4M4 12h4m12 0h.01M5 8h2a1 1 0 001-1V5a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1zm12 0h2a1 1 0 001-1V5a1 1 0 00-1-1h-2a1 1 0 00-1 1v2a1 1 0 001 1zM5 20h2a1 1 0 001-1v-2a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1z" />
                                </svg>
                                QR Code
                            </h3>

                            <div className="flex flex-col md:flex-row items-center gap-8">
                                {/* Print Preview */}
                                <div className="bg-gray-50 rounded-xl p-4 shadow-inner">
                                    <div className="text-xs text-gray-500 mb-2 text-center">Print Preview</div>
                                    <div className="w-[30mm] h-[50mm] border border-gray-300 mx-auto bg-white flex flex-col items-center justify-center p-2 rounded-lg shadow-sm">
                                        {qrLoading ? (
                                            <div className="w-[22mm] h-[22mm] flex items-center justify-center">
                                                <svg className="animate-spin h-6 w-6 text-teal-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                                </svg>
                                            </div>
                                        ) : (
                                            <img
                                                src={localQrUrl || tool.qr_code}
                                                alt="QR Preview"
                                                className="w-[22mm] h-[22mm] object-contain"
                                            />
                                        )}
                                        <div className="text-[6pt] mt-1.5 text-center overflow-hidden text-ellipsis w-full">
                                            {tool.name}
                                        </div>
                                        <div className="text-[5pt] mt-0.5">
                                            ID: {tool.tool_id}
                                        </div>
                                    </div>
                                </div>

                                {/* Button Actions */}
                                <div className="flex flex-col space-y-4 flex-1">
                                    <p className="text-sm text-gray-600">
                                        You can download or print this QR code for easy access to tool information.
                                    </p>

                                    <div className="flex gap-4">
                                        <a
                                            href={localQrUrl || tool.qr_code}
                                            download={`tool-${tool.tool_id}-qrcode.png`}
                                            className="flex-1 bg-gradient-to-r from-teal-500 to-teal-600 hover:from-teal-600 hover:to-teal-700 text-white py-3 px-4 rounded-lg transition-all duration-200 flex items-center justify-center shadow-md hover:shadow-lg"
                                            aria-label="Download QR Code"
                                        >
                                            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                                            </svg>
                                            Download QR
                                        </a>
                                        <button
                                            onClick={printQRCode}
                                            className="flex-1 bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700 text-white py-3 px-4 rounded-lg transition-all duration-200 flex items-center justify-center shadow-md hover:shadow-lg"
                                            aria-label="Print QR Code"
                                        >
                                            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
                                            </svg>
                                            Print QR
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ToolDetailsModal;