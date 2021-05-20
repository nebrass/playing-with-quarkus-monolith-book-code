package com.targa.labs.quarkushop.security.barbarus;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class BarbarusLoginDto implements Serializable {

    @NotNull
    private String username;

    @NotNull
    private String password;

    @Size(min = 36, max = 36)
    private String viewId;
}
