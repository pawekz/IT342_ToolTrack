package edu.cit.tooltrack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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


    public String uploadChunk(MultipartFile file, String fileName, int chunkIndex, int totalChunks) {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new RuntimeException("Failed to create upload directory");
        }

        try {
            Path filePath = Path.of(UPLOAD_DIR, fileName);

            try (OutputStream outputStream = Files.newOutputStream(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                outputStream.write(file.getBytes());
            }

            uploadedSizes.merge(fileName, (long) file.getSize(), Long::sum);

            //TRUE if last chucked is sent
            if (chunkIndex == totalChunks - 1) {
                long fileSize = Files.size(filePath);
                if (!uploadedSizes.get(fileName).equals(fileSize)) {
                    throw new RuntimeException("File reconstruction failed due to size mismatch");
                }
                String image_url = s3Service.upload(filePath.toFile(), "Tool_Images/", fileName);
                uploadedSizes.remove(fileName);
                return image_url;
            }

            return "Chunk " + chunkIndex + " uploaded successfully";

        } catch (IOException e) {
            throw new RuntimeException("An error occurred while uploading chunk: " + e.getMessage(), e);
        }
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
