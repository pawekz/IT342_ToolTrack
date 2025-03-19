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
@Table(name="tool_transactions")
public class ToolTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transaction_id;

    //Foreign Keys
    @JoinColumn(name = "tool_transactions_ibfk_1")
    private ToolItem tool_id;

    @JoinColumn(name="tool_transactions_ibfk_2")
    private User user_id;

    //Reference Key
    @OneToOne(mappedBy = "transaction_id")
    private TransactionImage ref_transactionImage;

    private TransactionType transaction_type;
    private String reason;
    @Column(nullable = false)
    private ConditionBefore condition_before;
    private ConditionAfter condition_after;

    @Column(nullable = false)
    private Timestamp borrow_date;
    @Column(nullable = false)
    private Timestamp due_date;
    @Column(nullable = false)
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
