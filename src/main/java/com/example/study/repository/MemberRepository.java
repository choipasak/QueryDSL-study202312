package com.example.study.repository;

import com.example.study.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>,
        MemberRepositoryCustom {
    // 상속을 2개 받을 수 있음.
    // 하나의 인터페이스로 모든 기능을 다 사용 가능하게 된다.
    // 인터페이스는 다중 상속이 가능하기 때문에 이런 기능사용이 가능해짐.
    
    // 동적 SQL : 값에 따라서 쿼리문을 바꿔 끼는 것
    // 예시 : 검색(제목, 작성자, 내용)에 따라 달라지는 쿼리문의 내용
    //


}
