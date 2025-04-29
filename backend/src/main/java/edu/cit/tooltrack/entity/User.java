package edu.cit.tooltrack.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_id;

    @JsonManagedReference("toolTransaction")
    @OneToMany(mappedBy = "user_id")
    private List<ToolTransaction> transactions;

    @JsonManagedReference("notifications")
    @OneToMany(mappedBy = "user_id")
    private List<Notifications> notifications;

    @JsonManagedReference("returnTransaction")
    @OneToMany(mappedBy = "user_id")
    private List<ReturnTransactionImage> returnTransaction_Image_image;

    private String first_name;
    private String last_name;
    private String email;
    private String password_hash;
    private Boolean isGoogle;
    private String role;
    private String image_url;
    private int is_active;
    private Timestamp created_at;
    private Timestamp updated_at; // Comment: null on update CURRENT_TIMESTAMP, what do you mean?

    public enum Role {
        Admin, Staff
    }

}
