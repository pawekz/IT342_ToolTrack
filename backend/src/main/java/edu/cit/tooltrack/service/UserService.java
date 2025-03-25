package edu.cit.tooltrack.service;

import edu.cit.tooltrack.dto.LoginRequest;
import edu.cit.tooltrack.dto.UserResponseDTO;
import edu.cit.tooltrack.entity.User;
import edu.cit.tooltrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.naming.NameNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public UserResponseDTO getUserData(String email){
        User user = userRepository.findByEmail(email);
        return new UserResponseDTO(user.getEmail(),user.getFirst_name(), user.getLast_name());
    }

    public UserResponseDTO addGoogleUser(OAuth2User oAuth2User){
        User user = new User();
        user.setIsGoogle(true);
        user.setFirst_name(oAuth2User.getAttributes().get("given_name").toString());
        user.setLast_name(oAuth2User.getAttributes().get("family_name").toString());
        user.setEmail(oAuth2User.getAttributes().get("email").toString());
        user.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));
        userRepository.save(user);
        return new UserResponseDTO(user.getEmail(), user.getFirst_name(), user.getLast_name());
    }


    public UserResponseDTO addUser(User user){
        user.setIs_active(1);
        user.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));
         userRepository.save(user);
         return new UserResponseDTO(user.getEmail(), user.getFirst_name(), user.getLast_name());
    }

    @SuppressWarnings("finally")
    public UserResponseDTO editUser(User newdata) {
        User user = userRepository.findByEmail(newdata.getEmail());
        try {
            user.setEmail(newdata.getEmail());
            user.setFirst_name(newdata.getFirst_name());
            user.setLast_name(newdata.getLast_name());
            user.setPassword_hash(newdata.getPassword_hash());
            user.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
        }catch (NoSuchElementException error) {
            throw new NameNotFoundException("User" + newdata.getEmail() + "not found");
        } finally {
             userRepository.save(user);
             return new UserResponseDTO(user.getEmail(), user.getFirst_name(), user.getLast_name());
        }
    }

    //delete
    @SuppressWarnings({ "unused" })
    public String deleteUser(String email) {
        String msg="";

        if(userRepository.findByEmail(email)!=null) {
            userRepository.deleteByEmail(email);
            msg = "User Record successfully deleted";
        }else {
            msg = email + "NOT found";
        }
        return msg;
    }

}
