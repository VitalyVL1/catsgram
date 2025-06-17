package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.PostRepository;
import ru.yandex.practicum.catsgram.dal.UserRepository;
import ru.yandex.practicum.catsgram.dto.PostDto;
import ru.yandex.practicum.catsgram.dto.UpdatePostRequest;
import ru.yandex.practicum.catsgram.dto.UpdateUserRequest;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.mapper.PostMapper;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;

// Указываем, что класс PostService - является бином и его
// нужно добавить в контекст приложения
@Service
@RequiredArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public Collection<PostDto> findAll(int size, String sort, int from) {
        switch (SortOrder.from(sort)) {
            case ASCENDING -> {
                return postRepository.findAll().stream()
                        .sorted(Comparator.comparing(Post::getPostDate))
                        .skip(from)
                        .limit(size)
                        .map(PostMapper::mapToPostDto)
                        .toList();
            }
            case DESCENDING -> {
                return postRepository.findAll().stream()
                        .sorted(Comparator.comparing(Post::getPostDate).reversed())
                        .skip(from)
                        .limit(size)
                        .map(PostMapper::mapToPostDto)
                        .toList();
            }
            default -> throw new ConditionsNotMetException(
                    String.format("Неверный порядок сортировки: %s, выберете между asc, desc!", sort));
        }
    }

    public PostDto create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        Optional<User> user = userRepository.findById(post.getAuthorId());

        if (user.isEmpty()) {
            throw new ConditionsNotMetException("Автор с id = " + post.getAuthorId() + " не найден");
        }

        return PostMapper.mapToPostDto(postRepository.save(post));
    }

    public PostDto update(long postId, UpdatePostRequest request) {
        if (!request.hasDescription()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        Post updatedPost = postRepository.findById(postId)
                .map(post -> PostMapper.updatePostFields(post, request))
                .orElseThrow(() -> new NotFoundException("Пост с id = " + postId + " не найден"));

        updatedPost = postRepository.update(updatedPost);
        return PostMapper.mapToPostDto(updatedPost);
    }

    public PostDto findById(long postId) {
        return postRepository.findById(postId)
                .map(PostMapper::mapToPostDto)
                .orElseThrow(()-> new NotFoundException("Пост не найден с ID: " + postId));
    }

    public enum SortOrder {
        ASCENDING, DESCENDING;

        // Преобразует строку в элемент перечисления
        public static SortOrder from(String order) {
            return switch (order.toLowerCase()) {
                case "ascending", "asc" -> ASCENDING;
                case "descending", "desc" -> DESCENDING;
                default -> null;
            };
        }
    }
}