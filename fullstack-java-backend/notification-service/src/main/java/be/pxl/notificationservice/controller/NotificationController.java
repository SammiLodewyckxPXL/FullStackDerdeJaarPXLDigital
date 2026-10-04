package be.pxl.notificationservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @PostMapping
    public String sendNotification(@RequestBody String notification) {
        return notification;
    }
}