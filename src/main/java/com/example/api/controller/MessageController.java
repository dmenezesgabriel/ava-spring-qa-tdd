package com.example.api.controller;

import com.example.api.exception.MessageNotFoundException;
import com.example.api.model.Message;
import com.example.api.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Message> registerMessage(@RequestBody Message message) {
        var newMessage = messageService.registerMessage(message);
        return new ResponseEntity<>(newMessage, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getMessage(@PathVariable String id) {
        var uuid = UUID.fromString(id);
        try {
            var message = messageService.getMessage(uuid);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (MessageNotFoundException messageNotFoundException) {
            return new ResponseEntity<>("Invalid ID", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public  ResponseEntity<Message> updateMessage(@PathVariable String id, @RequestBody Message message) {
        var uuid = UUID.fromString(id);
        var updatedMessage = messageService.updateMessage(uuid, message);
        return new ResponseEntity<>(updatedMessage, HttpStatus.OK);
    }
}
