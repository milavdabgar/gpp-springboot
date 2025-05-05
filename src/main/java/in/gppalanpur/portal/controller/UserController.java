package in.gppalanpur.portal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.gppalanpur.portal.dto.user.UserDetailsResponse;
import in.gppalanpur.portal.dto.user.UserProfileUpdateRequest;
import in.gppalanpur.portal.security.UserDetailsImpl;
import in.gppalanpur.portal.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management API")
public class UserController {

    private final UserService userService;
    
    @GetMapping("/me")
    @Operation(summary = "Get current user details")
    public ResponseEntity<UserDetailsResponse> getCurrentUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        UserDetailsResponse response = userService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/updateMe")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserDetailsResponse> updateUserProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        
        UserDetailsResponse response = userService.updateUserProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }
}