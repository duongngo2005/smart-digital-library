package com.ndd.digitallibrary.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "refresh_tokens")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RefreshToken extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = false, length = 500)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Builder.Default
    private boolean revoked = false;
}
