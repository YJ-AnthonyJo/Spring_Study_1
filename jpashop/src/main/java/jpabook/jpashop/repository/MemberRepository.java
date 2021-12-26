package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

//    @PersistenceContext //-> 대신 @Autowired, 이 대신, @RequiredArgsConstructor사용
    /*
    EntitiyManager : Autowired로 안되고, PerssitenceContext 어노테이션으로 가능.
    -> Spring Boot에서, Autowired로도 가능하게 해줌.
    -> 이 것 대신, @RequiredArgsConstructor로 사용하자.
     */
    private final EntityManager em;
    //-> 엔티티 메니저 만들어서 em에 주입해줌.

//    @PersistenceUnit //엔티티 메니저 팩토리를 직접 주입하고자 할때.
    //엔티티메니저팩토리?? : 더알아보자.
//    private EntityManagerFactory emf;

    public void save(Member member){
        em.persist(member); //persist를 한 순간, 영속성 컨텍스트로 저장되는데, 이때 member의 id는 자동으로 채워지게된다.(pk로 설정하였다)
        //이를 @GenerateValue에서 설정해주는 것이다. => 이 id값이 항상 존재한다는 것이 보장됨
        //영속성 컨텍스트에서 key : value가 되는데(음..), 이때 key가 이 PK값이 들어가며, 코드 상으로는 GenerateValue가 설정된 친구다.
        //영속성 컨텍스트 : hashtable같은 것인가? 더 알아보자.
    }

    public Member findOne(Long id){
       return em.find(Member.class, id);
    }

    public List<Member> findAll(){ //회원목록 조회를 위한 것.
        return em.createQuery("select m from Member_Before m", Member.class)
                .getResultList();
        // JPQL작성.
        // em.createQuery()의 인자
        //  첫번째 : JPQL
        //  두번째 : 반환타입 -> 이해 안됨 -> 자세히 알아보자.

        //참고 : inline화 단축키 : ctrl alt n

        /**JPQL vs SQL -> JPQL에 대한 자세한 것은 기본편이나, 책을 참고하자.
         * JPQL : Entity를 대상으로 쿼리를 한다.
         * SQL : 테이블을 대상으로 쿼리를 진행한다.
         */
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
