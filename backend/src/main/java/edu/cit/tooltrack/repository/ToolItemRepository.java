package edu.cit.tooltrack.repository;

import edu.cit.tooltrack.entity.ToolItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolItemRepository extends JpaRepository<ToolItems, Integer> {

    @Query(value = "SELECT * FROM tool_items ORDER BY tool_id DESC LIMIT 1", nativeQuery = true)
    ToolItems findLatestToolItem();

    @Query(value = "SELECT * FROM tool_items WHERE name = :name", nativeQuery = true)
    ToolItems findItemByName(@Param("name") String name);

    @Query(value = "SELECT name FROM tool_items", nativeQuery = true)
    List<String> getAllItemNames();

    ToolItems findByCategory(String category);
}
