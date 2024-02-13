package com.ex.palago.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	// @Column(name = "username")
	private String username;
	@Column(nullable = false)
	private String name;
	private String password;
	private String phone;
	private String email;
	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	private Role role;
	@CreatedDate
	private LocalDateTime registerDate;
	private Boolean status;

	public String getRoleKey() {
		return role.getKey();
	}

	public static Member createMember(String username, String name, String password, String phone, Role role) {
		Member member = new Member();
		member.setUsername(username);
		member.setName(name);
		member.setPassword(password);
		member.setPhone(phone);
		member.setRole(role);
		member.setRegisterDate(LocalDateTime.now());
		member.setStatus(Boolean.TRUE);
		return member;
	}

}
