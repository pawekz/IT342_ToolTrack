package edu.cit.tooltrack.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="tool_categories")
public class ToolCategory {
//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int category_id;

    @JsonManagedReference("toolItem_category")
    @OneToMany(mappedBy = "category_id")
    private List<ToolItems> toolItems;

    private String name;

    private String description;
    private Timestamp created_at;
    private Timestamp updated_at;


}
