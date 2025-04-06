package edu.cit.tooltrack.security.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request,response);
            return;
        }

        final String jwt = authHeader.substring(7);
        final String userName = jwtService.extractUserName(jwt);
        final String role = jwtService.extractRole(jwt);

        Authentication authentication
                = SecurityContextHolder.getContext().getAuthentication();

        if(userName !=null  && authentication == null) {

            System.out.println("User Name: " + userName + " Role: " + role + "");
            //Authenticate
            UserDetails userDetails
                    = userDetailsService.loadUserByUsername(userName);

            if(jwtService.isTokenValid(jwt,userDetails)) {

                System.out.println("jwt is valid!");
                //this token is use for next filter
                List<SimpleGrantedAuthority> authorities =
                        Collections.singletonList(new SimpleGrantedAuthority(role));

                UsernamePasswordAuthenticationToken authenticationToken
                        = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );
                SecurityContextHolder.getContext()
                        .setAuthentication(authenticationToken);
                System.out.println("executed everything in jwtfilterinternal");
            }
        }
        filterChain.doFilter(request,response);
    }
}
