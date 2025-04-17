package edu.cit.tooltrack.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="tool_items")
public class ToolItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tool_id;


    @JsonManagedReference("toolTransaction_toolId")
    @OneToMany(mappedBy = "tool_id")
    private List<ToolTransaction> toolTransaction;

    @JsonBackReference("toolItem_category")
    @ManyToOne
    @JoinColumn(name = "fk_category")
    private ToolCategory category_id;

    private String name;
    private String serial_number;
    private String qr_code;

    @Enumerated(EnumType.STRING)
    private Condition  tool_condition  = Condition.NEW;

    @Enumerated(EnumType.STRING)
    private Status status = Status.AVAILABLE;

    private String location;
    private String description;

    private Date date_acquired;

    @JsonManagedReference("toolItem_image")
    @OneToMany(mappedBy = "tool_item")
    private List<ToolImages> images; // foreign key to the tool_images

    private Timestamp created_at;
    private Timestamp updated_at; // Comment: null on update CURRENT_TIMESTAMP, what do you mean?


    @PrePersist //before it save to db this will run first to ensue the variables will not be empty
    protected void onCreate() {
        this.created_at = Timestamp.valueOf(LocalDateTime.now());
        this.updated_at = Timestamp.valueOf(LocalDateTime.now());
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated_at = Timestamp.valueOf(LocalDateTime.now());
    }

    private enum Condition {
        NEW, GOOD, FAIR, WORN, DAMAGED, BROKEN
    }

    private enum Status {
        AVAILABLE, BORROWED, MAINTENANCE, RETIRED
    }
}
