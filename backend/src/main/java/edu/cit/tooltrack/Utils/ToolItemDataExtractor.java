package edu.cit.tooltrack.Utils;


import edu.cit.tooltrack.dto.S3DataDTO;
import edu.cit.tooltrack.dto.UploadToolItemDTO;
import edu.cit.tooltrack.entity.ToolItems;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;


@NoArgsConstructor
@Getter
@Setter
@Component
public class ToolItemDataExtractor {

    private ToolItems toolItems;
    private S3DataDTO s3DataDTO;
    private String imageName;

    public void ExtractData(UploadToolItemDTO toolItemDTO) {

        this.imageName = generateFileName(toolItemDTO.getToolItem().getSerial_number(),
                toolItemDTO.getToolItem().getName(),toolItemDTO.getOriginalFileName());

        //define the data for database
        this.toolItems = new ToolItems();
        this.toolItems = toolItemDTO.getToolItem();
        this.s3DataDTO = new S3DataDTO(toolItemDTO.getImageBase64(), this.imageName);
    }

    private String generateFileName(String serialNumber, String toolName, String originalFilename) {
        String fileExtension = getFileExtension(originalFilename); // Extract file extension
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); // Current time
        return toolName + "_" + serialNumber + fileExtension;
    }

    // Helper method to get file extension
    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return ""; // No extension
    }

}
