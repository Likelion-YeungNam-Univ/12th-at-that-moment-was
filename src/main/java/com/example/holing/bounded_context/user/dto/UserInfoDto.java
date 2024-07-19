package com.example.holing.bounded_context.user.dto;

import com.example.holing.bounded_context.user.entity.Gender;
import com.example.holing.bounded_context.user.entity.User;

import java.util.Optional;

public record UserInfoDto(
        Long id,
        String email,
        String nickname,
        String profileImgUrl,
        Gender gender,
        Integer point,
        Long socialId,
        Long mateId
) {
    public static UserInfoDto fromEntity(User user) {
        return new UserInfoDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImgUrl(),
                user.getGender(),
                user.getPoint(),
                user.getSocialId(),
                Optional.ofNullable(user.getMate())
                        .map(User::getId)
                        .orElse(null)
        );
    }
}