package com.example.api.service;

import com.example.api.exception.MessageNotFoundException;
import com.example.api.model.Message;
import com.example.api.repository.MessageRepository;
import com.example.api.utils.MessageHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    private MessageService messageService;

    @Mock
    private MessageRepository messageRepository;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        messageService = new MessageServiceImpl(messageRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Test
    void shouldAllowRegisterMessage() {
        // Arrange
        var newMessage = MessageHelper.createMessage();
        when(messageRepository.save(any(Message.class)))
                .thenAnswer(index -> index.getArgument(0));

        // Act
        var message = messageService.registerMessage((newMessage));

        // Assert
        assertThat(message)
                .isInstanceOf(Message.class)
                .isNotNull();
        assertThat(message.getUsername()).isEqualTo(newMessage.getUsername());
        assertThat(message.getContent()).isEqualTo(newMessage.getContent());
        assertThat(message.getId()).isNotNull();
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void shouldAllowGetMessage() {
        // Arrange
        var id = UUID.randomUUID();
        var newMessage = MessageHelper.createMessage();
        newMessage.setId(id);

        when(messageRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(newMessage));

        // Act
        var message = messageService.getMessage(id);

        // Assert
        assertThat(message).isEqualTo(newMessage);
        verify(messageRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void shouldThrowExceptionWhenGetIfIfMessageIdNotFound() {
        // Arrange
        var id = UUID.randomUUID();
        when(messageRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.getMessage(id))
                .isInstanceOf(MessageNotFoundException.class)
                .hasMessage("Message not found");
        verify(messageRepository, times(1)).findById(id);
    }


    @Test
    void shouldAllowEditMessage() {
        // Arrange
        var id = UUID.randomUUID();
        var oldMessage = MessageHelper.createMessage();
        oldMessage.setId(id);

        var newMessage = new Message();
        newMessage.setId(oldMessage.getId());
        newMessage.setUsername((oldMessage.getUsername()));
        newMessage.setContent("Hello, Again!");

        when(messageRepository.findById(id))
                .thenReturn(Optional.of(oldMessage));
        when(messageRepository.save(any(Message.class)))
                .thenAnswer(item -> item.getArgument(0));

        // Act
        var gottenMessage = messageService.updateMessage(id, newMessage);

        // Assert
        assertThat(gottenMessage).isInstanceOf(Message.class).isNotNull();
        assertThat(gottenMessage.getId()).isEqualTo(newMessage.getId());
        assertThat(gottenMessage.getUsername()).isEqualTo(newMessage.getUsername());
        assertThat(gottenMessage.getContent()).isEqualTo(newMessage.getContent());
        verify(messageRepository, times(1)).findById(any(UUID.class));
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdateIfIfMessageIdNotFound() {
        // Arrange
        var id = UUID.randomUUID();
        var message = MessageHelper.createMessage();
        message.setId(id);
        when(messageRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> messageService.updateMessage(id, message))
                .isInstanceOf((MessageNotFoundException.class))
                .hasMessage("Message not found");
        verify(messageRepository, times(1)).findById(any(UUID.class));
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdateMessageIdIsNotEqual() {
        // Arrange
        var id = UUID.randomUUID();
        var oldMessage = MessageHelper.createMessage();
        oldMessage.setId(id);

        var newMessage = new Message();
        newMessage.setId(UUID.randomUUID());
        newMessage.setUsername((oldMessage.getUsername()));
        newMessage.setContent("Hello, Again!");
        when(messageRepository.findById(id)).thenReturn((Optional.of(oldMessage)));

        // Act & assert
        assertThatThrownBy(() -> messageService.updateMessage(id, newMessage))
                .isInstanceOf(MessageNotFoundException.class)
                .hasMessage("Updated message does not have the correct ID");
        verify(messageRepository, times(1)).findById(any(UUID.class));
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void shouldAllowDeleteMessage() {
        // Arrange
        var id = UUID.randomUUID();
        var message = MessageHelper.createMessage();
        message.setId(id);
        when(messageRepository.findById(id)).thenReturn(Optional.of(message));
        doNothing().when(messageRepository).deleteById(id);

        // Act
        var messageRemoved = messageService.deleteMessage(id);

        // Assert
        assertThat(messageRemoved).isTrue();
        verify(messageRepository, times(1)).findById(any(UUID.class));
        verify(messageRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void shouldThrowExceptionWhenDeleteIfIfMessageIdNotFound() {
        // Arrange
        var id = UUID.randomUUID();
        when(messageRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> messageService.deleteMessage(id))
                .isInstanceOf(MessageNotFoundException.class)
                .hasMessage("Message not found");
        verify(messageRepository, times(1)).findById(any(UUID.class));
        verify(messageRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void shouldAllowListMessages() {
        // Arrange
        Page<Message> messageList = new PageImpl<>(Arrays.asList(
                MessageHelper.createMessage(),
                MessageHelper.createMessage()
        ));
        when(messageRepository.listMessages(any(Pageable.class)))
                .thenReturn(messageList);

        // Act
        var result = messageService.listMessages(Pageable.unpaged());

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.getContent())
                .asList()
                .allSatisfy(message -> {
                    assertThat(message)
                            .isNotNull()
                            .isInstanceOf(Message.class);
                });
        verify(messageRepository, times(1)).listMessages(any(Pageable.class));
    }

}
