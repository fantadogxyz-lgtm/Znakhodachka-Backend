package com.znahodochka.backend.users.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Slf4j
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users", schema = "users")
@Builder
public class User implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login")
    String login;
    @Column(name = "name")
    String name;
    @Column(name = "password")
    String password;
    @Column(name = "email")
    String email;
    @Column(name = "avatar_uri")
    String avatarUri;
    @Column(name = "bio")
    String bio;
    @Column(name = "created_at")
    Instant createdAt;
    @Column(name="role")
    Role role;
    @Column(name="numb_of_followers")
    Long numberOfFollowers;
    @Column(name="numb_of_followings")
    Long numberOfFollowing;

    //TODO СВЯЗИ УКАЗАТЬ ЕЩЕ

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
