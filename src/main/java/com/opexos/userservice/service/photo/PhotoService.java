package com.opexos.userservice.service.photo;

import com.opexos.userservice.configuration.UserServiceProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Service for working with user photos
 */
@RequiredArgsConstructor
@Service
public class PhotoService {

    private final UserServiceProperties properties;

    public void savePhoto(long userId, @NonNull byte[] photo) {
        Assert.isTrue(photo.length > 0, "Empty photo");
        Path path = getPath(userId);
        try {
            try {
                Files.write(path, photo);
            } catch (NoSuchFileException e) {
                //perhaps there are no directories - we will create them
                Files.createDirectories(path.getParent());
                Files.write(path, photo);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving photo", e);
        }
    }

    public byte[] getPhoto(long userId) {
        if (Files.notExists(getPath(userId))) {
            //if the photo doesn't exist, return null instead of throwing an exception
            return null;
        }
        try {
            return Files.readAllBytes(getPath(userId));
        } catch (IOException e) {
            throw new RuntimeException("Error reading photo", e);
        }
    }

    public void deletePhoto(long userId) {
        try {
            Files.deleteIfExists(getPath(userId));
        } catch (IOException e) {
            throw new RuntimeException("Error deleting photo", e);
        }
    }

    private Path getPath(long userId) {
        //Save 10 thousand files in each folder. It will be more convenient to backup data,
        //and there will be no performance problems on some operating systems.
        return Paths.get(properties.getPhotoFolder(),
                "" + userId / 10000L,
                "" + userId);
    }

}
