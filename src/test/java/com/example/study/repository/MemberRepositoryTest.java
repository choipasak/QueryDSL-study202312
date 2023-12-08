package com.example.study.repository;

import com.example.study.entity.Member;
import com.example.study.entity.QMember;
import com.example.study.entity.Team;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.study.entity.QMember.member;
import static com.example.study.entity.QTeam.team;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    EntityManager em; // JPA 관리 핵심 객체
    
            // QueryDSL로 쿼리문을 작성하기 위한 핵심 객체
    JPAQueryFactory factory;

    @BeforeEach
    void settingObject() {
        factory = new JPAQueryFactory(em); // 직접 em으로 관리하겠다는 의미
    }

    @Test
    void testInsertData() {

//        Team teamA = Team.builder()
//                .name("teamA")
//                .build();
//        Team teamB = Team.builder()
//                .name("teamB")
//                .build();

//        teamRepository.save(teamA);
//        teamRepository.save(teamB);

//        Member member1 = Member.builder()
//                .userName("member1")
//                .age(50)
//                .team(teamA)
//                .build();
//        Member member2 = Member.builder()
//                .userName("member2")
//                .age(60)
//                .team(teamA)
//                .build();
//        Member member3 = Member.builder()
//                .userName("member3")
//                .age(70)
//                .team(teamB)
//                .build();
//        Member member4 = Member.builder()
//                .userName("member4")
//                .age(80)
//                .team(teamB)
//                .build();
//
//        memberRepository.save(member1);
//        memberRepository.save(member2);
//        memberRepository.save(member3);
//        memberRepository.save(member4);

        Member member5 = Member.builder()
                .userName("member9")
                .age(50)
//                .team(teamA)
                .build();
        Member member6 = Member.builder()
                .userName("member10")
                .age(50)
//                .team(teamA)
                .build();
        Member member7 = Member.builder()
                .userName("member11")
                .age(30)
//                .team(teamB)
                .build();
        Member member8 = Member.builder()
                .userName("member12")
                .age(80)
//                .team(teamB)
                .build();
        memberRepository.save(member5);
        memberRepository.save(member6);
        memberRepository.save(member7);
        memberRepository.save(member8);
    }

    @Test
    @DisplayName("testJPA")
    void testJPA() {

        List<Member> members = memberRepository.findAll();

        members.forEach(System.out::println);

    }

    @Test
    @DisplayName("testJPQL")
    void testJPQL() {
        //given
        String jpqlQuery = "SELECT * FROM Member m WHERE m.userName = :userName";

        //when
        // EntityManager를 활용하여 직접 jpql을 작성하고, 파라미터를 설정할 수 있음.
        Member foundMember = em.createQuery(jpqlQuery, Member.class)
                .setParameter("userName", "member2")
                .getSingleResult();

        //then
        assertEquals("teamA", foundMember.getTeam().getName());

        System.out.println("\n\n\n");
        System.out.println("foundMember = " + foundMember);
        System.out.println("foundMember.getTeam() = " + foundMember.getTeam());
        System.out.println("\n\n\n");
    }
    
    @Test
    @DisplayName("TestQueryDSL")
    void testQueryDSL() {
        //given
        // 미리 객체가 생성되어 있어서 그냥 객체 바로 가져오면 된다 -> new로 생성하지 않아도 된다.
        QMember m = member;
        
        //when
        Member findMember = factory.select(m)
                .from(m)
                .where(m.userName.eq("member1"))
                .fetchOne();

        //then
        assertEquals(findMember.getUserName(), "member1");
    }

    @Test
    @DisplayName("search")
    void search () {
        //given
//        QMember m = member;
        String searchName = "member2";
        int searchAge = 20;
        //when
        Member foundMember = factory.selectFrom(member)
                .where(
                        member.userName.eq(searchName), member.age.eq(searchAge) // 밑에줄과 동일 표현
//                        m.userName.eq(searchName).and(m.age.eq(searchAge))
                )
                .fetchOne();

        //then
        assertNotNull(foundMember);
        assertEquals("teamA",foundMember.getTeam().getName());

        /*
         JPAQueryFactory를 이용해서 쿼리문을 조립한 후 반환 인자를 결정합니다.
         - fetchOne(): 단일 건 조회. 여러 건 조회시 예외 발생. (= 단언(assert)와 비슷)
         - fetchFirst(): 단일 건 조회. 여러 개가 조회돼도 첫 번째 값만 반환
         - fetch(): List 형태로 반환
         * JPQL이 제공하는 모든 검색 조건을 queryDsl에서도 사용 가능
         *
         * member.userName.eq("member1") // userName = 'member1'
         * member.userName.ne("member1") // userName != 'member1'
         * member.userName.eq("member1").not() // userName != 'member1'
         * member.userName.isNotNull() // 이름이 is not null
         * member.age.in(10, 20) // age in (10,20)
         * member.age.notIn(10, 20) // age not in (10,20)
         * member.age.between(10, 30) // age between 10, 30
         * member.age.goe(30) // age >= 30
         * member.age.gt(30) // age > 30
         * member.age.loe(30) // age <= 30
         * member.age.lt(30) // age < 30
         * member.userName.like("_김%") // userName LIKE '_김%'
         * member.userName.contains("김") // userName LIKE '%김%'
         * member.userName.startsWith("김") // userName LIKE '김%'
         * member.userName.endsWith("김") // userName LIKE '%김'
         */
    }

    @Test
    @DisplayName("결과 반환하기")
    void testFetchResult() {
        // fetch
        List<Member> fetch1 = factory.selectFrom(member).fetch();
        System.out.println("\n\n ======== fetch ========");
        fetch1.forEach(System.out::println);
        System.out.println("======== fetch ======== \n\n");

        // fetchOne : 하나의 결과값을 반환하기 때문에 조건절이 필요
        Member fetch2 = factory.selectFrom(member)
                .where(member.id.eq(3L))
                .fetchOne();
        System.out.println("\n\n ======== fetch ========");
        System.out.println("fetch2 = " + fetch2);
        System.out.println("======== fetch ======== \n\n");

        // fetchFirst: 리미트가 걸리기 때문에 첫번째 결과만 반환된다.
        Member fetch3 = factory.selectFrom(member)
                .fetchFirst();
        System.out.println("\n\n ======== fetch ========");
        System.out.println("fetch3 = " + fetch3);
        System.out.println("======== fetch ======== \n\n");

        // fetchCount: 조회된 쿼리의 행의 개수를 알려주는 거였는데 deprecated 되버렸음.
        long fetch4 = factory.selectFrom(member)
                .fetchCount();
        System.out.println("\n\n ======== fetch ========");
        System.out.println("fetch4 = " + fetch4);
        System.out.println("======== fetch ======== \n\n");
    }
    
    @Test
    @DisplayName("QueryDSL custom 설정 확인")
    void queryDslCustom() {
        //given
        String name = "member4";
        //when
        List<Member> result = memberRepository.findbyName(name);

        //then
        assertEquals(1, result.size());
        assertEquals("teamB", result.get(0).getTeam().getName());
    }
    
    @Test
    @DisplayName("회원 정렬 조회")
    void sort() {
        //given

        //when
        List<Member> result = factory.selectFrom(member)
//                .where(원하는 조건)
                .orderBy(member.age.desc())
                .fetch();

        //then
        System.out.println("\n\n\n");
        System.out.println("result = " + result + "\n");
        System.out.println("\n\n\n");
    }

    @Test
    @DisplayName("queryDSL paging")
    void paging() {
        //given

        //when
        List<Member> result = factory.selectFrom(member)
                .orderBy(member.userName.desc())
                .offset(3)
                .limit(3)
                .fetch();
        //then
        System.out.println("\n\n\n");
        System.out.println("result = " + result + "\n");
        System.out.println("\n\n\n");

        assertEquals(result.size(), 3);
        assertEquals(result.get(1), "member6");
    }
    
    @Test
    @DisplayName("그룹 함수의 종류")
    void aggregation() {
        //given
        
        //when
        List<Tuple> result = factory.select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                .from(member)
                .fetch();

        //then
        Tuple tuple = result.get(0);
        assertEquals(tuple.get(member.count()), 8);
        assertEquals(tuple.get(member.age.sum()), 360);
        assertEquals(tuple.get(member.age.avg()), 45);
        assertEquals(tuple.get(member.age.min()), 10);
        assertEquals(tuple.get(member.age.max()), 80);

        System.out.println("\n\n\n");
        System.out.println("result = " + result + "\n");
        System.out.println("tuple = " + tuple + "\n");
        System.out.println("\n\n\n");
    }
    
    @Test
    @DisplayName("GROUP BY, HAVING")
    void testGroupBy() {
        //given

        //when
        List<Long> result = factory.select(member.age.count())
                .from(member)
                .groupBy(member.age)
                .having(member.age.count().goe(2))
                .orderBy(member.age.asc())
                .fetch();
        //then
        assertEquals(result.size(), 3);

        System.out.println("\n\n\n");
        result.forEach(System.out::println);
        System.out.println("\n\n\n");

    }

    @Test
    @DisplayName("join 해보기")
    void join() {
        //given


        /*
            Oracle DB의 경우 Oracle의 조인 문법도 사용이 가능하다.
            SELECT * FROM employees, departments WHERE ~~ 이런식으로 작성했는데,
            select().from(employees, departments).where(~~) 이런 방식으로도 작성이 가능하다.
         */

        //when
        List<Member> result = factory.selectFrom(member)
                // join(기준 Entity.조인 대상 Entity, 별칭)
                .join(member.team, team) // inner join임
                .where(team.name.eq("teamA"))
                .fetch();

        //then
        System.out.println("\n\n\n");
        result.forEach(System.out::println);
        System.out.println("\n\n\n");
    }



    /*
        - 원래 작성하는 쿼리문
        ex) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조회, 회원은 모두 조회.
        SQL:
        SELECT m.*, t.*
        FROM tbl_member m
        LEFT OUTER JOIN tbl_team t
        ON m.team_id = t.team_id
        AND t.name = 'teamA';
        ============================
        JPQL:
        SELECT
        FROM Member m
        LEFT JOIN m.team t
        ON t.name = 'teamA';
        - 어차피 Entity인 Member 안에 Team이 있기 떄문에 조인문이 필요가 없다.
     */
    @Test
    @DisplayName("left outer join test")
    void leftJoinTest() {
        //given

        //when
        List<Tuple> result = factory.select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                .on(team.name.eq("teamA"))
                .fetch();
        //then
        System.out.println("\n\n\n");
        result.forEach(tuple -> System.out.println("tuple = " + tuple));
        System.out.println("\n\n\n");
    }

    @Test
    @DisplayName("sub query 사용하기(나이가 가장 많은 회원을 조회)")
    void subQueryTest() {
        //given
        // 같은 테이블에서 서브쿼리를 적용하려면 별도로 QClass의 객체를 생성해야 합니다.
        QMember memberSub = new QMember("memberSub");

        //when
        List<Member> result = factory.selectFrom(member)
                .where(member.age.eq(
                        // 나이가 가장 많은 사람을 조회하는 서브쿼리문
                        JPAExpressions // 서브쿼리를 사용할 수 있게 해 주는 클래스
                                .select(memberSub.age.max())
                                .from(memberSub)
                )).fetch();

        //then
        System.out.println("\n\n\n");
        result.forEach(System.out::println);
        System.out.println("\n\n\n");

    }

    @Test
    @DisplayName("나이가 평균 나이 이상인 회원을 조회")
    void subQueryGoe() {
        //given
        QMember m2 = new QMember("m2");
        //when
        List<Member> result = factory.selectFrom(member)
                .where(member.age.goe(
                        /*
                            - 알아둬야 할 점 (JPAExpressions)
                            from절을 제외하고, select와 where절에서 사용이 가능.
                            그럼 from절은 어떻게 사용 -> Native SQL 사용 || MyBastis || JDBC 템플릿 을 사용해야 함.
                            ㄴ> JPQL도 마찬가지
                            ! 아니면, 따로따로 두 번 조회도 사용.
                         */
                        JPAExpressions
                                .select(m2.age.avg())
                                .from(m2)
                ))
                .fetch();
        //then
        System.out.println("\n\n\n");
        result.forEach(System.out::println);
        System.out.println("\n\n\n");

        assertEquals(result.size(), 4);
    }
    
    @Test
    @DisplayName("동적 SQL 테스트")
    void dynamicQueryTest() {
        //given
        String name = null;
        int age = 30;
        //when
        List<Member> result = memberRepository.findUser(name, null);
        //then
        assertEquals(result.size(), 8);

        System.out.println("\n\n\n");
        result.forEach(System.out::println);
        System.out.println("\n\n\n");
    }

}