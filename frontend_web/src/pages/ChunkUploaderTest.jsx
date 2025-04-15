import React, { useState } from 'react';

const CHUNK_SIZE = 1024 * 1024; // 1MB

const ChunkUploader = () => {
  const [file, setFile] = useState(null);
  const [progress, setProgress] = useState(0);
  const [status, setStatus] = useState("");

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
    setProgress(0);
    setStatus("");
  };

  const uploadFileInChunks = async () => {
    if (!file) return;
  
    const totalChunks = Math.ceil(file.size / CHUNK_SIZE);
    const fileName = file.name;
  
    for (let chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
      const start = chunkIndex * CHUNK_SIZE;
      const end = Math.min(start + CHUNK_SIZE, file.size);
      const chunk = file.slice(start, end);
  
      const formData = new FormData();
      formData.append('file', chunk);
      formData.append('fileName', fileName);
      formData.append('chunkIndex', chunkIndex);
      formData.append('totalChunks', totalChunks);
  
      try {
        const response = await fetch("http://localhost:8080/test/uploadChunk", {
          method: "POST",
          body: formData,
        });
  
        const result = await response.json();
  
        if (!response.ok) {
          throw new Error(result.message || `Chunk ${chunkIndex} failed`);
        }
  
        setProgress(((chunkIndex + 1) / totalChunks) * 100);
        setStatus(`Chunk ${chunkIndex + 1} of ${totalChunks} uploaded successfully`);
      } catch (err) {
        setStatus(`Upload failed: ${err.message}`);
        return;
      }
    }
  
    setStatus("Upload complete!");
  };

  return (
    <div style={styles.container}>
      <h3 style={styles.header}>Chunked Upload Test</h3>
      
      <label htmlFor="fileInput" style={styles.label}>Select a file to upload:</label>
      <input
        id="fileInput"
        type="file"
        onChange={handleFileChange}
        style={styles.fileInput}
      />
      
      <button
        onClick={uploadFileInChunks}
        disabled={!file}
        style={{
          ...styles.button,
          backgroundColor: file ? '#007BFF' : '#ccc',
          cursor: file ? 'pointer' : 'not-allowed',
        }}
      >
        Upload in Chunks
      </button>
      
      {progress > 0 && (
        <div style={styles.progressBarContainer}>
          <div
            style={{
              ...styles.progressBar,
              width: `${progress}%`,
            }}
          >
            {Math.round(progress)}%
          </div>
        </div>
      )}

      {status && <p style={styles.status}>{status}</p>}
    </div>
  );
};

const styles = {
  container: {
    padding: '24px',
    maxWidth: '500px',
    margin: '0 auto',
    fontFamily: 'Arial, sans-serif',
    border: '1px solid #ddd',
    borderRadius: '8px',
    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
    backgroundColor: '#f9f9f9',
  },
  header: {
    textAlign: 'center',
    color: '#333',
  },
  label: {
    display: 'block',
    marginBottom: '8px',
    fontWeight: 'bold',
    color: '#555',
  },
  fileInput: {
    display: 'block',
    marginBottom: '16px',
    padding: '8px',
    border: '1px solid #ccc',
    borderRadius: '4px',
    width: '100%',
  },
  button: {
    display: 'block',
    width: '100%',
    padding: '10px',
    fontSize: '16px',
    fontWeight: 'bold',
    color: '#fff',
    border: 'none',
    borderRadius: '4px',
    marginTop: '8px',
  },
  progressBarContainer: {
    marginTop: '16px',
    width: '100%',
    backgroundColor: '#e0e0e0',
    borderRadius: '4px',
    overflow: 'hidden',
  },
  progressBar: {
    height: '20px',
    backgroundColor: '#28a745',
    color: '#fff',
    textAlign: 'center',
    lineHeight: '20px',
    fontSize: '14px',
  },
  status: {
    marginTop: '16px',
    textAlign: 'center',
    color: '#555',
  },
};

export default ChunkUploader;