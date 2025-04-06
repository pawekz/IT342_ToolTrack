package edu.cit.tooltrack.repository;

import edu.cit.tooltrack.entity.ToolItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolItemRepository extends JpaRepository<ToolItems, Integer> {

    @Query(value = "SELECT tool_id FROM tool_items ORDER BY tool_id DESC LIMIT 1", nativeQuery = true)
    int findLatestToolItemId();

}
