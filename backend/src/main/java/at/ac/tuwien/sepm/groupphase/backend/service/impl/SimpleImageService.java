package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.service.ImageService;
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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SimpleImageService implements ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final Path imageSavedPath;

    @Autowired
    public SimpleImageService(@Value("${cawi.image-saved-path}") String imageSavedPath) {
        this.imageSavedPath = Paths.get(imageSavedPath);
    }

    @Override
    public String save(MultipartFile file) {
        LOGGER.info("Save image");
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException ex) {
            throw new RuntimeException("Could not store image. Error: " + ex.getMessage());
        }

        if (bytes.length == 0) {
            throw new IllegalArgumentException("Empty file");
        }

        //Create filename
        String contentType = getContentTypeFromBytes(bytes);
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("SHA-256").digest(bytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Could not store image. Error: " + ex.getMessage());
        }
        String filename = bytesToHex(hash) + "." + contentType;

        //Save image
        try {
           Files.write(imageSavedPath.resolve(filename), bytes);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store image. Error: " + ex.getMessage());
        }

        return filename;
    }

    private String getContentTypeFromBytes(byte[] bytes) {
        String contentType;
        try {
            contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(bytes));
        } catch (IOException ex) {
            throw new RuntimeException("Could not store image. Error: " + ex.getMessage());
        }
        if (contentType == null) {
            throw new IllegalArgumentException("Content type could not be determined");
        }
        contentType = contentType.split("/")[1].toLowerCase();
        if (!(contentType.equals("png") || contentType.equals("jpeg"))) {
            throw new IllegalArgumentException("Content type " + contentType + " is not supported");
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
