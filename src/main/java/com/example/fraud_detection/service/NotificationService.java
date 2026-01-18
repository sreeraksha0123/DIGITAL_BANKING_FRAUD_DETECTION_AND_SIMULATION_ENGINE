package com.example.fraud_detection.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    public static class Notification {
        public String message;
        public boolean read;
        public LocalDateTime time;

        public Notification(String message) {
            this.message = message;
            this.read = false;
            this.time = LocalDateTime.now();
        }
    }

    private final List<Notification> notifications = new ArrayList<>();

    public void add(String message) {
        notifications.add(0, new Notification(message));
    }

    public List<Notification> getAll() {
        return notifications;
    }

    public void markAllRead() {
        notifications.forEach(n -> n.read = true);
    }
}
