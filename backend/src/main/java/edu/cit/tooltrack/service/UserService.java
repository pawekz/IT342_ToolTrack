package edu.cit.tooltrack.service;

import edu.cit.tooltrack.dto.LoginRequest;
import edu.cit.tooltrack.dto.UserResponseDTO;
import edu.cit.tooltrack.entity.User;
import edu.cit.tooltrack.repository.UserRepository;
import edu.cit.tooltrack.security.jwt.CustomUserDetails;
import edu.cit.tooltrack.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    public UserResponseDTO verifyUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
       if(user != null && user.getRole().equals("Staff")){
           if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword_hash())) {
               throw new BadCredentialsException("Invalid email or password");
           }
           return new UserResponseDTO(user.getEmail(), user.getRole(), user.getFirst_name(), user.getLast_name(), user.getIsGoogle());
       }else{
           return null;
       }
    }


    public UserResponseDTO verifyBothUsers(LoginRequest loginRequest) {
        User user = getUserFullDetails(loginRequest.getEmail());

        if (user == null) {
            throw new BadCredentialsException("User not found");
        }

        if (user.getPassword_hash() != null &&
                !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword_hash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return new UserResponseDTO(
                user.getEmail(),
                user.getRole(),
                user.getFirst_name(),
                user.getLast_name(),
                user.getIsGoogle()
        );
    }


    public UserResponseDTO verifyAdmin(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if(user.getRole().equals("Admin")){
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword_hash())) {
                throw new BadCredentialsException("Invalid email or password");
            }
            return new UserResponseDTO(user.getEmail(), user.getRole(), user.getFirst_name(), user.getLast_name(), user.getIsGoogle());
        }else{
            return null; //means its a user
        }
    }

    public UserResponseDTO register(User user, String userType) {
        //check for userRole
        if(userType.equals("Staff")){
            user.setIs_active(1);
            user.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));
            user.setRole("Staff");
        }else{
            user.setIs_active(1);
            user.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));
            user.setRole("Admin");
        }
        //check if data has a password, hash it, else, it's a google registration skip the hash
        if(user.getPassword_hash() != null){
            String encodedPassword = passwordEncoder.encode(user.getPassword_hash());
            user.setPassword_hash(encodedPassword);
            user.setIsGoogle(false);
        }else{
            user.setIsGoogle(true);
        }

        User savedUser = userRepository.save(user);
        return new UserResponseDTO(savedUser.getEmail(), savedUser.getRole() ,savedUser.getFirst_name(), savedUser.getLast_name(), savedUser.getIsGoogle());
    }


    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public int getTotalUsers(){
        return userRepository.findAll().size();
    }

    public UserResponseDTO getUserData(String email){
        User user = userRepository.findByEmail(email);
        return new UserResponseDTO(user.getEmail(),user.getRole(),user.getFirst_name(), user.getLast_name(), user.getIsGoogle());
    }

    public User getUserFullDetails(String email){
        return userRepository.findByEmail(email);
    }

    public UserResponseDTO addGoogleUser(User user){
        user.setRole("staff");
        user.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));
        userRepository.save(user);
        return new UserResponseDTO(user.getEmail(), user.getRole() ,user.getFirst_name(), user.getLast_name(), user.getIsGoogle());
    }


    public UserResponseDTO addUser(User user){
        //no checking for if user already existed
        user.setIs_active(1);
        user.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));
         register(user,"Staff");
         return new UserResponseDTO(user.getEmail(), user.getRole(), user.getFirst_name(), user.getLast_name(), user.getIsGoogle());
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
             return new UserResponseDTO(user.getEmail(), user.getRole() ,user.getFirst_name(), user.getLast_name(), user.getIsGoogle());
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
