package edu.cit.tooltrack.controller;


import edu.cit.tooltrack.dto.LoginRequest;
import edu.cit.tooltrack.dto.UserResponseDTO;
import edu.cit.tooltrack.entity.User;
import edu.cit.tooltrack.security.jwt.JwtService;
import edu.cit.tooltrack.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Tag(name = "User Authentication API", description = "Endpoints for user authentication")
@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class UserAuthController {

    @Autowired
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


    @Operation(summary = "gets the user data thats from google api and returns token",
            description = "after the user gets authenticated by google, to get his token after login in to google it must send a requet to this endpoint to fetch the token")
    @GetMapping("/user-info")
    public String getUser (@AuthenticationPrincipal OAuth2User principal){

        if (principal == null) {
            return "Error: User is not authenticated, principal is null";
        }
        UserResponseDTO user;

        if(userService.isGoogleSignedIn(principal)) {
            user = userService.getUserData(principal.getAttributes().get("email").toString());
            System.out.println(user.getEmail());
        }
        else{
            user = userService.addGoogleUser(principal);
        }

        // Add user to the SecurityContext
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

//        return JwtService.generateToken(user);
        return "Name: " + user.getFirst_name() + " " + user.getLast_name() + " and Email: " + user.getEmail();
    }

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
        if(userService.verifyUser(loginRequest)!=null) {
            UserResponseDTO userResponse = userService.getUserData(loginRequest.getEmail());
            String token = JwtService.generateToken(userResponse);
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid Credentials"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        UserResponseDTO userResponse = userService.addUser(user);
        String token = JwtService.generateToken(userResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

}
