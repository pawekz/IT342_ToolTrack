package edu.cit.tooltrack.repository;

import edu.cit.tooltrack.entity.ToolCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolCategoryRepository extends JpaRepository<ToolCategory, Integer> {
    @Query("SELECT t FROM ToolCategory t WHERE t.name = :name")
    ToolCategory findByName(@Param("name") String name);
}
