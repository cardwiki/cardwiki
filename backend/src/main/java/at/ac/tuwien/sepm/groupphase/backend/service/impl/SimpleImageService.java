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
        try {
            byte[] bytes = file.getBytes();

            //Get and check content type
            String contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(bytes));
            if (contentType == null) {
                throw new IllegalArgumentException("Content type could not be determined");
            }
            contentType = contentType.split("/")[1].toLowerCase();
            if (!(contentType.equals("png") || contentType.equals("jpeg"))) {
                throw new IllegalArgumentException("Content type " + contentType + " is not supported");
            }

            //Save file
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(bytes);
            String filename = bytesToHex(hash) + "." + contentType;
            Files.write(imageSavedPath.resolve(filename), bytes);

            return filename;
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not store image. Error: " + e.getMessage());
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
