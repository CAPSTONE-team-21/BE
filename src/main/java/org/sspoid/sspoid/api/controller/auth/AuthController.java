package org.sspoid.sspoid.api.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sspoid.sspoid.api.controller.CurrentUser;
import org.sspoid.sspoid.api.dto.auth.EmailSendRequest;
import org.sspoid.sspoid.api.dto.auth.EmailVerifyRequest;
import org.sspoid.sspoid.api.dto.auth.LoginRequest;
import org.sspoid.sspoid.api.dto.auth.SignUpRequest;
import org.sspoid.sspoid.api.dto.auth.TokenResponse;
import org.sspoid.sspoid.api.service.auth.AuthService;
import org.sspoid.sspoid.api.service.auth.EmailService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(request));
        }
        catch (IllegalStateException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));
    }

    @PostMapping("/email/send") //메일 전송
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody EmailSendRequest request) {
        emailService.sendCodeToEmail(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email/verify") //메일 인증
    public ResponseEntity<String> verifyEmail(@Valid @RequestBody EmailVerifyRequest request) {
        EmailService.VerificationResult result = emailService.verifyEmail(request.email(), request.code());

        switch (result) {
            case SUCCESS -> {
                return ResponseEntity.ok("이메일 인증 성공!");
            }
            case BAD_REQUEST -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("인증 코드가 발송되지 않았습니다.");
            }
            case UNAUTHORIZED -> {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("인증 코드가 일치하지 않습니다.");
            }
            case GONE -> {
                return ResponseEntity.status(HttpStatus.GONE)
                        .body("인증 코드가 만료되었습니다.");
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                body("알 수 없는 오류가 발생하였습니다: " + result.toString());
    }

    @GetMapping("/test/user")
    public ResponseEntity<String> testCurrentUser(@CurrentUser Long currentUserId) {
        return ResponseEntity.ok("현재 유저 ID: " + currentUserId);
    }
}
