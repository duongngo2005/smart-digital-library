package com.ndd.digitallibrary.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String tokenType;

    @JsonIgnore
    private String refreshToken;
    private UserResponse userResponse;
}
