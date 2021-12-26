package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery") //연관관계 주인인 Order에서 delivery로 mapped by된다.
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(value = EnumType.STRING)
    // EnumType.ORDINARY : ENUM이 숫자로 들어감. -> ENUM이 변경되면, 문제생김
        // 만약, 준비와 배송 사이에 다른 단계가 생겨서 READY, XXX, COMP가 되면 원래 2였던 COMP가 3으로 밀리는데,
        // 이때 기존 것들은 업데이트가 안되므로(되기는 하지만, 실수할 여지가 있음) 오류가 생길 수 있다.
        //따라서 STRING을 사용한다.
    // EnumType.STRING : ENUM이 해당 이름으로 들어간다.(약간 HASHTABLE같은느낌?) -> ENUM자체가 변경되어도, 영향을 주지 않는다.
    // 영한 : ORDINARY는 절대 쓰면 안되고 이 STRING을 써야한다.
    private DeliveryStatus status; //READY(준비), COMP(배송)
}
