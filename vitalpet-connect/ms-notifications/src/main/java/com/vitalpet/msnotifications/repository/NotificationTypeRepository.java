package com.vitalpet.msnotifications.repository;

import com.vitalpet.msnotifications.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {

}
