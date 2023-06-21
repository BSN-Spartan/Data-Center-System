package com.spartan.dc.core.util.common;

import com.spartan.dc.core.enums.IconPrefixEnum;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

/**
 * @ClassName FileCode
 * @Author wjx
 * @Date 2022/11/12 14:27
 * @Version 1.0
 */
public class FileCode {
    public static String generateBase64(MultipartFile file) {
        byte[] imageBytes;
        String base64EncoderImg = "";
        try {
            imageBytes = file.getBytes();
            Base64.Encoder base64Encoder = Base64.getEncoder();
            base64EncoderImg = IconPrefixEnum.getEnumByCode(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."))).getName() + base64Encoder.encodeToString(imageBytes);
            base64EncoderImg = base64EncoderImg.replaceAll("[\\s*\t\n\r]", "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return base64EncoderImg;
    }
}
