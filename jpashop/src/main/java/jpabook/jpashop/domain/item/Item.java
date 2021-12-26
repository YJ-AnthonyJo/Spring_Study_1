package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter //stockQuantity에 대해서는 Setter가 필요가 없다. stockQuantity를 이 엔티티 안에서 처리해주기 때문.
@Inheritance(strategy = InheritanceType.JOINED)
/**
 * single table : 한 테이블 안에 상속 관계를 다 때려넣기
 *          자세히 : 클래스 hierarchy(계층)당 하나씩 테이블을 지정. 즉, ItemType(실제로는 DType)으로 상속관계를 테이블에 명시하는 것 : 맞는지 모르겠음.
 * joined : ??
 * Table per class : 상속된 것 하나당 하나의 테이블 생성. 즉 Book, Album, Movie각각 테이블을 생성하게 된다.
 */
@DiscriminatorColumn(name = "dtype") //상속관계의 hierarchy의 각 상속 클래스를 구분하는 기준자.
public abstract class Item { //추상클래스, 구현체를 가지기 때문.
    // 상속관계 매핑을 해야한다. : Album, Book, Movie
    // 상속관계 전략을 지정해주어야한다 : 우리는 Single Table을 사용한다.
    // 이 상속관계 전략은 부모 클래스에 잡아준다.
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    /**
     * M:M의 경우는 mappedBy가 어떻게 되는 것이지??
     * 내 추측 : Category에 items 리스트의 원소 중 하나라도 본인객체와 같다면 해당 category객체 추가.
     *
     * 예시
     * cat1의 items 원소 : [item1, item2, item5]
     * cat2의 items 원소 : [item1, item3, item4]
     * cat3의 items 원소 : [item2, item6, item4]
     * -> item1의 categories의 원소 : [cat1, cat2]
     * -> item2의 categories의 원소 : [cat1, cat3]
     */

    //==비지니스 로직==//

    /**
     * 재고(stock) 증가
     * @param quantity
     */
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    /**
     * 재고(stock) 감소. 단, 재고가 0보다 크도록.
     * @param quantity
     */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock<0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
