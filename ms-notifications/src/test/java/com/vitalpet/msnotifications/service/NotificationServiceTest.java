package com.vitalpet.msnotifications.service;

import com.vitalpet.msnotifications.client.UserClient;
import com.vitalpet.msnotifications.dto.NotificationRequestDTO;
import com.vitalpet.msnotifications.dto.NotificationResponseDTO;
import com.vitalpet.msnotifications.exception.ResourceNotFoundException;
import com.vitalpet.msnotifications.model.Notification;
import com.vitalpet.msnotifications.model.NotificationType;
import com.vitalpet.msnotifications.repository.NotificationRepository;
import com.vitalpet.msnotifications.repository.NotificationTypeRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private NotificationTypeRepository notificationTypeRepository;
    @Mock private UserClient userClient;

    @InjectMocks private NotificationService notificationService;

    private Faker faker;
    private Notification mockNotification;
    private NotificationType mockType;
    private NotificationRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        faker = new Faker();

        mockType = new NotificationType(1L, "PAYMENT_CONFIRMED");

        mockNotification = new Notification();
        mockNotification.setId(1L);
        mockNotification.setMessage("Su pago ha sido procesado.");
        mockNotification.setReadFlag(false);
        mockNotification.setSentAt(LocalDate.now());
        mockNotification.setNotificationType(mockType);
        mockNotification.setUserId(10L);

        requestDTO = new NotificationRequestDTO(
                mockNotification.getUserId(),
                mockType.getName(),
                mockNotification.getMessage()
        );
    }


    @Test
    void createNotification_ShouldReturnDTO_WhenValidationsPass() {
        when(userClient.getEmailById(requestDTO.getUserId())).thenReturn("usuario@vitalpet.com");
        when(notificationTypeRepository.findByName(requestDTO.getType())).thenReturn(Optional.of(mockType));

        // Mismo que en payments , gracias IA
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArguments()[0]);

        NotificationResponseDTO result = notificationService.createNotification(requestDTO);

        assertNotNull(result);
        assertEquals(requestDTO.getMessage(), result.getMessage());
        assertFalse(result.getReadFlag());
        assertEquals(mockType.getId(), result.getTypeId());

        verify(userClient, times(1)).getEmailById(requestDTO.getUserId());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void createNotification_ShouldThrowResourceNotFound_WhenUserClientFails() {
        when(userClient.getEmailById(requestDTO.getUserId())).thenThrow(new RuntimeException("Connection Timeout"));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> notificationService.createNotification(requestDTO));
        assertTrue(exception.getMessage().contains("Error de comunicación: El usuario con ID " + requestDTO.getUserId()));

        verify(notificationTypeRepository, never()).findByName(anyString());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void createNotification_ShouldThrowIllegalArgumentException_WhenEmailIsBlank() {
        when(userClient.getEmailById(requestDTO.getUserId())).thenReturn("   ");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> notificationService.createNotification(requestDTO));
        assertEquals("El usuario con ID " + requestDTO.getUserId() + " no tiene un email válido.", exception.getMessage());

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void createNotification_ShouldThrowResourceNotFound_WhenTypeIsInvalid() {
        when(userClient.getEmailById(requestDTO.getUserId())).thenReturn("usuario@vitalpet.com");
        when(notificationTypeRepository.findByName(requestDTO.getType())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> notificationService.createNotification(requestDTO));
        assertEquals("Tipo de notificación '" + requestDTO.getType() + "' no válido.", exception.getMessage());

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void markAsRead_ShouldChangeReadFlagToTrue() {
        assertFalse(mockNotification.getReadFlag());

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(mockNotification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArguments()[0]);

        NotificationResponseDTO result = notificationService.markAsRead(1L);

        assertNotNull(result);
        assertTrue(result.getReadFlag());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void markAsRead_ShouldThrowException_WhenNotFound() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.markAsRead(99L));
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void getNotificationsByUser_ShouldReturnList() {
        Long userId = 10L;
        when(notificationRepository.findByUserId(userId)).thenReturn(List.of(mockNotification));

        List<NotificationResponseDTO> result = notificationService.getNotificationsByUser(userId);

        assertEquals(1, result.size());
        verify(notificationRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getUnreadNotificationsByUser_ShouldReturnUnreadList() {
        Long userId = 10L;
        when(notificationRepository.findByUserIdAndReadFlagFalse(userId)).thenReturn(List.of(mockNotification));

        List<NotificationResponseDTO> result = notificationService.getUnreadNotificationsByUser(userId);

        assertEquals(1, result.size());
        assertFalse(result.get(0).getReadFlag());
        verify(notificationRepository, times(1)).findByUserIdAndReadFlagFalse(userId);
    }
}