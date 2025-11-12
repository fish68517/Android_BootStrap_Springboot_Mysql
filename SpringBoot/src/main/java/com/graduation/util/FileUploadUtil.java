package com.graduation.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 文件上传工具类
 * 用于处理图片上传功能
 */
@Component
public class FileUploadUtil {

    @Value("${file.upload-dir:src/main/resources/static/img}")
    private String uploadDir;

    @Value("${file.max-size:10485760}")
    private long maxFileSize;

    /**
     * 上传单个文件
     * 
     * @param file 上传的文件
     * @return 文件访问URL
     * @throws IOException 文件操作异常
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // 验证文件
        validateFile(file);

        // 确保上传目录存在
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // 保存文件
        Path targetPath = Paths.get(uploadDir, uniqueFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // 返回访问URL
        return "/img/" + uniqueFilename;
    }

    /**
     * 上传多个文件
     * 
     * @param files 上传的文件数组
     * @return 文件访问URL，用逗号分隔
     * @throws IOException 文件操作异常
     */
    public String uploadMultipleFiles(MultipartFile[] files) throws IOException {
        if (files == null || files.length == 0) {
            return "";
        }

        StringBuilder urls = new StringBuilder();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].isEmpty()) {
                String url = uploadFile(files[i]);
                urls.append(url);
                if (i < files.length - 1) {
                    urls.append(",");
                }
            }
        }
        return urls.toString();
    }

    /**
     * 删除文件
     * 
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        try {
            // 从URL提取文件名
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir, filename);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 删除多个文件
     * 
     * @param fileUrls 文件URL，用逗号分隔
     */
    public void deleteMultipleFiles(String fileUrls) {
        if (fileUrls == null || fileUrls.isEmpty()) {
            return;
        }

        String[] urls = fileUrls.split(",");
        for (String url : urls) {
            deleteFile(url.trim());
        }
    }

    /**
     * 验证文件
     * 
     * @param file 上传的文件
     * @throws IllegalArgumentException 文件验证失败
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("文件大小不能超过 " + (maxFileSize / 1024 / 1024) + "MB");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !isImageFile(contentType)) {
            throw new IllegalArgumentException("只支持图片文件（JPG, PNG, GIF）");
        }
    }

    /**
     * 检查是否为图片文件
     * 
     * @param contentType 文件MIME类型
     * @return 是否为图片
     */
    private boolean isImageFile(String contentType) {
        return contentType.equals("image/jpeg") ||
               contentType.equals("image/jpg") ||
               contentType.equals("image/png") ||
               contentType.equals("image/gif");
    }

    /**
     * 获取文件扩展名
     * 
     * @param filename 文件名
     * @return 扩展名（包含点号）
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex);
        }
        return "";
    }

    /**
     * 获取上传目录路径
     * 
     * @return 上传目录路径
     */
    public String getUploadDir() {
        return uploadDir;
    }
}
