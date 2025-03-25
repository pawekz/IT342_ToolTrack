package edu.cit.tooltrack.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="tool_transactions")
public class ToolTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transaction_id;

    @JsonManagedReference
    @OneToMany(mappedBy = "transaction_id")
    private List<TransactionImage> transactionImage;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "fk_tool_transactions_created_by")
    private User created_by;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "fk_tool_transactions_tool")
    private ToolItems tool_id;

    @JsonBackReference
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
    private Timestamp created_at = null;
    private Timestamp updated_at;  // Comment: null on update CURRENT_TIMESTAMP, what do you mean?


    private enum Status {
        pending, approved, active, completed, overdue
    }

    private enum ConditionBefore {
        Fresh, good, fair, worn, damaged, broken
    }

    private enum ConditionAfter {
        good, fair, worn, damaged, broken
    }

    private enum TransactionType {
        borrow, returned
    }

    @PrePersist
    protected void onCreate() {
        this.created_at = Timestamp.valueOf(LocalDateTime.now());
        this.updated_at = Timestamp.valueOf(LocalDateTime.now());
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated_at = Timestamp.valueOf(LocalDateTime.now());
    }



}
