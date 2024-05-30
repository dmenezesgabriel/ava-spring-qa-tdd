package com.example.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    private UUID id;

    @Column(nullable = false)
    @NotEmpty(message = "User may not be empty")
    private String username;

    @Column(nullable = false)
    @NotEmpty(message = "Content may not be null")
    private String content;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private int likeCount = 0;
}
