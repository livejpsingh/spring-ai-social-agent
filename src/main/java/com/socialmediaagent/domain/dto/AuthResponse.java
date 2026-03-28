package com.socialmediaagent.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token; // The backend JWT
    private UserData user;

    @Data
    @Builder
    public static class UserData {
        private Long id;
        private String name;
        private String email;
        private String picture;
    }
}
