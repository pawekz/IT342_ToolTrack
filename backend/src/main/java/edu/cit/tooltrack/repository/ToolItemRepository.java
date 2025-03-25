package edu.cit.tooltrack.repository;

import edu.cit.tooltrack.entity.ToolItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToolItemRepository extends JpaRepository<ToolItems, Integer> {
}
