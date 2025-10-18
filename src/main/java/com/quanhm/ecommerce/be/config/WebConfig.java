package com.quanhm.ecommerce.be.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("F:/ecommerce.be/uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();
        // ánh xạ /uploads/** tới thư mục thật trong máy
//        registry.addResourceHandler("/uploads/**")
//                .addResourceLocations("file:F:/build-ecommerce/src/assets/img/");
        registry.addResourceHandler("/uploads/**")
                // Quan trọng: Phải là "file:/" cộng với đường dẫn tuyệt đối và kết thúc bằng "/"
                .addResourceLocations("file:/" + uploadPath + "/");
    }
}