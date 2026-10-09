package com.znahodochka.backend.users.services;

import com.znahodochka.backend.users.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;


public interface UserService {
    public LoginResponseDto authenticate(LoginDto dto);

    RegResponseDto registration(RegDto dto);

    String getHello(Long principal);

    UserInfoDto getUserInfo(Long userId);

    void updateUserInfo(UpdateUserInfoDto dto, Long userId);

    boolean isLoginFree(String username);

    void updateAvatarUri(String avatarUri, Long principal);

    boolean isEmailFree(String email);

    void updateEmail(String email, Long principal);

    void updatePassword(String password, String newPassword, Long principal);

    ShortUserInfoDto getShortUserInfo(Long userId);

    void deleteAvatarUri(String avatarUri, Long principal);

    List<ShortUserInfoDto> searchUsers(String query);
}
