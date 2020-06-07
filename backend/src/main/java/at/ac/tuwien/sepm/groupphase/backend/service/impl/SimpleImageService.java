package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

@Service
public class SimpleImageService implements ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final Path dir = Paths.get("images");

    @Override
    public String save(byte[] image) {
        LOGGER.info("Save image");
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(image);
            String hex = bytesToHex(hash);
            Files.write(dir.resolve(hex), image);
            return hex;
        } catch (Exception e) {
            throw new RuntimeException("Could not store image. Error: " + e.getMessage());
        }
    }

    @Override
    public byte[] getImage(String filename) {
        LOGGER.info("Get image {}", filename);
        Path path = dir.resolve(filename);
        if (!Files.exists(path)) throw new NotFoundException("Could not find image " + filename);

        try {
            return Files.readAllBytes(path);
        } catch (Exception e) {
            throw new RuntimeException("Could not read image. Error: " + e.getMessage());
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
