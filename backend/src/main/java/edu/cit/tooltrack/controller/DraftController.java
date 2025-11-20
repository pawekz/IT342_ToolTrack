    package edu.cit.tooltrack.controller;

import edu.cit.tooltrack.dto.NotificationMessageDTO;
import edu.cit.tooltrack.entity.User;
import edu.cit.tooltrack.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = {"http://localhost:5173", "https://tooltrack-frontend-qybsm.ondigitalocean.app"})
public class DraftController {

    @Autowired
    private ToolItemService toolItemService;
    @Autowired
    private QRcodeService qrcodeService;
    @Autowired
    private ImageChunkUploader chunkUploadService;
    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/test")
    public String getDrafts() {
        return "hello test user";
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/getTotalUsers")
    public ResponseEntity<Integer> getTotalUsers() {
        int total = userService.getTotalUsers();
        if(total == 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
        }
        return ResponseEntity.ok(total);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/test-notify")
    public ResponseEntity<?> testNotify() {
        notificationService.sendNotification(
                NotificationMessageDTO.builder()
                        .toolName("Hammer")
                        .message("Your Requested Tool Hammer is approved")
                        .status("Approved")
                        .borrow_date(null)
                        .due_date(null)
                        .user_email("admin@email.com")
                        .build()
        );
        return ResponseEntity.ok("sent");
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
