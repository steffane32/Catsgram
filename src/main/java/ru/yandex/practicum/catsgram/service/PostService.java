package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.SortOrder;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;

@Service
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();
    private final UserService userService;

    public PostService(UserService userService) {
        this.userService = userService;
    }

    // Обновленный метод с пагинацией и сортировкой
    public Collection<Post> findAll(Integer from, Integer size, SortOrder sortOrder) {
        // Получаем все посты
        List<Post> allPosts = new ArrayList<>(posts.values());

        // Если sortOrder не передан, используем DESCENDING (самые свежие сверху)
        SortOrder order = (sortOrder != null) ? sortOrder : SortOrder.DESCENDING;

        // Сортируем по дате создания
        allPosts.sort((post1, post2) -> {
            if (order == SortOrder.ASCENDING) {
                return post1.getPostDate().compareTo(post2.getPostDate());
            } else {
                // DESCENDING
                return post2.getPostDate().compareTo(post1.getPostDate());
            }
        });

        // Применяем пагинацию: from
        if (from != null && from > 0) {
            if (from >= allPosts.size()) {
                return Collections.emptyList(); // Если from больше размера списка
            }
            allPosts = allPosts.subList(from, allPosts.size());
        }

        // Применяем пагинацию: size
        if (size != null && size > 0) {
            int toIndex = Math.min(size, allPosts.size());
            allPosts = allPosts.subList(0, toIndex);
        }

        return allPosts;
    }

    // Метод для обратной совместимости (если нужен)
    public Collection<Post> findAll() {
        return findAll(0, 10, SortOrder.DESCENDING);
    }

    public Post create(Post post) {
        // Проверка описания
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        // Проверка автора
        Long authorId = post.getAuthorId();
        if (authorId == null) {
            throw new ConditionsNotMetException("Идентификатор автора должен быть указан");
        }

        // Проверяем существование пользователя
        Optional<User> authorOptional = userService.findUserById(authorId);
        if (authorOptional.isEmpty()) {
            throw new ConditionsNotMetException("Автор с id = " + authorId + " не найден");
        }

        // Генерация ID и даты
        post.setId(getNextId());
        post.setPostDate(Instant.now());

        // Сохранение
        posts.put(post.getId(), post);

        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    public Post findPostById(Long id) {
        Post post = posts.get(id);
        if (post == null) {
            throw new NotFoundException("Пост с id = " + id + " не найден");
        }
        return post;
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}