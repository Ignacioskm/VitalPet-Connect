package com.vitalpet.msnotifications.repository;

import com.vitalpet.msnotifications.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long>{

}
