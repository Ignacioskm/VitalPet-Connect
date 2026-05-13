package com.vitalpet.mspayments.client;

import com.vitalpet.mspayments.dto.NotificationRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-notifications")
public interface NotificationClient {

    @PostMapping("/api/notifications")
    void sendNotification(@RequestBody NotificationRequestDTO notificationRequestDTO);
}
