package edu.cit.tooltrack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
public class S3DataDTO {
    private String imageBase64;
    private String imageName;
}
