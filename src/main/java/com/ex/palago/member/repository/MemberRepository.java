package com.ex.palago.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ex.palago.member.model.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByUsername(String username);
}
