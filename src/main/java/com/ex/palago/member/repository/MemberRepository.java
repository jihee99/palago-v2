package com.ex.palago.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ex.palago.member.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Member findByUsername(String username);
}
