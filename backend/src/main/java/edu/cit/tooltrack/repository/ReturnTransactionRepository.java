package edu.cit.tooltrack.repository;

import edu.cit.tooltrack.entity.ReturnTransactionImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnTransactionRepository extends JpaRepository<ReturnTransactionImage, Integer> {
}
