package edu.cit.tooltrack.controller;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class sample {

    private Map<String, Object> data;

    @GetMapping("/login")
    public void initiateGoogleLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorize/google");
    }

    @GetMapping("/user-info")
    public Map<String, Object> getUser (@AuthenticationPrincipal OAuth2User principal){
        data = principal.getAttributes();
        return principal.getAttributes();
    }

}
