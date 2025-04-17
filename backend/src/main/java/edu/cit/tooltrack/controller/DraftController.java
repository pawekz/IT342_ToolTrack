    package edu.cit.tooltrack.controller;

import edu.cit.tooltrack.dto.UploadToolItemDTO;
import edu.cit.tooltrack.entity.ToolItems;
import edu.cit.tooltrack.entity.User;
import edu.cit.tooltrack.service.ImageChunkUploader;
import edu.cit.tooltrack.service.QRcodeService;
import edu.cit.tooltrack.service.ToolItemService;
import edu.cit.tooltrack.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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
    private ImageChunkUploader chunkUploadService;
    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public String getDrafts() {
        return "hello test user";
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }


    @PostMapping("/upload")
    public ResponseEntity<?> uploadChunk(
            @RequestParam String name,
            @RequestParam long size,
            @RequestParam int currentChunkIndex,
            @RequestParam int totalChunks,
            HttpServletRequest request
    ) {
        try {
            String result = chunkUploadService.uploadChunk(name, size, currentChunkIndex, totalChunks, request);
            if (result != null) {
                return ResponseEntity.ok().body("{\"imageUrl\": \"" + result + "\"}");
            }
            return ResponseEntity.ok().body("{\"message\": \"Chunk uploaded successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"Chunk upload failed: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/addTool")
    public ResponseEntity<?> addTool(@RequestBody UploadToolItemDTO toolItemDTO) {
        ToolItems latestToolId = toolItemService.addToolItem(toolItemDTO);
        if (latestToolId != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("toolId", latestToolId.getTool_id()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Tool Item Addition Unsuccessful"));
        }
    }


}