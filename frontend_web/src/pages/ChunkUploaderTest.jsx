import React, { useEffect, useState } from 'react';
import axios from 'axios';

const chunkSize = 5 * 1024 * 1024; // 5MB

const ChunkedImageUploader = () => {

  const [error, setError] = useState(null);

  const [toolName, setToolName] = useState("");
  const [location, setLocation] = useState("");
  const [condition, setCondition] = useState("");
  const [description, setDescription] = useState("");
  const[collectedUrls, setCollectedUrls] = useState([]);
  
  const [images, setImages] = useState([]);
  const [isUploading, setIsUploading] = useState(false);

  const handleFileSelect = (e) => {
    const selected = Array.from(e.target.files);
    const withPreview = selected.map(file => ({
      file,
      preview: URL.createObjectURL(file),
      status: 'pending',
      progress: 0,
      finalFilename: null
    }));
    setImages(prev => [...prev, ...withPreview]);
  };

  useEffect(() => {
    if (error) {
      alert(error);
    }
  }, [error]);

  const handleDrop = (e) => {
    e.preventDefault();
    const dropped = Array.from(e.dataTransfer.files);
    const withPreview = dropped.map(file => ({
      file,
      preview: URL.createObjectURL(file),
      status: 'pending',
      progress: 0,
      finalFilename: null
    }));
    setImages(prev => [...prev, ...withPreview]);
  };

  useEffect(() => {
    alert("This is a test file only")
  },[])

  const handleDeleteImage = (index) => {
    setImages(prev => prev.filter((_, i) => i !== index));
  };

  const uploadImageInChunks = async (image, index) => {
    const file = image.file;
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
        const res = await axios.post(`http://localhost:8080/test/upload?${params.toString()}`, buffer, {
          headers: { 'Content-Type': 'application/octet-stream' },
        });
  
        if (res.status === 200) {
          // Update progress after each successful chunk
          setImages((prev) => {
            const updated = [...prev];
            updated[index].progress = Math.floor(((i + 1) / totalChunks) * 100);
            return updated;
          });
  
          // Only return image URL after last chunk
          if (res.data?.imageUrl) {
            const imageUrl = res.data.imageUrl;
  
            setImages((prev) => {
              const updated = [...prev];
              updated[index].finalFilename = imageUrl;
              return updated;
            });
  
            return imageUrl;
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
    

  const handleNextClick = async () => {
    setIsUploading(true);
    const uploadedUrls = [];
  
    for (let i = 0; i < images.length; i++) {
      // Mark image as uploading
      setImages(prev => {
        const updated = [...prev];
        updated[i].status = 'uploading';
        return updated;
      });
  
      try {
        const imageUrl = await uploadImageInChunks(images[i], i);
        if (imageUrl) {
          uploadedUrls.push(imageUrl);
        }
      } catch (err) {
        console.error(err);
        setError(`Upload failed for image ${i + 1}`);
      }
    }
  
    setCollectedUrls(uploadedUrls);
    console.log("Uploaded URLs:", uploadedUrls);
  
    const toolPayload = {
      name: toolName,
      location,
      condition,
      description
    };
  
    //upload the tool with image urls
    const response = await axios.post("http://localhost:8080/test/addTool", {
      toolItem: toolPayload,
      images: uploadedUrls,
      toolCategory: "Hardware"
    }, {
      headers: { 'Content-Type': 'application/json' }
    });
  
    if (response.status === 200) {
      alert("Tool added successfully!");
    }
  };
    

  useEffect(() => {
   
  }, [collectedUrls]);
  

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
      <div className="bg-white rounded-xl shadow-lg w-full max-w-md p-6 relative">
        <button className="absolute top-4 right-4 text-red-500 hover:text-red-700 text-xl">&times;</button>

        {/* Preview and Dropzone */}
        <div className="flex space-x-2 overflow-x-auto mb-4">
          {images.map((img, idx) => (
            <div key={idx} className="relative w-16 h-16 rounded overflow-hidden border group">
              <img src={img.preview} alt="preview" className="object-cover w-full h-full" />
              {img.status === 'uploading' && (
                <div className="absolute inset-0 bg-black bg-opacity-50 flex flex-col justify-center items-center text-white text-xs">
                  Uploading
                  <div className="w-full h-1 mt-1 bg-gray-200">
                    <div
                      className="h-1 bg-green-500"
                      style={{ width: `${img.progress}%` }}
                    ></div>
                  </div>
                </div>
              )}
              <button
                className="absolute top-0 right-0 m-1 bg-red-600 text-white rounded-full w-5 h-5 text-xs hidden group-hover:block"
                onClick={() => handleDeleteImage(idx)}
              >
                &times;
              </button>
            </div>
          ))}

          <div
            className="w-16 h-16 border-2 border-dashed border-gray-300 rounded flex items-center justify-center cursor-pointer"
            onClick={() => document.getElementById('fileInput').click()}
            onDrop={handleDrop}
            onDragOver={(e) => e.preventDefault()}
          >
            <span className="text-gray-400 text-2xl">+</span>
            <input
              id="fileInput"
              type="file"
              accept="image/*"
              multiple
              className="hidden"
              onChange={handleFileSelect}
            />
          </div>
        </div>

        {/* Form Fields */}
        <div className="space-y-4">
          <input
            type="text"
            placeholder="Tool Name"
            value={toolName}
            onChange={(e) => setToolName(e.target.value)}
            className="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
          />

          <input
            type="text"
            placeholder="Location"
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            className="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
          />

          <select
            value={condition}
            onChange={(e) => setCondition(e.target.value)}
            className="w-full px-4 py-2 border rounded-md bg-white focus:outline-none focus:ring-2 focus:ring-blue-400"
          >
            <option value="">Condition</option>
            <option value="NEW">New</option>
            <option value="USED">Used</option>
            <option value="DAMAGED">Damaged</option>
          </select>

          <textarea
            placeholder="Description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className="w-full px-4 py-2 border rounded-md resize-none h-32 focus:outline-none focus:ring-2 focus:ring-blue-400"
          ></textarea>
        </div>


        {/* Submit Button */}
        <div className="mt-6">
          <button
            className="w-full bg-teal-500 text-white py-2 rounded-md hover:bg-teal-600 transition disabled:opacity-50"
            onClick={handleNextClick}
            disabled={isUploading || images.length === 0}
          >
            {isUploading ? 'Uploading...' : 'Next'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ChunkedImageUploader;
