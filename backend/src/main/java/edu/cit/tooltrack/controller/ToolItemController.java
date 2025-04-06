package edu.cit.tooltrack.controller;

import edu.cit.tooltrack.dto.UploadToolItemDTO;
import edu.cit.tooltrack.entity.ToolItems;
import edu.cit.tooltrack.service.ToolItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = toolItemService.uploadImage(file);
        if(imageUrl != null){
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("imageUrl", imageUrl));
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "File Upload Unsuccessfull"));
        }
    }

    @GetMapping("/{imageName}")
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
