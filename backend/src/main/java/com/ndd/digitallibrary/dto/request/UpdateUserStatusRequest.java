package com.ndd.digitallibrary.dto.request;

import com.ndd.digitallibrary.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateUserStatusRequest {
    @NotNull(message = "Trạng thái không được để trống")
    private UserStatus userStatus;
}
