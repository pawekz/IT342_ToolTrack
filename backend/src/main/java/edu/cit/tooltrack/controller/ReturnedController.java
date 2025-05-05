package edu.cit.tooltrack.controller;

import edu.cit.tooltrack.entity.ReturnTransactionImage;
import edu.cit.tooltrack.service.ImageChunkUploader;
import edu.cit.tooltrack.service.ReturnTransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/returned")
public class ReturnedController {


    @Autowired
    private ReturnTransactionService returnTransactionService;
    @Autowired
    private ImageChunkUploader chunkUploadService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadChunk(
            @RequestParam String name,
            @RequestParam long size,
            @RequestParam int currentChunkIndex,
            @RequestParam int totalChunks,
            HttpServletRequest request
    ) {
        try {
            String uuidName = java.util.UUID.randomUUID() + "_" + name;
            String result = chunkUploadService.uploadChunk(uuidName, size, currentChunkIndex, totalChunks, request, "ReturnedTool_Images/");
            if (result != null) {
                return ResponseEntity.ok().body(Map.of("imageUrl", result, "image_name", uuidName));
            }
            return ResponseEntity.ok().body("{\"message\": \"Chunk uploaded successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"Chunk upload failed: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addReturnTool(
            @RequestBody ReturnTransactionImage returnTransactionImage){
        ReturnTransactionImage result = returnTransactionService.add(returnTransactionImage);

        if(result != null){
            return ResponseEntity.ok(result);
        }else
            return ResponseEntity.status(400).body("Failed to add return tool");
    }


}