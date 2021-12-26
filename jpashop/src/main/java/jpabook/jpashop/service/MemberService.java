package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // jpa의 데이터 변경, 로직은 Transaction안에서 실행되어야한다.
/* 이 Transactional 어노테이션은, javax말고 springframework거를 사용한다.
이렇게 annotation을 클래스 level에 걸면, public으로 되어있는 것 transaction에 걸리게 된다.
    // 아래 메소드 중 읽어들이는 메소드 : 이 readOnly = true로 주면, jpa가 이 함수(조회)의 성능을 최적화한다.
    // 읽기가 아닌 다른 기능을 수행하는 것에서는 절대 이 readOnly = true를 주면 안된다. 데이터 변경이 안된다.
    여기서는 read만 수행하는 메소드가 더 많다.
    => class 앞 어노테이션에 있는 Transactional은, 모든 public 메소드에 적용된다.
    단, join메소드와 같이, 메소드에 따로 Transaction을 지정하면, 그것으로 사용한다.
    즉 기본은 calss밖에 있는 것, 우선순위 높은 것은 메소드에 직접 붙은 것이다.
 */
//@AllArgsConstructor //모든 필드에 대하여 constructor를 만들어준다.
@RequiredArgsConstructor //final이 붙은 필드에 대해서만 constructor를 만들어준다.
public class MemberService {

//    @Autowired //필드 인젝션.
//    private MemberRepository memberRepository;
    private final MemberRepository memberRepository; //이와같이 final로 설정하는 것을 권장한다.
    //final 키워드 : 저장된 값 변경 불가.
    //할당 없으면, 오류 : constructor에 이 변수에 할당해주는 코드가 없으면, 오류난다. -> 컴파일 시점에 constructor를 제대로 구성하였는지 확인이 가능하다.
    //-> 확인 위해 사용하자.

    //나아가, lombok의 AllArgsConstructor : 모든 것에 대하여 constructor를 만들어준다.
    //나아가, RequiredArgsConstructor : final이 붙은 필드에 대해서만 constructor를 만들어준다.



    // setter 인젝션. : 요즘 잘 안쓴다 : 개발하는 중간에 이 memberRepository를 변경할 경우는 존재하지 않는다.
    // -> 이상적인 방법은 아니다. => Constructor(생성자) 인젝션을 사용한다.
//    @Autowired // 이 Annotaion은 왜 붙여지는지 모르겠네...
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    // Constructor 인젝션 : 인스턴스 만들때, 이러한 것이 인젝션되어야한다는 것을 알려줌 -> 편리함.
    // but 아래와 같은 코드를 작성하는 것은 번거로움.
    // => 생성자가 하나만 있는 경우,@Autowired 어노테이션이 없어도 스프링이 자동으로 인젝션을 해준다..????????
//    @Autowired // 이 Annotaion은 왜 붙여지는지 모르겠네...
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }


    //멤버에서 구현할 기능 : 회원가입, 회원 목록 조회

    /** 회원가입 join메소드
     *
     * @param member
     * @return
     */
    @Transactional // readOnly의 기본 값 : false.
    public Long join(Member member){
        //동일한 이름 중복가입 안됨.
        validateDuplicateMember(member); //중복회원 검증
        memberRepository.save(member);
        return member.getId(); //영속성 컨텍스트로 올라간 member는 id가 존재함 -> 여기서 id를 뽑아씀.
    }

    private void validateDuplicateMember(Member member) {
        //이름이 중복 -> 예외 터트리기
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
        // 이렇게 검증해도 오류날 수 있다. : 정말 동시에 join을 한 경우이다.
        //-> db의 제약조건으로 uniqe를 걸어주어 최후의 방어선을 형성하도록 하자.
    }

    //회원 전체 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    //한건 조회
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}
