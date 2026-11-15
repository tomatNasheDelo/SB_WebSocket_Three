package ru.demo.chat.room;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final ChatRoomRepository chatRoomRepository;

    public RoomController(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    @GetMapping
    public List<RoomResponse> allRooms() {
        return chatRoomRepository.findAll().stream()
                .map(room -> new RoomResponse(room.getName(), room.getOwnerUsername()))
                .toList();
    }

    @PostMapping
    public ResponseEntity<RoomResponse> create(@Valid @RequestBody RoomCreateRequest request) {
        String username = currentUsername();
        String roomName = sanitize(request.name());
        if (chatRoomRepository.existsByName(roomName)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room already exists");
        }
        ChatRoomEntity saved = chatRoomRepository.save(new ChatRoomEntity(roomName, username));
        return ResponseEntity.status(HttpStatus.CREATED).body(new RoomResponse(saved.getName(), saved.getOwnerUsername()));
    }

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return auth.getName();
    }

    private String sanitize(String value) {
        String trimmed = value == null ? "" : value.trim();
        if (!trimmed.matches("[a-zA-Z0-9_-]{3,50}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room name must be 3-50 chars and contain only letters, numbers, '_' or '-'");
        }
        return trimmed;
    }

    public record RoomCreateRequest(String name) { }
    public record RoomResponse(String name, String ownerUsername) { }
}
