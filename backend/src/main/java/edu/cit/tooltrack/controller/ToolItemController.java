package edu.cit.tooltrack.controller;

import edu.cit.tooltrack.dto.ToolBorrowDTO;
import edu.cit.tooltrack.entity.ToolItems;
import edu.cit.tooltrack.service.ImageChunkUploader;
import edu.cit.tooltrack.service.ToolItemService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

    //Edit Tool
    @PostMapping("/editTool")
    public ResponseEntity<?> editTool(@RequestBody ToolItems toolItems) {
        ToolItems latestToolId = toolItemService.updateToolItem(toolItems);
        if (latestToolId != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("tool", latestToolId));
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
    public ResponseEntity<?> addQr(
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
    public ResponseEntity<?> updateTool(@PathVariable String tool_id,
                                        @RequestBody ToolItems toolItems){

        return null;
    }

    @DeleteMapping("/delete/{toolId}")
    public ResponseEntity<?> deleteTool(@PathVariable String toolId){

        String deleteMessage = toolItemService.deleteToolItem(toolId);

        if(deleteMessage.equals("Tool Item deleted successfully")){
            return ResponseEntity.ok(Map.of("message", deleteMessage));
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", deleteMessage));
        }
    }

    @GetMapping("/borrow/{toolId}")
    public ResponseEntity<?> borrow(@PathVariable int toolId){
        ToolItems toolItem = toolItemService.getToolItem(toolId);
        if(toolItem != null){
            return ResponseEntity.ok(Map.of("toolItem", new ToolBorrowDTO(toolItem)));
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Tool Item not found"));
        }
    }

    @GetMapping("/search/tool/{toolName}")
    public ResponseEntity<?> searchItem(@PathVariable String toolName){
        String decodedToolName = URLDecoder.decode(toolName, StandardCharsets.UTF_8);
        ToolBorrowDTO item = toolItemService.getToolItemByName(decodedToolName);
        if(item != null){
            return ResponseEntity.ok(Map.of("toolItem", item));
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Tool Item not found"));
        }
    }

    @GetMapping("/search/tool/category/{category}")
    public ResponseEntity<?> searchCategory(@PathVariable String category) {
        String decodedCategory = java.net.URLDecoder.decode(category, StandardCharsets.UTF_8);
        List<ToolItems> item = toolItemService.getToolItemByCategory(decodedCategory);

        if (item != null) {
            return ResponseEntity.ok(Map.of("toolItem", item));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Tool Item not found for category: " + decodedCategory));
        }
    }


    @GetMapping("/getAllNames")
    public ResponseEntity<?> getAllName() {
        return ResponseEntity.ok(Map.of("Items", toolItemService.getAllToolItemNames()));
    }

    @GetMapping("/getTotalTools")
    public ResponseEntity<?>  getTotalTools() {
        int total = toolItemService.getTotalTools();
        if(total != 0){
            return ResponseEntity.ok(Map.of("total", total));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "No Tool Items Found"));
    }

}
