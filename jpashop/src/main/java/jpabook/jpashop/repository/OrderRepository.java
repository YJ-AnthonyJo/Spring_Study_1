package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch){ //jpql로 한다.
        return em.createQuery("select o from Order o join o.member m" +
                " where o.status = :status" +
                " and m.name like :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setMaxResults(1000) //최대 1000건.
                .getResultList();
        //jpa에서는 join으 이렇게 한다.
        //order와 관련딘 것을 이렇게 한다. 객체이기 때문에 참조와 비슷하게 join한다.
        //orde와 order와 관련된 member를 join한다.
    }
}
