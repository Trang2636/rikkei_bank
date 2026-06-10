package springboot_cntt2.it211_rikkeibank.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import springboot_cntt2.it211_rikkeibank.exception.BadRequestException;
import springboot_cntt2.it211_rikkeibank.service.FileStorageService;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Override
    public String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File eKYC không được để trống");
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null) {
            throw new BadRequestException("Tên file không hợp lệ");
        }

        String lowerName = originalFilename.toLowerCase();

        if (!lowerName.endsWith(".jpg") && !lowerName.endsWith(".jpeg") && !lowerName.endsWith(".png")) {
            throw new BadRequestException("File eKYC chỉ cho phép jpg, jpeg, png");
        }

        try {
            Path folderPath = Paths.get(uploadDir);

            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            String fileName = UUID.randomUUID() + "_" + originalFilename;
            Path filePath = folderPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filePath.toString().replace("\\", "/");
        } catch (IOException e) {
            throw new BadRequestException("Không thể lưu file eKYC");
        }
    }
}