package ru.mironov.moviecollections.dto;

import ru.mironov.moviecollections.entity.*;
import ru.mironov.moviecollections.repository.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotEmpty(message = "Поле не должно быть пустым")
    private String username;
    @NotEmpty(message = "Поле не должно быть пустым")
    @Email
    private String email;
    @NotEmpty(message = "Поле не должно быть пустым")
    private String password;
    private String role;
}