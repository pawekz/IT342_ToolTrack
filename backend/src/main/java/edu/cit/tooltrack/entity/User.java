package edu.cit.tooltrack.entity;

import jakarta.persistence.*;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_id;

    @OneToMany(mappedBy = "user_id")
    private List<ToolTransaction> toolTransaction_userid;

    @OneToMany(mappedBy = "created_by")
    private List<ToolTransaction> toolTransaction_created_by;

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
