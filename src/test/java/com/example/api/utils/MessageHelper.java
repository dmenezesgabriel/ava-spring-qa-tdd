package com.example.api.utils;

import com.example.api.model.Message;

import java.util.UUID;

public abstract class MessageHelper {
    static public Message createMessage(){
        return Message.builder()
                .id(UUID.randomUUID())
                .username("John")
                .content("Hello, World!")
                .build();
    }
}
