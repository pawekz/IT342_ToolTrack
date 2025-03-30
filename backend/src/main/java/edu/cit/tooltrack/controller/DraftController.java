package edu.cit.tooltrack.controller;


import edu.cit.tooltrack.dto.UploadToolItemDTO;
import edu.cit.tooltrack.service.ToolItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin("")
@RestController
@RequestMapping("/tool")
public class DraftController {

    @Autowired
    private ToolItemService toolItemService;

    @PostMapping("/uploadTool")
    public ResponseEntity<?> uploadTool(@RequestBody UploadToolItemDTO toolItemDTO){
        if("File Upload Successfully".equals(toolItemService.addToolItem(toolItemDTO))){
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "File Upload Successfully"));
        }
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "File Upload Unsuccessfull"));
    }

    @GetMapping("/{imageName}")
    public String getImageTool(@PathVariable String imageName){
        return toolItemService.getToolImage(imageName);
    }
}
