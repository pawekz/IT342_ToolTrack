package edu.cit.tooltrack.repository;

import edu.cit.tooltrack.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepostitory extends JpaRepository<Notifications, Integer> {
}
