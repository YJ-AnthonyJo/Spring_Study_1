package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

/*
순수한 단위테스트로 진행하는 것이 아니라, jpa가 실제로 도는 것까지 보는 것이 중요하기에, 메모리 모드로 db까지 엮어서 테스트할 것이다.

 */
@SpringBootTest
@Transactional // for RollBack용.
/*
같은 Transaction안에서 Entity의 id(PK)값이 같으면, 같은 영속성 컨텍스트에서 똑같은 객체로 관리된다.
하나로 관리된다.
 */
//Test에 @Transactional이 있으면, 기본적으로 Rollback한다. -> insert문이 존재하지 않는다. 영속성 컨텍스트에서 끝난다(?)
//다른 것(repository, service 등)에서의 @Transactional은, rollback이 없다.
//이 rollback을 하지 않고 insert까지 하고자 하면, 원하는 메소드에 @Rollback(falae)를 붙이면 된다.
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em; // for check insert query.

    /*
    given when then 테스트 방식
    given이 주어졌을 때, when을 실행하면, then이 되는 것을 검증해라.
     */


    @Test
//    @Rollback(value = false) // -> 회원가입 테스트에서 rollback을 하지 않는 경우의 셜정방법.
    public void 회원가입(){
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long saveId = memberService.join(member);

        //then
//        em.flush(); // rollback을 하지만, insert 쿼리가 나가는 것을 보고 싶을 때 사용. //강제로 쿼리 날림.
        //쿼리가 나가서 db가 변경되지만, rollback을 해서 원상복귀됨.
        //rollback에서 insert 쿼리가 나가지 않는 것은, 최적화 때문(?).
        // : rollback하는데 굳이 db를 변경할 필요가 없다 -> insert쿼리 안날린다.
        //즉 rollback은, db를 원상복구시킨다는 것. 이 개념 하에, jpa가 알아서 query 날림여부를 결정한 것.
        assertEquals(member, memberRepository.findOne(saveId));
    }
    
    @Test
//    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외(){
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
//        memberService.join(member2); //예외가 발생해야한다.
        //예외가 발생하면 try-catch가 없기에 쭉쭉 밖(메소드, 클래스 밖)으로 나간다.
        //따라서 예외가 발생하면, 이 라인 아래쪽 라인은 실행되면 안된다. 이 메소드(중복_회원_예외)밖으로 나가야한다.

        //try catch를 해준다~ -> 만약 excetption이 발생하지 않으면, fail이 실행될 것이다.
        //음.. try의 memberService.join(member2);다음에 fail이 들어가도 되지 않나?
//        try{
//            memberService.join(member2);
//        }catch (IllegalStateException e){
//            return;
//        }
//
        //위코드 : 복잡하다. -> 아래코드와, @Test(expected = IllegalStateException.class)으로 설정하자.
        // -> 에러 터져서 나간 것이 IllegalStateException이면, 성공으로 간주한다.
        // 원래 : exception이 뜨면, 실패로 간주한다.
        // 하지만 이렇게 하면, 이 중복_회원_예외()메소드에서 나간 exception이 IllegalStateException이면 성공으로 여긴다.
//        memberService.join(member2);

        //위 코드 : ㅋㅋㅋㅋㅋ junit4에서만 되는 것 같다. 지금은 Test어노테이션에 들어가는 인자가 존재하지 않는다.
        //기억상으로, assertThrows를 사용한다.
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));


        //then
        //fail("예외가 발생하여 이 라인이 실행되면 안되었었다~");

        assertEquals(e.getMessage(), "이미 존재하는 회원입니다.");
        //물론,, 스프링부트 입문강좌에서는 assertThat을 사용하였다. -> 이 둘의 차이를 알아보자.

    }
}