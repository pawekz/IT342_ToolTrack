package edu.cit.tooltrack.dto;

import edu.cit.tooltrack.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class UserResponseDTO {
    private String email;
    private String role;
    private String first_name;
    private String last_name;
    private Boolean isGoogle;
}
