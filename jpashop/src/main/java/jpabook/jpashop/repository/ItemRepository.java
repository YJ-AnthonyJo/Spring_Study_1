package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){
        if(item.getId()==null){
            //데이터를 저장할 때(jpa에 저장하기 전까지는)는 id가 없다.(persist되기 전까지)
            //-> id가 없다는 것은, 완전 새로운 item 객체라는 것을 의미한다.
            em.persist(item); //신규등록
        } else{
            //id가 존재. 이미 있는 item객체임 -> 업데이트가 맞음.
            em.merge(item); //업데이트 비슷한 것. 뒤의 웹 어플리케이션 파트에서 설명할 것.
        }
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
