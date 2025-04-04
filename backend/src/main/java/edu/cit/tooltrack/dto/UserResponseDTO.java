package edu.cit.tooltrack.dto;

import edu.cit.tooltrack.entity.User.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class UserResponseDTO {
    private String email;
    private Role role;
    private String first_name;
    private String last_name;
}
