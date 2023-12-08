package com.example.study.repository;

import com.example.study.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    // 객체화 시키려고 하는 것임 - SOLID 법칙 때문 (강력한 객체 지향을 위한 법칙)
    List<Member> findbyName(String name);
    // QQQQQQQQQQQQQQQQQQQQ - 왜 바로? -> 위의 이유


    List<Member> findUser(String nameParam, Integer ageParam);


}
