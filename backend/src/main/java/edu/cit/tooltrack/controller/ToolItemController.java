package edu.cit.tooltrack.controller;

import edu.cit.tooltrack.dto.UploadToolItemDTO;
import edu.cit.tooltrack.entity.ToolItems;
import edu.cit.tooltrack.service.ToolItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/toolitem")
@CrossOrigin(origins = "http://localhost:5173")
public class ToolItemController {
    @Autowired
    ToolItemService toolItemService;

    @Operation(
            summary = "Upload an image",
            description = "Accepts an image file and uploads it to the server. Returns the URL of the uploaded image if successful.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Multipart file containing the image to upload",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "object",
                                    properties = {
                                            @SchemaProperty(name = "file", schema = @Schema(type = "string", format = "binary"))
                                    }
                            )
                    )
            )
    )
    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = toolItemService.uploadImage(file);
        if (imageUrl != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("imageUrl", imageUrl));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "File Upload Unsuccessful"));
        }
    }

    @GetMapping("/getImage/{imageName}")
    public String getImageTool(@PathVariable String imageName){
        return toolItemService.getToolImage(imageName);
    }

    @PostMapping("/addTool")
    public ResponseEntity<?> addTool(@RequestBody UploadToolItemDTO toolItemDTO) {
        ToolItems addedTool = toolItemService.addToolItem(toolItemDTO);
        if (addedTool != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(addedTool);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Tool Item Addition Unsuccessful"));
        }
    }

}
