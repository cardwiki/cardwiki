package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Image;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.BadRequestException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ImageNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ImageRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ImageService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SimpleImageService implements ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final Path imageSavedPath;
    private final ImageRepository imageRepository;
    private final UserService userService;

    @Autowired
    public SimpleImageService(@Value("${cawi.image-saved-path}") String imageSavedPath, ImageRepository imageRepository, UserService userService) {
        this.imageSavedPath = Paths.get(imageSavedPath);
        this.imageRepository = imageRepository;
        this.userService = userService;
    }

    @Override
    public Image create(MultipartFile file) {
        LOGGER.debug("Create a new image");
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException ex) {
            throw new RuntimeException("Could not store image. Error: " + ex.getMessage());
        }

        if (bytes.length == 0) {
            throw new BadRequestException("Empty file");
        }

        // Create filename
        String contentType = getContentTypeFromBytes(bytes);
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("SHA-256").digest(bytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Could not store image. Error: " + ex.getMessage());
        }
        String filename = bytesToHex(hash) + "." + contentType;

        // Check if image already exists
        Image existingImage = imageRepository.findByFilename(filename).orElse(null);
        if (existingImage != null) {
            return existingImage;
        }

        // Save file to filesystem
        try {
           Files.write(imageSavedPath.resolve(filename), bytes);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store image.", ex);
        }

        // Create image entity
        User user = userService.loadCurrentUserOrThrow();
        Image image = new Image();
        image.setCreatedBy(user);
        image.setFilename(filename);

        return imageRepository.save(image);
    }

    @Transactional
    @Override
    public Image findOneOrThrow(String filename) {
        LOGGER.debug("Find image with filename {}", filename);
        Optional<Image> image = imageRepository.findByFilename(filename);
        return image.orElseThrow(() -> new ImageNotFoundException("Could not find image with filename " + filename));
    }

    private String getContentTypeFromBytes(byte[] bytes) {
        String contentType;
        try {
            contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(bytes));
        } catch (IOException ex) {
            throw new RuntimeException("Could not store image.", ex);
        }
        if (contentType == null) {
            throw new BadRequestException("Content type could not be determined");
        }
        contentType = contentType.split("/")[1].toLowerCase();
        if (!(contentType.equals("png") || contentType.equals("jpeg"))) {
            throw new BadRequestException("Content type " + contentType + " is not supported");
        }
        return contentType;
    }

    private String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
