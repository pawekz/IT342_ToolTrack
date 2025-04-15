package edu.cit.tooltrack.service;

import edu.cit.tooltrack.utils.ToolItemDataExtractor;
import edu.cit.tooltrack.dto.UploadToolItemDTO;
import edu.cit.tooltrack.entity.ToolCategory;
import edu.cit.tooltrack.entity.ToolItems;
import edu.cit.tooltrack.repository.ToolCategoryRepository;
import edu.cit.tooltrack.repository.ToolItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.tools.Tool;




/*
    The ToolItemDataExtractor Class will abstract the logic of extracting the data from this service.
    It will initialize neccessary data for preparing to upload the file necessary only for S3 and saving the data to the database;
 */

@Service
public class ToolItemService {

    @Autowired
    private ToolItemRepository toolItemRepository;
    @Autowired
    private ToolCategoryRepository toolCategoryRepository;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private ImageChunkUploader imageChunkUploader;

    public ToolItems addToolItem(UploadToolItemDTO toolItemDTO) {
        try {
            toolItemDTO.getToolItem().setCategory_id(toolCategoryRepository.findByName(toolItemDTO.getToolCategory()));
            toolItemRepository.save(toolItemDTO.getToolItem());
            return toolItemRepository.findLatestToolItem();
        }catch (Exception error){
            System.out.println(error.getMessage());
            return null;
        }
    }

    public String uploadImage(MultipartFile file,
                              String fileName,
                              int chunkIndex,
                              int totalChunks) {
        try {
            return imageChunkUploader.uploadChunk(file, fileName, chunkIndex, totalChunks);
        } catch (Exception error) {
            error.printStackTrace();
            return "An error occurred while uploading the image.";
        }
    }

    public String getToolImage(String imageName){
        return s3Service.getImage(imageName);
    }
}
