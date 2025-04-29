import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios'

const ToolModal = ({ show, onClose, onSubmit, initialData, isEditing }) => {
  const chunkSize = 5 * 1024 * 1024; // 5MB

  const [toolId, setToolId] = useState(null);
  const [qrImage, setQrImage] = useState(null);
  const [error, setError] = useState(null);
  const [step, setStep] = useState(1);
  const [validationErrors, setValidationErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [form, setForm] = useState({
    name: '',
    serial_number: '',
    location: '',
    description: '',
    date_acquired: '',
    image: null,
    image_preview: '',
    image_name: '',
    image_url: '',
    category: 'Power Tools',
  });
  const fileInputRef = useRef(null);

  // Predefined locations
  const locationOptions = [
    'Tool Area A',
    'Tool Area B',
    'Equipment Area',
    'Workshop Zone',
    'Storage Room 1',
    'Storage Room 2',
    'Maintenance Bay'
  ];

  // Predefined categories
  const categoryOptions = [
    'Power Tools',
    'Hand Tools',
    'Garden Tools',
    'Electrical Tools',
    'Plumbing Tools',
    'Painting',
    'Automotive Tools',
    'Measuring Tools',
    'Safety Equipment'
  ];

  // Initialize form with data when editing
  useEffect(() => {
    if (initialData) {
      setForm({
        ...initialData,
        image: null,
        image_preview: initialData.image_url || ''
      });
    } else {
      // Reset form when adding a new tool
      setForm({
        name: '',
        serial_number: '',
        location: locationOptions[0], // Default to first location
        description: '',
        date_acquired: '',
        image: null,
        image_preview: '',
        image_url: '',
        category: 'Power Tools',
      });
    }
  }, [initialData]);

  if (!show) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });

    // Clear validation error for this field when the user makes changes
    if (validationErrors[name]) {
      setValidationErrors({
        ...validationErrors,
        [name]: null
      });
    }
  };

  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      setForm({
        ...form,
        image: file,
        image_preview: URL.createObjectURL(file)
      });

      // Clear image validation error if it exists
      if (validationErrors.image) {
        setValidationErrors({
          ...validationErrors,
          image: null
        });
      }
    }
  };

  const triggerFileInput = () => {
    fileInputRef.current.click();
    if(form.image === null){
      setError(null);
    }
  };

  const handleSubmit = async () => {
    setIsSaving(true);

    try {
      const formData = new FormData();

      // Fetch the blob from the blob URL (qrImage is a blob URL)
      const response = await fetch(qrImage);
      const blob = await response.blob();

      // Convert blob into a File (important for multipart upload)
      const file = new File([blob], `${toolId}_qr.png`, { type: 'image/png' });

      // Append the correct fields
      formData.append('file', file);
      formData.append('toolId', toolId);

      // Upload QR code image
      const qrUploadRes = await axios.post(
          'https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/qrcode/uploadImage',
          formData,
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem('token')}`
            }
          }
      );

      if (qrUploadRes.status === 201) {
        const params = new URLSearchParams();
        params.append('image_url', qrUploadRes.data.imageUrl);
        params.append('tool_id', toolId);
        params.append('qr_code_name', qrUploadRes.data.qr_code_name);

        // Add QR to tool
        await axios.put('https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/toolitem/addQr',
            params,
            {
              headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`,
                'Content-Type': 'application/x-www-form-urlencoded'
              }
            });
      }

      // Create submission data for parent component
      const submissionData = {
        ...form,
        // If there's a new image, we'd use the URL from the server response
        // For now, we'll use the preview URL to simulate this
        image_url: form.image ? form.image_preview : form.image_url,
        qr_code_url: qrUploadRes.data.imageUrl
      };

      // Remove properties that shouldn't be passed back to the parent
      delete submissionData.image;
      delete submissionData.image_preview;

      // Pass the cleaned data back to the parent
      onSubmit(submissionData);
      onClose();
      setStep(1); // Reset step when closed
    } catch (error) {
      console.error('Error saving tool:', error);
      setError('Failed to save tool');
    } finally {
      setIsSaving(false);
    }
  };

  //this will upload image byte per byte, in this way it can accomodate
  //large image size
  const uploadImageInChunks = async (image) => {
    const file = image;
    const totalChunks = Math.ceil(file.size / chunkSize);

    for (let i = 0; i < totalChunks; i++) {
      const from = i * chunkSize;
      const to = Math.min(from + chunkSize, file.size);
      const blob = file.slice(from, to);
      const buffer = await blob.arrayBuffer();

      const params = new URLSearchParams();
      params.set('name', file.name);
      params.set('size', file.size);
      params.set('currentChunkIndex', i);
      params.set('totalChunks', totalChunks);

      try {
        const res = await axios.post(`https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/toolitem/upload?${params.toString()}`, buffer, {
          headers: {
            'Content-Type': 'application/octet-stream',
            Authorization: `Bearer ${localStorage.getItem('token')}`
          },
        });

        if (res.status === 200) {
          // Only return image URL after last chunk
          if (res.data) {
            return res.data;
          }
        } else {
          throw new Error("Upload failed with status: " + res.status);
        }
      } catch (err) {
        console.error("Chunk upload error:", err);
        throw new Error("Upload failed");
      }
    }
    throw new Error("Image URL not returned"); // fallback safety
  };

  const validateForm = () => {
    const errors = {};

    if (!form.name.trim()) {
      errors.name = 'Tool name is required';
    }

    if (!form.location) {
      errors.location = 'Location is required';
    }

    if (!form.description.trim()) {
      errors.description = 'Description is required';
    }

    if (!form.date_acquired) {
      errors.date_acquired = 'Date acquired is required';
    }

    if (!form.image && !form.image_url) {
      errors.image = 'Tool image is required';
    }

    setValidationErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const nextStep = async () => {
    // Validate form before proceeding
    if (!validateForm()) {
      return;
    }

    setIsLoading(true);

    try {
      const {imageUrl, image_name} = await uploadImageInChunks(form.image);
      const updatedForm = {
        ...form,
        image_name: image_name,
        image_url: imageUrl,
      };
      setForm(updatedForm);

      const response = await axios.post('https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/toolitem/addTool', updatedForm, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        }
      });

      if (response.status === 201) {
        setToolId(response.data.toolId);

        const qrResponse = await axios.post('https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/qrcode/create/' + response.data.toolId, {}, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
          },
          responseType: 'blob'
        });

        if (qrResponse.status === 200) {
          const blobUrl = URL.createObjectURL(qrResponse.data);
          setQrImage(blobUrl);
          setStep(2);
        }
      } else {
        setError("Failed to create tool");
      }
    } catch (error) {
      console.error("Error during tool creation:", error);
      setError("An error occurred while creating the tool");
    } finally {
      setIsLoading(false);
    }
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
                {qrImage ?  (
                        <img src={qrImage} alt="QR Code" className="h-full w-full object-cover"/>
                    )
                    :(<svg className="w-24 h-24 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 20h4M4 12h4m12 0h.01M5 8h2a1 1 0 001-1V5a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1zm12 0h2a1 1 0 001-1V5a1 1 0 00-1-1h-2a1 1 0 00-1 1v2a1 1 0 001 1zM5 20h2a1 1 0 001-1v-2a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1z"></path>
                    </svg>)}
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
              <div>Location: <span className="font-medium">{form.location}</span></div>
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
                className={`w-full border ${validationErrors.name ? 'border-red-500' : 'border-gray-300'} rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent`}
                placeholder="Enter tool name"
            />
            {validationErrors.name && (
                <p className="text-red-500 text-xs mt-1">{validationErrors.name}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Location</label>
            <select
                name="location"
                value={form.location}
                onChange={handleChange}
                className={`w-full border ${validationErrors.location ? 'border-red-500' : 'border-gray-300'} rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent bg-white`}
            >
              <option value="">Select a location</option>
              {locationOptions.map((location, index) => (
                  <option key={index} value={location}>{location}</option>
              ))}
            </select>
            {validationErrors.location && (
                <p className="text-red-500 text-xs mt-1">{validationErrors.location}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
            <input
                type="text"
                name="description"
                value={form.description}
                onChange={handleChange}
                className={`w-full border ${validationErrors.description ? 'border-red-500' : 'border-gray-300'} rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent`}
                placeholder="Short description"
            />
            {validationErrors.description && (
                <p className="text-red-500 text-xs mt-1">{validationErrors.description}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Date Acquired</label>
            <input
                type="date"
                name="date_acquired"
                value={form.date_acquired}
                onChange={handleChange}
                className={`w-full border ${validationErrors.date_acquired ? 'border-red-500' : 'border-gray-300'} rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent`}
            />
            {validationErrors.date_acquired && (
                <p className="text-red-500 text-xs mt-1">{validationErrors.date_acquired}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Image Upload</label>
            <div className="flex flex-col space-y-2">
              <input
                  type="file"
                  ref={fileInputRef}
                  onChange={handleImageUpload}
                  accept="image/*"
                  className="hidden"
              />
              <div className="flex items-center space-x-2">
                <button
                    type="button"
                    onClick={triggerFileInput}
                    className="px-3 py-2 bg-gray-100 border border-gray-300 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-teal-500"
                >
                  Choose Image
                </button>
                <span className="text-sm text-gray-500 truncate">
                {form.image ? form.image.name : "No file chosen"}
              </span>
              </div>

              {(form.image_preview || form.image_url) && (
                  <div className="mt-2 relative">
                    <div className="h-24 w-24 rounded-lg border border-gray-200 overflow-hidden">
                      <img
                          src={form.image_preview || form.image_url}
                          alt="Tool preview"
                          className="h-full w-full object-cover"
                      />
                    </div>
                  </div>
              )}
              {validationErrors.image && (
                  <p className="text-red-500 text-xs mt-1">{validationErrors.image}</p>
              )}
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Category</label>
            <select
                name="category"
                value={form.category}
                onChange={handleChange}
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent bg-white"
            >
              {categoryOptions.map((category, index) => (
                  <option key={index} value={category}>{category}</option>
              ))}
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
                className="cursor-pointer text-gray-500 hover:text-gray-700 focus:outline-none"
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
                    disabled={isSaving}
                    className="cursor-pointer px-5 py-2 border border-gray-300 text-gray-700 rounded-lg text-sm font-medium hover:bg-gray-50"
                >
                  Back
                </button>
            )}

            {step === 1 ? (
                <button
                    onClick={nextStep}
                    disabled={isLoading}
                    className="cursor-pointer bg-teal-500 text-white px-5 py-2 rounded-lg text-sm font-medium hover:bg-teal-600 shadow-sm flex items-center justify-center min-w-24"
                >
                  {isLoading ? (
                      <>
                        <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                        Processing...
                      </>
                  ) : "Next"}
                </button>
            ) : (
                <button
                    onClick={handleSubmit}
                    disabled={isSaving}
                    className="cursor-pointer bg-teal-500 text-white px-5 py-2 rounded-lg text-sm font-medium hover:bg-teal-600 shadow-sm flex items-center justify-center min-w-24"
                >
                  {isSaving ? (
                      <>
                        <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                        Saving...
                      </>
                  ) : (isEditing ? "Update Tool" : "Save Tool")}
                </button>
            )}
          </div>
        </div>
      </div>
  );
};

export default ToolModal;