package com.example.api.controller;

import com.example.api.exception.MessageNotFoundException;
import com.example.api.model.Message;
import com.example.api.service.MessageService;
import com.example.api.utils.MessageHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class MessageControllerTest {

    private MockMvc mockMVC;

    @Mock
    private MessageService messageService;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        MessageController messageController = new MessageController(messageService);
        mockMVC = MockMvcBuilders.standaloneSetup(messageController)
                .addFilter((request, response, chain) -> {
                    response.setCharacterEncoding(("UTF-8"));
                    chain.doFilter(request, response);
                })
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    public static String asJsonString(final Object object) {
        try {
            return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    class RegisterMessageTest {
        @Test
        void shouldAllowRegisterMessage() throws Exception {
            // Arrange
            var message = MessageHelper.createMessage();
            when(messageService.registerMessage(any(Message.class)))
                    .thenAnswer(index -> index.getArgument(0));

            // Act & Assert
            mockMVC.perform(post("/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(message))
            ).andExpect(status().isCreated());
            verify(messageService, times(1)).registerMessage(any(Message.class));
        }

        @Test
        void shouldThrowExceptionWhenRegisterIfMessagePayloadIsTypeXML() throws Exception {
            // Arrange
            String xmlPayload = """
            <message><username>Name</username><content>Hello!</content></message>
            """;

            // Act & Assert
            mockMVC.perform(post("/messages")
                            .contentType(MediaType.APPLICATION_XML)
                            .content(xmlPayload))
                    .andExpect(status().isUnsupportedMediaType());
            verify(messageService, never())
                    .registerMessage(any(Message.class));
        }
    }

    @Nested
    class GetMessageTest {
        @Test
        void shouldAllowGetMessage() throws Exception {
            // Arrange
            var id = UUID.randomUUID();
            var newMessage = MessageHelper.createMessage();
            when(messageService.getMessage(any(UUID.class)))
                    .thenReturn(newMessage);

            // Act & Assert
            mockMVC.perform(get("/messages/{id}", id))
                    .andExpect(status().isOk());
            verify(messageService, times(1)).getMessage(any(UUID.class));
        }
        @Test
        void shouldThrowExceptionWhenGetIfMessageIdNotFound() throws Exception {
            // Arrange
            var id = UUID.randomUUID();
            when(messageService.getMessage(id))
                    .thenThrow(MessageNotFoundException.class);
            // Act
            mockMVC.perform(get("/messages/{id}", id))
                    .andExpect(status().isBadRequest());
            verify(messageService, times(1)).getMessage(id);

        }
    }

    @Nested
    class UpdateMessageTest {
        @Test
        void shouldAllowUpdateMessage() throws Exception {
            // Arrange
            var id = UUID.randomUUID();
            var newMessage = MessageHelper.createMessage();
            newMessage.setId(id);

            when(messageService.updateMessage(any(UUID.class), any(Message.class)))
                    .thenAnswer(index -> index.getArgument(1));

            // Act & Assert
            mockMVC.perform(put("/messages/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(newMessage))
                    ).andExpect(status().isOk());

            verify(messageService, times(1))
                    .updateMessage(id, newMessage);
        }

        @Test
        void shouldThrowExceptionWhenUpdateIfMessageIdNotFound() throws Exception {
            // Arrange
            var id = UUID.randomUUID();
            var newMessage = MessageHelper.createMessage();
            newMessage.setId(id);
            var exceptionContent = "Message not found";
            when(messageService.updateMessage(id, newMessage))
                    .thenThrow(new MessageNotFoundException(exceptionContent));

            // Act & Assert
            mockMVC.perform(put("/messages/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(newMessage)))
//                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(exceptionContent));

            verify(messageService, times(1))
                    .updateMessage(any(UUID.class), any(Message.class));
        }

        @Test
        void shouldThrowExceptionWhenUpdateIfMessageIdIsNotEqual() throws Exception {
            // Arrange
            var id = UUID.randomUUID();
            var newMessage = MessageHelper.createMessage();
            newMessage.setId(id);
            var differentId = UUID.randomUUID();
            var exceptionContent = "Incorrect ID for updated message";
            when(messageService.updateMessage(differentId, newMessage))
                    .thenThrow(new MessageNotFoundException(exceptionContent));

            // Act & Assert
            mockMVC.perform(put("/messages/{id}", differentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(newMessage)))
//                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(exceptionContent));

            verify(messageService, times(1))
                    .updateMessage(any(UUID.class), any(Message.class));
        }

        @Test
        void shouldThrowExceptionWhenUpdateIfMessagePayloadIsTypeXML() throws Exception {
            // Arrange
            var id = UUID.randomUUID();
            String xmlPayload = "<message><id>" + id + "</id><username>Name</username><content>Hello!</content></message> ";

            // Act & Assert
            mockMVC.perform(put("/messages/{id}", id)
                            .contentType(MediaType.APPLICATION_XML)
                            .content(xmlPayload))
                    .andExpect(status().isUnsupportedMediaType());
            verify(messageService, never())
                    .updateMessage(any(UUID.class),any(Message.class));
        }

    }

    @Nested
    class DeleteMessageTest {
        @Test
        void shouldAllowDeleteMessage() throws Exception {
            // Arrange
            var id = UUID.randomUUID();
            when(messageService.deleteMessage(id)).thenReturn(true);

            // Act & Assert
            mockMVC.perform(delete("/messages/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Message deleted"));
            verify(messageService, times(1)).deleteMessage(id);
        }

        @Test
        void shouldThrowExceptionWhenDeleteIfMessageIdNotFound() throws Exception {
            // Arrange
            var id = UUID.randomUUID();
            var exceptionContent = "Message not found";
            when(messageService.deleteMessage(id))
                    .thenThrow(new MessageNotFoundException(exceptionContent));

            mockMVC.perform(delete("/messages/{id}", id))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(exceptionContent));

            verify(messageService, times(1)).deleteMessage(id);
        }
    }

    @Nested
    class ListMessagesTest {
        @Test
        void shouldAllowListMessages() throws Exception {
            // Arrange
            var newMessage = MessageHelper.createMessage();
            // https://github.com/spring-projects/spring-data-commons/issues/2987
            Pageable pageable = PageRequest.of(0, 10);
            var messageList = Collections.singletonList(newMessage);
            Page<Message> page = new PageImpl<>(messageList, pageable, messageList.size());

            when(messageService.listMessages(any(Pageable.class)))
                    .thenReturn(page);

            // Act & Assert
            mockMVC.perform(get("/messages")
                    .contentType(MediaType.APPLICATION_JSON))
                    // .andDo(print())
                    .andExpect(status().isOk());
            verify(messageService, times(1))
                    .listMessages(any(Pageable.class));
        }
    }
}
