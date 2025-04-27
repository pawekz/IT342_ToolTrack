import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios'

const ToolModal = ({ show, onClose, onSubmit, initialData, isEditing }) => {
  const chunkSize = 5 * 1024 * 1024; // 5MB

  const [toolId, setToolId] = useState(null);
  const [qrImage, setQrImage] = useState(null);
  const [error, setError] = useState(null);
  const [step, setStep] = useState(1);
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
    tool_condition: 'NEW',
  });
  const fileInputRef = useRef(null);

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
        location: '',
        description: '',
        date_acquired: '',
        image: null,
        image_preview: '',
        image_url: '',
        tool_condition: 'NEW',
      });
    }
  }, [initialData]);

  if (!show) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  };

  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      setForm({
        ...form,
        image: file,
        image_preview: URL.createObjectURL(file)
      });
    }
  };

  const triggerFileInput = () => {
    fileInputRef.current.click();
    if(form.image === null){
      setError(null);
    }
  };

  const handleSubmit = async () => {
    try {
      const form = new FormData();

      // Fetch the blob from the blob URL (qrImage is a blob URL)
      const response = await fetch(qrImage);
      const blob = await response.blob();

      // Convert blob into a File (important for multipart upload)
      const file = new File([blob], `${toolId}_qr.png`, { type: 'image/png' });

      // Append the correct fields
      form.append('file', file);
      form.append('toolId', toolId);

      // Correct axios post
      await axios.post(
          'https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/qrcode/uploadImage',
          form,
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem('token')}`
            }
          }
      ).then(res => {
        if (res.status === 201) {
          console.log(res)
          const params = new URLSearchParams();
          params.append('image_url', res.data.imageUrl);
          params.append('tool_id', toolId);

          axios.put('https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/toolitem/addQr',
              params,
            {
              headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`,
                'Content-Type': 'application/x-www-form-urlencoded'
              }
            }).then(res => {

          })
        }
      });
    } catch (error) {
      console.error('Error uploading QR Image:', error);
    }

    // Create a FormData object to handle the file upload
    const formData = new FormData();

    
    // Add all form fields to the FormData
    Object.keys(form).forEach(key => {
      if (key === 'image' && form[key]) {
        formData.append('image', form[key]);
      } else if (key !== 'image_preview') {
        formData.append(key, form[key]);
      }
    });
    
    // In a real implementation, the API would handle the image upload and return a URL
    // For now, we'll create an object that matches what the parent component expects
    const submissionData = {
      ...form,
      // If there's a new image, we'd use the URL from the server response
      // For now, we'll use the preview URL to simulate this
      image_url: form.image ? form.image_preview : form.image_url
    };
    
    // Remove properties that shouldn't be passed back to the parent
    delete submissionData.image;
    delete submissionData.image_preview;
    
    // Pass the cleaned data back to the parent
    onSubmit(submissionData);
    onClose();
    setStep(1); // Reset step when closed
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

  const nextStep = async () => {

    const {imageUrl, image_name}  = await uploadImageInChunks(form.image);
    await setForm({
      ...form,
      image_name: image_name,
      image_url: imageUrl,
    });

    axios.post('https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/toolitem/addTool', form, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json'
      }
    })
        .then(res => {
          if (res.status === 201) {
            setToolId(res.data.toolId)
            axios.post('https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/qrcode/create/' + res.data.toolId, {}, {
              headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`,
              },
              responseType: 'blob'
            })
                .then(res => {
                  if (res.status === 200) {
                    const blobUrl = URL.createObjectURL(res.data);
                    setQrImage(blobUrl);
                    setStep(2)
                  }
                })
          } else {
            //set error   <----
          }
        })
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

        {/*<div>*/}
        {/*  <label className="block text-sm font-medium text-gray-700 mb-1">Serial Number</label>*/}
        {/*  <input*/}
        {/*    type="text"*/}
        {/*    name="serial_number"*/}
        {/*    value={form.serial_number}*/}
        {/*    onChange={handleChange}*/}
        {/*    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent"*/}
        {/*    placeholder="Enter serial number"*/}
        {/*  />*/}
        {/*</div>*/}

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
          </div>
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

      {/*  <div>*/}
      {/*    <label className="block text-sm font-medium text-gray-700 mb-1">Status</label>*/}
      {/*    <select*/}
      {/*      name="status"*/}
      {/*      value={form.status}*/}
      {/*      onChange={handleChange}*/}
      {/*      className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent bg-white"*/}
      {/*    >*/}
      {/*      <option value="AVAILABLE">AVAILABLE</option>*/}
      {/*      <option value="BORROWED">BORROWED</option>*/}
      {/*      <option value="MAINTENANCE">MAINTENANCE</option>*/}
      {/*      <option value="RETIRED">RETIRED</option>*/}
      {/*    </select>*/}
      {/*  </div>*/}
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
              className="cursor-pointer px-5 py-2 border border-gray-300 text-gray-700 rounded-lg text-sm font-medium hover:bg-gray-50"
            >
              Back
            </button>
          )}
          
          {step === 1 ? (
            <button
              onClick={nextStep}
              className="cursor-pointer bg-teal-500 text-white px-5 py-2 rounded-lg text-sm font-medium hover:bg-teal-600 shadow-sm"
            >
              Next
            </button>
          ) : (
            <button
              onClick={handleSubmit}
              className="cursor-pointer bg-teal-500 text-white px-5 py-2 rounded-lg text-sm font-medium hover:bg-teal-600 shadow-sm"
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
