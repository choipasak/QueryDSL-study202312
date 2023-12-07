package com.example.study.repository;

import com.example.study.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    // 객체화 시키려고 하는 것임
    List<Member> findbyName(String name);
}
