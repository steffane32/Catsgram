package ru.yandex.practicum.catsgram.model;

import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "email")
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Instant registrationDate;


}
