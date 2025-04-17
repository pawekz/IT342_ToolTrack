package edu.cit.tooltrack.service;

import edu.cit.tooltrack.dto.UploadToolItemDTO;
import edu.cit.tooltrack.entity.ToolImages;
import edu.cit.tooltrack.entity.ToolItems;
import edu.cit.tooltrack.repository.ToolCategoryRepository;
import edu.cit.tooltrack.repository.ToolImagesRepository;
import edu.cit.tooltrack.repository.ToolItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ToolItemService {

    @Autowired
    private ToolItemRepository toolItemRepository;
    @Autowired
    private ToolCategoryService toolCategoryService;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private ToolImagesService toolImagesServ;
    @Autowired
    private ImageChunkUploader imageChunkUploader;

    public ToolItems addToolItem(UploadToolItemDTO toolItemDTO) {
        try {
            toolItemDTO.getToolItem().setCategory_id(toolCategoryService.getCategory(toolItemDTO.getToolCategory()));
            ToolItems savedToolItem = toolItemRepository.save(toolItemDTO.getToolItem());
            for (String imageUrl : toolItemDTO.getImages()) {
                toolImagesServ.add(imageUrl, savedToolItem);
            }
            return toolItemRepository.findLatestToolItem();
        }catch (Exception error){
            System.out.println(error.getMessage());
            return null;
        }
    }

    public String uploadImage(MultipartFile file,
                              String uploadId,
                              String fileName,
                              int chunkIndex,
                              int totalChunks) {
        try {
            if (file.isEmpty() || fileName == null || fileName.isBlank()) {
                return "Invalid file or file name.";
            }
//            return imageChunkUploader.uploadChunk(file, uploadId ,fileName, chunkIndex, totalChunks, "Tool_Images/");
        } catch (Exception error) {
            error.printStackTrace();
            return "An error occurred while uploading the image.";
        }
        return null;
    }

    public ToolItems getToolItem(int id) {
        return toolItemRepository.findById(id).orElse(null);
    }

    public List<ToolItems> getAllItem(){
        return toolItemRepository.findAll();
    }

    public String getToolImage(String imageName){
        return s3Service.getImage(imageName, "Tool_Images/");
    }
}
