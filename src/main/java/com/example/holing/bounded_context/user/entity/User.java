package com.example.holing.bounded_context.user.entity;

import com.example.holing.bounded_context.auth.dto.OAuthUserInfoDto;
import com.example.holing.bounded_context.schedule.entity.Schedule;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    List<Schedule> schedules = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String profileImgUrl;

    private Gender gender;

    private Integer point;

    private Long socialId;

    @OneToOne
    private User mate;

    @Builder
    public User(String email, String password, String nickname, String profileImgUrl, Gender gender, Integer point, Long socialId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.gender = gender;
        this.point = point;
        this.socialId = socialId;
    }

    public static User fromEntity(OAuthUserInfoDto dto) {
        return User.builder()
                .email(dto.email())
                .nickname(dto.nickname())
                .profileImgUrl(dto.profileImageUrl())
                .socialId(dto.id())
                .build();
    }

    public User update(String nickname, String profileImgUrl, Gender gender) {
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.gender = gender;
        return this;
    }

    public void connectMate(User user) {
        this.mate = user;
        user.mate = this;
    }

    public void disconnectMate(User user) {
        this.mate = null;
        user.mate = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return nickname;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}