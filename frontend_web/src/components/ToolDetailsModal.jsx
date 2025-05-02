import React, { useState, useEffect } from "react";

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
                                    src={localQrUrl || tool.qr_code}
                                    alt="Tool QR Code"
                                    className={`h-full w-full object-contain rounded-md ${qrLoading ? "hidden" : "block"}`}
                                    onLoad={() => setQrLoading(false)}
                                />
                            </div>
                        </div>

                        {/* Print Preview */}
                        <div className="mt-4 border border-gray-200 rounded-lg p-2 inline-block bg-gray-50">
                            <div className="text-xs text-gray-500 mb-1">Print Preview</div>
                            <div className="w-[30mm] h-[50mm] border border-gray-300 mx-auto bg-white flex flex-col items-center justify-center p-2">
                                <img
                                    src={localQrUrl || tool.qr_code}
                                    alt="QR Preview"
                                    className="w-[22mm] h-[22mm] object-contain"
                                />
                                <div className="text-[6pt] mt-1.5 text-center overflow-hidden text-ellipsis w-full">
                                    {tool.name}
                                </div>
                                <div className="text-[5pt] mt-0.5">
                                    ID: {tool.tool_id}
                                </div>
                            </div>
                        </div>

                        {/* Button Actions */}
                        <div className="mt-4 flex justify-center gap-3">
                            <a
                                href={localQrUrl || tool.qr_code}
                                download={`tool-${tool.tool_id}-qrcode.png`}
                                className="inline-block bg-teal-500 text-white p-3 rounded-md hover:bg-teal-600 transition flex items-center justify-center w-12 h-12 relative group"
                                aria-label="Download QR Code"
                            >
                                <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                                </svg>
                                <div className="absolute bottom-full mb-2 px-2 py-1 bg-gray-800 text-white text-xs rounded opacity-0 group-hover:opacity-100 transition-opacity duration-200 whitespace-nowrap pointer-events-none">
                                    Download QR Code
                                </div>
                            </a>
                            <button
                                onClick={printQRCode}
                                className="inline-block bg-blue-500 text-white p-3 rounded-md hover:bg-blue-600 transition flex items-center justify-center w-12 h-12 relative group"
                                aria-label="Print QR Code"
                            >
                                <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
                                </svg>
                                <div className="absolute bottom-full mb-2 px-2 py-1 bg-gray-800 text-white text-xs rounded opacity-0 group-hover:opacity-100 transition-opacity duration-200 whitespace-nowrap pointer-events-none">
                                    Print QR
                                </div>
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default ToolDetailsModal;