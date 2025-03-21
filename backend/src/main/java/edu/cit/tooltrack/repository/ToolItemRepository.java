package edu.cit.tooltrack.repository;

import edu.cit.tooltrack.entity.ToolItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToolItemRepository extends JpaRepository<ToolItem, Integer> {
}
