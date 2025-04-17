package edu.cit.tooltrack.service;

import edu.cit.tooltrack.entity.ToolCategory;
import edu.cit.tooltrack.repository.ToolCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ToolCategoryService {

    @Autowired
    private ToolCategoryRepository toolCategoryRepository;


    private ToolCategory add(String name, String description){
        ToolCategory toolCategory = new ToolCategory();
        toolCategory.setName(name);
        toolCategory.setDescription(description);
        return toolCategoryRepository.save(toolCategory);
    }

    public ToolCategory getCategory(String categoryName){
        return toolCategoryRepository.findByName(categoryName);
    }

}
