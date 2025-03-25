package edu.cit.tooltrack.service;

import edu.cit.tooltrack.entity.User;
import edu.cit.tooltrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.naming.NameNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Boolean isUserExist(String email){
        User user = userRepository.findByEmail(email);
        return user != null;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User addGoogleUser(OAuth2User oAuth2User){
        User user = new User();
        user.setIsGoogle(true);
        user.setFirst_name(oAuth2User.getAttributes().get("given_name").toString());
        user.setLast_name(oAuth2User.getAttributes().get("family_name").toString());
        user.setEmail(oAuth2User.getAttributes().get("email").toString());
        return userRepository.save(user);
    }


    public User addUser(User user){
        return userRepository.save(user);
    }

    @SuppressWarnings("finally")
    public User editUser(User newdata) {
        User user = userRepository.findByEmail(newdata.getEmail());
        try {

            user.setEmail(newdata.getEmail());
            user.setFirst_name(newdata.getFirst_name());
            user.setLast_name(newdata.getLast_name());
            user.setPassword_hash(newdata.getPassword_hash());
        }catch (NoSuchElementException error) {
            throw new NameNotFoundException("User" + newdata.getEmail() + "not found");
        } finally {
            return userRepository.save(user);
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
