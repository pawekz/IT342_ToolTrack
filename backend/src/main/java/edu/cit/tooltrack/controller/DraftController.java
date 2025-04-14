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


    //    @GetMapping("/{imageName}")
//    public String getImageTool(@PathVariable String imageName){
//        return toolItemService.getToolImage(imageName);
//    }


}
