package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
/**
 * ItemService는 ItemRepository에 단순히 위임만 하는 것.
 * 개발이 단순하게 끝난다.
 * 가끔씩은, 이렇게 단순 위임만 하는 것이 의미가 있을까를 고민하는 것도 필요하다.
 * -> controller에서 repository에 바로 접근하여 써도 문제는 없다.
 */
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional // 오버라이드. readOnly의 기본값은 false이다.
    //readOnly이면, 저장이 불가능하다.
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    //여기에는 @Transactional이 없으니, readOnly=true가 적용된다.
    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }
}
