package com.example.ecommerce_fashionformen.dto.notification;

import com.example.ecommerce_fashionformen.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String title;
    private String body;
    private Long referenceId;
    private boolean isRead;
    private LocalDateTime createdAt;
}
