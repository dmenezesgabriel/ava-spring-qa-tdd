package com.example.api.repository;

import com.example.api.model.Message;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class MessageRepositoryIntegrationTest {

    @Autowired
    private MessageRepository messageRepository;

    private Message createMessage(){
        return Message.builder()
                .id(UUID.randomUUID())
                .username("John")
                .content("Hello, World!")
                .build();

    }

    @Test
    void shouldAllowCreateTable(){
        var rowCount = messageRepository.count();
        assertThat(rowCount).isNotNegative();
    }

    @Test
    void shouldAllowRegisterMessage(){
        // Arrange
        var id = UUID.randomUUID();
        var newMessage = createMessage();
        newMessage.setId(id);

        // Act
        var message = messageRepository.save(newMessage);

        // Assert
        assertThat(message)
                .isInstanceOf(Message.class)
                .isNotNull();
        assertThat(message.getId()).isEqualTo(id);
        assertThat(message.getContent()).isEqualTo(newMessage.getContent());
        assertThat(message.getUsername()).isEqualTo(newMessage.getUsername());
    }

    @Test
    void shouldAllowDeleteMessage() {
        // Arrange
        var id = UUID.randomUUID();
        var newMessage = createMessage();
        newMessage.setId(id);
        messageRepository.save(newMessage);

        // Act
        messageRepository.deleteById(id);
        var message = messageRepository.findById(id);

        // Assert
        assertThat(message).isEmpty();
    }

    @Test
    void shouldAllowGetMessage(){
        // Arrange
        var id = UUID.randomUUID();
        var newMessage = createMessage();
        newMessage.setId(id);
        messageRepository.save(newMessage);

        // Act
        var message = messageRepository.findById(id);

        // Assert
        assertThat(message).isPresent();
        message.ifPresent(messageObj -> {
            assertThat(messageObj.getId()).isEqualTo(id);
            assertThat(messageObj.getContent()).isEqualTo(newMessage.getContent());
        });
    }

    @Test
    void shouldAllowListMessages(){
        // Arrange
        var message1 = createMessage();
        var message2 = createMessage();
        messageRepository.save(message1);
        messageRepository.save(message2);

        // Act
        var messagesList = messageRepository.findAll();

        // Assert
        assertThat(messagesList).hasSizeGreaterThan(0);

    }
}
