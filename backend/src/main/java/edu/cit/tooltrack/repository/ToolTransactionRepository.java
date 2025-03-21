package edu.cit.tooltrack.repository;

import edu.cit.tooltrack.entity.ToolTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToolTransactionRepository extends JpaRepository<ToolTransaction, Integer> {
}
