package edu.cit.tooltrack.service;

import edu.cit.tooltrack.utils.ToolItemDataExtractor;
import edu.cit.tooltrack.dto.UploadToolItemDTO;
import edu.cit.tooltrack.entity.ToolCategory;
import edu.cit.tooltrack.entity.ToolItems;
import edu.cit.tooltrack.repository.ToolCategoryRepository;
import edu.cit.tooltrack.repository.ToolItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public ToolItems addToolItem(UploadToolItemDTO toolItemDTO) {
        try {
            toolItemDTO.getToolItem().setCategory_id(toolCategoryRepository.findByName(toolItemDTO.getToolCategory()));
            return toolItemRepository.save(toolItemDTO.getToolItem());
        }catch (Exception error){
            System.out.println(error.getMessage());
            return null;
        }
    }

    public String uploadImage(MultipartFile file) {
        try{
            return s3Service.uploadFile(file);
        }catch (Exception error){
            return null;
        }
    }

    public String getToolImage(String imageName){
        return s3Service.getImage(imageName);
    }
}
