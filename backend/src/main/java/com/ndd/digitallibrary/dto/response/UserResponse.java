package com.ndd.digitallibrary.dto.response;

import com.ndd.digitallibrary.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Setter @Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String avatarUrl;
    private String userStatus;
    private String subscriptionTier;
    private int downloadedThisMonth;
    private LocalDateTime subscriptionUntil;

    public static UserResponse fromEntity(User user){
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .avatarUrl(user.getAvatarUrl())
                .userStatus(user.getUserStatus().name())
                .subscriptionTier(user.getSubscriptionTier().name())
                .subscriptionUntil(user.getSubscriptionUntil())
                .downloadedThisMonth(user.getDownloadedThisMonth())
                .build();
    }
}
