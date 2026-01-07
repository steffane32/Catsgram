package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.service.ImageService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/{postId}/images")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Image> addImagesToPost(
            @PathVariable long postId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        log.info("Добавление изображений к посту с ID: {}", postId);
        return imageService.saveImages(postId, files);
    }
}