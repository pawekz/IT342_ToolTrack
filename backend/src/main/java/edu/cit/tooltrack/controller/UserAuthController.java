package edu.cit.tooltrack.controller;


import edu.cit.tooltrack.dto.LoginRequest;
import edu.cit.tooltrack.dto.UserResponseDTO;
import edu.cit.tooltrack.entity.User;
import edu.cit.tooltrack.security.jwt.CustomUserDetails;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "User Authentication API", description = "Endpoints for user authentication")
@CrossOrigin(origins = {"http://localhost:5173", "https://tooltrack-frontend-hteudjc6beaqhudr.southeastasia-01.azurewebsites.net"})
@RestController
@RequestMapping("/auth")
public class UserAuthController {

    @Autowired
    private UserService userService;
    private Map<String, Object> data;

    @GetMapping("/checkUser")
    public ResponseEntity<Map<String, String>> checkUser(@RequestParam String email) {
        if (userService.isUserExist(email) ) {
            return ResponseEntity.ok(Map.of("msg", "User exists"));
        } else {
            return ResponseEntity.ok(Map.of("msg", "User does not exist"));
        }
    }


    @PostMapping("/user/googleLogin") //user googlelogin
    public ResponseEntity<?> googleUserLogin(@RequestBody User user) {
        UserResponseDTO userDetails = null;
        if (userService.isUserExist(user.getEmail())) {
            userDetails = userService.getUserData(user.getEmail());
            if(userDetails.getRole().equals("Staff")){
                return ResponseEntity.ok().body(Map.of("token", JwtService.generateToken(userDetails)));
            }else{
                //means admin
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credentials not found"));
            }
        } else {
            userDetails = userService.register(user, "Staff");
            return ResponseEntity.status(HttpStatus.CREATED).body(JwtService.generateToken(userDetails));
        }
    }

    @PostMapping("/admin/googleLogin") //user googlelogin
    public ResponseEntity<?> googleAdminLogin(@RequestBody User user) {
        UserResponseDTO userDetails = null;
        if (userService.isUserExist(user.getEmail())) {
            userDetails = userService.getUserData(user.getEmail());
            if(userDetails.getRole().equals("Admin")){
                return ResponseEntity.ok().body(Map.of("token", JwtService.generateToken(userDetails)));
            }else{
                //means its a user
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User is not an admin"));
            }
        } else {
            userDetails = userService.register(user, "Admin");
            return ResponseEntity.status(HttpStatus.CREATED).body(JwtService.generateToken(userDetails));
        }
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

    @PostMapping("/user/login")  //user login
    public ResponseEntity<?> userLogin(@RequestBody LoginRequest loginRequest) {
        // Check if user exists
        try {
            UserResponseDTO userDetails = userService.verifyUser(loginRequest);
            if(userDetails == null){
                //means its an admin
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credentials not found"));
            }
            return ResponseEntity.ok().body(Map.of("token", JwtService.generateToken(userDetails)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid Credentials"));
        }
    }

    @PostMapping("/admin/login")  //user login
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest loginRequest) {
        // Check if user exists
        try {
            UserResponseDTO userDetails = userService.verifyAdmin(loginRequest);
            if(userDetails == null){
                //means its a user
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credentials not found"));
            }
            return ResponseEntity.ok().body(Map.of("token", JwtService.generateToken(userDetails)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid Credentials"));
        }
    }

    @PostMapping("/user/register") //user register
    public ResponseEntity<String> userRegister(@RequestBody User user) {
        try {
            UserResponseDTO userResponse = userService.register(user,"Staff");
            return ResponseEntity.status(HttpStatus.CREATED).body(JwtService.generateToken(userResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/admin/register") //user register
    public ResponseEntity<String> adminRegister(@RequestBody User user) {
        try {
            UserResponseDTO userResponse = userService.register(user,"Admin");
            return ResponseEntity.status(HttpStatus.CREATED).body(JwtService.generateToken(userResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        }
    }

}
