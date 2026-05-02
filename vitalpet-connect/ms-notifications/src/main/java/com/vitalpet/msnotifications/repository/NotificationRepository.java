package com.vitalpet.msnotifications.repository;

import com.vitalpet.msnotifications.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>{
    List<Notification> findByUserId(Long userId);
    List<Notification> findByUserIdAndReadFlagFalse(Long userId);
}
