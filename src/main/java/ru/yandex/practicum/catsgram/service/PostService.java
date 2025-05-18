package ru.yandex.practicum.catsgram.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;

// Указываем, что класс PostService - является бином и его
// нужно добавить в контекст приложения
@Service
public class PostService {
    private final UserService userService;
    private final Map<Long, Post> posts = new HashMap<>();

    @Autowired
    public PostService(UserService userService) {
        this.userService = userService;
    }

    public Collection<Post> findAll(int size, String sort, int from) {
        switch (SortOrder.from(sort)) {
            case ASCENDING -> {
                return posts.values().stream()
                        .sorted(Comparator.comparing(Post::getPostDate))
                        .skip(from)
                        .limit(size)
                        .toList();
            }
            case DESCENDING -> {
                return posts.values().stream()
                        .sorted(Comparator.comparing(Post::getPostDate).reversed())
                        .skip(from)
                        .limit(size)
                        .toList();
            }
            default -> throw new ConditionsNotMetException(
                    String.format("Неверный порядок сортировки: %s, выберете между asc, desc!", sort));
        }
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        Optional<User> user = userService.findUserById(post.getAuthorId());

        if (user.isEmpty()) {
            throw new ConditionsNotMetException("Автор с id = " + post.getAuthorId() + " не найден");
        }

        post.setId(getNextId());
        post.setPostDate(Instant.now());
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

    public Post findPostById(long id) {
        return posts.entrySet().stream()
                .filter(entry -> entry.getKey().equals(id))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("Пост с id = %d не найден", id)));
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
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