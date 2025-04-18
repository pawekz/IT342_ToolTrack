    package edu.cit.tooltrack.controller;

import edu.cit.tooltrack.dto.UploadToolItemDTO;
import edu.cit.tooltrack.entity.ToolItems;
import edu.cit.tooltrack.entity.User;
import edu.cit.tooltrack.service.ImageChunkUploader;
import edu.cit.tooltrack.service.QRcodeService;
import edu.cit.tooltrack.service.ToolItemService;
import edu.cit.tooltrack.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = {"http://localhost:5173", "https://tooltrack-frontend-hteudjc6beaqhudr.southeastasia-01.azurewebsites.net"})
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
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
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

    /**
     * Test endpoint to fetch the first user from the database
     * This endpoint is for testing purposes only and should be removed after testing is confirmed
     */
    @Operation(
            summary = "Test database connection",
            description = "Fetches the first user from the database to test the connection.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved first user",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "No users found",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/dbconnect")
    public ResponseEntity<?> testDatabaseConnection() {
        // Get all users from the database
        List<User> users = userService.getAllUsers();

        // Check if there are any users
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "No users found"));
        }

        // Get the first user
        User firstUser = users.get(0);

        // Create a response with only the required fields
        Map<String, String> response = new HashMap<>();
        response.put("first_name", firstUser.getFirst_name());
        response.put("last_name", firstUser.getLast_name());
        response.put("email", firstUser.getEmail());

        // Return the response
        return ResponseEntity.ok(response);
    }
}
