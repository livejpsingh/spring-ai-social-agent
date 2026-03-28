package com.socialmediaagent.domain.dto;

import lombok.Data;

@Data
public class GoogleAuthRequest {
    private String credential; // The Google ID token from frontend
}
