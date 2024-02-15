package com.ex.palago.auth.controller;

import com.ex.palago.auth.model.request.SendSignUpEmailRequest;
import com.ex.palago.auth.model.request.SignupRequest;
import com.ex.palago.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/sign")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private static final String TYPE = "type=";
    private static final String SIGN_UP = TYPE + "sign-up";
    private static final String PASSWORD_CHANGE = TYPE + "password-change";


    /* 회원가입 API */
    @PostMapping("/new")
    public ResponseEntity<Void> signUp(@RequestBody SignupRequest signUpRequest){
        log.info("{}", signUpRequest);
        authService.signUp(signUpRequest.getUsername(), signUpRequest.getPassword(), signUpRequest.getName(), signUpRequest.getPhone());
        return ResponseEntity.ok().build();
    }

    /* 회원가입 인증 코드 이메일 발송 API */
//    @PostMapping(value = "/verification", params = SIGN_UP)
//    public ResponseEntity<Void> sendSignUpEmail(
//            @RequestBody SendSignUpEmailRequest sendSignUpEmailRequest) {
//        authService.sendSignUpEmail(sendSignUpEmailRequest);
//        return ResponseEntity.ok().build();
//    }

    /* 비밀번호 변경 인증코드 이메일 발송 API */
//    @PostMapping(value = "/verification", params = PASSWORD_CHANGE)
//    public ResponseEntity<Void> sendPasswordChangeEmail(
//            @RequestBody SendPasswordChangeEmailRequest sendPasswordChangeEmailRequest) {
//        authService.sendPasswordChangeEmail(sendPasswordChangeEmailRequest);
//        return ResponseEntity.ok().build();
//    }


    /* 비밀번호 변경 API */
//    @PostMapping("/password")
//    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
//        authService.changePassword(changePasswordRequest);
//        return ResponseEntity.ok().build();
//    }


}
