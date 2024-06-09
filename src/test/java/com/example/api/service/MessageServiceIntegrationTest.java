package com.example.api.service;

import com.example.api.exception.MessageNotFoundException;
import com.example.api.model.Message;
import com.example.api.repository.MessageRepository;
import com.example.api.utils.MessageHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;

@Nested
@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
class MessageServiceIntegrationTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageService messageService;

    @Nested
    class RegisterMessageTest {
        @Test
        void shouldAllowRegisterMessage() {
            // Arrange
            var message = MessageHelper.createMessage();

            // Act
            var result = messageService.registerMessage(message);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .isInstanceOf(Message.class);
            assertThat(result.getId()).isNotNull();
            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getLikeCount()).isNotNull();
        }
    }

    @Nested
    class GetMessageTest {
        @Test
        void shouldAllowGetMessage() {
            // Arrange
            var newMessage = MessageHelper.createMessage();
            var message = messageService.registerMessage(newMessage);
            var id = message.getId();

            // Act
            var result = messageService.getMessage(id);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .isInstanceOf(Message.class);
            assertThat(result.getId())
                    .isNotNull()
                    .isEqualTo(id);
            assertThat(result.getUsername())
                    .isNotNull()
                    .isEqualTo(message.getUsername());
            assertThat(result.getContent())
                    .isNotNull()
                    .isEqualTo(message.getContent());
            assertThat(result.getCreatedAt())
                    .isNotNull();
            assertThat(result.getLikeCount())
                    .isEqualTo(message.getLikeCount());
        }

        @Test
        void shouldThrowExceptionWhenGetIfMessageIdNotFound() {
            // Arrange
            var id = UUID.randomUUID();

            // Act && Assert
            assertThatThrownBy(() -> messageService.getMessage(id))
                    .isInstanceOf(MessageNotFoundException.class)
                    .hasMessage("Message not found");
        }
    }

    @Nested
    class UpdateMessageTest {
        @Test
        void shouldAllowEditMessage() {
            // Arrange
            var newMessage = MessageHelper.createMessage();
            var message = messageService.registerMessage(newMessage);
            var id = message.getId();
            newMessage.setContent("Hello, my friend!");

            // Act
            var result = messageService.updateMessage(id, newMessage);

            // Assert
            assertThat(result.getId())
                    .isNotNull()
                    .isEqualTo(newMessage.getId());
            assertThat(result.getUsername())
                    .isEqualTo(newMessage.getUsername());
            assertThat(result.getContent())
                    .isEqualTo(newMessage.getContent());
        }


        @Test
        void shouldThrowExceptionWhenUpdateIfMessageIdNotFound() {
            // Arrange
            var id = UUID.randomUUID();
            var updatedMessage = MessageHelper.createMessage();
            updatedMessage.setId(id);

            // Act & Assert
            assertThatThrownBy(() -> messageService.updateMessage(id, updatedMessage))
                    .isInstanceOf(MessageNotFoundException.class)
                    .hasMessage("Message not found");
        }

        @Test
        void shouldThrowExceptionWhenUpdateIfMessageIdIsNotEqual() {
            // Arrange
            var newMessage = MessageHelper.createMessage();
            var message = messageService.registerMessage(newMessage);
            var id = message.getId();
            newMessage.setId(UUID.randomUUID());
            newMessage.setContent("Hello, my friend!");

            // Act & Assert
            assertThatThrownBy(() -> messageService.updateMessage(id, newMessage))
                    .isInstanceOf(MessageNotFoundException.class)
                    .hasMessage("Updated message does not have the correct ID");
        }
    }

    @Nested
    class DeleteMessageTest {
        @Test
        void shouldAllowDeleteMessage() {
            // Arrange
            var newMessage = MessageHelper.createMessage();
            var message = messageService.registerMessage(newMessage);
            var id = message.getId();

            // Act
            var result = messageService.deleteMessage(id);

            // Arrange
            assertThat(result).isTrue();

        }

        @Test
        void shouldThrowExceptionWhenDeleteIfMessageIdNotFound() {
            // Arrange
            var id = UUID.randomUUID();

            // Act & Assert
            assertThatThrownBy(() -> messageService.deleteMessage(id))
                    .isInstanceOf(MessageNotFoundException.class)
                    .hasMessage("Message not found");

        }
    }

    @Nested
    class ListMessagesTest {
        @Test
        void shouldAllowListMessages() {
            // Arrange
            var newMessage = MessageHelper.createMessage();

            // Act
            messageService.registerMessage(newMessage);
            Page<Message> messageListResult = messageService.listMessages(Pageable.unpaged());

            // Assert
            assertThat(messageListResult.getContent())
                    .asList()
                    .isNotEmpty()
                    .allSatisfy(message -> {
                        assertThat(message).isInstanceOf(Message.class);
                    });
        }
    }
}
