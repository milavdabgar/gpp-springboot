package in.gppalanpur.portal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "GPP Spring Boot API is running");
        
        Map<String, Object> data = new HashMap<>();
        data.put("version", "1.0.0");
        data.put("name", "GPP Spring Boot API");
        
        response.put("data", data);
        
        return ResponseEntity.ok(response);
    }
}
