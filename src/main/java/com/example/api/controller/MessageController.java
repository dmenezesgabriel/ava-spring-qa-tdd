package com.example.api.controller;

import com.example.api.model.Message;
import com.example.api.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping()
    public ResponseEntity<Message> registerMessage(Message message) {
        var newMessage = messageService.registerMessage(message);
        return new ResponseEntity<>(newMessage, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Message> getMessage(@PathVariable String id) {
        var uuid = UUID.fromString(id);
        var message = messageService.getMessage(uuid);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
