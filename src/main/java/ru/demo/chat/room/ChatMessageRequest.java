package ru.demo.chat.room;

import java.time.Instant;

public record ChatMessageRequest(String roomName, String sender, String content) { }
public record ChatMessageResponse(Long id, String roomName, String sender, String content, Instant createdAt) {
    static ChatMessageResponse from(ChatMessageEntity entity) {
        return new ChatMessageResponse(entity.getId(), entity.getRoomName(), entity.getSender(), entity.getContent(), entity.getCreatedAt());
    }
}
