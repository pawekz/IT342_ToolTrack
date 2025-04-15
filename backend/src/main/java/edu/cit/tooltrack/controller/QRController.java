package edu.cit.tooltrack.controller;

import edu.cit.tooltrack.service.QRcodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/qrcode")
public class QRController {

    @Autowired
    private QRcodeService qrcodeService;

    @PostMapping("/create/{toolId}")
    public ResponseEntity<byte[]> createQRAsMultipart(@PathVariable("toolId") String toolId) {
        try {
            byte[] qrCodeData = qrcodeService.createQR("tool_id: " + toolId);
            // Set headers for multipart response (e.g., Content-Disposition)
            System.out.println(qrCodeData);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + toolId + "_qrcode.png");
            headers.add(HttpHeaders.CONTENT_TYPE, "image/png");
            return new ResponseEntity<>(qrCodeData, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = qrcodeService.uploadImage(file);
        if(imageUrl != null){
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("imageUrl", imageUrl));
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "File Upload Unsuccessfull"));
        }
    }

    //get QR Image

}
