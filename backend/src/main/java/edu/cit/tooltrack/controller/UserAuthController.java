package edu.cit.tooltrack.controller;


import edu.cit.tooltrack.dto.LoginRequest;
import edu.cit.tooltrack.dto.UserResponseDTO;
import edu.cit.tooltrack.entity.User;
import edu.cit.tooltrack.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Tag(name = "User Authentication API", description = "Endpoints for user authentication")
@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class UserAuthController {

    private UserService userService;

    private Map<String, Object> data;

    @Operation(
            summary = "Allow Google login",
            description = "Initiates OAuth2 authentication via Google. " +
                    "Redirects the user to Google's authorization page, and after successful authentication, " +
                    "redirects to the configured success URL.",
            responses = {
                    @ApiResponse(responseCode = "302", description = "Redirects the user to Google's authentication page")
            }
    )
    @GetMapping("/googlelogin")
    public void initiateGoogleLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google"); // Redirects to Google OAuth
    }


//    @Operation(summary = "google api test", description = "this test the google api works")
//    @GetMapping("/user-info")
//    public Map<String, Object> getUser (@AuthenticationPrincipal OAuth2User principal){
//        data = principal.getAttributes();
//        return principal.getAttributes();
//    }
//    @PostMapping
//    public UserResponseDTO login(@RequestBody LoginRequest loginRequest){
//        if(userService.isUserExist(loginRequest.getEmail()));
//
//        return userService.getUserData(loginRequest.getEmail());
//    }

    @Operation(
            summary = "Normal login",
            description = "Authenticates a user using email and password. If credentials are valid, returns user data; otherwise, returns an error message.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login request containing email and password",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful login, returns user data",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid credentials",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Incorrect Credentials\" }")))
            }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest) {
        // Check if user exists
        if(userService.isEmailValid(loginRequest) && userService.isPasswordValid(loginRequest)) {
            // Retrieve user data and return it in the response
            UserResponseDTO userResponse = userService.getUserData(loginRequest.getEmail());
            return ResponseEntity.ok(userResponse);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Incorrect Credentials"));


    }
}
