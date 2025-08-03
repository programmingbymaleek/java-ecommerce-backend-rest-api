package com.statless_api_setup.stateless_api.refreshTokenConfig;

import com.statless_api_setup.stateless_api.user.UserEntity;

public record RotationResult(UserEntity user, String newCookieValue) {
}