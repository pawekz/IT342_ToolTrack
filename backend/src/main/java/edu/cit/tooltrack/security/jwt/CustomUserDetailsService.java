package edu.cit.tooltrack.security.jwt;

import edu.cit.tooltrack.entity.User;
import edu.cit.tooltrack.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService{

    private final UserRepository user_repo;

    public CustomUserDetailsService(UserRepository user_repo) {
        this.user_repo = user_repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = user_repo.findByEmail(username);

        if(user == null)
            throw new UsernameNotFoundException("User Not Found");
        else
            return new CustomUserDetails(user);
    }
}
