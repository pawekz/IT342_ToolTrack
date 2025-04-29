package edu.cit.tooltrack.dto;

import edu.cit.tooltrack.entity.ToolItems;
import edu.cit.tooltrack.entity.ToolTransaction.Status;
import edu.cit.tooltrack.entity.User;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import net.minidev.json.annotate.JsonIgnore;

@AllArgsConstructor
@Getter
@Setter
public class TransactionsDTO {
    private int transaction_id;
    private int user_id;
    private String user_firstName;
    private String user_lastName;
    private int tool_id;;
    private String tool_name;
}
