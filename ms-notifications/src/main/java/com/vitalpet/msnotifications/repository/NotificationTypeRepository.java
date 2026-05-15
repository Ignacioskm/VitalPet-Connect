package com.vitalpet.msnotifications.repository;

import com.vitalpet.msnotifications.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {
    Optional<NotificationType> findByName(String name);
}
