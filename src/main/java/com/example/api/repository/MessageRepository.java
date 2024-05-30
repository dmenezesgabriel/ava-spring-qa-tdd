package com.example.api.repository;

import com.example.api.model.Message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;


public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("SELECT m FROM Message m ORDER BY m.createdAt DESC")
    Page<Message> listMessages(Pageable pageable);
}
