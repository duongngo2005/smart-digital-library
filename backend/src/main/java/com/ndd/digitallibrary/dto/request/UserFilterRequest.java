package com.ndd.digitallibrary.dto.request;

import com.ndd.digitallibrary.enums.Role;
import com.ndd.digitallibrary.enums.SubscriptionTier;
import com.ndd.digitallibrary.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserFilterRequest {
    private String keyword;
    private SubscriptionTier subscriptionTier;
    private UserStatus userStatus;
    private Role role;
}
