package edu.cit.tooltrack.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "notifications")
public class Notifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int notification_id;

    @ManyToOne
    @JoinColumn(name = "fk_notifications_user")
    private User user_id;

    private String title;
    private String message;


}
