package edu.cit.tooltrack.security;

import edu.cit.tooltrack.controller.UserController;
import edu.cit.tooltrack.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable) // ✅ Correct way to disable CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll() // ✅ Correct method
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            OAuth2User user = (OAuth2User) authentication.getPrincipal();

                            // Store user data in session
                            request.getSession().setAttribute("user", user.getAttributes());

                            if(!userService.isUserExist(user.getAttributes().get("email").toString())){
                                userService.addGoogleUser(user);
                            }
                            // Redirect to frontend with user data in query params (optional)
                            response.sendRedirect("http://localhost:5173/home");
                        })
                )
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .build();
    }
}
