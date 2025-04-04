package edu.cit.tooltrack.service;

import edu.cit.tooltrack.dto.LoginRequest;
import edu.cit.tooltrack.dto.UserResponseDTO;
import edu.cit.tooltrack.entity.User;
import edu.cit.tooltrack.repository.UserRepository;
import edu.cit.tooltrack.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.NameNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Transactional
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public Boolean isUserExist(String email){
        User user = userRepository.findByEmail(email);
        return user != null;
    }

    public Boolean isEmailValid(LoginRequest loginRequest){
        User user = userRepository.findByEmail(loginRequest.getEmail());
        return Objects.equals(user.getEmail(), loginRequest.getEmail());
    }

    public Boolean isPasswordValid(LoginRequest loginRequest){
        User user = userRepository.findByEmail(loginRequest.getEmail());
        return Objects.equals(user.getPassword_hash(), loginRequest.getPassword());
    }

    public UserResponseDTO verifyUser(LoginRequest loginRequest){
        // Fetch user by email
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword_hash());
        if (passwordMatches) {
            return new UserResponseDTO(user.getEmail(), user.getRole(), user.getFirst_name(), user.getLast_name());
        } else {
            return null; // Invalid credentials
        }
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public UserResponseDTO getUserData(String email){
        User user = userRepository.findByEmail(email);
        return new UserResponseDTO(user.getEmail(),user.getRole(),user.getFirst_name(), user.getLast_name());
    }

    public UserResponseDTO addGoogleUser(OAuth2User oAuth2User){
        User user = new User();
        user.setIsGoogle(true);
        user.setFirst_name(oAuth2User.getAttributes().get("given_name").toString());
        user.setLast_name(oAuth2User.getAttributes().get("family_name").toString());
        user.setEmail(oAuth2User.getAttributes().get("email").toString());
        user.setRole(User.Role.user);
        user.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));
        userRepository.save(user);
        return new UserResponseDTO(user.getEmail(), user.getRole() ,user.getFirst_name(), user.getLast_name());
    }

    public Boolean isGoogleSignedIn(OAuth2User oAuth2User){
        User user = userRepository.findByEmail(oAuth2User.getAttributes().get("email").toString());
        return user != null;
    }

    public User register(User user) {
        user.setPassword_hash(passwordEncoder
                .encode(user.getPassword_hash()));
        return userRepository.save(user);
    }

    public UserResponseDTO addUser(User user){
        //no checking for if user already existed
        user.setIs_active(1);
        user.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));
         register(user);
         return new UserResponseDTO(user.getEmail(), user.getRole(), user.getFirst_name(), user.getLast_name());
    }

    @SuppressWarnings("finally")
    public UserResponseDTO editUser(User newdata){
        User user = null;
        try {
            user = userRepository.findByEmail(newdata.getEmail());
            user.setEmail(newdata.getEmail());
            user.setFirst_name(newdata.getFirst_name());
            user.setLast_name(newdata.getLast_name());
            user.setPassword_hash(newdata.getPassword_hash());
            user.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
        }catch (NoSuchElementException error) {
            return null;
        } finally {
             userRepository.save(user);
             return new UserResponseDTO(user.getEmail(), user.getRole() ,user.getFirst_name(), user.getLast_name());
        }
    }

    //delete
    @SuppressWarnings({ "unused" })
    public String deleteUser(String email) {
        System.out.println("email: " + email);
        String msg="";
        User user = userRepository.findByEmail(email);

        if(user != null) {
            userRepository.delete(user);
            msg = "User Record successfully deleted";
            System.out.println(user);
        }else {
            System.out.println(user);
            msg = "User not found";
        }
        return msg;
    }

}
