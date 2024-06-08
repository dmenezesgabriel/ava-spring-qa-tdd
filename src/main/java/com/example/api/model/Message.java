package com.example.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class Message {

    @Id
    private UUID id;

    @Column(nullable = false)
    @NotEmpty(message = "User may not be empty")
    private String username;

    @Column(nullable = false)
    @NotEmpty(message = "Content may not be null")
    private String content;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSS")
    private LocalDateTime createdAt;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSS")
    private LocalDateTime updatedAt;

    @Builder.Default
    private int likeCount = 0;
}
