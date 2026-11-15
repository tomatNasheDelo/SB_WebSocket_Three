package ru.demo.chat.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatUserRepository extends JpaRepository<ChatUserEntity, Long> {
    Optional<ChatUserEntity> findByUsername(String username);
}
