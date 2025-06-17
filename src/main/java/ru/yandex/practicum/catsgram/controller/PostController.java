package ru.yandex.practicum.catsgram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.dto.PostDto;
import ru.yandex.practicum.catsgram.dto.UpdatePostRequest;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Collection<PostDto> findAll(
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sort,
            @RequestParam(defaultValue = "0") int from) {

        if (!List.of("asc","desc").contains(sort.toLowerCase())) {
            throw new ParameterNotValidException("sort", "Параметр должен быть asc или desc");
        }
        if (size <= 0) {
            throw new ParameterNotValidException("size", "Размер должен быть больше нуля");
        }
        if (from < 0) {
            throw new ParameterNotValidException("from", "Параметр не может быть меньше нуля");
        }
        return postService.findAll(size, sort, from);
    }

    @GetMapping("/{postId}")
    public PostDto findById(@PathVariable Long postId) {
        return postService.findById(postId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping("/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto update(@RequestParam long postId, @RequestBody UpdatePostRequest request) {
        return postService.update(postId, request);
    }
}