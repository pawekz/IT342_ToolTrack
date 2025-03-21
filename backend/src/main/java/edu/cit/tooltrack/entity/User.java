package edu.cit.tooltrack.entity;

import jakarta.persistence.*;

import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_id;
//
//    //Reference Keys
//    @OneToMany(mappedBy = "user_id")
//    private ToolTransaction ref_tool_transaction;
//
//    @OneToOne(mappedBy = "uploaded_by")
//    private TransactionImage ref_transactionImage;
//
//    //Foreign Key
//    @JoinColumn(name="users_ibfk_1")
//    private Employee fk_employee_id;
//
//    private BigInteger employee_id;
//    @Column(nullable = false)
//    private String first_name;
//    @Column(nullable = false)
//    private String last_name;
//
//    @Column(unique = true)
//    private String email;
//    @Column(nullable = false)
//    private String password_hash;
//    @Column(nullable = false)
//
//    private Role role;
//    private String image_url;
//    private int is_active;
//    private Timestamp created_at;
//    private Timestamp updated_at; // Comment: null on update CURRENT_TIMESTAMP, what do you mean?
//
//    public enum Role {
//        admin, staff, user
//    }

}
