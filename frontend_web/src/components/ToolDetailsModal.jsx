import React, { useState, useRef } from "react";
import { useReactToPrint } from "react-to-print";
import printJS from "print-js";

const ToolDetailsModal = ({ show, onClose, tool }) => {
    const [qrLoading, setQrLoading] = useState(true);
    const qrPrintRef = useRef();

    // Handle print via React-to-Print (for browser printing)
    const handlePrint = useReactToPrint({
        content: () => qrPrintRef.current,
        documentTitle: `ToolTrack-QR-${tool?.tool_id || "code"}`,
        pageStyle: `
            @page {
                size: 50mm 30mm;
                margin: 0;
            }
            @media print {
                body {
                    margin: 0;
                    padding: 0;
                }
                .qr-print-container {
                    width: 50mm;
                    height: 30mm;
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    justify-content: center;
                }
                .qr-image {
                    max-width: 26mm;
                    max-height: 26mm;
                }
                .qr-label {
                    font-size: 6pt;
                    margin-top: 1mm;
                    text-align: center;
                }
            }
        `,
    });

    // Alternative method using Print-JS (better for some thermal printers)
/*
    const handleThermalPrint = () => {
        // First download the QR image if it doesn't exist locally
        fetch(tool.qr_code)
            .then(response => response.blob())
            .then(blob => {
                const imageUrl = URL.createObjectURL(blob);

                // Use Print-JS to print the image with specific dimensions
                printJS({
                    printable: imageUrl,
                    type: 'image',
                    documentTitle: `Tool-${tool.tool_id}-QR`,
                    imageStyle: 'width:26mm; max-width:26mm; max-height:26mm;',
                    style: `
                        @page {
                            size: 50mm 30mm;
                            margin: 0;
                        }
                    `,
                    onLoadingEnd: () => {
                        // Clean up the object URL after printing
                        URL.revokeObjectURL(imageUrl);
                    }
                });
            })
            .catch(error => {
                console.error("Error printing QR code:", error);
                // Fallback to regular print method
                handlePrint();
            });
    };
*/

    // Update your handleThermalPrint function to use react-to-print directly
    const handleThermalPrint = () => {
        // Skip the fetch and just use react-to-print which already works
        handlePrint();
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
                                    src={tool.qr_code}
                                    alt="Tool QR Code"
                                    className={`h-full w-full object-contain rounded-md ${qrLoading ? "hidden" : "block"}`}
                                    onLoad={() => setQrLoading(false)}
                                />
                            </div>
                        </div>

                        {/* Hidden element for printing */}
                        <div style={{ display: "none" }}>
                            <div ref={qrPrintRef} className="qr-print-container">
                                <img 
                                    src={tool.qr_code} 
                                    alt="Printable QR" 
                                    className="qr-image"
                                />
                                <div className="qr-label">
                                    {tool.name} - ID: {tool.tool_id}
                                </div>
                            </div>
                        </div>

                        {/* Button Actions */}
                        {!qrLoading && (
                            <div className="mt-4 flex justify-center gap-3">
                                <a
                                    href={tool.qr_code}
                                    download={`tool-${tool.tool_id}-qrcode.png`}
                                    className="inline-block bg-teal-500 text-white text-sm px-4 py-2 rounded-md hover:bg-teal-600 transition flex items-center justify-center min-w-[120px]"
                                >
                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                                    </svg>
                                    Download QR Code
                                </a>
                                <button
                                    onClick={handleThermalPrint}
                                    className="inline-block bg-blue-500 text-white text-sm px-4 py-2 rounded-md hover:bg-blue-600 transition flex items-center justify-center min-w-[120px]"
                                >
                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
                                    </svg>
                                    Print QR
                                </button>
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default ToolDetailsModal;