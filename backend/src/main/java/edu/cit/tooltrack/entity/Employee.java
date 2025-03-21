package edu.cit.tooltrack.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "employees")
public class Employee {
//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int employee_id;
//
//    //Reference Key
//    @OneToOne(mappedBy = "fk_employee_id",  cascade = CascadeType.ALL)
//    private User users;
//
//    private String designation;
//    private String department;
//    private EnumType employee_type;
//    private Date date;
//    private Timestamp created_at;
//    private Timestamp updated_at;

}