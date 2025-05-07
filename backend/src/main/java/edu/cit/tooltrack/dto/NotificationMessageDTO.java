package edu.cit.tooltrack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class NotificationMessageDTO {
    private String toolName;
    private String message;
    private String status;
    private Timestamp borrow_date;
    private Timestamp due_date;
    private String user_email;
}
