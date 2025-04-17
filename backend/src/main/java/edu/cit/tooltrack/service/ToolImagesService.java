package edu.cit.tooltrack.service;

import edu.cit.tooltrack.entity.ToolImages;
import edu.cit.tooltrack.entity.ToolItems;
import edu.cit.tooltrack.repository.ToolImagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToolImagesService {
    @Autowired
    private ToolImagesRepository toolImagesRepo;

    public ToolImages add(String image_url, ToolItems toolItem) {
        ToolImages toolImage = new ToolImages();
        toolImage.setImage_url(image_url);
        toolImage.setTool_item(toolItem);
        return toolImagesRepo.save(toolImage);
    }
    
    public List<ToolImages> getAllToolImages(){
        return toolImagesRepo.findAll();
    }

    public ToolImages getImage(int toolItem_id) {
        return toolImagesRepo.findById(toolItem_id).orElse(null);
    }
}
