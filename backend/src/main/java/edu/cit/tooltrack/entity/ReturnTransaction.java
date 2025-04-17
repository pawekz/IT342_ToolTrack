package edu.cit.tooltrack.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "return_transaction_images")
public class ReturnTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int return_id;
    private String image_url;

    @ManyToOne
    @JoinColumn(name = "fk_user")
    @JsonBackReference("returnTransaction")
    private User user_id;

    @JsonBackReference("return_images")
    @ManyToOne
    @JoinColumn(name = "fk_tool_transaction")
    private ToolTransaction toolTransaction;

}
