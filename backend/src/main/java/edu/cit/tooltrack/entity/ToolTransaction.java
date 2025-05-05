package edu.cit.tooltrack.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="tool_transactions")
public class ToolTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transaction_id;

    @JsonManagedReference("return_images")
    @OneToMany(mappedBy = "toolTransaction")
    private List<ReturnTransactionImage> returnImage;

    @JsonBackReference("toolTransaction_toolId")
    @ManyToOne
    @JoinColumn(name = "fk_tool_id")
    private ToolItems tool_id;

    @JsonBackReference("toolTransaction")
    @ManyToOne
    @JoinColumn(name = "fk_tool_transactions_user")
    private User user_id;


    private TransactionType transaction_type;
    private String reason;
    private ConditionBefore condition_before;
    private ConditionAfter condition_after;

    private Timestamp borrow_date;
    private Timestamp due_date;
    private Timestamp return_date;
    private Status status = Status.pending;
    private Timestamp created_at;
    private Timestamp updated_at;


    public enum Status {
        pending, approved, active, completed, overdue, rejected
    }

    public enum ConditionBefore {
        Fresh, good, fair, worn, damaged, broken
    }

    public enum ConditionAfter {
        good, fair, worn, damaged, broken
    }

    public enum TransactionType {
        borrow, returned
    }

    @PrePersist
    protected void onCreate() {
        this.created_at = Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC));
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated_at = Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC));
    }



}
