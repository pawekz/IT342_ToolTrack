package edu.cit.tooltrack.controller;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class sample {

    @GetMapping("/user-info")
    public Map<String, Object> getUser (@AuthenticationPrincipal OAuth2User principal){
        return principal.getAttributes();
    }

}
