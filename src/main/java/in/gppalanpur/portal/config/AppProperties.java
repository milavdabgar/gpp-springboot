package in.gppalanpur.portal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {
    private final Jwt jwt = new Jwt();
    private final Cors cors = new Cors();
    private final FileStorage fileStorage = new FileStorage();

    @Data
    public static class Jwt {
        private String secret;
        private long expirationMs;
    }

    @Data
    public static class Cors {
        private String[] allowedOrigins;
    }
    
    @Data
    public static class FileStorage {
        private String uploadDir;
    }
}