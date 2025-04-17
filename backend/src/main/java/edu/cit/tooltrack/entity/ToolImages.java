package edu.cit.tooltrack.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tool_images")
public class ToolImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tool_id;
    private String image_url;

    @JsonBackReference("toolItem_image")
    @ManyToOne
    @JoinColumn(name = "fk_tool_items")
    private ToolItems tool_item;
}
