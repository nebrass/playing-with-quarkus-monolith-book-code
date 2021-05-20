package com.targa.labs.quarkushop.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BarbarusLoginDto {

    @NotNull
    private String username;

    @NotNull
    private String password;

    @Size(min = 36, max = 36)
    private String viewId;

    @NotNull
    private String loginUrl;
}
