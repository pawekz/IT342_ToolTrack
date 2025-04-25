import React, { useState, useEffect } from 'react';

const ToolModal = ({ show, onClose, onSubmit, initialData, isEditing }) => {
  const [step, setStep] = useState(1);
  const [form, setForm] = useState({
    name: '',
    serial_number: '',
    location: '',
    description: '',
    date_acquired: '',
    image_url: '',
    tool_condition: 'NEW',
    status: 'AVAILABLE',
  });

  // Initialize form with data when editing
  useEffect(() => {
    if (initialData) {
      setForm(initialData);
    } else {
      // Reset form when adding a new tool
      setForm({
        name: '',
        serial_number: '',
        location: '',
        description: '',
        date_acquired: '',
        image_url: '',
        tool_condition: 'NEW',
        status: 'AVAILABLE',
      });
    }
  }, [initialData]);

  if (!show) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  };

  const handleSubmit = () => {
    onSubmit(form);
    onClose();
    setStep(1); // Reset step when closed
  };

  const nextStep = () => {
    setStep(2);
  };

  const prevStep = () => {
    setStep(1);
  };

  const renderQRCodeStep = () => {
    // Mock QR code generation step
    return (
      <div className="space-y-6">
        <div className="text-center p-6">
          <div className="flex justify-center mb-4">
            <div className="w-48 h-48 bg-gray-100 border border-gray-200 rounded-lg flex items-center justify-center">
              <svg className="w-24 h-24 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 20h4M4 12h4m12 0h.01M5 8h2a1 1 0 001-1V5a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1zm12 0h2a1 1 0 001-1V5a1 1 0 00-1-1h-2a1 1 0 00-1 1v2a1 1 0 001 1zM5 20h2a1 1 0 001-1v-2a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1z"></path>
              </svg>
            </div>
          </div>
          <h3 className="text-lg font-medium text-gray-800">QR Code for {form.name}</h3>
          <p className="text-gray-500 text-sm mt-2">
            This QR code can be used for quick tool checkout and identification
          </p>
        </div>
        
        <div className="bg-gray-50 rounded-lg p-4">
          <h4 className="font-medium text-gray-700 mb-2">Tool Details</h4>
          <div className="grid grid-cols-2 gap-2 text-sm text-gray-600">
            <div>Name: <span className="font-medium">{form.name}</span></div>
            <div>Serial: <span className="font-medium">{form.serial_number}</span></div>
            <div>Location: <span className="font-medium">{form.location}</span></div>
            <div>Status: <span className="font-medium">{form.status}</span></div>
          </div>
        </div>
      </div>
    );
  };

  const renderForm = () => {
    return (
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Tool Name</label>
          <input
            type="text"
            name="name"
            value={form.name}
            onChange={handleChange}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent"
            placeholder="Enter tool name"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Serial Number</label>
          <input
            type="text"
            name="serial_number"
            value={form.serial_number}
            onChange={handleChange}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent"
            placeholder="Enter serial number"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Location</label>
          <input
            type="text"
            name="location"
            value={form.location}
            onChange={handleChange}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent"
            placeholder="Shelf A1 / Locker B2"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
          <input
            type="text"
            name="description"
            value={form.description}
            onChange={handleChange}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent"
            placeholder="Short description"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Date Acquired</label>
          <input
            type="date"
            name="date_acquired"
            value={form.date_acquired}
            onChange={handleChange}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Image URL</label>
          <input
            type="text"
            name="image_url"
            value={form.image_url}
            onChange={handleChange}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent"
            placeholder="https://example.com/image.jpg"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Condition</label>
          <select
            name="tool_condition"
            value={form.tool_condition}
            onChange={handleChange}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent bg-white"
          >
            <option value="NEW">NEW</option>
            <option value="GOOD">GOOD</option>
            <option value="FAIR">FAIR</option>
            <option value="POOR">POOR</option>
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Status</label>
          <select
            name="status"
            value={form.status}
            onChange={handleChange}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent bg-white"
          >
            <option value="AVAILABLE">AVAILABLE</option>
            <option value="BORROWED">BORROWED</option>
            <option value="MAINTENANCE">MAINTENANCE</option>
            <option value="RETIRED">RETIRED</option>
          </select>
        </div>
      </div>
    );
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
      <div className="bg-white rounded-xl shadow-xl w-full max-w-2xl p-6 animate-fade-in">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl font-semibold text-gray-800">
            {isEditing 
              ? (step === 1 ? "Edit Tool" : "Update QR Code") 
              : (step === 1 ? "Add New Tool" : "Generate QR Code")}
          </h2>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-gray-700 focus:outline-none"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        {/* Step indicator */}
        <div className="mb-6">
          <div className="flex items-center">
            <div className={`flex items-center justify-center w-8 h-8 rounded-full ${step === 1 ? 'bg-teal-500 text-white' : 'bg-teal-100 text-teal-500'} font-medium text-sm`}>
              1
            </div>
            <div className={`flex-1 h-1 mx-2 ${step === 2 ? 'bg-teal-500' : 'bg-teal-100'}`}></div>
            <div className={`flex items-center justify-center w-8 h-8 rounded-full ${step === 2 ? 'bg-teal-500 text-white' : 'bg-teal-100 text-teal-500'} font-medium text-sm`}>
              2
            </div>
          </div>
          <div className="flex justify-between mt-1 text-xs text-gray-500">
            <span>Tool Details</span>
            <span>QR Code</span>
          </div>
        </div>

        {step === 1 ? renderForm() : renderQRCodeStep()}

        <div className="mt-6 flex justify-end gap-3">
          {step === 2 && (
            <button
              onClick={prevStep}
              className="px-5 py-2 border border-gray-300 text-gray-700 rounded-lg text-sm font-medium hover:bg-gray-50"
            >
              Back
            </button>
          )}
          
          {step === 1 ? (
            <button
              onClick={nextStep}
              className="bg-teal-500 text-white px-5 py-2 rounded-lg text-sm font-medium hover:bg-teal-600 shadow-sm"
            >
              Next
            </button>
          ) : (
            <button
              onClick={handleSubmit}
              className="bg-teal-500 text-white px-5 py-2 rounded-lg text-sm font-medium hover:bg-teal-600 shadow-sm"
            >
              {isEditing ? "Update Tool" : "Save Tool"}
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default ToolModal;