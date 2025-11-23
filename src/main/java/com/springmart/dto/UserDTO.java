package com.springmart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String email;
    private String name;
    private String provider;
    private String providerId;
    private String imageUrl;
    private List<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

