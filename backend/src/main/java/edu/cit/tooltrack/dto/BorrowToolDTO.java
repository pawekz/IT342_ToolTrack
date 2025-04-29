package edu.cit.tooltrack.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class BorrowToolDTO {
    private int toolId;
    private String email;
}
