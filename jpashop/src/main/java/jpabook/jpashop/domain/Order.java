package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //아래의 생성메소드 사용을 강제하기 위함.
public class Order {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="member_id") // Foreign Key가 member_id가 된다.
    //JoinColumn : FK를 설정하는 것. @Id가 PK라면, 이것은 FK설정
    private Member member;
    /**연관관계를 기본값인 EAGER로 설정할 때.
     * <Order의 레코드가 100개라고 가정>
     * em.find로 order 중 하나만 가져오는 경우 : 상관 없다.
     * order 전부를 가져오는 경우 : JPQL로 select o from order o가 되고, 이는 SQL로 select * from order가 된다.
     *      select o from order o가 SQL로 join까지 포함해서 번역되지 않고(select * from order o join member m where o.member_id = m.member_id),
     *      그 자체로 select * from order로 번역된다.
     *      이렇게 order만 가지고 왔는데, 이를 객체로 매핑하려보니까, 문제가 생김 : member와의 연관관계를 매핑해주어야한다.
     *      이를 해결하고자, 모든 레코드(객체)에 해당하는 memeber를 연관관계 매핑해주기 위해서 100번의 단일 쿼리가 실행된다.
     *      내 추측 : select * from member m where m.member_id = {1~100}
     *
     * 이를 n + 1 문제라고 한다. : 첫번째 날린 쿼리(단순 select * from order)가 가져온 결과가 100이면, n을 100으로 치환하면 된다.
     * -> 즉, 100 + 1번의 쿼리가 발생하게 된다.
     * 여기서 1 : order를 가져오는 것.
     * n : 가져온 결과의 연관관계를 매핑하는 것.
     *
     * 즉 EAGER의 뜻은, JOIN해서 한번에 가져온다는 것이 아니라, 어떻게든 order를 조회하는 시점에서 member를 같이 조회하겠다는 것.
     * -> 난리난다.
     *
     * 지연로딩(LAZY)로 설정하면 order를 조회할 때 order만 가져온다. -> 이때 만약 "이 작업에서는 member를 지정해줄 필요가 있다"라고 하면,
     * fetch_join또는 엔티티 그래프 기능을 사용한다.
     *
     * 참고 JPQL : 모든 레코드를 객체변환해서 관리할 수는 없다. -> 현재 필요한 것만 변환해서 사용해야한다.
     * -> sql을 JPQL로 짜서, 내가 원하는 것만 객체변환할 수 있게한다.
     */


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // List -> OneToMany를 표현
    //Many의 대상 : OrderItem
    //OrderItem의 order attribute를 map시킴
    private List<OrderItem> orderItems = new ArrayList<>();
    /**Cascade.ALL : 잘못 이해한 것 같기도 하다. 우선 지금은 넘어가자.
     * Order객체를 만들어, 영속화시킬때(영속컨테이너로 넣을때, 즉 persist()할때)
     * orderItems에 있어야하는 모든 orderItem객체들을 persist한 이후에 Order객체를 persist해야한다.
     * Cascade.ALL은 이러한 과정을 자동화해준다.
     *
     * 즉, 원래 순서
     * orderItemA 객체 생성 -> 영속화
     * orderItemB 객체 생성 -> 영속화
     * orderItemC 객체 생성 -> 영속화
     * orderItems 컬렉션에 A~C저장
     * 이후 order 영속화
     *
     * Cascade.ALL의 순서
     * orderItemA 객체 생성
     * orderItemB 객체 생성
     * orderItemC 객체 생성
     * order영속화 : orderItem객체 영속화가 모두 포함되어있다.
     *
     * Cascade는 persist를 전파한다. -> orderItems에 있는 친구들을 다 자동으로 persist해준다.
     * delete할때도, 같이 지워버린다..?
     *      내 생각 : order를 persist할 때 생성된 영속화된 orderItem 객체들 -> 다른 곳에서 동일한 레코드로 매핑된 객체가 있을지라도, 그것과 이것은 다르다.(?불확실)
     *      -> 하나의 레코드 당 하나의 객체가 할당되는 것은 아닌 듯하다.
     *          즉, orderItem테이블에 A라는 레코드가 있을 때, 해당 레코드와 매핑된 영속 객체가 하나만 있는 것이 아니라(예시 : orderItemA), 다양하게 필요에 따라 존재하는 것 갈다.
     *          orderItemA_for_Order1, orderItemA_for_Order2..등등..
     *          : order1과 order2가 모두 orderItem테이블의 A레코드를 가질때(물론 이 예제에서 실제로 그러하지는 않지만,, cacade이해를 위해 가정해보자)
     *          order1의 orderItems에 존재하는 객체는 orderItemA_for_Order1이고,
     *          order2의 orderItems에 존재하는 객체는 orderItemA_for_Order2이고 등등..인 듯..
     *      -> order를 영속화할때 생성된 객체는 order안에서만 유효하다. -> order 영속 객체가 삭제될 때, 이 orderItem객체들도 같이 영속컨테이너에서 삭제해주는 듯하다.
     *
     *
     */



    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id") //Delivery와 Order의 연관관계에서 Order를 연관관계 주인으로 한다.
    private Delivery delivery;

    private LocalDateTime orderDate; //시간, 분, 초까지 다 있는 것
    //java 8부터, 어노테이션 안써도 hibernate가 알아서해준다.

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status; //주문상태 [ORDER, CANCEL]


    //==연관관계 메소드==//
    //이 연관관계 편의 메소드는 일반적으로 컨트롤하는 쪽이 가지고 있는 것이 좋다.
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem){
        orderItem.setOrder(this);
        orderItems.add(orderItem);
    }

    public void setDelivery(Delivery delivery){
        delivery.setOrder(this);
        this.delivery = delivery;
    }


//    원래코드??
//    public static void main(String[] args) {
//        Member member = new Member();
//        Order order = new Order();
//
////        member.getOrders().add(order); //본래 이 코드가 필요했었는데, 펠요없어졌다..?? : 왜 이 main을 사용하지 않는 것이지..?
//        order.setMember(member);
//    }

//    protected Order(){}//아래의 생성메소드 사용을 강제하기 위함.

    //==생성 메소드==//
    //논리적(?내가 만들어낸 표현..) 객체를 생성할 때 사용하는 메소드..
    //constructor와는 느낌이 다르다. 이 생성메소드는 논리적(비지니스적) 객체를 만들 때 사용하는 것이고,
    //constructor는, 객체를 생성할때 무조건적으로 따라야하는 것이다.

    /**
     * 아래와 같이 코드를 짜면, 연관관계를 모두 매핑을 하고, 상태와 주문정보도 세팅한다. -> 정리가 된다.
     * 밖에서 order를 만들때, setter로 지정해주는 것이 아니라, 이 메소드를 호출함으로서 깔끔하게하자.
     * -> 유지보수도 간단하다 : 주문방식 변경시, 이것만 바꾸어주면 된다.
     */
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        //'...'문법 : 가변인자 문법. 파라미터 중 가장 마지막으로 들어와야한다.
        //추측상, List<OrderItem> orderItems와 같지 않을까.. 싶다.
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER); // enum..
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비지니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel(){
        if(delivery.getStatus()==DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for(OrderItem orderItem : this.orderItems){
            orderItem.cancel();
        }
    }

    //==조회 로직==//

    /**
     * 전체 주문가격 조회
     */
    public int getTotalPrice(){
        int totalPrice = this.orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
        //위 코드는 아래 코드와 같다.

//        int totalPrice = 0;
//        for(OrderItem orderItem : this.orderItems){
//            totalPrice += orderItem.getTotalPrice();
//        }

        return totalPrice;
    }

}
