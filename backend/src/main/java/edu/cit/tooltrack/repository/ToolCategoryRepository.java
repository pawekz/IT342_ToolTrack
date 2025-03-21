package edu.cit.tooltrack.repository;

import edu.cit.tooltrack.entity.ToolCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToolCategoryRepository extends JpaRepository<ToolCategory, Integer> {
}
