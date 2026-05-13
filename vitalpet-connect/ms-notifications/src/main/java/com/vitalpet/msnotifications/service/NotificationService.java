package com.vitalpet.msnotifications.service;

import com.vitalpet.msnotifications.client.UserClient;
import com.vitalpet.msnotifications.dto.NotificationRequestDTO;
import com.vitalpet.msnotifications.dto.NotificationResponseDTO;
import com.vitalpet.msnotifications.exception.ResourceNotFoundException;
import com.vitalpet.msnotifications.model.Notification;
import com.vitalpet.msnotifications.model.NotificationType;
import com.vitalpet.msnotifications.repository.NotificationRepository;
import com.vitalpet.msnotifications.repository.NotificationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationTypeRepository notificationTypeRepository;

    @Autowired
    private UserClient userClient;

    public NotificationResponseDTO createNotification(NotificationRequestDTO request) {
        String email = userClient.getEmailById(request.getUserId());

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El usuario con ID " + request.getUserId() + " no tiene un email válido.");
        }

        NotificationType type = notificationTypeRepository.findByName(request.getType()).orElseThrow(() -> new ResourceNotFoundException("Tipo de notificación '" + request.getType() + "' no válido."));

        Notification notification = new Notification();

        notification.setUserId(request.getUserId());
        notification.setNotificationType(type);
        notification.setMessage(request.getMessage());
        notification.setReadFlag(false);
        notification.setSentAt(LocalDate.now());

        Notification savedNotification = notificationRepository.save(notification);
        return toDTO(savedNotification);
    }

    public List<NotificationResponseDTO> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserId(userId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<NotificationResponseDTO> getUnreadNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdAndReadFlagFalse(userId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public NotificationResponseDTO markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notificación con ID " + id + " no encontrada."));
        notification.setReadFlag(true);
        Notification updated = notificationRepository.save(notification);
        return toDTO(updated);
    }

    private NotificationResponseDTO toDTO(Notification notification) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setReadFlag(notification.getReadFlag());
        dto.setSentAt(notification.getSentAt());
        dto.setUserId(notification.getUserId());
        dto.setTypeId(notification.getNotificationType().getId());
        return dto;
    }
}
