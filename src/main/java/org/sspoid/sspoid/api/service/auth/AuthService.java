package org.sspoid.sspoid.api.service.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.sspoid.sspoid.api.dto.auth.LoginRequest;
import org.sspoid.sspoid.api.dto.auth.LoginResponse;
import org.sspoid.sspoid.api.dto.auth.SignUpRequest;
import org.sspoid.sspoid.api.dto.auth.TokenResponse;
import org.sspoid.sspoid.common.security.JwtTokenProvider;
import org.sspoid.sspoid.db.token.RefreshToken;
import org.sspoid.sspoid.db.token.RefreshTokenRepository;
import org.sspoid.sspoid.db.user.User;
import org.sspoid.sspoid.db.user.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtUtil;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Transactional
    public TokenResponse signUp(SignUpRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("이미 존재하는 이메일입니다");
        }
        User newUser = User.builder()
                .name(request.nickname())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .build();

        userRepository.save(newUser);

        String accessToken = jwtUtil.createAccessToken(newUser);
        String refreshToken = jwtUtil.createRefreshToken(newUser);

        saveRefreshToken(newUser, refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("[LOGIN FAILED] email={}, timestamp={}", user.getEmail(), LocalDateTime.now());
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(user.getId())
                .orElseThrow(()-> new IllegalArgumentException("토큰이 존재하지 않습니다,"));

        String accessToken = "";
        String refreshToken = refreshTokenEntity.getToken();

        if (jwtUtil.isValidRefreshToken(refreshToken)) { //refreshtoken 유효성 검증 갱신
            accessToken = jwtUtil.createAccessToken(user);
            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .name(user.getName())
                    .build();
        }

        refreshToken = jwtUtil.createRefreshToken(user);
        refreshTokenEntity.updateToken(refreshToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .name(user.getName())
                .build();
    }

    private void saveRefreshToken(User newUser, String refreshToken) {
        RefreshToken token = RefreshToken.builder()
                        .user(newUser)
                        .token(refreshToken)
                        .build();
        refreshTokenRepository.save(token);
    }

}
