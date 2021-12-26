package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     * @param memberId : 주문하는 회원 id
     * @param itemId : 주문하는 item의 id
     * 즉, 인자로 id만 들어간다. -> id로 객체 찾음.(애초에 findOne메소드가 이 id를 기반으로 찾는 것.)
     * @param count : 주문 수량.
     * @return
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count){
        //원래 계획했던 것은 하나의 order에 다양한 orderitem이 들어가는 것이었으나, 예제를 단순화하기 위해서 하나만 넘기는 것으로 함.

        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);
        /**
         * CascadeType.ALL옵션으로 이것 하나만 save를 진행해도 나머지 것 orderItem, delivery등이 자동으로 save된다.
         * order를 persist하면, 그 컬렉션 안에 있는 orderitem들도 다 persist를 날려준다.
         */

        return order.getId();
    }

    /**
     * 주문 취소
     * @param orderId : 마찬가지로 id만 들어온다.
     */
    @Transactional
    public void cancelOrder(Long orderId){
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();

        /**
         * 원래는 바뀐 것들 다 업데이트 쿼리를 날려주어야한다.
         * 하지만 jpa는 바뀐 것들을 알아서 찾아(더티체킹?) 업데이트 쿼리를 날려준다.
         * 이 경우에는 order.cancel메소드 안에서 order의 Status 변화가 생겼기에, order에 대한 업데이트쿼리가 날라가게된다.
         * 또 order.cancel에 있는 orderitem.cancel -> getItem().addstock을 통해 stock 원상복귀 쿼리가 자동으로 날라가게 된다.
         *
         * 이것이 jpa를 사용하는 것에 있어 굉장한 장점이다.
         */
    }

    //검색 -> 나중에 구현할 것임.
    /*public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAll(orderSearch);
    }*/

}
