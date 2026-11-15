package ru.demo.chat.room;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatMessageService {
    private static final int HISTORY_LIMIT = 100;

    private final ChatMessageRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatMessageService(ChatMessageRepository repository, SimpMessagingTemplate messagingTemplate) {
        this.repository = repository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public ChatMessageResponse saveAndBroadcast(ChatMessageRequest request) {
        String roomName = sanitizeRoom(request.roomName());
        String sender = sanitizeText(request.sender(), "sender", 50);
        String content = sanitizeText(request.content(), "content", 1000);

        ChatMessageEntity entity = repository.save(new ChatMessageEntity(roomName, sender, content));
        ChatMessageResponse response = ChatMessageResponse.from(entity);
        messagingTemplate.convertAndSend("/topic/rooms/" + roomName, response);
        return response;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getHistory(String roomName) {
        String validRoomName = sanitizeRoom(roomName);
        return repository.findByRoomNameOrderByCreatedAtAsc(validRoomName, PageRequest.of(0, HISTORY_LIMIT, Sort.by("createdAt").ascending()))
                .stream().map(ChatMessageResponse::from).toList();
    }

    private String sanitizeRoom(String value) {
        if (value == null || !value.matches("[a-zA-Z0-9_-]{3,50}")) {
            throw new IllegalArgumentException("Room name must be 3-50 chars and contain only letters, numbers, _ or -");
        }
        return value.trim();
    }

    private String sanitizeText(String value, String fieldName, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        String trimmed = value.trim();
        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " length must be <= " + maxLength);
        }
        return trimmed;
    }
}
