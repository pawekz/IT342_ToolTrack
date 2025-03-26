package edu.cit.tooltrack.controller;

import edu.cit.tooltrack.dto.UserResponseDTO;
import edu.cit.tooltrack.entity.User;
import edu.cit.tooltrack.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "User Authentication API", description = "Endpoints for user authentication")
@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /*
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
     */


    @Operation(
            summary = "Retrieve all users",
            description = "Fetches a list of all registered users.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users",
                    content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = User.class))))
    })
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Update a user",
            description = "Updates the details of an existing user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User provides the new credentials",
                    required = true
            ))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(value = "{\"error\": \"Incorrect Credentials\"}")))
    })
    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        UserResponseDTO updatedUser = userService.editUser(user);
        if(updatedUser == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Incorrect Credentials"));
        }
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
            summary = "Add a new user",
            description = "Registers a new user in the system.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User inputs valid credentials",
                    required = true
            ))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully",
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Credentials already existed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(value = "{\"error\": \"Data already existed\"}")))
    })
    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        UserResponseDTO newUser = userService.addUser(user);
        if(newUser == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Incorrect Credentials"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @Operation(
            summary = "Delete a user",
            description = "Deletes a user by their email address.",
            parameters = @Parameter(name = "email", description = "The email of the user to delete", required = true)
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(value = "{\"message\": \"Deleted Successfully\"}"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(value = "{\"error\": \"User not found\"}")))
    })
    @DeleteMapping("/deleteUser/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        String result = userService.deleteUser(email);
        if ("User not found".equals(result)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
        return ResponseEntity.ok(result);
    }

}
