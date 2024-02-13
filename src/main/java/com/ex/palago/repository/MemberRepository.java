package com.ex.palago.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ex.palago.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Member findByUsername(String username);
}
