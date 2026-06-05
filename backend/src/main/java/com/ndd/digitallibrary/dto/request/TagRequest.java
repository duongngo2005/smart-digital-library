package com.ndd.digitallibrary.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class TagRequest {
    @NotBlank(message = "Tên tag không được để trống")
    private String name;
}
