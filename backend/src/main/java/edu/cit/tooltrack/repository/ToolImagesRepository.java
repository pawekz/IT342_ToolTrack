package edu.cit.tooltrack.repository;

import edu.cit.tooltrack.entity.ToolImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolImagesRepository extends JpaRepository<ToolImages, Integer> {
}
