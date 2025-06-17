package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.ImageRepository;
import ru.yandex.practicum.catsgram.dal.PostRepository;
import ru.yandex.practicum.catsgram.dal.UserRepository;
import ru.yandex.practicum.catsgram.dto.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.PostDto;
import ru.yandex.practicum.catsgram.dto.UpdatePostRequest;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.mapper.PostMapper;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

// Указываем, что класс PostService - является бином и его
// нужно добавить в контекст приложения
@Service
@RequiredArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;

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

    public PostDto create(NewPostRequest newPostRequest) {
        if (newPostRequest.getDescription() == null || newPostRequest.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        User author = userRepository.findById(newPostRequest.getAuthorId())
                .orElseThrow(() ->
                        new ConditionsNotMetException("Автор с id = " + newPostRequest.getAuthorId() + " не найден"));

        Post post = PostMapper.mapToPost(newPostRequest, author);

        postRepository.save(post);

        return PostMapper.mapToPostDto(post);
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
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Пост с идентификатором " + postId + " не найден."));

        User author = userRepository.findById(post.getAuthor().getId())
                .orElseThrow(() -> new RuntimeException("Автор поста не найден"));

        List<Image> images = imageRepository.findByPostId(postId);

        post.setAuthor(author);
        post.setImages(images);

        return PostMapper.mapToPostDto(post);
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