package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;

@Service
public class SimpleImageService implements ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final Path path;

    @Autowired
    public SimpleImageService(@Value("${cawi.image-saved-path}") String path) {
        this.path = Paths.get(path);
    }

    @Override
    public String save(InputStream inputStream) {
        LOGGER.info("Save image");
        try {
            byte[] bytes = inputStream.readAllBytes();
            String contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(bytes));
            if (contentType == null) {
                throw new RuntimeException("Content type could not be determined");
            }
            contentType = contentType.split("/")[1];
            if (!(contentType.equals("png") || contentType.equals("jpeg"))) {
                throw new RuntimeException("Content type " + contentType + " is not supported");
            }

            byte[] hash = MessageDigest.getInstance("SHA-256").digest(bytes);
            String filename = bytesToHex(hash) + "." + contentType;
            Files.write(path.resolve(filename), bytes);
            return filename;
        } catch (Exception e) {
            throw new RuntimeException("Could not store image. Error: " + e.getMessage());
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
