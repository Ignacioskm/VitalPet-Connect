package com.vitalpet.msnotifications.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    private Boolean readFlag;

    private LocalDate sentAt;

    @CreationTimestamp
    @Column(name = "create_at",updatable = false)
    private LocalDateTime createdAt;

    // FK a Notification Type
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name="type_id", nullable = false)
    private NotificationType notificationType;

    // Referencia Lógica ms user
    @Column(name = "user_id", nullable = false)
    private Long userId;
}
