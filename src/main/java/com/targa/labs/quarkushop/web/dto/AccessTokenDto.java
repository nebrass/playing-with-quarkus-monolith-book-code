package com.targa.labs.quarkushop.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenDto {
    @NotNull
    private String accessToken;
    @NotNull
    private String refreshToken;
}
