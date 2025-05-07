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

//    @MessageMapping("/notifications")
//    @SendTo("/topic/notifications")
//    public NotificationMessageDTO notifyUser(NotificationMessageDTO payload) {
//            return payload;
//    }
}
