package edu.cit.tooltrack.controller;


import edu.cit.tooltrack.dto.UploadToolItemDTO;
import edu.cit.tooltrack.service.QRcodeService;
import edu.cit.tooltrack.service.ToolItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@CrossOrigin("")
@RestController
@RequestMapping("/test")
public class DraftController {

    @Autowired
    private ToolItemService toolItemService;
    @Autowired
    private QRcodeService qrcodeService;

    @GetMapping("/test")
    public String getDrafts(){
        return "hello nitwitt";
    }

//    //upload the tool image
    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = qrcodeService.uploadImage(file);
        if(imageUrl != null){
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("imageUrl", imageUrl));
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "File Upload Unsuccessfull"));
        }
    }

//    @GetMapping("/{imageName}")
//    public String getImageTool(@PathVariable String imageName){
//        return toolItemService.getToolImage(imageName);
//    }

    //generate a qr image
    //return: image format
    @PostMapping("/qrcode/{toolId}")
    public ResponseEntity<byte[]> createQRAsMultipart(@PathVariable("toolId") String toolId) {
        try {
            byte[] qrCodeData = qrcodeService.createQR("tool_id: " + toolId);
            // Set headers for multipart response (e.g., Content-Disposition)
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + toolId + "_qrcode.png");
            headers.add(HttpHeaders.CONTENT_TYPE, "image/png");
            return new ResponseEntity<>(qrCodeData, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

}
