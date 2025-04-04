package edu.cit.tooltrack.security;


import edu.cit.tooltrack.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class OAuth2AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final UserService userService;
    private final HttpSession httpSession;

    public OAuth2AuthenticationSuccessListener(UserService userService, HttpSession httpSession) {
        this.userService = userService;
        this.httpSession = httpSession;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        // Check if this is an OAuth2 login
        if (event.getAuthentication().getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) event.getAuthentication().getPrincipal();

            String email = oauth2User.getAttribute("email");
            if (email != null && !userService.isUserExist(email)) {
                userService.addGoogleUser(oauth2User);
            }

            // Store user attributes in the session
            httpSession.setAttribute("user", oauth2User.getAttributes());
        }
    }
}

