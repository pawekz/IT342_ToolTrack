package edu.cit.tooltrack.service;

import edu.cit.tooltrack.dto.S3DataDTO;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Base64;


//NOTE: Need revision in future
@Service
public class S3Service {

    private final String BUCKET_NAME = "academics-bucket"; // Change this
    private final String BUCKET_KEY = "System-Integ/Tool_Images/";
    private S3Client s3;

    private S3Service() {
        if (s3 == null) {
            s3 = S3Client.builder()
                    .region(Region.AP_SOUTHEAST_1)
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(
                                    System.getenv("AWS_ACCESS_KEY"),
                                            System.getenv("AWS_ACCESS_SECRET_KEY")
                            )
                    ))
                    .build();
        }
    }

    // Upload Image
    public String uploadImage(S3DataDTO s3DataDTO) {
            // Decode Base64 to byte array
            byte[] imageBytes = decodeBase64(s3DataDTO.getImageBase64());
            System.out.println(s3DataDTO.getImageName());

            // Create S3 PutObjectRequest
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(BUCKET_KEY + s3DataDTO.getImageName())
                    .contentType(getContentType(s3DataDTO.getImageName())) // Set content type
                    .build();

            // Upload file to S3
            s3.putObject(request, RequestBody.fromBytes(imageBytes));

            System.out.println("Image uploaded successfully: " + s3DataDTO.getImageName());
            return s3DataDTO.getImageName(); // Return the S3 key (filename)
    }

    public String getImage(String s3Key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(BUCKET_KEY + s3Key)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(getObjectRequest);
            byte[] imageBytes = objectBytes.asByteArray();

            return Base64.getEncoder().encodeToString(imageBytes); // Encode to Base64
        } catch (Exception e) {
            System.err.println("Error fetching image: " + e.getMessage());
            return null; // Or handle error appropriately
        }
    }


    private static byte[] decodeBase64(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

    // Get Content Type Based on File Extension
    private static String getContentType(String fileName) {
        if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream"; // Default if unknown
        }
    }

}
