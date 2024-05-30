package com.example.api.service;

import com.example.api.exception.MessageNotFoundException;
import com.example.api.model.Message;
import com.example.api.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    public Message registerMessage(Message message) {
        message.setId(UUID.randomUUID());
        return messageRepository.save(message);
    }

    @Override
    public Message getMessage(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));
    }

    @Override
    public Message updateMessage(UUID id, Message updatedMessage) {
        var message = getMessage(id);
        if (!message.getId().equals(updatedMessage.getId())){
            throw  new MessageNotFoundException("Updated message does not have the correct ID");
        }
        message.setContent(updatedMessage.getContent());
        return messageRepository.save(message);
    }

    @Override
    public boolean deleteMessage(UUID id) {
        var message = getMessage(id);
        messageRepository.deleteById(id);
        return true;
    }

    @Override
    public Page<Message> listMessages(Pageable pageable) {
        return messageRepository.listMessages(pageable);
    }
}
