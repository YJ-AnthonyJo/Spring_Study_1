package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    // order테이블에 있는 member라는 필드에 의해서 매핑된 것이라는 명시
    //이제 아래 것은, 읽기전용이 된다.
    //이 값을 변경한다고 해서 FK가 변경되지는 않는다.
    private List<Order> orders = new ArrayList<>();
    //mappedBy의 의미(내 생각) : Order에 있는 member가 자신의 객체(현재 Member 자신의 객체)와 같은 것을 선택한다
    /**
     * 즉, Member member1 = new Member();
     * member1.setName("tony");
     * ...
     * 으로 설정하였다면, orders에는 자동으로 Order 객체들 중, member attribute에 해당하는 Member객체가 자신의 Member 객체인
     * member1과 같은 것들을 넣을 것이다.
     *
     * DB에서는 하나의 FK로 특정 member가 가지는 order을 조회할 수도 있고,
     *  select * from member m join order o on m.order_id = o.order_id;
     * 반대로 order가 가지는 member를 조회할 수 있다.
     *  select * from order o join member m on o.order_id = m.order_id;
     *
     * 하지만, 객체에서는 그렇지 못하다. 이들의 연관관계는 단방향밖에 되지 않는다.
     */
    /* 내가 든 예시와 설명(아직은 확신하지 못한다.)
    RDB에서는 양방향으로, 하나의 FK만 있으면 어떤 경우든 상관이 없는데, 이것을 ORM으로 가져와서 객체로 엮으려고 할때, 단방향이라는 것이 발생.

    객체는 서로를 참조할 때, 단방향밖에 되지 않음.
    하나의 객체가 하나의 레코드를 담당하게 된다. 이때, 해당 table로 생성된 모든 레코드를 탐색할 수는 없다. 객체는 독립적이고(?), 생성된 모든 객체가 실제 레코드들처럼 모여있는 것이 아니기에.
    //틀림.. JPQL로 해당 Entity를 대상으로 쿼리수행가능.
    //아.. 이렇게 이해하면되겠다.(아래내용 무시하고) 객체지향의 장점을 이용하기 위해서 1쪽에 리스트(컬렉터)로 연결해둠.
    //-> 즉, 모든 객체를 탐색할 수 없어서 리스트로 마련한 것이 아니라, 객체지향의 장점을 이용하기 위함임.  하지만 이것도 불확실하다는 사실ㅋㅋㅋ
    따라서 1:M의 관계에서, M쪽은 단순히 1에 대한 하나의 객체정보만을 가지면 되지만,
    1쪽은 리스트로, M쪽의 객체들 중 본인객체를 FK로 가진(본인 객체(레코드)와 연관관계를 가진) M쪽의 객체들을 저장해야한다. 따라서 단방향의 연관관계가 생성되는 것이다.

    이때, 필요에 따라 단방향 조회만 필요하다면, 굳이 객체사이에서 양방향으로 설정하지 않아도 된다. 단방향만 설정하여도 된다.

    member와 team을 예시로 들자면
    member는 하나의 team을 가지고, team은 여러개의 member를 가지기에 1(team):M(member)의 관계이다. 당연히 fk는 member가 가진다.

    member객체는 하나의 레코드를 나타낸다.
    member1는 자신이 속한 팀의 정보를 가진 team1 객체를 가진다.
    member.team = team1;

    반면, team1은 member1을 비롯하여 member의 객체들 중, team attribute로 자신을 가지는 것들을 리스트에 저장한다.
    team1.members = { member1, member2, member5... }; //야매코드
    // member1.team == member2.team == member5.team  == team1
     */
}
