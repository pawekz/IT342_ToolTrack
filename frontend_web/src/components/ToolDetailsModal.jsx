import React, { useState, useRef, useEffect } from "react";
import { useReactToPrint } from "react-to-print";
import printJS from "print-js";

const ToolDetailsModal = ({ show, onClose, tool }) => {
    const [qrLoading, setQrLoading] = useState(true);
    const [localQrUrl, setLocalQrUrl] = useState(null);
    const qrPrintRef = useRef();

    // Download QR code once when modal opens
    useEffect(() => {
        if (show && tool?.qr_code && !localQrUrl) {
            // Create a new Image object to preload
            const img = new Image();
            img.crossOrigin = "anonymous"; // Try with this first
            img.onload = () => {
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
            };
            img.onerror = () => {
                // If loading fails with crossOrigin, use direct URL
                // Note: This will still show in the UI but won't be printable with printJS
                console.warn("Failed to load image with crossOrigin, using direct URL");
                setQrLoading(false);
            };
            img.src = tool.qr_code;
        }
    }, [show, tool, localQrUrl]);

    // Handle print via React-to-Print (browser printing)
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

    // Modified to use the local QR URL
    const handleThermalPrint = () => {
        if (localQrUrl) {
            printJS({
                printable: localQrUrl,
                type: 'image',
                documentTitle: `Tool-${tool.tool_id}-QR`,
                imageStyle: 'width:26mm; max-width:26mm; max-height:26mm;',
                style: `
                    @page {
                        size: 50mm 30mm;
                        margin: 0;
                    }
                `,
            });
        } else {
            // Fall back to handlePrint if localQrUrl is not available
            handlePrint();
        }
    };

    // Modify your render function to use localQrUrl when available
    // Update the image source in the main display and hidden print element
    // ...

    return (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex justify-center items-center z-50">
            {/* Rest of your JSX */}
            {/* Just update the image sources */}
            <img
                src={localQrUrl || tool.qr_code}
                alt="Tool QR Code"
                className={`h-full w-full object-contain rounded-md ${qrLoading ? "hidden" : "block"}`}
                onLoad={() => setQrLoading(false)}
            />

            {/* Also update the hidden element for printing */}
            <div style={{ display: "none" }}>
                <div ref={qrPrintRef} className="qr-print-container">
                    <img
                        src={localQrUrl || tool.qr_code}
                        alt="Printable QR"
                        className="qr-image"
                    />
                    <div className="qr-label">
                        {tool.name} - ID: {tool.tool_id}
                    </div>
                </div>
            </div>

            {/* Button Actions - update download link to use localQrUrl */}
            {!qrLoading && (
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
                        {/* Tooltip */}
                        <div className="absolute bottom-full mb-2 px-2 py-1 bg-gray-800 text-white text-xs rounded opacity-0 group-hover:opacity-100 transition-opacity duration-200 whitespace-nowrap pointer-events-none">
                            Download QR Code
                        </div>
                    </a>
                    <button
                        onClick={handleThermalPrint}
                        className="inline-block bg-blue-500 text-white p-3 rounded-md hover:bg-blue-600 transition flex items-center justify-center w-12 h-12 relative group"
                        aria-label="Print QR Code"
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
                        </svg>
                        {/* Tooltip */}
                        <div className="absolute bottom-full mb-2 px-2 py-1 bg-gray-800 text-white text-xs rounded opacity-0 group-hover:opacity-100 transition-opacity duration-200 whitespace-nowrap pointer-events-none">
                            Print QR
                        </div>
                    </button>
                </div>
            )}
        </div>
    );
};

export default ToolDetailsModal;