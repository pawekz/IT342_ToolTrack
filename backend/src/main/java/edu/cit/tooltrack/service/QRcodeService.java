package edu.cit.tooltrack.service;

import com.genqrcode.api.GenQRCodeApi;
import com.genqrcode.api.TextQRCodeBuilder;
import com.genqrcode.customization.Color;
import com.genqrcode.customization.Format;
import com.genqrcode.customization.SquaresShape;
import com.genqrcode.data.TextQRCodeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class QRcodeService {

    private GenQRCodeApi genQRCodeApi;
    @Autowired
    private S3Service s3Service;

    public QRcodeService() {
        this.genQRCodeApi = new GenQRCodeApi(System.getenv("QR_DYNAMIC_API"));
    }

    public byte[] createQR(String data) {
        try {
            return genQRCodeApi.textQRCode()
                    .format(Format.PNG)
                    .data(TextQRCodeData.withText(data))
                    .width(500)
                    .height(500)
                    .build()
                    .get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage(), e);
        }
    }

    public String uploadImage(MultipartFile file) {
        try{
            return s3Service.uploadFile(file, "QR_Images/");
        }catch (Exception error){
            return null;
        }
    }
}
