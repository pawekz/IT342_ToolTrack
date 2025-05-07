package edu.cit.tooltrack.Websocket;

import edu.cit.tooltrack.dto.NotificationMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class NotificationSocketController {


    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/greetings")  // Handles /app/greeting //if user will send a message to server like a chat
    @SendTo("/topic/greetings")   // Broadcasts to subscribers of /topic/greetings
    public String greeting(String Message) {
        return "Welcome "+Message +"to Websocket Springboot!";
    }

    public void sendNotification(NotificationMessageDTO payload) {
        if (payload.getUser_email() == null || payload.getUser_email().isBlank()) {
            System.out.println("Error: User email is missing in the payload");
            return;
        }
        System.out.println("Sending to user: " + payload.getUser_email());
        messagingTemplate.convertAndSendToUser(payload.getUser_email(), "/queue/notify", payload);
    }

}
