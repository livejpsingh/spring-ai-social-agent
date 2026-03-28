package com.socialmediaagent.domain.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog implements Serializable {

    private Long userId;
    private String action;
    private String details;
    private LocalDateTime timestamp;
}
