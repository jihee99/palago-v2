package com.ex.palago.auth.controller;

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

    @PostMapping("/new")
    public ResponseEntity<Void> signUp(@RequestBody SignupRequest signUpRequest){
        log.info("{}", signUpRequest);
        authService.signUp(signUpRequest.getUsername(), signUpRequest.getPassword(), signUpRequest.getName(), signUpRequest.getPhone());
        return ResponseEntity.ok().build();
    }


}
