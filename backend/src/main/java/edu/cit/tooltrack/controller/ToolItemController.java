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
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/toolitem")
@CrossOrigin(origins = {"http://localhost:5173", "https://tooltrack-frontend-hteudjc6beaqhudr.southeastasia-01.azurewebsites.net"})
public class ToolItemController {
    @Autowired
    private ToolItemService toolItemService;
    @Autowired
    private ImageChunkUploader imageChunkUploader;
    @Autowired
    private ImageChunkUploader chunkUploadService;

    //Uploading the tool image
    @PostMapping("/upload")
    public ResponseEntity<?> uploadChunk(
            @RequestParam String name,
            @RequestParam long size,
            @RequestParam int currentChunkIndex,
            @RequestParam int totalChunks,
            HttpServletRequest request
    ) {
        try {
            String uuidName = java.util.UUID.randomUUID() + "_" + name;
            String result = chunkUploadService.uploadChunk(uuidName, size, currentChunkIndex, totalChunks, request, "Tool_Images/");
            if (result != null) {
                return ResponseEntity.ok().body(Map.of("imageUrl", result, "image_name", uuidName));
            }
            return ResponseEntity.ok().body("{\"message\": \"Chunk uploaded successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"Chunk upload failed: " + e.getMessage() + "\"}");
        }
    }

    //Adding Tool
    @PostMapping("/addTool")
    public ResponseEntity<?> addTool(@RequestBody ToolItems toolItems) {

        System.out.println("image_url: " + toolItems.getImage_url() + "");
        System.out.println("image_name: " + toolItems.getImage_name() + "");
        ToolItems latestToolId = toolItemService.addToolItem(toolItems);
        if (latestToolId != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("toolId", latestToolId.getTool_id()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Tool Item Addition Unsuccessful"));
        }
    }

    //getting a specific tool
    @GetMapping("/getTool")
    public ResponseEntity<?> getToolItem(@RequestParam("tool_id") int toolId){
        ToolItems tool = toolItemService.getToolItem(toolId);
        if (tool != null) {
            return ResponseEntity.ok(tool);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body(Map.of("message", "Tool Item Addition Unsuccessful"));
        }
    }

    @GetMapping("/getAllTool")
    public ResponseEntity<?> getAllToolItems(){
            List<ToolItems> toolItems = toolItemService.getAllItem();
           if(toolItems.size() > 0){
               return ResponseEntity.ok(toolItems);
           }else{
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "No Tool Items Found"));
           }
    }

    //This will be use during submitting the new tool
    @PutMapping("/addQr")
    public ResponseEntity<?> updateTool(
            @RequestParam("image_url") String image_url,
            @RequestParam("tool_id") int tool_id,
            @RequestParam("qr_code_name") String qr_code_name){
        ToolItems toolItems = toolItemService.addQrImage(tool_id, image_url,qr_code_name);
        if (toolItems != null) {
            return ResponseEntity.ok(toolItems);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body(Map.of("message", "Tool Item Addition Unsuccessful"));
        }
    }

    @PostMapping("/add/qr/{tool_id}")
    public ResponseEntity<?> updateTool(@RequestParam String tool_id,
                                        @RequestBody ToolItems toolItems){
        return null;
    }

    @DeleteMapping("/delete/{toolId}")
    public ResponseEntity<?> updateTool(@PathVariable String toolId){

        String deleteMessage = toolItemService.deleteToolItem(toolId);

        if(deleteMessage.equals("Tool Item deleted successfully")){
            return ResponseEntity.ok(Map.of("message", deleteMessage));
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", deleteMessage));
        }
    }



}
