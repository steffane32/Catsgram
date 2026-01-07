package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.SortOrder;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Collection<Post> findAll(
            @RequestParam(defaultValue = "desc") String sort,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {
        // Преобразуем строку сортировки в SortOrder
        SortOrder sortOrder = SortOrder.from(sort);

        // Если sortOrder == null (неверное значение), используем DESCENDING
        if (sortOrder == null) {
            sortOrder = SortOrder.DESCENDING;
        }

        // Если size не указан, но указан from, используем все оставшиеся
        // Если ни size, ни from не указаны - используем значения по умолчанию
        if (size == null && from == null) {
            size = 10; // По умолчанию 10 постов
        }

        return postService.findAll(from, size, sortOrder);
    }

    @GetMapping("/{id}")
    public Post findPostById(@PathVariable("id") Long id) {
        return postService.findPostById(id);
    }

    @PostMapping
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }
}