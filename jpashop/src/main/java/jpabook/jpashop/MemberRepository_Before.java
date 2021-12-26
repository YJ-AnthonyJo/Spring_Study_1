package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository_Before {
    @PersistenceContext //Spring Container에서 자동으로 주입해준다.
    //spring-boot-starter-data-jpa 라이브러리를 implemenataion하면서, EntityManager를 자동생성한다.
    //application.properties를 기반으로 EntityManager가 생성된다.
    private EntityManager em;

    public Long save(Member member){
        em.persist(member);
        return member.getId();
        //id만 반환하는 이유 : 영한님 개인의 원칙
        //command랑 query랑 분리해라(..?)
        //-> 사이드이펙트를 일으킬 가능성이 있는 커멘드는 리턴값을 거의 만들지 않는다.
        //대신, id정도는 반환하여 조회가능하도록 한다.
    }

    public Member find(Long id){
        return em.find(Member.class, id); // -> guide보고 이해해보자.
    }
}
