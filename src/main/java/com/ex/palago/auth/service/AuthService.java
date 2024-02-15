package com.ex.palago.auth.service;

import com.ex.palago.auth.model.request.SendSignUpEmailRequest;
import com.ex.palago.auth.model.request.SignInRequest;
import com.ex.palago.auth.model.response.SignInRequestValidationResult;
import com.ex.palago.member.model.Member;
import com.ex.palago.member.model.Role;
import com.ex.palago.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{8,20}$";

	@Transactional
	public void signUp(String username, String password, String name, String phone) {
		validateMemberNotExist(username);
		validatePasswordForm(password);

		Member newMember = Member.createMember(username, name, bCryptPasswordEncoder.encode(password), phone, Role.USER);
		log.info("{}", newMember);

		memberRepository.save(newMember);
	}

	@Transactional(readOnly = true)
	public SignInRequestValidationResult validateSignInRequest(SignInRequest signInRequest) {

		log.info("{}", signInRequest);
		Member member = memberRepository.findByUsername(signInRequest.getUsername()).orElseThrow();
//				.orElseThrow(() -> MemberNotFoundException.EXCEPTION);

		validateMatchesPassword(signInRequest.getPassword(), member.getPassword());

		return new SignInRequestValidationResult(member.getRoleKey());
	}

//	@Transactional
//	public void sendSignUpEmail(SendSignUpEmailRequest request) {
//		validateMemberNotExist(request.getEmail());
//		emailVerificationService.sendVerificationCodeByEmail(request.getEmail());
//	}



	private void validateMemberNotExist(String username) {
		if (memberRepository.findByUsername(username).isPresent()) {
			log.info("[회원가입 실패] 중복 아이디 회원가입 시도 -> id : " + username);
			log.info("{}", "DuplicateIdException");
//			throw DuplicateIdException.EXCEPTION;
		}
	}

	private void validateMemberExist(String username) {
		if (memberRepository.findByUsername(username).isEmpty()) {
			log.info("[비밀번호 변경 시도 실패] 가입하지 않은 회원이 비밀번호 변경 요청 -> email : {}" + username);
//			throw PasswordChangeMemberNotExistException.EXCEPTION;
		}
	}


	private void validatePasswordForm(String password) {
		if (!password.matches(PASSWORD_REGEX)) {
			log.info("{}", "PasswordFormatMismatchException");
//			throw PasswordFormatMismatchException.EXCEPTION;
		}
	}


	private void validateMatchesPassword(String rawPassword, String encodedPassword) {

		if (!bCryptPasswordEncoder.matches(rawPassword, encodedPassword)) {
			log.info("{}", "PasswordIncorrectException");
//			throw PasswordIncorrectException.EXCEPTION;
		}
	}

	public Member getCurrentUser(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return (Member) authentication.getPrincipal();
	}


	private void validatePasswords(String newPassword, String confirmationPassword) {
		if (newPassword == null || confirmationPassword == null) {
			log.info("{}", "PasswordNullException");
//			throw PasswordNullException.EXCEPTION;
		}

		validateRequestPasswordsAreEqual(newPassword, confirmationPassword);
		validatePasswordForm(newPassword);
	}

	private void validateRequestPasswordsAreEqual(String newPassword, String confirmationPassword) {
		if (!Objects.equals(newPassword, confirmationPassword)) {
			log.info("{}", "PasswordsNotEqualException");
//			throw PasswordsNotEqualException.EXCEPTION;
		}
	}
}
