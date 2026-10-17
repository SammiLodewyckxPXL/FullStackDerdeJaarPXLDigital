package be.pxl.notificationservice.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @PostMapping
    public String sendNotification(@Valid @RequestBody String notification) {
        return notification;
    }
}