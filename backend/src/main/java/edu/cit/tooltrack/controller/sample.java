package edu.cit.tooltrack.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class sample {

    @PostMapping("/login")
    public String login(){
        return "hello sir";
    }

    @PostMapping("signin")
    public void signIn(){

    }

}
