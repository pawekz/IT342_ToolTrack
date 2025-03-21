package edu.cit.tooltrack.repository;

import edu.cit.tooltrack.entity.TransactionImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionImageRepository extends JpaRepository<TransactionImage, Integer> {
}
