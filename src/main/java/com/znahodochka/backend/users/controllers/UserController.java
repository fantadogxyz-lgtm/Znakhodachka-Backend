package com.znahodochka.backend.users.controllers;

import com.znahodochka.backend.users.dto.*;
import com.znahodochka.backend.users.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody final LoginDto dto) {
        return userService.authenticate(dto);
    }

    @PostMapping("/reg")
    public RegResponseDto reg(@RequestBody final RegDto dto) {
        return userService.registration(dto);
    }

    @GetMapping("/hello")
    @PreAuthorize("hasAnyRole('BASIC')")
    public String hello(
                                final UsernamePasswordAuthenticationToken token) {
        return userService.getHello((Long) token.getPrincipal());
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('BASIC')")
    public UserInfoDto getMe(final UsernamePasswordAuthenticationToken token) {
        Long userId = (Long) token.getPrincipal();
        return userService.getUserInfo(userId);
    }

    @GetMapping("/user-info/{userId}")
    @PreAuthorize("hasAnyRole('BASIC')")
    public UserInfoDto getUserInfo(
            @PathVariable final Long userId) {
        return userService.getUserInfo(userId);
    }
    @GetMapping("/short-user-info/{userId}")
    @PreAuthorize("hasAnyRole('BASIC')")
    public ShortUserInfoDto getShortUserInfo(
            @PathVariable final Long userId) {
        return userService.getShortUserInfo(userId);
    }

    @PutMapping("/user-info")
    @PreAuthorize("hasAnyRole('BASIC')")
    public void UpdateUserInfo( @RequestBody UpdateUserInfoDto dto,
            final UsernamePasswordAuthenticationToken token) {
            userService.updateUserInfo(dto, (Long) token.getPrincipal());
    }

    @PutMapping("/user-info/avatar_uri")
    @PreAuthorize("hasAnyRole('BASIC')")
    public void UpdateUserAvatarUri( @RequestParam String avatarUri,
                                final UsernamePasswordAuthenticationToken token) {
        userService.updateAvatarUri(avatarUri, (Long) token.getPrincipal());
    }

    @DeleteMapping("/user-info/avatar_uri")
    @PreAuthorize("hasAnyRole('BASIC')")
    public void deleteUserAvatarUri( @RequestParam String avatarUri,
                                final UsernamePasswordAuthenticationToken token) {
        userService.deleteAvatarUri(avatarUri, (Long) token.getPrincipal());
    }

    @PutMapping("/user-info/email")
    @PreAuthorize("hasAnyRole('BASIC')")
    public void UpdateUserEmail( @RequestParam String email,
                                     final UsernamePasswordAuthenticationToken token) {
        userService.updateEmail(email, (Long) token.getPrincipal());
    }

    @PutMapping("/user-info/password")
    @PreAuthorize("hasAnyRole('BASIC')")
    public void UpdateUserPassword( @RequestParam String password,
                                     @RequestParam String newPassword,
                                     final UsernamePasswordAuthenticationToken token) {
        userService.updatePassword(password, newPassword, (Long) token.getPrincipal());
    }



    @GetMapping("/free-login")
    public boolean isLoginFree(@RequestParam String username) {
        return userService.isLoginFree(username);
    }
    @GetMapping("/free-email")
    public boolean isEmailFree(@RequestParam String email) {
        return userService.isEmailFree(email);
    }

}
