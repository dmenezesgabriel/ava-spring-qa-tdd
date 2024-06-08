package com.example.api.utils;

import com.example.api.model.Message;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class MessageHelper {
    public static Message createMessage() {
        return Message.builder()
                .username("John")
                .content("Hello, World!")
                .build();
    }

    public static Message createFullMessage() {
        var timestamp = LocalDateTime.now();
        return Message.builder()
                .id(UUID.randomUUID())
                .username("John")
                .content("Hello, World!")
                .createdAt(timestamp)
                .updatedAt(timestamp)
                .build();
    }


}
