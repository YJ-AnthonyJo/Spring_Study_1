package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
public class Category {

    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    //java에서의 M:M : collection이 있기에 문제가 없지만(List:List로 풀기)
    //RDMBS의 경우
    // M:M의 관계 -> 이를 구현하기 위해서는 중간에 1:M, M:1로 풀어내는 중간 테이블이 있어야 가능하다.
    private List<Item> items = new ArrayList<>();
    /**
     *      * 내 추측 : Item객체 categories 리스트의 원소 중 하나라도 본인 객체와 같다면 items에 추가.
     *
     * 예시
     * item1의 categories의 원소 : [cat1, cat2]
     * item2의 categories의 원소 : [cat1, cat3]
     * item3의 categories의 원소 : [cat2]
     * item4의 categories의 원소 : [cat2, cat3]
     * item5의 categories의 원소 : [cat1]
     * item6의 categories의 원소 : [cat2]
     *
     * -> cat1의 items 원소 : [item1, item2, item5]
     * -> cat2의 items 원소 : [item1, item3, item4]
     * -> cat3의 items 원소 : [item2, item6, item4]
     */

    /** 이 category_item테이블의 예시(객체화..)를 그려보자. ci라고 줄여표현한다.
     * 위 예시를 따름
     * ci1 : item1, cat1
     * ci2 : item1, cat2
     *
     * ci3 : item2, cat1
     * ci4 : item2, cat3
     *
     * ci5 : item3, cat2
     *
     * ci6 : item4, cat2
     * ci7 : item4, cat3
     *
     * ci8 : item5, cat1
     *
     * ci9 : item6, cat2
     *
     * 이를 통한 조회는 어떻게 될까?
     * case 1 : category에 속하는 item 조회(cat : 1기준)
     * SELECT c.name, i.name FROM category c
     *      (INNER) JOIN category_item ci ON ci.category_id = c.category_id
     *      //^ -> 카테고리 이름/아이템ID 의 형식일 것. -> 아이템ID를 아이템 이름으로 JOIN
     *      (INNER) JOIN item i ON i.item_id = ci.item_id;
     *
     *
     * case 2 : item이 속하는 category조회(item : 1기준)
     * SELECT i.name, c.name FROM item i
     *      (INNER) JOIN category_item ci ON i.item_id = ci.item_id
     *      //^ -> 아이템 이름/카테고리ID 의 형식일 것. -> 카테고리ID를 카테고리 이름으로 JOIN
     *      (INNER) JOIN category c ON ci.category_id = c.category_id;
     */


    //Category : 계층을 가진다.
    //예시 : 전자기기 - [컴퓨터, 노트북, 스마트 워치 등등] -[노트북 선택가정]- [가벼움, 무거움], [화면 인치] 등등..
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id") //여기 코드에는 이 parent_id를 만들지 않았지만, 자동으로 table 칼럼을 설정해준다
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    //==연관관계 메소드==//
    public void addChildCategory(Category child){
        this.child.add(child);
        child.setParent(this);
    }
}
