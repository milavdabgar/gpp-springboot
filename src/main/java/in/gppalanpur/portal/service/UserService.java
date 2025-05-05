package in.gppalanpur.portal.service;

import in.gppalanpur.portal.dto.user.UserDetailsResponse;
import in.gppalanpur.portal.dto.user.UserProfileUpdateRequest;

public interface UserService {
    
    UserDetailsResponse getCurrentUser(String username);
    
    UserDetailsResponse updateUserProfile(String username, UserProfileUpdateRequest request);
}