    package edu.cit.tooltrack.controller;

import edu.cit.tooltrack.service.ImageChunkUploader;
import edu.cit.tooltrack.service.QRcodeService;
import edu.cit.tooltrack.service.ToolItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "http://localhost:5173")
public class DraftController {

    @Autowired
    private ToolItemService toolItemService;
    @Autowired
    private QRcodeService qrcodeService;
    @Autowired
    private ImageChunkUploader imageChunkUploader;

    @GetMapping("/test")
    public String getDrafts() {
        return "hello test user";
    }

    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileName") String fileName,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("totalChunks") int totalChunks) {
        try {
            String imageUrl = toolItemService.uploadImage(file, fileName, chunkIndex, totalChunks);
            if (imageUrl != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("imageUrl", imageUrl));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "File Upload Unsuccessful"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An error occurred: " + e.getMessage()));
        }
    }

    // Chunked upload method
    @PostMapping("/uploadChunk")
    public ResponseEntity<?> uploadChunk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileName") String fileName,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("totalChunks") int totalChunks) {
        try {
            String message = imageChunkUploader.uploadChunk(file, fileName, chunkIndex, totalChunks);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", message));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An error occurred: " + e.getMessage()));
        }
    }

//    // Optional: Endpoint to abort the file upload and clean up resources
//    @DeleteMapping("/abortUpload")
//    public ResponseEntity<?> abortUpload(@RequestParam("fileName") String fileName) {
//        try {
//            Path filePath = Path.of(UPLOAD_DIR, fileName);
//            Files.deleteIfExists(filePath);
//            uploadedSizes.remove(fileName);
//            return ResponseEntity.ok("Upload aborted successfully");
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Failed to abort upload: " + e.getMessage());
//        }
//    }
}