package edu.cit.tooltrack.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name="tool_categories")
public class ToolCategory {
//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int category_id;
//
//    //Reference  Key
//    @OneToOne(mappedBy = "category_id")
//    private ToolItem ref_category_id;
//
//    @OneToOne(mappedBy = "tool_id")
//    private ToolTransaction ref_tool_items;
//
//    @Column(unique = true)
//    private String name;
//
//    private String description;
//    private Timestamp created_at;
//    private Timestamp updated_at;


}
