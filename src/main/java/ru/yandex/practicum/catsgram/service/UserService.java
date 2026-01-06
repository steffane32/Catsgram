package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, User> usersByEmail = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User create(User user) {
        // Проверка: email должен быть указан
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        // Проверка уникальности email
        if (usersByEmail.containsKey(user.getEmail())) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        // Устанавливаем ID и дату регистрации
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());

        // Сохраняем пользователя
        users.put(user.getId(), user);
        usersByEmail.put(user.getEmail(), user);

        return user;
    }

    public User update(User newUser) {
        // Проверка: ID должен быть указан
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        // Проверка существования пользователя
        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        User oldUser = users.get(newUser.getId());

        // Проверка уникальности нового email (если он указан и изменился)
        if (newUser.getEmail() != null && !newUser.getEmail().equals(oldUser.getEmail())) {
            if (usersByEmail.containsKey(newUser.getEmail())) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }

            // Обновляем email в карте
            usersByEmail.remove(oldUser.getEmail());
            oldUser.setEmail(newUser.getEmail());
            usersByEmail.put(newUser.getEmail(), oldUser);
        }

        // Обновляем username (только если не null)
        if (newUser.getUsername() != null) {
            oldUser.setUsername(newUser.getUsername());
        }

        // Обновляем password (только если не null)
        if (newUser.getPassword() != null) {
            oldUser.setPassword(newUser.getPassword());
        }

        return oldUser;
    }

    // НОВЫЙ МЕТОД: поиск пользователя по ID (для проверки автора поста)
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}