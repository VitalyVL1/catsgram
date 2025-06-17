package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.model.Image;

import java.util.Collection;
import java.util.Optional;

@Repository
public class ImageRepository extends BaseRepository<Image> {
    private static final String FIND_BY_POST_ID_QUERY = "SELECT * FROM image_storage WHERE post_id=?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM image_storage WHERE id=?";
    private static final String INSERT_IMAGE_QUERY = "INSERT INTO image_storage (original_name, file_path, post_id) " +
            "VALUES (?,?,?) returning id";
    private static final String DELETE_IMAGE_QUERY = "DELETE FROM image_storage WHERE id=?";

    public ImageRepository(JdbcTemplate jdbc, RowMapper<Image> mapper) {
        super(jdbc, mapper);
    }

    public Image save(Image image) {
        long postId = insert(
                INSERT_IMAGE_QUERY,
                image.getOriginalFileName(),
                image.getFilePath(),
                image.getPostId()
        );
        image.setId(postId);
        return image;
    }

    public Optional<Image> findById(long imageId) {
        return findOne(FIND_BY_ID_QUERY, imageId);
    }

    public Collection<Image> findByPostId(long postId) {
        return findMany(FIND_BY_POST_ID_QUERY, postId);
    }

    public boolean deleteById(long imageId) {
        return delete(DELETE_IMAGE_QUERY, imageId);
    }
}
