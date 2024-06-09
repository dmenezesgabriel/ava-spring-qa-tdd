package com.example.api.controller;


import com.example.api.exception.MessageNotFoundException;
import com.example.api.model.Message;
import com.example.api.service.MessageService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Message>> listMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageService.listMessages(pageable);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }


    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public  ResponseEntity<?> updateMessage(@PathVariable String id, @RequestBody Message message) {
        var uuid = UUID.fromString(id);
        try {
            var updatedMessage = messageService.updateMessage(uuid, message);
            return new ResponseEntity<>(updatedMessage, HttpStatus.ACCEPTED);
        } catch (MessageNotFoundException messageNotFoundException) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(messageNotFoundException.getMessage());
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable String id) {
        var uuid = UUID.fromString(id);
        try {
            messageService.deleteMessage(uuid);
            return new ResponseEntity<>("Message deleted", HttpStatus.OK);
        } catch (MessageNotFoundException messageNotFoundException) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(messageNotFoundException.getMessage());
        }
    }
}

