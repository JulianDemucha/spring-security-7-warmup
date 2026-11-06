package com.sigmaproject1.project1.dto;

import java.util.UUID;

public record RefreshTokenDto(Long userId, String newRawToken, UUID familyId) {
}