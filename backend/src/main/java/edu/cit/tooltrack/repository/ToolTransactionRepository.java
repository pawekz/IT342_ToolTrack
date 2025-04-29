package edu.cit.tooltrack.repository;

import edu.cit.tooltrack.entity.ToolTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.tools.Tool;
import java.util.List;


@Repository
public interface ToolTransactionRepository extends JpaRepository<ToolTransaction, Integer> {


}
