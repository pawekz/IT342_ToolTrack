package edu.cit.tooltrack.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@Tag(name = "User Authentication API", description = "Endpoints for user authentication")
@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class UserAuthController {

    private Map<String, Object> data;

    @Operation(summary = "Allow Google login", description = "uses the google api for login")
    @GetMapping("/googlelogin")
    public void initiateGoogleLogin(
            HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorize/google");
    }

    @Operation(summary = "google api test", description = "this test the google api works")
    @GetMapping("/user-info")
    public Map<String, Object> getUser (@AuthenticationPrincipal OAuth2User principal){
        data = principal.getAttributes();
        return principal.getAttributes();
    }

}
