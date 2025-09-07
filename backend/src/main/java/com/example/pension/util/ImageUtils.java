package com.example.pension.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

/**
 * 图片处理工具类
 * 用于处理Base64图片的保存、删除等操作
 */
@Component
public class ImageUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);
    
    // 图片存储根目录
    @Value("${file.upload.path:/data/pension/images}")
    private String uploadPath;
    
    // 图片访问URL前缀
    @Value("${file.access.url:/api/files/images}")
    private String accessUrlPrefix;
    
    // 支持的图片格式
    private static final String[] SUPPORTED_FORMATS = {"jpg", "jpeg", "png", "gif", "bmp"};
    
    /**
     * 保存Base64编码的图片
     * @param base64Data Base64编码的图片数据（可能包含前缀）
     * @param category 图片分类（如：face_recognition, face_stranger）
     * @param fileName 文件名（不包含扩展名）
     * @return 图片的相对路径
     */
    public String saveBase64Image(String base64Data, String category, String fileName) {
        if (!StringUtils.hasText(base64Data)) {
            throw new IllegalArgumentException("Base64图片数据不能为空");
        }
        
        if (!StringUtils.hasText(category)) {
            category = "default";
        }
        
        if (!StringUtils.hasText(fileName)) {
            fileName = UUID.randomUUID().toString();
        }
        
        try {
            // 解析Base64数据和图片格式
            Base64ImageInfo imageInfo = parseBase64Data(base64Data);
            
            // 生成文件路径
            String relativePath = generateFilePath(category, fileName, imageInfo.getFormat());
            String absolutePath = Paths.get(uploadPath, relativePath).toString();
            
            // 确保目录存在
            createDirectoryIfNotExists(absolutePath);
            
            // 保存图片文件
            try (FileOutputStream fos = new FileOutputStream(absolutePath)) {
                fos.write(imageInfo.getImageBytes());
                fos.flush();
            }
            
            logger.info("图片保存成功: {}", relativePath);
            return relativePath;
            
        } catch (Exception e) {
            logger.error("保存Base64图片失败: fileName={}, category={}", fileName, category, e);
            throw new RuntimeException("保存图片失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 删除图片文件
     * @param relativePath 图片的相对路径
     * @return 是否删除成功
     */
    public boolean deleteImage(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return false;
        }
        
        try {
            String absolutePath = Paths.get(uploadPath, relativePath).toString();
            File file = new File(absolutePath);
            
            if (file.exists() && file.isFile()) {
                boolean deleted = file.delete();
                if (deleted) {
                    logger.info("图片删除成功: {}", relativePath);
                } else {
                    logger.warn("图片删除失败: {}", relativePath);
                }
                return deleted;
            } else {
                logger.warn("图片文件不存在: {}", relativePath);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("删除图片失败: {}", relativePath, e);
            return false;
        }
    }
    
    /**
     * 获取图片的访问URL
     * @param relativePath 图片的相对路径
     * @return 图片的访问URL
     */
    public String getImageUrl(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return null;
        }
        
        return accessUrlPrefix + "/" + relativePath.replace("\\", "/");
    }
    
    /**
     * 检查图片文件是否存在
     * @param relativePath 图片的相对路径
     * @return 是否存在
     */
    public boolean imageExists(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return false;
        }
        
        String absolutePath = Paths.get(uploadPath, relativePath).toString();
        File file = new File(absolutePath);
        return file.exists() && file.isFile();
    }
    
    /**
     * 获取图片文件大小
     * @param relativePath 图片的相对路径
     * @return 文件大小（字节），如果文件不存在返回-1
     */
    public long getImageSize(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return -1;
        }
        
        String absolutePath = Paths.get(uploadPath, relativePath).toString();
        File file = new File(absolutePath);
        
        if (file.exists() && file.isFile()) {
            return file.length();
        }
        
        return -1;
    }
    
    /**
     * 解析Base64数据
     * @param base64Data Base64编码的图片数据
     * @return 图片信息
     */
    private Base64ImageInfo parseBase64Data(String base64Data) {
        String format = "jpg"; // 默认格式
        String pureBase64;
        
        // 检查是否包含数据URL前缀（如：data:image/jpeg;base64,）
        if (base64Data.startsWith("data:image/")) {
            int commaIndex = base64Data.indexOf(",");
            if (commaIndex > 0) {
                String prefix = base64Data.substring(0, commaIndex);
                pureBase64 = base64Data.substring(commaIndex + 1);
                
                // 提取图片格式
                if (prefix.contains("jpeg")) {
                    format = "jpg";
                } else if (prefix.contains("png")) {
                    format = "png";
                } else if (prefix.contains("gif")) {
                    format = "gif";
                } else if (prefix.contains("bmp")) {
                    format = "bmp";
                }
            } else {
                pureBase64 = base64Data;
            }
        } else {
            pureBase64 = base64Data;
        }
        
        // 解码Base64数据
        byte[] imageBytes;
        try {
            imageBytes = Base64.getDecoder().decode(pureBase64);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("无效的Base64数据", e);
        }
        
        // 验证图片数据
        if (imageBytes.length == 0) {
            throw new RuntimeException("图片数据为空");
        }
        
        // 通过文件头判断实际格式（可选）
        String detectedFormat = detectImageFormat(imageBytes);
        if (detectedFormat != null) {
            format = detectedFormat;
        }
        
        return new Base64ImageInfo(imageBytes, format);
    }
    
    /**
     * 通过文件头检测图片格式
     * @param imageBytes 图片字节数据
     * @return 图片格式，如果无法识别返回null
     */
    private String detectImageFormat(byte[] imageBytes) {
        if (imageBytes.length < 4) {
            return null;
        }
        
        // JPEG: FF D8 FF
        if (imageBytes[0] == (byte) 0xFF && imageBytes[1] == (byte) 0xD8 && imageBytes[2] == (byte) 0xFF) {
            return "jpg";
        }
        
        // PNG: 89 50 4E 47
        if (imageBytes[0] == (byte) 0x89 && imageBytes[1] == 0x50 && imageBytes[2] == 0x4E && imageBytes[3] == 0x47) {
            return "png";
        }
        
        // GIF: 47 49 46 38
        if (imageBytes[0] == 0x47 && imageBytes[1] == 0x49 && imageBytes[2] == 0x46 && imageBytes[3] == 0x38) {
            return "gif";
        }
        
        // BMP: 42 4D
        if (imageBytes[0] == 0x42 && imageBytes[1] == 0x4D) {
            return "bmp";
        }
        
        return null;
    }
    
    /**
     * 生成文件路径
     * @param category 分类
     * @param fileName 文件名
     * @param format 格式
     * @return 相对路径
     */
    private String generateFilePath(String category, String fileName, String format) {
        // 按日期分目录：category/yyyy/MM/dd/filename.format
        LocalDate now = LocalDate.now();
        String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        
        return Paths.get(category, datePath, fileName + "." + format).toString();
    }
    
    /**
     * 创建目录（如果不存在）
     * @param filePath 文件路径
     */
    private void createDirectoryIfNotExists(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Path parentDir = path.getParent();
        
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
            logger.debug("创建目录: {}", parentDir);
        }
    }
    
    /**
     * Base64图片信息内部类
     */
    private static class Base64ImageInfo {
        private final byte[] imageBytes;
        private final String format;
        
        public Base64ImageInfo(byte[] imageBytes, String format) {
            this.imageBytes = imageBytes;
            this.format = format;
        }
        
        public byte[] getImageBytes() {
            return imageBytes;
        }
        
        public String getFormat() {
            return format;
        }
    }
}