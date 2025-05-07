package edu.cit.tooltrack.service;

import edu.cit.tooltrack.Websocket.NotificationSocketController;
import edu.cit.tooltrack.dto.NotificationMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

//    @Autowired
//    private NotificationSocketController notificationSocketController;
@Autowired
private SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String userId, NotificationMessageDTO payload) {
        log.info("Sending notification to " + userId);
        messagingTemplate.convertAndSendToUser(
                userId,
                "/notifications",
                payload
        );
    }

}
