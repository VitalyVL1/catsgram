package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.dto.PostDto;
import ru.yandex.practicum.catsgram.model.Post;

import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository extends BaseRepository {
    private static final String FIND_ALL_QUERY = "SELECT * FROM post";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM post WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO post (id, author_id, description, post_date) " +
            "VALUES (?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE post SET author_id = ?, description = ? WHERE id = ?";

    public PostRepository(JdbcTemplate jdbc,  RowMapper<Post> mapper) {
        super(jdbc, mapper);
    }

    public List<Post> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Post> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Post save(Post post) {
        long id = insert(
                INSERT_QUERY,
                post.getId(),
                post.getAuthorId(),
                post.getDescription(),
                post.getPostDate());
        post.setId(id);
        return post;
    }

    public Post update(Post post) {
        update(
                UPDATE_QUERY,
                post.getAuthorId(),
                post.getDescription()
        );
        return post;
    }
}
