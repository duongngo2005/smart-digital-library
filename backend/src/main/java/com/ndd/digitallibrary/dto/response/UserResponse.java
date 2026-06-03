package com.ndd.digitallibrary.dto.response;

import lombok.*;

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
}
