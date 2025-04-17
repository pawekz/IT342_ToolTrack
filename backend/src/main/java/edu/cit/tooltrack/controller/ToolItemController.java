package edu.cit.tooltrack.controller;

import edu.cit.tooltrack.dto.UploadToolItemDTO;
import edu.cit.tooltrack.entity.ToolItems;
import edu.cit.tooltrack.service.ImageChunkUploader;
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
    private ToolItemService toolItemService;
    @Autowired
    private ImageChunkUploader imageChunkUploader;

    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadImage(
            @RequestParam("uploadId") String uploadId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileName") String fileName,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("totalChunks") int totalChunks)
    {
//        String imageUrl = imageChunkUploader.uploadChunk(file,uploadId ,fileName, chunkIndex, totalChunks, "Tool_Images/");
//        if (imageUrl != null) {
//            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("imageUrl", imageUrl));
//        } else {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "File Upload Unsuccessful"));
//        }
        return null;
    }

    @GetMapping("/getImage/{imageName}")
    public String getImageTool(@PathVariable String imageName){
        return toolItemService.getToolImage(imageName);
    }


    @PostMapping("/addTool")
    public ResponseEntity<?> addTool(@RequestBody UploadToolItemDTO toolItemDTO) {
        ToolItems latestToolId = toolItemService.addToolItem(toolItemDTO);
        if (latestToolId != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("toolId", latestToolId.getTool_id()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Tool Item Addition Unsuccessful"));
        }
    }

}
