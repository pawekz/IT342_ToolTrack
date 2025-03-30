package edu.cit.tooltrack.repository;

import edu.cit.tooltrack.entity.ToolItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolItemRepository extends JpaRepository<ToolItems, Integer> {
}
