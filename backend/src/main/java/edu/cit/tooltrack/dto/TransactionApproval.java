package edu.cit.tooltrack.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionApproval {
    private int transactionId;
    private Boolean approvalStatus;
}
