package kamanotes.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kamanotes.service.FileService;

@Service
public class LocalFileServiceImpl implements FileService {

    @Value("${upload.path}")
    private String uploadBasePath;

    @Value("${upload.url-prefix}")
    private String urlPrefix;

    private static final List<String> ALLOWED_IMAGE_EXTENSIONS
            = Arrays.asList(".jpg", ".jpeg", ".png", ".webp");

    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;

    @Override
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传的图片文件为空");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("图片大小不能超过 10MB");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("图片文件名无效");
        }
        String lowerCaseExtension = originalFilename
                .substring(originalFilename.lastIndexOf("."))
                .toLowerCase();
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(lowerCaseExtension)) {
            throw new IllegalArgumentException(
                    "只支持 " + ALLOWED_IMAGE_EXTENSIONS + " 等格式图片");
        }
        return doUpload(file);
    }

    @Override
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件为空");
        }
        return doUpload(file);
    }

    private String doUpload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("文件名不合法");
        }
        String fileExtension = originalFilename
                .substring(originalFilename.lastIndexOf("."))
                .toLowerCase();
        String newFileName = UUID.randomUUID() + fileExtension;

        File uploadDir = new File(uploadBasePath);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new IllegalStateException("无法创建上传目录: " + uploadBasePath);
        }

        File destFile = new File(uploadDir, newFileName);
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            throw new IllegalStateException("文件保存失败: " + e.getMessage(), e);
        }
        return urlPrefix + "/" + newFileName;
    }
}


