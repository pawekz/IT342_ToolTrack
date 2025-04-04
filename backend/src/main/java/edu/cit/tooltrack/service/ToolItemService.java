package edu.cit.tooltrack.service;

import edu.cit.tooltrack.utils.ToolItemDataExtractor;
import edu.cit.tooltrack.dto.UploadToolItemDTO;
import edu.cit.tooltrack.entity.ToolCategory;
import edu.cit.tooltrack.entity.ToolItems;
import edu.cit.tooltrack.repository.ToolCategoryRepository;
import edu.cit.tooltrack.repository.ToolItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




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
    private ToolItemDataExtractor toolItemDataExtractor;

    public String addToolItem(UploadToolItemDTO toolItemDTO) {
        //filter the data and extract
        ToolCategory category =  toolCategoryRepository.findByName("Handtools");
        ToolItems toolItem = new ToolItems();
        toolItem.setName(toolItemDTO.getToolItem().getName());
        toolItem.setSerial_number(toolItemDTO.getToolItem().getSerial_number());
        toolItem.setQr_code(toolItemDTO.getToolItem().getQr_code());
        toolItem.setLocation(toolItemDTO.getToolItem().getLocation());
        toolItem.setDescription(toolItemDTO.getToolItem().getDescription());
        toolItem.setDate_acquired(toolItemDTO.getToolItem().getDate_acquired());
        try {
            this.toolItemDataExtractor.ExtractData(toolItemDTO);
            s3Service.uploadImage(toolItemDataExtractor.getS3DataDTO());

            if(toolItemDTO.getToolCategory() == "Handtools") {
                toolItem.setCategory_id(toolCategoryRepository.findByName("Handtools"));
                System.out.println("Handtools if done ");
                toolItemRepository.save(toolItem);
                System.out.println("Handtools! save");
            }else{
                toolItem.setCategory_id(toolCategoryRepository.findByName("PowerTools"));
                toolItemRepository.save(toolItem);
            }
            System.out.println("File Upload Successfully");
            return "File Upload Successfully";
        }catch (Exception error){
            System.out.println(error.getMessage());
            return "File Upload Unsuccessfull";
        }
    }

    public String getToolImage(String imageName){
        return s3Service.getImage(imageName);
    }
}
