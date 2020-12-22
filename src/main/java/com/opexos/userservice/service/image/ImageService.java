package com.opexos.userservice.service.image;

import com.opexos.userservice.exception.InvalidImageException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Service for working with images
 */
@RequiredArgsConstructor
@Transactional
@Service
public class ImageService {
    /**
     * Crop the image to a square shape and returns the cropped and resized image in jpeg format
     *
     * @param data     image in any format in byte array representation
     * @param sideSize side size in pixels
     * @return the cropped and resized image in jpeg format
     * @throws InvalidImageException if an image has corrupted
     */
    public byte[] getCroppedImage(@NonNull byte[] data, int sideSize) throws InvalidImageException {

        BufferedImage image = getImage(data);

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage cropped;
        if (width > height) {
            cropped = Scalr.crop(image, (width - height) / 2, 0, height, height);
        } else if (width < height) {
            cropped = Scalr.crop(image, 0, (height - width) / 2, width, width);
        } else {
            //already square
            cropped = image;
        }

        BufferedImage resized = Scalr.resize(cropped, sideSize);
        return getBytes(resized);
    }

    /**
     * Converts BufferedImage to byte[] using jpeg encoding
     */
    private byte[] getBytes(@NonNull BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error writing image", e);
        }
    }

    /**
     * Converts byte[] to BufferedImage
     */
    private BufferedImage getImage(@NonNull byte[] data) throws InvalidImageException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            try {
                BufferedImage image = ImageIO.read(inputStream);
                if (image == null) {
                    throw new Exception(); //sometimes ImageIO.read returns null instead of exception
                }
                return image;
            } catch (Exception e) {
                throw new InvalidImageException("Invalid image format or data is corrupted");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading image", e);
        }
    }


}
