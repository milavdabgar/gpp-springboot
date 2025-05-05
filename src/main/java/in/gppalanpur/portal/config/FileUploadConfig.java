package in.gppalanpur.portal.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.File;

@Configuration
@EnableConfigurationProperties
public class FileUploadConfig {
    
    private final AppProperties appProperties;
    
    public FileUploadConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
        
        // Create upload directory if it doesn't exist
        File uploadDir = new File(appProperties.getFileStorage().getUploadDir());
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }
    
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}