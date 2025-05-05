package edu.cit.tooltrack.service;

import edu.cit.tooltrack.dto.ToolBorrowDTO;
import edu.cit.tooltrack.dto.TransactionsDTO;
import edu.cit.tooltrack.entity.ToolItems;
import edu.cit.tooltrack.entity.ToolTransaction;
import edu.cit.tooltrack.entity.User;
import edu.cit.tooltrack.repository.ToolItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.tools.Tool;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ToolItemService {

    @Autowired
    private ToolItemRepository toolItemRepository;
    @Autowired
    private ImageChunkUploader imageChunkUploader;
    @Autowired
    private S3Service s3Service;

    public ToolItems addToolItem(ToolItems toolItems) {
        try {
            toolItemRepository.save(toolItems);
            return toolItemRepository.findLatestToolItem();
        }catch (Exception error){
            System.out.println(error.getMessage());
            return null;
        }
    }


    public ToolItems getToolItem(int id) {
        return toolItemRepository.findById(id).orElse(null);
    }

    public List<ToolItems> getAllItem(){
        return toolItemRepository.findAll();
    }

    public ToolItems addQrImage(int toolId, String qr_url, String qr_name) {
        try {
            ToolItems old_tool = toolItemRepository.findById(toolId).orElse(null);
            old_tool.setQr_code(qr_url);
            old_tool.setQr_code_name(qr_name);
            return toolItemRepository.save(old_tool);
        } catch (Exception e) {
            return null;
        }
    }

    public ToolItems updateToolItem(ToolItems newToolData) {
        try {
            ToolItems old_tool = toolItemRepository.findById(newToolData.getTool_id()).orElse(null);

            if (old_tool != null) {
                old_tool.setName(newToolData.getName());
                old_tool.setCategory(newToolData.getCategory());
                old_tool.setLocation(newToolData.getLocation());
                old_tool.setDescription(newToolData.getDescription());
                old_tool.setDate_acquired(newToolData.getDate_acquired());
                old_tool.setImage_url(newToolData.getImage_url());
                old_tool.setImage_name(newToolData.getImage_name());
                toolItemRepository.save(old_tool);
                return old_tool;
            } else {
                throw new IllegalArgumentException("No Item found");
            }
        } catch (IllegalArgumentException error) {
            return null;
        }
    }

    @SuppressWarnings({ "unused" })
    public String deleteToolItem(String toolId) {
        String message = null;

        try {
            ToolItems toolItem = toolItemRepository.findById(Integer.parseInt(toolId)).orElse(null);
            if(toolItem != null){
                s3Service.deleteImage(toolItem.getImage_name(), "Tool_Images/");
                s3Service.deleteImage(toolItem.getQr_code_name(), "QR_Images/");
                toolItemRepository.deleteById(Integer.parseInt(toolId));
                message = "Tool Item deleted successfully";
            }
        }catch(NoSuchElementException e){
            message = "Tool Item not found";
        }
        return message;
    }

    public List<String> getAllToolItemNames(){
        return toolItemRepository.getAllItemNames();
    }

    public ToolBorrowDTO getToolItemByName(String name) {
        try {
            ToolItems toolItem = toolItemRepository.findItemByName(name);
            if (toolItem != null) {
                return new ToolBorrowDTO(toolItem);
            } else {
                throw new NoSuchElementException("Item not found");
            }
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public List<ToolItems> getToolItemByCategory(String category) {
        try {
            return toolItemRepository.findByCategory(category);
        } catch (Exception e) {
            return null;
        }
    }

    public int getTotalTools(){
        return toolItemRepository.findAll().size();
    }

    public String dateFormat(Timestamp timestamp) {
        return null;
    }
}
