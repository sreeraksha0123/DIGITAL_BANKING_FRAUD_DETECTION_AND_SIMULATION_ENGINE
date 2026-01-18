package com.example.fraud_detection.controller;

import com.example.fraud_detection.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@CrossOrigin
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public List<?> getNotifications() {
        return service.getAll();
    }

    @PostMapping("/mark-read")
    public void markAllRead() {
        service.markAllRead();
    }
}
