package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class MemberRepositoryTest_Before {

    @Autowired
    MemberRepository_Before memberRepository;

    @Test
    @Transactional
//    @Rollback(value = false) //test에서 Transaction이 끝나도 rollback안하고 커밋한다.
    public void testMember(){
        //given
        Member member = new Member();
        member.setName("memberA");

        //when
        Long savedId = memberRepository.save(member);
        Member findMember = memberRepository.find(savedId);

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getName()).isEqualTo(member.getName());
        Assertions.assertThat(findMember).isEqualTo(member);
        // find한 거랑, 원래 것이랑 같을까? -> 같다.. 와우..
        System.out.println("(findMember==member) : " + (findMember==member));
        //같은Transaction안에서 실행했기에 영속성 콘텍스트(?)가 똑같다.
        //같은 영속성 Context에서는 ID값이 같으면 같은 Entity로 식별한다..
        // 같은 객체를 반환한다는 것인가? 신기하네
        //일차 캐시라고 불리는 것에서 (영속성 컨텍스트에서 관리되는) 같은 것이 있기에, 기존에 관리되던 것이 나온 것
        // 더 알아보기 : 영속성 Context, Entity
        // select 쿼리조차 안나갔다. 영속성 컨텍스트에 있다 -> 거기에서 뽑아왔다.
    }
}