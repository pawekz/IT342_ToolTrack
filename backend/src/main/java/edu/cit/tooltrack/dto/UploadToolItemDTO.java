package edu.cit.tooltrack.dto;

import edu.cit.tooltrack.entity.ToolItems;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;


@NoArgsConstructor
@Getter
@Setter
public class UploadToolItemDTO {
    @Autowired
    private ToolItems toolItem;
    private String toolCategory;
}
