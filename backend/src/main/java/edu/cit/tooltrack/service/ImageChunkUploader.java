package edu.cit.tooltrack.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ImageChunkUploader {

    @Autowired
    private S3Service s3Service;
    private String imageName;
    private static final String UPLOAD_DIR = "backend/uploads/"; // Directory to store uploaded files
    private static final ConcurrentHashMap<String, Long> uploadedSizes = new ConcurrentHashMap<>();

    public String uploadChunk(String uuidName, long size,
                              int currentChunkIndex,
                              int totalChunks,
                              HttpServletRequest request,
                              String directory
    ) throws IOException {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        File outputFile = new File(uploadDir, uuidName);

        try (InputStream inputStream = request.getInputStream();
            FileOutputStream fos = new FileOutputStream(outputFile, true)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }

        if (currentChunkIndex == totalChunks - 1) {
            // Final chunk received, upload to S3 and delete local file
            String imageUrl = s3Service.upload(outputFile,directory , uuidName);
            // Clean up local file after upload
            outputFile.delete();
            return imageUrl;
        }

        return null; // Waiting for more chunks
    }


    // Method to abort the file upload and clean up resources
    public String abortUpload(String fileName) {
        try {
            Path filePath = Path.of(UPLOAD_DIR, fileName);
            Files.deleteIfExists(filePath);
            uploadedSizes.remove(fileName);
            return "Upload aborted successfully";
        } catch (IOException e) {
            return "Failed to abort upload: " + e.getMessage();
        }
    }
}
