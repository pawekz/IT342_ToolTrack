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
@CrossOrigin(origins = {"http://localhost:5173", "https://tooltrack-frontend-hteudjc6beaqhudr.southeastasia-01.azurewebsites.net"})
public class QRController {

    @Autowired
    private QRcodeService qrcodeService;

    @PostMapping("/create/{toolId}")
    public ResponseEntity<?> createQRAsMultipart(@PathVariable("toolId") String toolId) {
        try {
            byte[] qrCodeData = qrcodeService.createQR("tool_id: " + toolId);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "image/png");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + toolId + "_qrcode.png");

            return new ResponseEntity<>(qrCodeData, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to generate QR code"));
        }
    }


    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam int toolId) {

        String uuidName = java.util.UUID.randomUUID() + "_" + toolId;
        String imageUrl = qrcodeService.uploadImage(file, uuidName);
        if (imageUrl != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "imageUrl", imageUrl,
                    "qr_code_name", uuidName
            ));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", "File Upload Unsuccessful"
            ));
        }
    }

    //get QR Image

}
