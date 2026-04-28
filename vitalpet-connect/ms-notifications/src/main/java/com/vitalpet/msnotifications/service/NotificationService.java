package com.vitalpet.msnotifications.service;

import com.vitalpet.msnotifications.client.UserClient;
import com.vitalpet.msnotifications.dto.NotificationResponseDTO;
import com.vitalpet.msnotifications.model.Notification;
import com.vitalpet.msnotifications.repository.NotificationRepository;
import com.vitalpet.msnotifications.repository.NotificationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationTypeRepository notificationTypeRepository;

    // OpenFeign
    private UserClient userClient;

    private NotificationResponseDTO toDTO(Notification notification){
        NotificationResponseDTO dto = new NotificationResponseDTO();

        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setReadFlag(notification.getReadFlag());
        dto.setSentAt(notification.getSentAt());
        dto.setUserId(notification.getUserId());
        dto.setTypeId(notification.getNotificationType().getId());

    }
}
