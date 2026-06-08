package com.ndd.digitallibrary.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {

    @NotBlank(message = "Không được để trống mật khẩu hiện tại")
    private String currentPassword;

    @NotBlank(message = "Không được để trống mật khẩu mới")
    private String newPassword;
    @NotBlank(message = "Không được để trống xác nhận mật khẩu mới")
    private String confirmPassword;
}
