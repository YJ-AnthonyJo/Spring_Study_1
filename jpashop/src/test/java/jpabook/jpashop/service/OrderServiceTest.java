package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문() {
        //given
        Member member = createMember();

        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        Assertions.assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER");
        //본래 : Assert.assertEquals(message, expected, actual)인데 junit5로 넘어오면서 바뀐듯
        Assertions.assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품의 종류 수가 정확해야한다.");
        Assertions.assertEquals(10000 * orderCount, getOrder.getTotalPrice(), "주문 가격은 가격 * 수량이다.");
        Assertions.assertEquals(8, book.getStockQuantity(), "주문 수량만큼 재고가 줄어야한다.");
    }

    @Test
    public void 상품주문_재고수량초과_예외발생(){
        //given
        Member member = createMember();
        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 11; //예외 터짐

        //when
//        orderService.order(member.getId(), book.getId(), orderCount);
        NotEnoughStockException e = Assertions.assertThrows(NotEnoughStockException.class, () -> orderService.order(member.getId(), book.getId(), orderCount));

        //then
        Assertions.assertEquals(e.getMessage(), "need more stock");
    }

    @Test
    public void 주문취소(){
        //given
        Book book = createBook("시골JPA", 10000, 10);
        Member member = createMember();

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        //주문 취소를 테스트하는 것 -> 이 order까지가 given에 들어가는 것이 맞다.
        //데이터 세팅하고, 주문한 것까지 준비한 것

        //when : 실제로 테스트하고 싶은 것.
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);
        Assertions.assertEquals(OrderStatus.CANCEL, getOrder.getStatus(), "주문 취소시 상태는 CANCEL이다.");
        Assertions.assertEquals(10, book.getStockQuantity(), "주문이 취소된 상품은 그만큼 재고가 증가해야한다.");
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }
}