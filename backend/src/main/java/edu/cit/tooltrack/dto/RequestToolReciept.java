package edu.cit.tooltrack.dto;

import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;

@Getter
@Setter
public class RequestToolReciept {
    private String name;
    private String image_url;
    private String location;
    private String condition;
    private Timestamp borrow_date;
    private Timestamp return_date;
}
