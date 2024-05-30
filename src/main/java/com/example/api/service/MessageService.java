package com.example.api.service;

import com.example.api.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MessageService {

    Message registerMessage(Message message);

    Message getMessage(UUID id);

    Message updateMessage(UUID is, Message updatedMessage);

    boolean deleteMessage(UUID id);

    Page<Message> listMessages(Pageable pageable);
}
