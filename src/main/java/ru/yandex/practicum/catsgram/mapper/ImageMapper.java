package ru.yandex.practicum.catsgram.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.catsgram.dto.ImageDto;
import ru.yandex.practicum.catsgram.dto.ImageUploadResponse;
import ru.yandex.practicum.catsgram.model.Image;

import java.nio.file.Path;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageMapper {
    public static Image mapToImage(long postId, Path filePath, String originalFileName) {
        Image image = new Image();
        image.setPostId(postId);
        image.setFilePath(filePath.toString());
        image.setOriginalFileName(originalFileName);
        return image;
    }

    public static ImageDto mapToImageDto(Image image, byte[] data) {
        ImageDto imageDto = new ImageDto();
        imageDto.setId(image.getId());
        imageDto.setPostId(image.getPostId());
        imageDto.setOriginalFileName(image.getOriginalFileName());
        imageDto.setData(data);
        return imageDto;
    }

    public static ImageUploadResponse mapToImageUploadResponse(Image image) {
        ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
        imageUploadResponse.setId(image.getId());
        imageUploadResponse.setPostId(image.getPostId());
        imageUploadResponse.setOriginalFileName(image.getOriginalFileName());
        imageUploadResponse.setFilePath(image.getFilePath());
        return imageUploadResponse;
    }
}
