package com.znahodochka.backend.users.services;

import com.znahodochka.backend.publications.service.PublicationService;
import com.znahodochka.backend.users.config.jwt.JwtUtils;
import com.znahodochka.backend.users.dto.*;
import com.znahodochka.backend.users.model.Role;
import com.znahodochka.backend.users.model.User;
import com.znahodochka.backend.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    private final PasswordEncoder passwordEncoder;

    /**
     * Аутентифицирует пользователя.
     *
     * @param dto данные для входа
     * @return ответ с JWT
     * @throws UsernameNotFoundException если аутентификация не удалась
     */
    @Override
    public LoginResponseDto authenticate(LoginDto dto) {

        final UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(dto.login(), dto.password());
        try {

            final Authentication authentication = authenticationManager.authenticate(token);


            final String jwt = jwtUtils.generateJwtToken(authentication);

            return new LoginResponseDto(jwt);

        } catch (final BadCredentialsException ex) {
            throw new UsernameNotFoundException(dto.login(), ex);
        }
    }

    @Override
    public RegResponseDto registration(RegDto dto) {
        Optional<User> user = userRepository.findByLogin(dto.login());
        if (user.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already in use");
        }
        User newUser = User.builder()
                .login(dto.login())
                .password(passwordEncoder.encode(dto.password()))
                .bio(dto.bio())
                .role(Role.BASIC)
                .avatarUri(dto.avatar_uri())
                .numberOfFollowers(0L)
                .numberOfFollowing(0L)
                .createdAt(Instant.now())
                .name(dto.name())
                .build();
        userRepository.save(newUser);

        final UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(dto.login(), dto.password());
        try {

            final Authentication authentication = authenticationManager.authenticate(token);


            final String jwt = jwtUtils.generateJwtToken(authentication);

            return new RegResponseDto(jwt);

        } catch (final BadCredentialsException ex) {
            throw new UsernameNotFoundException(dto.login(), ex);
        }
    }

    @Override
    public String getHello(Long principal) {
        return "Hello " +
                (userRepository.findById(principal).orElseThrow()).getLogin();
    }

    @Override
    public UserInfoDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return UserInfoDto.builder()
                .id(user.getId())
                .login(user.getLogin())
                .bio(user.getBio())
                .name(user.getName())
                .avatar_uri(user.getAvatarUri())
                .numberOfFollowers(user.getNumberOfFollowers())
                .numberOfFollowing(user.getNumberOfFollowing())
                .email(user.getEmail())

                .build();
    }

    @Override
    public void updateUserInfo(UpdateUserInfoDto dto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setBio(dto.bio());
        user.setName(dto.name());
        userRepository.save(user);
    }

    @Override
    public boolean isLoginFree(String username) {
        return !userRepository.existsByLogin(username);
    }

    @Override
    public void updateAvatarUri(String avatarUri, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setAvatarUri(avatarUri);
        userRepository.save(user);
    }

    @Override
    public boolean isEmailFree(String email) {
        return !userRepository.existsByEmail(email);
    }

    @Override
    public void updateEmail(String email, Long principal) {
        if(userRepository.existsByEmail(email)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
        }
        User user = userRepository.findById(principal).orElseThrow();
        user.setEmail(email);
        userRepository.save(user);
    }

    @Override
    public void updatePassword(String password, String newPassword, Long principal) {
        User user = userRepository.findById(principal).orElseThrow();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public ShortUserInfoDto getShortUserInfo(Long userId) {
        User user =  userRepository.findById(userId).orElseThrow();
        return ShortUserInfoDto.builder()
                .id(userId)
                .login(user.getLogin())
                .avatarUri(user.getAvatarUri())
                .name(user.getName())
                .build();
    }

    @Override
    public void deleteAvatarUri(String avatarUri, Long principal) {
        User user = userRepository.findById(principal).orElseThrow();
        user.setAvatarUri(null);
        userRepository.save(user);
    }

    @Override
    public List<ShortUserInfoDto> searchUsers(String query) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }
        return userRepository.findByLoginLike(query.trim())
                .stream()
                .map(this::toShortDto)
                .toList();
    }

    private ShortUserInfoDto toShortDto(User user) {
        return ShortUserInfoDto.builder()
                .id(user.getId())
                .login(user.getLogin())
                .name(user.getName())
                .avatarUri(user.getAvatarUri())
                .build();
    }
}
