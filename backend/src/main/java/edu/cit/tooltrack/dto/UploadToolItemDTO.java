package edu.cit.tooltrack.dto;

import edu.cit.tooltrack.entity.ToolItems;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@NoArgsConstructor
@Getter
@Setter
public class UploadToolItemDTO {
    private ToolItems toolItem;
    private List<String> images;
    private String toolCategory;
}
