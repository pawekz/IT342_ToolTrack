package edu.cit.tooltrack.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
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

    @JsonManagedReference
    @OneToMany(mappedBy = "user_id")
    private List<ToolTransaction> toolTransaction_userid;

    @JsonManagedReference
    @OneToMany(mappedBy = "created_by")
    private List<ToolTransaction> toolTransaction_created_by;

    @JsonManagedReference
    @OneToMany(mappedBy = "user_id")
    private List<Notifications> notifications;


    private BigInteger employee_id;
    private String first_name;
    private String last_name;
    private String email;
    private String password_hash;
    private Boolean isGoogle;
    private Role role;
    private String image_url;
    private int is_active;
    private Timestamp created_at;
    private Timestamp updated_at; // Comment: null on update CURRENT_TIMESTAMP, what do you mean?

    public enum Role {
        admin, staff, user
    }

}
