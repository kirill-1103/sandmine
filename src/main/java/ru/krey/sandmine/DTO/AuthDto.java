package ru.krey.sandmine.DTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDto {
    @NonNull private String login;

    @NonNull private String password;
}
