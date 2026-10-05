package com.znahodochka.backend.users.services;

import com.znahodochka.backend.users.dto.CreateFollowingDto;
import com.znahodochka.backend.users.dto.FollowingDto;
import com.znahodochka.backend.users.dto.ShortUserInfoDto;
import com.znahodochka.backend.users.model.Following;
import com.znahodochka.backend.users.model.User;
import com.znahodochka.backend.users.repositories.FollowingRepository;
import com.znahodochka.backend.users.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class FollowingServiceImpl implements FollowingService {

    private final UserRepository userRepository;
    private final FollowingRepository followingRepository;
    private final UserService userService;

    public FollowingServiceImpl(UserRepository userRepository, FollowingRepository followingRepository, UserService userService) {
        this.userRepository = userRepository;
        this.followingRepository = followingRepository;
        this.userService = userService;
    }

    @Transactional
    @Override
    public FollowingDto createFollowing(CreateFollowingDto dto, Long userId) {

        if (followingRepository.existsByFollowerIdAndFollowingId(userId, dto.followingId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already following");
        }

        if (userId.equals(dto.followingId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot follow yourself");
        }

        Following following = Following.builder()
                .followerId(userId)
                .followingId(dto.followingId())
                .followDate(Instant.now())
                .build();
        followingRepository.save(following);

        User follower = userRepository.findById(userId).orElseThrow();
        follower.setNumberOfFollowing(follower.getNumberOfFollowing() + 1);
        userRepository.save(follower);

        User followingUser = userRepository.findById(dto.followingId()).orElseThrow();
        followingUser.setNumberOfFollowers(followingUser.getNumberOfFollowers() + 1);
        userRepository.save(followingUser);

        return FollowingDto.builder()
                .id(following.getId())
                .followerId(following.getFollowerId())
                .followingId(following.getFollowingId())
                .createdAt(following.getFollowDate())
                .build();
    }

    @Transactional
    @Override
    public void deleteFollowing(Long followingId, Long principal) {

        Following following = followingRepository
                .findByFollowerIdAndFollowingId(principal, followingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Following not found"));

        User follower = userRepository.findById(principal).orElseThrow();
        follower.setNumberOfFollowing(
                Math.max(0, follower.getNumberOfFollowing() - 1));
        userRepository.save(follower);

        User followingUser = userRepository.findById(followingId).orElseThrow();
        followingUser.setNumberOfFollowers(
                Math.max(0, followingUser.getNumberOfFollowers() - 1));
        userRepository.save(followingUser);

        followingRepository.delete(following);
    }

    @Override
    public List<ShortUserInfoDto> getFollowers(Long userId) {
        return followingRepository.getAllByFollowingId(userId).stream()
                .map(f -> userService.getShortUserInfo(f.getFollowerId()))
                .toList();
    }

    @Override
    public List<ShortUserInfoDto> getFollowings(Long userId) {
        return followingRepository.getAllByFollowerId(userId).stream()
                .map(f -> userService.getShortUserInfo(f.getFollowingId()))
                .toList();
    }

    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        return followingRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }
}
