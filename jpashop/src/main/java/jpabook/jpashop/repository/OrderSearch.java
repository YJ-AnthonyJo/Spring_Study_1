package jpabook.jpashop.repository;

import jpabook.jpashop.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {

    private String memberName; //회원 이름, 기본 NULL
    private OrderStatus orderStatus; // 주문 상태[ORDER, CANCEL] 기본 NULL

    //위 파라마터 값이 있으면, where절이 만들어져야한다.

}
