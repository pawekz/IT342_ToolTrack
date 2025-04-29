package edu.cit.tooltrack.repository;

import edu.cit.tooltrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    String deleteByEmail(String email);
}
