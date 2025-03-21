package edu.cit.tooltrack.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="tool_items")
public class ToolItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tool_id;
//
//   //Foreign Keys
//    @OneToOne
//    @JoinColumn(name="tool_items_ibfk_1")
//    @Column(nullable = false)
//    private ToolCategory category_id;
//
//    //Reference Key
//    @OneToOne(mappedBy = "tool_id")
//    private ToolTransaction ref_tool_transactions;
//
//    @Column(nullable = false)
//    private String name;
//    private String serial_number;
//    private String qr_code;
//
//    @Column(nullable = false)
//    @Enumerated(EnumType.STRING)
//    private Condition  condition = Condition.NEW;
//
//    @Column(nullable = false)
//    @Enumerated(EnumType.STRING)
//    private Status status = Status.AVAILABLE;
//
//    private String location;
//    private String description;
//
//    @Column(nullable = false)
//    private Date date_acquired;
//    private String image_url;
//    private Timestamp created_at;
//    private Timestamp updated_at; // Comment: null on update CURRENT_TIMESTAMP, what do you mean?
//
//
//
//    @PrePersist //before it save to db this will run first to ensue the variables will not be empty
//    protected void onCreate() {
//        this.created_at = Timestamp.valueOf(LocalDateTime.now());
//        this.updated_at = Timestamp.valueOf(LocalDateTime.now());
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        this.updated_at = Timestamp.valueOf(LocalDateTime.now());
//    }
//
//    private enum Condition {
//        NEW, GOOD, FAIR, WORN, DAMAGED, BROKEN
//    }
//
//    private enum Status {
//        AVAILABLE, BORROWED, MAINTENANCE, RETIRED
//    }
}
