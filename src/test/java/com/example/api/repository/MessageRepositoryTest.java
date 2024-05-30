package com.example.api.repository;

import com.example.api.model.Message;
import com.example.api.utils.MessageHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class MessageRepositoryTest {

    @Mock
    private MessageRepository messageRepository;

    AutoCloseable openMocks;

    @BeforeEach
    void  setup(){
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception{
        openMocks.close();
    }


    @Test
    void shouldAllowRegisterMessage(){
        // Arrange
        var message = MessageHelper.createMessage();

        when(messageRepository.save(any(Message.class))).thenReturn(message);
        // Act
        var newMessage = messageRepository.save(message);

        // Assert
        assertThat(newMessage)
                .isNotNull()
                .isEqualTo(message);
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void shouldAllowDeleteMessage(){
        // Arrange
        var id = UUID.randomUUID();
        doNothing().when(messageRepository).deleteById(any(UUID.class));
        // Act
        messageRepository.deleteById((id));
        // Assert
        verify(messageRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void shouldAllowGetMessage(){
        // Arrange
        var id = UUID.randomUUID();
        var newMessage = MessageHelper.createMessage();
        newMessage.setId(id);

        when(messageRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(newMessage));

        // Act
        var message = messageRepository.findById(id);

        // Assert
        assertThat(message)
                .isPresent()
                .containsSame(newMessage);
        message.ifPresent(messageObj -> {
            assertThat(messageObj.getId()).isEqualTo(newMessage.getId());
            assertThat(messageObj.getContent()).isEqualTo(newMessage.getContent());
        });
        verify(messageRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void shouldAllowListMessages(){
        // Arrange
        var message1 = MessageHelper.createMessage();
        var message2 = MessageHelper.createMessage();
        var newMessageList = Arrays.asList(
                message1,
                message2
        );
        when(messageRepository.findAll()).thenReturn(newMessageList);

        // Act
        var messageList = messageRepository.findAll();

        // Assert
        assertThat(messageList)
                .hasSize(2)
                .containsExactlyInAnyOrder(message1, message2);
        verify(messageRepository, times(1)).findAll();
    }
}
